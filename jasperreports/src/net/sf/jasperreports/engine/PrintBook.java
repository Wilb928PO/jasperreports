/*
 * JasperReports - Free Java Reporting Library.
 * Copyright (C) 2001 - 2013 Jaspersoft Corporation. All rights reserved.
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


/*
 * Contributors:
 * John Bindel - jbindel@users.sourceforge.net 
 */

package net.sf.jasperreports.engine;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;


/**
 * @author Teodor Danciu (teodord@users.sourceforge.net)
 * @version $Id: JasperPrint.java 6841 2014-01-28 14:51:00Z teodord $
 */
public class PrintBook implements Serializable, JRPropertiesHolder
{

	/**
	 *
	 */
	private static final long serialVersionUID = JRConstants.SERIAL_VERSION_UID;

	/**
	 *
	 */
	private String name;

	//FIXME unsynchronize on serialization?
	private List<JasperPrint> jasperPrintList = Collections.synchronizedList(new ArrayList<JasperPrint>());

	private JRPropertiesMap propertiesMap;
	

	/**
	 * Creates a new empty book. 
	 */
	public PrintBook()
	{
		propertiesMap = new JRPropertiesMap();
	}

	/**
	 * @return Returns the name of the book
	 */
	public String getName()
	{
		return name;
	}
		
	/**
	 * Sets the name of the book.
	 * 
	 * @param name name of the book
	 */
	public void setName(String name)
	{
		this.name = name;
	}

	public boolean hasProperties()
	{
		return propertiesMap != null && propertiesMap.hasProperties();
	}
	
	/**
	 * 
	 */
	public JRPropertiesMap getPropertiesMap()
	{
		return propertiesMap;
	}

	public JRPropertiesHolder getParentProperties()
	{
		return null;
	}
	
	/**
	 *
	 */
	public String[] getPropertyNames()
	{
		return propertiesMap.getPropertyNames();
	}

	/**
	 *
	 */
	public String getProperty(String propName)
	{
		return propertiesMap.getProperty(propName);
	}

	/**
	 *
	 */
	public void setProperty(String propName, String value)
	{
		propertiesMap.setProperty(propName, value);
	}

	/**
	 *
	 */
	public void removeProperty(String propName)
	{
		propertiesMap.removeProperty(propName);
	}

	/**
	 * Returns a list of all JasperPrint objects the filled book.
	 */
	public List<JasperPrint> getJasperPrintList()
	{
		return jasperPrintList;
	}
		
	/**
	 * Adds a new JasperPrint to the book.
	 */
	public synchronized void addJasperPrint(JasperPrint page)
	{
		jasperPrintList.add(page);
	}

	/**
	 * Adds a new JasperPrint to the book, placing it at the specified index.
	 */
	public synchronized void addJasperPrint(int index, JasperPrint page)
	{
		jasperPrintList.add(index, page);
	}

	/**
	 * Removes a JasperPrint from the book.
	 */
	public synchronized JasperPrint removeJasperPrint(int index)
	{
		return jasperPrintList.remove(index);
	}

}
