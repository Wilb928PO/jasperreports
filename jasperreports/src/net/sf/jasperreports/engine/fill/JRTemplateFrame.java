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

import net.sf.jasperreports.engine.JRBox;
import net.sf.jasperreports.engine.util.JRProperties;

/**
 * @author Lucian Chirita (lucianc@users.sourceforge.net)
 * @version $Id$
 */
public class JRTemplateFrame extends JRTemplateElement
{
	private static final long serialVersionUID = JRProperties.VERSION_SERIAL_UID;

	private JRBox box;

	protected JRTemplateFrame(JRFillCrosstab crosstab, JRFillCellContents cell)
	{
		super();
		
		setMode(crosstab.getMode());
		setForecolor(crosstab.getForecolor());
		if (cell.getBackcolor() != null)
		{
			setBackcolor(cell.getBackcolor());
		}
		else
		{
			setBackcolor(crosstab.getBackcolor());
		}
		setBox(cell.getBox());
	}

	public JRBox getBox()
	{
		return box;
	}

	protected void setBox(JRBox box)
	{
		this.box = box;
	}
}
