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

import java.util.List;
import java.util.Locale;
import java.util.ResourceBundle;
import java.util.TimeZone;

import net.sf.jasperreports.engine.JRComponentElement;
import net.sf.jasperreports.engine.JRDefaultStyleProvider;
import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.JRExpression;
import net.sf.jasperreports.engine.JROrigin;
import net.sf.jasperreports.engine.JRPart;
import net.sf.jasperreports.engine.JRPrintElement;
import net.sf.jasperreports.engine.JRPropertiesHolder;
import net.sf.jasperreports.engine.JRPropertiesMap;
import net.sf.jasperreports.engine.JRStyle;
import net.sf.jasperreports.engine.component.Component;
import net.sf.jasperreports.engine.component.ComponentKey;
import net.sf.jasperreports.engine.component.ComponentManager;
import net.sf.jasperreports.engine.component.ComponentsEnvironment;
import net.sf.jasperreports.engine.component.FillComponent;
import net.sf.jasperreports.engine.component.FillContext;
import net.sf.jasperreports.engine.type.EvaluationTimeEnum;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;


/**
 * @author Teodor Danciu (teodord@users.sourceforge.net)
 * @version $Id: JRFillBand.java 6166 2013-05-16 17:17:16Z lucianc $
 */
public class JRFillPart implements JRPart, FillContext
{

	private static final Log log = LogFactory.getLog(JRFillPart.class);

	/**
	 *
	 */
	private JRPart parent;
	protected JRBaseFiller filler;

	private boolean isPrintWhenTrue = true;

	private FillComponent fillComponent;
	private List<JRFillDatasetRun> componentDatasetRuns;
	
	/**
	 *
	 */
	protected JRFillPart(
		JRBaseFiller filler,
		JRPart part,
		JRFillObjectFactory factory
		)
	{
		factory.put(part, this);

		this.parent = part;
		this.filler = filler;
		
		ComponentKey componentKey = part.getComponentKey();
		ComponentManager manager = ComponentsEnvironment.getInstance(filler.getJasperReportsContext()).getManager(componentKey);
		
		factory.trackDatasetRuns();
		fillComponent = manager.getComponentFillFactory(filler.getJasperReportsContext()).toFillComponent(part.getComponent(), factory);
		fillComponent.initialize(this);
		this.componentDatasetRuns = factory.getTrackedDatasetRuns();
	}


	/**
	 *
	 */
	public JRExpression getPrintWhenExpression()
	{
		return (parent == null ? null : parent.getPrintWhenExpression());
	}

	/**
	 *
	 */
	protected boolean isPrintWhenExpressionNull()
	{
		return (getPrintWhenExpression() == null);
	}

	/**
	 *
	 */
	protected boolean isPrintWhenTrue()
	{
		return isPrintWhenTrue;
	}

	/**
	 *
	 */
	protected void setPrintWhenTrue(boolean isPrintWhenTrue)
	{
		this.isPrintWhenTrue = isPrintWhenTrue;
	}

	/**
	 *
	 */
	protected boolean isToPrint()
	{
		return
			(isPrintWhenExpressionNull() ||
			 (!isPrintWhenExpressionNull() &&
			  isPrintWhenTrue()));
	}


	/**
	 *
	 */
	protected void evaluatePrintWhenExpression(
		byte evaluation
		) throws JRException
	{
		boolean isPrintTrue = false;

		JRExpression expression = getPrintWhenExpression();
		if (expression != null)
		{
			Boolean printWhenExpressionValue = (Boolean)filler.evaluateExpression(expression, evaluation);
			if (printWhenExpressionValue == null)
			{
				isPrintTrue = false;
			}
			else
			{
				isPrintTrue = printWhenExpressionValue.booleanValue();
			}
		}

		setPrintWhenTrue(isPrintTrue);
	}



	/**
	 *
	 */
	protected JRPrintBand fill() throws JRException
	{
		return null;//fill(getHeight(), false);
	}


	/**
	 *
	 */
	protected JRPrintBand fill(
		int availableHeight
		) throws JRException
	{
		return fill(availableHeight, true);
	}


	/**
	 *
	 */
	protected JRPrintBand fill(
		int availableHeight,
		boolean isOverflowAllowed
		) throws JRException
	{
		filler.checkInterrupted();
		filler.fillContext.ensureMasterPageAvailable();

		filler.setBandOverFlowAllowed(isOverflowAllowed);

//		initFill();
//
//		if (isNewPageColumn && !isOverflow)
//		{
//			isFirstWholeOnPageColumn = true;
//		}
//		
//		resetElements();
//
//		prepareElements(availableHeight, isOverflowAllowed);
//
//		stretchElements();
//
//		moveBandBottomElements();
//
//		removeBlankElements();
//
//		isFirstWholeOnPageColumn = isNewPageColumn && isOverflow;
//		isNewPageColumn = false;
//		isNewGroupMap = new HashMap<JRGroup,Boolean>();
//
//		JRPrintBand printBand = new JRPrintBand();
//		fillElements(printBand);
//
//		return printBand;
		return null;
	}


	protected void evaluate(byte evaluation) throws JRException
	{
//		resetSavedVariables();
//		evaluateConditionalStyles(evaluation);
//		super.evaluate(evaluation);
	}
	
	public boolean hasProperties()
	{
		return parent.hasProperties();
	}

	// not doing anything with the properties at fill time
	public JRPropertiesMap getPropertiesMap()
	{
		return parent.getPropertiesMap();
	}
	
	public JRPropertiesHolder getParentProperties()
	{
		return null;
	}


	@Override
	public JRFillDataset getFillDataset() {
		// TODO Auto-generated method stub
		return null;
	}


	@Override
	public JRComponentElement getComponentElement() {
		// TODO Auto-generated method stub
		return null;
	}


	@Override
	public int getElementSourceId() {
		// TODO Auto-generated method stub
		return 0;
	}


	@Override
	public PrintElementOriginator getPrintElementOriginator() {
		// TODO Auto-generated method stub
		return null;
	}


	@Override
	public Object evaluate(JRExpression expression, byte evaluation)
			throws JRException {
		// TODO Auto-generated method stub
		return null;
	}


	@Override
	public JRDefaultStyleProvider getDefaultStyleProvider() {
		// TODO Auto-generated method stub
		return null;
	}


	@Override
	public JROrigin getElementOrigin() {
		// TODO Auto-generated method stub
		return null;
	}


	@Override
	public int getElementPrintY() {
		// TODO Auto-generated method stub
		return 0;
	}


	@Override
	public JRStyle getElementStyle() {
		// TODO Auto-generated method stub
		return null;
	}


	@Override
	public void registerDelayedEvaluation(JRPrintElement printElement,
			EvaluationTimeEnum evaluationTime, String evaluationGroup) {
		// TODO Auto-generated method stub
		
	}


	@Override
	public ResourceBundle getReportResourceBundle() {
		// TODO Auto-generated method stub
		return null;
	}


	@Override
	public Locale getReportLocale() {
		// TODO Auto-generated method stub
		return null;
	}


	@Override
	public TimeZone getReportTimezone() {
		// TODO Auto-generated method stub
		return null;
	}


	@Override
	public JRBaseFiller getFiller() {
		// TODO Auto-generated method stub
		return null;
	}


	@Override
	public FillContainerContext getFillContainerContext() {
		// TODO Auto-generated method stub
		return null;
	}


	@Override
	public ComponentKey getComponentKey() {
		// TODO Auto-generated method stub
		return null;
	}


	@Override
	public Component getComponent() {
		// TODO Auto-generated method stub
		return null;
	}


	/**
	 *
	 */
	public Object clone() 
	{
		throw new UnsupportedOperationException();
	}

}
