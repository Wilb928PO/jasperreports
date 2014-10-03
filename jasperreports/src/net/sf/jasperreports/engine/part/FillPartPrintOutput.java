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
package net.sf.jasperreports.engine.part;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import net.sf.jasperreports.engine.JRPrintPage;
import net.sf.jasperreports.engine.PrintPart;
import net.sf.jasperreports.engine.fill.BaseReportFiller;
import net.sf.jasperreports.engine.fill.DelayedFillActions;
import net.sf.jasperreports.engine.fill.JREvaluationTime;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * @author Lucian Chirita (lucianc@users.sourceforge.net)
 * @version $Id$
 */
public class FillPartPrintOutput implements PartPrintOutput
{
	private static final Log log = LogFactory.getLog(FillPartPrintOutput.class);

	private TreeMap<Integer, PrintPart> parts;
	private List<JRPrintPage> pages;
	private DelayedFillActions delayedActions;

	public FillPartPrintOutput(BaseReportFiller filler)
	{
		parts = new TreeMap<Integer, PrintPart>();
		pages = new ArrayList<JRPrintPage>();
		
		delayedActions = new DelayedFillActions(filler);
		delayedActions.createDelayedEvaluationTime(JREvaluationTime.EVALUATION_TIME_MASTER);
	}

	@Override
	public void startPart(PrintPart printPart)
	{
		int startIndex = pages.size();
		parts.put(startIndex, printPart);
		
		if (log.isDebugEnabled())
		{
			log.debug("added part " + printPart.getName() + " at index " + startIndex);
		}
	}

	@Override
	public void addPage(JRPrintPage page, DelayedFillActions delayedActionsSource)
	{
		int pageIndex = pages.size();
		if (log.isDebugEnabled())
		{
			log.debug("adding part page at index " + pageIndex);
		}
		
		pages.add(page);
		//addLastPageBookmarks();//FIXMEBOOK needed?
		
		//FIXMEBOOK fill element Ids & virtualization listener
		delayedActions.moveMasterEvaluations(delayedActionsSource, page, pageIndex);
	}

	@Override
	public JRPrintPage getPage(int pageIndex)
	{
		return pages.get(pageIndex);
	}

	public void appendTo(PartPrintOutput output)
	{
		Iterator<JRPrintPage> pagesIterator = pages.iterator();
		int prevPartStart = 0;
		for (Map.Entry<Integer, PrintPart> partEntry : parts.entrySet())
		{
			int partStart = partEntry.getKey();
			// add the pages that belong to the previous part
			for (int i = prevPartStart; i < partStart; i++)
			{
				JRPrintPage page = pagesIterator.next();
				output.addPage(page, delayedActions);
			}
			prevPartStart = partStart;
			
			PrintPart part = partEntry.getValue();
			output.startPart(part);
		}

		// add the pages that belong to the last part
		while (pagesIterator.hasNext())
		{
			JRPrintPage page = pagesIterator.next();
			output.addPage(page, delayedActions);
		}
	}
}
