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
package net.sf.jasperreports.parts.subreport;

import java.util.Map;

import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.JasperFillManager;
import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.engine.JasperReport;
import net.sf.jasperreports.engine.fill.JRFillExpressionEvaluator;
import net.sf.jasperreports.engine.fill.JRFillObjectFactory;
import net.sf.jasperreports.engine.fill.JRFillSubreport;
import net.sf.jasperreports.engine.part.BasePartFillComponent;

/**
 * @author Lucian Chirita (lucianc@users.sourceforge.net)
 * @version $Id$
 */
public class SubreportFillPart extends BasePartFillComponent
{

	private SubreportPartComponent subreportPart;
	private JRFillExpressionEvaluator expressionEvaluator;

	private JasperReport jasperReport;
	private Map<String, Object> parameterValues;
	
	public SubreportFillPart(SubreportPartComponent subreportPart, JRFillObjectFactory factory)
	{
		this.subreportPart = subreportPart;
		expressionEvaluator = factory.getExpressionEvaluator();
	}

	@Override
	public void evaluate(byte evaluation) throws JRException
	{
		jasperReport = evaluateReport(evaluation);
		parameterValues = JRFillSubreport.getParameterValues(fillContext.getFiller(), expressionEvaluator, 
				subreportPart.getParametersMapExpression(), subreportPart.getParameters(), 
				evaluation, false, 
				jasperReport.getResourceBundle() != null, jasperReport.getFormatFactoryClass() != null);
	}

	private JasperReport evaluateReport(byte evaluation) throws JRException
	{
		Object reportSource = fillContext.evaluate(subreportPart.getExpression(), evaluation);
		return JRFillSubreport.loadReport(reportSource, fillContext.getFiller());
	}

	@Override
	public void fill() throws JRException
	{
		//FIXMEBOOK
		JasperPrint jasperPrint = JasperFillManager.fillReport(jasperReport, parameterValues);
		fillContext.addPartReport(jasperPrint);
	}

}
