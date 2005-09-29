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
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import net.sf.jasperreports.engine.JRAbstractObjectFactory;
import net.sf.jasperreports.engine.JRChild;
import net.sf.jasperreports.engine.JRElement;
import net.sf.jasperreports.engine.JRElementGroup;
import net.sf.jasperreports.engine.xml.JRXmlWriter;


/**
 * @author Teodor Danciu (teodord@users.sourceforge.net)
 * @version $Id$
 */
public class JRFillElementGroup implements JRElementGroup
{


	/**
	 *
	 */
	protected List children = new ArrayList();
	protected JRElementGroup elementGroup = null;

	/**
	 *
	 */
	protected JRFillElement[] elements = null;

	/**
	 *
	 */
	private JRElement topElementInGroup = null;
	private JRElement bottomElementInGroup = null;
	private int stretchHeightDiff = 0;


	/**
	 *
	 */
	protected JRFillElementGroup(
		JRElementGroup elementGrp, 
		JRFillObjectFactory factory
		)
	{
		factory.put(elementGrp, this);

		if (elementGrp != null)
		{
			/*   */
			List list = elementGrp.getChildren();
			if (list != null && list.size() > 0)
			{
				for(int i = 0; i < list.size(); i++)
				{
					JRChild child = (JRChild)list.get(i);
					child = child.getCopy(factory);
					children.add(child);
				}
			}
	
			/*   */
			this.getElements();
	
			this.elementGroup = factory.getElementGroup(elementGrp.getElementGroup());
		}
	}


	/**
	 *
	 */
	public List getChildren()
	{
		return this.children;
	}


	/**
	 *
	 */
	public JRElementGroup getElementGroup()
	{
		return this.elementGroup;
	}


	/**
	 *
	 */
	public JRElement[] getElements()
	{
		if (this.elements == null)
		{
			if (this.children != null)
			{
				List allElements = new ArrayList();
				Object child = null;
				JRElement[] childElementArray = null;
				for(int i = 0; i < this.children.size(); i++)
				{
					child = this.children.get(i);
					if (child instanceof JRFillElement)
					{
						allElements.add(child);
					}
					else if (child instanceof JRFillElementGroup)
					{
						childElementArray = ((JRFillElementGroup)child).getElements();
						if (childElementArray != null)
						{
							allElements.addAll( Arrays.asList(childElementArray) );
						}
					}
				}
				
				this.elements = new JRFillElement[allElements.size()];
				allElements.toArray(this.elements);
			}
		}
		
		return this.elements;
	}


	/**
	 *
	 */
	public JRElement getElementByKey(String key)
	{
		return null;
	}


	/**
	 *
	 */
	protected void reset()
	{
		topElementInGroup = null;
	}


	/**
	 *
	 */
	protected int getStretchHeightDiff()
	{
		if (topElementInGroup == null)
		{
			stretchHeightDiff = 0;
			
			setTopBottomElements();

			JRElement[] allElements = getElements();

			if (allElements != null && allElements.length > 0)
			{
				JRFillElement topElem = null;
				JRFillElement bottomElem = null;

				for(int i = 0; i < allElements.length; i++)
				{
					JRFillElement element = (JRFillElement)allElements[i];
					//if (element != this && element.isToPrint())
					if (element.isToPrint())
					{
						if (
							topElem == null ||
							(topElem != null &&
							element.getRelativeY() + element.getStretchHeight() <
							topElem.getRelativeY() + topElem.getStretchHeight())
							)
						{
							topElem = element;
						}

						if (
							bottomElem == null ||
							(bottomElem != null &&
							element.getRelativeY() + element.getStretchHeight() >
							bottomElem.getRelativeY() + bottomElem.getStretchHeight())
							)
						{
							bottomElem = element;
						}
					}
				}

				if (topElem != null)
				{
					stretchHeightDiff = 
						bottomElem.getRelativeY() + bottomElem.getStretchHeight() - topElem.getRelativeY() -
						(bottomElementInGroup.getY() + bottomElementInGroup.getHeight() - topElementInGroup.getY());
				}

				if (stretchHeightDiff < 0)
				{
					stretchHeightDiff = 0;
				}
			}
		}
		
		return stretchHeightDiff;
	}


	/**
	 *
	 */
	private void setTopBottomElements()
	{
		JRElement[] allElements = getElements();
	
		if (allElements != null && allElements.length > 0)
		{
			for(int i = 0; i < allElements.length; i++)
			{
				if (
					topElementInGroup == null ||
					(topElementInGroup != null &&
					allElements[i].getY() + allElements[i].getHeight() <
					topElementInGroup.getY() + topElementInGroup.getHeight())
					)
				{
					topElementInGroup = allElements[i];
				}

				if (
					bottomElementInGroup == null ||
					(bottomElementInGroup != null &&
					allElements[i].getY() + allElements[i].getHeight() >
					bottomElementInGroup.getY() + bottomElementInGroup.getHeight())
					)
				{
					bottomElementInGroup = allElements[i];
				}
			}
		}
	}


	/**
	 *
	 */
	public JRChild getCopy(JRAbstractObjectFactory factory)
	{
		return factory.getElementGroup(this);
	}


	/**
	 *
	 */
	public void writeXml(JRXmlWriter xmlWriter) throws IOException
	{
		xmlWriter.writeElementGroup(this);
	}


}
