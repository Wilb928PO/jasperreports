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
import java.util.List;


/**
 * @author Teodor Danciu (teodord@users.sourceforge.net)
 * @version $Id: JasperPrint.java 6841 2014-01-28 14:51:00Z teodord $
 */
public interface PrintBook extends Serializable, JRPropertiesHolder
{
	/**
	 * @return Returns the name of the book
	 */
	public String getName();
		
	public boolean hasProperties();
	
	/**
	 * 
	 */
	public JRPropertiesMap getPropertiesMap();

	public JRPropertiesHolder getParentProperties();
	
	/**
	 *
	 */
	public String[] getPropertyNames();

	/**
	 * Returns a list of all JasperPrint objects the filled book.
	 */
	public List<JasperPrint> getJasperPrintList();
}
