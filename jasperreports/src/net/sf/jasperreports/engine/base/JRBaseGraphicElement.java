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

import net.sf.jasperreports.engine.JRGraphicElement;


/**
 * This class provides functionality common to graphic elements. It provides implementation for the methods described
 * in <tt>JRTextElement</tt>.
 * @author Teodor Danciu (teodord@users.sourceforge.net)
 * @version $Id$
 */
public abstract class JRBaseGraphicElement extends JRBaseElement implements JRGraphicElement
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
	 * Constructs an empty graphic element. By default graphic elements are opaque.
	 *
	protected JRBaseGraphicElement()
	{
		super();
	}
		

	/**
	 * Initializes properties that are specific to graphic elements. Common properties are initialized by its
	 * parent constructor.
	 * @param graphicElement an element whose properties are copied to this element. Usually it is a
	 * {@link net.sf.jasperreports.engine.design.JRDesignGraphicElement} that must be transformed into an
	 * <tt>JRBaseGraphicElement</tt> at compile time.
	 * @param factory a factory used in the compile process
	 */
	protected JRBaseGraphicElement(JRGraphicElement graphicElement, JRBaseObjectFactory factory)
	{
		super(graphicElement, factory);
		
		pen = graphicElement.getOwnPen();
		fill = graphicElement.getOwnFill();
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
