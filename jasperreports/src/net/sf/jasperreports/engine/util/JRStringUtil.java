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

/*
 * Contributors:
 * Gaganis Giorgos - gaganis@users.sourceforge.net
 */
package net.sf.jasperreports.engine.util;


/**
 * @author Teodor Danciu (teodord@users.sourceforge.net)
 * @version $Id$
 */
public class JRStringUtil
{


	/**
	 *
	 */
	public static String xmlEncode(String text)
	{
		if (text != null)
		{
			StringBuffer ret = new StringBuffer();

			for (int i = 0; i < text.length(); i ++)
			{
				switch (text.charAt(i))
				{
		//			case ' ' : ret.append("&nbsp;"); break;
					case '&' : ret.append("&amp;"); break;
					case '>' : ret.append("&gt;"); break;
					case '<' : ret.append("&lt;"); break;
					case '\"' : ret.append("&quot;"); break;

					default : ret.append(text.substring(i, i + 1)); break;
				}
			}

			return ret.toString();
		}

		return null;
	}


	/**
	 *
	 */
	public static String htmlEncode(String text)
	{
		if (text != null)
		{
			StringBuffer ret = new StringBuffer();

			boolean isEncodeSpace = true;
			for (int i = 0; i < text.length(); i ++)
			{
				switch (text.charAt(i))
				{
					case ' ' : 
						if (isEncodeSpace)
						{
							ret.append("&nbsp;");
							isEncodeSpace = false;
						}
						else
						{
							ret.append(" ");
							isEncodeSpace = true;
						}
						break;
					case '&' : ret.append("&amp;"); isEncodeSpace = false; break;
					case '>' : ret.append("&gt;"); isEncodeSpace = false; break;
					case '<' : ret.append("&lt;"); isEncodeSpace = false; break;
					case '\"' : ret.append("&quot;"); isEncodeSpace = false; break;
					case '\n' : ret.append("<br>"); isEncodeSpace = false; break;

					default : ret.append(text.substring(i, i + 1)); isEncodeSpace = false; break;
				}
			}

			return ret.toString();
		}

		return null;
	}


	/**
	 * Takes a name and returns the same if it is a Java identifier;
	 * else it substitutes the illegal characters so that it can be an identifier
	 *
	 * @param name
	 */
	public static String getLiteral(String name)
	{
		if (isValidLiteral(name))
		{
			return name;
		}

		StringBuffer buffer = new StringBuffer(name.length() + 5);
		
		char[] literalChars = new char[name.length()];
		name.getChars(0, literalChars.length, literalChars, 0);
		
		for (int i = 0; i < literalChars.length; i++)
		{
			if (i == 0 && !Character.isJavaIdentifierStart(literalChars[i]))
			{
				buffer.append((int)literalChars[i]);
			}
			else if (i != 0 && !Character.isJavaIdentifierPart(literalChars[i]))
			{
				buffer.append((int)literalChars[i]);
			}
			else
			{
				buffer.append(literalChars[i]);
			}
		}
		
		return buffer.toString();
	}
	
	
	/**
	 * Checks if the input is a valid Java literal
	 * @param literal
	 * @author Gaganis Giorgos (gaganis@users.sourceforge.net) 
	 */
	private static boolean isValidLiteral(String literal)
	{
		boolean result = true;
		
		char[] literalChars = new char[literal.length()];
		literal.getChars(0, literalChars.length, literalChars, 0);
		
		for (int i = 0; i < literalChars.length; i++)
		{
			if (i == 0 && !Character.isJavaIdentifierStart(literalChars[i]))
			{
				result = false;
			}
			
			if (i != 0 && !Character.isJavaIdentifierPart(literalChars[i]))
			{
				result = false;
			}
		}
		
		return result;
	}

	
	/**
	 * Replaces DOS end of line (CRLF) with Unix end of line (LF).
	 * 
	 * @param text the text
	 * @return the text with CRLF replaced by LF; if no CRLF was found, the same object is returned.
	 */
	public static String replaceDosEOL(String text)
	{
		if (text == null || text.length() < 2)
		{
			return text;
		}
		
		int length = text.length();
		char[] chars = text.toCharArray();
		int r = 0;
		for (int i = 0; i < length; ++i)
		{
			char ch = chars[i];
			if (!(ch == '\r' && i + 1 < length && chars[i + 1] == '\n'))
			{
				if (r > 0)
				{
					chars[i - r] = ch;
				}
			}
			else
			{
				++r;
			}
		}

		return r > 0 ? new String(chars, 0, length - r) : text;
	}
}
