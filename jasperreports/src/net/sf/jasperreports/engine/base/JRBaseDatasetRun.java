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
package net.sf.jasperreports.engine.base;

import java.io.Serializable;

import net.sf.jasperreports.engine.JRDatasetRun;
import net.sf.jasperreports.engine.JRExpression;
import net.sf.jasperreports.engine.JRSubreportParameter;

/**
 * Base implementation of the {@link net.sf.jasperreports.engine.JRDatasetRun JRDatasetRun} interface.
 * 
 * @author Lucian Chirita (lucianc@users.sourceforge.net)
 * @version $Id$
 */
public class JRBaseDatasetRun implements JRDatasetRun, Serializable
{
	protected String datasetName;
	protected JRExpression parametersMapExpression;
	protected JRSubreportParameter[] parameters;
	protected JRExpression connectionExpression;
	protected JRExpression dataSourceExpression;
	
	
	/**
	 * Creates an empty object.
	 */
	protected JRBaseDatasetRun()
	{
	}

	
	/**
	 * Creates a copy of a dataset instantiation.
	 * 
	 * @param datasetRun the dataset instantiation
	 * @param factory the base object factory
	 */
	protected JRBaseDatasetRun(JRDatasetRun datasetRun, JRBaseObjectFactory factory)
	{
		if (factory != null)
		{
			factory.put(datasetRun, this);
		}
		
		datasetName = datasetRun.getDatasetName();
		parametersMapExpression = datasetRun.getParametersMapExpression();
		connectionExpression = datasetRun.getConnectionExpression();
		dataSourceExpression = datasetRun.getDataSourceExpression();
		
		JRSubreportParameter[] datasetParams = datasetRun.getParameters();
		if (datasetParams != null && datasetParams.length > 0)
		{
			parameters = new JRBaseSubreportParameter[datasetParams.length];
			for (int i = 0; i < parameters.length; i++)
			{
				if (factory != null)
				{
					parameters[i] = factory.getSubreportParameter(datasetParams[i]);
				}
				else
				{
					parameters[i] = new JRBaseSubreportParameter(datasetParams[i], null);
				}
			}
		}
	}

	public String getDatasetName()
	{
		return datasetName;
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
