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
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

import net.sf.jasperreports.engine.JRBand;
import net.sf.jasperreports.engine.JRElement;
import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.JRRuntimeException;
import net.sf.jasperreports.engine.JasperCompileManager;
import net.sf.jasperreports.engine.JasperReport;
import net.sf.jasperreports.engine.ReportContext;
import net.sf.jasperreports.engine.design.JRDesignComponentElement;
import net.sf.jasperreports.engine.design.JasperDesign;
import net.sf.jasperreports.engine.xml.JRXmlLoader;
import net.sf.jasperreports.engine.xml.JRXmlWriter;
import net.sf.jasperreports.web.commands.CommandTarget;



/**
 * @author Teodor Danciu (teodord@users.sourceforge.net)
 * @version $Id: FileRepositoryService.java 4819 2011-11-28 15:24:25Z lucianc $
 */
public class JasperDesignCache
{
	/**
	 * 
	 */
	private static final String PARAMETER_JASPER_DESIGN_CACHE = "net.sf.jasperreports.parameter.jasperdesign.cache";

	/**
	 * 
	 */
	private Map<String, JasperDesignReportResource> cachedResourcesMap = new HashMap<String, JasperDesignReportResource>();
	//private Map<UUID, String> cachedSubreportsMap = new HashMap<UUID, String>();

	/**
	 * 
	 */
	public static JasperDesignCache getInstance(ReportContext reportContext)
	{
		JasperDesignCache cache = (JasperDesignCache)reportContext.getParameterValue(PARAMETER_JASPER_DESIGN_CACHE);
		
		if (cache == null)
		{
			cache = new JasperDesignCache();
			reportContext.setParameterValue(PARAMETER_JASPER_DESIGN_CACHE, cache);
		}
		
		return cache;
	}
	
	/**
	 * 
	 */
	private JasperDesignCache()
	{
	}
	
	/**
	 * 
	 */
	public JasperReport getJasperReport(String uri)
	{
		JasperDesignReportResource resource = getResource(uri);
		if (resource != null)
		{
			return resource.getReport();
		}
		return null;
	}

	/**
	 * 
	 */
	public JasperDesign getJasperDesign(String uri)
	{
		JasperDesignReportResource resource = getResource(uri);
		if (resource != null)
		{
			return resource.getJasperDesign();
		}
		return null;
	}

	/**
	 * 
	 *
	public JasperDesign getJasperDesign(UUID subreportElementUUID)
	{
		String uri = cachedSubreportsMap.get(subreportElementUUID);
		if (uri != null)
		{
			JasperDesignReportResource resource = getResource(uri);
			if (resource != null)
			{
				return resource.getJasperDesign();
			}
		}
		return null;
	}

	/**
	 * 
	 */
	public CommandTarget getCommandTarget(UUID uuid)
	{
//		Collection<JasperDesignReportResource> resources = cachedResourcesMap.values();
//		for (JasperDesignReportResource resource : resources)
//		{
		Set<String> uris = cachedResourcesMap.keySet();
		for (String uri : uris)
		{
			CommandTarget target = new CommandTarget();
			target.setUri(uri);
			
			JasperDesign jasperDesign = getJasperDesign(uri);
			
			//FIXMEJIVE now we just look for table components in title and summary bands
			// this is strongly hardcoded to allow the reports in the webapp-repo sample to work
			JRBand[] bands = new JRBand[]{jasperDesign.getTitle(), jasperDesign.getSummary()};
			for (JRBand band : bands)
			{
				if (band != null)
				{
					for (JRElement element : band.getElements())
					{
						if (element instanceof JRDesignComponentElement) 
						{
							if (uuid.equals(element.getUUID()))
							{
								target.setIdentifiable(element);
								return target;
							}
						}
					}
				}
			}
		}
		return null;
	}

	/**
	 * 
	 */
	public void set(String uri, JasperReport jasperReport)
	{
		JasperDesignReportResource resource = new JasperDesignReportResource();
		resource.setReport(jasperReport);
		cachedResourcesMap.put(uri, resource);
	}

	/**
	 * 
	 */
	public void set(String uri, JasperDesign jasperDesign)
	{
		JasperDesignReportResource resource = new JasperDesignReportResource();
		resource.setJasperDesign(jasperDesign);
		cachedResourcesMap.put(uri, resource);
	}

	/**
	 * 
	 */
	public void resetJasperReport(String uri)
	{
		JasperDesignReportResource resource = cachedResourcesMap.get(uri);
		if (resource != null)
		{
			resource.setReport(null);
		}
		//cachedResourcesMap.put(uri, resource);
	}

	/**
	 * 
	 *
	public void set(UUID subreportElementUUID, String uri)
	{
		cachedSubreportsMap.put(subreportElementUUID, uri);
	}

	/**
	 * 
	 */
	private JasperDesignReportResource getResource(String uri)
	{
		JasperDesignReportResource resource = cachedResourcesMap.get(uri);
		
		if (resource != null)
		{
			JasperDesign jasperDesign = resource.getJasperDesign();
			JasperReport jasperReport = resource.getReport();
			
			if (jasperDesign == null)
			{
				if (jasperReport == null)
				{
					throw new JRRuntimeException("Invalid JasperDesignCache entry.");
				}
				else
				{
					ByteArrayInputStream bais = null;
					ByteArrayOutputStream baos = new ByteArrayOutputStream();
					try
					{
						JRXmlWriter.writeReport(jasperReport, baos, "UTF-8");
						bais = new ByteArrayInputStream(baos.toByteArray());
						jasperDesign = JRXmlLoader.load(bais);
						resource.setJasperDesign(jasperDesign);
					}
					catch (JRException e)
					{
						throw new JRRuntimeException(e);
					}
					finally
					{
						try
						{
							baos.close();
							if (bais != null)
							{
								bais.close();
							}
						}
						catch (IOException e)
						{
						}
					}
				}
			}
			else
			{
				if (jasperReport == null)
				{
					try
					{
						jasperReport = JasperCompileManager.compileReport(jasperDesign);
						resource.setReport(jasperReport);
					}
					catch (JRException e)
					{
						throw new JRRuntimeException(e);
					}
				}
				else
				{
					//nothing to do?
				}
			}
		}
		
		return resource;
	}


	/**
	 * 
	 */
	public Map<String, JasperDesignReportResource> getCachedResources()
	{
		return cachedResourcesMap;
	}
}
