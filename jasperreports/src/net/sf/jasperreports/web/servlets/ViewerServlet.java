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
package net.sf.jasperreports.web.servlets;

import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import net.sf.jasperreports.components.sort.SortElement;
import net.sf.jasperreports.engine.JRConstants;
import net.sf.jasperreports.engine.JRExporterParameter;
import net.sf.jasperreports.engine.JRRuntimeException;
import net.sf.jasperreports.engine.export.JRHtmlExporterParameter;
import net.sf.jasperreports.engine.export.JRXhtmlExporter;
import net.sf.jasperreports.engine.util.JRProperties;
import net.sf.jasperreports.repo.WebFileRepositoryService;
import net.sf.jasperreports.web.WebReportContext;
import net.sf.jasperreports.web.actions.AbstractAction;
import net.sf.jasperreports.web.actions.ResizeColumnAction;
import net.sf.jasperreports.web.util.ReportExecutionHyperlinkProducerFactory;
import net.sf.jasperreports.web.util.VelocityUtil;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.velocity.VelocityContext;
import org.codehaus.jackson.JsonParseException;
import org.codehaus.jackson.JsonParser;
import org.codehaus.jackson.map.JsonMappingException;
import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.map.jsontype.NamedType;


/**
 * @author Teodor Danciu (teodord@users.sourceforge.net)
 * @version $Id: ReportServlet.java 4938 2012-01-26 17:03:20Z narcism $
 */
public class ViewerServlet extends HttpServlet
{
	private static final long serialVersionUID = JRConstants.SERIAL_VERSION_UID;
	
	private static final Log log = LogFactory.getLog(ViewerServlet.class);

	public static final String REQUEST_PARAMETER_REPORT_URI = "jr.uri";
	public static final String REQUEST_PARAMETER_IGNORE_PAGINATION = "jr.ignrpg";
	public static final String REQUEST_PARAMETER_RUN_REPORT = "jr.run";
	
	public static final String REQUEST_PARAMETER_ASYNC = "jr.async";

	public static final String REQUEST_PARAMETER_ACTION = "jr.action";
	
	/**
	 * 
	 */
	public void init(ServletConfig config) throws ServletException 
	{
		super.init(config);
		
		WebFileRepositoryService.setApplicationRealPath(config.getServletContext().getRealPath("/"));
	}
	

	/**
	 *
	 */
	public void service(
		HttpServletRequest request,
		HttpServletResponse response
		) throws IOException, ServletException
	{
		response.setContentType("text/html; charset=UTF-8");
		
		// Set to expire far in the past.
		response.setHeader("Expires", "Sat, 6 May 1995 12:00:00 GMT");
		// Set standard HTTP/1.1 no-cache headers.
		response.setHeader("Cache-Control", "no-store, no-cache, must-revalidate");
		// Set IE extended HTTP/1.1 no-cache headers (use addHeader).
		response.addHeader("Cache-Control", "post-check=0, pre-check=0");
		// Set standard HTTP/1.0 no-cache header.
		response.setHeader("Pragma", "no-cache");

		PrintWriter out = response.getWriter();

		WebReportContext webReportContext = WebReportContext.getInstance(request);

		try
		{
			ReportServlet.runReport(request, webReportContext);
			
			render(request, webReportContext, out);
		}
		catch (Exception e)
		{
			log.error("Error on report execution", e);
			
			out.println("<html>");//FIXMEJIVE do we need to render this? or should this be done by the viewer?
			out.println("<head>");
			out.println("<title>JasperReports - Web Application Sample</title>");
			out.println("<link rel=\"stylesheet\" type=\"text/css\" href=\"../stylesheet.css\" title=\"Style\">");
			out.println("</head>");
			
			out.println("<body bgcolor=\"white\">");

			out.println("<span class=\"bnew\">JasperReports encountered this error :</span>");
			out.println("<pre>");

			e.printStackTrace(out);

			out.println("</pre>");

			out.println("</body>");
			out.println("</html>");
		}
	}


	//public static final String APPLICATION_CONTEXT_PATH_VAR = "APPLICATION_CONTEXT_PATH";

	public static final String REQUEST_PARAMETER_PAGE = "jr.page";
	
	public static final String REQUEST_PARAMETER_PAGE_TIMESTAMP = "jr.pagetimestamp";
	
	protected static final String TEMPLATE_HEADER_NOPAGES = "net/sf/jasperreports/web/servlets/resources/templates/HeaderTemplateNoPages.vm";
	protected static final String TEMPLATE_FOOTER_NOPAGES = "net/sf/jasperreports/web/servlets/resources/templates/FooterTemplateNoPages.vm";
	
	/**
	 *
	 */
	public void render(
		HttpServletRequest request,
		WebReportContext webReportContext,
		PrintWriter writer
		)// throws JRException //IOException, ServletException
	{
		writer.write(getHeader(request, webReportContext));
		
		JasperPrintAccessor jasperPrintAccessor = 
			(JasperPrintAccessor) webReportContext.getParameterValue(
				WebReportContext.REPORT_CONTEXT_PARAMETER_JASPER_PRINT_ACCESSOR
				);
		Integer pageCount = jasperPrintAccessor.getTotalPageCount();
		// if the page count is null, it means that the fill is not yet done but there is at least a page
		boolean hasPages = pageCount == null || pageCount > 0;//FIXMEJIVE we should call pageStatus here
		
		JRXhtmlExporter exporter = new JRXhtmlExporter();

		ReportPageStatus pageStatus = null;

		try
		{
			if (hasPages)
			{
				String reportPage = request.getParameter(REQUEST_PARAMETER_PAGE);
				int pageIdx = reportPage == null ? 0 : Integer.parseInt(reportPage);
				String pageTimestamp = request.getParameter(REQUEST_PARAMETER_PAGE_TIMESTAMP);
				Long timestamp = pageTimestamp == null ? null : Long.valueOf(pageTimestamp);
				
				pageStatus = jasperPrintAccessor.pageStatus(pageIdx, timestamp);
				
				if (pageStatus.getError() != null)
				{
					throw new JRRuntimeException("Error occured during report generation", pageStatus.getError());
				}
				
				if (!pageStatus.pageExists())
				{
					throw new JRRuntimeException("Page " + pageIdx + " not found in report");
				}
				
				exporter.setParameter(JRExporterParameter.PAGE_INDEX, pageIdx);
			}
			else
			{
				pageStatus = ReportPageStatus.PAGE_FINAL;
			}
			
			exporter.setReportContext(webReportContext);
			exporter.setParameter(JRExporterParameter.JASPER_PRINT, jasperPrintAccessor.getJasperPrint());
			exporter.setParameter(JRExporterParameter.OUTPUT_WRITER, writer);
			exporter.setParameter(JRHtmlExporterParameter.IMAGES_URI, "image?" + WebReportContext.REQUEST_PARAMETER_REPORT_CONTEXT_ID + "=" + webReportContext.getId() + "&image=");
			
			exporter.setParameter(JRHtmlExporterParameter.HTML_HEADER, "");//getHeader(request, webReportContext, hasPages, pageStatus));
			exporter.setParameter(JRHtmlExporterParameter.BETWEEN_PAGES_HTML, "");//getBetweenPages(request, webReportContext));
			exporter.setParameter(JRHtmlExporterParameter.HTML_FOOTER, "");//getFooter(request, webReportContext, hasPages, pageStatus));
			
			exporter.setParameter(
				JRHtmlExporterParameter.HYPERLINK_PRODUCER_FACTORY, 
				ReportExecutionHyperlinkProducerFactory.getInstance(request)
				);
			
			//TODO lucianc do not export if the page has not modified
			exporter.exportReport();

//			try
//			{
//			}
//			catch (JRException e)
//			{
//				StringWriter stackTraceWriter = new StringWriter(128);
//				e.printStackTrace(new PrintWriter(stackTraceWriter));
//				stackTraceWriter.flush();
//				stackTraceWriter.close();
//				VelocityContext exceptionContext = new VelocityContext();
//				exceptionContext.put("stackTrace", stackTraceWriter.getBuffer().toString());
	//
//				out.print(VelocityUtil.processTemplate(TEMPLATE_EXCEPTION, exceptionContext));
//			}
		}
		catch (Exception e)
		{
			StringWriter stackTraceWriter = new StringWriter(128);
			e.printStackTrace(new PrintWriter(stackTraceWriter));
			stackTraceWriter.flush();
			try
			{
				stackTraceWriter.close();
			}
			catch (IOException ioe)
			{
				//nothing to do
			}
			VelocityContext exceptionContext = new VelocityContext();
			exceptionContext.put("stackTrace", stackTraceWriter.getBuffer().toString());

			writer.print(VelocityUtil.processTemplate(TEMPLATE_EXCEPTION, exceptionContext));
		}
		
		writer.write(getFooter(request, webReportContext, hasPages, pageStatus));
	}

	protected String getCurrentUrl(HttpServletRequest request, WebReportContext webReportContext) 
	{
		String newQueryString = "";
		
//		Enumeration<String> paramNames = request.getParameterNames();
//		while (paramNames.hasMoreElements()) {
//			String paramName = paramNames.nextElement();
//			if (!paramName.equals(AbstractViewer.REQUEST_PARAMETER_PAGE)) {
//				newQueryString += paramName + "=" + request.getParameter(paramName) + "&";
//			}
//		}
//		
//		newQueryString = newQueryString.substring(0, newQueryString.lastIndexOf("&"));
		
//		if (!newQueryString.contains(WebReportContext.REQUEST_PARAMETER_REPORT_CONTEXT_ID))
//		{
//			newQueryString += "&" + WebReportContext.REQUEST_PARAMETER_REPORT_CONTEXT_ID + "=" + webReportContext.getId();
//		}
		
		newQueryString = ReportServlet.REQUEST_PARAMETER_REPORT_URI + "=" + webReportContext.getParameterValue(ReportServlet.REQUEST_PARAMETER_REPORT_URI) + 
					"&" + SortElement.REQUEST_PARAMETER_DATASET_RUN + "=" + webReportContext.getParameterValue(SortElement.REQUEST_PARAMETER_DATASET_RUN) +
					"&" + WebReportContext.REQUEST_PARAMETER_REPORT_CONTEXT_ID + "=" + webReportContext.getId();
		return request.getContextPath() + request.getServletPath() + "?" + newQueryString;
	}


	/**
	 *
	 */
	private static final String RESOURCE_GLOBAL_JS = "net/sf/jasperreports/web/servlets/resources/global.js";
	private static final String RESOURCE_GLOBAL_CSS = "net/sf/jasperreports/web/servlets/resources/global.css";

//	private static final String APPLICATION_CONTEXT_PATH_VAR = "APPLICATION_CONTEXT_PATH";

//	private static final String PARAMETER_TOOLBAR = "toolbar";
	private static final String PARAMETER_IS_AJAX= "isajax";
	
	private static final String TEMPLATE_HEADER= "net/sf/jasperreports/web/servlets/resources/templates/HeaderTemplate.vm";
	private static final String TEMPLATE_FOOTER= "net/sf/jasperreports/web/servlets/resources/templates/FooterTemplate.vm";
	private static final String TEMPLATE_EXCEPTION= "net/sf/jasperreports/web/servlets/resources/templates/ExceptionTemplate.vm";

	protected String getHeader(HttpServletRequest request, WebReportContext webReportContext)
	{
		VelocityContext headerContext = new VelocityContext();
//		if (hasPages) 
//		{
			String webResourcesBasePath = JRProperties.getProperty("net.sf.jasperreports.web.resources.base.path");//FIXMEJIVE reuse this code
			if (webResourcesBasePath == null)
			{
				webResourcesBasePath = request.getContextPath() + ResourceServlet.DEFAULT_PATH + "?" + ResourceServlet.RESOURCE_URI + "=";
			}
			headerContext.put("isAjax", request.getParameter(PARAMETER_IS_AJAX) != null && request.getParameter(PARAMETER_IS_AJAX).equals("true"));
			headerContext.put("contextPath", request.getContextPath());
			headerContext.put("globaljs", webResourcesBasePath + RESOURCE_GLOBAL_JS);
			headerContext.put("globalcss", webResourcesBasePath + RESOURCE_GLOBAL_CSS);
	//		headerContext.put("showToolbar", request.getParameter(PARAMETER_TOOLBAR) != null && request.getParameter(PARAMETER_TOOLBAR).equals("true"));
			headerContext.put("showToolbar", Boolean.TRUE);
			headerContext.put("toolbarId", "toolbar_" + request.getSession().getId() + "_" + (int)(Math.random() * 99999));
			headerContext.put("currentUrl", getCurrentUrl(request, webReportContext));
			headerContext.put("strRunReportParam", ReportServlet.REQUEST_PARAMETER_RUN_REPORT + "=false");
	
			JasperPrintAccessor jasperPrintAccessor = (JasperPrintAccessor) webReportContext.getParameterValue(
					WebReportContext.REPORT_CONTEXT_PARAMETER_JASPER_PRINT_ACCESSOR);
			headerContext.put("totalPages", jasperPrintAccessor.getTotalPageCount());
	
			String reportPage = request.getParameter(REQUEST_PARAMETER_PAGE);
			headerContext.put("currentPage", (reportPage != null ? reportPage : "0"));
			
//			if (!pageStatus.isPageFinal())
//			{
//				headerContext.put("pageTimestamp", pageStatus.getTimestamp());
//			}
			
			return VelocityUtil.processTemplate(TEMPLATE_HEADER, headerContext);
//		} else 
//		{
//			return VelocityUtil.processTemplate(AbstractViewer.TEMPLATE_HEADER_NOPAGES, headerContext);
//		}
		
	}

	protected String getFooter(HttpServletRequest request, WebReportContext webReportContext, boolean hasPages, 
			ReportPageStatus pageStatus) 
	{
		VelocityContext footerContext = new VelocityContext();
		if (hasPages) {
			return VelocityUtil.processTemplate(TEMPLATE_FOOTER, footerContext);
		} else 
		{
			return VelocityUtil.processTemplate(AbstractViewer.TEMPLATE_FOOTER_NOPAGES, footerContext);
		}
	}


	/**
	 *
	 */
	public static void main(String[] args) {
		ObjectMapper mapper = new ObjectMapper();
		mapper.configure(JsonParser.Feature.ALLOW_SINGLE_QUOTES, true);
		mapper.registerSubtypes(new NamedType(ResizeColumnAction.class, "net.sf.jasperreports.web.actions.ResizeColumnAction"));
		
		String strColData = "{'actionName': 'net.sf.jasperreports.web.actions.ResizeColumnAction', 'resizeColumnData': {'columnIndex': 1, 'width': 100, 'direction': 'left'}}";
		try {
			AbstractAction rcd = mapper.readValue(strColData, AbstractAction.class);
			System.out.println("rcd: " + ((ResizeColumnAction)rcd).getResizeColumnData());
		} catch (JsonParseException e) {
			e.printStackTrace();
		} catch (JsonMappingException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

}
