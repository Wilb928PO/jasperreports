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

import java.sql.Connection;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.TimeZone;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import net.sf.jasperreports.engine.JRAbstractScriptlet;
import net.sf.jasperreports.engine.JRDataSource;
import net.sf.jasperreports.engine.JRDataset;
import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.JRExpression;
import net.sf.jasperreports.engine.JRParameter;
import net.sf.jasperreports.engine.JRPropertiesUtil;
import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.engine.JasperReport;
import net.sf.jasperreports.engine.JasperReportsContext;
import net.sf.jasperreports.engine.type.CalculationEnum;
import net.sf.jasperreports.engine.util.DefaultFormatFactory;
import net.sf.jasperreports.engine.util.FormatFactory;
import net.sf.jasperreports.engine.util.JRGraphEnvInitializer;
import net.sf.jasperreports.engine.util.LocalJasperReportsContext;

/**
 * @author Teodor Danciu (teodord@users.sourceforge.net)
 * @version $Id$
 */
public abstract class BaseReportFiller implements ReportFiller
{
	private static final Log log = LogFactory.getLog(BaseReportFiller.class);
	
	protected JasperReportsContext jasperReportsContext;
	protected JRPropertiesUtil propertiesUtil;

	protected JRFillContext fillContext;
	
	//FIXMEBOOK replace these with a parent context
	protected FillerParent parent;
	
	protected final int fillerId;

	protected List<String> printTransferPropertyPrefixes;

	/**
	 * The report.
	 */
	protected JasperReport jasperReport;

	protected JRCalculator calculator;

	protected final JRFillObjectFactory factory;

	/**
	 * Main report dataset.
	 */
	protected JRFillDataset mainDataset;

	/**
	 * Map of datasets ({@link JRFillDataset JRFillDataset} objects} indexed by name.
	 */
	protected Map<String,JRFillDataset> datasetMap;

	protected JRAbstractScriptlet scriptlet;

	protected FormatFactory formatFactory;
	
	protected JasperPrint jasperPrint;
	
	private boolean isInterrupted;

	public BaseReportFiller(JasperReportsContext jasperReportsContext, JasperReport jasperReport, 
			FillerParent parent) throws JRException
	{
		JRGraphEnvInitializer.initializeGraphEnv();
		
		setJasperReportsContext(jasperReportsContext);
		
		this.jasperReport = jasperReport;
		
		this.parent = parent;

		DatasetExpressionEvaluator initEvaluator = null;
		if (parent == null)
		{
			fillContext = new JRFillContext(this);
			printTransferPropertyPrefixes = readPrintTransferPropertyPrefixes();
		}
		else
		{
			fillContext = parent.getFiller().fillContext;
			printTransferPropertyPrefixes = parent.getFiller().printTransferPropertyPrefixes;
			initEvaluator = parent.getCachedEvaluator();
		}
		
		this.fillerId = fillContext.generatedFillerId();
		if (log.isDebugEnabled())
		{
			log.debug("Fill " + fillerId + ": created for " + jasperReport.getName());
		}
		
		if (initEvaluator == null)
		{
			calculator = JRFillDataset.createCalculator(jasperReportsContext, jasperReport, jasperReport.getMainDataset());
		}
		else
		{
			calculator = new JRCalculator(initEvaluator);
		}
		
		jasperPrint = new JasperPrint();
		propertiesUtil.transferProperties(jasperReport, jasperPrint, 
				JasperPrint.PROPERTIES_PRINT_TRANSFER_PREFIX);
		
		factory = initFillFactory();

		createDatasets();
		mainDataset = factory.getDataset(jasperReport.getMainDataset());
		
		if (parent == null)
		{
			FillDatasetPosition masterFillPosition = new FillDatasetPosition(null);
			mainDataset.setFillPosition(masterFillPosition);
		}
	}
	
	private List<String> readPrintTransferPropertyPrefixes()
	{
		List<JRPropertiesUtil.PropertySuffix> transferProperties = propertiesUtil.getProperties(
				JasperPrint.PROPERTIES_PRINT_TRANSFER_PREFIX);
		List<String> prefixes = new ArrayList<String>(transferProperties.size());
		for (JRPropertiesUtil.PropertySuffix property : transferProperties)
		{
			String transferPrefix = property.getValue();
			if (transferPrefix != null && transferPrefix.length() > 0)
			{
				prefixes.add(transferPrefix);
			}
		}
		return prefixes;
	}

	protected abstract JRFillObjectFactory initFillFactory();

	private void createDatasets() throws JRException
	{
		datasetMap = new HashMap<String,JRFillDataset>();

		JRDataset[] datasets = jasperReport.getDatasets();
		if (datasets != null && datasets.length > 0)
		{
			for (int i = 0; i < datasets.length; i++)
			{
				JRFillDataset fillDataset = factory.getDataset(datasets[i]);
				fillDataset.createCalculator(jasperReport);

				datasetMap.put(datasets[i].getName(), fillDataset);
			}
		}
	}

	protected final void initDatasets() throws JRException
	{
		mainDataset.initElementDatasets(factory);
		initDatasets(factory);

		mainDataset.checkVariableCalculationReqs(factory);

		mainDataset.setCalculator(calculator);
		mainDataset.initCalculator();
	}

	private void initDatasets(JRFillObjectFactory factory)
	{
		for (Iterator<JRFillDataset> it = datasetMap.values().iterator(); it.hasNext();)
		{
			JRFillDataset dataset = it.next();
			dataset.inheritFromMain();
			dataset.initElementDatasets(factory);
		}
	}

	public JasperReportsContext getJasperReportsContext()
	{
		return jasperReportsContext;
	}

	public JRPropertiesUtil getPropertiesUtil()
	{
		return propertiesUtil;
	}

	/**
	 * Returns the report.
	 *
	 * @return the report
	 */
	public JasperReport getJasperReport()
	{
		return jasperReport;
	}

	protected final void setJasperReportsContext(JasperReportsContext jasperReportsContext)
	{
		this.jasperReportsContext = jasperReportsContext;
		this.propertiesUtil = JRPropertiesUtil.getInstance(jasperReportsContext);
	}

	protected final void setParametersToContext(Map<String,Object> parameterValues)
	{
		JasperReportsContext localContext = LocalJasperReportsContext.getLocalContext(jasperReportsContext, parameterValues);
		if (localContext != jasperReportsContext)
		{
			setJasperReportsContext(localContext);
		}
	}

	@Override
	public JasperPrint fill(Map<String,Object> parameterValues, Connection conn) throws JRException
	{
		if (parameterValues == null)
		{
			parameterValues = new HashMap<String,Object>();
		}

		setConnectionParameterValue(parameterValues, conn);

		return fill(parameterValues);
	}

	protected void setConnectionParameterValue(Map<String,Object> parameterValues, Connection conn)
	{
		mainDataset.setConnectionParameterValue(parameterValues, conn);
	}

	@Override
	public JasperPrint fill(Map<String,Object> parameterValues, JRDataSource ds) throws JRException
	{
		if (parameterValues == null)
		{
			parameterValues = new HashMap<String,Object>();
		}

		setDatasourceParameterValue(parameterValues, ds);

		return fill(parameterValues);
	}

	protected void setDatasourceParameterValue(Map<String,Object> parameterValues, JRDataSource ds)
	{
		mainDataset.setDatasourceParameterValue(parameterValues, ds);
	}
	
	protected boolean isInterrupted()
	{
		return (isInterrupted || (parent != null && parent.getFiller().isInterrupted()));
	}

	protected void setInterrupted(boolean isInterrupted)
	{
		this.isInterrupted = isInterrupted;
	}

	protected void checkInterrupted()
	{
		if (Thread.interrupted())
		{
			setInterrupted(true);
		}
		
		if (isInterrupted())
		{
			if (log.isDebugEnabled())
			{
				log.debug("Fill " + fillerId + ": interrupting");
			}

			throw new JRFillInterruptedException();
		}
	}

	public JRFillContext getFillContext()
	{
		return fillContext;
	}

	public JRFillDataset getMainDataset()
	{
		return mainDataset;
	}
	
	/**
	 * Returns the map of parameter values.
	 * 
	 * @return the map of parameter values
	 */
	public Map<String,Object> getParameterValuesMap()
	{
		return mainDataset.getParameterValuesMap();
	}

	/**
	 * Returns the report parameters indexed by name.
	 *
	 * @return the report parameters map
	 */
	protected Map<String,JRFillParameter> getParametersMap()
	{
		return mainDataset.parametersMap;
	}

	/**
	 * Returns the report locale.
	 *
	 * @return the report locale
	 */
	protected Locale getLocale()
	{
		return mainDataset.getLocale();
	}

	/**
	 * Returns the report time zone.
	 *
	 * @return the report time zone
	 */
	protected TimeZone getTimeZone()
	{
		return mainDataset.timeZone;
	}

	/**
	 * Adds a variable calculation request.
	 *
	 * @param variableName
	 *            the variable name
	 * @param calculation
	 *            the calculation type
	 */
	protected void addVariableCalculationReq(String variableName, CalculationEnum calculation)
	{
		mainDataset.addVariableCalculationReq(variableName, calculation);
	}

	/**
	 * Returns a report variable.
	 *
	 * @param variableName the variable name
	 * @return the variable
	 */
	protected JRFillVariable getVariable(String variableName)
	{
		return mainDataset.getVariable(variableName);
	}

	protected JRFillExpressionEvaluator getExpressionEvaluator()
	{
		return calculator;
	}

	protected boolean isSubreport()
	{
		return parent != null;
	}

	/**
	 * Evaluates an expression
	 * @param expression the expression
	 * @param evaluation the evaluation type
	 * @return the evaluation result
	 * @throws JRException
	 */
	public Object evaluateExpression(JRExpression expression, byte evaluation) throws JRException
	{
		return mainDataset.evaluateExpression(expression, evaluation);
	}

	protected final void setFormatFactory(Map<String,Object> parameterValues)
	{
		formatFactory = (FormatFactory)parameterValues.get(JRParameter.REPORT_FORMAT_FACTORY);
		if (formatFactory == null)
		{
			formatFactory = DefaultFormatFactory.createFormatFactory(jasperReport.getFormatFactoryClass());
			parameterValues.put(JRParameter.REPORT_FORMAT_FACTORY, formatFactory);
		}
	}

	/**
	 * Returns the report format factory.
	 *
	 * @return the report format factory
	 */
	protected FormatFactory getFormatFactory()
	{
		return formatFactory;
	}

}
