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

import java.awt.Color;
import java.io.Serializable;

import net.sf.jasperreports.engine.JRPrintElement;


/**
 * @author Teodor Danciu (teodord@users.sourceforge.net)
 * @version $Id$
 */
public class JRTemplatePrintElement implements JRPrintElement, Serializable
{


	/**
	 *
	 */
	private static final long serialVersionUID = 10000;

	/**
	 *
	 */
	protected JRTemplateElement template = null;

	private int x = 0;
	private int y = 0;
	private int height = 0;
	private int width = 0;


	/**
	 *
	 */
	protected JRTemplatePrintElement(JRTemplateElement element)
	{
		template = element;
	}

	/**
	 *
	 */
	public byte getMode()
	{
		return this.template.getMode();
	}
	
	/**
	 *
	 */
	public void setMode(byte mode)
	{
	}
	
	/**
	 *
	 */
	public int getX()
	{
		return this.x;
	}
	
	/**
	 *
	 */
	public void setX(int x)
	{
		this.x = x;
	}
	
	/**
	 *
	 */
	public int getY()
	{
		return this.y;
	}
	
	/**
	 *
	 */
	public void setY(int y)
	{
		this.y = y;
	}
	
	/**
	 *
	 */
	public int getWidth()
	{
		return this.width;
	}
	
	/**
	 *
	 */
	public void setWidth(int width)
	{
		this.width = width;
	}
	
	/**
	 *
	 */
	public int getHeight()
	{
		return this.height;
	}
	
	/**
	 *
	 */
	public void setHeight(int height)
	{
		this.height = height;
	}
	
	/**
	 *
	 */
	public Color getForecolor()
	{
		return this.template.getForecolor();
	}
	
	/**
	 *
	 */
	public void setForecolor(Color color)
	{
	}
	
	/**
	 *
	 */
	public Color getBackcolor()
	{
		return this.template.getBackcolor();
	}

	/**
	 *
	 */
	public void setBackcolor(Color color)
	{
	}
	

}
