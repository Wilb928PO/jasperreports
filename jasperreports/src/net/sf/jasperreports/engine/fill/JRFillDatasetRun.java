/*
 * ============================================================================
 * GNU Lesser General Public License
 * ============================================================================
 *
 * JasperReports - Free Java report-generating library.
 * Copyright (C) 2001-2005 JasperSoft Corporation http://www.jaspersoft.com
 * 
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2.1 of the License, or (at your option) any later version.
 * 
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 * 
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307, USA.
 * 
 * JasperSoft Corporation
 * 185, Berry Street, Suite 6200
 * San Francisco CA 94107
 * http://www.jaspersoft.com
 */
package net.sf.jasperreports.engine.fill;

import java.sql.Connection;
import java.util.Map;

import net.sf.jasperreports.engine.JRDataSource;
import net.sf.jasperreports.engine.JRDatasetRun;
import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.JRExpression;
import net.sf.jasperreports.engine.JRParameter;
import net.sf.jasperreports.engine.JRScriptletException;
import net.sf.jasperreports.engine.JRSubreportParameter;
import net.sf.jasperreports.engine.JRVariable;

/**
 * Class used to instantiate sub datasets.
 * 
 * @author Lucian Chirita (lucianc@users.sourceforge.net)
 * @version $Id$
 */
public class JRFillDatasetRun implements JRDatasetRun
{
	private final JRBaseFiller filler;

	private final JRFillDataset dataset;

	private JRExpression parametersMapExpression;

	private JRSubreportParameter[] parameters;

	private JRExpression connectionExpression;

	private JRExpression dataSourceExpression;

	
	/**
	 * Construct an instance for a dataset run.
	 * 
	 * @param filler the filler
	 * @param datasetRun the dataset run
	 * @param factory the fill object factory
	 */
	public JRFillDatasetRun(JRBaseFiller filler, JRDatasetRun datasetRun, JRFillObjectFactory factory)
	{
		factory.put(datasetRun, this);

		this.filler = filler;
		this.dataset = (JRFillDataset) filler.datasetMap.get(datasetRun.getDatasetName());

		parametersMapExpression = datasetRun.getParametersMapExpression();
		parameters = datasetRun.getParameters();
		connectionExpression = datasetRun.getConnectionExpression();
		dataSourceExpression = datasetRun.getDataSourceExpression();
	}

	
	/**
	 * Instantiates and iterates the sub dataset for a chart dataset evaluation.
	 * 
	 * @param chartDataset the chart dataset
	 * @param evaluation the evaluation type
	 * @throws JRException
	 */
	public void evaluate(JRFillChartDataset chartDataset, byte evaluation) throws JRException
	{
		Map parameterValues = JRFillSubreport.getParameterValues(filler, parametersMapExpression, parameters, evaluation);

		if (!parameterValues.containsKey(JRParameter.REPORT_LOCALE))
		{
			parameterValues.put(JRParameter.REPORT_LOCALE, filler.getLocale());
		}

		dataset.setParameters(parameterValues);
		dataset.setParameterValues(parameterValues);

		try
		{
			if (dataSourceExpression != null)
			{
				JRDataSource dataSource = (JRDataSource) filler.evaluateExpression(dataSourceExpression, evaluation);

				dataset.setDatasource(parameterValues, dataSource);
			}
			else if (dataset.getQuery() != null)
			{
				Connection connection = null;

				if (connectionExpression != null)
				{
					connection = (Connection) filler.evaluateExpression(connectionExpression, evaluation);
				}
				else
				{
					JRFillParameter connParam = (JRFillParameter) filler.getParametersMap().get(JRParameter.REPORT_CONNECTION);
					connection = (Connection) connParam.getValue();
				}

				JRDataSource dataSource = dataset.createDataSource(parameterValues, connection);
				dataset.setDatasource(parameterValues, dataSource);
			}
			else
			{
				throw new JRException("Cannot instantiate data set.");
			}
			
			dataset.filterChartDatasets(chartDataset);
			
			dataset.initScriptlet();
			dataset.initCalculator();

			iterate();
		}
		finally
		{
			dataset.closeStatement();
			dataset.restoreChartDatasets();
		}
	}

	protected void iterate() throws JRException
	{
		dataset.start();

		if (dataset.next())
		{
			init();

			detail();

			while (dataset.next())
			{
				checkInterrupted();

				group();

				detail();
			}
		}

	}

	
	protected void checkInterrupted()
	{
		if (Thread.currentThread().isInterrupted() || filler.isInterrupted())
		{
			filler.setInterrupted(true);

			throw new JRFillInterruptedException();
		}
	}

	
	protected void group() throws JRException, JRScriptletException
	{
		dataset.calculator.estimateGroupRuptures();

		dataset.scriptlet.callBeforeGroupInit();
		dataset.calculator.initializeVariables(JRVariable.RESET_TYPE_GROUP);
		dataset.scriptlet.callAfterGroupInit();
	}

	protected void init() throws JRScriptletException, JRException
	{
		dataset.scriptlet.callBeforeReportInit();
		dataset.calculator.initializeVariables(JRVariable.RESET_TYPE_REPORT);
		dataset.scriptlet.callAfterReportInit();
	}

	protected void detail() throws JRScriptletException, JRException
	{
		dataset.scriptlet.callBeforeDetailEval();
		dataset.calculator.calculateVariables();
		dataset.scriptlet.callAfterDetailEval();
	}

	public String getDatasetName()
	{
		return dataset.getName();
	}

	public JRExpression getParametersMapExpression()
	{
		return parametersMapExpression;
	}

	public JRSubreportParameter[] getParameters()
	{
		return parameters;
	}

	public JRExpression getConnectionExpression()
	{
		return connectionExpression;
	}

	public JRExpression getDataSourceExpression()
	{
		return dataSourceExpression;
	}
}
