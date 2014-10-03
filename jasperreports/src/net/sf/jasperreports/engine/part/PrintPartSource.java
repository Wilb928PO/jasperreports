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

import net.sf.jasperreports.engine.JRException;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * @author Lucian Chirita (lucianc@users.sourceforge.net)
 * @version $Id$
 */
public class PrintPartSource
{
	private static final Log log = LogFactory.getLog(PrintPartSource.class);

	private final FillPart part;
	
	private final FillPartPrintOutput printOutput;
	
	public PrintPartSource(FillPart part)
	{
		this.part = part;
		this.printOutput = new FillPartPrintOutput(part.getFiller());
	}

	public void fill(byte evaluation) throws JRException
	{
		Output output = new Output();
		part.fill(evaluation, output);
	}

	public FillPartPrintOutput getPrintOutput()
	{
		return printOutput;
	}

	public boolean isPageFinal(int pageIndex)
	{
		return part.isPageFinal(pageIndex);
	}
	
	protected class Output implements PartOutput
	{
		@Override
		public PartPrintOutput getPrintOutput()
		{
			return printOutput;
		}

		@Override
		public void partPageUpdated(int partPageIndex)
		{
			//FIXMEBOOK part.getFiller().partPageUpdated(startPageIndex + partPageIndex);
		}
	}
	
}
