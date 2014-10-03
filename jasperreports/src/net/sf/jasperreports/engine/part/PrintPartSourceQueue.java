/*
 * JasperReports - Free Java Reporting Library.
 * Copyright (C) 2001 - 2014 TIBCO Software Inc. All rights reserved.
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
package net.sf.jasperreports.engine.part;

/**
 * @author Lucian Chirita (lucianc@users.sourceforge.net)
 * @version $Id$
 */
public class PrintPartSourceQueue
{

	private PrintPartSource head;
	private PrintPartSource tail;

	public PrintPartSourceQueue()
	{
	}
	
	public boolean isEmpty()
	{
		return head == null;
	}

	public void append(PrintPartSource part)
	{
		if (head == null)
		{
			head = tail = part;
		}
		else
		{
			tail.setNextPart(part);
			tail = part;
		}
	}
	
	public boolean isHead(PrintPartSource part)
	{
		return part == head;// == is fine
	}

	public PrintPartSource head()
	{
		return head;
	}

	public void removeHead()
	{
		head = head.getNextPart();
		if (head == null)
		{
			tail = null;
		}
	}
	
}
