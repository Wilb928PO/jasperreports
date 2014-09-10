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
package net.sf.jasperreports.engine.fill;

/**
 * @author Lucian Chirita (lucianc@users.sourceforge.net)
 * @version $Id$
 */
public class FillerSubreportParent implements FillerParent
{

	private final JRFillSubreport parentElement;
	private final JRBaseFiller parentFiller;
	private final DatasetExpressionEvaluator evaluator;
	
	public FillerSubreportParent(JRFillSubreport parentElement, DatasetExpressionEvaluator evaluator)
	{
		this.parentElement = parentElement;
		this.parentFiller = parentElement.filler;
		this.evaluator = evaluator;
	}

	@Override
	public BaseReportFiller getFiller()
	{
		return parentFiller;
	}

	@Override
	public void registerSubfiller(JRBaseFiller filler)
	{
		parentFiller.registerSubfiller(filler);
	}

	@Override
	public void unregisterSubfiller(JRBaseFiller filler)
	{
		parentFiller.unregisterSubfiller(filler);
	}

	@Override
	public boolean isRunToBottom()
	{
		return parentElement.isRunToBottom() != null && parentElement.isRunToBottom();
	}

	@Override
	public boolean isPageBreakInhibited()
	{
		return parentElement.getBand().isPageBreakInhibited();
	}

	@Override
	public boolean isBandOverFlowAllowed()
	{
		return parentFiller.isBandOverFlowAllowed();
	}

	@Override
	public DatasetExpressionEvaluator getCachedEvaluator()
	{
		return evaluator;
	}

}
