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

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.ListIterator;
import java.util.Map;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.JRExpression;
import net.sf.jasperreports.engine.JRGroup;
import net.sf.jasperreports.engine.JRParameter;
import net.sf.jasperreports.engine.JRPrintPage;
import net.sf.jasperreports.engine.JRRuntimeException;
import net.sf.jasperreports.engine.JRScriptletException;
import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.engine.JasperReport;
import net.sf.jasperreports.engine.JasperReportsContext;
import net.sf.jasperreports.engine.PrintPart;
import net.sf.jasperreports.engine.ReportContext;
import net.sf.jasperreports.engine.part.FillPart;
import net.sf.jasperreports.engine.part.FillParts;
import net.sf.jasperreports.engine.part.FillingPrintPart;
import net.sf.jasperreports.engine.part.GroupFillParts;
import net.sf.jasperreports.engine.part.PartEvaluationTime;
import net.sf.jasperreports.engine.part.PartPrintOutput;
import net.sf.jasperreports.engine.part.PrintPartSource;
import net.sf.jasperreports.engine.part.PrintPartSourceQueue;
import net.sf.jasperreports.engine.type.IncrementTypeEnum;
import net.sf.jasperreports.engine.type.ResetTypeEnum;
import net.sf.jasperreports.engine.type.SectionTypeEnum;
import net.sf.jasperreports.engine.util.JRDataUtils;
import net.sf.jasperreports.parts.PartFillerParent;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * @author Lucian Chirita (lucianc@users.sourceforge.net)
 * @version $Id$
 */
public class PartReportFiller extends BaseReportFiller
{
	private static final Log log = LogFactory.getLog(PartReportFiller.class);
	
	private FillParts detailParts;
	private List<GroupFillParts> groupParts;
	private Map<String, GroupFillParts> groupPartsByName;
	
	private PrintPartSourceQueue partQueue;
	
	private List<PrintPartSource> reportEvaluatedParts;
	
	private PartPrintOutput printOutput;
	
	public PartReportFiller(JasperReportsContext jasperReportsContext, JasperReport jasperReport) throws JRException
	{
		this(jasperReportsContext, jasperReport, null);
	}
	
	public PartReportFiller(JasperReportsContext jasperReportsContext, JasperReport jasperReport, 
			PartFillerParent parent) throws JRException
	{
		super(jasperReportsContext, jasperReport, parent);
		
		if (jasperReport.getSectionType() != SectionTypeEnum.PART)
		{
			throw new JRRuntimeException("Unsupported report section type " + jasperReport.getSectionType());
		}
		
		detailParts = new FillParts(jasperReport.getDetailSection(), factory);
		
		JRGroup[] reportGroups = jasperReport.getGroups();
		if (reportGroups == null || reportGroups.length == 0)
		{
			groupParts = Collections.emptyList();
			groupPartsByName = Collections.emptyMap();
		}
		else
		{
			groupParts = new ArrayList<GroupFillParts>(reportGroups.length);
			groupPartsByName = new HashMap<String, GroupFillParts>();
			for (JRGroup reportGroup : reportGroups)
			{
				GroupFillParts groupFillParts = new GroupFillParts(reportGroup, factory);
				groupParts.add(groupFillParts);
				groupPartsByName.put(reportGroup.getName(), groupFillParts);
			}
		}
		
		initDatasets();
		
		reportEvaluatedParts = new ArrayList<PrintPartSource>();
		
		printOutput = parent == null ? new JasperPrintPartOutput() : parent.getPrintOutput();
		partQueue = new PrintPartSourceQueue();
	}

	@Override
	protected JRFillObjectFactory initFillFactory()
	{
		return new JRFillObjectFactory(this);
	}

	@Override
	public JasperPrint fill(Map<String, Object> parameterValues) throws JRException
	{
		//FIXMEBOOK copied from JRBaseFiller
		if (parameterValues == null)
		{
			parameterValues = new HashMap<String,Object>();
		}

		if (log.isDebugEnabled())
		{
			log.debug("Fill " + fillerId + ": filling report");
		}

		setParametersToContext(parameterValues);

		fillingThread = Thread.currentThread();
		
		JRResourcesFillUtil.ResourcesFillContext resourcesContext = 
			JRResourcesFillUtil.setResourcesFillContext(parameterValues);
		
		boolean success = false;
		try
		{
			createBoundElemementMaps();

/*			if (parent != null)
			{
				parent.registerSubfiller(this);
			}
*/
			setParameters(parameterValues);

			//loadStyles();

			jasperPrint.setName(jasperReport.getName());
			jasperPrint.setPageWidth(jasperReport.getPageWidth());
			jasperPrint.setPageHeight(jasperReport.getPageHeight());
			jasperPrint.setTopMargin(jasperReport.getTopMargin());
			jasperPrint.setLeftMargin(jasperReport.getLeftMargin());
			jasperPrint.setBottomMargin(jasperReport.getBottomMargin());
			jasperPrint.setRightMargin(jasperReport.getRightMargin());
			jasperPrint.setOrientation(jasperReport.getOrientationValue());

			jasperPrint.setFormatFactoryClass(jasperReport.getFormatFactoryClass());
			jasperPrint.setLocaleCode(JRDataUtils.getLocaleCode(getLocale()));
			jasperPrint.setTimeZoneId(JRDataUtils.getTimeZoneId(getTimeZone()));

/*			jasperPrint.setDefaultStyle(defaultStyle);

			if (styles != null && styles.length > 0)
			{
				for (int i = 0; i < styles.length; i++)
				{
					addPrintStyle(styles[i]);
				}
			}
*/
			/*   */
			mainDataset.start();

			/*   */
			fillReport();
			
			if (bookmarkHelper != null)
			{
				jasperPrint.setBookmarks(bookmarkHelper.getRootBookmarks());
			}

			if (log.isDebugEnabled())
			{
				log.debug("Fill " + fillerId + ": ended");
			}

			success = true;
			return jasperPrint;
		}
		finally
		{
			mainDataset.closeDatasource();
			mainDataset.disposeParameterContributors();
			
			if (success && parent == null)
			{
				// commit the cached data
				fillContext.cacheDone();
			}

/*			if (parent != null)
			{
				parent.unregisterSubfiller(this);
			}
			
			if (fillContext.isUsingVirtualizer())
			{
				// removing the listener
				virtualizationContext.removeListener(virtualizationListener);
			}
*/			

			fillingThread = null;

			//kill the subreport filler threads
			//killSubfillerThreads();
			
			if (parent == null)
			{
				fillContext.dispose();
			}

			JRResourcesFillUtil.revertResourcesFillContext(resourcesContext);
		}
	}

	private void createBoundElemementMaps()
	{
		createBoundElementMaps(JREvaluationTime.EVALUATION_TIME_MASTER);
	}

	@Override
	protected void virtualizationContextCreated()
	{
		//NOP
	}
	
	protected void setParameters(Map<String,Object> parameterValues) throws JRException
	{
		//FIXMEBOOK copied from JRBaseFiller
		initVirtualizationContext(parameterValues);

		setFormatFactory(parameterValues);

		//setIgnorePagination(parameterValues);

		if (parent == null)
		{
			ReportContext reportContext = (ReportContext) parameterValues.get(JRParameter.REPORT_CONTEXT);
			fillContext.setReportContext(reportContext);
		}

		mainDataset.setParameterValues(parameterValues);
		mainDataset.initDatasource();

		this.scriptlet = mainDataset.delegateScriptlet;

		if (!isSubreport())
		{
			fillContext.setMasterFormatFactory(getFormatFactory());
			fillContext.setMasterLocale(getLocale());
			fillContext.setMasterTimeZone(getTimeZone());
		}
	}
	
	protected void fillReport() throws JRException
	{
		startReport();
		
		if (mainDataset.next())
		{
			fillFirstGroupHeaders();
			calculateDetail();
			fillDetail();

			while (mainDataset.next())
			{
				checkInterrupted();
				estimateGroups();
				fillChangedGroupFooters();
				fillChangedGroupEvaluatedParts();
				
				calculateGroups();
				fillChangedGroupHeaders();
				
				calculateDetail();
				fillDetail();
			}
			
			fillLastGroupFooters();
			fillLastGroupEvaluatedParts();
		}
		
		fillReportEvaluatedParts();
		assert partQueue.isEmpty();
		
		if (isMasterReport())
		{
			resolveMasterBoundElements();
		}
	}

	protected void startReport() throws JRScriptletException, JRException
	{
		scriptlet.callBeforeReportInit();
		calculator.initializeVariables(ResetTypeEnum.REPORT, IncrementTypeEnum.REPORT);
		scriptlet.callAfterReportInit();
	}

	protected void calculateDetail() throws JRScriptletException, JRException
	{
		scriptlet.callBeforeDetailEval();
		calculator.calculateVariables();
		scriptlet.callAfterDetailEval();
	}

	protected void estimateGroups() throws JRException
	{
		calculator.estimateGroupRuptures();
	}
	
	protected void calculateGroups() throws JRException
	{
		scriptlet.callBeforeGroupInit();
		calculator.initializeVariables(ResetTypeEnum.GROUP, IncrementTypeEnum.GROUP);
		scriptlet.callAfterGroupInit();
	}

	protected void fillDetail() throws JRException
	{
		fillParts(detailParts, JRExpression.EVALUATION_DEFAULT);
	}

	protected void fillFirstGroupHeaders() throws JRException
	{
		for (GroupFillParts group : groupParts)
		{
			fillParts(group.getHeaderParts(), JRExpression.EVALUATION_DEFAULT);
		}
	}

	protected void fillChangedGroupHeaders() throws JRException
	{
		for (GroupFillParts group : groupParts)
		{
			if (group.hasChanged())
			{
				fillParts(group.getHeaderParts(), JRExpression.EVALUATION_DEFAULT);
			}
		}
	}

	private void fillChangedGroupFooters() throws JRException
	{
		for (GroupFillParts group : groupParts)
		{
			if (group.hasChanged())
			{
				fillParts(group.getFooterParts(), JRExpression.EVALUATION_OLD);
			}
		}
	}

	private void fillLastGroupFooters() throws JRException
	{
		for (GroupFillParts group : groupParts)
		{
			fillParts(group.getFooterParts(), JRExpression.EVALUATION_DEFAULT);
		}
	}

	protected void fillParts(FillParts parts, byte evaluation) throws JRException
	{
		for (FillPart part : parts.getParts())
		{
			checkInterrupted();
			fillPart(part, evaluation);
		}
	}

	protected void fillPart(FillPart part, byte evaluation) throws JRException
	{
		PrintPartSource partSource = new PrintPartSource(part);
		
		boolean addToQueue;
		PartEvaluationTime evaluationTime = part.getEvaluationTime();
		switch (evaluationTime.getEvaluationTimeType())
		{
		case NOW:
			if (partQueue.isEmpty())
			{
				// no previous parts, filling directly to the filler output
				partSource.fill(evaluation, printOutput);
				addToQueue = false;
			}
			else
			{
				// filling to a local output
				partSource.fill(evaluation);
				addToQueue = true;
			}
			break;
		case REPORT:
			reportEvaluatedParts.add(partSource);
			addToQueue = true;
			break;
		case GROUP:
			GroupFillParts groupFillParts = groupPartsByName.get(evaluationTime.getEvaluationGroup());
			if (groupFillParts == null)//FIXMEBOOK validate
			{
				throw new JRRuntimeException("Part evaluation group " + evaluationTime.getEvaluationGroup() + " not found");
			}
			
			groupFillParts.addGroupEvaluatedPart(partSource);
			addToQueue = true;
			break;
		default:
			throw new JRRuntimeException("Unknown evaluation time type " + evaluationTime.getEvaluationTimeType());
		}
		
		if (addToQueue)
		{
			partQueue.append(partSource);
		}
	}

	@Override
	public boolean isPageFinal(int pageIndex)
	{
		if (isSubreport())
		{
			// shouldn't be called
			return false;
		}
		
		return ((JasperPrintPartOutput) printOutput).isPageFinal(pageIndex);
	}

	protected void partPageUpdated(int pageIndex)
	{
		if (fillListener != null)
		{
			fillListener.pageUpdated(jasperPrint, pageIndex);
		}
	}

	protected void fillReportEvaluatedParts() throws JRException
	{
		fillDelayedEvaluatedParts(reportEvaluatedParts, JRExpression.EVALUATION_DEFAULT);
	}

	protected void fillChangedGroupEvaluatedParts() throws JRException
	{
		for (GroupFillParts group : groupParts)//FIXMEBOOK order?
		{
			if (group.hasChanged())
			{
				fillDelayedEvaluatedParts(group.getGroupEvaluatedParts(), JRExpression.EVALUATION_OLD);
			}
		}
	}

	protected void fillLastGroupEvaluatedParts() throws JRException
	{
		for (GroupFillParts group : groupParts)
		{
			fillDelayedEvaluatedParts(group.getGroupEvaluatedParts(), JRExpression.EVALUATION_DEFAULT);
		}
	}
	
	protected void fillDelayedEvaluatedParts(List<PrintPartSource> parts, byte evaluation) throws JRException
	{
		for (ListIterator<PrintPartSource> it = parts.listIterator(); it.hasNext();)
		{
			PrintPartSource part = it.next();
			it.remove();
			
			fillDelayedPart(evaluation, part);
		}
	}

	protected void fillDelayedPart(byte evaluation, PrintPartSource part) throws JRException
	{
		if (partQueue.isHead(part))
		{
			// first part in the queue, filling directly to the filler output
			part.fill(evaluation, printOutput);
			// remove the part that we just filled
			partQueue.removeHead();
			
			// go through the next parts if copy the ones that are already filled
			while (!partQueue.isEmpty())
			{
				PrintPartSource queuedPart = partQueue.head();
				boolean appended = queuedPart.appendLocalOutput(printOutput);
				if (appended)
				{
					partQueue.removeHead();
				}
				else
				{
					break;
				}
			}
		}
		else
		{
			// filling to a local output and keeping in queue
			part.fill(evaluation);
		}
	}
	
	protected class JasperPrintPartOutput implements PartPrintOutput
	{
		private final ReadWriteLock currentFillPartLock = new ReentrantReadWriteLock();
		private transient int currentPartStartIndex;
		private FillingPrintPart currentFillingPart;
		
		@Override
		public void startPart(PrintPart part, FillingPrintPart fillingPart)
		{
			int startIndex = jasperPrint.getPages().size();
			jasperPrint.addPart(startIndex, part);

			currentFillPartLock.writeLock().lock();
			try
			{
				currentPartStartIndex = startIndex;
				currentFillingPart = fillingPart;
			}
			finally
			{
				currentFillPartLock.writeLock().unlock();
			}
			
			if (log.isDebugEnabled())
			{
				log.debug("added part " + part.getName() + " at index " + startIndex);
			}
		}
		
		public boolean isPageFinal(int pageIndex)
		{
			currentFillPartLock.readLock().lock();
			try
			{
				JRPrintPage page = getPage(pageIndex);
				boolean hasMasterActions = delayedActions.hasDelayedActions(page);
				if (hasMasterActions)
				{
					return false;
				}
				
				boolean isFinal;
				if (pageIndex < currentPartStartIndex)
				{
					isFinal = true;
				}
				else
				{
					isFinal = currentFillingPart.isPageFinal(page);
				}
				return isFinal;
			}
			finally
			{
				currentFillPartLock.readLock().unlock();
			}
		}

		@Override
		public void addPage(JRPrintPage page, DelayedFillActions delayedActionsSource)
		{
			int pageIndex = jasperPrint.getPages().size();
			if (log.isDebugEnabled())
			{
				log.debug("adding part page at index " + pageIndex);
			}
			
			jasperPrint.addPage(page);
			addLastPageBookmarks();
			
			//FIXMEBOOK fill element Ids & virtualization listener
			delayedActions.moveMasterEvaluations(delayedActionsSource, page, pageIndex);
			
			if (fillListener != null)
			{
				fillListener.pageGenerated(jasperPrint, pageIndex);
			}
		}
		
		@Override
		public JRPrintPage getPage(int pageIndex)
		{
			return jasperPrint.getPages().get(pageIndex);
		}

		@Override
		public void pageUpdated(int partPageIndex)
		{
			partPageUpdated(currentPartStartIndex + partPageIndex);
		}
	}

}
