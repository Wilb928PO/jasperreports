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

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.concurrent.atomic.AtomicReference;

import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.JasperReport;
import net.sf.jasperreports.engine.ReportContext;
import net.sf.jasperreports.engine.util.ThreadLocalStack;
import net.sf.jasperreports.extensions.ExtensionsEnvironment;


/**
 * @author Teodor Danciu (teodord@users.sourceforge.net)
 * @version $Id$
 */
public final class RepositoryUtil
{
	//private static final Log log = LogFactory.getLog(RepositoryUtil.class);

	private static ThreadLocalStack localContextStack = new ThreadLocalStack();//FIXMEREPO final?
	
	private static final ThreadLocal<ReportContext> threadReportContext = new InheritableThreadLocal<ReportContext>();

	private static AtomicReference<List<RepositoryService>> repositoryServices = 
			new AtomicReference<List<RepositoryService>>();//FIXMEREPO should this be lazy loaded or not? maybe thread local?
	
	/**
	 * 
	 */
	public static List<RepositoryService> getRepositoryServices()
	{
		List<RepositoryService> cachedServices = repositoryServices.get();
		if (cachedServices != null)
		{
			return cachedServices;
		}
		
		//FIXME we should cache this per thread class loader
		List<RepositoryService> services = 
			ExtensionsEnvironment.getExtensionsRegistry().getExtensions(RepositoryService.class);
		
		// set if not already set
		if (repositoryServices.compareAndSet(null, services))
		{
			return services;
		}
		
		// already set in the meantime by another thread
		return repositoryServices.get();
	}
	
	
	/**
	 * 
	 *
	private static RepositoryContext getRepositoryContext()
	{
		return (RepositoryContext)localContextStack.top();
	}
	
	
	/**
	 * 
	 */
	public static void setRepositoryContext(RepositoryContext context)
	{
		List<RepositoryService> services = getRepositoryServices();
		if (services != null)
		{
			for (RepositoryService service : services)
			{
				service.setContext(context);
			}
		}
		localContextStack.push(context);
	}
	
	
	/**
	 * 
	 */
	public static void revertRepositoryContext()
	{
		//RepositoryContext repositoryContext = getRepositoryContext();
		List<RepositoryService> services = getRepositoryServices();
		if (services != null)
		{
			for (RepositoryService service : services)
			{
				service.revertContext();//FIXMEREPO context?
			}
		}
		localContextStack.pop();
	}
	
	
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
	public static JasperReport getReport(String location) throws JRException
	{
		JasperReport jasperReport = null;
		
		JasperDesignCache cache = getJasperDesignCache();
		if (cache != null)
		{
			jasperReport = cache.getJasperReport(location);
		}

		if (jasperReport == null)
		{
			ReportResource resource = getResource(location, ReportResource.class);
			if (resource == null)
			{
				throw new JRException("Report not found at : " + location);
			}

			jasperReport = resource.getReport();

			if (cache != null)
			{
				cache.set(location, jasperReport);
			}
		}
		
		return jasperReport;
	}


	/**
	 * 
	 */
	public static <K extends Resource> K getResource(String location, Class<K> resourceType) throws JRException
	{
		K resource = null;
		List<RepositoryService> services = getRepositoryServices();
		if (services != null)
		{
			for (RepositoryService service : services)
			{
				resource = service.getResource(location, resourceType);
				if (resource != null)
				{
					break;
				}
			}
		}
		if (resource == null)
		{
			throw new JRException("Resource not found at : " + location);
		}
		return resource;
	}


	/**
	 *
	 */
	public static InputStream getInputStream(String location) throws JRException
	{
		InputStream is = findInputStream(location);
		if (is == null)
		{
			throw new JRException("Input stream not found at : " + location);
		}
		return is;
	}
	
	
	/**
	 *
	 */
	private static InputStream findInputStream(String location) throws JRException
	{
		InputStreamResource inputStreamResource = null;
		List<RepositoryService> services = getRepositoryServices();
		if (services != null)
		{
			for (RepositoryService service : services)
			{
				inputStreamResource = service.getResource(location, InputStreamResource.class);
				if (inputStreamResource != null)
				{
					break;
				}
			}
		}
		return inputStreamResource == null ? null : inputStreamResource.getInputStream();
	}
	
	
	/**
	 *
	 */
	public static byte[] getBytes(String location) throws JRException
	{
		InputStream is = findInputStream(location);
		
		if (is == null)
		{
			throw new JRException("Byte data not found at : " + location);
		}

		ByteArrayOutputStream baos = null;

		try
		{
			baos = new ByteArrayOutputStream();

			byte[] bytes = new byte[10000];
			int ln = 0;
			while ((ln = is.read(bytes)) > 0)
			{
				baos.write(bytes, 0, ln);
			}

			baos.flush();
		}
		catch (IOException e)
		{
			throw new JRException("Error loading byte data from : " + location, e);
		}
		finally
		{
			if (baos != null)
			{
				try
				{
					baos.close();
				}
				catch(IOException e)
				{
				}
			}

			if (is != null)
			{
				try
				{
					is.close();
				}
				catch(IOException e)
				{
				}
			}
		}

		return baos.toByteArray();
	}


	/**
	 * 
	 */
	private static JasperDesignCache getJasperDesignCache()
	{
		ReportContext reportContext = getThreadReportContext();
		if (reportContext != null)
		{
			return JasperDesignCache.getInstance(reportContext);
		}
		return null;
	}
	

	/**
	 * 
	 */
	private RepositoryUtil()
	{
	}
}
