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
package net.sf.jasperreports.repo;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.JRRuntimeException;
import net.sf.jasperreports.engine.JasperReport;
import net.sf.jasperreports.engine.ReportContext;
import net.sf.jasperreports.engine.design.JasperDesign;
import net.sf.jasperreports.engine.util.JRLoader;
import net.sf.jasperreports.engine.xml.JRXmlLoader;
import net.sf.jasperreports.engine.xml.JRXmlWriter;



/**
 * @author Teodor Danciu (teodord@users.sourceforge.net)
 * @version $Id: FileRepositoryService.java 4819 2011-11-28 15:24:25Z lucianc $
 */
public class CachedJasperDesignPersistenceService implements PersistenceService
{
	/**
	 * 
	 */
	private static final String PARAMETER_JASPER_DESIGN_CACHE = "net.sf.jasperreports.parameter.jasperdesign.cache";

	/**
	 * 
	 */
	private static final ThreadLocal<ReportContext> threadReportContext = new InheritableThreadLocal<ReportContext>();

	/**
	 * 
	 */
	public static ReportContext getThreadReportContext()
	{
		return threadReportContext.get();
	}

	/**
	 * 
	 */
	public static void setThreadReportContext(ReportContext reportContext)
	{
		threadReportContext.set(reportContext);
	}

	/**
	 * 
	 */
	public static void resetThreadReportContext()
	{
		threadReportContext.set(null);
	}
	
	/**
	 * 
	 */
	private Map<String, JasperDesign> getCache()
	{
		ReportContext reportContext = getThreadReportContext();
		if (reportContext == null)
		{
			throw new JRRuntimeException("Thread report context not set.");
		}

		Map<String, JasperDesign> cache = (Map<String, JasperDesign>)reportContext.getParameterValue(PARAMETER_JASPER_DESIGN_CACHE);
		
		if (cache == null)
		{
			cache = new HashMap<String, JasperDesign>();
			reportContext.setParameterValue(PARAMETER_JASPER_DESIGN_CACHE, cache);
		}
		
		return cache;
	}
	
	/**
	 * 
	 */
	private JasperDesign getJasperDesignFromCache(String uri)
	{
		return getCache().get(uri);
	}
	
	/**
	 * 
	 */
	private JasperDesign getJasperDesignFromRepository(String uri, RepositoryService repositoryService)
	{
		String reportUri = uri;
		String lcReportUri = reportUri.toLowerCase();
		if (lcReportUri.endsWith(".jasper"))
		{
			reportUri = reportUri.substring(0, lcReportUri.lastIndexOf(".jasper"));
		}
		else if (lcReportUri.endsWith(".jrxml"))
		{
			reportUri = reportUri.substring(0, lcReportUri.lastIndexOf(".jrxml"));
		}
		String jasperUri = reportUri + ".jasper"; 
		String jrxmlUri = reportUri + ".jrxml";
		
		InputStreamResource jrxmlResource = repositoryService.getResource(jrxmlUri, InputStreamResource.class);
		InputStreamResource jasperResource = repositoryService.getResource(jasperUri, InputStreamResource.class);
		
		InputStream jrxmlIs = jrxmlResource == null ? null : jrxmlResource.getInputStream();
		InputStream jasperIs = jasperResource == null ? null : jasperResource.getInputStream();

		if (jrxmlIs == null)
		{
			if (jasperIs == null)
			{
				throw new JRRuntimeException("Report not found: " + reportUri);
			}
			else
			{
				try
				{
					JasperReport jasperReport = (JasperReport)JRLoader.loadObject(jasperIs);
					ByteArrayOutputStream baos = new ByteArrayOutputStream();
					JRXmlWriter.writeReport(jasperReport, baos, "UTF-8");
					jrxmlIs = new ByteArrayInputStream(baos.toByteArray());
				}
				catch (JRException e)
				{
					throw new JRRuntimeException(e);
				}
				finally
				{
					try
					{
						jasperIs.close();
					}
					catch (IOException e)
					{
					}
				}
			}
		}

		JasperDesign jasperDesign = null;
		
		try
		{
			jasperDesign = JRXmlLoader.load(jrxmlIs);
		}
		catch (JRException e)
		{
			throw new JRRuntimeException(e);
		}
		finally
		{
			try
			{
				jrxmlIs.close();
			}
			catch (IOException e)
			{
			}
		}
		
		return jasperDesign;
	}
	
	
	/**
	 * 
	 */
	public Resource load(String uri, RepositoryService repositoryService)
	{
		JasperDesign jasperDesign = getJasperDesignFromCache(uri);
		
		if (jasperDesign == null)
		{
			jasperDesign = getJasperDesignFromRepository(uri, repositoryService);
		}
		
		JasperDesignResource resource = null;

		if (jasperDesign != null)
		{
			resource = new JasperDesignResource();
			resource.setJasperDesign(jasperDesign);
		}

		return resource;
	}


	/**
	 * 
	 */
	public void save(Resource resource, String uri, RepositoryService repositoryService) 
	{
		// TODO Auto-generated method stub
	}

}
