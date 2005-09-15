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
package net.sf.jasperreports.engine.util;

import net.sf.jasperreports.engine.JRFont;
import net.sf.jasperreports.engine.JRStyle;
import net.sf.jasperreports.engine.JRStyleContainer;


/**
 * @author Teodor Danciu (teodord@users.sourceforge.net)
 * @version $Id$
 */
public class JRStyleResolver
{


	/**
	 *
	 */
	private static JRFont getBaseFont(JRFont font)
	{
		if (font.getReportFont() != null)
			return font.getReportFont();
		if (font.getDefaultStyleProvider() != null)
			return font.getDefaultStyleProvider().getDefaultFont();
		return null;
	}
	
	/**
	 *
	 */
	private static JRStyle getBaseStyle(JRStyleContainer styleContainer)
	{
		if (styleContainer.getStyle() != null)
			return styleContainer.getStyle();
		if (styleContainer.getDefaultStyleProvider() != null)
			return styleContainer.getDefaultStyleProvider().getDefaultStyle();
		return null;
	}

	/**
	 *
	 */
	public static String getFontName(JRFont font)
	{
		if (font.getOwnFontName() != null)
			return font.getOwnFontName();
		JRFont baseFont = getBaseFont(font);
		if (baseFont != null && baseFont.getFontName() != null)
			return baseFont.getFontName();
		JRStyle baseStyle = getBaseStyle(font);
		if (baseStyle != null && baseStyle.getFontName() != null)
			return baseStyle.getFontName();
		return JRFont.DEFAULT_FONT_NAME;
	}
	

	/**
	 *
	 */
	public static String getFontName(JRStyle style)
	{
		if (style.getOwnFontName() != null)
			return style.getOwnFontName();
		JRStyle baseStyle = getBaseStyle(style);
		if (baseStyle != null && baseStyle.getFontName() != null)
			return baseStyle.getFontName();
		return JRFont.DEFAULT_FONT_NAME;
	}

}
