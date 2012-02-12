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

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.ResourceBundle;
import java.util.UUID;

import net.sf.jasperreports.components.BaseElementHtmlHandler;
import net.sf.jasperreports.components.headertoolbar.actions.FilterAction;
import net.sf.jasperreports.components.headertoolbar.actions.SortAction;
import net.sf.jasperreports.components.sort.FieldFilter;
import net.sf.jasperreports.components.sort.FilterTypeDateOperatorsEnum;
import net.sf.jasperreports.components.sort.FilterTypeNumericOperatorsEnum;
import net.sf.jasperreports.components.sort.FilterTypeTextOperatorsEnum;
import net.sf.jasperreports.components.sort.FilterTypesEnum;
import net.sf.jasperreports.components.sort.actions.FilterCommand;
import net.sf.jasperreports.components.sort.actions.FilterData;
import net.sf.jasperreports.components.sort.actions.SortData;
import net.sf.jasperreports.components.table.StandardTable;
import net.sf.jasperreports.engine.DatasetFilter;
import net.sf.jasperreports.engine.JRExporterParameter;
import net.sf.jasperreports.engine.JRGenericPrintElement;
import net.sf.jasperreports.engine.JRIdentifiable;
import net.sf.jasperreports.engine.JRParameter;
import net.sf.jasperreports.engine.JRPrintHyperlinkParameter;
import net.sf.jasperreports.engine.JRPrintHyperlinkParameters;
import net.sf.jasperreports.engine.JRPropertiesMap;
import net.sf.jasperreports.engine.JRPropertiesUtil;
import net.sf.jasperreports.engine.JRRuntimeException;
import net.sf.jasperreports.engine.JRSortField;
import net.sf.jasperreports.engine.JasperReportsContext;
import net.sf.jasperreports.engine.ReportContext;
import net.sf.jasperreports.engine.base.JRBasePrintHyperlink;
import net.sf.jasperreports.engine.design.JRDesignComponentElement;
import net.sf.jasperreports.engine.design.JRDesignDataset;
import net.sf.jasperreports.engine.design.JRDesignDatasetRun;
import net.sf.jasperreports.engine.design.JasperDesign;
import net.sf.jasperreports.engine.export.JRHtmlExporterContext;
import net.sf.jasperreports.engine.export.JRXhtmlExporter;
import net.sf.jasperreports.engine.type.JREnum;
import net.sf.jasperreports.engine.type.ModeEnum;
import net.sf.jasperreports.engine.util.JRColorUtil;
import net.sf.jasperreports.repo.JasperDesignCache;
import net.sf.jasperreports.web.WebReportContext;
import net.sf.jasperreports.web.commands.CommandTarget;
import net.sf.jasperreports.web.servlets.ReportServlet;
import net.sf.jasperreports.web.servlets.ResourceServlet;
import net.sf.jasperreports.web.util.JacksonUtil;
import net.sf.jasperreports.web.util.VelocityUtil;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.velocity.VelocityContext;
import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.type.TypeReference;

/**
 * @author Teodor Danciu (teodord@users.sourceforge.net)
 * @version $Id:ChartThemesUtilities.java 2595 2009-02-10 17:56:51Z teodord $
 */
public class HeaderToolbarElementHtmlHandler extends BaseElementHtmlHandler
{
	private static final Log log = LogFactory.getLog(HeaderToolbarElementHtmlHandler.class);
	
	private static final String RESOURCE_HEADERTOOLBAR_JS = "net/sf/jasperreports/components/headertoolbar/resources/jasperreports-tableHeaderToolbar.js";
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
			String tableUUID = (String) element.getParameterValue(HeaderToolbarElement.PARAMETER_TABLE_UUID);
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
			String webResourcesBasePath = JRPropertiesUtil.getInstance(context.getExporter().getJasperReportsContext()).getProperty("net.sf.jasperreports.web.resources.base.path");
			if (webResourcesBasePath == null)
			{
				webResourcesBasePath = ResourceServlet.DEFAULT_PATH + "?" + ResourceServlet.RESOURCE_URI + "=";
			}
			String imagesResourcePath = (appContextPath == null ? "" : appContextPath) + webResourcesBasePath;//FIXMEJIVE

			velocityContext.put("jasperreports_tableHeaderToolbar_js", webResourcesBasePath + HeaderToolbarElementHtmlHandler.RESOURCE_HEADERTOOLBAR_JS);
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
			velocityContext.put("filterColumnName", sortColumnName);
			velocityContext.put("filterColumnNameLabel", sortColumnLabel != null ? sortColumnLabel : "");
			velocityContext.put("filterCloseDialogImageResource", imagesResourcePath + HeaderToolbarElementHtmlHandler.RESOURCE_IMAGE_CLOSE);
			
			// begin: the params that will generate the JSON post object for filtering
			velocityContext.put("filterTableNameParam", FilterData.FILTER_TABLE_UUID);
			velocityContext.put("filterTableNameValue", tableUUID);

			velocityContext.put("filterFieldParamName", FilterData.FIELD_NAME);

			velocityContext.put("filterTypeParamName", FilterData.FILTER_TYPE);
			velocityContext.put("filterTypeParamNameValue", filterType.getName());
			
			velocityContext.put("filterPatternParamName", FilterData.FILTER_PATTERN);
			velocityContext.put("filterPatternParamValue", filterPattern);
			
			velocityContext.put("filterTypeOperatorParamName", FilterData.FILTER_TYPE_OPERATOR);
			velocityContext.put("filterTypeValuesMap", translatedOperators);

			velocityContext.put("filterValueStartParamName", FilterData.FIELD_VALUE_START);
			
			velocityContext.put("filterValueEndParamName", FilterData.FIELD_VALUE_END);
			// end
			
			

			
			
			velocityContext.put("resizeColumnAction", getResizeColumnLink(context));

			// begin:temp
			if (popupId != null) {
				velocityContext.put("popupId", popupId);
			}
//			String sortAscHref = getSortLink(context, sortColumnName, sortColumnType, HeaderToolbarElement.SORT_ORDER_ASC, sortDatasetName);
			String sortAscHref = getSortActionLink(context);
			SortData sortAscData = new SortData(tableUUID, sortColumnName, sortColumnType, HeaderToolbarElement.SORT_ORDER_ASC, sortDatasetName);
	
//			String sortDescHref = getSortLink(context, sortColumnName, sortColumnType, HeaderToolbarElement.SORT_ORDER_DESC, sortDatasetName);
			String sortDescHref = getSortActionLink(context);
			SortData sortDescData = new SortData(tableUUID, sortColumnName, sortColumnType, HeaderToolbarElement.SORT_ORDER_DESC, sortDatasetName);
	
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

			String sortField = getCurrentSortField(context.getExporter().getJasperReportsContext(), reportContext, tableUUID, sortDatasetName, sortColumnName, sortColumnType);
			if (sortField != null) 
			{
				String[] sortActionData = HeaderToolbarElementUtils.extractColumnInfo(sortField);
				
				boolean isAscending = HeaderToolbarElement.SORT_ORDER_ASC.equals(sortActionData[2]);
				if (isAscending) {
					sortAscData.setSortOrder(HeaderToolbarElement.SORT_ORDER_NONE);
					sortAscSrc = RESOURCE_SORT_ENABLED_ASC;
					sortAscHoverSrc = RESOURCE_SORT_ENABLED_ASC_HOVER;
				} else {
					sortDescData.setSortOrder(HeaderToolbarElement.SORT_ORDER_NONE);
					sortDescSrc = RESOURCE_SORT_ENABLED_DESC;
					sortDescHoverSrc = RESOURCE_SORT_ENABLED_DESC_HOVER;
				}
			}
			
			// existing filters
			String filterValueStart = "";
			String filterValueEnd = "";
			String filterTypeOperatorValue = "";
			boolean isFiltered = false;
			boolean enableFilterEndParameter = false;
			List<DatasetFilter> fieldFilters = getExistingFiltersForField(context.getExporter().getJasperReportsContext(), reportContext, tableUUID, sortColumnName);

			if (fieldFilters.size() > 0) {
				FieldFilter ff = (FieldFilter)fieldFilters.get(0);
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
				if (ff.getIsValid() != null && !ff.getIsValid()) {
					filterSrc = RESOURCE_FILTER_WRONG;
					filterHoverSrc = RESOURCE_FILTER_WRONG_HOVER;
				} else {
					filterSrc = RESOURCE_FILTER_ENABLED;
					filterHoverSrc = RESOURCE_FILTER_ENABLED_HOVER;
				}
			}
			
			velocityContext.put("isFiltered", isFiltered);
			
			// params for clear filter
			velocityContext.put("filterToRemoveParamName", FilterData.FIELD_NAME);
			velocityContext.put("filterToRemoveParamvalue", sortColumnName);
			
			String filtersJsonString = JacksonUtil.getInstance(context.getExporter().getJasperReportsContext()).getEscapedJsonString(fieldFilters);
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
			velocityContext.put("sortAscData", JacksonUtil.getInstance(context.getExporter().getJasperReportsContext()).getEscapedJsonString(sortAscData));
			velocityContext.put("sortDescHref", sortDescHref);
			velocityContext.put("sortDescData", JacksonUtil.getInstance(context.getExporter().getJasperReportsContext()).getEscapedJsonString(sortDescData));
			
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
	
//	private String getSortLink(JRHtmlExporterContext context, String sortColumnName, String sortColumnType, String sortOrder, String sortTableName) {
//		JRBasePrintHyperlink hyperlink = new JRBasePrintHyperlink();
//		hyperlink.setLinkType("ReportExecution");
//		
//		JRPrintHyperlinkParameters parameters = new JRPrintHyperlinkParameters();
//		parameters.addParameter(new JRPrintHyperlinkParameter(
//				HeaderToolbarElement.REQUEST_PARAMETER_SORT_DATA,
//				String.class.getName(), 
//				HeaderToolbarElementUtils.packSortColumnInfo(sortColumnName, sortColumnType, sortOrder)));
//		parameters.addParameter(new JRPrintHyperlinkParameter(HeaderToolbarElement.REQUEST_PARAMETER_DATASET_RUN, String.class.getName(), sortTableName));
//		
//		ReportContext reportContext = context.getExporter().getReportContext();
//		parameters.addParameter(new JRPrintHyperlinkParameter(WebReportContext.REQUEST_PARAMETER_REPORT_CONTEXT_ID, String.class.getName(), reportContext.getId()));
//		parameters.addParameter(new JRPrintHyperlinkParameter(ReportServlet.REQUEST_PARAMETER_RUN_REPORT, String.class.getName(), "true"));
//		
//		hyperlink.setHyperlinkParameters(parameters);
//		
//		return context.getHyperlinkURL(hyperlink);
//	}
	
	private String getSortActionLink(JRHtmlExporterContext context) {
		JRBasePrintHyperlink hyperlink = new JRBasePrintHyperlink();
		hyperlink.setLinkType("ReportExecution");
		
		JRPrintHyperlinkParameters parameters = new JRPrintHyperlinkParameters();
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
	
	private String getResizeColumnLink(JRHtmlExporterContext context) {
		JRBasePrintHyperlink hyperlink = new JRBasePrintHyperlink();
		hyperlink.setLinkType("ReportExecution");
		
		JRPrintHyperlinkParameters parameters = new JRPrintHyperlinkParameters();
		ReportContext reportContext = context.getExporter().getReportContext();
		parameters.addParameter(new JRPrintHyperlinkParameter(WebReportContext.REQUEST_PARAMETER_REPORT_CONTEXT_ID, String.class.getName(), reportContext.getId()));
		parameters.addParameter(new JRPrintHyperlinkParameter(ReportServlet.REQUEST_PARAMETER_RUN_REPORT, String.class.getName(), "true"));
		
		hyperlink.setHyperlinkParameters(parameters);
		
		return context.getHyperlinkURL(hyperlink);
	}

	private String getCurrentSortField(
		JasperReportsContext jasperReportsContext,
		ReportContext reportContext, 
		String uuid, 
		String sortDatasetName, 
		String sortColumnName, 
		String sortColumnType
		) 
	{
//		String currentSortDataset = (String) reportContext.getParameterValue(
//				HeaderToolbarElement.REQUEST_PARAMETER_DATASET_RUN);
//		if (sortDatasetName == null || !sortDatasetName.equals(currentSortDataset))
//		{
//			// sorting is on a different dataset
//			return null;
//		}
//		
//		String currentTableSortFieldsParam = currentSortDataset + HeaderToolbarElement.SORT_FIELDS_PARAM_SUFFIX;
//		@SuppressWarnings("unchecked")
//		List<JRSortField> existingFields = (List<JRSortField>) reportContext.getParameterValue(currentTableSortFieldsParam);

		JasperDesignCache cache = JasperDesignCache.getInstance(jasperReportsContext, reportContext);
		SortAction action = new SortAction();
		action.init(jasperReportsContext, reportContext);
		CommandTarget target = action.getCommandTarget(UUID.fromString(uuid));
		if (target != null)
		{
			JRIdentifiable identifiable = target.getIdentifiable();
			JRDesignComponentElement componentElement = identifiable instanceof JRDesignComponentElement ? (JRDesignComponentElement)identifiable : null;
			StandardTable table = componentElement == null ? null : (StandardTable)componentElement.getComponent();
			
			JRDesignDatasetRun datasetRun = (JRDesignDatasetRun)table.getDatasetRun();
			
			String datasetName = datasetRun.getDatasetName();
			
			JasperDesign jasperDesign = cache.getJasperDesign(target.getUri());//FIXMEJIVE getJasperReport not design
			JRDesignDataset dataset = (JRDesignDataset)jasperDesign.getDatasetMap().get(datasetName);
			
			List<JRSortField> existingFields =  dataset.getSortFieldsList();
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
		
		return null;
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
	
	private List<DatasetFilter> getExistingFiltersForField(
		JasperReportsContext jasperReportsContext, 
		ReportContext reportContext, 
		String uuid, 
		String filterFieldName
		) 
	{
		JasperDesignCache cache = JasperDesignCache.getInstance(jasperReportsContext, reportContext);
		FilterAction action = new FilterAction();
		action.init(jasperReportsContext, reportContext);
		CommandTarget target = action.getCommandTarget(UUID.fromString(uuid));
		List<DatasetFilter> result = new ArrayList<DatasetFilter>();
		if (target != null)
		{
			JRIdentifiable identifiable = target.getIdentifiable();
			JRDesignComponentElement componentElement = identifiable instanceof JRDesignComponentElement ? (JRDesignComponentElement)identifiable : null;
			StandardTable table = componentElement == null ? null : (StandardTable)componentElement.getComponent();
			
			JRDesignDatasetRun datasetRun = (JRDesignDatasetRun)table.getDatasetRun();
			
			String datasetName = datasetRun.getDatasetName();
			
			JasperDesign jasperDesign = cache.getJasperDesign(target.getUri());//FIXMEJIVE getJasperReport not design
			JRDesignDataset dataset = (JRDesignDataset)jasperDesign.getDatasetMap().get(datasetName);
			
			// get existing filter as JSON string
			String serializedFilters = "[]";
			JRPropertiesMap propertiesMap = dataset.getPropertiesMap();
			if (propertiesMap.getProperty(FilterCommand.DATASET_FILTER_PROPERTY) != null) {
				serializedFilters = propertiesMap.getProperty(FilterCommand.DATASET_FILTER_PROPERTY);
			}
			
			ObjectMapper mapper = new ObjectMapper();
			List<DatasetFilter> existingFilters = null;
			try {
				existingFilters = mapper.readValue(serializedFilters, new TypeReference<List<FieldFilter>>(){});
			} catch (Exception e) {
				throw new JRRuntimeException(e);
			}
			
			if (existingFilters.size() > 0) {
				for (DatasetFilter filter: existingFilters) {
					if (((FieldFilter)filter).getField().equals(filterFieldName)) {
						result.add(filter);
						break;
					}
				}
			}
		}
		
		return result;		
	}

}
