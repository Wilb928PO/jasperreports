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
import java.awt.font.TextAttribute;
import java.util.HashMap;
import java.util.Map;

import net.sf.jasperreports.engine.JRAlignment;
import net.sf.jasperreports.engine.JRBox;
import net.sf.jasperreports.engine.JRDefaultStyleProvider;
import net.sf.jasperreports.engine.JRElement;
import net.sf.jasperreports.engine.JRFont;
import net.sf.jasperreports.engine.JRGraphicElement;
import net.sf.jasperreports.engine.JRHyperlink;
import net.sf.jasperreports.engine.JRReportFont;
import net.sf.jasperreports.engine.JRStaticText;
import net.sf.jasperreports.engine.JRStyle;
import net.sf.jasperreports.engine.JRTextElement;
import net.sf.jasperreports.engine.JRTextField;
import net.sf.jasperreports.engine.util.JRFontUtil;
import net.sf.jasperreports.engine.util.JRStyleResolver;


/**
 * @author Teodor Danciu (teodord@users.sourceforge.net)
 * @version $Id$
 */
public class JRTemplateText extends JRTemplateElement implements JRAlignment, JRBox, JRFont
{


	/**
	 *
	 */
	private static final long serialVersionUID = 10001;

	/**
	 *
	 */
	private Byte horizontalAlignment = null;
	private Byte verticalAlignment = null;
	private Byte rotation = null;
	private Byte lineSpacing = null;
	private Boolean isStyledText = null;
	private byte hyperlinkType = JRHyperlink.HYPERLINK_TYPE_NONE;
	private byte hyperlinkTarget = JRHyperlink.HYPERLINK_TARGET_SELF;
	//private JRBox box = null;
	//private JRFont font = null;

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
	
	protected transient Map attributes = null;
	
	
	/**
	 *
	 */
	protected JRTemplateText(JRDefaultStyleProvider defaultStyleProvider, JRStaticText staticText)
	{
		super(defaultStyleProvider);
		
		setStaticText(staticText);
	}

	/**
	 *
	 */
	protected JRTemplateText(JRDefaultStyleProvider defaultStyleProvider, JRTextField textField)
	{
		super(defaultStyleProvider);
		
		setTextField(textField);
	}


	/**
	 *
	 */
	protected void setStaticText(JRStaticText staticText)
	{
		setTextElement(staticText);
	}

	/**
	 *
	 */
	protected void setTextField(JRTextField textField)
	{
		setTextElement(textField);

		hyperlinkType = textField.getHyperlinkType();
		hyperlinkTarget = textField.getHyperlinkTarget();
	}

	/**
	 *
	 */
	protected void setTextElement(JRTextElement textElement)
	{
		super.setElement(textElement);

		border = textElement.getOwnBorder();
		topBorder = textElement.getOwnTopBorder();
		leftBorder = textElement.getOwnLeftBorder();
		bottomBorder = textElement.getOwnBottomBorder();
		rightBorder = textElement.getOwnRightBorder();
		borderColor = textElement.getOwnBorderColor();
		topBorderColor = textElement.getOwnTopBorderColor();
		leftBorderColor = textElement.getOwnLeftBorderColor();
		bottomBorderColor = textElement.getOwnBottomBorderColor();
		rightBorderColor = textElement.getOwnRightBorderColor();
		padding = textElement.getOwnPadding();
		topPadding = textElement.getOwnTopPadding();
		leftPadding = textElement.getOwnLeftPadding();
		bottomPadding = textElement.getOwnBottomPadding();
		rightPadding = textElement.getOwnRightPadding();

		reportFont = textElement.getReportFont();

		fontName = textElement.getOwnFontName();
		isBold = textElement.isOwnBold();
		isItalic = textElement.isOwnItalic();
		isUnderline = textElement.isOwnUnderline();
		isStrikeThrough = textElement.isOwnStrikeThrough();
		size = textElement.getOwnSize();
		pdfFontName = textElement.getOwnPdfFontName();
		pdfEncoding = textElement.getOwnPdfEncoding();
		isPdfEmbedded = textElement.isOwnPdfEmbedded();

		horizontalAlignment = textElement.getOwnHorizontalAlignment();
		verticalAlignment = textElement.getOwnVerticalAlignment();
		rotation = textElement.getOwnRotation();
		lineSpacing = textElement.getOwnLineSpacing();
		isStyledText = textElement.isOwnStyledText();
	}

	
	/**
	 * 
	 */
	protected JRFont getBaseFont()
	{
		if (reportFont != null)
			return reportFont;
		if (defaultStyleProvider != null)
			return defaultStyleProvider.getDefaultFont();
		return null;
	}
	
	/**
	 *
	 */
	protected byte getDefaultMode()
	{
		return JRElement.MODE_TRANSPARENT;
	}
		
		
	/**
	 * @deprecated Replaced by {@link #getHorizontalAlignment()}.
	 */
	public byte getTextAlignment()
	{
		return getHorizontalAlignment();
	}
		
	/**
	 *
	 */
	public byte getHorizontalAlignment()
	{
		if (horizontalAlignment == null) {
			if (parentStyle != null && parentStyle.getHorizontalAlignment() != null)
				return parentStyle.getHorizontalAlignment().byteValue();
			return JRAlignment.HORIZONTAL_ALIGN_LEFT;
		}
		return horizontalAlignment.byteValue();
	}
		
	/**
	 *
	 */
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
	public void setHorizontalAlignment(Byte horizontalAlignment)
	{
		this.horizontalAlignment = horizontalAlignment;
	}

	/**
	 *
	 */
	public byte getVerticalAlignment()
	{
		if (verticalAlignment == null) {
			if (parentStyle != null && parentStyle.getVerticalAlignment() != null)
				return parentStyle.getVerticalAlignment().byteValue();
			return JRAlignment.VERTICAL_ALIGN_TOP;
		}
		return verticalAlignment.byteValue();
	}
		
	/**
	 *
	 */
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
	public void setVerticalAlignment(Byte verticalAlignment)
	{
		this.verticalAlignment = verticalAlignment;
	}

	/**
	 *
	 */
	public byte getRotation()
	{
		if (rotation == null) {
			if (parentStyle != null && parentStyle.getRotation() != null)
				return parentStyle.getRotation().byteValue();
			return JRTextElement.ROTATION_NONE;
		}
		return rotation.byteValue();
	}
		
	/**
	 *
	 */
	public Byte getOwnRotation()
	{
		return rotation;
	}
		
	/**
	 *
	 */
	public byte getLineSpacing()
	{
		if (lineSpacing == null) {
			if (parentStyle != null && parentStyle.getLineSpacing() != null)
				return parentStyle.getLineSpacing().byteValue();
			return JRTextElement.LINE_SPACING_SINGLE;
		}
		return lineSpacing.byteValue();
	}
		
	/**
	 *
	 */
	public Byte getOwnLineSpacing()
	{
		return lineSpacing;
	}
		
	/**
	 *
	 */
	public boolean isStyledText()
	{
		if (isStyledText == null) {
			if (parentStyle != null && parentStyle.isStyledText() != null)
				return parentStyle.isStyledText().booleanValue();
			return false;
		}
		return isStyledText.booleanValue();
	}
		
	/**
	 *
	 */
	public Boolean isOwnStyledText()
	{
		return isStyledText;
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
	 *
	 */
	public byte getHyperlinkType()
	{
		return hyperlinkType;
	}

	/**
	 *
	 */
	public byte getHyperlinkTarget()
	{
		return hyperlinkTarget;
	}

	/**
	 *
	 */
	public byte getBorder()
	{
		if (border == null) {
			if (parentStyle != null && parentStyle.getBorder() != null)
				return parentStyle.getBorder().byteValue();
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
			if (parentStyle != null && parentStyle.getBorderColor() != null)
				return parentStyle.getBorderColor();
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
		if (padding == null) {
			if (parentStyle != null && parentStyle.getPadding() != null)
				return parentStyle.getPadding().intValue();
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
			if (parentStyle != null && parentStyle.getTopBorder() != null)
				return parentStyle.getTopBorder().byteValue();
			return JRGraphicElement.PEN_NONE;
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
			if (parentStyle != null && parentStyle.getTopBorderColor() != null)
				return parentStyle.getTopBorderColor();
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
			if (parentStyle != null && parentStyle.getTopPadding() != null)
				return parentStyle.getTopPadding().intValue();
			return 0;
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
			if (parentStyle != null && parentStyle.getLeftBorder() != null)
				return parentStyle.getLeftBorder().byteValue();
			return JRGraphicElement.PEN_NONE;
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
			if (parentStyle != null && parentStyle.getLeftBorderColor() != null)
				return parentStyle.getLeftBorderColor();
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
			if (parentStyle != null && parentStyle.getLeftPadding() != null)
				return parentStyle.getLeftPadding().intValue();
			return 0;
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
			if (parentStyle != null && parentStyle.getBottomBorder() != null)
				return parentStyle.getBottomBorder().byteValue();
			return JRGraphicElement.PEN_NONE;
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
			if (parentStyle != null && parentStyle.getBottomBorderColor() != null)
				return parentStyle.getBottomBorderColor();
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
			if (parentStyle != null && parentStyle.getBottomPadding() != null)
				return parentStyle.getBottomPadding().intValue();
			return 0;
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
			if (parentStyle != null && parentStyle.getRightBorder() != null)
				return parentStyle.getRightBorder().byteValue();
			return JRGraphicElement.PEN_NONE;
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
			if (parentStyle != null && parentStyle.getRightBorderColor() != null)
				return parentStyle.getRightBorderColor();
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
			if (parentStyle != null && parentStyle.getRightPadding() != null)
				return parentStyle.getRightPadding().intValue();
			return 0;
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
		return JRStyleResolver.getFontName(this);
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
			if (parentStyle != null && parentStyle.isBold() != null)
				return parentStyle.isBold().booleanValue();
			return JRFont.DEFAULT_FONT_BOLD;
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
			if (parentStyle != null && parentStyle.isItalic() != null)
				return parentStyle.isItalic().booleanValue();
			return JRFont.DEFAULT_FONT_ITALIC;
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
			if (parentStyle != null && parentStyle.isUnderline() != null)
				return parentStyle.isUnderline().booleanValue();
			return JRFont.DEFAULT_FONT_UNDERLINE;
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
			JRFont font = getBaseFont();
			if (font != null)
				return font.isStrikeThrough();
			JRStyle style = getBaseStyle();
			if (style != null && style.isStrikeThrough() != null)
				return style.isStrikeThrough().booleanValue();
			return JRFont.DEFAULT_FONT_STRIKETHROUGH;
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
			JRFont font = getBaseFont();
			if (font != null)
				return font.getSize();
			JRStyle style = getBaseStyle();
			if (style != null && style.getSize() != null)
				return style.getSize().intValue();
			return JRFont.DEFAULT_FONT_SIZE;
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
			if (parentStyle != null && parentStyle.getPdfFontName() != null)
				return parentStyle.getPdfFontName();
			return JRFont.DEFAULT_PDF_FONT_NAME;
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
			if (parentStyle != null && parentStyle.getPdfEncoding() != null)
				return parentStyle.getPdfEncoding();
			return JRFont.DEFAULT_PDF_ENCODING;
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
			if (parentStyle != null && parentStyle.isPdfEmbedded() != null)
				return parentStyle.isPdfEmbedded().booleanValue();
			return JRFont.DEFAULT_PDF_EMBEDDED;
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
	public void setBorder(Byte border)
	{
		this.border = border;
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
	public void setTopBorder(Byte topBorder)
	{
		this.topBorder = topBorder;
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
	public void setLeftBorder(Byte leftBorder)
	{
		this.leftBorder = leftBorder;
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
	public void setBottomBorder(Byte bottomBorder)
	{
		this.bottomBorder = bottomBorder;
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
	public void setRightBorder(Byte rightBorder)
	{
		this.rightBorder = rightBorder;
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
		if (attributes == null)
		{
			attributes = new HashMap();
			
			JRFontUtil.setAttributes(attributes, this);
		}

		return attributes;
	}

	public JRStyle getStyle()
	{
		return parentStyle;
	}
}
