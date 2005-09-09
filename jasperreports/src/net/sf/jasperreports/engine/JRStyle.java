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
package net.sf.jasperreports.engine;

import java.awt.Color;

/**
 * @author Ionut Nedelcu (ionutned@users.sourceforge.net)
 * @version $Id
 */
public interface JRStyle
{
	/**
	 * Gets the style unique name.
	 */
	public String getName();

	public JRStyle getParentStyle();

	/**
	 * Gets a flag that specifies if this is the default report style.
	 */
	public boolean isDefault();

	/**
	 * Returns the element transparency mode.
	 * The default value depends on the type of the report element. Graphic elements like rectangles and lines are
	 * opaque by default, but the images are transparent. Both static texts and text fields are transparent
	 * by default, and so are the subreport elements.
	 * @return MODE_OPAQUE or MODE_TRANSPARENT
	 */
	public Byte getMode();

	public Byte getOwnMode();

	public Color getForecolor();

	public Color getOwnForecolor();

	public Color getBackcolor();

	public Color getOwnBackcolor();

	/**
	 * Indicates the pen type used for this element.
	 * @return one of the pen constants in this class
	 */
	public Byte getPen();

	public Byte getOwnPen();

	/**
	 * Indicates the fill type used for this element.
	 * @return one of the pen constants in this class
	 */
	public Byte getFill();

	public Byte getOwnFill();

	/**
	 * Indicates the corner radius for rectangles with round corners. The default is 0.
	 */
	public Integer getRadius();

	public Integer getOwnRadius();

	/**
	 * Gets the image scale type.
	 * @return one of the scale constants in this class
	 */
	public Byte getScaleImage();

	public Byte getOwnScaleImage();

	/**
	 * Gets the horizontal alignment of the element.
	 * @return one of the alignment values defined in {@link JRAlignment}
	 */
	public Byte getHorizontalAlignment();

	public Byte getOwnHorizontalAlignment();

	/**
	 * Gets the vertical alignment of the element.
	 * @return one of the alignment values defined in {@link JRAlignment}
	 */
	public Byte getVerticalAlignment();

	public Byte getOwnVerticalAlignment();

	/**
	 * Gets the default border pen size (can be overwritten by individual settings).
	 */
	public Byte getBorder();

	public Byte getOwnBorder();

	/**
	 * Gets the default border color (can be overwritten by individual settings).
	 */
	public Color getBorderColor();

	public Color getOwnBorderColor();

	/**
	 * Gets the default padding in pixels (can be overwritten by individual settings).
	 */
	public Integer getPadding();

	public Integer getOwnPadding();

	/**
	 * Gets the top border pen size.
	 */
	public Byte getTopBorder();


	/**
	 * Gets the top border pen size (if the default value was overwritten).
	 */
	public Byte getOwnTopBorder();


	/**
	 * Gets the top border color.
	 */
	public Color getTopBorderColor();


	/**
	 * Gets the top border color (if the default value was overwritten).
	 */
	public Color getOwnTopBorderColor();


	/**
	 *
	 */
	public Integer getTopPadding();


	/**
	 *
	 */
	public Integer getOwnTopPadding();


	/**
	 *
	 */
	public Byte getLeftBorder();


	/**
	 *
	 */
	public Byte getOwnLeftBorder();


	/**
	 *
	 */
	public Color getLeftBorderColor();


	/**
	 *
	 */
	public Color getOwnLeftBorderColor();


	/**
	 *
	 */
	public Integer getLeftPadding();


	/**
	 *
	 */
	public Integer getOwnLeftPadding();


	/**
	 *
	 */
	public Byte getBottomBorder();


	/**
	 *
	 */
	public Byte getOwnBottomBorder();


	/**
	 *
	 */
	public Color getBottomBorderColor();


	/**
	 *
	 */
	public Color getOwnBottomBorderColor();


	/**
	 *
	 */
	public Integer getBottomPadding();


	/**
	 *
	 */
	public Integer getOwnBottomPadding();


	/**
	 *
	 */
	public Byte getRightBorder();


	/**
	 *
	 */
	public Byte getOwnRightBorder();


	/**
	 *
	 */
	public Color getRightBorderColor();


	/**
	 *
	 */
	public Color getOwnRightBorderColor();


	/**
	 *
	 */
	public Integer getRightPadding();


	/**
	 *
	 */
	public Integer getOwnRightPadding();


	/**
	 * Gets the text rotation.
	 * @return a value representing one of the rotation constants in this class
	 */
	public Byte getRotation();

	public Byte getOwnRotation();

	/**
	 * Gets the line spacing.
	 * @return a value representing one of the line spacing constants in this class
	 */
	public Byte getLineSpacing();

	public Byte getOwnLineSpacing();

	/**
	 * Returns true if the text can contain style tags.
	 */
	public Boolean isStyledText();

	public Boolean isOwnStyledText();

	/**
	 *
	 */
	public String getFontName();

	/**
	 *
	 */
	public String getOwnFontName();

	/**
	 *
	 */
	public Boolean isBold();

	/**
	 *
	 */
	public Boolean isOwnBold();

	/**
	 *
	 */
	public Boolean isItalic();

	/**
	 *
	 */
	public Boolean isOwnItalic();

	/**
	 *
	 */
	public Boolean isUnderline();

	/**
	 *
	 */
	public Boolean isOwnUnderline();

	/**
	 *
	 */
	public Boolean isStrikeThrough();

	/**
	 *
	 */
	public Boolean isOwnStrikeThrough();

	/**
	 *
	 */
	public Integer getSize();

	/**
	 *
	 */
	public Integer getOwnSize();

	/**
	 *
	 */
	public String getPdfFontName();

	/**
	 *
	 */
	public String getOwnPdfFontName();

	/**
	 *
	 */
	public String getPdfEncoding();

	/**
	 *
	 */
	public String getOwnPdfEncoding();

	/**
	 *
	 */
	public Boolean isPdfEmbedded();

	/**
	 *
	 */
	public Boolean isOwnPdfEmbedded();

	/**
	 * Gets the pattern used for this text field. The pattern will be used in a <tt>SimpleDateFormat</tt> for dates
	 * and a <tt>DecimalFormat</tt> for numeric text fields. The pattern format must follow one of these two classes
	 * formatting rules, as specified in the JDK API docs.
	 * @return a string containing the pattern.
	 */
	public String getPattern();

	public String getOwnPattern();
}
