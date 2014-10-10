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
package net.sf.jasperreports.parts.subreport;

import net.sf.jasperreports.engine.JRExpressionCollector;
import net.sf.jasperreports.engine.JRSubreportParameter;
import net.sf.jasperreports.engine.base.JRBaseObjectFactory;
import net.sf.jasperreports.engine.design.JRVerifier;
import net.sf.jasperreports.engine.part.PartComponent;
import net.sf.jasperreports.engine.part.PartComponentCompiler;

/**
 * Compile-time handler of {@link SubreportPartComponent list component} instances.
 * 
 * @author Lucian Chirita (lucianc@users.sourceforge.net)
 * @version $Id: ListComponentCompiler.java 5877 2013-01-07 19:51:14Z teodord $
 */
public class SubreportPartComponentCompiler implements PartComponentCompiler
{

	public void collectExpressions(PartComponent component, JRExpressionCollector collector)
	{
		SubreportPartComponent subreport = (SubreportPartComponent) component;
		
		collector.addExpression(subreport.getParametersMapExpression());
		
		JRSubreportParameter[] parameters = subreport.getParameters();
		if (parameters != null && parameters.length > 0)
		{
			for(int j = 0; j < parameters.length; j++)
			{
				collector.addExpression(parameters[j].getExpression());
			}
		}

		collector.addExpression(subreport.getExpression());
	}

	public PartComponent toCompiledComponent(PartComponent component,
			JRBaseObjectFactory baseFactory)
	{
		SubreportPartComponent subreportComponent = (SubreportPartComponent) component;
		StandardSubreportPartComponent compiledComponent = new StandardSubreportPartComponent(
				subreportComponent, baseFactory);
		return compiledComponent;
	}

	public void verify(PartComponent component, JRVerifier verifier)
	{
//FIXMEBOOK
	}

}
