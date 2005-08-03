/*
 * ============================================================================
 * GNU Lesser General Public License
 * ============================================================================
 *
 * JasperReports - Free Java report-generating library.
 * Copyright (C) 2001-2005 JasperSoft Corporation http://www.jaspersoft.com
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
 * 185, Berry Street, Suite 6200
 * San Francisco CA 94107
 * http://www.jaspersoft.com
 */
package net.sf.jasperreports.engine.util;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.net.MalformedURLException;
import java.net.URL;

import net.sf.jasperreports.engine.JRException;


/**
 * @author Teodor Danciu (teodord@users.sourceforge.net)
 * @version $Id$
 */
public class JRLoader
{


	/**
	 *
	 */
	//private static boolean wasWarning = false;


	/**
	 *
	 */
	public static Object loadObject(String fileName) throws JRException
	{
		return loadObject(new File(fileName));
	}


	/**
	 *
	 */
	public static Object loadObject(File file) throws JRException
	{
		if (!file.exists() || !file.isFile())
		{
			throw new JRException( new FileNotFoundException(String.valueOf(file)) );
		}

		Object obj = null;

		FileInputStream fis = null;
		ObjectInputStream ois = null;

		try
		{
			fis = new FileInputStream(file);
			ois = new ObjectInputStream(fis);
			obj = ois.readObject();
		}
		catch (IOException e)
		{
			throw new JRException("Error loading object from file : " + file, e);
		}
		catch (ClassNotFoundException e)
		{
			throw new JRException("Class not found when loading object from file : " + file, e);
		}
		finally
		{
			if (ois != null)
			{
				try
				{
					ois.close();
				}
				catch(IOException e)
				{
				}
			}

			if (fis != null)
			{
				try
				{
					fis.close();
				}
				catch(IOException e)
				{
				}
			}
		}

		return obj;
	}


	/**
	 *
	 */
	public static Object loadObject(URL url) throws JRException
	{
		Object obj = null;

		InputStream is = null;
		ObjectInputStream ois = null;

		try
		{
			is = url.openStream();
			ois = new ObjectInputStream(is);
			obj = ois.readObject();
		}
		catch (IOException e)
		{
			throw new JRException("Error loading object from URL : " + url, e);
		}
		catch (ClassNotFoundException e)
		{
			throw new JRException("Class not found when loading object from URL : " + url, e);
		}
		finally
		{
			if (ois != null)
			{
				try
				{
					ois.close();
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

		return obj;
	}


	/**
	 *
	 */
	public static Object loadObject(InputStream is) throws JRException
	{
		Object obj = null;

		ObjectInputStream ois = null;

		try
		{
			ois = new ObjectInputStream(is);
			obj = ois.readObject();
		}
		catch (IOException e)
		{
			throw new JRException("Error loading object from InputStream", e);
		}
		catch (ClassNotFoundException e)
		{
			throw new JRException("Class not found when loading object from InputStream", e);
		}
		finally
		{
			//FIXME should not close the stream
			if (ois != null)
			{
				try
				{
					ois.close();
				}
				catch(IOException e)
				{
				}
			}
		}

		return obj;
	}


	/**
	 *
	 */
	public static Object loadObjectFromLocation(String location) throws JRException
	{
		try
		{
			URL url = new URL(location);
			return loadObject(url);
		}
		catch(MalformedURLException e)
		{
		}

		File file = new File(location);
		if (file.exists() && file.isFile())
		{
			return loadObject(file);
		}

		ClassLoader classLoader = Thread.currentThread().getContextClassLoader();

		URL url = null;
		if (classLoader != null)
		{
			url = classLoader.getResource(location);
		}
		if (url == null)
		{
			//if (!wasWarning)
			//{
			//	if (log.isWarnEnabled())
			//		log.warn("Failure using Thread.currentThread().getContextClassLoader() in JRLoader class. Using JRLoader.class.getClassLoader() instead.");
			//	wasWarning = true;
			//}
			classLoader = JRLoader.class.getClassLoader();
			if (classLoader == null)
			{
				url = JRLoader.class.getResource("/" + location);
			}
			else
			{				
				url = classLoader.getResource(location);
			}
		}

		if (url != null)
		{
			return loadObject(url);
		}

		throw new JRException("Could not load object from location : " + location);
	}


	/**
	 *
	 */
	public static byte[] loadBytes(File file) throws JRException
	{
		ByteArrayOutputStream baos = null;
		FileInputStream fis = null;

		try
		{
			fis = new FileInputStream(file);
			baos = new ByteArrayOutputStream();

			byte[] bytes = new byte[10000];
			int ln = 0;
			while ((ln = fis.read(bytes)) > 0)
			{
				baos.write(bytes, 0, ln);
			}

			baos.flush();
		}
		catch (IOException e)
		{
			throw new JRException("Error loading byte data : " + file, e);
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

			if (fis != null)
			{
				try
				{
					fis.close();
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
	public static byte[] loadBytes(URL url) throws JRException
	{
		ByteArrayOutputStream baos = null;
		InputStream is = null;

		try
		{
			is = url.openStream();
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
			throw new JRException("Error loading byte data : " + url, e);
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
	public static byte[] loadBytes(InputStream is) throws JRException
	{
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
			throw new JRException("Error loading byte data from input stream.", e);
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
		}

		return baos.toByteArray();
	}


	/**
	 *
	 */
	public static byte[] loadBytesFromLocation(String location) throws JRException
	{
		try
		{
			URL url = new URL(location);
			return loadBytes(url);
		}
		catch(MalformedURLException e)
		{
		}

		File file = new File(location);
		if (file.exists() && file.isFile())
		{
			return loadBytes(file);
		}

		URL url = null;
		
		ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
		if (classLoader != null)
		{
			url = classLoader.getResource(location);
		}
		
		if (url == null)
		{
			//if (!wasWarning)
			//{
			//	if (log.isWarnEnabled())
			//		log.warn("Failure using Thread.currentThread().getContextClassLoader() in JRLoader class. Using JRLoader.class.getClassLoader() instead.");
			//	wasWarning = true;
			//}
			classLoader = JRLoader.class.getClassLoader();
			if (classLoader == null)
			{
				url = JRLoader.class.getResource("/" + location);
			}
			else
			{
				url = classLoader.getResource(location);
			}
		}

		if (url != null)
		{
			return loadBytes(url);
		}

		throw new JRException("Byte data not found at location : " + location);
	}


}
