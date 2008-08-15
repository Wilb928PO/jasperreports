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

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import net.sf.jasperreports.engine.util.ClassUtils;
import net.sf.jasperreports.engine.util.JRProperties;

/**
 * TODO component
 * 
 * @author Lucian Chirita (lucianc@users.sourceforge.net)
 * @version $Id$
 */
public final class ComponentsEnvironment
{

	private ComponentsEnvironment()
	{
	}
	
	private static final Log log = LogFactory.getLog(ComponentsEnvironment.class); 
	
	public static final String PROPERTY_COMPONENTS_REGISTRY_CLASS = 
		JRProperties.PROPERTY_PREFIX + "components.registry.class";
	
	private static ComponentsRegistry systemRegistry;
	private static final ThreadLocal threadRegistry = new InheritableThreadLocal();
	
	public static synchronized ComponentsRegistry getSystemComponentsRegistry()
	{
		if (systemRegistry == null)
		{
			systemRegistry = createDefaultRegistry();
		}
		return systemRegistry;
	}
	
	private static ComponentsRegistry createDefaultRegistry()
	{
		String registryClass = JRProperties.getProperty(PROPERTY_COMPONENTS_REGISTRY_CLASS);
		
		if (log.isDebugEnabled())
		{
			log.debug("Instantiating components registry class " + registryClass);
		}
		
		ComponentsRegistry registry = (ComponentsRegistry) ClassUtils.
			instantiateClass(registryClass, ComponentsRegistry.class);
		return registry;
	}

	public static synchronized void setSystemComponentsRegistry(ComponentsRegistry componentsRegistry)
	{
		ComponentsEnvironment.systemRegistry = componentsRegistry;
	}

	public static ComponentsRegistry getThreadComponentsRegistry()
	{
		return (ComponentsRegistry) threadRegistry.get();
	}

	public static void getThreadComponentsRegistry(ComponentsRegistry componentsRegistry)
	{
		threadRegistry.set(componentsRegistry);
	}
	
	public static ComponentsRegistry getComponentsRegistry()
	{
		ComponentsRegistry registry = getThreadComponentsRegistry();
		if (registry == null)
		{
			registry = getSystemComponentsRegistry();
		}
		return registry;
	}
	
}
