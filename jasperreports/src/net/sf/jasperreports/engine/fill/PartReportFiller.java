/*
 * JasperReports - Free Java Reporting Library.
 * Copyright (C) 2001 - 2013 Jaspersoft Corporation. All rights reserved.
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
import net.sf.jasperreports.engine.ReportContext;
import net.sf.jasperreports.engine.SimplePrintPageFormat;
import net.sf.jasperreports.engine.SimplePrintPart;
import net.sf.jasperreports.engine.part.FillPart;
import net.sf.jasperreports.engine.part.FillParts;
import net.sf.jasperreports.engine.part.GroupFillParts;
import net.sf.jasperreports.engine.type.IncrementTypeEnum;
import net.sf.jasperreports.engine.type.ResetTypeEnum;
import net.sf.jasperreports.engine.type.SectionTypeEnum;
import net.sf.jasperreports.engine.util.JRDataUtils;

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
	
	private ReadWriteLock currentFillPartLock = new ReentrantReadWriteLock();
	private FillPart currentFillingPart;
	
	public PartReportFiller(JasperReportsContext jasperReportsContext, JasperReport jasperReport) throws JRException
	{
		this(jasperReportsContext, jasperReport, null);
	}
	
	public PartReportFiller(JasperReportsContext jasperReportsContext, JasperReport jasperReport, FillerParent parent) throws JRException
	{
		super(jasperReportsContext, jasperReport, parent);
		
		if (jasperReport.getSectionType() != SectionTypeEnum.PART)
		{
			throw new JRRuntimeException("Unsupported report section type " + jasperReport.getSectionType());
		}
		
		initDatasets();
		
		detailParts = new FillParts(jasperReport.getDetailSection(), factory);
		
		JRGroup[] reportGroups = jasperReport.getGroups();
		if (reportGroups == null || reportGroups.length == 0)
		{
			groupParts = Collections.emptyList();
		}
		else
		{
			groupParts = new ArrayList<GroupFillParts>(reportGroups.length);
			for (JRGroup reportGroup : reportGroups)
			{
				GroupFillParts groupFillParts = new GroupFillParts(reportGroup, factory);
				groupParts.add(groupFillParts);
			}
		}
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
			//createBoundElemementMaps();

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
			detail();
			fillParts(detailParts, JRExpression.EVALUATION_DEFAULT);

			while (mainDataset.next())
			{
				checkInterrupted();
				estimateGroups();
				fillChangedGroupFooters();
				
				groups();
				fillChangedGroupHeaders();
				
				detail();
				fillParts(detailParts, JRExpression.EVALUATION_DEFAULT);
			}
			
			fillLastGroupFooters();
		}
	}

	protected void startReport() throws JRScriptletException, JRException
	{
		scriptlet.callBeforeReportInit();
		calculator.initializeVariables(ResetTypeEnum.REPORT, IncrementTypeEnum.REPORT);
		scriptlet.callAfterReportInit();
	}

	protected void detail() throws JRScriptletException, JRException
	{
		scriptlet.callBeforeDetailEval();
		calculator.calculateVariables();
		scriptlet.callAfterDetailEval();
	}

	protected void estimateGroups() throws JRException
	{
		calculator.estimateGroupRuptures();
	}
	
	protected void groups() throws JRException
	{
		scriptlet.callBeforeGroupInit();
		calculator.initializeVariables(ResetTypeEnum.GROUP, IncrementTypeEnum.GROUP);
		scriptlet.callAfterGroupInit();
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
		int startPageIndex = jasperPrint.getPages().size();
		
		currentFillPartLock.writeLock().lock();
		try
		{
			part.prepareFill(startPageIndex);
			currentFillingPart = part;
		}
		finally
		{
			currentFillPartLock.writeLock().unlock();
		}
		
		part.fill(evaluation);
	}

	@Override
	public boolean isPageFinal(int pageIndex)
	{
		currentFillPartLock.readLock().lock();
		try
		{
			FillPart fillingPart = currentFillingPart;
			if (fillingPart == null)
			{
				//FIXMEBOOK
				return true;
			}
			
			int partStartPageIndex = fillingPart.getStartPageIndex();
			if (pageIndex < partStartPageIndex)
			{
				return true;
			}
			
			return fillingPart.isPageFinal(pageIndex);
		}
		finally
		{
			currentFillPartLock.readLock().unlock();
		}
	}

	public void partPageUpdated(int pageIndex)
	{
		if (fillListener != null)
		{
			fillListener.pageUpdated(jasperPrint, pageIndex);
		}
	}

	public void startPart(JasperPrint partPrint)
	{
		SimplePrintPart part = new SimplePrintPart();
		part.setName(partPrint.getName());
		
		SimplePrintPageFormat pageFormat = new SimplePrintPageFormat();
		pageFormat.setPageWidth(partPrint.getPageWidth());
		pageFormat.setPageHeight(partPrint.getPageHeight());
		pageFormat.setOrientation(partPrint.getOrientationValue());
		pageFormat.setLeftMargin(partPrint.getLeftMargin());
		pageFormat.setTopMargin(partPrint.getTopMargin());
		pageFormat.setRightMargin(partPrint.getRightMargin());
		pageFormat.setBottomMargin(partPrint.getBottomMargin());
		part.setPageFormat(pageFormat);
		
		int startIndex = jasperPrint.getPages().size();
		jasperPrint.addPart(startIndex, part);
		
		if (log.isDebugEnabled())
		{
			log.debug("added part " + part.getName() + " at index " + startIndex);
		}
	}

	public void addPartPage(JRPrintPage page)
	{
		int pageIndex = jasperPrint.getPages().size();
		if (log.isDebugEnabled())
		{
			log.debug("adding part page at index " + pageIndex);
		}
		
		jasperPrint.addPage(page);
		addLastPageBookmarks();
		
		if (fillListener != null)
		{
			fillListener.pageGenerated(jasperPrint, pageIndex);
		}
	}

	public JRPrintPage getPrintPage(int pageIndex)
	{
		return jasperPrint.getPages().get(pageIndex);
	}

}
