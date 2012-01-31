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
package net.sf.jasperreports.web.util;

import java.io.IOException;
import java.util.List;

import net.sf.jasperreports.engine.JRRuntimeException;
import net.sf.jasperreports.extensions.ExtensionsEnvironment;
import net.sf.jasperreports.web.actions.AbstractAction;

import org.codehaus.jackson.JsonGenerationException;
import org.codehaus.jackson.JsonParseException;
import org.codehaus.jackson.JsonParser;
import org.codehaus.jackson.map.JsonMappingException;
import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.map.jsontype.NamedType;



/**
 * @author Teodor Danciu (teodord@users.sourceforge.net)
 * @version $Id: FileRepositoryService.java 4819 2011-11-28 15:24:25Z lucianc $
 */
public class JacksonUtil
{
	/**
	 * 
	 */
	private static volatile ObjectMapper objectMapper;
	
	private static ObjectMapper getObjectMapper()
	{
		if (objectMapper != null)
		{
			return objectMapper;
		}
		
		synchronized (JacksonUtil.class)
		{
			// double check
			if (objectMapper != null)
			{
				return objectMapper;
			}
			
			ObjectMapper mapper = new ObjectMapper();

			List<JacksonMapping> jacksonMappings = ExtensionsEnvironment.getExtensionsRegistry().getExtensions(JacksonMapping.class);
			for (JacksonMapping jacksonMapping : jacksonMappings)
			{
				register(mapper, jacksonMapping);
			}

			mapper.configure(JsonParser.Feature.ALLOW_SINGLE_QUOTES, true);
			
			objectMapper = mapper;
		}
		
		return objectMapper;
	}
	
	
	/**
	 *
	 */
	private static void register(ObjectMapper mapper, JacksonMapping mapping)
	{
		try
		{
			Class<?> clazz = Class.forName(mapping.getClassName());
			mapper.registerSubtypes(new NamedType(clazz, mapping.getName()));
		}
		catch (ClassNotFoundException e)
		{
			throw new JRRuntimeException(e);
		}
	}


	/**
	 *
	 */
	public static Object load(String jsonData, Class<?> clazz)
	{
		Object result = null;
		if (jsonData != null) 
		{
			ObjectMapper mapper = getObjectMapper();
			
			try 
			{
				result = mapper.readValue(jsonData, AbstractAction.class);
			}
			catch (JsonParseException e) 
			{
				throw new JRRuntimeException(e);
			}
			catch (JsonMappingException e) 
			{
				throw new JRRuntimeException(e);
			}
			catch (IOException e) 
			{
				throw new JRRuntimeException(e);
			}
		}
		return result;
	}


	/**
	 * 
	 */
	public static String getJsonString(Object object) 
	{
		ObjectMapper mapper = getObjectMapper();
		try 
		{
			return mapper.writeValueAsString(object);
		} 
		catch (JsonGenerationException e) 
		{
			throw new JRRuntimeException(e);
		} 
		catch (JsonMappingException e) 
		{
			throw new JRRuntimeException(e);
		} 
		catch (IOException e) 
		{
			throw new JRRuntimeException(e);
		}
	}
	
	
	/**
	 * 
	 */
	public static String getEscapedJsonString(Object object){
		return getJsonString(object).replaceAll("\\\"", "\\\\\\\"");
	}
}
