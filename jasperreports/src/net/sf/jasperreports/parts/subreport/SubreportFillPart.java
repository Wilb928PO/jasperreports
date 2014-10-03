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
package net.sf.jasperreports.parts.subreport;

import java.util.Map;

import net.sf.jasperreports.data.cache.DataCacheHandler;
import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.JRPrintPage;
import net.sf.jasperreports.engine.JRPropertiesUtil;
import net.sf.jasperreports.engine.JRRuntimeException;
import net.sf.jasperreports.engine.JRVariable;
import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.engine.JasperReport;
import net.sf.jasperreports.engine.JasperReportsContext;
import net.sf.jasperreports.engine.SimplePrintPart;
import net.sf.jasperreports.engine.fill.BandReportFillerParent;
import net.sf.jasperreports.engine.fill.BaseReportFiller;
import net.sf.jasperreports.engine.fill.DatasetExpressionEvaluator;
import net.sf.jasperreports.engine.fill.FillDatasetPosition;
import net.sf.jasperreports.engine.fill.FillListener;
import net.sf.jasperreports.engine.fill.FillReturnValues;
import net.sf.jasperreports.engine.fill.FillerPageAddedEvent;
import net.sf.jasperreports.engine.fill.JRBaseFiller;
import net.sf.jasperreports.engine.fill.JRFillDataset;
import net.sf.jasperreports.engine.fill.JRFillExpressionEvaluator;
import net.sf.jasperreports.engine.fill.JRFillObjectFactory;
import net.sf.jasperreports.engine.fill.JRFillSubreport;
import net.sf.jasperreports.engine.fill.JRHorizontalFiller;
import net.sf.jasperreports.engine.fill.JRVerticalFiller;
import net.sf.jasperreports.engine.fill.PartReportFiller;
import net.sf.jasperreports.engine.part.BasePartFillComponent;
import net.sf.jasperreports.engine.part.PartPrintOutput;
import net.sf.jasperreports.engine.part.PartOutput;
import net.sf.jasperreports.engine.type.SectionTypeEnum;
import net.sf.jasperreports.parts.PartFillerParent;

/**
 * @author Lucian Chirita (lucianc@users.sourceforge.net)
 * @version $Id$
 */
public class SubreportFillPart extends BasePartFillComponent
{

	private SubreportPartComponent subreportPart;
	private JRFillExpressionEvaluator expressionEvaluator;
	private FillReturnValues returnValues;
	private FillReturnValues.SourceContext returnValuesSource;

	private JasperReport jasperReport;
	private Map<String, Object> parameterValues;
	
	private FillDatasetPosition datasetPosition;
	private boolean cacheIncluded;
	
	private volatile BaseReportFiller subreportFiller;
	
	public SubreportFillPart(SubreportPartComponent subreportPart, JRFillObjectFactory factory)
	{
		this.subreportPart = subreportPart;
		this.expressionEvaluator = factory.getExpressionEvaluator();
		
		this.returnValues = new FillReturnValues(subreportPart.getReturnValues(), factory, factory.getReportFiller());
		this.returnValuesSource = new FillReturnValues.SourceContext()
		{
			@Override
			public JRVariable getVariable(String name)
			{
				return subreportFiller.getVariable(name);
			}
			
			@Override
			public Object getVariableValue(String name)
			{
				return subreportFiller.getVariableValue(name);
			}
		};
	}

	@Override
	public void evaluate(byte evaluation) throws JRException
	{
		jasperReport = evaluateReport(evaluation);
		
		JRFillDataset parentDataset = expressionEvaluator.getFillDataset();
		datasetPosition = new FillDatasetPosition(parentDataset.getFillPosition());
		datasetPosition.addAttribute("subreportPartUUID", fillContext.getPart().getUUID());
		parentDataset.setCacheRecordIndex(datasetPosition, evaluation);
		
		String cacheIncludedProp = JRPropertiesUtil.getOwnProperty(fillContext.getPart(), DataCacheHandler.PROPERTY_INCLUDED); 
		cacheIncluded = JRPropertiesUtil.asBoolean(cacheIncludedProp, true);// default to true
		//FIXMEBOOK do not evaluate REPORT_DATA_SOURCE
		
		parameterValues = JRFillSubreport.getParameterValues(fillContext.getFiller(), expressionEvaluator, 
				subreportPart.getParametersMapExpression(), subreportPart.getParameters(), 
				evaluation, false, 
				jasperReport.getResourceBundle() != null, jasperReport.getFormatFactoryClass() != null);
	}

	private JasperReport evaluateReport(byte evaluation) throws JRException
	{
		Object reportSource = fillContext.evaluate(subreportPart.getExpression(), evaluation);
		return JRFillSubreport.loadReport(reportSource, fillContext.getFiller());//FIXMEBOOK cache
	}

	@Override
	public void fill(PartOutput output) throws JRException
	{
		subreportFiller = createSubreportFiller(output);
		returnValues.checkReturnValues(returnValuesSource);
		
		JRFillDataset subreportDataset = subreportFiller.getMainDataset();
		subreportDataset.setFillPosition(datasetPosition);
		subreportDataset.setCacheSkipped(!cacheIncluded);
		
		subreportFiller.fill(parameterValues);
		returnValues.copyValues(returnValuesSource);
	}

	@Override
	public boolean isPageFinal(int pageIndex)
	{
		BaseReportFiller filler = subreportFiller;
		if (filler == null)
		{
			//FIXMEBOOK
			return true;
		}
		
		return filler.isPageFinal(pageIndex);
	}
	
	protected BaseReportFiller createSubreportFiller(final PartOutput output) throws JRException
	{
		SectionTypeEnum sectionType = jasperReport.getSectionType();
		sectionType = sectionType == null ? SectionTypeEnum.BAND : sectionType;
		
		JasperReportsContext jasperReportsContext = fillContext.getFiller().getJasperReportsContext();
		BaseReportFiller filler;
		switch (sectionType)
		{
		case BAND:
			BandReportFillerParent bandParent = new PartBandParent(output);
			switch (jasperReport.getPrintOrderValue())
			{
			case HORIZONTAL:
				filler = new JRHorizontalFiller(jasperReportsContext, jasperReport, bandParent);
				break;
			case VERTICAL:
				filler = new JRVerticalFiller(jasperReportsContext, jasperReport, bandParent);
				break;
			default:
				throw new JRRuntimeException("Unknown report section type " + sectionType);
			}
			break;
		case PART:
			PartParent partParent = new PartParent(output.getPrintOutput());
			filler = new PartReportFiller(jasperReportsContext, jasperReport, partParent);
			break;
		default:
			throw new JRRuntimeException("Unknown report section type " + sectionType);
		}
		
		filler.addFillListener(new FillListener()
		{
			@Override
			public void pageGenerated(JasperPrint jasperPrint, int pageIndex)
			{
				//NOP
			}
			
			@Override
			public void pageUpdated(JasperPrint jasperPrint, int pageIndex)
			{
				output.partPageUpdated(pageIndex);
			}
		});
		
		return filler;
	}
	
	protected class PartBandParent implements BandReportFillerParent
	{
		private final PartOutput output;

		protected PartBandParent(PartOutput output)
		{
			this.output = output;
		}

		@Override
		public BaseReportFiller getFiller()
		{
			return fillContext.getFiller();
		}

		@Override
		public DatasetExpressionEvaluator getCachedEvaluator()
		{
			//FIXMEBOOK
			return null;
		}

		@Override
		public void registerSubfiller(JRBaseFiller filler)
		{
			//FIXMEBOOK
		}

		@Override
		public void unregisterSubfiller(JRBaseFiller jrBaseFiller)
		{
			//FIXMEBOOK
		}

		@Override
		public boolean isRunToBottom()
		{
			return true;
		}

		@Override
		public boolean isPageBreakInhibited()
		{
			return false;
		}

		@Override
		public void addPage(FillerPageAddedEvent pageAdded) throws JRException
		{
			PartPrintOutput printOutput = output.getPrintOutput();
			if (pageAdded.getPageIndex() == 0)
			{
				//first page, adding the part info
				SimplePrintPart printPart = SimplePrintPart.fromJasperPrint(pageAdded.getJasperPrint());
				printOutput.startPart(printPart);
			}
			
			printOutput.addPage(pageAdded.getPage(), pageAdded.getDelayedActions());
			
			//FIXMEBOOK styles
		}

		@Override
		public JRPrintPage getPage(int pageIndex)
		{
			return output.getPrintOutput().getPage(pageIndex);
		}
	}
	
	protected class PartParent implements PartFillerParent
	{
		private PartPrintOutput printOutput;

		public PartParent(PartPrintOutput printDestination)
		{
			this.printOutput = printDestination;
		}

		@Override
		public BaseReportFiller getFiller()
		{
			return fillContext.getFiller();
		}

		@Override
		public DatasetExpressionEvaluator getCachedEvaluator()
		{
			//FIXMEBOOK
			return null;
		}

		@Override
		public PartPrintOutput getPrintOutput()
		{
			return printOutput;
		}
	}

}