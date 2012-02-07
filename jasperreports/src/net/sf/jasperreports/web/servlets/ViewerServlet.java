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
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import net.sf.jasperreports.engine.JRConstants;
import net.sf.jasperreports.engine.util.JRProperties;
import net.sf.jasperreports.web.WebReportContext;
import net.sf.jasperreports.web.util.JacksonUtil;
import net.sf.jasperreports.web.util.VelocityUtil;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.velocity.VelocityContext;


/**
 * @author Teodor Danciu (teodord@users.sourceforge.net)
 * @version $Id: ReportServlet.java 4938 2012-01-26 17:03:20Z narcism $
 */
public class ViewerServlet extends AbstractServlet
{
	private static final long serialVersionUID = JRConstants.SERIAL_VERSION_UID;
	
	private static final Log log = LogFactory.getLog(ViewerServlet.class);
	
	private static final String TEMPLATE_HEADER = "net/sf/jasperreports/web/servlets/resources/viewer/HeaderTemplate.vm";
	private static final String TEMPLATE_BODY = "net/sf/jasperreports/web/servlets/resources/viewer/BodyTemplate.vm";
	private static final String TEMPLATE_FOOTER = "net/sf/jasperreports/web/servlets/resources/viewer/FooterTemplate.vm";
	
	private static final String RESOURCE_JR_GLOBAL_JS = "net/sf/jasperreports/web/servlets/resources/jasperreports-global.js";
	private static final String RESOURCE_JR_GLOBAL_CSS = "net/sf/jasperreports/web/servlets/resources/jasperreports-global.css";
	private static final String RESOURCE_VIEWER_TOOLBAR_JS = "net/sf/jasperreports/web/servlets/resources/jasperreports-reportViewerToolbar.js";

	public static final String REQUEST_PARAMETER_REPORT_URI = "jr.uri";
	

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


	/**
	 *
	 */
	public void render(
		HttpServletRequest request,
		WebReportContext webReportContext,
		PrintWriter writer
		)
	{
		String toolbarId = "toolbar_" + request.getSession().getId() + "_" + (int)(Math.random() * 99999);
		
		writer.write(getHeader(request, webReportContext, toolbarId));
		writer.write(getBody(request, webReportContext, toolbarId));
		writer.write(getFooter());
	}

	protected String getCurrentUrl(HttpServletRequest request, WebReportContext webReportContext) 
	{
		String newQueryString = request.getQueryString();
		return request.getContextPath() + ReportServlet.PATH + "?" + newQueryString + "&" + WebReportContext.REQUEST_PARAMETER_REPORT_CONTEXT_ID + "=" + webReportContext.getId();
	}


	protected String getHeader(HttpServletRequest request, WebReportContext webReportContext, String toolbarId)
	{
		VelocityContext headerContext = new VelocityContext();
		String webResourcesBasePath = JRProperties.getProperty("net.sf.jasperreports.web.resources.base.path");//FIXMEJIVE reuse this code
		if (webResourcesBasePath == null)
		{
			webResourcesBasePath = request.getContextPath() + ResourceServlet.DEFAULT_PATH + "?" + ResourceServlet.RESOURCE_URI + "=";
		}
		headerContext.put("contextPath", request.getContextPath());
		headerContext.put("jasperreports_global_js", webResourcesBasePath + RESOURCE_JR_GLOBAL_JS);
		headerContext.put("jasperreports_reportViewerToolbar_js", webResourcesBasePath + RESOURCE_VIEWER_TOOLBAR_JS);
		headerContext.put("jasperreports_global_css", webResourcesBasePath + RESOURCE_JR_GLOBAL_CSS);
		headerContext.put("showToolbar", Boolean.TRUE);
		headerContext.put("toolbarId", toolbarId);
		headerContext.put("currentUrl", getCurrentUrl(request, webReportContext));

		return VelocityUtil.processTemplate(TEMPLATE_HEADER, headerContext);
	}
	
	protected String getBody(HttpServletRequest request, WebReportContext webReportContext, String toolbarId) {
		VelocityContext bodyContext = new VelocityContext();
		
		Enumeration<String> paramsEnum = request.getParameterNames();
		Map<String, String> paramsMap = new HashMap<String, String>();
		
		while(paramsEnum.hasMoreElements()) {
			String param = paramsEnum.nextElement();
			paramsMap.put(param, request.getParameter(param));
		}
		paramsMap.put(WebReportContext.REQUEST_PARAMETER_REPORT_CONTEXT_ID, String.valueOf(webReportContext.getId()));
//		paramsMap.put(ReportServlet.REQUEST_PARAMETER_TOOLBAR_ID, toolbarId);
		
		bodyContext.put("reportUrl", request.getContextPath() + ReportServlet.PATH);
		bodyContext.put("jsonParamsObject", JacksonUtil.getInstance(getJasperReportsContext()).getEscapedJsonString(paramsMap));
		bodyContext.put("toolbarId", toolbarId);
		
		return VelocityUtil.processTemplate(TEMPLATE_BODY, bodyContext);
	}

	protected String getFooter() 
	{
		return VelocityUtil.processTemplate(TEMPLATE_FOOTER, new VelocityContext());
	}

}
