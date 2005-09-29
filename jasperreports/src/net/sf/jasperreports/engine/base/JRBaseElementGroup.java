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
package net.sf.jasperreports.engine.base;

import java.io.IOException;
import java.io.Serializable;
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
public class JRBaseElementGroup implements JRElementGroup, Serializable
{


	/**
	 *
	 */
	private static final long serialVersionUID = 10000;

	/**
	 *
	 */
	protected List children = new ArrayList();
	protected JRElementGroup elementGroup = null;


	/**
	 *
	 */
	protected JRBaseElementGroup()
	{
	}
	
	
	/**
	 *
	 */
	protected JRBaseElementGroup(JRElementGroup elementGrp, JRBaseObjectFactory factory)
	{
		factory.put(elementGrp, this);
		
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

		this.elementGroup = factory.getElementGroup(elementGrp.getElementGroup());
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
		JRElement[] elements = null;
		
		if (this.children != null)
		{
			List allElements = new ArrayList();
			Object child = null;
			JRElement[] childElementArray = null;
			for(int i = 0; i < this.children.size(); i++)
			{
				child = this.children.get(i);
				if (child instanceof JRElement)
				{
					allElements.add(child);
				}
				else if (child instanceof JRElementGroup)
				{
					childElementArray = ((JRElementGroup)child).getElements();
					if (childElementArray != null)
					{
						allElements.addAll( Arrays.asList(childElementArray) );
					}
				}
			}
			
			elements = new JRElement[allElements.size()];
			allElements.toArray(elements);
		}
		
		return elements;
	}


	/**
	 *
	 */
	public JRElement getElementByKey(String key)
	{
		JRElement element = null;
		
		if (key != null)
		{
			JRElement[] elements = this.getElements();
			
			if (elements != null)
			{
				int i = 0;
				while (element == null && i < elements.length)
				{
					if (key.equals(elements[i].getKey()))
					{
						element = elements[i];
					}
					i++;
				}
			}
		}
		
		return element;
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
