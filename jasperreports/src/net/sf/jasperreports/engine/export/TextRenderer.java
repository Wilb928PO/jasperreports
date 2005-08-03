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
package net.sf.jasperreports.engine.export;

import java.awt.Graphics2D;
import java.awt.font.LineBreakMeasurer;
import java.awt.font.TextLayout;
import java.text.AttributedCharacterIterator;
import java.text.AttributedString;
import java.util.StringTokenizer;

import net.sf.jasperreports.engine.JRAlignment;
import net.sf.jasperreports.engine.JRTextElement;
import net.sf.jasperreports.engine.util.JRStyledText;


/**
 * @author Teodor Danciu (teodord@users.sourceforge.net)
 * @version $Id$
 */
public class TextRenderer
{

	private Graphics2D grx = null;
	private int x = 0;
	private int y = 0;
	private int topPadding = 0;
	private int leftPadding = 0;
	private float formatWidth = 0;
	private float verticalOffset = 0;
	private float floatLineSpacing = 0;
	private int maxHeight = 0;
	private float drawPosY = 0;
	private float drawPosX = 0;
	private boolean isMaxHeightReached = false;
	private byte horizontalAlignment = 0;
	
	
	/**
	 * 
	 */
	public void render(
		Graphics2D initGrx,
		int initX,
		int initY,
		int initWidth,
		int initHeight,
		int initTopPadding,
		int initLeftPadding,
		int initBottomPadding,
		int initRightPadding,
		float initTextHeight,
		byte initHorizontalAlignment,
		byte initVerticalAlignment,
		byte initLineSpacing,
		JRStyledText styledText,
		String allText
		)
	{
		/*   */
		initialize(
			initGrx, 
			initX, 
			initY, 
			initWidth, 
			initHeight, 
			initTopPadding,
			initLeftPadding,
			initBottomPadding,
			initRightPadding,
			initTextHeight, 
			initHorizontalAlignment, 
			initVerticalAlignment, 
			initLineSpacing
			);
		
		AttributedCharacterIterator allParagraphs = styledText.getAttributedString().getIterator();

		int tokenPosition = 0;
		int lastParagraphStart = 0;
		String lastParagraphText = null;

		StringTokenizer tkzer = new StringTokenizer(allText, "\n", true);

		while(tkzer.hasMoreTokens() && !isMaxHeightReached) 
		{
			String token = tkzer.nextToken();

			if ("\n".equals(token))
			{
				renderParagraph(allParagraphs, lastParagraphStart, lastParagraphText);

				lastParagraphStart = tokenPosition;
				lastParagraphText = null;
			}
			else
			{
				lastParagraphStart = tokenPosition;
				lastParagraphText = token;
			}

			tokenPosition += token.length();
		}

		if (!isMaxHeightReached && lastParagraphStart < allText.length())
		{
			renderParagraph(allParagraphs, lastParagraphStart, lastParagraphText);
		}
	}


	/**
	 * 
	 */
	private void initialize(
		Graphics2D initGrx,
		int initX,
		int initY,
		int initWidth,
		int initHeight,
		int initTopPadding,
		int initLeftPadding,
		int initBottomPadding,
		int initRightPadding,
		float initTextHeight,
		byte initHorizontalAlignment,
		byte initVerticalAlignment,
		byte initLineSpacing
		)
	{
		this.grx = initGrx;
		
		this.horizontalAlignment = initHorizontalAlignment;

		verticalOffset = 0f;
		switch (initVerticalAlignment)
		{
			case JRAlignment.VERTICAL_ALIGN_TOP :
			{
				verticalOffset = 0f;
				break;
			}
			case JRAlignment.VERTICAL_ALIGN_MIDDLE :
			{
				verticalOffset = (initHeight - initTextHeight) / 2f;
				break;
			}
			case JRAlignment.VERTICAL_ALIGN_BOTTOM :
			{
				verticalOffset = initHeight - initTextHeight;
				break;
			}
			default :
			{
				verticalOffset = 0f;
			}
		}

		floatLineSpacing = 1f;
		switch (initLineSpacing)
		{
			case JRTextElement.LINE_SPACING_SINGLE :
			{
				floatLineSpacing = 1f;
				break;
			}
			case JRTextElement.LINE_SPACING_1_1_2 :
			{
				floatLineSpacing = 1.5f;
				break;
			}
			case JRTextElement.LINE_SPACING_DOUBLE :
			{
				floatLineSpacing = 2f;
				break;
			}
			default :
			{
				floatLineSpacing = 1f;
			}
		}

		this.x = initX;
		this.y = initY;
		this.topPadding = initTopPadding;
		this.leftPadding = initLeftPadding;
		formatWidth = initWidth - initLeftPadding - initRightPadding;
		formatWidth = formatWidth < 0 ? 0 : formatWidth;
		maxHeight = initHeight - initTopPadding - initBottomPadding;
		maxHeight = maxHeight < 0 ? 0 : maxHeight;

		drawPosY = 0;
		drawPosX = 0;
	
		isMaxHeightReached = false;
	}
	
	/**
	 * 
	 */
	private void renderParagraph(
		AttributedCharacterIterator allParagraphs,
		int lastParagraphStart,
		String lastParagraphText
		)
	{
		AttributedCharacterIterator paragraph = null;
		
		if (lastParagraphText == null)
		{
			paragraph = 
				new AttributedString(
					" ",
					new AttributedString(
						allParagraphs, 
						lastParagraphStart, 
						lastParagraphStart + 1
						).getIterator().getAttributes()
					).getIterator();
		}
		else
		{
			paragraph = 
				new AttributedString(
					allParagraphs, 
					lastParagraphStart, 
					lastParagraphStart + lastParagraphText.length()
					).getIterator();
		}

		LineBreakMeasurer lineMeasurer = new LineBreakMeasurer(paragraph, grx.getFontRenderContext());
	
		while (lineMeasurer.getPosition() < paragraph.getEndIndex() && !isMaxHeightReached)
		{
			//eugene fix - start
			int startIndex = lineMeasurer.getPosition();
			//eugene fix - end

			TextLayout layout = lineMeasurer.nextLayout(formatWidth);

			//eugene fix - start
			AttributedString tmpText = 
				new AttributedString(
					paragraph, 
					startIndex, 
					startIndex + layout.getCharacterCount()
					);
			layout = new TextLayout(tmpText.getIterator(), grx.getFontRenderContext());
			//eugene fix - end

			drawPosY += layout.getLeading() + floatLineSpacing * layout.getAscent();

			if (drawPosY + layout.getDescent() <= maxHeight)
			{
				switch (horizontalAlignment)
				{
					case JRAlignment.HORIZONTAL_ALIGN_JUSTIFIED :
					{
						if (layout.isLeftToRight())
						{
							drawPosX = 0;
						}
						else
						{
							drawPosX = formatWidth - layout.getAdvance();
						}
						if (lineMeasurer.getPosition() < paragraph.getEndIndex())
						{
							layout = layout.getJustifiedLayout(formatWidth);
						}

						break;
					}
					case JRAlignment.HORIZONTAL_ALIGN_RIGHT :
					{
						drawPosX = formatWidth - layout.getAdvance();
						break;
					}
					case JRAlignment.HORIZONTAL_ALIGN_CENTER :
					{
						drawPosX = (formatWidth - layout.getAdvance()) / 2;
						break;
					}
					case JRAlignment.HORIZONTAL_ALIGN_LEFT :
					default :
					{
						drawPosX = 0;
					}
				}

				draw(layout);
				drawPosY += layout.getDescent();
			}
			else
			{
				drawPosY -= layout.getLeading() + floatLineSpacing * layout.getAscent();
				isMaxHeightReached = true;
			}
		}
	}
	
	/**
	 * 
	 */
	public float getTextHeight()
	{
		return drawPosY + 1;
	}
	
	/**
	 * 
	 */
	public void draw(TextLayout layout)
	{
		layout.draw(
			grx,
			drawPosX + x + leftPadding,
			drawPosY + y + topPadding + verticalOffset
			);
	}
	
}
