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
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.JRPrintPage;
import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.engine.PrintPart;
import net.sf.jasperreports.engine.SimplePrintPageFormat;
import net.sf.jasperreports.engine.SimplePrintPart;
import net.sf.jasperreports.engine.fill.DelayedFillActions;
import net.sf.jasperreports.engine.fill.FillerPageAddedEvent;
import net.sf.jasperreports.engine.fill.JREvaluationTime;
import net.sf.jasperreports.engine.fill.PartReportFiller;

/**
 * @author Lucian Chirita (lucianc@users.sourceforge.net)
 * @version $Id$
 */
public class FillPartOutput
{
	private static final Log log = LogFactory.getLog(PartReportFiller.class);

	private final FillPart part;
	
	private PrintPart printPart;
	private final List<JRPrintPage> pages;
	private DelayedFillActions delayedActions;

	public FillPartOutput(FillPart part)
	{
		this.part = part;
		
		this.pages = new ArrayList<JRPrintPage>();
		this.delayedActions = new DelayedFillActions(part.getFiller());
		this.delayedActions.createDelayedEvaluationTime(JREvaluationTime.EVALUATION_TIME_MASTER);
	}

	public void fill(byte evaluation) throws JRException
	{
		Output output = new Output();
		part.fill(evaluation, output);
	}
	
	protected void setPart(JasperPrint partPrint)
	{
		SimplePrintPart printPart = new SimplePrintPart();
		printPart.setName(partPrint.getName());
		
		SimplePrintPageFormat pageFormat = new SimplePrintPageFormat();
		pageFormat.setPageWidth(partPrint.getPageWidth());
		pageFormat.setPageHeight(partPrint.getPageHeight());
		pageFormat.setOrientation(partPrint.getOrientationValue());
		pageFormat.setLeftMargin(partPrint.getLeftMargin());
		pageFormat.setTopMargin(partPrint.getTopMargin());
		pageFormat.setRightMargin(partPrint.getRightMargin());
		pageFormat.setBottomMargin(partPrint.getBottomMargin());
		printPart.setPageFormat(pageFormat);
		
		this.printPart = printPart;
		
		if (log.isDebugEnabled())
		{
			log.debug("created part " + printPart.getName());
		}
	}
	
	protected void addPartPage(FillerPageAddedEvent pageAdded)
	{
		int pageIndex = pages.size();
		if (log.isDebugEnabled())
		{
			log.debug("adding part page at index " + pageIndex);
		}
		
		JRPrintPage page = pageAdded.getPage();
		pages.add(page);
		//addLastPageBookmarks();//FIXMEBOOK needed?
		
		//FIXMEBOOK fill element Ids & virtualization listener
		delayedActions.moveMasterEvaluations(pageAdded.getDelayedActions(), page, pageIndex);
	}

	public PrintPart getPrintPart()
	{
		return printPart;
	}

	public List<JRPrintPage> getPages()
	{
		return pages;
	}

	public DelayedFillActions getDelayedActions()
	{
		return delayedActions;
	}

	public boolean isPageFinal(int pageIndex)
	{
		return part.isPageFinal(pageIndex);
	}
	
	protected class Output implements PartOutput
	{
		@Override
		public void startPart(JasperPrint jasperPrint)
		{
			setPart(jasperPrint);
		}

		@Override
		public void addPage(FillerPageAddedEvent pageAdded)
		{
			addPartPage(pageAdded);
		}

		@Override
		public JRPrintPage getPage(int pageIndex)
		{
			return pages.get(pageIndex);
		}

		@Override
		public void partPageUpdated(int partPageIndex)
		{
			//FIXMEBOOK part.getFiller().partPageUpdated(startPageIndex + partPageIndex);
		}
	}
	
}
