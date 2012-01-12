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
import java.util.List;

import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.JRRuntimeException;
import net.sf.jasperreports.engine.JasperCompileManager;
import net.sf.jasperreports.engine.JasperReport;
import net.sf.jasperreports.engine.ReportContext;
import net.sf.jasperreports.engine.design.JasperDesign;
import net.sf.jasperreports.engine.util.JRLoader;
import net.sf.jasperreports.engine.xml.JRXmlLoader;
import net.sf.jasperreports.engine.xml.JRXmlWriter;




/**
 * @author Teodor Danciu (teodord@users.sourceforge.net)
 * @version $Id: FileRepositoryService.java 4882 2012-01-09 14:54:19Z teodord $
 */
public class CachedJasperDesignRepositoryService implements RepositoryService
{
	/**
	 * 
	 */
	private static final CachedJasperDesignRepositoryService INSTANCE = new CachedJasperDesignRepositoryService();

	/**
	 * 
	 */
	private static final ThreadLocal<ReportContext> threadReportContext = new InheritableThreadLocal<ReportContext>();

	/**
	 * 
	 */
	public static CachedJasperDesignRepositoryService getInstance()
	{
		return INSTANCE;
	}
	
	/**
	 * 
	 */
	private CachedJasperDesignRepositoryService()
	{
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
	private JasperDesignReportResourceCache getCache()
	{
		ReportContext reportContext = getThreadReportContext();
		if (reportContext == null)
		{
			throw new JRRuntimeException("Thread report context not set.");
		}

		return JasperDesignReportResourceCache.getInstance(reportContext);
	}
	
	/**
	 * 
	 */
	private JasperDesignReportResource getResourceFromCache(String uri)
	{
		return getCache().getResource(uri);
	}
	
	/**
	 * 
	 */
	private JasperDesign getJasperDesignFromRepositories(String uri)
	{
		List<RepositoryService> services = RepositoryUtil.getRepositoryServices();
		if (services != null)
		{
			for (RepositoryService service : services)
			{
				if (!service.getClass().equals(CachedJasperDesignRepositoryService.class))
				{
					JasperDesign jasperDesign = getJasperDesignFromRepository(uri, service);
					if (jasperDesign != null)
					{
						return jasperDesign;
					}
				}
			}
		}
		
		return null;
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
			if (jasperIs != null)
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
		
		if (jrxmlIs != null)
		{
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
		}
		
		return jasperDesign;
	}

	/**
	 * 
	 */
	public void setContext(RepositoryContext context) //FIXMEREPO the context is useless here; consider refactoring
	{
	}
	
	public void revertContext()
	{
	}

	/**
	 * 
	 */
	public InputStream getInputStream(String uri)
	{
		throw new UnsupportedOperationException();
	}
	
	/**
	 * 
	 */
	public Resource getResource(String uri)
	{
		throw new JRRuntimeException("Not implemented.");//FIXMEREPO
	}
	
	/**
	 * 
	 */
	public <K extends Resource> K getResource(String uri, Class<K> resourceType)
	{
		if (ReportResource.class.isAssignableFrom(resourceType))
		{
			JasperDesignReportResource resource = getResourceFromCache(uri);
			
			if (resource == null)
			{
				JasperDesign jasperDesign = getJasperDesignFromRepositories(uri);

				if (jasperDesign != null)
				{
					resource = new JasperDesignReportResource();
					resource.setJasperDesign(jasperDesign);
					
					getCache().setResource(uri, resource);
				}
			}
			
			if (resource != null)
			{
				JasperReport jasperReport = resource.getReport();
				if (jasperReport == null)
				{
					try
					{
						jasperReport = JasperCompileManager.compileReport(resource.getJasperDesign());
						resource.setReport(jasperReport);
					}
					catch (JRException e)
					{
						throw new JRRuntimeException(e);
					}
				}
			}
			
			return (K)resource;
		}

		return null;
	}

	/**
	 * 
	 */
	public void saveResource(String uri, Resource resource)
	{
//		PersistenceService persistenceService = PersistenceUtil.getPersistenceService(FileRepositoryService.class, resource.getClass());
//		if (persistenceService != null)
//		{
//			persistenceService.save(resource, uri, this);
//		}
	}
}
