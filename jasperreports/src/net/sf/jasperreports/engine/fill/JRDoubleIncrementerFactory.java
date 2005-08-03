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

import net.sf.jasperreports.engine.JRVariable;


/**
 * @author Teodor Danciu (teodord@users.sourceforge.net)
 * @version $Id$
 */
public class JRDoubleIncrementerFactory implements JRIncrementerFactory
{


	/**
	 *
	 */
	protected static final Double ZERO = new Double(0);


	/**
	 *
	 */
	private static JRDoubleIncrementerFactory mainInstance = new JRDoubleIncrementerFactory();


	/**
	 *
	 */
	private JRDoubleIncrementerFactory()
	{
	}


	/**
	 *
	 */
	public static JRDoubleIncrementerFactory getInstance()
	{
		return mainInstance;
	}


	/**
	 *
	 */
	public JRIncrementer getIncrementer(byte calculation)
	{
		JRIncrementer incrementer = null;

		switch (calculation)
		{
			case JRVariable.CALCULATION_COUNT :
			{
				incrementer = JRDoubleCountIncrementer.getInstance();
				break;
			}
			case JRVariable.CALCULATION_SUM :
			{
				incrementer = JRDoubleSumIncrementer.getInstance();
				break;
			}
			case JRVariable.CALCULATION_AVERAGE :
			{
				incrementer = JRDoubleAverageIncrementer.getInstance();
				break;
			}
			case JRVariable.CALCULATION_LOWEST :
			case JRVariable.CALCULATION_HIGHEST :
			{
				incrementer = JRComparableIncrementerFactory.getInstance().getIncrementer(calculation);
				break;
			}
			case JRVariable.CALCULATION_STANDARD_DEVIATION :
			{
				incrementer = JRDoubleStandardDeviationIncrementer.getInstance();
				break;
			}
			case JRVariable.CALCULATION_VARIANCE :
			{
				incrementer = JRDoubleVarianceIncrementer.getInstance();
				break;
			}
			case JRVariable.CALCULATION_SYSTEM :
			case JRVariable.CALCULATION_NOTHING :
			default :
			{
				incrementer = JRDefaultIncrementerFactory.getInstance().getIncrementer(calculation);
				break;
			}
		}
		
		return incrementer;
	}


}


/**
 *
 */
class JRDoubleCountIncrementer implements JRIncrementer
{
	/**
	 *
	 */
	private static JRDoubleCountIncrementer mainInstance = new JRDoubleCountIncrementer();

	/**
	 *
	 */
	private JRDoubleCountIncrementer()
	{
	}

	/**
	 *
	 */
	public static JRDoubleCountIncrementer getInstance()
	{
		return mainInstance;
	}

	/**
	 *
	 */
	public Object increment(
		JRFillVariable variable, 
		Object expressionValue,
		AbstractValueProvider valueProvider
		)
	{
		Number value = (Number)variable.getIncrementedValue();

		if (expressionValue == null)
		{
			if (variable.isInitialized())
			{
				return JRDoubleIncrementerFactory.ZERO;
			}
		
			return value;
		}

		if (value == null || variable.isInitialized())
		{
			value = JRDoubleIncrementerFactory.ZERO;
		}

		return new Double(value.doubleValue() + 1);
	}
}


/**
 *
 */
class JRDoubleSumIncrementer implements JRIncrementer
{
	/**
	 *
	 */
	private static JRDoubleSumIncrementer mainInstance = new JRDoubleSumIncrementer();

	/**
	 *
	 */
	private JRDoubleSumIncrementer()
	{
	}

	/**
	 *
	 */
	public static JRDoubleSumIncrementer getInstance()
	{
		return mainInstance;
	}

	/**
	 *
	 */
	public Object increment(
		JRFillVariable variable, 
		Object expressionValue,
		AbstractValueProvider valueProvider
		)
	{
		Number value = (Number)variable.getIncrementedValue();
		Number newValue = (Number)expressionValue;

		if (newValue == null)
		{
			if (variable.isInitialized())
			{
				return null;
			}

			return value;
		}

		if (value == null || variable.isInitialized())
		{
			value = JRDoubleIncrementerFactory.ZERO;
		}

		return new Double(value.doubleValue() + newValue.doubleValue());
	}
}


/**
 *
 */
class JRDoubleAverageIncrementer implements JRIncrementer
{
	/**
	 *
	 */
	private static JRDoubleAverageIncrementer mainInstance = new JRDoubleAverageIncrementer();

	/**
	 *
	 */
	private JRDoubleAverageIncrementer()
	{
	}

	/**
	 *
	 */
	public static JRDoubleAverageIncrementer getInstance()
	{
		return mainInstance;
	}

	/**
	 *
	 */
	public Object increment(
		JRFillVariable variable, 
		Object expressionValue,
		AbstractValueProvider valueProvider
		)
	{
		if (expressionValue == null)
		{
			if (variable.isInitialized())
			{
				return null;
			}
			return variable.getValue();
		}
		Number countValue = (Number)valueProvider.getValue(variable.getHelperVariable(JRFillVariable.HELPER_COUNT));
		Number sumValue = (Number)valueProvider.getValue(variable.getHelperVariable(JRFillVariable.HELPER_SUM));
		return new Double(sumValue.doubleValue() / countValue.doubleValue());
	}
}


/**
 *
 */
class JRDoubleStandardDeviationIncrementer implements JRIncrementer
{
	/**
	 *
	 */
	private static JRDoubleStandardDeviationIncrementer mainInstance = new JRDoubleStandardDeviationIncrementer();

	/**
	 *
	 */
	private JRDoubleStandardDeviationIncrementer()
	{
	}

	/**
	 *
	 */
	public static JRDoubleStandardDeviationIncrementer getInstance()
	{
		return mainInstance;
	}

	/**
	 *
	 */
	public Object increment(
		JRFillVariable variable, 
		Object expressionValue,
		AbstractValueProvider valueProvider
		)
	{
		if (expressionValue == null)
		{
			if (variable.isInitialized())
			{
				return null;
			}
			return variable.getValue(); 
		}
		Number varianceValue = (Number)valueProvider.getValue(variable.getHelperVariable(JRFillVariable.HELPER_VARIANCE));
		return new Double( Math.sqrt(varianceValue.doubleValue()) );
	}
}


/**
 *
 */
class JRDoubleVarianceIncrementer implements JRIncrementer
{
	/**
	 *
	 */
	private static JRDoubleVarianceIncrementer mainInstance = new JRDoubleVarianceIncrementer();

	/**
	 *
	 */
	private JRDoubleVarianceIncrementer()
	{
	}

	/**
	 *
	 */
	public static JRDoubleVarianceIncrementer getInstance()
	{
		return mainInstance;
	}

	/**
	 *
	 */
	public Object increment(
		JRFillVariable variable, 
		Object expressionValue,
		AbstractValueProvider valueProvider
		)
	{
		Number value = (Number)variable.getIncrementedValue();
		Number newValue = (Number)expressionValue;
		
		if (newValue == null)
		{
			if (variable.isInitialized())
			{
				return null;
			}
			return value;
		}
		else if (value == null || variable.isInitialized())
		{
			return JRDoubleIncrementerFactory.ZERO;
		}
		else
		{
			Number countValue = (Number)valueProvider.getValue(variable.getHelperVariable(JRFillVariable.HELPER_COUNT));
			Number sumValue = (Number)valueProvider.getValue(variable.getHelperVariable(JRFillVariable.HELPER_SUM));
			return
				new Double(
					(countValue.doubleValue() - 1) * value.doubleValue() / countValue.doubleValue() +
					( sumValue.doubleValue() / countValue.doubleValue() - newValue.doubleValue() ) *
					( sumValue.doubleValue() / countValue.doubleValue() - newValue.doubleValue() ) /
					(countValue.doubleValue() - 1)
					);
		}
	}
}
