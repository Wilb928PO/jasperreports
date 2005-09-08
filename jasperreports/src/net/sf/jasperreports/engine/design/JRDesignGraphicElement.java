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
package net.sf.jasperreports.engine.design;

import net.sf.jasperreports.engine.JRDefaultStyleProvider;
import net.sf.jasperreports.engine.JRGraphicElement;


/**
 * This class contains functionality common to graphic elements at design time. It provides implementation for the methods described
 * in <tt>JRTextElement</tt>.
 * 
 * @author Teodor Danciu (teodord@users.sourceforge.net)
 * @version $Id$
 */
public abstract class JRDesignGraphicElement extends JRDesignElement implements JRGraphicElement
{


	/**
	 *
	 */
	private static final long serialVersionUID = 10001;

	/**
	 *
	 */
	protected Byte pen;
	protected Byte fill;


	/**
	 *
	 */
	protected JRDesignGraphicElement(JRDefaultStyleProvider defaultStyleProvider)
	{
		super(defaultStyleProvider);
	}
		

	/**
	 *
	 */
	public byte getMode()
	{
		if (mode == null) {
			if (style != null && style.getMode() != null)
				return style.getMode().byteValue();
			return MODE_OPAQUE;
		}
		return mode.byteValue();
	}

	/**
	 *
	 */
	public byte getPen()
	{
		if (pen == null) {
			if (style != null && style.getPen() != null)
				return style.getPen().byteValue();
			return PEN_1_POINT;
		}
		return pen.byteValue();
	}

	public Byte getOwnPen()
	{
		return this.pen;
	}

	/**
	 *
	 */
	public void setPen(byte pen)
	{
		this.pen = new Byte(pen);
	}

	/**
	 *
	 */
	public byte getFill()
	{
		if (fill == null) {
			if (style != null && style.getFill() != null)
				return style.getFill().byteValue();
			return FILL_SOLID;
		}
		return fill.byteValue();
	}

	public Byte getOwnFill()
	{
		return this.fill;
	}

	/**
	 *
	 */
	public void setFill(byte fill)
	{
		this.fill = new Byte(fill);
	}
}
