/*
 * ============================================================================
 * GNU Lesser General Public License
 * ============================================================================
 *
 * JasperReports - Free Java report-generating library.
 * Copyright (C) 2001-2006 JasperSoft Corporation http://www.jaspersoft.com
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
 * 303 Second Street, Suite 450 North
 * San Francisco, CA 94107
 * http://www.jaspersoft.com
 */
package net.sf.jasperreports.engine.component;

import java.net.URL;

import net.sf.jasperreports.engine.JRPropertiesMap;
import net.sf.jasperreports.engine.JRRuntimeException;
import net.sf.jasperreports.engine.util.JRLoader;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.xml.XmlBeanFactory;
import org.springframework.core.io.UrlResource;

/**
 * TODO component
 * 
 * @author Lucian Chirita (lucianc@users.sourceforge.net)
 * @version $Id: JRCrosstab.java 1741 2007-06-08 10:53:33Z lucianc $
 */
public class SpringComponentsMetaFactory implements ComponentsMetaFactory
{

	private final static Log log = LogFactory.getLog(SpringComponentsMetaFactory.class);
	
	public static final String PROPERTY_SUFFIX_SPRING_BEANS_RESOURCE = ".spring.beans.resource";

	public static final String PROPERTY_SUFFIX_SPRING_META_BEAN = ".spring.meta.bean";

	public static final String DEFAULT_COMPONENT_MANAGER_BEAN = "componentsMeta";
	
	public ComponentsMeta createComponentsMeta(String componentId,
			JRPropertiesMap properties)
	{
		BeanFactory beanFactory = getBeanFactory(componentId, properties);
		String beanName = getComponentManagerBeanName(componentId, properties);
		if (log.isDebugEnabled())
		{
			log.debug("Retrieving component manager for " + componentId 
					+ " using bean " + beanName);
		}
		ComponentsMeta component = (ComponentsMeta) beanFactory.getBean(
				beanName, ComponentsMeta.class);
		return component;
	}

	protected BeanFactory getBeanFactory(String componentId,
			JRPropertiesMap properties)
	{
		String resourceProp = ComponentEnvironment.PROPERTY_COMPONENT_PREFIX
				+ componentId + PROPERTY_SUFFIX_SPRING_BEANS_RESOURCE;
		String resource = properties.getProperty(resourceProp);
		if (resource == null)
		{
			throw new JRRuntimeException("No Spring resource property set");
		}
		
		URL resourceLocation = JRLoader.getResource(resource);
		if (resourceLocation == null)
		{
			throw new JRRuntimeException("Could not find Spring resource " + resource 
					+ " for component " + componentId);
		}
		
		if (log.isDebugEnabled())
		{
			log.debug("Creating Spring beans factory for component " + componentId 
					+ " using "+ resourceLocation);
		}
		
		XmlBeanFactory beanFactory = new XmlBeanFactory(new UrlResource(resourceLocation));
		return beanFactory;
	}
	
	protected String getComponentManagerBeanName(String componentId,
			JRPropertiesMap properties)
	{
		String nameProp = ComponentEnvironment.PROPERTY_COMPONENT_PREFIX
				+ componentId + PROPERTY_SUFFIX_SPRING_META_BEAN;
		String name = properties.getProperty(nameProp);
		if (name == null)
		{
			name = DEFAULT_COMPONENT_MANAGER_BEAN;
		}
		return name;
	}
}
