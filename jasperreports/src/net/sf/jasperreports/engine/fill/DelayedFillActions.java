/*
 * JasperReports - Free Java Reporting Library.
 * Copyright (C) 2001 - 2014 TIBCO Software Inc. All rights reserved.
 * http://www.jaspersoft.com
 *
 * Unless you have purchased a commercial license agreement from Jaspersoft,
 * the following license terms apply:
 *
 * This program is part of JasperReports.
 *
 * JasperReports is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * JasperReports is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with JasperReports. If not, see <http://www.gnu.org/licenses/>.
 */
package net.sf.jasperreports.engine.fill;

import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;

import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.JRPrintElement;
import net.sf.jasperreports.engine.JRPrintPage;
import net.sf.jasperreports.engine.JRRuntimeException;
import net.sf.jasperreports.engine.JRVirtualizable;
import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.engine.PrintElementVisitor;
import net.sf.jasperreports.engine.base.JRVirtualPrintPage;
import net.sf.jasperreports.engine.base.VirtualElementsData;
import net.sf.jasperreports.engine.base.VirtualizablePageElements;
import net.sf.jasperreports.engine.util.LinkedMap;
import net.sf.jasperreports.engine.util.UniformPrintElementVisitor;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * @author Lucian Chirita (lucianc@users.sourceforge.net)
 * @version $Id$
 */
public class DelayedFillActions
{
	private static final Log log = LogFactory.getLog(DelayedFillActions.class);
	
	private final BaseReportFiller reportFiller;
	private final JRFillContext fillContext;

	// we can use HashMap because the map is initialized in the beginning and doesn't change afterwards
	private final HashMap<JREvaluationTime, LinkedHashMap<FillPageKey, LinkedMap<Object, EvaluationBoundAction>>> actionsMap;
	
	public DelayedFillActions(BaseReportFiller reportFiller)
	{
		this.reportFiller = reportFiller;
		this.fillContext = reportFiller.fillContext;
		this.actionsMap = new HashMap<JREvaluationTime, LinkedHashMap<FillPageKey,LinkedMap<Object,EvaluationBoundAction>>>();
	}

	public void createDelayedEvaluationTime(JREvaluationTime evaluationTime)
	{
		LinkedHashMap<FillPageKey, LinkedMap<Object, EvaluationBoundAction>> evaluationActions = 
				new LinkedHashMap<FillPageKey, LinkedMap<Object, EvaluationBoundAction>>();
		actionsMap.put(evaluationTime, evaluationActions);
	}

	public void addDelayedAction(Object actionKey, EvaluationBoundAction action, 
			JREvaluationTime evaluationTime, FillPageKey pageKey)
	{
		if (log.isDebugEnabled())
		{
			log.debug(this + " adding delayed action " + action + " at " + evaluationTime + ", key " + pageKey);
		}
			
		// get the pages map for the evaluation
		LinkedHashMap<FillPageKey, LinkedMap<Object, EvaluationBoundAction>> pagesMap = actionsMap.get(evaluationTime);
		
		fillContext.lockVirtualizationContext();
		try
		{
			synchronized (pagesMap)
			{
				// get the actions map for the current page, creating if it does not yet exist
				LinkedMap<Object, EvaluationBoundAction> boundElementsMap = pagesMap.get(pageKey);
				if (boundElementsMap == null)
				{
					boundElementsMap = new LinkedMap<Object, EvaluationBoundAction>();
					pagesMap.put(pageKey, boundElementsMap);
				}
				
				// add the delayed element action to the map
				boundElementsMap.add(actionKey, action);
			}
		}
		finally
		{
			fillContext.unlockVirtualizationContext();
		}
	}
	
	public void runActions(JREvaluationTime evaluationTime, byte evaluation) throws JRException
	{
		if (log.isDebugEnabled())
		{
			log.debug("running delayed actions on " + evaluationTime);
		}
		
		LinkedHashMap<FillPageKey, LinkedMap<Object, EvaluationBoundAction>> pagesMap = actionsMap.get(evaluationTime);
		
		boolean hasEntry;
		do
		{
			reportFiller.checkInterrupted();
			
			// locking once per page so that we don't hold the lock for too long
			// (that would prevent async exporters from getting page data during a long resolve)
			fillContext.lockVirtualizationContext();
			try
			{
				synchronized (pagesMap)
				{
					// resolve a single page
					Iterator<Map.Entry<FillPageKey, LinkedMap<Object, EvaluationBoundAction>>> pagesIt = pagesMap.entrySet().iterator();
					hasEntry = pagesIt.hasNext();
					if (hasEntry)
					{
						Map.Entry<FillPageKey, LinkedMap<Object, EvaluationBoundAction>> pageEntry = pagesIt.next();
						int pageIdx = pageEntry.getKey().index;
						
						if (log.isDebugEnabled())
						{
							log.debug("running actions for page " + pageIdx);
						}
						
						StandardBoundActionExecutionContext context = new StandardBoundActionExecutionContext();
						context.setCurrentPageIndex(pageIdx);
						JasperPrint jasperPrint = fillContext.getMasterFiller().getJasperPrint();
						context.setTotalPages(jasperPrint.getPages().size());
						context.setEvaluationTime(evaluationTime);
						context.setExpressionEvaluationType(evaluation);
						
						LinkedMap<Object, EvaluationBoundAction> boundElementsMap = pageEntry.getValue();
						// execute the actions
						while (!boundElementsMap.isEmpty())
						{
							EvaluationBoundAction action = boundElementsMap.pop();
							action.execute(context);
						}
						
						// remove the entry from the pages map
						pagesIt.remove();
						
						// call the listener to signal that the page has been modified
						if (reportFiller.fillListener != null)
						{
							reportFiller.fillListener.pageUpdated(jasperPrint, pageIdx);
						}
					}
				}
			}
			finally
			{
				fillContext.unlockVirtualizationContext();
			}
		}
		while(hasEntry);
	}
	
	public boolean hasDelayedActions(JRPrintPage page)
	{
		FillPageKey pageKey = new FillPageKey(page);
		for (LinkedHashMap<FillPageKey, LinkedMap<Object, EvaluationBoundAction>> map : actionsMap.values())
		{
			fillContext.lockVirtualizationContext();
			try
			{
				synchronized (map)
				{
					LinkedMap<Object, EvaluationBoundAction> boundMap = map.get(pageKey);
					if (boundMap != null && !boundMap.isEmpty())
					{
						return true;
					}
				}
			}
			finally
			{
				fillContext.unlockVirtualizationContext();
			}
		}
		
		return false;
	}
	
	protected boolean hasMasterDelayedActions(JRPrintPage page)
	{
		LinkedHashMap<FillPageKey, LinkedMap<Object, EvaluationBoundAction>> masterActions = actionsMap.get(JREvaluationTime.EVALUATION_TIME_MASTER);
		FillPageKey pageKey = new FillPageKey(page);
		//FIXMEBOOK lock/sync?
		LinkedMap<Object, EvaluationBoundAction> pageMasterActions = masterActions.get(pageKey);
		return pageMasterActions != null && !pageMasterActions.isEmpty();
	}
	
	public void moveActions(FillPageKey fromKey, FillPageKey toKey)
	{
		if (log.isDebugEnabled())
		{
			log.debug(this + " moving actions from " + fromKey + " to " + toKey);
		}
		
		for (LinkedHashMap<FillPageKey, LinkedMap<Object, EvaluationBoundAction>> map : actionsMap.values())
		{
			fillContext.lockVirtualizationContext();
			try
			{
				synchronized (map)
				{
					LinkedMap<Object, EvaluationBoundAction> subreportMap = map.remove(fromKey);
					if (subreportMap != null && !subreportMap.isEmpty())
					{
						LinkedMap<Object, EvaluationBoundAction> masterMap = map.get(toKey);
						if (masterMap == null)
						{
							masterMap = new LinkedMap<Object, EvaluationBoundAction>();
							map.put(toKey, masterMap);
						}
						
						masterMap.addAll(subreportMap);
					}
				}
			}
			finally
			{
				fillContext.unlockVirtualizationContext();
			}
		}
	}
	
	public void setElementEvaluationsToPage(final JRVirtualizable<VirtualElementsData> object)
	{
		JRVirtualPrintPage page = ((VirtualizablePageElements) object).getPage();// ugly but needed for now
		FillPageKey pageKey = new FillPageKey(page);
		VirtualElementsData virtualData = object.getVirtualData();
		
		for (Map.Entry<JREvaluationTime, LinkedHashMap<FillPageKey, LinkedMap<Object, EvaluationBoundAction>>> boundMapEntry : 
			actionsMap.entrySet())
		{
			final JREvaluationTime evaluationTime = boundMapEntry.getKey();
			LinkedHashMap<FillPageKey, LinkedMap<Object, EvaluationBoundAction>> map = boundMapEntry.getValue();
			
			synchronized (map)
			{
				final LinkedMap<Object, EvaluationBoundAction> actionsMap = map.get(pageKey);
				
				if (actionsMap != null && !actionsMap.isEmpty())
				{
					// collection delayed evaluations for elements that are about to be externalized.
					// the evaluations store the ID of the fill elements in order to serialize the data.
					final Map<JRPrintElement, Integer> elementEvaluations = new LinkedHashMap<JRPrintElement, Integer>();
					
					// FIXME optimize for pages with a single virtual block
					// create a deep element visitor
					PrintElementVisitor<Void> visitor = new UniformPrintElementVisitor<Void>(true)
					{
						@Override
						protected void visitElement(JRPrintElement element, Void arg)
						{
							// remove the action from the map because we're saving it as part of the page.
							// ugly cast but acceptable for now.
							ElementEvaluationAction action = (ElementEvaluationAction) actionsMap.remove(element);
							if (action != null)
							{
								elementEvaluations.put(element, action.element.printElementOriginator.getSourceElementId());
								
								if (log.isDebugEnabled())
								{
									log.debug("filler " + reportFiller.fillerId + " saving evaluation " + evaluationTime + " of element " + element 
											+ " on object " + object);
								}
							}
						}
					};
					
					for (JRPrintElement element : virtualData.getElements())
					{
						element.accept(visitor, null);
					}
					
					if (!elementEvaluations.isEmpty())
					{
						// save the evaluations in the virtual data
						virtualData.setElementEvaluations(reportFiller.fillerId, evaluationTime, elementEvaluations);
						
						// add an action for the page so that it gets devirtualized on resolveBoundElements
						actionsMap.add(null, new VirtualizedPageEvaluationAction(object));
					}
				}
			}
		}
	}
	
	public void getElementEvaluationsFromPage(JRVirtualizable<VirtualElementsData> object)
	{
		JRVirtualPrintPage page = ((VirtualizablePageElements) object).getPage();// ugly but needed for now
		FillPageKey pageKey = new FillPageKey(page);
		VirtualElementsData elementsData = object.getVirtualData();
		
		for (Map.Entry<JREvaluationTime, LinkedHashMap<FillPageKey, LinkedMap<Object, EvaluationBoundAction>>> boundMapEntry : 
			actionsMap.entrySet())
		{
			JREvaluationTime evaluationTime = boundMapEntry.getKey();
			LinkedHashMap<FillPageKey, LinkedMap<Object, EvaluationBoundAction>> map = boundMapEntry.getValue();
			
			synchronized (map)
			{
				LinkedMap<Object, EvaluationBoundAction> actionsMap = map.get(pageKey);
				
				// get the delayed evaluations from the devirtualized data and add it back
				// to the filler delayed evaluation maps.
				Map<JRPrintElement, Integer> elementEvaluations = elementsData.getElementEvaluations(reportFiller.fillerId, evaluationTime);
				if (elementEvaluations != null)
				{
					for (Map.Entry<JRPrintElement, Integer> entry : elementEvaluations.entrySet())
					{
						JRPrintElement element = entry.getKey();
						int fillElementId = entry.getValue();
						JRFillElement fillElement = ((JRBaseFiller) reportFiller).fillElements.get(fillElementId);//FIXMEBOOK
						
						if (log.isDebugEnabled())
						{
							log.debug("filler " + reportFiller.fillerId + " got evaluation " + evaluationTime + " on " + element 
									+ " from object " + object + ", using " + fillElement);
						}
						
						if (fillElement == null)
						{
							throw new JRRuntimeException("Fill element with id " + fillElementId + " not found");
						}
						
						// add first so that it will be executed immediately
						actionsMap.addFirst(element, new ElementEvaluationAction(fillElement, element));
					}
				}
			}
		}
	}

	public void moveMasterEvaluations(DelayedFillActions sourceActions, JRPrintPage page, int pageIndex)
	{
		FillPageKey sourcePageKey = new FillPageKey(page);
		FillPageKey destinationPageKey = new FillPageKey(page, pageIndex);
		moveMasterEvaluations(sourceActions, sourcePageKey, destinationPageKey);
	}
	
	public void moveMasterEvaluations(DelayedFillActions sourceActions, FillPageKey pageKey)
	{
		moveMasterEvaluations(sourceActions, pageKey, pageKey);
	}

	protected void moveMasterEvaluations(DelayedFillActions sourceActions, FillPageKey sourcePageKey, FillPageKey destinationPageKey)
	{
		if (log.isDebugEnabled())
		{
			log.debug(this + " moving master actions from " + sourceActions
					+ ", source " + sourcePageKey + ", destination " + destinationPageKey);
		}
		
		LinkedHashMap<FillPageKey, LinkedMap<Object, EvaluationBoundAction>> actions = 
				sourceActions.actionsMap.get(JREvaluationTime.EVALUATION_TIME_MASTER);
		
		LinkedMap<Object, EvaluationBoundAction> pageActions = actions.remove(sourcePageKey);
		if (pageActions == null || pageActions.isEmpty())
		{
			return;
		}
		
		LinkedHashMap<FillPageKey, LinkedMap<Object, EvaluationBoundAction>> masterActions = 
				actionsMap.get(JREvaluationTime.EVALUATION_TIME_MASTER);
		LinkedMap<Object, EvaluationBoundAction> masterCurrent = masterActions.get(destinationPageKey);
		if (masterCurrent == null)
		{
			masterActions.put(destinationPageKey, pageActions);
		}
		else
		{
			masterCurrent.addAll(pageActions);
		}
	}
	
}
