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

import java.awt.Color;
import java.io.Serializable;

import net.sf.jasperreports.engine.JRAbstractObjectFactory;
import net.sf.jasperreports.engine.JRStyle;

/**
 * @author Ionut Nedelcu (ionutned@users.sourceforge.net)
 * @version $Id
 */
public class JRBaseStyle implements JRStyle, Serializable
{

	/**
	 *
	 */
	private static final long serialVersionUID = 10001;


	/**
	 *
	 */
	protected String name;
	protected JRStyle parentStyle;
	protected boolean isDefault = false;

	protected Byte positionType;
	protected Byte stretchType;
	protected Byte mode;
	protected Color forecolor;
	protected Color backcolor;

	protected Byte pen;
	protected Byte fill;

	protected Integer radius;

	protected Byte scaleImage;
	protected Byte horizontalAlignment;
	protected Byte verticalAlignment;

	protected Byte border;
	protected Byte topBorder = null;
	protected Byte leftBorder = null;
	protected Byte bottomBorder = null;
	protected Byte rightBorder = null;
	protected Color borderColor = null;
	protected Color topBorderColor = null;
	protected Color leftBorderColor = null;
	protected Color bottomBorderColor = null;
	protected Color rightBorderColor = null;
	protected Integer padding;
	protected Integer topPadding = null;
	protected Integer leftPadding = null;
	protected Integer bottomPadding = null;
	protected Integer rightPadding = null;

	protected String fontName = null;
	protected Boolean isBold = null;
	protected Boolean isItalic = null;
	protected Boolean isUnderline = null;
	protected Boolean isStrikeThrough = null;
	protected Integer size = null;
	protected String pdfFontName = null;
	protected String pdfEncoding = null;
	protected Boolean isPdfEmbedded = null;

	protected Byte rotation;
	protected Byte lineSpacing;
	protected Boolean isStyledText;

	protected String pattern = null;

	/**
	 *
	 */
	public JRBaseStyle()
	{
	}


	/**
	 *
	 */
	public JRBaseStyle(JRStyle style, JRAbstractObjectFactory factory)
	{
		this.name= style.getName();
		this.parentStyle = factory.getStyle(style.getParentStyle());
		isDefault = style.isDefault();

		this.positionType = style.getOwnPositionType();
		this.stretchType = style.getOwnStretchType();
		this.mode = style.getOwnMode();
		this.forecolor = style.getOwnForecolor();
		this.backcolor = style.getOwnBackcolor();

		this.pen = style.getOwnPen();
		this.fill = style.getOwnFill();

		this.radius = style.getOwnRadius();

		this.scaleImage = style.getOwnScaleImage();
		this.horizontalAlignment = style.getOwnHorizontalAlignment();
		this.verticalAlignment = style.getOwnVerticalAlignment();

		this.border = style.getOwnBorder();
		this.topBorder = style.getOwnTopBorder();
		this.leftBorder = style.getOwnLeftBorder();
		this.bottomBorder = style.getOwnBottomBorder();
		this.rightBorder = style.getOwnRightBorder();
		this.borderColor = style.getOwnBorderColor();
		this.topBorderColor = style.getOwnTopBorderColor();
		this.leftBorderColor = style.getOwnLeftBorderColor();
		this.bottomBorderColor = style.getOwnBottomBorderColor();
		this.rightBorderColor = style.getOwnRightBorderColor();
		this.padding = style.getOwnPadding();
		this.topPadding = style.getOwnTopPadding();
		this.leftPadding = style.getOwnLeftPadding();
		this.bottomPadding = style.getOwnBottomPadding();
		this.rightPadding = style.getOwnRightPadding();

		this.rotation = style.getOwnRotation();
		this.lineSpacing = style.getOwnLineSpacing();
		this.isStyledText = style.isOwnStyledText();

		this.pattern = style.getOwnPattern();
	}


	/**
	 *
	 */
	public JRStyle getParentStyle()
	{
		return parentStyle;
	}

	public String getName()
	{
		return name;
	}

	/**
	 *
	 */
	public boolean isDefault()
	{
		return isDefault;
	}

	/**
	 *
	 */
	public Color getForecolor()
	{
		if (forecolor == null && parentStyle != null)
			return parentStyle.getForecolor();
		return forecolor;
	}


	/**
	 *
	 */
	public Color getOwnForecolor()
	{
		return forecolor;
	}

	public Color getBackcolor()
	{
		if (backcolor == null && parentStyle != null)
			return parentStyle.getBackcolor();
		return backcolor;
	}

	public Color getOwnBackcolor()
	{
		return backcolor;
	}

	public Byte getPen()
	{
		if (pen == null && parentStyle != null)
			return parentStyle.getPen();
		return pen;
	}

	public Byte getOwnPen()
	{
		return pen;
	}

	public Byte getFill()
	{
		if (fill == null && parentStyle != null)
			return parentStyle.getFill();
		return fill;
	}

	public Byte getOwnFill()
	{
		return fill;
	}

	public Integer getRadius()
	{
		if (radius == null && parentStyle != null)
			return parentStyle.getRadius();
		return radius;
	}

	public Integer getOwnRadius()
	{
		return radius;
	}

	public Byte getScaleImage()
	{
		if (scaleImage == null && parentStyle != null)
			return parentStyle.getScaleImage();
		return scaleImage;
	}

	public Byte getOwnScaleImage()
	{
		return scaleImage;
	}

	public Byte getHorizontalAlignment()
	{
		if (horizontalAlignment == null && parentStyle != null)
			return parentStyle.getHorizontalAlignment();
		return horizontalAlignment;
	}

	public Byte getOwnHorizontalAlignment()
	{
		return horizontalAlignment;
	}

	public Byte getVerticalAlignment()
	{
		if (verticalAlignment == null && parentStyle != null)
			return parentStyle.getVerticalAlignment();
		return verticalAlignment;
	}

	public Byte getOwnVerticalAlignment()
	{
		return verticalAlignment;
	}

	public Byte getBorder()
	{
		if (border == null && parentStyle != null)
			return parentStyle.getBorder();
		return border;
	}

	public Byte getOwnBorder()
	{
		return border;
	}

	public Color getBorderColor()
	{
		if (borderColor == null && parentStyle != null)
			return parentStyle.getBorderColor();
		return borderColor;
	}

	public Color getOwnBorderColor()
	{
		return borderColor;
	}

	public Integer getPadding()
	{
		if (padding == null && parentStyle != null)
			return parentStyle.getPadding();
		return padding;
	}

	public Integer getOwnPadding()
	{
		return padding;
	}

	public Byte getTopBorder()
	{
		if (topBorder == null) {
			if (border != null)
				return border;
			if (parentStyle != null)
				return parentStyle.getTopBorder();
		}
		return topBorder;
	}

	public Byte getOwnTopBorder()
	{
		return topBorder;
	}

	public Color getTopBorderColor()
	{
		if (topBorderColor == null) {
			if (borderColor != null)
				return borderColor;
			if (parentStyle != null)
				return parentStyle.getTopBorderColor();
		}
		return topBorderColor;
	}

	public Color getOwnTopBorderColor()
	{
		return topBorderColor;
	}

	public Integer getTopPadding()
	{
		if (topPadding == null) {
			if (padding != null)
				return padding;
			if (parentStyle != null)
				return parentStyle.getTopPadding();
		}
		return topPadding;
	}

	public Integer getOwnTopPadding()
	{
		return topPadding;
	}

	public Byte getLeftBorder()
	{
		if (leftBorder == null) {
			if (border != null)
				return border;
			if (parentStyle != null)
				return parentStyle.getLeftBorder();
		}
		return leftBorder;
	}

	public Byte getOwnLeftBorder()
	{
		return leftBorder;
	}

	public Color getLeftBorderColor()
	{
		if (leftBorderColor == null) {
			if (borderColor != null)
				return borderColor;
			if (parentStyle != null)
				return parentStyle.getLeftBorderColor();
		}
		return leftBorderColor;
	}

	public Color getOwnLeftBorderColor()
	{
		return leftBorderColor;
	}

	public Integer getLeftPadding()
	{
		if (leftPadding == null) {
			if (padding != null)
				return padding;
			if (parentStyle != null)
				return parentStyle.getLeftPadding();
		}
		return leftPadding;
	}

	public Integer getOwnLeftPadding()
	{
		return leftPadding;
	}

	public Byte getBottomBorder()
	{
		if (bottomBorder == null) {
			if (border != null)
				return border;
			if (parentStyle != null)
				return parentStyle.getBottomBorder();
		}
		return bottomBorder;
	}

	public Byte getOwnBottomBorder()
	{
		return bottomBorder;
	}

	public Color getBottomBorderColor()
	{
		if (bottomBorderColor == null) {
			if (borderColor != null)
				return borderColor;
			if (parentStyle != null)
				return parentStyle.getBottomBorderColor();
		}
		return bottomBorderColor;
	}

	public Color getOwnBottomBorderColor()
	{
		return bottomBorderColor;
	}

	public Integer getBottomPadding()
	{
		if (bottomPadding == null) {
			if (padding != null)
				return padding;
			if (parentStyle != null)
				return parentStyle.getBottomPadding();
		}
		return bottomPadding;
	}

	public Integer getOwnBottomPadding()
	{
		return bottomPadding;
	}

	public Byte getRightBorder()
	{
		if (rightBorder == null) {
			if (border != null)
				return border;
			if (parentStyle != null)
				return parentStyle.getRightBorder();
		}
		return rightBorder;
	}

	public Byte getOwnRightBorder()
	{
		return rightBorder;
	}

	public Color getRightBorderColor()
	{
		if (rightBorderColor == null) {
			if (borderColor != null)
				return borderColor;
			if (parentStyle != null)
				return parentStyle.getRightBorderColor();
		}
		return rightBorderColor;
	}

	public Color getOwnRightBorderColor()
	{
		return rightBorderColor;
	}

	public Integer getRightPadding()
	{
		if (rightPadding == null) {
			if (padding != null)
				return padding;
			if (parentStyle != null)
				return parentStyle.getRightPadding();
		}
		return rightPadding;
	}

	public Integer getOwnRightPadding()
	{
		return rightPadding;
	}

	public Byte getRotation()
	{
		if (rotation == null && parentStyle != null)
			return parentStyle.getRotation();
		return rotation;
	}

	public Byte getOwnRotation()
	{
		return rotation;
	}

	public Byte getLineSpacing()
	{
		if (lineSpacing == null && parentStyle != null)
			return parentStyle.getLineSpacing();
		return lineSpacing;
	}

	public Byte getOwnLineSpacing()
	{
		return lineSpacing;
	}

	public Boolean isStyledText()
	{
		if (isStyledText == null && parentStyle != null)
			return parentStyle.isStyledText();
		return isStyledText;
	}

	public Boolean isOwnStyledText()
	{
		return isStyledText;
	}


	public String getFontName()
	{
		if (fontName == null && parentStyle != null)
			return parentStyle.getFontName();
		return fontName;
	}

	public String getOwnFontName()
	{
		return fontName;
	}

	public Boolean isBold()
	{
		if (isBold == null && parentStyle != null)
			return parentStyle.isBold();
		return isBold;
	}

	public Boolean isOwnBold()
	{
		return isBold;
	}

	public Boolean isItalic()
	{
		if (isItalic == null && parentStyle != null)
			return parentStyle.isItalic();
		return isItalic;
	}

	public Boolean isOwnItalic()
	{
		return isItalic;
	}

	public Boolean isUnderline()
	{
		if (isUnderline == null && parentStyle != null)
				return parentStyle.isUnderline();
		return isUnderline;
	}

	public Boolean isOwnUnderline()
	{
		return isUnderline;
	}

	public Boolean isStrikeThrough()
	{
		if (isStrikeThrough == null && parentStyle != null)
			return parentStyle.isStrikeThrough();
		return isStrikeThrough;
	}

	public Boolean isOwnStrikeThrough()
	{
		return isStrikeThrough;
	}

	public Integer getSize()
	{
		if (size == null && parentStyle != null)
			return parentStyle.getSize();
		return size;
	}

	public Integer getOwnSize()
	{
		return size;
	}

	public String getPdfFontName()
	{
		if (pdfFontName == null && parentStyle != null)
			return parentStyle.getPdfFontName();
		return pdfFontName;
	}

	public String getOwnPdfFontName()
	{
		return pdfFontName;
	}

	public String getPdfEncoding()
	{
		if (pdfEncoding == null && parentStyle != null)
				return parentStyle.getPdfEncoding();
		return pdfEncoding;
	}

	public String getOwnPdfEncoding()
	{
		return pdfEncoding;
	}

	public Boolean isPdfEmbedded()
	{
		if (isPdfEmbedded == null && parentStyle != null)
				return parentStyle.isPdfEmbedded();
		return isPdfEmbedded;
	}

	public Boolean isOwnPdfEmbedded()
	{
		return isPdfEmbedded;
	}

	public String getPattern()
	{
		if (pattern == null && parentStyle != null)
			return parentStyle.getPattern();
		return pattern;
	}

	public String getOwnPattern()
	{
		return pattern;
	}

	public Byte getPositionType()
	{
		if (positionType == null && parentStyle != null)
			return parentStyle.getPositionType();
		return positionType;
	}

	public Byte getOwnPositionType()
	{
		return positionType;
	}

	public Byte getStretchType()
	{
		if (stretchType == null && parentStyle != null)
			return parentStyle.getStretchType();
		return stretchType;
	}

	public Byte getOwnStretchType()
	{
		return stretchType;
	}

	public Byte getMode()
	{
		if (mode == null && parentStyle != null)
			return parentStyle.getMode();
		return mode;
	}

	public Byte getOwnMode()
	{
		return mode;
	}

	/**
	 *
	 */
	public void setPositionType(byte positionType)
	{
		setPositionType(new Byte(positionType));
	}
	/**
	 *
	 */
	public void setPositionType(Byte positionType)
	{
		this.positionType = positionType;
	}

	/**
	 *
	 */
	public void setStretchType(byte stretchType)
	{
		setStretchType(new Byte(stretchType));
	}
	/**
	 *
	 */
	public void setStretchType(Byte stretchType)
	{
		this.stretchType = stretchType;
	}

	/**
	 *
	 */
	public void setForecolor(Color forecolor)
	{
		this.forecolor = forecolor;
	}

	/**
	 *
	 */
	public void setBackcolor(Color backcolor)
	{
		this.backcolor = backcolor;
	}

	/**
	 *
	 */
	public void setMode(byte mode)
	{
		setMode(new Byte(mode));
	}

	/**
	 *
	 */
	public void setMode(Byte mode)
	{
		this.mode = mode;
	}

	/**
	 *
	 */
	public void setPen(byte pen)
	{
		setPen(new Byte(pen));
	}

	/**
	 *
	 */
	public void setPen(Byte pen)
	{
		this.pen = pen;
	}

	/**
	 *
	 */
	public void setFill(byte fill)
	{
		setFill(new Byte(fill));
	}

	/**
	 *
	 */
	public void setFill(Byte fill)
	{
		this.fill = fill;
	}

	/**
	 *
	 */
	public void setRadius(int radius)
	{
		setRadius(new Integer(radius));
	}

	/**
	 *
	 */
	public void setRadius(Integer radius)
	{
		this.radius = radius;
	}

	/**
	 *
	 */
	public void setScaleImage(byte scaleImage)
	{
		setScaleImage(new Byte(scaleImage));
	}

	/**
	 *
	 */
	public void setScaleImage(Byte scaleImage)
	{
		this.scaleImage = scaleImage;
	}

	/**
	 *
	 */
	public void setHorizontalAlignment(byte horizontalAlignment)
	{
		setHorizontalAlignment(new Byte(horizontalAlignment));
	}

	/**
	 *
	 */
	public void setHorizontalAlignment(Byte horizontalAlignment)
	{
		this.horizontalAlignment = horizontalAlignment;
	}

	/**
	 *
	 */
	public void setVerticalAlignment(byte verticalAlignment)
	{
		setVerticalAlignment(new Byte(verticalAlignment));
	}

	/**
	 *
	 */
	public void setVerticalAlignment(Byte verticalAlignment)
	{
		this.verticalAlignment = verticalAlignment;
	}

	/**
	 *
	 */
	public void setBorder(byte border)
	{
		setBorder(new Byte(border));
	}

	/**
	 *
	 */
	public void setBorder(Byte border)
	{
		this.border = border;
	}

	/**
	 *
	 */
	public void setBorderColor(Color borderColor)
	{
		this.borderColor = borderColor;
	}

	/**
	 *
	 */
	public void setPadding(int padding)
	{
		setPadding(new Integer(padding));
	}

	/**
	 *
	 */
	public void setPadding(Integer padding)
	{
		this.padding = padding;
	}

	/**
	 *
	 */
	public void setTopBorder(byte topBorder)
	{
		setTopBorder(new Byte(topBorder));
	}

	/**
	 *
	 */
	public void setTopBorder(Byte topBorder)
	{
		this.topBorder = topBorder;
	}

	/**
	 *
	 */
	public void setTopBorderColor(Color topBorderColor)
	{
		this.topBorderColor = topBorderColor;
	}

	/**
	 *
	 */
	public void setTopPadding(int topPadding)
	{
		setTopPadding(new Integer(topPadding));
	}

	/**
	 *
	 */
	public void setTopPadding(Integer topPadding)
	{
		this.topPadding = topPadding;
	}

	/**
	 *
	 */
	public void setLeftBorder(byte leftBorder)
	{
		setLeftBorder(new Byte(leftBorder));
	}

	/**
	 *
	 */
	public void setLeftBorder(Byte leftBorder)
	{
		this.leftBorder = leftBorder;
	}

	/**
	 *
	 */
	public void setLeftBorderColor(Color leftBorderColor)
	{
		this.leftBorderColor = leftBorderColor;
	}

	/**
	 *
	 */
	public void setLeftPadding(int leftPadding)
	{
		setLeftPadding(new Integer(leftPadding));
	}

	/**
	 *
	 */
	public void setLeftPadding(Integer leftPadding)
	{
		this.leftPadding = leftPadding;
	}

	/**
	 *
	 */
	public void setBottomBorder(byte bottomBorder)
	{
		setBottomBorder(new Byte(bottomBorder));
	}

	/**
	 *
	 */
	public void setBottomBorder(Byte bottomBorder)
	{
		this.bottomBorder = bottomBorder;
	}

	/**
	 *
	 */
	public void setBottomBorderColor(Color bottomBorderColor)
	{
		this.bottomBorderColor = bottomBorderColor;
	}

	/**
	 *
	 */
	public void setBottomPadding(int bottomPadding)
	{
		setBottomPadding(new Integer(bottomPadding));
	}

	/**
	 *
	 */
	public void setBottomPadding(Integer bottomPadding)
	{
		this.bottomPadding = bottomPadding;
	}

	/**
	 *
	 */
	public void setRightBorder(byte rightBorder)
	{
		setRightBorder(new Byte(rightBorder));
	}

	/**
	 *
	 */
	public void setRightBorder(Byte rightBorder)
	{
		this.rightBorder = rightBorder;
	}

	/**
	 *
	 */
	public void setRightBorderColor(Color rightBorderColor)
	{
		this.rightBorderColor = rightBorderColor;
	}

	/**
	 *
	 */
	public void setRightPadding(int rightPadding)
	{
		setRightPadding(new Integer(rightPadding));
	}

	/**
	 *
	 */
	public void setRightPadding(Integer rightPadding)
	{
		this.rightPadding = rightPadding;
	}

	/**
	 *
	 */
	public void setRotation(byte rotation)
	{
		setRotation(new Byte(rotation));
	}

	/**
	 *
	 */
	public void setRotation(Byte rotation)
	{
		this.rotation = rotation;
	}

	/**
	 *
	 */
	public void setFontName(String fontName)
	{
		this.fontName = fontName;
	}

	/**
	 *
	 */
	public void setBold(boolean bold)
	{
		setBold(Boolean.valueOf(bold));
	}

	/**
	 *
	 */
	public void setBold(Boolean bold)
	{
		isBold = bold;
	}

	/**
	 *
	 */
	public void setItalic(boolean italic)
	{
		setItalic(Boolean.valueOf(italic));
	}

	/**
	 *
	 */
	public void setItalic(Boolean italic)
	{
		isItalic = italic;
	}

	/**
	 *
	 */
	public void setPdfEmbedded(boolean pdfEmbedded)
	{
		setPdfEmbedded(Boolean.valueOf(pdfEmbedded));
	}

	/**
	 *
	 */
	public void setPdfEmbedded(Boolean pdfEmbedded)
	{
		isPdfEmbedded = pdfEmbedded;
	}

	/**
	 *
	 */
	public void setStrikeThrough(boolean strikeThrough)
	{
		setStrikeThrough(Boolean.valueOf(strikeThrough));
	}

	/**
	 *
	 */
	public void setStrikeThrough(Boolean strikeThrough)
	{
		isStrikeThrough = strikeThrough;
	}

	/**
	 *
	 */
	public void setStyledText(boolean styledText)
	{
		setStyledText(Boolean.valueOf(styledText));
	}

	/**
	 *
	 */
	public void setStyledText(Boolean styledText)
	{
		isStyledText = styledText;
	}

	/**
	 *
	 */
	public void setUnderline(boolean underline)
	{
		setUnderline(Boolean.valueOf(underline));
	}

	/**
	 *
	 */
	public void setUnderline(Boolean underline)
	{
		isUnderline = underline;
	}

	/**
	 *
	 */
	public void setLineSpacing(byte lineSpacing)
	{
		setLineSpacing(new Byte(lineSpacing));
	}

	/**
	 *
	 */
	public void setLineSpacing(Byte lineSpacing)
	{
		this.lineSpacing = lineSpacing;
	}

	/**
	 *
	 */
	public void setPattern(String pattern)
	{
		this.pattern = pattern;
	}

	/**
	 *
	 */
	public void setPdfEncoding(String pdfEncoding)
	{
		this.pdfEncoding = pdfEncoding;
	}

	/**
	 *
	 */
	public void setPdfFontName(String pdfFontName)
	{
		this.pdfFontName = pdfFontName;
	}

	/**
	 *
	 */
	public void setSize(int size)
	{
		setSize(new Integer(size));
	}

	/**
	 *
	 */
	public void setSize(Integer size)
	{
		this.size = size;
	}
}
