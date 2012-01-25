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

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import net.sf.jasperreports.engine.JRConstants;
import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.JRParameter;
import net.sf.jasperreports.engine.ReportContext;
import net.sf.jasperreports.repo.WebFileRepositoryService;
import net.sf.jasperreports.web.WebReportContext;
import net.sf.jasperreports.web.actions.AbstractAction;
import net.sf.jasperreports.web.actions.Action;
import net.sf.jasperreports.web.actions.ClearFilterAction;
import net.sf.jasperreports.web.actions.FilterAction;
import net.sf.jasperreports.web.actions.RedoAction;
import net.sf.jasperreports.web.actions.ResizeColumnAction;
import net.sf.jasperreports.web.actions.SaveAction;
import net.sf.jasperreports.web.actions.SortAction;
import net.sf.jasperreports.web.actions.UndoAction;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.codehaus.jackson.JsonParseException;
import org.codehaus.jackson.JsonParser;
import org.codehaus.jackson.map.JsonMappingException;
import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.map.jsontype.NamedType;


/**
 * @author Teodor Danciu (teodord@users.sourceforge.net)
 * @version $Id$
 */
public class ReportServlet extends HttpServlet
{
	private static final long serialVersionUID = JRConstants.SERIAL_VERSION_UID;
	
	private static final Log log = LogFactory.getLog(ReportServlet.class);

	public static final String REQUEST_PARAMETER_REPORT_URI = "jr.uri";
	public static final String REQUEST_PARAMETER_IGNORE_PAGINATION = "jr.ignrpg";
	public static final String REQUEST_PARAMETER_RUN_REPORT = "jr.run";
	public static final String REQUEST_PARAMETER_REPORT_VIEWER = "jr.vwr";
	
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
			runReport(request, webReportContext);
			
			String viewer = request.getParameter(REQUEST_PARAMETER_REPORT_VIEWER);
			if (viewer == null || viewer.trim().length() == 0)
			{
				new DefaultViewer().render(request, webReportContext, out);
			}
			else
			{
				//FIXMEJIVE
				new NoDecorationViewer().render(request, webReportContext, out);
			}
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
	public void runReport(
		HttpServletRequest request, //FIXMEJIVE put request in report context, maybe as a thread local?
		WebReportContext webReportContext
		) throws JRException //IOException, ServletException
	{
		JasperPrintAccessor jasperPrintAccessor = 
			(JasperPrintAccessor) webReportContext.getParameterValue(
				WebReportContext.REPORT_CONTEXT_PARAMETER_JASPER_PRINT_ACCESSOR
			);

		String run = request.getParameter(REQUEST_PARAMETER_RUN_REPORT);
		if (jasperPrintAccessor == null || Boolean.valueOf(run))
		{
			String reportUri = request.getParameter(REQUEST_PARAMETER_REPORT_URI);
			if (reportUri != null)
			{
				webReportContext.setParameterValue(REQUEST_PARAMETER_REPORT_URI, reportUri);
			}
			
			Boolean isIgnorePagination = Boolean.valueOf(request.getParameter(REQUEST_PARAMETER_IGNORE_PAGINATION));
			if (isIgnorePagination != null) 
			{
				webReportContext.setParameterValue(JRParameter.IS_IGNORE_PAGINATION, isIgnorePagination);
			}		
			
			String async = request.getParameter(REQUEST_PARAMETER_ASYNC);
			if (async != null)
			{
				webReportContext.setParameterValue(REQUEST_PARAMETER_ASYNC, Boolean.valueOf(async));
			}

			Action action = getAction(webReportContext, request.getParameter(REQUEST_PARAMETER_ACTION));

			Controller controller = new Controller();
			
			controller.runReport(webReportContext, action);
		}
	}


	/**
	 *
	 */
	private Action getAction(ReportContext webReportContext, String jsonData)
	//private Action getAction(ReportContext webReportContext, String reportUri, String jsonData)
	{
		AbstractAction result = null;
		if (jsonData != null) {
			ObjectMapper mapper = new ObjectMapper();
			mapper.configure(JsonParser.Feature.ALLOW_SINGLE_QUOTES, true);
			
			mapper.registerSubtypes(new NamedType(ResizeColumnAction.class, "resize"));
			mapper.registerSubtypes(new NamedType(UndoAction.class, "undo"));
			mapper.registerSubtypes(new NamedType(RedoAction.class, "redo"));
			mapper.registerSubtypes(new NamedType(SaveAction.class, "save"));
			mapper.registerSubtypes(new NamedType(SortAction.class, "sort"));
			mapper.registerSubtypes(new NamedType(FilterAction.class, "filter"));
			mapper.registerSubtypes(new NamedType(ClearFilterAction.class, "clearfilter"));
			
			try {
				result = mapper.readValue(jsonData, AbstractAction.class);
				result.init(webReportContext);//, reportUri);
			} catch (JsonParseException e) {
				e.printStackTrace();
			} catch (JsonMappingException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		return result;
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
