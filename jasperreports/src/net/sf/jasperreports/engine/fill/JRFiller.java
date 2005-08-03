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
package net.sf.jasperreports.engine.fill;

import java.sql.Connection;
import java.util.Map;

import net.sf.jasperreports.engine.JRDataSource;
import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.JRReport;
import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.engine.JasperReport;


/**
 * @author Teodor Danciu (teodord@users.sourceforge.net)
 * @version $Id$
 */
public abstract class JRFiller
{


	/**
	 *
	 */
	public static JasperPrint fillReport(
		JasperReport jasperReport,
		Map parameters,
		Connection conn
		) throws JRException
	{
		JRBaseFiller filler = createFiller(jasperReport);
		
		JasperPrint jasperPrint = null;
		
		try
		{
			jasperPrint = filler.fill(parameters, conn);
		}
		catch(JRFillInterruptedException e)
		{
			throw new JRException("The report filling thread was interrupted.");
		}
		
		return jasperPrint;
	}


	/**
	 *
	 */
	public static JasperPrint fillReport(
		JasperReport jasperReport,
		Map parameters,
		JRDataSource dataSource
		) throws JRException
	{
		JRBaseFiller filler = createFiller(jasperReport);
		
		JasperPrint jasperPrint = null;
		
		try
		{
			jasperPrint = filler.fill(parameters, dataSource);
		}
		catch(JRFillInterruptedException e)
		{
			throw new JRException("The report filling thread was interrupted.");
		}
		
		return jasperPrint;
	}


	public static JRBaseFiller createFiller(JasperReport jasperReport) throws JRException
	{
		JRBaseFiller filler = null;

		switch (jasperReport.getPrintOrder())
		{
			case JRReport.PRINT_ORDER_HORIZONTAL :
			{
				filler = new JRHorizontalFiller(jasperReport);
				break;
			}
			case JRReport.PRINT_ORDER_VERTICAL :
			{
				filler = new JRVerticalFiller(jasperReport);
				break;
			}
		}
		return filler;
	}
}
