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

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import net.sf.jasperreports.engine.JRAbstractObjectFactory;
import net.sf.jasperreports.engine.JRChartDataset;
import net.sf.jasperreports.engine.JRChild;
import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.JRExpression;
import net.sf.jasperreports.engine.JRExpressionCollector;
import net.sf.jasperreports.engine.JRGraphicElement;
import net.sf.jasperreports.engine.JRParameter;
import net.sf.jasperreports.engine.JRPrintElement;
import net.sf.jasperreports.engine.JRPrintRectangle;
import net.sf.jasperreports.engine.JRRuntimeException;
import net.sf.jasperreports.engine.JasperReport;
import net.sf.jasperreports.engine.crosstab.JRCellContents;
import net.sf.jasperreports.engine.crosstab.JRCrosstab;
import net.sf.jasperreports.engine.crosstab.JRCrosstabBucket;
import net.sf.jasperreports.engine.crosstab.JRCrosstabCell;
import net.sf.jasperreports.engine.crosstab.JRCrosstabColumnGroup;
import net.sf.jasperreports.engine.crosstab.JRCrosstabDataset;
import net.sf.jasperreports.engine.crosstab.JRCrosstabGroup;
import net.sf.jasperreports.engine.crosstab.JRCrosstabMeasure;
import net.sf.jasperreports.engine.crosstab.JRCrosstabParameter;
import net.sf.jasperreports.engine.crosstab.JRCrosstabRowGroup;
import net.sf.jasperreports.engine.crosstab.calculation.Bucket;
import net.sf.jasperreports.engine.crosstab.calculation.BucketingService;
import net.sf.jasperreports.engine.crosstab.calculation.CrosstabCell;
import net.sf.jasperreports.engine.crosstab.calculation.HeaderCell;
import net.sf.jasperreports.engine.crosstab.calculation.Measure;
import net.sf.jasperreports.engine.crosstab.calculation.Bucket.BucketValue;
import net.sf.jasperreports.engine.crosstab.calculation.Measure.MeasureValue;
import net.sf.jasperreports.engine.design.JRDefaultCompiler;
import net.sf.jasperreports.engine.design.JRDesignRectangle;
import net.sf.jasperreports.engine.fill.crosstab.JRCrosstabExpressionEvaluator;
import net.sf.jasperreports.engine.fill.crosstab.JRFillCrosstabCell;
import net.sf.jasperreports.engine.fill.crosstab.JRFillCrosstabColumnGroup;
import net.sf.jasperreports.engine.fill.crosstab.JRFillCrosstabGroup;
import net.sf.jasperreports.engine.fill.crosstab.JRFillCrosstabMeasure;
import net.sf.jasperreports.engine.fill.crosstab.JRFillCrosstabParameter;
import net.sf.jasperreports.engine.fill.crosstab.JRFillCrosstabRowGroup;
import net.sf.jasperreports.engine.fill.crosstab.JRPrintCell;
import net.sf.jasperreports.engine.xml.JRXmlWriter;

import org.jfree.data.general.Dataset;

/**
 * @author Lucian Chirita (lucianc@users.sourceforge.net)
 * @version $Id$
 */
public class JRFillCrosstab extends JRFillElement implements JRCrosstab
{
	final protected JRCrosstab parentCrosstab;
	protected JRFillCrosstabDataset dataset;
	protected JRFillCrosstabRowGroup[] rowGroups;
	protected Map rowGroupsMap;
	protected JRFillCrosstabColumnGroup[] columnGroups;
	protected Map columnGroupsMap;
	protected JRFillCrosstabMeasure[] measures;
	protected BucketingService bucketingService;
	protected byte fillOrder = JRCrosstab.FILL_ORDER_HORIZONTAL;
	protected boolean repeatColumnHeaders = true;
	protected boolean repeatRowHeaders = true;
	protected JRFillVariable[] variables;
	protected Map variablesMap;
	protected JRFillCrosstabParameter[] parameters;
	protected Map parametersMap;
	protected JRCrosstabExpressionEvaluator crosstabEvaluator;
	protected JRFillCellContents[][] crossCells;
	protected HeaderCell[][] columnHeadersData;
	protected HeaderCell[][] rowHeadersData;
	protected CrosstabCell[][] cellData;
	protected int[] rowYOffsets;
	protected int[] rowHeaderXOffsets;
	protected int[] columnXOffsets;
	protected int[] columnHeadersYOffsets;
	protected JRPrintCell[][] rowHeaderPrints;
	protected JRPrintCell[][] columnHeaderPrints;
	protected JRPrintCell[][] dataPrints;
	
	protected int rowIndex;
	protected boolean columnHeadersPrinted;

	public JRFillCrosstab(JRBaseFiller filler, JRCrosstab crosstab, JRFillObjectFactory factory)
	{
		super(filler, crosstab, factory);

		parentCrosstab = crosstab;

		loadEvaluator(filler.getJasperReport());
		
		fillOrder = crosstab.getFillOrder();
		repeatColumnHeaders = crosstab.isRepeatColumnHeaders();
		repeatRowHeaders = crosstab.isRepeatRowHeaders();
		
		JRFillObjectFactory crosstabFactory = new JRFillObjectFactory(filler, crosstabEvaluator);
		
		copyRowGroups(crosstab, crosstabFactory);		
		setRowHeadersXOffsets();
		
		copyColumnGroups(crosstab, crosstabFactory);
		setColumnHeadersYOffsets();
		
		copyMeasures(crosstab, crosstabFactory);
		
		copyCells(crosstab, crosstabFactory);
		setRowHeadersSizes();
		setColumnHeadersSizes();

		dataset = factory.getCrosstabDataset(crosstab.getDataset(), this);
		
		copyParameters(crosstab, factory);
		
		initVariables();
	}

	private void copyRowGroups(JRCrosstab crosstab, JRFillObjectFactory factory)
	{
		JRCrosstabRowGroup[] groups = crosstab.getRowGroups();
		rowGroups = new JRFillCrosstabRowGroup[groups.length];
		rowGroupsMap = new HashMap();
		for (int i = 0; i < groups.length; ++i)
		{
			JRFillCrosstabRowGroup group = factory.getCrosstabRowGroup(groups[i]);
			
			rowGroups[i] = group;
			rowGroupsMap.put(group.getName(), new Integer(i));
		}
	}

	private void setRowHeadersSizes()
	{
		for (int i = rowGroups.length - 1, widthSum = 0, heightSum = 0; i >= 0; --i)
		{
			JRFillCrosstabRowGroup group = rowGroups[i];

			widthSum += group.getWidth();
			if (crossCells[i + 1][columnGroups.length] != null)
			{
				heightSum += crossCells[i + 1][columnGroups.length].getHeight();
			}
			
			JRFillCellContents header = group.getFillHeader();
			if (header != null)
			{
				header.setHeight(heightSum);
				header.setWidth(group.getWidth());
				header.setElementsBandBottomY();
			}
			
			JRFillCellContents totalHeader = group.getFillTotalHeader();			
			if (totalHeader != null)
			{
				totalHeader.setWidth(widthSum);
				totalHeader.setHeight(crossCells[i][columnGroups.length].getHeight());
				totalHeader.setElementsBandBottomY();
			}
		}
	}

	private void setRowHeadersXOffsets()
	{
		rowHeaderXOffsets = new int[rowGroups.length + 1];
		rowHeaderXOffsets[0] = 0;
		for (int i = 0; i < rowGroups.length; i++)
		{
			rowHeaderXOffsets[i + 1] = rowHeaderXOffsets[i] + rowGroups[i].getWidth();
		}
	}

	private void copyColumnGroups(JRCrosstab crosstab, JRFillObjectFactory factory)
	{
		JRCrosstabColumnGroup[] groups = crosstab.getColumnGroups();
		columnGroups = new JRFillCrosstabColumnGroup[groups.length];
		columnGroupsMap = new HashMap();
		for (int i = 0; i < groups.length; ++i)
		{
			JRFillCrosstabColumnGroup group = factory.getCrosstabColumnGroup(groups[i]);
			columnGroups[i] = group;
			columnGroupsMap.put(group.getName(), new Integer(i));
		}
	}

	private void setColumnHeadersSizes()
	{
		for (int i = columnGroups.length - 1, heightSum = 0, widthSum = 0; i >= 0; --i)
		{
			JRFillCrosstabColumnGroup group = columnGroups[i];

			heightSum += group.getHeight();
			if (crossCells[rowGroups.length][i + 1] != null)
			{
				widthSum += crossCells[rowGroups.length][i + 1].getWidth();
			}
			
			
			JRFillCellContents header = group.getFillHeader();
			if (header != null)
			{
				header.setHeight(group.getHeight());
				header.setWidth(widthSum);
				header.setElementsBandBottomY();
			}
			
			JRFillCellContents totalHeader = group.getFillTotalHeader();			
			if (totalHeader != null)
			{
				totalHeader.setHeight(heightSum);
				totalHeader.setWidth(crossCells[rowGroups.length][i].getWidth());
				totalHeader.setElementsBandBottomY();
			}
		}
	}

	private void setColumnHeadersYOffsets()
	{
		columnHeadersYOffsets = new int[columnGroups.length + 1];
		columnHeadersYOffsets[0] = 0;
		for (int i = 0; i < columnGroups.length; i++)
		{
			columnHeadersYOffsets[i + 1] = columnHeadersYOffsets[i] + columnGroups[i].getHeight();
		}
	}

	
	private void copyMeasures(JRCrosstab crosstab, JRFillObjectFactory factory)
	{
		JRCrosstabMeasure[] crossMeasures = crosstab.getMeasures();
		measures = new JRFillCrosstabMeasure[crossMeasures.length];
		for (int i = 0; i < crossMeasures.length; i++)
		{
			measures[i] = factory.getCrosstabMeasure(crossMeasures[i]);
		}
	}

	private void copyParameters(JRCrosstab crosstab, JRFillObjectFactory factory)
	{
		JRCrosstabParameter[] crossParams = crosstab.getParameters();
		parameters = new JRFillCrosstabParameter[crossParams.length];
		parametersMap = new HashMap();
		for (int i = 0; i < crossParams.length; i++)
		{
			parameters[i] = factory.getCrosstabParameter(crossParams[i]);
			parametersMap.put(parameters[i].getName(), parameters[i]);
		}
	}

	
	private void copyCells(JRCrosstab crosstab, JRFillObjectFactory factory)
	{
		JRCrosstabCell[] crosstabCells = crosstab.getCells();
		crossCells = new JRFillCellContents[rowGroups.length + 1][columnGroups.length + 1];
		for (int i = 0; i < crosstabCells.length; i++)
		{
			JRFillCrosstabCell crosstabCell = factory.getCrosstabCell(crosstabCells[i]);
			
			String rowTotalGroup = crosstabCell.getRowTotalGroup();
			int rowGroupIndex = getRowGroupIndex(rowTotalGroup);
			
			if (rowTotalGroup == null)
			{
				crosstabCell.getFillContents().setWidth(crosstabCell.getWidth().intValue());
			}
			
			String columnTotalGroup = crosstabCell.getColumnTotalGroup();
			int columnGroupIndex = getColumnGroupIndex(columnTotalGroup);
			
			if (columnTotalGroup == null)
			{
				crosstabCell.getFillContents().setHeight(crosstabCell.getHeight().intValue());
			}
			
			crossCells[rowGroupIndex][columnGroupIndex] = crosstabCell.getFillContents();
		}
		
		for (int i = 0; i <= rowGroups.length; ++i)
		{
			if (crossCells[i][columnGroups.length] != null)
			{
				for (int j = 0; j < columnGroups.length; ++j)
				{
					if (crossCells[i][j] != null)
					{
						crossCells[i][j].setHeight(crossCells[i][columnGroups.length].getHeight());
					}
				}
			}
		}
				
		for (int i = 0; i <= columnGroups.length; ++i)
		{
			if (crossCells[rowGroups.length][i] != null)
			{
				for (int j = 0; j < rowGroups.length; ++j)
				{
					if (crossCells[j][i] != null)
					{
						crossCells[j][i].setWidth(crossCells[rowGroups.length][i].getWidth());
					}
				}
			}
		}
		
		for (int i = 0; i <= rowGroups.length; ++i)
		{
			for (int j = 0; j < columnGroups.length; ++j)
			{
				if (crossCells[i][j] != null)
				{
					crossCells[i][j].setElementsBandBottomY();
				}
			}
		}
	}

	
	private void initVariables()
	{
		variables = new JRFillVariable[rowGroups.length + columnGroups.length + measures.length];
		
		int c = 0;
		
		for (int i = 0; i < rowGroups.length; i++)
		{
			variables[c++] = rowGroups[i].getFillVariable();
		}
		
		for (int i = 0; i < columnGroups.length; i++)
		{
			variables[c++] = columnGroups[i].getFillVariable();
		}
		
		for (int i = 0; i < measures.length; i++)
		{
			variables[c++] = measures[i].getFillVariable();
		}
		
		variablesMap = new HashMap();
		for (int i = 0; i < variables.length; i++)
		{
			variablesMap.put(variables[i].getName(), variables[i]);
		}
	}

	
	protected int getRowGroupIndex(String groupName)
	{
		return groupName == null ? rowGroups.length : ((Integer) rowGroupsMap.get(groupName)).intValue();
	}

	protected int getColumnGroupIndex(String groupName)
	{
		return groupName == null ? columnGroups.length : ((Integer) columnGroupsMap.get(groupName)).intValue();
	}
	
	protected void loadEvaluator(JasperReport jasperReport)
	{
		try
		{
			JREvaluator evaluator = new JRDefaultCompiler().loadEvaluator(jasperReport, parentCrosstab);
			crosstabEvaluator = new JRCrosstabExpressionEvaluator(evaluator);
		}
		catch (JRException e)
		{
			throw new JRRuntimeException("Could not load evaluator for crosstab " + getName(), e);
		}
	}

	private BucketingService createService(byte evaluation) throws JRException
	{
		List rowBuckets = new ArrayList(rowGroups.length);
		for (int i = 0; i < rowGroups.length; ++i)
		{
			rowBuckets.add(createServiceBucket(rowGroups[i], evaluation));
		}

		List colBuckets = new ArrayList(columnGroups.length);
		for (int i = 0; i < columnGroups.length; ++i)
		{
			colBuckets.add(createServiceBucket(columnGroups[i], evaluation));
		}

		List measureList = new ArrayList(measures.length);
		for (int i = 0; i < measures.length; ++i)
		{
			measureList.add(createMeasure(measures[i]));
		}

		return new BucketingService(rowBuckets, colBuckets, measureList, isDataPreSorted());
	}

	private Bucket createServiceBucket(JRCrosstabGroup group, byte evaluation) throws JRException
	{
		JRCrosstabBucket bucket = group.getBucket();

		Comparator comparator = null;
		JRExpression comparatorExpression = bucket.getComparatorExpression();
		if (comparatorExpression != null)
		{
			comparator = (Comparator) evaluateExpression(comparatorExpression, evaluation);
		}

		return new Bucket(
				bucket.getValueClass(), 
				null, 
				comparator, 
				bucket.getOrder(), 
				group.getTotalPosition());
	}

	private Measure createMeasure(JRCrosstabMeasure measure)
	{
		JRExtendedIncrementerFactory incrementerFactory;

		String incrementerFactoryClassName = measure.getIncrementerFactoryClassName();
		if (incrementerFactoryClassName == null)
		{
			incrementerFactory = JRDefaultIncrementerFactory.getFactory(measure.getValueClass());
		}
		else
		{
			incrementerFactory = (JRExtendedIncrementerFactory) JRIncrementerFactoryCache.getInstance(measure.getIncrementerFactoryClass());
		}

		return new Measure(measure.getValueClass(), measure.getCalculation(), incrementerFactory, measure.getPercentageOfType());
	}

	public JRFillExpressionEvaluator getExpressionEvaluator()
	{
		return crosstabEvaluator;
	}

	protected void reset()
	{
		super.reset();
		
		for (int i = 0; i < variables.length; i++)
		{
			variables[i].setValue(null);
			variables[i].setInitialized(true);
		}
	}

	protected void evaluate(byte evaluation) throws JRException
	{
		reset();

		evaluatePrintWhenExpression(evaluation);

		if ((isPrintWhenExpressionNull() || (!isPrintWhenExpressionNull() && isPrintWhenTrue())))
		{
			dataset.evaluateDatasetRun(evaluation);
			
			initEvaluator(evaluation);
			
			bucketingService.processData();
			
			columnHeadersData = bucketingService.getColumnHeaders();
			rowHeadersData = bucketingService.getRowHeaders();
			cellData = bucketingService.getCrosstabCells();
			
			initFill();
		}
	}

	private void initFill()
	{		
		rowIndex = 0;
		columnHeadersPrinted = false;
		
		computeColumnOffsets();
		computeRowOffsets();
	}

	protected void initEvaluator(byte evaluation) throws JRException
	{
		Map parameterValues = JRFillSubreport.getParameterValues(filler, getParametersMapExpression(), getParameters(), evaluation);
		
		for (int i = 0; i < parameters.length; i++)
		{
			Object value = parameterValues.get(parameters[i].getName());
			parameters[i].setValue(value);
		}
		
		JRFillParameter resourceBundleParam = (JRFillParameter) parametersMap.get(JRParameter.REPORT_RESOURCE_BUNDLE);
		if (resourceBundleParam == null)
		{
			resourceBundleParam = (JRFillParameter) filler.getParametersMap().get(JRParameter.REPORT_RESOURCE_BUNDLE);
		}
		
		crosstabEvaluator.init(parametersMap, variablesMap, resourceBundleParam, filler.getWhenResourceMissingType());
	}

	protected void initBucketingService()
	{
		if (bucketingService == null)
		{
			try
			{
				bucketingService = createService(JRExpression.EVALUATION_TIME_NOW);
			}
			catch (JRException e)
			{
				throw new JRRuntimeException("Could not create bucketing service", e);
			}
		}
		else
		{
			bucketingService.clear();
		}
	}

	private void computeColumnOffsets()
	{
		columnXOffsets = new int[columnHeadersData[0].length + 1];
		columnXOffsets[0] = 0;
		for (int i = 0; i < columnHeadersData[0].length; i++)
		{
			int width = 0;
			for (int j = columnGroups.length - 1; j >= 0; --j)
			{
				if (columnHeadersData[j][i] != null)
				{
					width = columnHeadersData[j][i].isTotal() ? 
							columnGroups[j].getFillTotalHeader().getWidth() :
							columnGroups[j].getFillHeader().getWidth();
					break;
				}
			}
			
			columnXOffsets[i + 1] = columnXOffsets[i] + width;
		}
	}
	
	
	private void computeRowOffsets()
	{
		rowYOffsets = new int[rowHeadersData.length + 1];
		rowYOffsets[0] = 0;
		for (int i = 0, offset = 0; i < rowHeadersData.length; i++)
		{
			int height = 0;
			for (int j = rowGroups.length - 1; j >= 0; --j)
			{
				if (rowHeadersData[i][j] != null)
				{
					height = rowHeadersData[i][j].isTotal() ? 
							rowGroups[j].getFillTotalHeader().getHeight() :
							rowGroups[j].getFillHeader().getHeight();
					break;
				}
			}
			
			offset += height;
			rowYOffsets[i + 1] = offset;
		}
	}

	protected boolean prepare(int availableStretchHeight, boolean isOverflow) throws JRException
	{
		super.prepare(availableStretchHeight, isOverflow);
		
		if (!isToPrint())
		{
			return false;
		}
		
		if (availableStretchHeight < getRelativeY() - getY() - getBandBottomY())
		{
			setToPrint(false);
			return true;//willOverflow;
		}

		boolean fillEnded = rowIndex >= rowHeadersData.length;
		if (isOverflow && fillEnded && !isPrintWhenDetailOverflows() && isAlreadyPrinted())
		{
			setStretchHeight(getHeight());
			setToPrint(false);
			
			return false;
		}
		
		if (isOverflow && isPrintWhenDetailOverflows())
		{
			setReprinted(true);
		}
		
		return fillCrosstab(availableStretchHeight);
	}

	
	protected JRPrintElement fill() throws JRException
	{
		JRPrintRectangle printRectangle = null;

		printRectangle = new JRTemplatePrintRectangle(getJRTemplateRectangle());
		printRectangle.setX(getX());
		printRectangle.setY(getRelativeY());
		printRectangle.setWidth(getWidth());
		printRectangle.setHeight(getStretchHeight());
		
		return printRectangle;
	}
	
	
	protected JRTemplateRectangle getJRTemplateRectangle()
	{
		if (template == null)
		{
			JRDesignRectangle rectangle = new JRDesignRectangle();

			rectangle.setKey(getKey());
			rectangle.setPositionType(getPositionType());
			//rectangle.setPrintRepeatedValues(isPrintRepeatedValues());
			rectangle.setMode(getMode());
			rectangle.setX(getX());
			rectangle.setY(getY());
			rectangle.setWidth(getWidth());
			rectangle.setHeight(getHeight());
			rectangle.setRemoveLineWhenBlank(isRemoveLineWhenBlank());
			rectangle.setPrintInFirstWholeBand(isPrintInFirstWholeBand());
			rectangle.setPrintWhenDetailOverflows(isPrintWhenDetailOverflows());
			rectangle.setPrintWhenGroupChanges(getPrintWhenGroupChanges());
			rectangle.setForecolor(getForecolor());
			rectangle.setBackcolor(getBackcolor());
			rectangle.setPen(JRGraphicElement.PEN_NONE);

			template = new JRTemplateRectangle(rectangle);
		}
		
		return (JRTemplateRectangle)template;
	}
	
	
	protected void rewind() throws JRException
	{
		// TODO luci Auto-generated method stub

	}

	
	protected boolean fillCrosstab(int availableStretchHeight) throws JRException
	{
		int availableHeight = getHeight() + availableStretchHeight - getRelativeY() + getY() + getBandBottomY();
		int lastRowIndex = computeLastRowIndex(availableHeight);
		if (lastRowIndex > 0 && lastRowIndex < rowHeadersData.length)
		{
			breakRowHeaders(lastRowIndex);
		}
		
		if (lastRowIndex == rowIndex)
		{
			setStretchHeight(availableHeight);
			return true;
		}
		
		int columnHeadersYOffset;
		if (printColumnHeaders())
		{
			fillColumnHeaders();
			
			columnHeadersYOffset = columnHeadersYOffsets[columnGroups.length];			
			columnHeadersPrinted = true;
		}
		else
		{
			columnHeaderPrints = null;
			columnHeadersYOffset = 0;
		}
		
		fillRowHeaders(lastRowIndex, columnHeadersYOffset);
		
		fillDataCells(lastRowIndex, columnHeadersYOffset);
		
		boolean fillEnded = lastRowIndex >= rowHeadersData.length;
		if (fillEnded)
		{
			setStretchHeight(columnHeadersYOffset + rowYOffsets[lastRowIndex] - rowYOffsets[rowIndex]);
		}
		else
		{
			setStretchHeight(availableHeight);
		}

		rowIndex = lastRowIndex;
		if (rowIndex >= rowHeadersData.length)
		{
			columnHeadersPrinted = false;
		}
		
		return !fillEnded;
	}
	
	
	private boolean printColumnHeaders()
	{
		return !columnHeadersPrinted || isRepeatColumnHeaders();
	}
	
	
	private int getColumnHeadersYOffset()
	{
		return printColumnHeaders() ? columnHeadersYOffsets[columnGroups.length] : 0;
	}
	
	private int computeLastRowIndex(int availableHeight)
	{
		int maxYOffset = availableHeight - getColumnHeadersYOffset() + rowYOffsets[rowIndex];
		int i = rowIndex;
		while (i < rowHeadersData.length && rowYOffsets[i + 1] <= maxYOffset)
		{
			++i;
		}

		return i;
	}

	
	private void breakRowHeaders(int lastRowIndex)
	{
		for (int i = 0; i < rowGroups.length; ++i)
		{
			HeaderCell cell = rowHeadersData[lastRowIndex - 1][i];
			if (cell == null)
			{
				int spanIndex = lastRowIndex - 2;
				while (spanIndex >= 0 && rowHeadersData[spanIndex][i] == null)
				{
					--spanIndex;
				}
				
				if (spanIndex >= 0)
				{
					HeaderCell spanCell = rowHeadersData[spanIndex][i];
					int span = spanCell.getRowSpan();
					
					if (span > lastRowIndex - spanIndex)
					{
						rowHeadersData[spanIndex][i] = HeaderCell.createCopy(spanCell, lastRowIndex - spanIndex);
						rowHeadersData[lastRowIndex][i] = HeaderCell.createCopy(spanCell, span + spanIndex - lastRowIndex);						
					}
				}
			}
			else
			{
				int span = cell.getRowSpan();
				
				if (span > 1)
				{
					rowHeadersData[lastRowIndex - 1][i] = HeaderCell.createCopy(cell, 1);
					rowHeadersData[lastRowIndex][i] = HeaderCell.createCopy(cell, span - 1);
				}
			}
		}
	}

	protected void fillColumnHeaders() throws JRException
	{
		columnHeaderPrints = new JRPrintCell[columnGroups.length][columnHeadersData[0].length];
		
		for (int i = 0; i < columnGroups.length; i++)
		{
			JRFillCrosstabColumnGroup group = columnGroups[i];
			JRFillCellContents header = group.getFillHeader();
			JRFillCellContents totalHeader = group.getFillTotalHeader();
			byte position = group.getPosition();
			
			for (int j = 0; j < columnHeadersData[i].length; ++j)
			{
				HeaderCell cell = columnHeadersData[i][j];
				if (cell != null)
				{
					setGroupVariables(columnGroups, cell.getBucketValues());
					
					JRFillCellContents contents;
					if (cell.isTotal())
					{
						contents = totalHeader;
					}
					else
					{
						contents = header;
					}
					
					if (contents != null)
					{
						int width = columnXOffsets[j + cell.getColSpan()] - columnXOffsets[j];

						contents = JRFillCellContents.getTransformedContents(filler, this, contents, width, contents.getHeight(), position, JRCellContents.POSITION_Y_TOP);

						JRPrintCell printCell = fillCellContents(contents);
						printCell.setPosition(rowHeaderXOffsets[rowGroups.length] + columnXOffsets[j], columnHeadersYOffsets[i]);

						columnHeaderPrints[i][j] = printCell;
					}
				}
			}
		}
	}

	
	protected void fillRowHeaders(int lastRowIndex, int columnHeadersYOffset) throws JRException
	{
		rowHeaderPrints = new JRPrintCell[lastRowIndex - rowIndex][rowGroups.length];

		for (int i = 0; i < rowGroups.length; i++)
		{
			JRFillCrosstabRowGroup group = rowGroups[i];
			JRFillCellContents header = group.getFillHeader();
			JRFillCellContents totalHeader = group.getFillTotalHeader();
			byte position = group.getPosition();
			
			for (int j = rowIndex; j < lastRowIndex; ++j)
			{
				HeaderCell cell = rowHeadersData[j][i];
				if (cell != null)
				{
					setGroupVariables(rowGroups, cell.getBucketValues());
					
					JRFillCellContents contents;
					if (cell.isTotal())
					{
						contents = totalHeader;
					}
					else
					{
						contents = header;
					}
					
					if (contents != null)
					{
						int height = rowYOffsets[j + cell.getRowSpan()] - rowYOffsets[j];

						contents = JRFillCellContents.getTransformedContents(filler, this, contents, contents.getWidth(), height, JRCellContents.POSITION_X_LEFT, position);

						JRPrintCell printCell = fillCellContents(contents);
						printCell.setPosition(rowHeaderXOffsets[i], columnHeadersYOffset + rowYOffsets[j] - rowYOffsets[rowIndex]);

						rowHeaderPrints[j - rowIndex][i] = printCell;
					}
				}
			}
		}
	}
	

	private void setGroupVariables(JRFillCrosstabGroup[] groups, BucketValue[] bucketValues)
	{
		for (int i = 0; i < groups.length; i++)
		{
			Object value = null;
			if (bucketValues[i] != null && !bucketValues[i].isTotal())
			{
				value = bucketValues[i].getValue();
			}
			groups[i].getFillVariable().setValue(value);
		}
	}
	

	private void setMeasureVariables(MeasureValue[] values)
	{
		for (int i = 0; i < measures.length; i++)
		{
			measures[i].getFillVariable().setValue(values[i].getValue());
		}
	}
	
	
	private void fillDataCells(int lastRowIndex, int columnHeadersYOffset) throws JRException
	{
		dataPrints = new JRPrintCell[lastRowIndex - rowIndex][columnHeadersData[0].length];
		
		for (int i = rowIndex; i < lastRowIndex; ++i)
		{
			for (int j = 0; j < columnHeadersData[0].length; ++j)
			{
				CrosstabCell data = cellData[i][j];
				
				JRFillCellContents contents = crossCells[data.getRowTotalGroupIndex()][data.getColumnTotalGroupIndex()];
				
				setGroupVariables(rowGroups, data.getRowBucketValues());
				setGroupVariables(columnGroups, data.getColumnBucketValues());
				setMeasureVariables(data.getMesureValues());

				JRPrintCell printCell = fillCellContents(contents);
				printCell.setPosition(rowHeaderXOffsets[rowGroups.length] + columnXOffsets[j], columnHeadersYOffset + rowYOffsets[i] - rowYOffsets[rowIndex]);
				
				dataPrints[i - rowIndex][j] = printCell;
			}
		}
	}

	
	private JRPrintCell fillCellContents(JRFillCellContents contents) throws JRException
	{
		contents.evaluate(JRExpression.EVALUATION_DEFAULT);
		
		return contents.fill(0);
	}

	
	protected List getPrintElements()
	{
		List printElements = new ArrayList();
		
		collectColumnHeaders(printElements);
		collectRowHeaders(printElements);
		collectDataCells(printElements);
		
		return printElements;
	}

	private void collectColumnHeaders(List printElements)
	{
		if (columnHeaderPrints != null)
		{
			for (int i = 0; i < columnGroups.length; i++)
			{
				for (int j = 0; j < columnHeaderPrints[i].length; ++j)
				{
					collectCell(printElements, columnHeaderPrints[i][j]);
				}
			}
		}
	}

	
	private void collectRowHeaders(List printElements)
	{
		if (rowHeaderPrints != null)
		{
			for (int i = 0; i < rowHeaderPrints.length; i++)
			{
				for (int j = 0; j < rowGroups.length; ++j)
				{
					collectCell(printElements, rowHeaderPrints[i][j]);
				}
			}
		}
	}

	
	private void collectDataCells(List printElements)
	{
		if (dataPrints != null)
		{
			for (int i = 0; i < dataPrints.length; i++)
			{
				for (int j = 0; j < dataPrints[i].length; j++)
				{
					collectCell(printElements, dataPrints[i][j]);
				}
			}
		}
	}

	
	private void collectCell(List printElements, JRPrintCell cell)
	{
		if (cell != null)
		{
			printElements.addAll(cell.getElements());
		}
	}

	
	protected void resolveElement(JRPrintElement element, byte evaluation) throws JRException
	{
		// nothing
	}

	public void collectExpressions(JRExpressionCollector collector)
	{
		collector.collect(this);
	}

	public JRChild getCopy(JRAbstractObjectFactory factory)
	{
		return factory.getCrosstab(this);
	}

	public void writeXml(JRXmlWriter writer)
	{
		writer.writeCrosstab(this);
	}

	public String getName()
	{
		return parentCrosstab.getName();
	}

	public JRCrosstabDataset getDataset()
	{
		return dataset;
	}

	public JRCrosstabRowGroup[] getRowGroups()
	{
		return rowGroups;
	}

	public JRCrosstabColumnGroup[] getColumnGroups()
	{
		return columnGroups;
	}

	public JRCrosstabMeasure[] getMeasures()
	{
		return measures;
	}

	public class JRFillCrosstabDataset extends JRFillChartDataset implements JRCrosstabDataset
	{
		private Object[] bucketValues;

		private Object[] measureValues;

		public JRFillCrosstabDataset(JRCrosstabDataset dataset, JRFillObjectFactory factory)
		{
			super(dataset, factory);

			this.bucketValues = new Object[rowGroups.length + columnGroups.length];
			this.measureValues = new Object[measures.length];
		}

		protected void customInitialize()
		{
			initBucketingService();
		}

		protected void customEvaluate(JRCalculator calculator) throws JRExpressionEvalException
		{
			for (int i = 0; i < rowGroups.length; i++)
			{
				bucketValues[i] = calculator.evaluate(rowGroups[i].getBucket().getExpression());
			}

			for (int i = 0; i < columnGroups.length; ++i)
			{
				bucketValues[i + rowGroups.length] = calculator.evaluate(columnGroups[i].getBucket().getExpression());
			}

			for (int i = 0; i < measures.length; i++)
			{
				measureValues[i] = calculator.evaluate(measures[i].getValueExpression());
			}
		}

		protected void customIncrement()
		{
			try
			{
				bucketingService.addData(bucketValues, measureValues);
			}
			catch (JRException e)
			{
				throw new JRRuntimeException("Error incrementing crosstab dataset", e);
			}
		}

		protected Dataset getCustomDataset()
		{
			return null;
		}

		public byte getDatasetType()
		{
			return JRChartDataset.CROSSTAB;
		}

		public void collectExpressions(JRExpressionCollector collector)
		{
		}
	}

	public boolean isDataPreSorted()
	{
		return parentCrosstab.isDataPreSorted();
	}

	public byte getFillOrder()
	{
		return fillOrder;
	}

	public boolean isRepeatColumnHeaders()
	{
		return repeatColumnHeaders;
	}

	public boolean isRepeatRowHeaders()
	{
		return repeatRowHeaders;
	}

	public JRCrosstabCell[] getCells()
	{
		return parentCrosstab.getCells();
	}

	public JRCrosstabParameter[] getParameters()
	{
		return parentCrosstab.getParameters();
	}

	public JRExpression getParametersMapExpression()
	{
		return parentCrosstab.getParametersMapExpression();
	}
}
