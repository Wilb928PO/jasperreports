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

import net.sf.jasperreports.engine.JRAnchor;
import net.sf.jasperreports.engine.JRBox;
import net.sf.jasperreports.engine.JRFont;
import net.sf.jasperreports.engine.JRPrintText;
import net.sf.jasperreports.engine.JRReportFont;


/**
 * @author Teodor Danciu (teodord@users.sourceforge.net)
 * @version $Id$
 */
public class JRTemplatePrintText extends JRTemplatePrintElement implements JRPrintText
{


	/**
	 *
	 */
	private static final long serialVersionUID = 10001;

	/**
	 *
	 */
	private String text = "";
	private float lineSpacingFactor = 0;
	private float leadingOffset = 0;
	private byte runDirection = RUN_DIRECTION_LTR;
	private float textHeight = 0;
	private String anchorName = null;
	private String hyperlinkReference = null;
	private String hyperlinkAnchor = null;
	private Integer hyperlinkPage = null;

	/**
	 * The bookmark level for the anchor associated with this field.
	 * @see JRAnchor#getBookmarkLevel()
	 */
	protected int bookmarkLevel = JRAnchor.NO_BOOKMARK;
	
	/**
	 *
	 */
	public JRTemplatePrintText(JRTemplateText text)
	{
		super(text);
	}

	/**
	 *
	 */
	public String getText()
	{
		return text;
	}
		
	/**
	 *
	 */
	public void setText(String text)
	{
		this.text = text;
	}

	/**
	 *
	 */
	public float getLineSpacingFactor()
	{
		return lineSpacingFactor;
	}
		
	/**
	 *
	 */
	public void setLineSpacingFactor(float lineSpacingFactor)
	{
		this.lineSpacingFactor = lineSpacingFactor;
	}

	/**
	 *
	 */
	public float getLeadingOffset()
	{
		return leadingOffset;
	}
		
	/**
	 *
	 */
	public void setLeadingOffset(float leadingOffset)
	{
		this.leadingOffset = leadingOffset;
	}

	/**
	 * @deprecated Replaced by {@link #getHorizontalAlignment()}.
	 */
	public byte getTextAlignment()
	{
		return getHorizontalAlignment();
	}
		
	/**
	 * @deprecated Replaced by {@link #setHorizontalAlignment(byte)}.
	 */
	public void setTextAlignment(byte horizontalAlignment)
	{
	}
		
	/**
	 *
	 */
	public byte getHorizontalAlignment()
	{
		return ((JRTemplateText)template).getHorizontalAlignment();
	}
		
	public Byte getOwnHorizontalAlignment()
	{
		return null;
	}

	/**
	 *
	 */
	public void setHorizontalAlignment(byte horizontalAlignment)
	{
	}
		
	/**
	 *
	 */
	public void setHorizontalAlignment(Byte horizontalAlignment)
	{
	}
		
	/**
	 *
	 */
	public byte getVerticalAlignment()
	{
		return ((JRTemplateText)template).getVerticalAlignment();
	}
		
	/**
	 *
	 */
	public Byte getOwnVerticalAlignment()
	{
		return null;
	}

	/**
	 *
	 */
	public void setVerticalAlignment(byte verticalAlignment)
	{
	}
		
	/**
	 *
	 */
	public void setVerticalAlignment(Byte verticalAlignment)
	{
	}
		
	/**
	 *
	 */
	public byte getRotation()
	{
		return ((JRTemplateText)template).getRotation();
	}
		
	public Byte getOwnRotation()
	{
		return null;
	}

	/**
	 *
	 */
	public void setRotation(byte rotation)
	{
	}
		
	/**
	 *
	 */
	public void setRotation(Byte rotation)
	{
	}
		
	/**
	 *
	 */
	public byte getRunDirection()
	{
		return runDirection;
	}
		
	/**
	 *
	 */
	public void setRunDirection(byte runDirection)
	{
		this.runDirection = runDirection;
	}
		
	/**
	 *
	 */
	public float getTextHeight()
	{
		return textHeight;
	}
		
	/**
	 *
	 */
	public void setTextHeight(float textHeight)
	{
		this.textHeight = textHeight;
	}

	/**
	 *
	 */
	public byte getLineSpacing()
	{
		return ((JRTemplateText)template).getLineSpacing();
	}
		
	public Byte getOwnLineSpacing()
	{
		return null;
	}

	/**
	 *
	 */
	public void setLineSpacing(byte lineSpacing)
	{
	}
		
	/**
	 *
	 */
	public void setLineSpacing(Byte lineSpacing)
	{
	}
		
	/**
	 *
	 */
	public boolean isStyledText()
	{
		return ((JRTemplateText)template).isStyledText();
	}
		
	public Boolean isOwnStyledText()
	{
		return null;
	}

	/**
	 *
	 */
	public void setStyledText(boolean isStyledText)
	{
	}
		
	/**
	 *
	 */
	public void setStyledText(Boolean isStyledText)
	{
	}
		
	/**
	 * @deprecated
	 */
	public JRBox getBox()
	{
		return (JRTemplateText)template;
	}
		
	/**
	 * @deprecated
	 */
	public void setBox(JRBox box)
	{
	}

	/**
	 * @deprecated
	 */
	public JRFont getFont()
	{
		return (JRTemplateText)template;
	}
		
	/**
	 * @deprecated
	 */
	public void setFont(JRFont font)
	{
	}

	/**
	 *
	 */
	public String getAnchorName()
	{
		return anchorName;
	}
		
	/**
	 *
	 */
	public void setAnchorName(String anchorName)
	{
		this.anchorName = anchorName;
	}
		
	/**
	 *
	 */
	public byte getHyperlinkType()
	{
		return ((JRTemplateText)template).getHyperlinkType();
	}
		
	/**
	 *
	 */
	public void setHyperlinkType(byte hyperlinkType)
	{
	}

	/**
	 *
	 */
	public byte getHyperlinkTarget()
	{
		return ((JRTemplateText)template).getHyperlinkTarget();
	}
		
	/**
	 *
	 */
	public void setHyperlinkTarget(byte hyperlinkTarget)
	{
	}

	/**
	 *
	 */
	public String getHyperlinkReference()
	{
		return hyperlinkReference;
	}
		
	/**
	 *
	 */
	public void setHyperlinkReference(String hyperlinkReference)
	{
		this.hyperlinkReference = hyperlinkReference;
	}
		
	/**
	 *
	 */
	public String getHyperlinkAnchor()
	{
		return hyperlinkAnchor;
	}
		
	/**
	 *
	 */
	public void setHyperlinkAnchor(String hyperlinkAnchor)
	{
		this.hyperlinkAnchor = hyperlinkAnchor;
	}
		
	/**
	 *
	 */
	public Integer getHyperlinkPage()
	{
		return hyperlinkPage;
	}
		
	/**
	 *
	 */
	public void setHyperlinkPage(Integer hyperlinkPage)
	{
		this.hyperlinkPage = hyperlinkPage;
	}


	public int getBookmarkLevel()
	{
		return bookmarkLevel;
	}


	public void setBookmarkLevel(int bookmarkLevel)
	{
		this.bookmarkLevel = bookmarkLevel;
	}

	/**
	 *
	 */
	public byte getBorder()
	{
		return ((JRTemplateText)template).getBorder();
	}

	public Byte getOwnBorder()
	{
		return null;
	}

	/**
	 *
	 */
	public void setBorder(byte border)
	{
	}

	/**
	 *
	 */
	public Color getBorderColor()
	{
		return ((JRTemplateText)template).getBorderColor();
	}

	public Color getOwnBorderColor()
	{
		return null;
	}

	/**
	 *
	 */
	public void setBorderColor(Color borderColor)
	{
	}

	/**
	 *
	 */
	public int getPadding()
	{
		return ((JRTemplateText)template).getPadding();
	}

	public Integer getOwnPadding()
	{
		return null;
	}

	/**
	 *
	 */
	public void setPadding(int padding)
	{
	}

	/**
	 *
	 */
	public byte getTopBorder()
	{
		return ((JRTemplateText)template).getTopBorder();
	}

	/**
	 *
	 */
	public Byte getOwnTopBorder()
	{
		return null;
	}

	/**
	 *
	 */
	public void setTopBorder(byte topBorder)
	{
	}

	/**
	 *
	 */
	public Color getTopBorderColor()
	{
		return ((JRTemplateText)template).getTopBorderColor();
	}

	/**
	 *
	 */
	public Color getOwnTopBorderColor()
	{
		return null;
	}

	/**
	 *
	 */
	public void setTopBorderColor(Color topBorderColor)
	{
	}

	/**
	 *
	 */
	public int getTopPadding()
	{
		return ((JRTemplateText)template).getTopPadding();
	}

	/**
	 *
	 */
	public Integer getOwnTopPadding()
	{
		return null;
	}

	/**
	 *
	 */
	public void setTopPadding(int topPadding)
	{
	}

	/**
	 *
	 */
	public byte getLeftBorder()
	{
		return ((JRTemplateText)template).getLeftBorder();
	}

	/**
	 *
	 */
	public Byte getOwnLeftBorder()
	{
		return null;
	}

	/**
	 *
	 */
	public void setLeftBorder(byte leftBorder)
	{
	}

	/**
	 *
	 */
	public Color getLeftBorderColor()
	{
		return ((JRTemplateText)template).getLeftBorderColor();
	}

	/**
	 *
	 */
	public Color getOwnLeftBorderColor()
	{
		return null;
	}

	/**
	 *
	 */
	public void setLeftBorderColor(Color leftBorderColor)
	{
	}

	/**
	 *
	 */
	public int getLeftPadding()
	{
		return ((JRTemplateText)template).getLeftPadding();
	}

	/**
	 *
	 */
	public Integer getOwnLeftPadding()
	{
		return null;
	}

	/**
	 *
	 */
	public void setLeftPadding(int leftPadding)
	{
	}

	/**
	 *
	 */
	public byte getBottomBorder()
	{
		return ((JRTemplateText)template).getBottomBorder();
	}

	/**
	 *
	 */
	public Byte getOwnBottomBorder()
	{
		return null;
	}

	/**
	 *
	 */
	public void setBottomBorder(byte bottomBorder)
	{
	}

	/**
	 *
	 */
	public Color getBottomBorderColor()
	{
		return ((JRTemplateText)template).getBottomBorderColor();
	}

	/**
	 *
	 */
	public Color getOwnBottomBorderColor()
	{
		return null;
	}

	/**
	 *
	 */
	public void setBottomBorderColor(Color bottomBorderColor)
	{
	}

	/**
	 *
	 */
	public int getBottomPadding()
	{
		return ((JRTemplateText)template).getBottomPadding();
	}

	/**
	 *
	 */
	public Integer getOwnBottomPadding()
	{
		return null;
	}

	/**
	 *
	 */
	public void setBottomPadding(int bottomPadding)
	{
	}

	/**
	 *
	 */
	public byte getRightBorder()
	{
		return ((JRTemplateText)template).getRightBorder();
	}

	/**
	 *
	 */
	public Byte getOwnRightBorder()
	{
		return null;
	}

	/**
	 *
	 */
	public void setRightBorder(byte rightBorder)
	{
	}

	/**
	 *
	 */
	public Color getRightBorderColor()
	{
		return ((JRTemplateText)template).getRightBorderColor();
	}

	/**
	 *
	 */
	public Color getOwnRightBorderColor()
	{
		return null;
	}

	/**
	 *
	 */
	public void setRightBorderColor(Color rightBorderColor)
	{
	}

	/**
	 *
	 */
	public int getRightPadding()
	{
		return ((JRTemplateText)template).getRightPadding();
	}

	/**
	 *
	 */
	public Integer getOwnRightPadding()
	{
		return null;
	}

	/**
	 *
	 */
	public void setRightPadding(int rightPadding)
	{
	}

	/**
	 *
	 */
	public JRReportFont getReportFont()
	{
		return ((JRTemplateText)template).getReportFont();
	}

	/**
	 *
	 */
	public void setReportFont(JRReportFont reportFont)
	{
	}

	/**
	 *
	 */
	public String getFontName()
	{
		return ((JRTemplateText)template).getFontName();
	}

	/**
	 *
	 */
	public String getOwnFontName()
	{
		return null;
	}

	/**
	 *
	 */
	public void setFontName(String fontName)
	{
	}


	/**
	 *
	 */
	public boolean isBold()
	{
		return ((JRTemplateText)template).isBold();
	}

	/**
	 *
	 */
	public Boolean isOwnBold()
	{
		return null;
	}

	/**
	 *
	 */
	public void setBold(boolean isBold)
	{
	}

	/**
	 * Alternative setBold method which allows also to reset
	 * the "own" isBold property.
	 */
	public void setBold(Boolean isBold)
	{
	}


	/**
	 *
	 */
	public boolean isItalic()
	{
		return ((JRTemplateText)template).isItalic();
	}

	/**
	 *
	 */
	public Boolean isOwnItalic()
	{
		return null;
	}

	/**
	 *
	 */
	public void setItalic(boolean isItalic)
	{
	}

	/**
	 * Alternative setItalic method which allows also to reset
	 * the "own" isItalic property.
	 */
	public void setItalic(Boolean isItalic)
	{
	}

	/**
	 *
	 */
	public boolean isUnderline()
	{
		return ((JRTemplateText)template).isUnderline();
	}

	/**
	 *
	 */
	public Boolean isOwnUnderline()
	{
		return null;
	}

	/**
	 *
	 */
	public void setUnderline(boolean isUnderline)
	{
	}

	/**
	 * Alternative setUnderline method which allows also to reset
	 * the "own" isUnderline property.
	 */
	public void setUnderline(Boolean isUnderline)
	{
	}

	/**
	 *
	 */
	public boolean isStrikeThrough()
	{
		return ((JRTemplateText)template).isStrikeThrough();
	}

	/**
	 *
	 */
	public Boolean isOwnStrikeThrough()
	{
		return null;
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
	}

	/**
	 *
	 */
	public int getSize()
	{
		return ((JRTemplateText)template).getSize();
	}

	/**
	 *
	 */
	public Integer getOwnSize()
	{
		return null;
	}

	/**
	 *
	 */
	public void setSize(int size)
	{
	}

	/**
	 * Alternative setSize method which allows also to reset
	 * the "own" size property.
	 */
	public void setSize(Integer size)
	{
	}

	/**
	 *
	 */
	public String getPdfFontName()
	{
		return ((JRTemplateText)template).getPdfFontName();
	}

	/**
	 *
	 */
	public String getOwnPdfFontName()
	{
		return null;
	}

	/**
	 *
	 */
	public void setPdfFontName(String pdfFontName)
	{
	}


	/**
	 *
	 */
	public String getPdfEncoding()
	{
		return ((JRTemplateText)template).getPdfEncoding();
	}

	/**
	 *
	 */
	public String getOwnPdfEncoding()
	{
		return null;
	}

	/**
	 *
	 */
	public void setPdfEncoding(String pdfEncoding)
	{
	}


	/**
	 *
	 */
	public boolean isPdfEmbedded()
	{
		return ((JRTemplateText)template).isPdfEmbedded();
	}

	/**
	 *
	 */
	public Boolean isOwnPdfEmbedded()
	{
		return null;
	}

	/**
	 *
	 */
	public void setPdfEmbedded(boolean isPdfEmbedded)
	{
	}

	/**
	 * Alternative setPdfEmbedded method which allows also to reset
	 * the "own" isPdfEmbedded property.
	 */
	public void setPdfEmbedded(Boolean isPdfEmbedded)
	{
	}

	/**
	 *
	 */
	public void setBorder(Byte border)
	{
	}

	/**
	 *
	 */
	public void setPadding(Integer padding)
	{
	}

	/**
	 *
	 */
	public void setTopBorder(Byte topBorder)
	{
	}

	/**
	 *
	 */
	public void setTopPadding(Integer topPadding)
	{
	}

	/**
	 *
	 */
	public void setLeftBorder(Byte leftBorder)
	{
	}

	/**
	 *
	 */
	public void setLeftPadding(Integer leftPadding)
	{
	}

	/**
	 *
	 */
	public void setBottomBorder(Byte bottomBorder)
	{
	}

	/**
	 *
	 */
	public void setBottomPadding(Integer bottomPadding)
	{
	}

	/**
	 *
	 */
	public void setRightBorder(Byte rightBorder)
	{
	}

	/**
	 *
	 */
	public void setRightPadding(Integer rightPadding)
	{
	}


	/**
	 *
	 */
	public Map getAttributes()
	{
		return ((JRTemplateText)template).getAttributes();
	}
	
}
