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
package net.sf.jasperreports.engine.design;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import net.sf.jasperreports.engine.JRAbstractObjectFactory;
import net.sf.jasperreports.engine.JRBox;
import net.sf.jasperreports.engine.JRChild;
import net.sf.jasperreports.engine.JRElement;
import net.sf.jasperreports.engine.JRElementGroup;
import net.sf.jasperreports.engine.JRExpressionCollector;
import net.sf.jasperreports.engine.JRFrame;
import net.sf.jasperreports.engine.base.JRBaseElementGroup;
import net.sf.jasperreports.engine.xml.JRXmlWriter;

/**
 * @author Lucian Chirita (lucianc@users.sourceforge.net)
 * @version $Id$
 */
public class JRDesignFrame extends JRDesignElement implements JRFrame
{
	protected JRBox box;
	protected List children;

	public JRDesignFrame()
	{
		super();
		
		children = new ArrayList();
		
		mode = JRElement.MODE_TRANSPARENT;
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

	public JRBox getBox()
	{
		return box;
	}

	public void setBox(JRBox box)
	{
		this.box = box;
	}

	public JRElement[] getElements()
	{
		return JRBaseElementGroup.getElements(children);
	}

	public void addElement(JRElement element)
	{
		children.add(element);
	}
	
	public void removeElement(JRElement element)
	{
		children.remove(element);
	}

	public void addElementGroup(JRElementGroup group)
	{
		children.add(group);
	}
	
	public void removeElementGroup(JRElementGroup group)
	{
		children.remove(group);
	}

	public List getChildren()
	{
		return children;
	}
}
