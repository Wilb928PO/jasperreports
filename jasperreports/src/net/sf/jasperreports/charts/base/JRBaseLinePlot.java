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
package net.sf.jasperreports.charts.base;

import net.sf.jasperreports.charts.JRLinePlot;
import net.sf.jasperreports.engine.JRChartPlot;
import net.sf.jasperreports.engine.JRExpression;
import net.sf.jasperreports.engine.JRExpressionCollector;
import net.sf.jasperreports.engine.base.JRBaseChartPlot;
import net.sf.jasperreports.engine.base.JRBaseObjectFactory;

/**
 * @author Flavius Sana (flavius_sana@users.sourceforge.net)
 * @version $Id$ 
 */
public class JRBaseLinePlot extends JRBaseChartPlot implements JRLinePlot {
	
	private static final long serialVersionUID = 10000;
	
	protected JRExpression categoryAxisLabelExpression = null;
	protected JRExpression valueAxisLabelExpression = null;
	
	boolean isShowShapes = true;
	boolean isShowLines = true;
	
	
	public JRBaseLinePlot( JRChartPlot linePlot){
		super( linePlot);
	}
	
	public JRBaseLinePlot( JRLinePlot linePlot, JRBaseObjectFactory factory ){
		super( linePlot, factory );
		
		isShowShapes = linePlot.isShowShapes();
		isShowLines = linePlot.isShowLines();
		
		categoryAxisLabelExpression = factory.getExpression( linePlot.getCategoryAxisLabelExpression() );
		valueAxisLabelExpression = factory.getExpression( linePlot.getValueAxisLabelExpression() );
	
	}
	
	public JRExpression getCategoryAxisLabelExpression(){
		return categoryAxisLabelExpression;
	}
	
	public JRExpression getValueAxisLabelExpression() {
		return valueAxisLabelExpression;
	}
	
	public boolean isShowShapes(){
		return isShowShapes;
	}
	
	public boolean isShowLines(){
		return isShowLines;
	}
	
	public void setShowShapes( boolean value ){
		this.isShowShapes = value;
	}
	
	public void setShowLines( boolean value ){
		this.isShowLines = value;
	}

	/**
	 *
	 */
	public void collectExpressions(JRExpressionCollector collector)
	{
		collector.collect(this);
	}

}
