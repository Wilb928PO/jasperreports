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

import java.io.IOException;
import java.util.List;

import net.sf.jasperreports.engine.JRAbstractObjectFactory;
import net.sf.jasperreports.engine.JRBox;
import net.sf.jasperreports.engine.JRChild;
import net.sf.jasperreports.engine.JRElement;
import net.sf.jasperreports.engine.JRElementGroup;
import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.JRExpressionCollector;
import net.sf.jasperreports.engine.JRFrame;
import net.sf.jasperreports.engine.JRPrintElement;
import net.sf.jasperreports.engine.base.JRBaseBox;
import net.sf.jasperreports.engine.base.JRBaseElementGroup;
import net.sf.jasperreports.engine.xml.JRXmlWriter;

/**
 * @author Lucian Chirita (lucianc@users.sourceforge.net)
 * @version $Id$
 */
public class JRFillFrame extends JRFillElement implements JRFrame
{
	protected final JRFrame parentFrame;
	protected final JRBox box;
	
	private JRFillFrameElements container;
	
	private JRTemplateFrame templateFrame;
	private JRTemplateFrame bottomTemplateFrame;
	private JRTemplateFrame topTemplateFrame;
	private JRTemplateFrame topBottomTemplateFrame;
	
	private boolean first;
	private boolean last;

	public JRFillFrame(JRBaseFiller filler, JRFrame frame, JRFillObjectFactory factory)
	{
		super(filler, frame, factory);
		
		parentFrame = frame;
		box = frame.getBox();
		
		container = new JRFillFrameElements(factory);
		
		templateFrame = new JRTemplateFrame(this);
	}

	protected void evaluate(byte evaluation) throws JRException
	{
		container.evaluate(evaluation);
	}

	protected void rewind() throws JRException
	{
		container.rewind();
	}

	protected boolean prepare(int availableStretchHeight, boolean isOverflow) throws JRException
	{
		super.prepare(availableStretchHeight, isOverflow);

		if (!isToPrint())
		{
			return false;
		}

		if (availableStretchHeight < getRelativeY() - getY() - getBandBottomY())
		{
			setToPrint(false);
			return true;
		}

		boolean finished = !container.willOverflow();
		if (isOverflow && finished && isAlreadyPrinted())
		{
			if (isPrintWhenDetailOverflows())
			{
				rewind();
				setReprinted(true);
			}
			else
			{
				setStretchHeight(getHeight());
				setToPrint(false);

				return false;
			}
		}
		
		first = !isOverflow || finished;
				
		int topPadding = 0;
		int bottomPadding = 0;
		if (box != null)
		{
			if (first)
			{
				topPadding = box.getTopPadding();
			}
			
			bottomPadding = box.getBottomPadding();
		}
		
		container.initFill();
		container.resetElements();
		container.prepareElements(availableStretchHeight - topPadding - bottomPadding, true);
		
		boolean willOverflow = container.willOverflow();
		if (willOverflow)
		{
			setStretchHeight(getHeight() + availableStretchHeight - getRelativeY() + getY() + getBandBottomY());
		}
		else
		{
			setStretchHeight(container.getStretchHeight());
		}
				
		last = !willOverflow;
		
		return willOverflow;
	}

	protected JRPrintElement fill() throws JRException
	{
		container.stretchElements();
		container.moveBandBottomElements();
		container.removeBlankElements();
		
		JRTemplatePrintFrame printFrame = new JRTemplatePrintFrame(getTemplate());
		printFrame.setX(getX());
		printFrame.setY(getRelativeY());
		printFrame.setWidth(getWidth());
		printFrame.setHeight(container.getStretchHeight());
		
		container.fillElements(printFrame);
		
		return printFrame;
	}

	protected JRTemplateFrame getTemplate()
	{
		JRTemplateFrame boxTemplate;
		
		if (first)
		{
			if (last)
			{
				boxTemplate = templateFrame;
			}
			else
			{
				if (bottomTemplateFrame == null)
				{
					JRBox bottomBox = new JRBaseBox(box, true, true, true, false, null);
					
					bottomTemplateFrame = new JRTemplateFrame(this);
					bottomTemplateFrame.setBox(bottomBox);
				}
				
				boxTemplate = bottomTemplateFrame;
			}
		}
		else
		{
			if (last)
			{
				if (topTemplateFrame == null)
				{
					JRBox topBox = new JRBaseBox(box, true, true, false, true, null);
					
					topTemplateFrame = new JRTemplateFrame(this);
					topTemplateFrame.setBox(topBox);
				}
				
				boxTemplate = topTemplateFrame;
			}
			else
			{
				if (topBottomTemplateFrame == null)
				{
					JRBox topBottomBox = new JRBaseBox(box, true, true, false, false, null);
					
					topBottomTemplateFrame = new JRTemplateFrame(this);
					topBottomTemplateFrame.setBox(topBottomBox);
				}
				
				boxTemplate = topBottomTemplateFrame;
			}
		}
		
		return boxTemplate;
	}

	protected void resolveElement(JRPrintElement element, byte evaluation) throws JRException
	{
		// nothing
	}

	public JRBox getBox()
	{
		return box;
	}

	public JRElement[] getElements()
	{
		return JRBaseElementGroup.getElements(getChildren());
	}
	
	public List getChildren()
	{
		return container.children;
	}

	public void collectExpressions(JRExpressionCollector collector)
	{
		collector.collect(this);
	}

	public JRChild getCopy(JRAbstractObjectFactory factory)
	{
		return factory.getFrame(this);
	}

	public void writeXml(JRXmlWriter writer) throws IOException
	{
		writer.writeFrame(this);
	}
	
	
	protected class JRFillFrameElements extends JRFillElementContainer
	{
		JRFillFrameElements(JRFillObjectFactory factory)
		{
			super(JRFillFrame.this.filler, new JRFrameElementGroup(), factory);
			initElements();
		}

		protected int getHeight()
		{
			return JRFillFrame.this.getHeight();
		}
	}
	
	
	protected class JRFrameElementGroup implements JRElementGroup
	{
		public List getChildren()
		{
			return parentFrame.getChildren();
		}

		public JRElementGroup getElementGroup()
		{
			return null;
		}

		public JRElement[] getElements()
		{
			return JRBaseElementGroup.getElements(getChildren());
		}

		public JRElement getElementByKey(String key)
		{
			// not used
			return null;
		}

		public JRChild getCopy(JRAbstractObjectFactory factory)
		{
			// not used
			return null;
		}

		public void writeXml(JRXmlWriter writer) throws IOException
		{
			// not used
		}
	}
}
