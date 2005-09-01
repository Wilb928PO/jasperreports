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

import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;

import net.sf.jasperreports.engine.JRBand;
import net.sf.jasperreports.engine.JRDataset;
import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.JRExpressionCollector;
import net.sf.jasperreports.engine.JRField;
import net.sf.jasperreports.engine.JRGroup;
import net.sf.jasperreports.engine.JRParameter;
import net.sf.jasperreports.engine.JRQuery;
import net.sf.jasperreports.engine.JRReportFont;
import net.sf.jasperreports.engine.JRVariable;
import net.sf.jasperreports.engine.base.JRBaseReport;


/**
 * @author Teodor Danciu (teodord@users.sourceforge.net)
 * @version $Id$
 */
public class JasperDesign extends JRBaseReport
{
	/** Property change support mechanism. */
	private transient PropertyChangeSupport propSupport;

	/** Bean property name for the report's name. */
	public static final String NAME_PROPERTY = "name";

	/** Bean property name for language. */
	public static final String LANGUAGE_PROPERTY = "language";

	/** Bean property name for query. */
	public static final String QUERY_PROPERTY = "query";

	/** Bean property name for resource bundle. */
	public static final String RESOURCE_BUNDLE_PROPERTY = "resourceBundle";

	/** Bean property name for scriptlet class. */
	public static final String SCRIPTLET_CLASS_PROPERTY = "scriptletClass";

	/** Bean property name for orientation. */
	public static final String ORIENTATION_PROPERTY = "orientation";

	/** Bean property name for background. */
	public static final String BACKGROUND_PROPERTY = "background";

	/** Bean property name for column count. */
	public static final String COLUMN_COUNT_PROPERTY = "columnCount";

	/** Bean property name for column header. */
	public static final String COLUMN_HEADER_PROPERTY = "columnHeader";

	/** Bean property name for column footer. */
	public static final String COLUMN_FOOTER_PROPERTY = "columnFooter";

	/** Bean property name for left margin. */
	public static final String LEFT_MARGIN_PROPERTY = "leftMargin";

	/** Bean property name for right margin. */
	public static final String RIGHT_MARGIN_PROPERTY = "rightMargin";

	/** Bean property name for top margin. */
	public static final String TOP_MARGIN_PROPERTY = "topMargin";

	/** Bean property name for bottom margin. */
	public static final String BOTTOM_MARGIN_PROPERTY = "bottomMargin";

	/** Bean property name for column width. */
	public static final String COLUMN_WIDTH_PROPERTY = "columnWidth";

	/** Bean property name for column spacing. */
	public static final String COLUMN_SPACING_PROPERTY = "columnSpacing";

	/** Bean property name for last page footer. */
	public static final String PRINT_ORDER_PROPERTY = "printOrder";

	/** Bean property name for default font. */
	public static final String DEFAULT_FONT_PROPERTY = "defaultFont";

	/** Bean property name for title. */
	public static final String TITLE_PROPERTY = "title";

	/** Bean property name for title new page. */
	public static final String TITLE_NEW_PAGE_PROPERTY = "titleNewPage";

	/** Bean property name for page width. */
	public static final String PAGE_WIDTH_PROPERTY = "pageWidth";

	/** Bean property name for page height. */
	public static final String PAGE_HEIGHT_PROPERTY = "pageHeight";

	/** Bean property name for page header. */
	public static final String PAGE_HEADER_PROPERTY = "pageHeader";

	/** Bean property name for page footer. */
	public static final String PAGE_FOOTER_PROPERTY = "pageFooter";

	/** Bean property name for last page footer. */
	public static final String LAST_PAGE_FOOTER_PROPERTY = "lastPageFooter";

	/** Bean property name for summary. */
	public static final String SUMMARY_PROPERTY = "summary";

	/** Bean property name for summary new page. */
	public static final String SUMMARY_NEW_PAGE_PROPERTY = "summaryNewPage";

	/** Bean property name for float column footer. */
	public static final String FLOAT_COLUMN_FOOTER_PROPERTY = "floatColumnFooter";

	/** Bean property name for detail band. */
	public static final String DETAIL_PROPERTY = "detail";

	/**
	 *
	 */
	private static final long serialVersionUID = 10000;

	/**
	 *
	 */
	private Map fontsMap = new HashMap();
	private List fontsList = new ArrayList();

	/**
	 * Main report dataset.
	 */
	private JRDesignDataset mainDesignDataset;

	/**
	 * Report sub datasets indexed by name.
	 */
	private Map datasetMap = new HashMap();
	private List datasetList = new ArrayList();
	
	/**
	 *
	 */
	public JasperDesign()
	{
		setMainDataset(new JRDesignDataset(true));
	}


	/**
	 *
	 */
	public void setName(String name)
	{
		Object oldValue = this.name;
		this.name = name;
		getPropertyChangeSupport().firePropertyChange(NAME_PROPERTY, oldValue, this.name);
	}


	/**
	 *
	 */
	public void setLanguage(String language)
	{
		Object oldValue = this.language;
		this.language = language;
		getPropertyChangeSupport().firePropertyChange(LANGUAGE_PROPERTY, oldValue, this.language);
	}
		

	/**
	 *
	 */
	public void setColumnCount(int columnCount)
	{
		int oldValue = this.columnCount;
		this.columnCount = columnCount;
		getPropertyChangeSupport().firePropertyChange(COLUMN_COUNT_PROPERTY, oldValue, this.columnCount);
	}
		

	/**
	 *
	 */
	public void setPrintOrder(byte printOrder)
	{
		Object oldValue = new Byte(this.printOrder);
		this.printOrder = printOrder;
		getPropertyChangeSupport().firePropertyChange(PRINT_ORDER_PROPERTY, oldValue, new Byte(this.printOrder));
	}
		

	/**
	 *
	 */
	public void setPageWidth(int pageWidth)
	{
		int oldValue = this.pageWidth;
		this.pageWidth = pageWidth;
		getPropertyChangeSupport().firePropertyChange(PAGE_WIDTH_PROPERTY, oldValue, this.pageWidth);
	}
		

	/**
	 *
	 */
	public void setPageHeight(int pageHeight)
	{
		int oldValue = this.pageHeight;
		this.pageHeight = pageHeight;
		getPropertyChangeSupport().firePropertyChange(PAGE_HEIGHT_PROPERTY, oldValue,
				this.pageHeight);
	}
		

	/**
	 *
	 */
	public void setOrientation(byte orientation)
	{
		Object oldValue = new Byte(this.orientation);
		this.orientation = orientation;
		getPropertyChangeSupport().firePropertyChange(ORIENTATION_PROPERTY, oldValue,
				new Byte(this.orientation));
	}
		

	/**
	 *
	 */
	public void setColumnWidth(int columnWidth)
	{
		int oldValue = this.columnWidth;
		this.columnWidth = columnWidth;
		getPropertyChangeSupport().firePropertyChange(COLUMN_WIDTH_PROPERTY, oldValue,
				this.columnWidth);
	}
		

	/**
	 *
	 */
	public void setColumnSpacing(int columnSpacing)
	{
		int oldValue = this.columnSpacing;
		this.columnSpacing = columnSpacing;
		getPropertyChangeSupport().firePropertyChange(COLUMN_SPACING_PROPERTY, oldValue,
				this.columnSpacing);
	}
		

	/**
	 *
	 */
	public void setLeftMargin(int leftMargin)
	{
		int oldValue = this.leftMargin;
		this.leftMargin = leftMargin;
		getPropertyChangeSupport().firePropertyChange(LEFT_MARGIN_PROPERTY, oldValue,
				this.leftMargin);
	}
		

	/**
	 *
	 */
	public void setRightMargin(int rightMargin)
	{
		int oldValue = this.rightMargin;
		this.rightMargin = rightMargin;
		getPropertyChangeSupport().firePropertyChange(RIGHT_MARGIN_PROPERTY, oldValue,
				this.rightMargin);
	}
		

	/**
	 *
	 */
	public void setTopMargin(int topMargin)
	{
		int oldValue = this.topMargin;
		this.topMargin = topMargin;
		getPropertyChangeSupport().firePropertyChange(TOP_MARGIN_PROPERTY, oldValue,
				this.topMargin);
	}
		

	/**
	 *
	 */
	public void setBottomMargin(int bottomMargin)
	{
		int oldValue = this.bottomMargin;
		this.bottomMargin = bottomMargin;
		getPropertyChangeSupport().firePropertyChange(BOTTOM_MARGIN_PROPERTY, oldValue,
				this.bottomMargin);
	}
		

	/**
	 *
	 */
	public void setBackground(JRBand background)
	{
		Object oldValue = this.background;
		this.background = background;
		getPropertyChangeSupport().firePropertyChange(BACKGROUND_PROPERTY, oldValue, this.background);
	}
		

	/**
	 *
	 */
	public void setTitle(JRBand title)
	{
		Object oldValue = this.title;
		this.title = title;
		getPropertyChangeSupport().firePropertyChange(TITLE_PROPERTY, oldValue, this.title);
	}
		

	/**
	 *
	 */
	public void setTitleNewPage(boolean isTitleNewPage)
	{
		boolean oldValue = this.isTitleNewPage;
		this.isTitleNewPage = isTitleNewPage;
		getPropertyChangeSupport().firePropertyChange(TITLE_NEW_PAGE_PROPERTY, oldValue, this.isTitleNewPage);
	}
		

	/**
	 *
	 */
	public void setSummary(JRBand summary)
	{
		Object oldValue = this.summary;
		this.summary = summary;
		getPropertyChangeSupport().firePropertyChange(SUMMARY_PROPERTY, oldValue, this.summary);
	}
		

	/**
	 *
	 */
	public void setSummaryNewPage(boolean isSummaryNewPage)
	{
		boolean oldValue = this.isSummaryNewPage;
		this.isSummaryNewPage = isSummaryNewPage;
		getPropertyChangeSupport().firePropertyChange(SUMMARY_NEW_PAGE_PROPERTY, oldValue,
				this.isSummaryNewPage);
	}
		

	/**
	 *
	 */
	public void setFloatColumnFooter(boolean isFloatColumnFooter)
	{
		boolean oldValue = this.isFloatColumnFooter;
		this.isFloatColumnFooter = isFloatColumnFooter;
		getPropertyChangeSupport().firePropertyChange(FLOAT_COLUMN_FOOTER_PROPERTY, oldValue,
				this.isFloatColumnFooter);
	}
		

	/**
	 *
	 */
	public void setPageHeader(JRBand pageHeader)
	{
		Object oldValue = this.pageHeader;
		this.pageHeader = pageHeader;
		getPropertyChangeSupport().firePropertyChange(PAGE_HEADER_PROPERTY, oldValue, this.pageHeader);
	}
		

	/**
	 *
	 */
	public void setPageFooter(JRBand pageFooter)
	{
		Object oldValue = this.pageFooter;
		this.pageFooter = pageFooter;
		getPropertyChangeSupport().firePropertyChange(PAGE_FOOTER_PROPERTY, oldValue, this.pageFooter);
	}
		

	/**
	 *
	 */
	public void setLastPageFooter(JRBand lastPageFooter)
	{
		Object oldValue = this.lastPageFooter;
		this.lastPageFooter = lastPageFooter;
		getPropertyChangeSupport().firePropertyChange(LAST_PAGE_FOOTER_PROPERTY, oldValue,
				this.lastPageFooter);
	}
		

	/**
	 *
	 */
	public void setColumnHeader(JRBand columnHeader)
	{
		Object oldValue = this.columnHeader;
		this.columnHeader = columnHeader;
		getPropertyChangeSupport().firePropertyChange(COLUMN_HEADER_PROPERTY, oldValue,
				this.columnHeader);
	}
		

	/**
	 *
	 */
	public void setColumnFooter(JRBand columnFooter)
	{
		Object oldValue = this.columnFooter;
		this.columnFooter = columnFooter;
		getPropertyChangeSupport().firePropertyChange(COLUMN_FOOTER_PROPERTY, oldValue,
				this.columnFooter);
	}
		

	/**
	 *
	 */
	public void setDetail(JRBand detail)
	{
		Object oldValue = this.detail;
		this.detail = detail;
		getPropertyChangeSupport().firePropertyChange(DETAIL_PROPERTY, oldValue, this.detail);
	}
		

	/**
	 *
	 */
	public void setScriptletClass(String scriptletClass)
	{
		Object oldValue = mainDesignDataset.getScriptletClass();
		mainDesignDataset.setScriptletClass(scriptletClass);
		getPropertyChangeSupport().firePropertyChange(SCRIPTLET_CLASS_PROPERTY, oldValue, scriptletClass);
	}
		

	/**
	 *
	 */
	public void setResourceBundle(String resourceBundle)
	{
		Object oldValue = mainDesignDataset.getResourceBundle();
		mainDesignDataset.setResourceBundle(resourceBundle);
		getPropertyChangeSupport().firePropertyChange(RESOURCE_BUNDLE_PROPERTY, oldValue, resourceBundle);
	}
		

	/**
	 *
	 */
	public void addImport(String value)
	{
		if (importsSet == null)
		{
			importsSet = new HashSet();
		}
		importsSet.add(value);
	}


	/**
	 *
	 */
	public void removeImport(String value)
	{
		if (importsSet != null)
		{
			importsSet.remove(value);
		}
	}


	/**
	 *
	 */
	public void setDefaultFont(JRReportFont font)
	{
		Object oldValue = this.defaultFont;
		this.defaultFont = font;
		getPropertyChangeSupport().firePropertyChange(DEFAULT_FONT_PROPERTY, oldValue, this.defaultFont);
	}
		

	/**
	 *
	 */
	public JRReportFont[] getFonts()
	{
		JRReportFont[] fontsArray = new JRReportFont[fontsList.size()];
		
		fontsList.toArray(fontsArray);

		return fontsArray;
	}
	

	/**
	 *
	 */
	public List getFontsList()
	{
		return fontsList;
	}
	

	/**
	 *
	 */
	public Map getFontsMap()
	{
		return fontsMap;
	}
	

	/**
	 *
	 */
	public void addFont(JRReportFont reportFont) throws JRException
	{
		if (fontsMap.containsKey(reportFont.getName()))
		{
			throw new JRException("Duplicate declaration of report font : " + reportFont.getName());
		}

		fontsList.add(reportFont);
		fontsMap.put(reportFont.getName(), reportFont);
		
		if (reportFont.isDefault())
		{
			setDefaultFont(reportFont);
		}
	}
	

	/**
	 *
	 */
	public JRReportFont removeFont(String propName)
	{
		return removeFont(
			(JRReportFont)fontsMap.get(propName)
			);
	}


	/**
	 *
	 */
	public JRReportFont removeFont(JRReportFont reportFont)
	{
		if (reportFont != null)
		{
			if (reportFont.isDefault())
			{
				setDefaultFont(null);
			}

			fontsList.remove(reportFont);
			fontsMap.remove(reportFont.getName());
		}

		return reportFont;
	}
	

	/**
	 *
	 */
	public List getParametersList()
	{
		return mainDesignDataset.getParametersList();
	}
	

	/**
	 *
	 */
	public Map getParametersMap()
	{
		return mainDesignDataset.getParametersMap();
	}
	

	/**
	 *
	 */
	public void addParameter(JRParameter parameter) throws JRException
	{
		mainDesignDataset.addParameter(parameter);
	}
	

	/**
	 *
	 */
	public JRParameter removeParameter(String parameterName)
	{
		return mainDesignDataset.removeParameter(parameterName);
	}


	/**
	 *
	 */
	public JRParameter removeParameter(JRParameter parameter)
	{
		return mainDesignDataset.removeParameter(parameter);
	}


	/**
	 *
	 */
	public void setQuery(JRQuery query)
	{
		Object oldValue = mainDesignDataset.getQuery();
		mainDesignDataset.setQuery(query);
		getPropertyChangeSupport().firePropertyChange(QUERY_PROPERTY, oldValue, query);
	}

	/**
	 * Add a property listener to listen to all properties of this class.
	 * @param l The property listener to add.
	 */
	public void addPropertyChangeListener(PropertyChangeListener l) {
		getPropertyChangeSupport().addPropertyChangeListener(l);
	}

	/**
	 * Add a property listener to receive property change events for only one specific
	 * property.
	 * @param propName The property to listen to.
	 * @param l The property listener to add.
	 */
	public void addPropertyChangeListener(String propName, PropertyChangeListener l) {
		getPropertyChangeSupport().addPropertyChangeListener(propName, l);
	}

	/**
	 * Remove a property change listener.  This will remove any listener that was added
	 * through either of the addPropertyListener methods.
	 * @param l The listener to remove.
	 */
	public void removePropertyChangeListener(PropertyChangeListener l) {
		getPropertyChangeSupport().removePropertyChangeListener(l);
	}

	/**
	 * Get the property change support object for this class.  Because the
	 * property change support object has to be transient, it may need to be
	 * created.
	 * @return The property change support object.
	 */
	protected PropertyChangeSupport getPropertyChangeSupport() {
		if (propSupport == null) {
			propSupport = new PropertyChangeSupport(this);
		}
		return propSupport;
	}
	

	/**
	 *
	 */
	public List getFieldsList()
	{
		return mainDesignDataset.getFieldsList();
	}
	

	/**
	 *
	 */
	public Map getFieldsMap()
	{
		return mainDesignDataset.getFieldsMap();
	}
	

	/**
	 *
	 */
	public void addField(JRField field) throws JRException
	{
		mainDesignDataset.addField(field);
	}
	

	/**
	 *
	 */
	public JRField removeField(String fieldName)
	{
		return mainDesignDataset.removeField(fieldName);
	}


	/**
	 *
	 */
	public JRField removeField(JRField field)
	{
		return mainDesignDataset.removeField(field);
	}
	

	/**
	 *
	 */
	public List getVariablesList()
	{
		return mainDesignDataset.getVariablesList();
	}
	

	/**
	 *
	 */
	public Map getVariablesMap()
	{
		return mainDesignDataset.getVariablesMap();
	}
	

	/**
	 *
	 */
	public void addVariable(JRDesignVariable variable) throws JRException
	{
		mainDesignDataset.addVariable(variable);
	}
	

	/**
	 *
	 */
	public JRVariable removeVariable(String variableName)
	{
		return mainDesignDataset.removeVariable(variableName);
	}


	/**
	 *
	 */
	public JRVariable removeVariable(JRVariable variable)
	{
		return mainDesignDataset.removeVariable(variable);
	}
	

	/**
	 *
	 */
	public List getGroupsList()
	{
		return mainDesignDataset.getGroupsList();
	}
	

	/**
	 *
	 */
	public Map getGroupsMap()
	{
		return mainDesignDataset.getGroupsMap();
	}
	

	/**
	 *
	 */
	public void addGroup(JRDesignGroup group) throws JRException
	{
		mainDesignDataset.addGroup(group);
	}
	

	/**
	 *
	 */
	public JRGroup removeGroup(String groupName)
	{
		return mainDesignDataset.removeGroup(groupName);
	}


	/**
	 *
	 */
	public JRGroup removeGroup(JRGroup group)
	{
		return mainDesignDataset.removeGroup(group);
	}


	/**
	 * 
	 */
	public Collection getExpressions()
	{
		JRExpressionCollector expressionCollector = new JRExpressionCollector();
		return expressionCollector.collect(this);
	}

	
	public JRDataset[] getDatasets()
	{
		JRDataset[] datasetArray = new JRDataset[datasetList.size()];
		datasetList.toArray(datasetArray);
		return datasetArray;
	}
	
	
	/**
	 * Returns the list of report sub datasets.
	 * 
	 * @return list of {@link JRDesignDataset JRDesignDataset} objects
	 */
	public List getDatasetsList()
	{
		return datasetList;
	}
	
	
	/**
	 * Returns the sub datasets of the report indexed by name.
	 * 
	 * @return the sub datasets of the report indexed by name
	 */
	public Map getDatasetMap()
	{
		return datasetMap;
	}
	
	
	/**
	 * Adds a sub dataset to the report.
	 * 
	 * @param dataset the dataset
	 * @throws JRException
	 */
	public void addDataset(JRDesignDataset dataset) throws JRException
	{
		if (datasetMap.containsKey(dataset.getName()))
		{
			throw new JRException("Duplicate declaration of dataset : " + dataset.getName());
		}

		datasetList.add(dataset);
		datasetMap.put(dataset.getName(), dataset);
	}
	

	/**
	 * Removes a sub dataset from the report.
	 * 
	 * @param datasetName the dataset name
	 * @return the removed dataset
	 */
	public JRDataset removeDataset(String datasetName)
	{
		return removeDataset(
			(JRDataset)datasetMap.get(datasetName)
			);
	}


	/**
	 * Removes a sub dataset from the report.
	 * 
	 * @param dataset the dataset to be removed
	 * @return the dataset
	 */
	public JRDataset removeDataset(JRDataset dataset)
	{
		if (dataset != null)
		{
			datasetList.remove(dataset);
			datasetMap.remove(dataset.getName());
		}
		
		return dataset;
	}
	
	
	/**
	 * Returns the main report dataset.
	 * 
	 * @return the main report dataset
	 */
	public JRDesignDataset getMainDesignDataset()
	{
		return mainDesignDataset;
	}
	
	
	/**
	 * Sets the main report dataset.
	 * <p>
	 * This method can be used as an alternative to setting the parameters, fields, etc directly on the report. 
	 * 
	 * @param dataset the dataset
	 */
	public void setMainDataset(JRDesignDataset dataset)
	{
		this.mainDataset = this.mainDesignDataset = dataset;
	}
}
