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
package net.sf.jasperreports.engine;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import net.sf.jasperreports.extensions.ExtensionsEnvironment;


/**
 * @author Teodor Danciu (teodord@users.sourceforge.net)
 * @version $Id: JRCloneable.java 4595 2011-09-08 15:55:10Z teodord $
 */
public class DefaultJasperReportsContext implements JasperReportsContext
{
	/**
	 *
	 */
	private static final DefaultJasperReportsContext INSTANCE = new DefaultJasperReportsContext();
	
	private Map<String, Object> values = new HashMap<String, Object>();

	/**
	 *
	 */
	private DefaultJasperReportsContext()
	{
	}

	/**
	 *
	 */
	public static DefaultJasperReportsContext getInstance()
	{
		return INSTANCE;
	}

	/**
	 *
	 */
	public Object getValue(String key)
	{
		return values.get(key);
	}

	/**
	 *
	 */
	public void setValue(String key, Object value)
	{
		values.put(key, value);
	}
	
	/**
	 * Returns a list of extension objects for a specific extension type.
	 * 
	 * @param extensionType the extension type
	 * @param <T> generic extension type
	 * @return a list of extension objects
	 */
	public <T> List<T> getExtensions(Class<T> extensionType)
	{
		return ExtensionsEnvironment.getExtensionsRegistry().getExtensions(extensionType);
	}
	
}
