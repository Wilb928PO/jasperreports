/*
 * ============================================================================
 * GNU Lesser General Public License
 * ============================================================================
 *
 * JasperReports - Free Java report-generating library.
 * Copyright (C) 2001-2006 JasperSoft Corporation http://www.jaspersoft.com
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
 * 303 Second Street, Suite 450 North
 * San Francisco, CA 94107
 * http://www.jaspersoft.com
 */
package net.sf.jasperreports.engine.fill;

import net.sf.jasperreports.engine.Component;
import net.sf.jasperreports.engine.JRComponentElement;
import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.JRExpressionCollector;
import net.sf.jasperreports.engine.JRPrintElement;
import net.sf.jasperreports.engine.JRVisitor;
import net.sf.jasperreports.engine.component.ComponentKey;

/**
 * TODO component
 * 
 * @author Lucian Chirita (lucianc@users.sourceforge.net)
 * @version $Id: JRCrosstab.java 1741 2007-06-08 10:53:33Z lucianc $
 */
public class JRFillComponentElement extends JRFillElement implements JRComponentElement
{

	public JRFillComponentElement(JRBaseFiller filler, JRComponentElement element,
			JRFillObjectFactory factory)
	{
		super(filler, element, factory);
		// TODO Auto-generated constructor stub
	}

	public JRFillComponentElement(JRFillComponentElement element,
			JRFillCloneFactory factory)
	{
		super(element, factory);
		// TODO Auto-generated constructor stub
	}

	protected void evaluate(byte evaluation) throws JRException
	{
		// TODO Auto-generated method stub

	}

	protected JRPrintElement fill() throws JRException
	{
		// TODO Auto-generated method stub
		return null;
	}

	protected void resolveElement(JRPrintElement element, byte evaluation)
			throws JRException
	{
		// TODO Auto-generated method stub

	}

	protected void rewind() throws JRException
	{
		// TODO Auto-generated method stub

	}

	public void collectExpressions(JRExpressionCollector collector)
	{
		// TODO Auto-generated method stub

	}

	public void visit(JRVisitor visitor)
	{
		// TODO Auto-generated method stub

	}

	public JRFillCloneable createClone(JRFillCloneFactory factory)
	{
		// TODO Auto-generated method stub
		return null;
	}

	public Component getComponent()
	{
		// TODO Auto-generated method stub
		return null;
	}

	public ComponentKey getComponentKey()
	{
		return ((JRComponentElement) parent).getComponentKey();
	}

}
