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

/*
 * Contributors:
 * Peter Severin - peter_p_s@users.sourceforge.net 
 */
package net.sf.jasperreports.engine.fill;

import java.text.MessageFormat;
import java.util.Map;
import java.util.MissingResourceException;
import java.util.ResourceBundle;

import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.JRExpression;
import net.sf.jasperreports.engine.JRParameter;
import net.sf.jasperreports.engine.JRReport;
import net.sf.jasperreports.engine.JRRuntimeException;
import net.sf.jasperreports.engine.JRVariable;


/**
 * @author Teodor Danciu (teodord@users.sourceforge.net)
 * @version $Id$
 */
public abstract class JRCalculator
{


	/**
	 *
	 */
	protected Map parsm = null;
	protected Map fldsm = null;
	protected Map varsm = null;
	protected JRFillVariable[] variables = null;
	protected JRFillGroup[] groups = null;
	protected JRFillChartDataset[] datasets = null;

	private JRFillParameter resourceBundle = null;
	private JRFillVariable pageNumber = null;
	private JRFillVariable columnNumber = null;
	
	protected JRBaseFiller filler;


	/**
	 *
	 */
	protected JRCalculator()
	{
	}


	/**
	 *
	 */
	protected void init(JRBaseFiller parentFiller) throws JRException
	{
		filler = parentFiller;
		
		parsm = filler.parametersMap;
		fldsm = filler.fieldsMap;
		varsm = filler.variablesMap;
		variables = filler.variables;
		groups = filler.groups;
		datasets = filler.datasets;

		resourceBundle = (JRFillParameter)parsm.get(JRParameter.REPORT_RESOURCE_BUNDLE);
		pageNumber = (JRFillVariable)varsm.get(JRVariable.PAGE_NUMBER);
		columnNumber = (JRFillVariable)varsm.get(JRVariable.COLUMN_NUMBER);
		
		customizedInit(
			parsm,
			fldsm,
			varsm
			);
	}


	/**
	 *
	 */
	protected abstract void customizedInit(
		Map parametersMap,
		Map fieldsMap,
		Map variablesMap
		) throws JRException;


	/**
	 *
	 */
	public JRFillVariable getPageNumber()
	{
		return pageNumber;
	}
	

	/**
	 *
	 */
	public JRFillVariable getColumnNumber()
	{
		return columnNumber;
	}
	

	/**
	 *
	 */
	public void calculateVariables() throws JRException
	{
		if (variables != null && variables.length > 0)
		{
			for(int i = 0; i < variables.length; i++)
			{
				JRFillVariable variable = variables[i];
				Object expressionValue = evaluate(variable.getExpression());
				Object newValue = variable.getIncrementer().increment(variable, expressionValue, AbstractValueProvider.getCurrentValueProvider());
				variable.setValue(newValue);
				variable.setInitialized(false);

				if (variable.getIncrementType() == JRVariable.RESET_TYPE_NONE)
				{
					variable.setIncrementedValue(variable.getValue());
				}
			}
		}

		if (datasets != null && datasets.length > 0)
		{
			for(int i = 0; i < datasets.length; i++)
			{
				JRFillChartDataset dataset = datasets[i];
				dataset.evaluate(this);

				if (dataset.getIncrementType() == JRVariable.RESET_TYPE_NONE)
				{
					dataset.increment();
				}
			}
		}
	}


	/**
	 *
	 */
	public void estimateVariables() throws JRException
	{
		if (variables != null && variables.length > 0)
		{
			JRFillVariable variable = null;
			Object expressionValue = null;
			Object newValue = null;
			
			for(int i = 0; i < variables.length; i++)
			{
				variable = variables[i];
				expressionValue = evaluateEstimated(variable.getExpression());
				newValue = variable.getIncrementer().increment(variable, expressionValue,  AbstractValueProvider.getEstimatedValueProvider());
				variable.setEstimatedValue(newValue);
				//variable.setInitialized(false);
			}
		}
	}


	/**
	 *
	 */
	public void estimateGroupRuptures() throws JRException
	{
		estimateVariables();

		JRFillGroup group = null;
		Object oldValue = null;
		Object estimatedValue = null;
		boolean groupHasChanged = false;
		boolean isTopLevelChange = false;
		if (groups != null && groups.length > 0)
		{
			for(int i = 0; i < groups.length; i++)
			{
				group = groups[i];
				
				isTopLevelChange = false;

				if (!groupHasChanged)
				{
					oldValue = evaluateOld(group.getExpression());
					estimatedValue = evaluateEstimated(group.getExpression());

					if ( 
						(oldValue == null && estimatedValue != null) ||
						(oldValue != null && !oldValue.equals(estimatedValue))
						)
					{
						groupHasChanged = true;
						isTopLevelChange = true;
					}
				}

				group.setHasChanged(groupHasChanged);
				group.setTopLevelChange(isTopLevelChange);
			}
		}
	}


	/**
	 *
	 */
	public void initializeVariables(byte resetType) throws JRException
	{
		if (variables != null && variables.length > 0)
		{
			for(int i = 0; i < variables.length; i++)
			{
				incrementVariable(variables[i], resetType);
				initializeVariable(variables[i], resetType);
			}
		}

		if (datasets != null && datasets.length > 0)
		{
			for(int i = 0; i < datasets.length; i++)
			{
				incrementDataset(datasets[i], resetType);
				initializeDataset(datasets[i], resetType);
			}
		}
	}


	/**
	 *
	 */
	private void incrementVariable(JRFillVariable variable, byte incrementType)
	{
		if (variable.getIncrementType() != JRVariable.RESET_TYPE_NONE)
		{
			boolean toIncrement = false;
			switch (incrementType)
			{
				case JRVariable.RESET_TYPE_REPORT :
				{
					toIncrement = true;
					break;
				}
				case JRVariable.RESET_TYPE_PAGE :
				{
					toIncrement = 
						(
						variable.getIncrementType() == JRVariable.RESET_TYPE_PAGE || 
						variable.getIncrementType() == JRVariable.RESET_TYPE_COLUMN
						);
					break;
				}
				case JRVariable.RESET_TYPE_COLUMN :
				{
					toIncrement = (variable.getIncrementType() == JRVariable.RESET_TYPE_COLUMN);
					break;
				}
				case JRVariable.RESET_TYPE_GROUP :
				{
					if (variable.getIncrementType() == JRVariable.RESET_TYPE_GROUP)
					{
						JRFillGroup group = (JRFillGroup)variable.getIncrementGroup();
						toIncrement = group.hasChanged();
					}
					break;
				}
				case JRVariable.RESET_TYPE_NONE :
				default :
				{
				}
			}

			if (toIncrement)
			{
				variable.setIncrementedValue(variable.getValue());
//				variable.setValue(
//					evaluate(variable.getInitialValueExpression())
//					);
//				variable.setInitialized(true);
			}
		}
		else
		{
			variable.setIncrementedValue(variable.getValue());
//			variable.setValue(
//				evaluate(variable.getExpression())
//				);
		}
	}


	/**
	 *
	 */
	private void incrementDataset(JRFillChartDataset dataset, byte incrementType)
	{
		if (dataset.getIncrementType() != JRVariable.RESET_TYPE_NONE)
		{
			boolean toIncrement = false;
			switch (incrementType)
			{
				case JRVariable.RESET_TYPE_REPORT :
				{
					toIncrement = true;
					break;
				}
				case JRVariable.RESET_TYPE_PAGE :
				{
					toIncrement = 
						(
						dataset.getIncrementType() == JRVariable.RESET_TYPE_PAGE || 
						dataset.getIncrementType() == JRVariable.RESET_TYPE_COLUMN
						);
					break;
				}
				case JRVariable.RESET_TYPE_COLUMN :
				{
					toIncrement = (dataset.getIncrementType() == JRVariable.RESET_TYPE_COLUMN);
					break;
				}
				case JRVariable.RESET_TYPE_GROUP :
				{
					if (dataset.getIncrementType() == JRVariable.RESET_TYPE_GROUP)
					{
						JRFillGroup group = (JRFillGroup)dataset.getIncrementGroup();
						toIncrement = group.hasChanged();
					}
					break;
				}
				case JRVariable.RESET_TYPE_NONE :
				default :
				{
				}
			}

			if (toIncrement)
			{
				dataset.increment();
			}
		}
		else
		{
			//FIXME NOW dataset.increment();
		}
	}


	/**
	 *
	 */
	private void initializeVariable(JRFillVariable variable, byte resetType) throws JRException
	{
		//if (jrVariable.getCalculation() != JRVariable.CALCULATION_NOTHING)
		if (variable.getResetType() != JRVariable.RESET_TYPE_NONE)
		{
			boolean toInitialize = false;
			switch (resetType)
			{
				case JRVariable.RESET_TYPE_REPORT :
				{
					toInitialize = true;
					break;
				}
				case JRVariable.RESET_TYPE_PAGE :
				{
					toInitialize = 
						(
						variable.getResetType() == JRVariable.RESET_TYPE_PAGE || 
						variable.getResetType() == JRVariable.RESET_TYPE_COLUMN
						);
					break;
				}
				case JRVariable.RESET_TYPE_COLUMN :
				{
					toInitialize = (variable.getResetType() == JRVariable.RESET_TYPE_COLUMN);
					break;
				}
				case JRVariable.RESET_TYPE_GROUP :
				{
					if (variable.getResetType() == JRVariable.RESET_TYPE_GROUP)
					{
						JRFillGroup group = (JRFillGroup)variable.getResetGroup();
						toInitialize = group.hasChanged();
					}
					break;
				}
				case JRVariable.RESET_TYPE_NONE :
				default :
				{
				}
			}

			if (toInitialize)
			{
				variable.setValue(
					evaluate(variable.getInitialValueExpression())
					);
				variable.setInitialized(true);
				variable.setIncrementedValue(null);
			}
		}
		else
		{
			variable.setValue(
				evaluate(variable.getExpression())
				);
			variable.setIncrementedValue(variable.getValue());
		}
	}


	/**
	 *
	 */
	private void initializeDataset(JRFillChartDataset dataset, byte resetType)
	{
		//if (jrVariable.getCalculation() != JRVariable.CALCULATION_NOTHING)
//		if (dataset.getResetType() != JRVariable.RESET_TYPE_NONE)
//		{
			boolean toInitialize = false;
			switch (resetType)
			{
				case JRVariable.RESET_TYPE_REPORT :
				{
					toInitialize = true;
					break;
				}
				case JRVariable.RESET_TYPE_PAGE :
				{
					toInitialize = 
						(
						dataset.getResetType() == JRVariable.RESET_TYPE_PAGE || 
						dataset.getResetType() == JRVariable.RESET_TYPE_COLUMN
						);
					break;
				}
				case JRVariable.RESET_TYPE_COLUMN :
				{
					toInitialize = (dataset.getResetType() == JRVariable.RESET_TYPE_COLUMN);
					break;
				}
				case JRVariable.RESET_TYPE_GROUP :
				{
					if (dataset.getResetType() == JRVariable.RESET_TYPE_GROUP)
					{
						JRFillGroup group = (JRFillGroup)dataset.getResetGroup();
						toInitialize = group.hasChanged();
					}
					break;
				}
				case JRVariable.RESET_TYPE_NONE :
				default :
				{
				}
			}

			if (toInitialize)
			{
//				dataset.setValue(
//					evaluate(dataset.getInitialValueExpression())
//					);
//				dataset.setInitialized(true);
//				dataset.setIncrementedValue(null);
				dataset.initialize();
			}
//		}FIXME NOW verify that reset type none does not make any sense
//		else
//		{
//			dataset.setValue(
//				evaluate(dataset.getExpression())
//				);
//			dataset.setIncrementedValue(dataset.getValue());
//		}
	}


	/**
	 *
	 */
	protected Object evaluate(
		JRExpression expression,
		byte evaluationType
		) throws JRException
	{
		Object value = null;
		
		switch (evaluationType)
		{
			case JRExpression.EVALUATION_OLD :
			{
				value = evaluateOld(expression);
				break;
			}
			case JRExpression.EVALUATION_ESTIMATED :
			{
				value = evaluateEstimated(expression);
				break;
			}
			case JRExpression.EVALUATION_DEFAULT :
			default :
			{
				value = evaluate(expression);
				break;
			}
		}

		return value;
	}
	

	/**
	 *
	 */
	public Object evaluateOld(JRExpression expression) throws JRExpressionEvalException
	{
		Object value = null;
		
		try
		{
			value = evaluateOld(expression.getId());
		}
		catch (NullPointerException e)
		{
		}
		catch (OutOfMemoryError e)
		{
			throw e;
		}
		catch (Throwable e)
		{
			throw new JRExpressionEvalException(expression, e); 
		}
		
		return value;
	}


	/**
	 *
	 */
	public Object evaluateEstimated(JRExpression expression) throws JRExpressionEvalException
	{
		Object value = null;
		
		try
		{
			value = evaluateEstimated(expression.getId());
		}
		catch (NullPointerException e)
		{
		}
		catch (OutOfMemoryError e)
		{
			throw e;
		}
		catch (Throwable e)
		{
			throw new JRExpressionEvalException(expression, e); 
		}
		
		return value;
	}


	/**
	 *
	 */
	public Object evaluate(JRExpression expression) throws JRExpressionEvalException
	{
		Object value = null;
		
		try
		{
			value = evaluate(expression.getId());
		}
		catch (NullPointerException e)
		{
		}
		catch (OutOfMemoryError e)
		{
			throw e;
		}
		catch (Throwable e)
		{
			throw new JRExpressionEvalException(expression, e); 
		}
		
		return value;
	}


	/**
	 *
	 */
	protected abstract Object evaluateOld(int id) throws Throwable;


	/**
	 *
	 */
	protected abstract Object evaluateEstimated(int id) throws Throwable;


	/**
	 *
	 */
	protected abstract Object evaluate(int id) throws Throwable;


	/**
	 *
	 */
	public String str(String key)
	{
		String str = null;
		
		try
		{
			str = ((ResourceBundle)resourceBundle.getValue()).getString(key);
		}
		catch (NullPointerException e)
		{
			str = handleMissingResource(key, e);
		}
		catch (MissingResourceException e)
		{
			str = handleMissingResource(key, e);
		}

		return str;
	}

	/**
	 *
	 */
	public String msg(String pattern, Object arg0)
	{
		return MessageFormat.format(pattern, new Object[]{arg0});
	}

	/**
	 *
	 */
	public String msg(String pattern, Object arg0, Object arg1)
	{
		return MessageFormat.format(pattern, new Object[]{arg0, arg1});
	}

	/**
	 *
	 */
	public String msg(String pattern, Object arg0, Object arg1, Object arg2)
	{
		return MessageFormat.format(pattern, new Object[]{arg0, arg1, arg2});
	}

	/**
	 * Handles the case when a resource is missing.
	 * 
	 * @param key the resource key
	 * @param e the exception
	 * @return the value to use for the resource 
	 * @throws JRRuntimeException when the resource missing handling type is Error
	 */
	protected String handleMissingResource(String key, Exception e) throws JRRuntimeException
	{
		String str;
		switch (filler.whenResourceMissingType)
		{
			case JRReport.WHEN_RESOURCE_MISSING_TYPE_EMPTY:
			{
				str = "";
				break;
			}
			case JRReport.WHEN_RESOURCE_MISSING_TYPE_KEY:
			{
				str = key;
				break;
			}
			case JRReport.WHEN_RESOURCE_MISSING_TYPE_ERROR:
			{
				throw new JRRuntimeException("Resource nout found for key \"" + key + "\".", e);
			}
			case JRReport.WHEN_RESOURCE_MISSING_TYPE_NULL:
			default:
			{
				str = null;
				break;
			}
		}
		
		return str;
	}

}
