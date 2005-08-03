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

import net.sf.jasperreports.engine.JRExpression;
import net.sf.jasperreports.engine.JRGroup;
import net.sf.jasperreports.engine.JRVariable;


/**
 * @author Teodor Danciu (teodord@users.sourceforge.net)
 * @version $Id$
 */
public class JRFillVariable implements JRVariable
{


	/**
	 *
	 */
	protected JRVariable parent = null;

	/**
	 *
	 */
	private JRGroup resetGroup = null;
	private JRGroup incrementGroup = null;

	/**
	 *
	 */
	private Object oldValue = null;
	private Object estimatedValue = null;
	private Object incrementedValue = null;
	private Object value = null;
	private boolean isInitialized = false;
	
	/**
	 * Constant for the count helper variable.
	 */
	public static final byte HELPER_COUNT = 0;
	
	/**
	 * Constant for the count sum variable.
	 */
	public static final byte HELPER_SUM = 1;
	
	/**
	 * Constant for the count variance variable.
	 */
	public static final byte HELPER_VARIANCE = 2;
	
	private static final int HELPER_SIZE = 3;
	
	private JRFillVariable[] helperVariables;

	/**
	 *
	 */
	private JRIncrementer incrementer = null;


	/**
	 *
	 */
	protected JRFillVariable(
		JRVariable variable, 
		JRFillObjectFactory factory
		)
	{
		factory.put(variable, this);

		parent = variable;
		
		resetGroup = factory.getGroup(variable.getResetGroup());
		incrementGroup = factory.getGroup(variable.getIncrementGroup());
		
		helperVariables = new JRFillVariable[HELPER_SIZE];
	}


	/**
	 *
	 */
	public String getName()
	{
		return this.parent.getName();
	}
		
	/**
	 *
	 */
	public Class getValueClass()
	{
		return this.parent.getValueClass();
	}
		
	/**
	 *
	 */
	public String getValueClassName()
	{
		return this.parent.getValueClassName();
	}
		
	/**
	 *
	 */
	public Class getIncrementerFactoryClass()
	{
		return this.parent.getIncrementerFactoryClass();
	}
		
	/**
	 *
	 */
	public String getIncrementerFactoryClassName()
	{
		return this.parent.getIncrementerFactoryClassName();
	}
		
	/**
	 *
	 */
	public JRExpression getExpression()
	{
		return this.parent.getExpression();
	}
		
	/**
	 *
	 */
	public JRExpression getInitialValueExpression()
	{
		return this.parent.getInitialValueExpression();
	}
		
	/**
	 *
	 */
	public byte getResetType()
	{
		return this.parent.getResetType();
	}
		
	/**
	 *
	 */
	public byte getIncrementType()
	{
		return this.parent.getIncrementType();
	}
		
	/**
	 *
	 */
	public byte getCalculation()
	{
		return this.parent.getCalculation();
	}
		
	/**
	 *
	 */
	public boolean isSystemDefined()
	{
		return this.parent.isSystemDefined();
	}

	/**
	 *
	 */
	public JRGroup getResetGroup()
	{
		return this.resetGroup;
	}
		
	/**
	 *
	 */
	public JRGroup getIncrementGroup()
	{
		return this.incrementGroup;
	}
	
	/**
	 *
	 */
	public Object getOldValue()
	{
		return this.oldValue;
	}
		
	/**
	 *
	 */
	public void setOldValue(Object oldValue)
	{
		this.oldValue = oldValue;
	}

	/**
	 *
	 */
	public Object getEstimatedValue()
	{
		return this.estimatedValue;
	}
		
	/**
	 *
	 */
	public void setEstimatedValue(Object estimatedValue)
	{
		this.estimatedValue = estimatedValue;
	}

	/**
	 *
	 */
	public Object getIncrementedValue()
	{
		return this.incrementedValue;
	}
		
	/**
	 *
	 */
	public void setIncrementedValue(Object incrementedValue)
	{
		this.incrementedValue = incrementedValue;
	}

	/**
	 *
	 */
	public Object getValue()
	{
		return this.value;
	}
		
	/**
	 *
	 */
	public void setValue(Object value)
	{
		this.value = value;
	}

	/**
	 *
	 */
	public boolean isInitialized()
	{
		return this.isInitialized;
	}
		
	/**
	 *
	 */
	public void setInitialized(boolean isInitialized)
	{
		this.isInitialized = isInitialized;
	}

		
	/**
	 *
	 */
	public JRIncrementer getIncrementer()
	{
		if (incrementer == null)
		{
			Class incrementerFactoryClass = getIncrementerFactoryClass();
			
			JRIncrementerFactory incrementerFactory;
			if (incrementerFactoryClass == null)
			{
				incrementerFactory = JRDefaultIncrementerFactory.getFactory(getValueClass());
			}
			else
			{
				incrementerFactory = JRIncrementerFactoryCache.getInstance(incrementerFactoryClass); 
			}
			
			incrementer = incrementerFactory.getIncrementer(getCalculation());
		}
		
		return incrementer;
	}

	
	/**
	 * Sets a helper variable.
	 * 
	 * @param helperVariable the helper variable
	 * @param type the helper type
	 * @return the previous helper variable for the type
	 */
	public JRFillVariable setHelperVariable(JRFillVariable helperVariable, byte type)
	{
		JRFillVariable old = helperVariables[type]; 
		helperVariables[type] = helperVariable;
		return old;
	}
	
	
	/**
	 * Returns a helper variable.
	 * 
	 * @param type the helper type
	 * @return the helper variable for the specified type
	 */
	public JRFillVariable getHelperVariable(byte type)
	{
		return helperVariables[type];
	}
}
