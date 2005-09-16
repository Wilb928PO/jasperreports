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
import java.util.Map;

import net.sf.jasperreports.engine.JRBox;
import net.sf.jasperreports.engine.JRElement;
import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.JRGraphicElement;
import net.sf.jasperreports.engine.JRPrintElement;
import net.sf.jasperreports.engine.base.JRBasePrintImage;
import net.sf.jasperreports.engine.crosstab.JRCellContents;
import net.sf.jasperreports.engine.fill.crosstab.JRPrintCell;

import org.apache.commons.collections.ReferenceMap;

/**
 * @author Lucian Chirita (lucianc@users.sourceforge.net)
 * @version $Id$
 */
public class JRFillCellContents extends JRFillElementContainer implements JRCellContents
{
	protected static Map transformedContents = new ReferenceMap();
	
	private JRCellContents parentCell;
	
	private int height;
	private int width;

	public JRFillCellContents(JRCellContents cell, JRFillObjectFactory factory)
	{
		super(cell, factory);
		
		parentCell = cell;
		
		initElements();
	}

	public Color getBackcolor()
	{
		return parentCell.getBackcolor();
	}

	public JRBox getBox()
	{
		return parentCell.getBox();
	}

	
	public int getHeight()
	{
		return height;
	}
	
	
	public int getWidth()
	{
		return width;
	}
	
	
	public void setHeight(int height)
	{
		this.height = height;
	}
	
	
	public void setWidth(int width)
	{
		this.width = width;
	}
	
	
	public static JRFillCellContents getTransformedContents(
			JRBaseFiller filler,
			JRFillCrosstab crosstab,
			JRFillCellContents contents, 
			int newWidth, int newHeight,
			byte xPosition, byte yPosition)
	{
		if ((contents.getHeight() == newHeight) && 
				(contents.getWidth() == newWidth))
		{
			return contents;
		}
		
		Object key = new StretchedContents(contents, newHeight, newWidth, xPosition, yPosition);
		
		JRFillCellContents transformedCell = (JRFillCellContents) transformedContents.get(key);
		if (transformedCell == null)
		{
			JRFillObjectFactory factory = new JRFillObjectFactory(filler, crosstab.getExpressionEvaluator());
			transformedCell = factory.getCell(contents.parentCell);
			filler.setTextFieldsFormats();
			
			transformedCell.setWidth(contents.getWidth());
			transformedCell.setHeight(contents.getHeight());
			transformedCell.transform(newWidth, newHeight, xPosition, yPosition);
			
			transformedCell.setElementsBandBottomY();
			
			transformedContents.put(key, transformedCell);
		}
		
		return transformedCell;
	}
	
	
	private void transform(int newWidth, int newHeight, byte xPosition, byte yPosition)
	{
		transformElements(newWidth, newHeight, xPosition, yPosition);
		
		width = newWidth;
		height = newHeight;
	}

	private void transformElements(int newWidth, int newHeight, byte xPosition, byte yPosition)
	{
		if ((height == newHeight || yPosition == JRCellContents.POSITION_Y_TOP) && 
				(width == newWidth || xPosition == JRCellContents.POSITION_X_LEFT))
		{
			return;
		}

		double scaleX =  -1d;
		int offsetX = 0;
		switch (xPosition)
		{
			case JRCellContents.POSITION_X_CENTER:
				offsetX = (newWidth - width) / 2;
				break;
			case JRCellContents.POSITION_X_RIGHT:
				offsetX = newWidth - width;
				break;
			case JRCellContents.POSITION_X_STRETCH:
				scaleX = ((double) newWidth) / width;
				break;
		}
		
		double scaleY =  -1d;
		int offsetY = 0;
		switch (yPosition)
		{
			case JRCellContents.POSITION_Y_MIDDLE:
				offsetY = (newHeight - height) / 2;
				break;
			case JRCellContents.POSITION_Y_BOTTOM:
				offsetY = newHeight - height;
				break;
			case JRCellContents.POSITION_X_STRETCH:
				scaleY = ((double) newHeight) / height;
				break;
		}
		
		JRElement[] allElements = getElements();
		if (allElements != null)
		{
			for (int i = 0; i < allElements.length; i++)
			{
				JRFillElement element = (JRFillElement) allElements[i];
				
				if (scaleX != -1d)
				{
					element.setX((int) (element.getX() * scaleX));
					element.setWidth((int) (element.getWidth() * scaleX));
				}
				
				if (offsetX != 0)
				{
					element.setX(element.getX() + offsetX);
				}				
				
				if (scaleY != -1d)
				{
					element.setY((int) (element.getY() * scaleY));
					element.setHeight((int) (element.getHeight() * scaleY));
				}
				
				if (offsetY != 0)
				{
					element.setY(element.getY() + offsetY);
				}
			}
		}
	}

	
	protected JRPrintCell fill(int availableStretchHeight) throws JRException
	{
		initFill();
		
		this.resetElements();

		this.prepareElements(availableStretchHeight, false);

		this.stretchElements();

		this.moveBandBottomElements();

		this.removeBlankElements();

		JRPrintCell printCell = new JRPrintCell();
		printCell.setWidth(width);
		printCell.setHeight(height);
		
		printCell.addElement(getCellElement());
		
		fillElements(printCell);
		
		return printCell;
	}


	private JRPrintElement getCellElement()
	{
		JRBasePrintImage image = new JRBasePrintImage();
		image.setX(0);
		image.setY(0);
		image.setWidth(width);
		image.setHeight(height);
		image.setMode(JRElement.MODE_OPAQUE);
		Color backcolor = getBackcolor();
		image.setBackcolor(backcolor == null ? Color.white : backcolor);
		image.setFill(JRGraphicElement.FILL_SOLID);
		image.setBox(getBox());
		return image;
	}

	protected void addPrintElement(JRFillElement element, JRPrintElement printElement, JRPrintElementContainer printContainer)
	{
	}

	
	protected static class StretchedContents
	{
		final JRFillCellContents contents;
		final int newHeight;
		final int newWidth;
		final int hashCode;
		final byte xPosition;
		final byte yPosition;
		
		StretchedContents(JRFillCellContents contents,
				int newHeight, int newWidth, byte xPosition, byte yPosition)
		{
			this.contents = contents;
			this.newHeight = newHeight;
			this.newWidth = newWidth;
			this.xPosition = xPosition;
			this.yPosition = yPosition;
			
			int hash = contents.hashCode();
			hash = 31*hash + newHeight;
			hash = 31*hash + newWidth;
			hash = 31*hash + xPosition;
			hash = 31*hash + yPosition;
			hashCode = hash;
		}
		
		public boolean equals(Object o)
		{
			if (!(o instanceof StretchedContents))
			{
				return false;				
			}
			
			StretchedContents s = (StretchedContents) o;
			
			return s.contents.equals(contents) && 
				s.newHeight == newHeight && s.newWidth == newWidth &&
				s.xPosition == xPosition && s.yPosition == yPosition;
		}
		
		public int hashCode()
		{
			return hashCode;
		}
	}
}
