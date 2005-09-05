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

import java.awt.Color;
import java.awt.font.TextAttribute;
import java.util.HashMap;
import java.util.Map;

import net.sf.jasperreports.engine.JRBox;
import net.sf.jasperreports.engine.JRFont;
import net.sf.jasperreports.engine.JRGraphicElement;
import net.sf.jasperreports.engine.JRReportFont;
import net.sf.jasperreports.engine.JRTextElement;
import net.sf.jasperreports.engine.util.JRTextAttribute;


/**
 * This class provides functionality common to design text elements. It provides implementation for the methods described
 * in <tt>JRTextElement</tt> and setters for text element attributes that can only be modified at design time.
 *
 * @author Teodor Danciu (teodord@users.sourceforge.net)
 * @version $Id$
 */
public abstract class JRDesignTextElement extends JRDesignElement implements JRTextElement
{


	/**
	 *
	 */
	private static final long serialVersionUID = 10001;

	/**
	 *
	 */
	protected Byte horizontalAlignment;
	protected Byte verticalAlignment;
	protected Byte rotation;
	protected Byte lineSpacing;
	protected Boolean isStyledText;

	/**
	 *
	 */
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

	protected JRReportFont reportFont = null;
	protected String fontName = null;
	protected Boolean isBold = null;
	protected Boolean isItalic = null;
	protected Boolean isUnderline = null;
	protected Boolean isStrikeThrough = null;
	protected Integer size = null;
	protected String pdfFontName = null;
	protected String pdfEncoding = null;
	protected Boolean isPdfEmbedded = null;

	protected boolean isCachingAttributes = false;
	protected transient Map attributes = null;

	/**
	 *
	 */
//	protected JRBox box = null;
//	protected JRFont font = null;


	/**
	 * Creates a new text element.
	 */
	protected JRDesignTextElement()
	{
		super();
	}


	/**
	 *
	 */
	public byte getMode()
	{
		if (mode == null) {
			if (style != null && style.getMode() != null)
				return style.getMode().byteValue();
			return MODE_TRANSPARENT;
		}
		return mode.byteValue();
	}


	/**
	 * @deprecated Replaced by {@link #getHorizontalAlignment()}.
	 */
	public byte getTextAlignment()
	{
		if (horizontalAlignment == null) {
			if (style != null && style.getHorizontalAlignment() != null)
				return style.getHorizontalAlignment().byteValue();
			return HORIZONTAL_ALIGN_LEFT;
		}
		return horizontalAlignment.byteValue();
	}

	/**
	 * @deprecated Replaced by {@link #setHorizontalAlignment(byte)}.
	 */
	public void setTextAlignment(byte horizontalAlignment)
	{
		this.horizontalAlignment = new Byte(horizontalAlignment);
	}

	/**
	 *
	 */
	public byte getHorizontalAlignment()
	{
		if (horizontalAlignment == null) {
			if (style != null && style.getHorizontalAlignment() != null)
				return style.getHorizontalAlignment().byteValue();
			return HORIZONTAL_ALIGN_LEFT;
		}
		return horizontalAlignment.byteValue();
	}

	public Byte getOwnHorizontalAlignment()
	{
		return horizontalAlignment;
	}

	/**
	 *
	 */
	public void setHorizontalAlignment(byte horizontalAlignment)
	{
		this.horizontalAlignment = new Byte(horizontalAlignment);
	}

	/**
	 *
	 */
	public byte getVerticalAlignment()
	{
		if (verticalAlignment == null) {
			if (style != null && style.getVerticalAlignment() != null)
				return style.getVerticalAlignment().byteValue();
			return VERTICAL_ALIGN_TOP;
		}
		return verticalAlignment.byteValue();
	}

	public Byte getOwnVerticalAlignment()
	{
		return verticalAlignment;
	}

	/**
	 *
	 */
	public void setVerticalAlignment(byte verticalAlignment)
	{
		this.verticalAlignment = new Byte(verticalAlignment);
	}

	/**
	 *
	 */
	public byte getRotation()
	{
		if (rotation == null) {
			if (style != null && style.getRotation() != null)
				return style.getRotation().byteValue();
			return ROTATION_NONE;
		}
		return rotation.byteValue();
	}

	public Byte getOwnRotation()
	{
		return rotation;
	}

	/**
	 *
	 */
	public void setRotation(byte rotation)
	{
		this.rotation = new Byte(rotation);
	}

	/**
	 *
	 */
	public byte getLineSpacing()
	{
		if (lineSpacing == null) {
			if (style != null && style.getLineSpacing() != null)
				return style.getLineSpacing().byteValue();
			return LINE_SPACING_SINGLE;
		}
		return lineSpacing.byteValue();
	}

	public Byte getOwnLineSpacing()
	{
		return lineSpacing;
	}

	/**
	 *
	 */
	public void setLineSpacing(byte lineSpacing)
	{
		this.lineSpacing = new Byte(lineSpacing);
	}

	/**
	 *
	 */
	public boolean isStyledText()
	{
		if (isStyledText == null) {
			if (style != null && style.isStyledText() != null)
				return style.isStyledText().booleanValue();
			return false;
		}
		return isStyledText.booleanValue();
	}

	public Boolean isOwnStyledText()
	{
		return isStyledText;
	}

	/**
	 *
	 */
	public void setStyledText(boolean isStyledText)
	{
		this.isStyledText = Boolean.valueOf(isStyledText);
	}

	/**
	 * @deprecated
	 */
	public JRBox getBox()
	{
		return this;
	}

	/**
	 * @deprecated
	 */
	public JRFont getFont()
	{
		return this;
	}


	/**
	 * @deprecated
	 */
	public void setBox(JRBox box)
	{
		border = box.getOwnBorder();
		topBorder = box.getOwnTopBorder();
		leftBorder = box.getOwnLeftBorder();
		bottomBorder = box.getOwnBottomBorder();
		rightBorder = box.getOwnRightBorder();
		borderColor = box.getOwnBorderColor();
		topBorderColor = box.getOwnTopBorderColor();
		leftBorderColor = box.getOwnLeftBorderColor();
		bottomBorderColor = box.getOwnBottomBorderColor();
		rightBorderColor = box.getOwnRightBorderColor();
		padding = box.getOwnPadding();
		topPadding = box.getOwnTopPadding();
		leftPadding = box.getOwnLeftPadding();
		bottomPadding = box.getOwnBottomPadding();
		rightPadding = box.getOwnRightPadding();
	}

	/**
	 * Sets a <tt>JRFont</tt> object containing all font properties for this text element
	 * @deprecated
	 */
	public void setFont(JRFont font)
	{
		fontName = font.getOwnFontName();
		isBold = font.isOwnBold();
		isItalic = font.isOwnItalic();
		isUnderline = font.isOwnUnderline();
		isStrikeThrough = font.isOwnStrikeThrough();
		size = font.getOwnSize();
		pdfFontName = font.getOwnPdfFontName();
		pdfEncoding = font.getOwnPdfEncoding();
		isPdfEmbedded = font.isOwnPdfEmbedded();
	}

	/**
	 *
	 */
	public byte getBorder()
	{
		if (border == null) {
			if (style != null && style.getBorder() != null)
				return style.getBorder().byteValue();
			return JRGraphicElement.PEN_NONE;
		}
		return border.byteValue();
	}

	public Byte getOwnBorder()
	{
		return border;
	}

	/**
	 *
	 */
	public void setBorder(byte border)
	{
		this.border = new Byte(border);
	}

	/**
	 *
	 */
	public Color getBorderColor()
	{
		if (borderColor == null) {
			if (style != null && style.getBorderColor() != null)
				return style.getBorderColor();
			return Color.black;
		}
		return borderColor;
	}

	public Color getOwnBorderColor()
	{
		return borderColor;
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
	public int getPadding()
	{
		if (border == null) {
			if (style != null && style.getPadding() != null)
				return style.getPadding().intValue();
			return 0;
		}
		return padding.intValue();
	}

	public Integer getOwnPadding()
	{
		return padding;
	}

	/**
	 *
	 */
	public void setPadding(int padding)
	{
		this.padding = new Integer(padding);
	}

	/**
	 *
	 */
	public byte getTopBorder()
	{
		if (topBorder == null)
		{
			if (border != null)
				return border.byteValue();
			if (style != null && style.getTopBorder() != null)
				return style.getTopBorder().byteValue();
		}
		return topBorder.byteValue();
	}

	/**
	 *
	 */
	public Byte getOwnTopBorder()
	{
		return topBorder;
	}

	/**
	 *
	 */
	public void setTopBorder(byte topBorder)
	{
		this.topBorder = new Byte(topBorder);
	}

	/**
	 *
	 */
	public Color getTopBorderColor()
	{
		if (topBorderColor == null)
		{
			if (borderColor != null)
				return borderColor;
			if (style != null && style.getTopBorderColor() != null)
				return style.getTopBorderColor();
		}
		return topBorderColor;
	}

	/**
	 *
	 */
	public Color getOwnTopBorderColor()
	{
		return topBorderColor;
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
	public int getTopPadding()
	{
		if (topPadding == null)
		{
			if (padding != null)
				return padding.intValue();
			if (style != null && style.getTopPadding() != null)
				return style.getTopPadding().intValue();
		}
		return topPadding.intValue();
	}

	/**
	 *
	 */
	public Integer getOwnTopPadding()
	{
		return topPadding;
	}

	/**
	 *
	 */
	public void setTopPadding(int topPadding)
	{
		this.topPadding = new Integer(topPadding);
	}

	/**
	 *
	 */
	public byte getLeftBorder()
	{
		if (leftBorder == null)
		{
			if (border != null)
				return border.byteValue();
			if (style != null && style.getLeftBorder() != null)
				return style.getLeftBorder().byteValue();
		}
		return leftBorder.byteValue();
	}

	/**
	 *
	 */
	public Byte getOwnLeftBorder()
	{
		return leftBorder;
	}

	/**
	 *
	 */
	public void setLeftBorder(byte leftBorder)
	{
		this.leftBorder = new Byte(leftBorder);
	}

	/**
	 *
	 */
	public Color getLeftBorderColor()
	{
		if (leftBorderColor == null)
		{
			if (borderColor != null)
				return borderColor;
			if (style != null && style.getLeftBorderColor() != null)
				return style.getLeftBorderColor();
		}
		return leftBorderColor;
	}

	/**
	 *
	 */
	public Color getOwnLeftBorderColor()
	{
		return leftBorderColor;
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
	public int getLeftPadding()
	{
		if (leftPadding == null)
		{
			if (padding != null)
				return padding.intValue();
			if (style != null && style.getLeftPadding() != null)
				return style.getLeftPadding().intValue();
		}
		return leftPadding.intValue();
	}

	/**
	 *
	 */
	public Integer getOwnLeftPadding()
	{
		return leftPadding;
	}

	/**
	 *
	 */
	public void setLeftPadding(int leftPadding)
	{
		this.leftPadding = new Integer(leftPadding);
	}

	/**
	 *
	 */
	public byte getBottomBorder()
	{
		if (bottomBorder == null)
		{
			if (border != null)
				return border.byteValue();
			if (style != null && style.getBottomBorder() != null)
				return style.getBottomBorder().byteValue();
		}
		return bottomBorder.byteValue();
	}

	/**
	 *
	 */
	public Byte getOwnBottomBorder()
	{
		return bottomBorder;
	}

	/**
	 *
	 */
	public void setBottomBorder(byte bottomBorder)
	{
		this.bottomBorder = new Byte(bottomBorder);
	}

	/**
	 *
	 */
	public Color getBottomBorderColor()
	{
		if (bottomBorderColor == null)
		{
			if (borderColor != null)
				return borderColor;
			if (style != null && style.getBottomBorderColor() != null)
				return style.getBottomBorderColor();
		}
		return bottomBorderColor;
	}

	/**
	 *
	 */
	public Color getOwnBottomBorderColor()
	{
		return bottomBorderColor;
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
	public int getBottomPadding()
	{
		if (bottomPadding == null)
		{
			if (padding != null)
				return padding.intValue();
			if (style != null && style.getBottomPadding() != null)
				return style.getBottomPadding().intValue();
		}
		return bottomPadding.intValue();
	}

	/**
	 *
	 */
	public Integer getOwnBottomPadding()
	{
		return bottomPadding;
	}

	/**
	 *
	 */
	public void setBottomPadding(int bottomPadding)
	{
		this.bottomPadding = new Integer(bottomPadding);
	}

	/**
	 *
	 */
	public byte getRightBorder()
	{
		if (rightBorder == null)
		{
			if (border != null)
				return border.byteValue();
			if (style != null && style.getRightBorder() != null)
				return style.getRightBorder().byteValue();
		}
		return rightBorder.byteValue();
	}

	/**
	 *
	 */
	public Byte getOwnRightBorder()
	{
		return rightBorder;
	}

	/**
	 *
	 */
	public void setRightBorder(byte rightBorder)
	{
		this.rightBorder = new Byte(rightBorder);
	}

	/**
	 *
	 */
	public Color getRightBorderColor()
	{
		if (rightBorderColor == null)
		{
			if (borderColor != null)
				return borderColor;
			if (style != null && style.getRightBorderColor() != null)
				return style.getRightBorderColor();
		}
		return rightBorderColor;
	}

	/**
	 *
	 */
	public Color getOwnRightBorderColor()
	{
		return rightBorderColor;
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
	public int getRightPadding()
	{
		if (rightPadding == null)
		{
			if (padding != null)
				return padding.intValue();
			if (style != null && style.getRightPadding() != null)
				return style.getRightPadding().intValue();
		}
		return rightPadding.intValue();
	}

	/**
	 *
	 */
	public Integer getOwnRightPadding()
	{
		return rightPadding;
	}

	/**
	 *
	 */
	public void setRightPadding(int rightPadding)
	{
		this.rightPadding = new Integer(rightPadding);
	}

	/**
	 *
	 */
	public JRReportFont getReportFont()
	{
		return reportFont;
	}

	/**
	 *
	 */
	public void setReportFont(JRReportFont reportFont)
	{
		this.reportFont = reportFont;
	}

	/**
	 *
	 */
	public String getFontName()
	{
		if (fontName == null)
		{
			if (reportFont != null)
				return reportFont.getFontName();
			if (style != null && style.getFontName() != null)
				return style.getFontName();
			return DEFAULT_FONT_NAME;
		}
		return fontName;
	}

	/**
	 *
	 */
	public String getOwnFontName()
	{
		return fontName;
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
	public boolean isBold()
	{
		if (isBold == null)
		{
			if (reportFont != null)
				return reportFont.isBold();
			if (style != null && style.isBold() != null)
				return style.isBold().booleanValue();
			return DEFAULT_FONT_BOLD;
		}
		return isBold.booleanValue();
	}

	/**
	 *
	 */
	public Boolean isOwnBold()
	{
		return isBold;
	}

	/**
	 *
	 */
	public void setBold(boolean isBold)
	{
		setBold(isBold ? Boolean.TRUE : Boolean.FALSE);
	}

	/**
	 * Alternative setBold method which allows also to reset
	 * the "own" isBold property.
	 */
	public void setBold(Boolean isBold)
	{
		this.isBold = isBold;
	}


	/**
	 *
	 */
	public boolean isItalic()
	{
		if (isItalic == null)
		{
			if (reportFont != null)
				return reportFont.isItalic();
			if (style != null && style.isItalic() != null)
				return style.isItalic().booleanValue();
			return DEFAULT_FONT_ITALIC;
		}
		return isItalic.booleanValue();
	}

	/**
	 *
	 */
	public Boolean isOwnItalic()
	{
		return isItalic;
	}

	/**
	 *
	 */
	public void setItalic(boolean isItalic)
	{
		setItalic(isItalic ? Boolean.TRUE : Boolean.FALSE);
	}

	/**
	 * Alternative setItalic method which allows also to reset
	 * the "own" isItalic property.
	 */
	public void setItalic(Boolean isItalic)
	{
		this.isItalic = isItalic;
	}

	/**
	 *
	 */
	public boolean isUnderline()
	{
		if (isUnderline == null)
		{
			if (reportFont != null)
				return reportFont.isUnderline();
			if (style != null && style.isUnderline() != null)
				return style.isUnderline().booleanValue();
			return DEFAULT_FONT_UNDERLINE;
		}
		return isUnderline.booleanValue();
	}

	/**
	 *
	 */
	public Boolean isOwnUnderline()
	{
		return isUnderline;
	}

	/**
	 *
	 */
	public void setUnderline(boolean isUnderline)
	{
		setUnderline(isUnderline ? Boolean.TRUE : Boolean.FALSE);
	}

	/**
	 * Alternative setUnderline method which allows also to reset
	 * the "own" isUnderline property.
	 */
	public void setUnderline(Boolean isUnderline)
	{
		this.isUnderline = isUnderline;
	}

	/**
	 *
	 */
	public boolean isStrikeThrough()
	{
		if (isStrikeThrough == null)
		{
			if (reportFont != null)
				return reportFont.isStrikeThrough();
			if (style != null && style.isStrikeThrough() != null)
				return style.isStrikeThrough().booleanValue();
			return DEFAULT_FONT_STRIKETHROUGH;
		}
		return isStrikeThrough.booleanValue();
	}

	/**
	 *
	 */
	public Boolean isOwnStrikeThrough()
	{
		return isStrikeThrough;
	}

	/**
	 *
	 */
	public void setStrikeThrough(boolean isStrikeThrough)
	{
		setStrikeThrough(isStrikeThrough ? Boolean.TRUE : Boolean.FALSE);
	}

	/**
	 * Alternative setStrikeThrough method which allows also to reset
	 * the "own" isStrikeThrough property.
	 */
	public void setStrikeThrough(Boolean isStrikeThrough)
	{
		this.isStrikeThrough = isStrikeThrough;
	}

	/**
	 *
	 */
	public int getSize()
	{
		if (size == null)
		{
			if (reportFont != null)
				return reportFont.getSize();
			if (style != null && style.getSize() != null)
				return style.getSize().intValue();
			return DEFAULT_FONT_SIZE;
		}
		return size.intValue();
	}

	/**
	 *
	 */
	public Integer getOwnSize()
	{
		return size;
	}

	/**
	 *
	 */
	public void setSize(int size)
	{
		setSize(new Integer(size));
	}

	/**
	 * Alternative setSize method which allows also to reset
	 * the "own" size property.
	 */
	public void setSize(Integer size)
	{
		this.size = size;
	}

	/**
	 *
	 */
	public String getPdfFontName()
	{
		if (pdfFontName == null)
		{
			if (reportFont != null)
				return reportFont.getPdfFontName();
			if (style != null && style.getPdfFontName() != null)
				return style.getPdfFontName();
			return DEFAULT_PDF_FONT_NAME;
		}
		return pdfFontName;
	}

	/**
	 *
	 */
	public String getOwnPdfFontName()
	{
		return pdfFontName;
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
	public String getPdfEncoding()
	{
		if (pdfEncoding == null)
		{
			if (reportFont != null)
				return reportFont.getPdfEncoding();
			if (style != null && style.getPdfEncoding() != null)
				return style.getPdfEncoding();
			return DEFAULT_PDF_ENCODING;
		}
		return pdfEncoding;
	}

	/**
	 *
	 */
	public String getOwnPdfEncoding()
	{
		return pdfEncoding;
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
	public boolean isPdfEmbedded()
	{
		if (isPdfEmbedded == null)
		{
			if (reportFont != null)
				return reportFont.isPdfEmbedded();
			if (style != null && style.isPdfEmbedded() != null)
				return style.isPdfEmbedded().booleanValue();
			return DEFAULT_PDF_EMBEDDED;
		}
		return isPdfEmbedded.booleanValue();
	}

	/**
	 *
	 */
	public Boolean isOwnPdfEmbedded()
	{
		return isPdfEmbedded;
	}

	/**
	 *
	 */
	public void setPdfEmbedded(boolean isPdfEmbedded)
	{
		setPdfEmbedded(isPdfEmbedded ? Boolean.TRUE : Boolean.FALSE);
	}

	/**
	 * Alternative setPdfEmbedded method which allows also to reset
	 * the "own" isPdfEmbedded property.
	 */
	public void setPdfEmbedded(Boolean isPdfEmbedded)
	{
		this.isPdfEmbedded = isPdfEmbedded;
	}

	/**
	 *
	 */
	public boolean isCachingAttributes()
	{
		return isCachingAttributes;
	}

	/**
	 *
	 */
	public void setCachingAttributes(boolean isCachingAttributes)
	{
		this.isCachingAttributes = isCachingAttributes;
		attributes = null;
	}


	/**
	 *
	 */
	public Map getNonPdfAttributes()
	{
		Map nonPdfAttributes = new HashMap();

		nonPdfAttributes.put(TextAttribute.FAMILY, getFontName());
		nonPdfAttributes.put(TextAttribute.SIZE, new Float(getSize()));

		if (isBold())
		{
			nonPdfAttributes.put(TextAttribute.WEIGHT, TextAttribute.WEIGHT_BOLD);
		}
		if (isItalic())
		{
			nonPdfAttributes.put(TextAttribute.POSTURE, TextAttribute.POSTURE_OBLIQUE);
		}
		if (isUnderline())
		{
			nonPdfAttributes.put(TextAttribute.UNDERLINE, TextAttribute.UNDERLINE_ON);
		}
		if (isStrikeThrough())
		{
			nonPdfAttributes.put(TextAttribute.STRIKETHROUGH, TextAttribute.STRIKETHROUGH_ON);
		}

		return nonPdfAttributes;
	}


	/**
	 *
	 */
	public Map getAttributes()
	{
		if (attributes == null || !isCachingAttributes)
		{
			attributes = getNonPdfAttributes();

			attributes.put(JRTextAttribute.PDF_FONT_NAME, getPdfFontName());
			attributes.put(JRTextAttribute.PDF_ENCODING, getPdfEncoding());

			if (isPdfEmbedded())
			{
				attributes.put(JRTextAttribute.IS_PDF_EMBEDDED, Boolean.TRUE);
			}
		}

		return attributes;
	}


}
