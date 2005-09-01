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
package net.sf.jasperreports.engine;

import java.io.Serializable;

import net.sf.jasperreports.engine.base.JRBaseReport;


/**
 * @author Teodor Danciu (teodord@users.sourceforge.net)
 * @version $Id$
 */
public class JasperReport extends JRBaseReport
{


	/**
	 *
	 */
	private static final long serialVersionUID = 10000;

	/**
	 *
	 */
	private String compilerClass = null;
	private Serializable compileData = null;


	/**
	 * Constructs a report by specifying the template report and compile information.
	 * 
	 * @param report the report template
	 * @param compilerClass the name of the class used to compile the report
	 * @param compileData the report/main dataset compile data
	 * @param expressionCollector instance used to collect expressions from the report design
	 * <p>
	 * The collector is used to fetch the generated expression IDs.
	 */
	public JasperReport(
		JRReport report,
		String compilerClass, 
		Serializable compileData,
		JRExpressionCollector expressionCollector
		)
	{
		super(report, expressionCollector);
		
		this.compilerClass = compilerClass;
		this.compileData = compileData;
	}

	/**
	 *
	 */
	public String getCompilerClass()
	{
		return this.compilerClass;
	}


	/**
	 *
	 */
	public Object getCompileData()
	{
		return this.compileData;
	}
}
