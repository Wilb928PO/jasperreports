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

import java.util.Map;

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
	private Object compileData = null;
	
	/**
	 * Map containing compiled data per sub dataset name.
	 */
	private Map datasetCompileData;


	/**
	 * Constructs a report by specifying the template report and compile information.
	 * 
	 * @param report the report template
	 * @param compilerClass the name of the class used to compile the report
	 * @param compileData the report/main dataset compile data
	 * @param datasetCompileData the map of sub dataset compile data, indexed by dataset name
	 */
	public JasperReport(
		JRReport report,
		String compilerClass, 
		Object compileData,
		Map datasetCompileData
		)
	{
		super(report);
		
		this.compilerClass = compilerClass;
		this.compileData = compileData;
		this.datasetCompileData = datasetCompileData;
	}

	
	/**
	 * Constructs a report by specifying the template report and compile information.
	 * This constructor should be used only when there are no sub datasets.
	 * 
	 * @param report the report template
	 * @param compilerClass the name of the class used to compile the report
	 * @param compileData the main report compile data
	 */
	public JasperReport(
			JRReport report,
			String compilerClass, 
			Object compileData)
	{
		this(report, compilerClass, compileData, null);
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


	/**
	 * Returns the compile data for a dataset.
	 * 
	 * @param dataset the dataset
	 * @return the data saved when the report was compiled
	 */
	public Object getDatasetCompileData(JRDataset dataset)
	{
		Object data;
		if (dataset.isMainDataset())
		{
			data = this.compileData;
		}
		else
		{
			data = datasetCompileData.get(dataset.getName());
		}
		
		return data; 
	}

}
