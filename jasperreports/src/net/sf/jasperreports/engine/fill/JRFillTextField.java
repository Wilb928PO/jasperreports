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

import java.text.DateFormat;
import java.text.DecimalFormat;
import java.text.Format;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.Map;

import net.sf.jasperreports.engine.JRAbstractObjectFactory;
import net.sf.jasperreports.engine.JRChild;
import net.sf.jasperreports.engine.JRElement;
import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.JRExpression;
import net.sf.jasperreports.engine.JRExpressionCollector;
import net.sf.jasperreports.engine.JRGroup;
import net.sf.jasperreports.engine.JRPrintElement;
import net.sf.jasperreports.engine.JRPrintText;
import net.sf.jasperreports.engine.JRTextField;
import net.sf.jasperreports.engine.xml.JRXmlWriter;


/**
 * @author Teodor Danciu (teodord@users.sourceforge.net)
 * @version $Id$
 */
public class JRFillTextField extends JRFillTextElement implements JRTextField
{


	/**
	 *
	 */
	private JRGroup evaluationGroup = null;

	/**
	 *
	 */
	private Format format = null;
	private boolean isValueRepeating = false;
	private String anchorName = null;
	private String hyperlinkReference = null;
	private String hyperlinkAnchor = null;
	private Integer hyperlinkPage = null;


	/**
	 *
	 */
	protected JRFillTextField(
		JRBaseFiller filler,
		JRTextField textField, 
		JRFillObjectFactory factory
		)
	{
		super(filler, textField, factory);
		
		JRExpression expression = getExpression();
		if (expression != null)
		{
			Class expressionClass = expression.getValueClass();
			if (
				java.util.Date.class.isAssignableFrom(expressionClass)
				|| java.lang.Number.class.isAssignableFrom(expressionClass)
				)
			{
				filler.formattedTextFields.add(this);
			}
		}
		
		evaluationGroup = factory.getGroup(textField.getEvaluationGroup());
	}


	/**
	 *
	 */
	public boolean isStretchWithOverflow()
	{
		return ((JRTextField)parent).isStretchWithOverflow();
	}

	/**
	 *
	 */
	public void setStretchWithOverflow(boolean isStretchWithOverflow)
	{
	}

	/**
	 *
	 */
	public byte getEvaluationTime()
	{
		return ((JRTextField)parent).getEvaluationTime();
	}
		
	/**
	 *
	 */
	public String getPattern()
	{
		return ((JRTextField)parent).getPattern();
	}
		
	/**
	 *
	 */
	public void setPattern(String pattern)
	{
	}
		
	/**
	 *
	 */
	public boolean isBlankWhenNull()
	{
		return ((JRTextField)parent).isBlankWhenNull();
	}

	/**
	 *
	 */
	public void setBlankWhenNull(boolean isBlank)
	{
	}

	/**
	 *
	 */
	public byte getHyperlinkType()
	{
		return ((JRTextField)parent).getHyperlinkType();
	}
		
	/**
	 *
	 */
	public byte getHyperlinkTarget()
	{
		return ((JRTextField)parent).getHyperlinkTarget();
	}
		
	/**
	 *
	 */
	public JRGroup getEvaluationGroup()
	{
		return evaluationGroup;
	}
		
	/**
	 *
	 */
	public JRExpression getExpression()
	{
		return ((JRTextField)parent).getExpression();
	}

	/**
	 *
	 */
	public JRExpression getAnchorNameExpression()
	{
		return ((JRTextField)parent).getAnchorNameExpression();
	}

	/**
	 *
	 */
	public JRExpression getHyperlinkReferenceExpression()
	{
		return ((JRTextField)parent).getHyperlinkReferenceExpression();
	}

	/**
	 *
	 */
	public JRExpression getHyperlinkAnchorExpression()
	{
		return ((JRTextField)parent).getHyperlinkAnchorExpression();
	}

	/**
	 *
	 */
	public JRExpression getHyperlinkPageExpression()
	{
		return ((JRTextField)parent).getHyperlinkPageExpression();
	}

		
	/**
	 *
	 */
	protected String getAnchorName()
	{
		return anchorName;
	}

	/**
	 *
	 */
	protected String getHyperlinkReference()
	{
		return hyperlinkReference;
	}

	/**
	 *
	 */
	protected String getHyperlinkAnchor()
	{
		return hyperlinkAnchor;
	}

	/**
	 *
	 */
	protected Integer getHyperlinkPage()
	{
		return hyperlinkPage;
	}
		

	/**
	 *
	 */
	protected JRTemplateText getJRTemplateText()
	{
		if (template == null)
		{
			template = new JRTemplateText((JRTextField)parent, getFont());
		}
		
		return (JRTemplateText)template;
	}


	/**
	 *
	 */
	protected void evaluate(
		byte evaluation
		) throws JRException
	{
		reset();
		
		evaluatePrintWhenExpression(evaluation);

		if (
			(isPrintWhenExpressionNull() ||
			(!isPrintWhenExpressionNull() && 
			isPrintWhenTrue()))
			)
		{
			if (getEvaluationTime() == JRExpression.EVALUATION_TIME_NOW)
			{
				evaluateText(evaluation);
			}
		}
	}


	/**
	 *
	 */
	protected void evaluateText(
		byte evaluation
		) throws JRException
	{
		Object textFieldValue = evaluateExpression(getExpression(), evaluation);

		if (textFieldValue == null)
		{
			if (isBlankWhenNull())
			{
				textFieldValue = "";
			}
		}
		else
		{
			if (format != null)
			{
				textFieldValue = format.format(textFieldValue);
			}
		}

/*
		String newRawText = 
			JRStringUtil.treatNewLineChars(
				String.valueOf(textFieldValue)
				);
*/		
		String newRawText = String.valueOf(textFieldValue);
		String oldRawText = getRawText();

		setRawText(newRawText);
		setTextStart(0);
		setTextEnd(0);

		if (
			(oldRawText == null && newRawText == null) ||
			(oldRawText != null && oldRawText.equals(newRawText))
			)
		{
			isValueRepeating = true;
		}
		else
		{
			isValueRepeating = false;
		}

		anchorName = (String) evaluateExpression(getAnchorNameExpression(), evaluation);
		hyperlinkReference = (String) evaluateExpression(getHyperlinkReferenceExpression(), evaluation);
		hyperlinkAnchor = (String) evaluateExpression(getHyperlinkAnchorExpression(), evaluation);
		hyperlinkPage = (Integer) evaluateExpression(getHyperlinkPageExpression(), evaluation);
	}


	/**
	 *
	 */
	protected boolean prepare(
		int availableStretchHeight,
		boolean isOverflow
		)
	{
		boolean willOverflow = false;

		super.prepare(availableStretchHeight, isOverflow);

		if (!isToPrint())
		{
			return willOverflow;
		}

		boolean isToPrint = true;
		boolean isReprinted = false;

		if (getEvaluationTime() == JRExpression.EVALUATION_TIME_NOW)
		{
			if (isOverflow)
			{
				if (getPositionType() == JRElement.POSITION_TYPE_FIX_RELATIVE_TO_BOTTOM)
				{
					// the content of the band bottom text fields is not
					// consumed during overflows, because they only appear on the last overflow
					setTextStart(0);
					setTextEnd(0);
				}

				if (
					getTextEnd() >= getText().length()
					|| !isStretchWithOverflow()
					|| getRotation() != ROTATION_NONE
					)
				{
					// there is no more text left in the text field to overflow
					// on the new page, or the text field is not stretchable
					
					if (isAlreadyPrinted())
					{
						// the text field has already displayed all its content
						// on the previous page even if it not stretchable
						
						if (isPrintWhenDetailOverflows())
						{
							// the whole content is reprinted
							setTextStart(0);
							setTextEnd(0);

							isReprinted = true;
						}
						else
						{
							isToPrint = false;
						}
					}
					else
					{
						// the text field did not print on the previous page.
						// we let it go since it is its first time anyway
					}
				}
				else
				{
					// there is text left inside the stretchable text field.
					// we simply let it go
				}

				if (
					isToPrint &&
					isPrintWhenExpressionNull() &&
					!isPrintRepeatedValues() &&
					isValueRepeating
					)
				{
					isToPrint = false; // FIXME, shouldn't we test for the first whole band and the other exceptions to the rule?
				}
			}
			else
			{
				if (
					isPrintWhenExpressionNull() &&
					!isPrintRepeatedValues() &&
					isValueRepeating
					)
				{
					if (
						( !isPrintInFirstWholeBand() || !getBand().isNewPageColumn() ) &&
						( getPrintWhenGroupChanges() == null || !getBand().isNewGroup(getPrintWhenGroupChanges()) )
						)
					{
						isToPrint = false;
					}
				}
			}

			if (isToPrint)
			{
				if (availableStretchHeight >= getRelativeY() - getY() - getBandBottomY())
				{
					// the available vertical space is sufficient

					if (
						getTextEnd() < getText().length() 
						|| getTextEnd() == 0
						)
					{
						// there is still some text left in the text field or
						// the text field is empty

						if (
							isStretchWithOverflow()
							&& getRotation() == ROTATION_NONE
							)
						{
							// the text field is allowed to stretch downwards in order to
							// display all its content

							chopTextElement(availableStretchHeight - getRelativeY() + getY() + getBandBottomY());
							if (getTextEnd() < getText().length())// - 1)
							{
								// even after the current chop operation there is some text left
								// that will overflow on the next page

								willOverflow = true;
							}
						}
						else
						{
							// the text field is not allowed to stretch downwards in order to
							// display all its content

							chopTextElement(0);
						}
					}
					else
					{
						// there is no text left in the text field and the text field was not empty

						// this section is probably unreachable since it is most likely that
						// the isToPrint flag was already set on false in the code above.
						isToPrint = false;
					}
				}
				else
				{
					// the available vertical space is not sufficient

					// no matter if there is some text left inside or not,
					// there was an explicit request to display it, 
					// even if we are on an overflow.
					// since there is no space available, it will overflow
					
					isToPrint = false;
					willOverflow = true;
				}
			}

			if (
				isToPrint &&
				isRemoveLineWhenBlank() &&	//FIXME if the line won't be removed due to other elements 
				getText().substring(		// present on that line, the background does not appear
					getTextStart(),
					getTextEnd()
					).trim().length() == 0
				)
			{
				isToPrint = false;
			}
		}
		else
		{
			if (isOverflow && isAlreadyPrinted())
			{
				if (isPrintWhenDetailOverflows())
				{
					isReprinted = true;
				}
				else
				{
					isToPrint = false;
				}
			}
			
			if (
				isToPrint && 
				availableStretchHeight < this.getRelativeY() - this.getY() - this.getBandBottomY()
				)
			{
				isToPrint = false;
				willOverflow = true;
			}
		}

		setToPrint(isToPrint);
		setReprinted(isReprinted);

		return willOverflow;
	}


	/**
	 *
	 */
	protected JRPrintElement fill()
	{
		JRPrintText text = null;

		text = new JRTemplatePrintText(getJRTemplateText());
		text.setX(getX());
		text.setY(getRelativeY());
		if (getRotation() == ROTATION_NONE)
		{
			text.setHeight(getStretchHeight());
		}
		else
		{
			text.setHeight(getHeight());
		}
		text.setRunDirection(getRunDirection());

		switch (getEvaluationTime())
		{
			case JRExpression.EVALUATION_TIME_REPORT :
			{
				filler.reportBoundElements.put(text, this);
				break;
			}
			case JRExpression.EVALUATION_TIME_PAGE :
			{
				filler.pageBoundElements.put(text, this);
				break;
			}
			case JRExpression.EVALUATION_TIME_COLUMN :
			{
				filler.columnBoundElements.put(text, this);
				break;
			}
			case JRExpression.EVALUATION_TIME_GROUP :
			{
				Map specificGroupBoundTexts = (Map)filler.groupBoundElements.get(getEvaluationGroup().getName());
				specificGroupBoundTexts.put(text, this);
				break;
			}
			case JRExpression.EVALUATION_TIME_BAND :
			{
				band.boundElements.put(text, this);
				break;
			}
			case JRExpression.EVALUATION_TIME_NOW :
			default :
			{
				copy(text);
			}
		}

		return text;
	}


	/**
	 *
	 */
	protected void copy(JRPrintText text)
	{
		text.setLineSpacingFactor(getLineSpacingFactor());
		text.setLeadingOffset(getLeadingOffset());
		text.setTextHeight(getTextHeight());
		//FIXME rotation and run direction?

		text.setText(textChopper.chop(this, getTextStart(), getTextEnd()));

		text.setAnchorName(getAnchorName());
		text.setHyperlinkReference(getHyperlinkReference());
		text.setHyperlinkAnchor(getHyperlinkAnchor());
		text.setHyperlinkPage(getHyperlinkPage());
	}
	
	
	/**
	 *
	 */
	protected void setFormat()
	{
		String pattern = getPattern();
		Class expressionClass = getExpression().getValueClass();
		if (java.util.Date.class.isAssignableFrom(expressionClass))
		{
			format = DateFormat.getDateTimeInstance(DateFormat.SHORT, DateFormat.SHORT, filler.getLocale());
			if (
				pattern != null && pattern.trim().length() > 0
				&& format instanceof SimpleDateFormat
				)
			{
				((SimpleDateFormat)format).applyPattern(pattern);
			}
		}
		else if (java.lang.Number.class.isAssignableFrom(expressionClass))
		{
			if (pattern != null && pattern.trim().length() > 0)
			{
				format = NumberFormat.getNumberInstance(filler.getLocale());
				if (format instanceof DecimalFormat)
				{
					((DecimalFormat)format).applyPattern(pattern);
				}
			}
		}
	}


	/**
	 *
	 */
	public JRChild getCopy(JRAbstractObjectFactory factory)
	{
		return factory.getTextField(this);
	}

	/**
	 *
	 */
	public void collectExpressions(JRExpressionCollector collector)
	{
		collector.collect(this);
	}

	/**
	 *
	 */
	public void writeXml(JRXmlWriter xmlWriter)
	{
		xmlWriter.writeTextField(this);
	}


	protected void resolveElement(JRPrintElement element, byte evaluation) throws JRException
	{
		evaluateText(evaluation);

		chopTextElement(0);

		copy((JRPrintText) element);
	}
}
