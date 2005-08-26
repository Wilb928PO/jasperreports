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


/**
 * @author Teodor Danciu (teodord@users.sourceforge.net)
 * @version $Id$
 */
public interface JRReport extends JRDefaultFontProvider
{

	
	/**
	 *
	 */
	public static final String LANGUAGE_JAVA = "java";

	/**
	 *
	 */
	public static final byte PRINT_ORDER_VERTICAL = 1;
	public static final byte PRINT_ORDER_HORIZONTAL = 2;

	/**
	 *
	 */
	public static final byte ORIENTATION_PORTRAIT = 1;
	public static final byte ORIENTATION_LANDSCAPE = 2;

	/**
	 *
	 */
	public static final byte WHEN_NO_DATA_TYPE_NO_PAGES = 1;
	public static final byte WHEN_NO_DATA_TYPE_BLANK_PAGE = 2;
	public static final byte WHEN_NO_DATA_TYPE_ALL_SECTIONS_NO_DETAIL = 3;

	/**
	 * Return NULL when a resource is missing.
	 */
	public static final byte WHEN_RESOURCE_MISSING_TYPE_NULL = JRDataset.WHEN_RESOURCE_MISSING_TYPE_NULL;
	/**
	 * Return empty string when a resource is missing.
	 */
	public static final byte WHEN_RESOURCE_MISSING_TYPE_EMPTY = JRDataset.WHEN_RESOURCE_MISSING_TYPE_EMPTY;
	/**
	 * Return the key when a resource is missing.
	 */
	public static final byte WHEN_RESOURCE_MISSING_TYPE_KEY = JRDataset.WHEN_RESOURCE_MISSING_TYPE_KEY;
	/**
	 * Throw an exception when a resource is missing.
	 */
	public static final byte WHEN_RESOURCE_MISSING_TYPE_ERROR = JRDataset.WHEN_RESOURCE_MISSING_TYPE_ERROR;

	
	/**
	 *
	 */
	public String getName();

	/**
	 *
	 */
	public String getLanguage();
		
	/**
	 *
	 */
	public int getColumnCount();
		
	/**
	 *
	 */
	public byte getPrintOrder();
		
	/**
	 *
	 */
	public int getPageWidth();
		
	/**
	 *
	 */
	public int getPageHeight();
		
	/**
	 *
	 */
	public byte getOrientation();
		
	/**
	 *
	 */
	public byte getWhenNoDataType();
		
	/**
	 *
	 */
	public void setWhenNoDataType(byte whenNoDataType);
		
	/**
	 *
	 */
	public int getColumnWidth();
		
	/**
	 *
	 */
	public int getColumnSpacing();
		
	/**
	 *
	 */
	public int getLeftMargin();
		
	/**
	 *
	 */
	public int getRightMargin();
		
	/**
	 *
	 */
	public int getTopMargin();
		
	/**
	 *
	 */
	public int getBottomMargin();
		
	/**
	 *
	 */
	public boolean isTitleNewPage();
		
	/**
	 *
	 */
	public boolean isSummaryNewPage();
		
	/**
	 *
	 */
	public boolean isFloatColumnFooter();
		
	/**
	 *
	 */
	public String getScriptletClass();

	/**
	 *
	 */
	public String getResourceBundle();

	/**
	 *
	 */
	public String[] getPropertyNames();

	/**
	 *
	 */
	public String getProperty(String name);

	/**
	 *
	 */
	public void setProperty(String name, String value);

	/**
	 *
	 */
	public void removeProperty(String name);

	/**
	 *
	 */
	public String[] getImports();

	/**
	 *
	 */
	public JRReportFont[] getFonts();

	/**
	 *
	 */
	public JRParameter[] getParameters();

	/**
	 *
	 */
	public JRQuery getQuery();

	/**
	 *
	 */
	public JRField[] getFields();

	/**
	 *
	 */
	public JRVariable[] getVariables();

	/**
	 *
	 */
	public JRGroup[] getGroups();

	/**
	 *
	 */
	public JRBand getBackground();

	/**
	 *
	 */
	public JRBand getTitle();

	/**
	 *
	 */
	public JRBand getPageHeader();

	/**
	 *
	 */
	public JRBand getColumnHeader();

	/**
	 *
	 */
	public JRBand getDetail();

	/**
	 *
	 */
	public JRBand getColumnFooter();

	/**
	 *
	 */
	public JRBand getPageFooter();

	/**
	 *
	 */
	public JRBand getLastPageFooter();

	/**
	 *
	 */
	public JRBand getSummary();

	/**
	 * Returns the resource missing handling type.
	 */
	public byte getWhenResourceMissingType();
		
	/**
	 * Sets the resource missing handling type.
	 * @param whenResourceMissingType the resource missing handling type
	 */
	public void setWhenResourceMissingType(byte whenResourceMissingType);

	
	/**
	 * Returns the main report dataset.
	 * <p>
	 * The main report dataset consists of all parameters, fields, variables and groups of the report.
	 * 
	 * @return the main report dataset
	 */
	public JRDataset getMainDataset();
	
	
	/**
	 * Returns the datasets of this report.
	 * 
	 * @return the datasets of this report
	 */
	public JRDataset[] getDatasets();
}
