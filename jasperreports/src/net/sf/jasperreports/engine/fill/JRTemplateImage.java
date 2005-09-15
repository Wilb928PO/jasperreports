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

import net.sf.jasperreports.engine.JRAlignment;
import net.sf.jasperreports.engine.JRBox;
import net.sf.jasperreports.engine.JRChart;
import net.sf.jasperreports.engine.JRDefaultStyleProvider;
import net.sf.jasperreports.engine.JRGraphicElement;
import net.sf.jasperreports.engine.JRHyperlink;
import net.sf.jasperreports.engine.JRImage;


/**
 * @author Teodor Danciu (teodord@users.sourceforge.net)
 * @version $Id$
 */
public class JRTemplateImage extends JRTemplateGraphicElement
{


	/**
	 *
	 */
	private static final long serialVersionUID = 10001;

	/**
	 *
	 */
	private byte scaleImage = JRImage.SCALE_IMAGE_CLIP;
	private byte horizontalAlignment = JRAlignment.HORIZONTAL_ALIGN_LEFT;
	private byte verticalAlignment = JRAlignment.VERTICAL_ALIGN_TOP;
	protected boolean isLazy = false;
	private byte onErrorType = JRImage.ON_ERROR_TYPE_ERROR;
	private byte hyperlinkType = JRHyperlink.HYPERLINK_TYPE_NONE;
	private byte hyperlinkTarget = JRHyperlink.HYPERLINK_TARGET_SELF;
	private JRBox box = null;


	/**
	 *
	 */
	protected JRTemplateImage(JRDefaultStyleProvider defaultStyleProvider, JRImage image)
	{
		super(defaultStyleProvider);
		
		setImage(image);
	}


	/**
	 *
	 */
	protected JRTemplateImage(JRDefaultStyleProvider defaultStyleProvider, JRChart chart)
	{
		super(defaultStyleProvider);
		
		setChart(chart);
	}


	/**
	 *
	 */
	protected void setImage(JRImage image)
	{
		super.setGraphicElement(image);
		
		setScaleImage(image.getScaleImage());
		setHorizontalAlignment(image.getHorizontalAlignment());
		setVerticalAlignment(image.getVerticalAlignment());
		setLazy(image.isLazy());
		setOnErrorType(image.getOnErrorType());
		setHyperlinkType(image.getHyperlinkType());
		setHyperlinkTarget(image.getHyperlinkTarget());

//		box = image.getBox();
	}

	/**
	 *
	 */
	protected void setChart(JRChart chart)
	{
		super.setElement(chart);
		
		setPen(JRGraphicElement.PEN_NONE);
		setFill(JRGraphicElement.FILL_SOLID);

		setHyperlinkType(chart.getHyperlinkType());
		setHyperlinkTarget(chart.getHyperlinkTarget());
		
		//box = chart.getBox();
	}

	/**
	 *
	 */
	public byte getScaleImage()
	{
		return this.scaleImage;
	}
		
	/**
	 *
	 */
	protected void setScaleImage(byte scaleImage)
	{
		this.scaleImage = scaleImage;
	}

	/**
	 *
	 */
	public byte getHorizontalAlignment()
	{
		return this.horizontalAlignment;
	}
		
	/**
	 *
	 */
	protected void setHorizontalAlignment(byte horizontalAlignment)
	{
		this.horizontalAlignment = horizontalAlignment;
	}
		
	/**
	 *
	 */
	public byte getVerticalAlignment()
	{
		return this.verticalAlignment;
	}
		
	/**
	 *
	 */
	protected void setVerticalAlignment(byte verticalAlignment)
	{
		this.verticalAlignment = verticalAlignment;
	}
		
	/**
	 *
	 */
	public boolean isLazy()
	{
		return this.isLazy;
	}

	/**
	 *
	 */
	public void setLazy(boolean isLazy)
	{
		this.isLazy = isLazy;
	}

	/**
	 *
	 */
	public byte getOnErrorType()
	{
		return this.onErrorType;
	}

	/**
	 *
	 */
	public void setOnErrorType(byte onErrorType)
	{
		this.onErrorType = onErrorType;
	}

	/**
	 *
	 */
	public JRBox getBox()
	{
		return box;
	}
		
	/**
	 *
	 */
	public byte getHyperlinkType()
	{
		return this.hyperlinkType;
	}
		
	/**
	 *
	 */
	protected void setHyperlinkType(byte hyperlinkType)
	{
		this.hyperlinkType = hyperlinkType;
	}
		
	/**
	 *
	 */
	public byte getHyperlinkTarget()
	{
		return this.hyperlinkTarget;
	}
		
	/**
	 *
	 */
	protected void setHyperlinkTarget(byte hyperlinkTarget)
	{
		this.hyperlinkTarget = hyperlinkTarget;
	}
	

}
