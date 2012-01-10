/*
 * JasperReports - Free Java Reporting Library.
 * Copyright (C) 2001 - 2011 Jaspersoft Corporation. All rights reserved.
 * http://www.jaspersoft.com
 *
 * Unless you have purchased a commercial license agreement from Jaspersoft,
 * the following license terms apply:
 *
 * This program is part of JasperReports.
 *
 * JasperReports is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * JasperReports is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with JasperReports. If not, see <http://www.gnu.org/licenses/>.
 */
package net.sf.jasperreports.components.headertoolbar;

import java.io.StringWriter;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.ResourceBundle;

import net.sf.jasperreports.components.BaseElementHtmlHandler;
import net.sf.jasperreports.components.sort.FieldFilter;
import net.sf.jasperreports.components.sort.FilterTypeDateOperatorsEnum;
import net.sf.jasperreports.components.sort.FilterTypeNumericOperatorsEnum;
import net.sf.jasperreports.components.sort.FilterTypeTextOperatorsEnum;
import net.sf.jasperreports.components.sort.FilterTypesEnum;
import net.sf.jasperreports.engine.CompositeDatasetFilter;
import net.sf.jasperreports.engine.DatasetFilter;
import net.sf.jasperreports.engine.JRExporterParameter;
import net.sf.jasperreports.engine.JRGenericPrintElement;
import net.sf.jasperreports.engine.JRParameter;
import net.sf.jasperreports.engine.JRPrintHyperlinkParameter;
import net.sf.jasperreports.engine.JRPrintHyperlinkParameters;
import net.sf.jasperreports.engine.JRRuntimeException;
import net.sf.jasperreports.engine.JRSortField;
import net.sf.jasperreports.engine.ReportContext;
import net.sf.jasperreports.engine.base.JRBasePrintHyperlink;
import net.sf.jasperreports.engine.export.JRHtmlExporterContext;
import net.sf.jasperreports.engine.export.JRXhtmlExporter;
import net.sf.jasperreports.engine.type.JREnum;
import net.sf.jasperreports.engine.type.ModeEnum;
import net.sf.jasperreports.engine.util.JRColorUtil;
import net.sf.jasperreports.engine.util.JRProperties;
import net.sf.jasperreports.web.WebReportContext;
import net.sf.jasperreports.web.servlets.ReportServlet;
import net.sf.jasperreports.web.servlets.ResourceServlet;
import net.sf.jasperreports.web.util.VelocityUtil;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.velocity.VelocityContext;
import org.codehaus.jackson.map.ObjectMapper;

/**
 * @author Teodor Danciu (teodord@users.sourceforge.net)
 * @version $Id:ChartThemesUtilities.java 2595 2009-02-10 17:56:51Z teodord $
 */
public class HeaderToolbarElementHtmlHandler extends BaseElementHtmlHandler
{
	private static final Log log = LogFactory.getLog(HeaderToolbarElementHtmlHandler.class);
	
	private static final String RESOURCE_HEADERTOOLBAR_JS = "net/sf/jasperreports/components/headertoolbar/resources/headertoolbar.js";
	private static final String RESOURCE_IMAGE_CLOSE = "net/sf/jasperreports/components/headertoolbar/resources/images/delete_edit.gif";
	private static final String RESOURCE_TRANSPARENT_PIXEL = "net/sf/jasperreports/engine/images/pixel.GIF";
	
	// new
	private static final String RESOURCE_FILTER_DISABLED = 		"net/sf/jasperreports/components/headertoolbar/resources/images/filter.png";
	private static final String RESOURCE_FILTER_DEFAULT = 		"net/sf/jasperreports/components/headertoolbar/resources/images/filter.png";
	private static final String RESOURCE_FILTER_DEFAULT_HOVER = "net/sf/jasperreports/components/headertoolbar/resources/images/filter_over.png";
	private static final String RESOURCE_FILTER_ENABLED = 		"net/sf/jasperreports/components/headertoolbar/resources/images/filter_over.png";
	private static final String RESOURCE_FILTER_ENABLED_HOVER = "net/sf/jasperreports/components/headertoolbar/resources/images/filter_over.png";
	private static final String RESOURCE_FILTER_WRONG = 		"net/sf/jasperreports/components/headertoolbar/resources/images/filter_wrong.png";
	private static final String RESOURCE_FILTER_WRONG_HOVER = 	"net/sf/jasperreports/components/headertoolbar/resources/images/filter_wrong.png";
	
	private static final String RESOURCE_SORT_DEFAULT_ASC = 		"net/sf/jasperreports/components/headertoolbar/resources/images/sort_asc.png";
	private static final String RESOURCE_SORT_DEFAULT_ASC_HOVER = 	"net/sf/jasperreports/components/headertoolbar/resources/images/sort_asc_over.png";
	private static final String RESOURCE_SORT_ENABLED_ASC = 		"net/sf/jasperreports/components/headertoolbar/resources/images/sort_asc_over.png";
	private static final String RESOURCE_SORT_ENABLED_ASC_HOVER = 	"net/sf/jasperreports/components/headertoolbar/resources/images/sort_asc_over.png";
	
	private static final String RESOURCE_SORT_DEFAULT_DESC = 		"net/sf/jasperreports/components/headertoolbar/resources/images/sort_desc.png";
	private static final String RESOURCE_SORT_DEFAULT_DESC_HOVER = 	"net/sf/jasperreports/components/headertoolbar/resources/images/sort_desc_over.png";
	private static final String RESOURCE_SORT_ENABLED_DESC = 		"net/sf/jasperreports/components/headertoolbar/resources/images/sort_desc_over.png";
	private static final String RESOURCE_SORT_ENABLED_DESC_HOVER = 	"net/sf/jasperreports/components/headertoolbar/resources/images/sort_desc_over.png";
	// new
	
	private static final String SORT_ELEMENT_HTML_TEMPLATE = "net/sf/jasperreports/components/headertoolbar/resources/HeaderToolbarElementHtmlTemplate.vm";
	
	private static class CustomJRExporterParameter extends JRExporterParameter{

		protected CustomJRExporterParameter(String name) {
			super(name);
		}
		
	}
	
	private static final CustomJRExporterParameter param = new HeaderToolbarElementHtmlHandler.CustomJRExporterParameter("already_generated");

	public String getHtmlFragment(JRHtmlExporterContext context, JRGenericPrintElement element)
	{
		if (context.getExportParameters().containsKey(param) && (Boolean)context.getExportParameters().get(param)) {
			if (log.isDebugEnabled()) {
				log.debug("found alredy_generated param");
			}
		} else {
			if (log.isDebugEnabled()) {
				log.debug("did not find alredy_generated param");
			}
			context.getExportParameters().put(param, Boolean.TRUE);
		}
		String htmlFragment = null;
		ReportContext reportContext = context.getExporter().getReportContext();
		if (reportContext != null)//FIXMEJIVE
		{
			String sortColumnName = (String) element.getParameterValue(HeaderToolbarElement.PARAMETER_SORT_COLUMN_NAME);
			String sortColumnLabel = (String) element.getParameterValue(HeaderToolbarElement.PARAMETER_SORT_COLUMN_LABEL);
			String sortColumnType = (String) element.getParameterValue(HeaderToolbarElement.PARAMETER_SORT_COLUMN_TYPE);
			
			String sortDatasetName = element.getPropertiesMap().getProperty(HeaderToolbarElement.PROPERTY_DATASET_RUN);

			String popupId = (String) element.getParameterValue("popupId");
			
			FilterTypesEnum filterType = FilterTypesEnum.getByName(element.getPropertiesMap().getProperty(HeaderToolbarElement.PROPERTY_FILTER_TYPE));
			if (filterType == null)//FIXMEJIVE
			{
				return null;
			}
			
			String filterPattern = element.getPropertiesMap().getProperty(HeaderToolbarElement.PROPERTY_FILTER_PATTERN);

			Locale locale = (Locale) reportContext.getParameterValue(JRParameter.REPORT_LOCALE);
			
			if (log.isDebugEnabled()) {
				log.debug("report locale: " + locale);
			}
			
			if (locale == null) {
				locale = Locale.getDefault();
			}
			
			Map<String, String> translatedOperators = null;
			switch (filterType) {
				case NUMERIC:
					translatedOperators = getTranslatedOperators(FilterTypeNumericOperatorsEnum.class.getName(), FilterTypeNumericOperatorsEnum.values(), locale);
					break;
				case DATE:
					translatedOperators = getTranslatedOperators(FilterTypeDateOperatorsEnum.class.getName(), FilterTypeDateOperatorsEnum.values(), locale);
					break;
				case TEXT:
					translatedOperators = getTranslatedOperators(FilterTypeTextOperatorsEnum.class.getName(), FilterTypeTextOperatorsEnum.values(), locale);
					break;
			}
			
			String appContextPath = (String)reportContext.getParameterValue("net.sf.jasperreports.web.app.context.path");//FIXMEJIVE define constant
			
			VelocityContext velocityContext = new VelocityContext();
			String webResourcesBasePath = JRProperties.getProperty("net.sf.jasperreports.web.resources.base.path");
			if (webResourcesBasePath == null)
			{
				webResourcesBasePath = ResourceServlet.DEFAULT_PATH + "?" + ResourceServlet.RESOURCE_URI + "=";
			}
			String imagesResourcePath = (appContextPath == null ? "" : appContextPath) + webResourcesBasePath;//FIXMEJIVE

			velocityContext.put("resourceHeaderToolbarJs", webResourcesBasePath + HeaderToolbarElementHtmlHandler.RESOURCE_HEADERTOOLBAR_JS);
			velocityContext.put("elementX", ((JRXhtmlExporter)context.getExporter()).toSizeUnit(element.getX()));
			velocityContext.put("elementY", ((JRXhtmlExporter)context.getExporter()).toSizeUnit(element.getY()));
			velocityContext.put("elementWidth", element.getWidth());
			velocityContext.put("elementHeight", element.getHeight());
			velocityContext.put("sortLinkClass", sortDatasetName);
			velocityContext.put("transparentPixelSrc", imagesResourcePath + HeaderToolbarElementHtmlHandler.RESOURCE_TRANSPARENT_PIXEL);
			
			velocityContext.put("isFilterable", filterType != null);
			velocityContext.put("filterDivId", "filter_" + sortDatasetName + "_" + sortColumnName);
			velocityContext.put("filterFormAction", getFilterFormActionLink(context));
			velocityContext.put("filterReportUriParamName", ReportServlet.REQUEST_PARAMETER_REPORT_URI);
			velocityContext.put("filterReportUriParamValue", reportContext.getParameterValue(ReportServlet.REQUEST_PARAMETER_REPORT_URI));
			velocityContext.put("filterFieldParamName", HeaderToolbarElement.REQUEST_PARAMETER_FILTER_FIELD);
			velocityContext.put("filterColumnName", sortColumnName);
			velocityContext.put("filterColumnNameLabel", sortColumnLabel != null ? sortColumnLabel : "");
			velocityContext.put("filterTableNameParam", HeaderToolbarElement.REQUEST_PARAMETER_DATASET_RUN);
			velocityContext.put("filterTableNameValue", sortDatasetName);
			velocityContext.put("filterCloseDialogImageResource", imagesResourcePath + HeaderToolbarElementHtmlHandler.RESOURCE_IMAGE_CLOSE);

			velocityContext.put("filterTypeParamName", HeaderToolbarElement.REQUEST_PARAMETER_FILTER_TYPE);
			velocityContext.put("filterTypeParamNameValue", filterType.getName());
			velocityContext.put("filterTypeOperatorParamName", HeaderToolbarElement.REQUEST_PARAMETER_FILTER_TYPE_OPERATOR);
			
			velocityContext.put("filterPatternParamName", HeaderToolbarElement.REQUEST_PARAMETER_FILTER_PATTERN);
			velocityContext.put("filterPatternParamValue", filterPattern);

			velocityContext.put("filterTypeValuesMap", translatedOperators);
			
			velocityContext.put("filterValueStartParamName", HeaderToolbarElement.REQUEST_PARAMETER_FILTER_VALUE_START);
			velocityContext.put("filterValueEndParamName", HeaderToolbarElement.REQUEST_PARAMETER_FILTER_VALUE_END);

			// begin:temp
			if (popupId != null) {
				velocityContext.put("popupId", popupId);
			}
			String sortAscHref = getSortLink(context, sortColumnName, sortColumnType, HeaderToolbarElement.SORT_ORDER_ASC, sortDatasetName);
			String sortDescHref = getSortLink(context, sortColumnName, sortColumnType, HeaderToolbarElement.SORT_ORDER_DESC, sortDatasetName);
	
			String sortAscSrc = RESOURCE_SORT_DEFAULT_ASC;
			String sortAscHoverSrc = RESOURCE_SORT_DEFAULT_ASC_HOVER;
			String sortDescSrc = RESOURCE_SORT_DEFAULT_DESC;
			String sortDescHoverSrc = RESOURCE_SORT_DEFAULT_DESC_HOVER;
			String filterSrc = RESOURCE_FILTER_DISABLED;
			String filterHoverSrc = "";
			
			if (filterType != null) {
				filterSrc = RESOURCE_FILTER_DEFAULT;
				filterHoverSrc = RESOURCE_FILTER_DEFAULT_HOVER;
			}
			// end:temp
			
			
			if (element.getModeValue() == ModeEnum.OPAQUE)
			{
				velocityContext.put("backgroundColor", JRColorUtil.getColorHexa(element.getBackcolor()));
			}

			String sortField = getCurrentSortField(reportContext, sortDatasetName, sortColumnName, sortColumnType);
			if (sortField != null) 
			{
				String[] sortActionData = HeaderToolbarElementUtils.extractColumnInfo(sortField);
				
				boolean isAscending = HeaderToolbarElement.SORT_ORDER_ASC.equals(sortActionData[2]);
				if (isAscending) {
					sortAscHref = getSortLink(context, sortColumnName, sortColumnType, HeaderToolbarElement.SORT_ORDER_NONE, sortDatasetName);
					sortAscSrc = RESOURCE_SORT_ENABLED_ASC;
					sortAscHoverSrc = RESOURCE_SORT_ENABLED_ASC_HOVER;
				} else {
					sortDescHref = getSortLink(context, sortColumnName, sortColumnType, HeaderToolbarElement.SORT_ORDER_NONE, sortDatasetName);
					sortDescSrc = RESOURCE_SORT_ENABLED_DESC;
					sortDescHoverSrc = RESOURCE_SORT_ENABLED_DESC_HOVER;
				}
			}
			
			// existing filters
			String currentDataset = (String) reportContext.getParameterValue(HeaderToolbarElement.REQUEST_PARAMETER_DATASET_RUN);
			String filterValueStart = "";
			String filterValueEnd = "";
			String filterTypeOperatorValue = "";
			boolean isFiltered = false;
			boolean enableFilterEndParameter = false;
			List<FieldFilter> fieldFilters = new ArrayList<FieldFilter>();

			if (sortDatasetName != null && sortDatasetName.equals(currentDataset))
			{
				String currentTableFiltersParam = currentDataset + HeaderToolbarElement.FILTER_FIELDS_PARAM_SUFFIX;
				DatasetFilter existingFilter = (DatasetFilter) reportContext.getParameterValue(currentTableFiltersParam);
				getFieldFilters(existingFilter, fieldFilters, sortColumnName);
				
				if (fieldFilters.size() > 0) {
					FieldFilter ff = fieldFilters.get(0);
					if (ff.getFilterValueStart() != null) {
						filterValueStart = ff.getFilterValueStart();
					}
					if (ff.getFilterValueEnd() != null) {
						filterValueEnd = ff.getFilterValueEnd();
					}
					filterTypeOperatorValue = ff.getFilterTypeOperator();
					isFiltered = true;
					if (filterTypeOperatorValue != null && filterTypeOperatorValue.toLowerCase().contains("between")) {
						enableFilterEndParameter = true;
					}
					if (!ff.getIsValid()) {
						filterSrc = RESOURCE_FILTER_WRONG;
						filterHoverSrc = RESOURCE_FILTER_WRONG_HOVER;
					} else {
						filterSrc = RESOURCE_FILTER_ENABLED;
						filterHoverSrc = RESOURCE_FILTER_ENABLED_HOVER;
					}
					
				}
			}
			
			velocityContext.put("isFiltered", isFiltered);
			velocityContext.put("filterToRemoveParamName", HeaderToolbarElement.REQUEST_PARAMETER_REMOVE_FILTER);
			velocityContext.put("filterToRemoveParamvalue", sortColumnName);
			
			String filtersJsonString = getJsonString(fieldFilters).replaceAll("\\\"", "\\\\\\\"");
			if (log.isDebugEnabled()) {
				log.debug("filtersJsonString: " + filtersJsonString);
			}
			velocityContext.put("filtersJsonString", filtersJsonString);

			velocityContext.put("filterValueStartParamValue", filterValueStart);
			velocityContext.put("filterValueEndParamValue", filterValueEnd);
			velocityContext.put("filterTypeOperatorParamValue", filterTypeOperatorValue);
			velocityContext.put("enableFilterEndParameter", enableFilterEndParameter);
			
			// begin:temp
			velocityContext.put("sortAscHref", sortAscHref);
			velocityContext.put("sortDescHref", sortDescHref);
			
			velocityContext.put("sortAscSrc", imagesResourcePath + sortAscSrc);
			velocityContext.put("sortAscHoverSrc", imagesResourcePath + sortAscHoverSrc);
			velocityContext.put("sortDescSrc", imagesResourcePath + sortDescSrc);
			velocityContext.put("sortDescHoverSrc", imagesResourcePath + sortDescHoverSrc);
			velocityContext.put("filterSrc", imagesResourcePath + filterSrc);
			velocityContext.put("filterHoverSrc", imagesResourcePath + filterHoverSrc);
			// end: temp
			
			htmlFragment = VelocityUtil.processTemplate(HeaderToolbarElementHtmlHandler.SORT_ELEMENT_HTML_TEMPLATE, velocityContext);
		}
		
		return htmlFragment;
	}
	
	private String getSortLink(JRHtmlExporterContext context, String sortColumnName, String sortColumnType, String sortOrder, String sortTableName) {
		JRBasePrintHyperlink hyperlink = new JRBasePrintHyperlink();
		hyperlink.setLinkType("ReportExecution");
		
		JRPrintHyperlinkParameters parameters = new JRPrintHyperlinkParameters();
		parameters.addParameter(new JRPrintHyperlinkParameter(
				HeaderToolbarElement.REQUEST_PARAMETER_SORT_DATA,
				String.class.getName(), 
				HeaderToolbarElementUtils.packSortColumnInfo(sortColumnName, sortColumnType, sortOrder)));
		parameters.addParameter(new JRPrintHyperlinkParameter(HeaderToolbarElement.REQUEST_PARAMETER_DATASET_RUN, String.class.getName(), sortTableName));
		
		ReportContext reportContext = context.getExporter().getReportContext();
		parameters.addParameter(new JRPrintHyperlinkParameter(WebReportContext.REQUEST_PARAMETER_REPORT_CONTEXT_ID, String.class.getName(), reportContext.getId()));
		parameters.addParameter(new JRPrintHyperlinkParameter(ReportServlet.REQUEST_PARAMETER_RUN_REPORT, String.class.getName(), "true"));
		
		hyperlink.setHyperlinkParameters(parameters);
		
		return context.getHyperlinkURL(hyperlink);
	}
	
	private String getFilterFormActionLink(JRHtmlExporterContext context) {
		JRBasePrintHyperlink hyperlink = new JRBasePrintHyperlink();
		hyperlink.setLinkType("ReportExecution");
		
		JRPrintHyperlinkParameters parameters = new JRPrintHyperlinkParameters();
		ReportContext reportContext = context.getExporter().getReportContext();
		parameters.addParameter(new JRPrintHyperlinkParameter(WebReportContext.REQUEST_PARAMETER_REPORT_CONTEXT_ID, String.class.getName(), reportContext.getId()));
		parameters.addParameter(new JRPrintHyperlinkParameter(ReportServlet.REQUEST_PARAMETER_RUN_REPORT, String.class.getName(), "true"));
		
		hyperlink.setHyperlinkParameters(parameters);
		
		return context.getHyperlinkURL(hyperlink);
	}

	private String getCurrentSortField(ReportContext reportContext, String sortDatasetName, String sortColumnName, String sortColumnType) 
	{
		String currentSortDataset = (String) reportContext.getParameterValue(
				HeaderToolbarElement.REQUEST_PARAMETER_DATASET_RUN);
		if (sortDatasetName == null || !sortDatasetName.equals(currentSortDataset))
		{
			// sorting is on a different dataset
			return null;
		}
		
		String currentTableSortFieldsParam = currentSortDataset + HeaderToolbarElement.SORT_FIELDS_PARAM_SUFFIX;
		@SuppressWarnings("unchecked")
		List<JRSortField> existingFields = (List<JRSortField>) reportContext.getParameterValue(currentTableSortFieldsParam);
		String sortField = null;

		if (existingFields != null && existingFields.size() > 0) {
			for (JRSortField field: existingFields) {
				if (field.getName().equals(sortColumnName) && field.getType().getName().equals(sortColumnType)) {
					sortField = sortColumnName + HeaderToolbarElement.SORT_COLUMN_TOKEN_SEPARATOR + sortColumnType + HeaderToolbarElement.SORT_COLUMN_TOKEN_SEPARATOR;
					switch (field.getOrderValue()) {
						case ASCENDING:
							sortField += HeaderToolbarElement.SORT_ORDER_ASC;
							break;
						case DESCENDING:
							sortField += HeaderToolbarElement.SORT_ORDER_DESC;
							break;
					}
					break;
				}
			}
		}
		
		return sortField;
	}
	
	public boolean toExport(JRGenericPrintElement element) {
		return true;
	}
	
	private Map<String, String> getTranslatedOperators(String bundleName, JREnum[] operators, Locale locale) {
		Map<String, String> result = new LinkedHashMap<String, String>();
		ResourceBundle rb = ResourceBundle.getBundle(bundleName, locale);
		
		for (JREnum operator: operators) {
			result.put(((Enum<?>)operator).name(), rb.getString(((Enum<?>)operator).name()));
		}
		
		return result;
	}
	
	private void getFieldFilters(DatasetFilter existingFilter, List<FieldFilter> fieldFilters, String fieldName) {
		if (existingFilter instanceof FieldFilter) {
			if ( fieldName == null || (fieldName != null && ((FieldFilter)existingFilter).getField().equals(fieldName))) {
				fieldFilters.add((FieldFilter)existingFilter);
			} 
		} else if (existingFilter instanceof CompositeDatasetFilter) {
			for (DatasetFilter filter : ((CompositeDatasetFilter)existingFilter).getFilters())
			{
				getFieldFilters(filter, fieldFilters, fieldName);
			}
		}
	}
	
	private String getJsonString(List<FieldFilter> fieldFilters) {
		
		ObjectMapper mapper = new ObjectMapper();
		StringWriter writer = new StringWriter(128);
		try {
			mapper.writeValue(writer, fieldFilters);
			writer.flush();
			writer.close();
		} catch (Exception e) {
			throw new JRRuntimeException(e);
		}
		
		return writer.getBuffer().toString();
	}

}
