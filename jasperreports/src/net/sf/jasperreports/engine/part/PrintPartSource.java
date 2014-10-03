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
	
	private PrintPartSource nextPart;

	private FillPartPrintOutput localOutput;
	
	public PrintPartSource(FillPart part)
	{
		this.part = part;
	}

	public PrintPartSource getNextPart()
	{
		return nextPart;
	}

	public void setNextPart(PrintPartSource nextPart)
	{
		this.nextPart = nextPart;
	}

	public void fill(byte evaluation) throws JRException
	{
		localOutput = new FillPartPrintOutput(part.getFiller());
		fill(evaluation, localOutput);
	}

	public void fill(byte evaluation, PartPrintOutput printOutput) throws JRException
	{
		Output output = new Output(printOutput);
		part.fill(evaluation, output);
	}
	
	public boolean appendLocalOutput(PartPrintOutput printOutput)
	{
		if (localOutput == null)
		{
			return false;
		}
		
		localOutput.appendTo(printOutput);
		return true;
	}

	public boolean isPageFinal(int pageIndex)
	{
		return part.isPageFinal(pageIndex);
	}
	
	protected class Output implements PartOutput
	{
		private PartPrintOutput printOutput;

		public Output(PartPrintOutput printOutput)
		{
			this.printOutput = printOutput;
		}

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
