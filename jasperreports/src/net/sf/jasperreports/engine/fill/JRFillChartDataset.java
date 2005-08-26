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

import net.sf.jasperreports.engine.JRChartDataset;
import net.sf.jasperreports.engine.JRDatasetRun;
import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.JRGroup;

import org.jfree.data.general.Dataset;


/**
 * @author Teodor Danciu (teodord@users.sourceforge.net)
 * @version $Id$
 */
public abstract class JRFillChartDataset implements JRChartDataset
{


	/**
	 *
	 */
	protected JRChartDataset parent = null;

	protected JRGroup resetGroup = null;
	protected JRGroup incrementGroup = null;

	private boolean isIncremented = true;

	protected JRFillDatasetRun datasetRun;

	/**
	 *
	 */
	protected JRFillChartDataset(
		JRChartDataset dataset, 
		JRFillObjectFactory factory
		)
	{
		factory.put(dataset, this);

		parent = dataset;
		
		resetGroup = factory.getGroup(dataset.getResetGroup());
		incrementGroup = factory.getGroup(dataset.getIncrementGroup());
		
		datasetRun = factory.getDatasetRun(dataset.getDatasetRun());
	}


	/**
	 *
	 */
	public byte getResetType()
	{
		return parent.getResetType();
	}
		
	/**
	 *
	 */
	public byte getIncrementType()
	{
		return parent.getIncrementType();
	}
		
	/**
	 *
	 */
	public JRGroup getResetGroup()
	{
		return resetGroup;
	}
		
	/**
	 *
	 */
	public JRGroup getIncrementGroup()
	{
		return incrementGroup;
	}
		
	/**
	 *
	 */
	protected void initialize()
	{
		customInitialize();
		isIncremented = false;
	}

	/**
	 *
	 */
	protected void evaluate(JRCalculator calculator) throws JRExpressionEvalException
	{
		customEvaluate(calculator);
		isIncremented = false;
	}

	/**
	 *
	 */
	protected void increment()
	{
		if (!isIncremented)
		{
			customIncrement();
		}
		isIncremented = true;
	}

	/**
	 *
	 */
	public Dataset getDataset()
	{
		increment();
		
		return getCustomDataset();
	}


	/**
	 *
	 */
	protected abstract void customInitialize();

	/**
	 *
	 */
	protected abstract void customEvaluate(JRCalculator calculator) throws JRExpressionEvalException;

	/**
	 *
	 */
	protected abstract void customIncrement();

	/**
	 *
	 */
	protected abstract Dataset getCustomDataset();


	public JRDatasetRun getDatasetRun()
	{
		return datasetRun;
	}
	
	
	protected void evaluateDatasetRun(byte evaluation) throws JRException
	{
		if (datasetRun != null)
		{
			datasetRun.evaluate(this, evaluation);
		}
	}
}
