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
package net.sf.jasperreports.engine.base;

import java.awt.Color;

import net.sf.jasperreports.charts.JRAreaPlot;
import net.sf.jasperreports.charts.JRBar3DPlot;
import net.sf.jasperreports.charts.JRBarPlot;
import net.sf.jasperreports.charts.JRBubblePlot;
import net.sf.jasperreports.charts.JRCandlestickPlot;
import net.sf.jasperreports.charts.JRCategoryDataset;
import net.sf.jasperreports.charts.JRHighLowDataset;
import net.sf.jasperreports.charts.JRHighLowPlot;
import net.sf.jasperreports.charts.JRLinePlot;
import net.sf.jasperreports.charts.JRPie3DPlot;
import net.sf.jasperreports.charts.JRPieDataset;
import net.sf.jasperreports.charts.JRPiePlot;
import net.sf.jasperreports.charts.JRScatterPlot;
import net.sf.jasperreports.charts.JRTimePeriodDataset;
import net.sf.jasperreports.charts.JRTimeSeriesDataset;
import net.sf.jasperreports.charts.JRTimeSeriesPlot;
import net.sf.jasperreports.charts.JRXyDataset;
import net.sf.jasperreports.charts.JRXyzDataset;
import net.sf.jasperreports.engine.JRAbstractObjectFactory;
import net.sf.jasperreports.engine.JRAnchor;
import net.sf.jasperreports.engine.JRBox;
import net.sf.jasperreports.engine.JRChart;
import net.sf.jasperreports.engine.JRChartDataset;
import net.sf.jasperreports.engine.JRChartPlot;
import net.sf.jasperreports.engine.JRChild;
import net.sf.jasperreports.engine.JRExpression;
import net.sf.jasperreports.engine.JRExpressionCollector;
import net.sf.jasperreports.engine.JRFont;
import net.sf.jasperreports.engine.JRGroup;
import net.sf.jasperreports.engine.JRHyperlink;
import net.sf.jasperreports.engine.JRRuntimeException;
import net.sf.jasperreports.engine.JRGraphicElement;
import net.sf.jasperreports.engine.JRStyle;
import net.sf.jasperreports.engine.xml.JRXmlWriter;


/**
 * @author Teodor Danciu (teodord@users.sourceforge.net)
 * @version $Id$
 */
public class JRBaseChart extends JRBaseElement implements JRChart
{


	/**
	 *
	 */
	private static final long serialVersionUID = 10001;

	/**
	 *
	 */
	protected byte chartType = 0;

	/**
	 *
	 */
	protected boolean isShowLegend = false;
	protected byte evaluationTime = JRExpression.EVALUATION_TIME_NOW;
	protected byte hyperlinkType = JRHyperlink.HYPERLINK_TYPE_NONE;
	protected byte hyperlinkTarget = JRHyperlink.HYPERLINK_TARGET_SELF;
	protected byte titlePosition = JRChart.TITLE_POSITION_TOP;
	protected Color titleColor = Color.black;
	protected Color subtitleColor = Color.black;

	/**
	 *
	 */
	protected JRBox box = null;
	protected JRFont titleFont = null;
	protected JRFont subtitleFont = null;

	protected String customizerClass;

	/**
	 *
	 */
	protected JRGroup evaluationGroup = null;
	protected JRExpression titleExpression = null;
	protected JRExpression subtitleExpression = null;
	protected JRExpression anchorNameExpression = null;
	protected JRExpression hyperlinkReferenceExpression = null;
	protected JRExpression hyperlinkAnchorExpression = null;
	protected JRExpression hyperlinkPageExpression = null;

	protected JRChartDataset dataset = null;
	protected JRChartPlot plot = null;


	/**
	 *
	 */
	protected Byte border;
	protected Byte topBorder = null;
	protected Byte leftBorder = null;
	protected Byte bottomBorder = null;
	protected Byte rightBorder = null;
	protected Color borderColor = null;
	protected Color topBorderColor = null;
	protected Color leftBorderColor = null;
	protected Color bottomBorderColor = null;
	protected Color rightBorderColor = null;
	protected Integer padding;
	protected Integer topPadding = null;
	protected Integer leftPadding = null;
	protected Integer bottomPadding = null;
	protected Integer rightPadding = null;


	/**
	 * The bookmark level for the anchor associated with this chart.
	 * @see JRAnchor#getBookmarkLevel()
	 */
	protected int bookmarkLevel = JRAnchor.NO_BOOKMARK;
	
	/**
	 *
	 */
	protected JRBaseChart(JRChart chart, JRBaseObjectFactory factory)
	{
		super(chart, factory);

		chartType = chart.getChartType();

		switch(chartType) {
			case CHART_TYPE_AREA:
				dataset = factory.getCategoryDataset((JRCategoryDataset) chart.getDataset());
				plot = factory.getAreaPlot((JRAreaPlot) chart.getPlot());
				break;
			case CHART_TYPE_BAR:
				dataset = factory.getCategoryDataset((JRCategoryDataset) chart.getDataset());
				plot = factory.getBarPlot((JRBarPlot) chart.getPlot());
				break;
			case CHART_TYPE_BAR3D:
				dataset = factory.getCategoryDataset((JRCategoryDataset) chart.getDataset());
				plot = factory.getBar3DPlot((JRBar3DPlot) chart.getPlot());
				break;
			case CHART_TYPE_BUBBLE:
				dataset = factory.getXyzDataset((JRXyzDataset) chart.getDataset());
				plot = factory.getBubblePlot((JRBubblePlot) chart.getPlot());
				break;
			case CHART_TYPE_CANDLESTICK:
				dataset = factory.getHighLowDataset((JRHighLowDataset) chart.getDataset());
				plot = factory.getCandlestickPlot((JRCandlestickPlot) chart.getPlot());
				break;
			case CHART_TYPE_HIGHLOW:
				dataset = factory.getHighLowDataset((JRHighLowDataset) chart.getDataset());
				plot = factory.getHighLowPlot((JRHighLowPlot) chart.getPlot());
				break;
			case CHART_TYPE_LINE:
				dataset = factory.getCategoryDataset((JRCategoryDataset) chart.getDataset());
				plot = factory.getLinePlot((JRLinePlot) chart.getPlot());
				break;
			case CHART_TYPE_PIE:
				dataset = factory.getPieDataset((JRPieDataset) chart.getDataset());
				plot = factory.getPiePlot((JRPiePlot) chart.getPlot());
				break;
			case CHART_TYPE_PIE3D:
				dataset = factory.getPieDataset((JRPieDataset) chart.getDataset());
				plot = factory.getPie3DPlot((JRPie3DPlot) chart.getPlot());
				break;
			case CHART_TYPE_SCATTER:
				dataset = factory.getXyDataset((JRXyDataset) chart.getDataset());
				plot = factory.getScatterPlot((JRScatterPlot) chart.getPlot());
				break;
			case CHART_TYPE_STACKEDBAR:
				dataset = factory.getCategoryDataset((JRCategoryDataset) chart.getDataset());
				plot = factory.getBarPlot((JRBarPlot) chart.getPlot());
				break;
			case CHART_TYPE_STACKEDBAR3D:
				dataset = factory.getCategoryDataset((JRCategoryDataset) chart.getDataset());
				plot = factory.getBar3DPlot((JRBar3DPlot) chart.getPlot());
				break;
			case CHART_TYPE_TIMESERIES:
				dataset = factory.getTimeSeriesDataset((JRTimeSeriesDataset)chart.getDataset());
				plot = factory.getTimeSeriesPlot( (JRTimeSeriesPlot)chart.getPlot() );
				break;
			case CHART_TYPE_XYAREA:
				dataset = factory.getXyDataset((JRXyDataset) chart.getDataset());
				plot = factory.getAreaPlot((JRAreaPlot) chart.getPlot());
				break;
			case CHART_TYPE_XYBAR:
				
				switch (chart.getDataset().getDatasetType()){
					case JRChartDataset.TIMESERIES_DATASET:
						dataset = factory.getTimeSeriesDataset((JRTimeSeriesDataset) chart.getDataset());
						break;
					case JRChartDataset.TIMEPERIOD_DATASET:
						dataset = factory.getTimePeriodDataset((JRTimePeriodDataset) chart.getDataset() );
						break;
					case JRChartDataset.XY_DATASET:
						dataset = factory.getXyDataset( (JRXyDataset)chart.getDataset() );
						break;
				}
				plot = factory.getBarPlot((JRBarPlot)chart.getPlot());
				break;
			case CHART_TYPE_XYLINE:
				dataset = factory.getXyDataset((JRXyDataset) chart.getDataset());
				plot = factory.getLinePlot((JRLinePlot) chart.getPlot());
				break;
			default:
				throw new JRRuntimeException("Chart type not supported.");
		}

		isShowLegend = chart.isShowLegend();
		evaluationTime = chart.getEvaluationTime();
		hyperlinkType = chart.getHyperlinkType();
		hyperlinkTarget = chart.getHyperlinkTarget();
		titlePosition = chart.getTitlePosition();
		titleColor = chart.getTitleColor();
		subtitleColor = chart.getSubtitleColor();

		titleFont = factory.getFont(chart.getTitleFont());
		subtitleFont = factory.getFont(chart.getSubtitleFont());

		evaluationGroup = factory.getGroup(chart.getEvaluationGroup());
		titleExpression = factory.getExpression(chart.getTitleExpression());
		subtitleExpression = factory.getExpression(chart.getSubtitleExpression());
		anchorNameExpression = factory.getExpression(chart.getAnchorNameExpression());
		hyperlinkReferenceExpression = factory.getExpression(chart.getHyperlinkReferenceExpression());
		hyperlinkAnchorExpression = factory.getExpression(chart.getHyperlinkAnchorExpression());
		hyperlinkPageExpression = factory.getExpression(chart.getHyperlinkPageExpression());
		bookmarkLevel = chart.getBookmarkLevel();

		customizerClass = chart.getCustomizerClass();

		border = chart.getOwnBorder();
		topBorder = chart.getOwnTopBorder();
		leftBorder = chart.getOwnLeftBorder();
		bottomBorder = chart.getOwnBottomBorder();
		rightBorder = chart.getOwnRightBorder();
		borderColor = chart.getOwnBorderColor();
		topBorderColor = chart.getOwnTopBorderColor();
		leftBorderColor = chart.getOwnLeftBorderColor();
		bottomBorderColor = chart.getOwnBottomBorderColor();
		rightBorderColor = chart.getOwnRightBorderColor();
		padding = chart.getOwnPadding();
		topPadding = chart.getOwnTopPadding();
		leftPadding = chart.getOwnLeftPadding();
		bottomPadding = chart.getOwnBottomPadding();
		rightPadding = chart.getOwnRightPadding();

	}
		

	/**
	 *
	 */
	public boolean isShowLegend()
	{
		return this.isShowLegend;
	}

	/**
	 *
	 */
	public void setShowLegend(boolean isShowLegend)
	{
		this.isShowLegend = isShowLegend;
	}

	/**
	 *
	 */
	public byte getEvaluationTime()
	{
		return evaluationTime;
	}
		
	/**
	 *
	 */
	public JRGroup getEvaluationGroup()
	{
		return evaluationGroup;
	}
		
	/**
	 * @deprecated
	 */
	public JRBox getBox()
	{
		return this;
	}

	/**
	 *
	 */
	public JRFont getTitleFont()
	{
		return titleFont;
	}

	/**
	 *
	 */
	public byte getTitlePosition()
	{
		return titlePosition;
	}

	/**
	 *
	 */
	public void setTitlePosition(byte titlePosition)
	{
		this.titlePosition = titlePosition;
	}

	/**
	 *
	 */
	public Color getTitleColor()
	{
		return titleColor;
	}

	/**
	 *
	 */
	public void setTitleColor(Color titleColor)
	{
		this.titleColor = titleColor;
	}

	/**
	 *
	 */
	public JRFont getSubtitleFont()
	{
		return subtitleFont;
	}

	/**
	 *
	 */
	public Color getSubtitleColor()
	{
		return subtitleColor;
	}

	/**
	 *
	 */
	public void setSubtitleColor(Color subtitleColor)
	{
		this.subtitleColor = subtitleColor;
	}

	/**
	 *
	 */
	public byte getHyperlinkType()
	{
		return hyperlinkType;
	}
		
	/**
	 *
	 */
	public byte getHyperlinkTarget()
	{
		return hyperlinkTarget;
	}
		
	/**
	 *
	 */
	public JRExpression getTitleExpression()
	{
		return titleExpression;
	}

	/**
	 *
	 */
	public JRExpression getSubtitleExpression()
	{
		return subtitleExpression;
	}

	/**
	 *
	 */
	public JRExpression getAnchorNameExpression()
	{
		return anchorNameExpression;
	}

	/**
	 *
	 */
	public JRExpression getHyperlinkReferenceExpression()
	{
		return hyperlinkReferenceExpression;
	}

	/**
	 *
	 */
	public JRExpression getHyperlinkAnchorExpression()
	{
		return hyperlinkAnchorExpression;
	}

	/**
	 *
	 */
	public JRExpression getHyperlinkPageExpression()
	{
		return hyperlinkPageExpression;
	}
	
	/**
	 *
	 */
	public JRChartDataset getDataset()
	{
		return dataset;
	}

	/**
	 *
	 */
	public JRChartPlot getPlot()
	{
		return plot;
	}

	public byte getChartType()
	{
		return chartType;
	}


	public JRChild getCopy(JRAbstractObjectFactory factory)
	{
		return factory.getChart(this);
	}


	public void collectExpressions(JRExpressionCollector collector)
	{
		collector.collect(this);
	}


	public void writeXml(JRXmlWriter xmlWriter)
	{
		xmlWriter.writeChartTag(this);
	}


	public int getBookmarkLevel()
	{
		return bookmarkLevel;
	}


	/**
	 *
	 */
	public String getCustomizerClass()
	{
		return customizerClass;
	}

	/**
	 *
	 */
	public byte getBorder()
	{
		if (border == null) {
			JRStyle style = getBaseStyle();
			if (style != null && style.getBorder() != null)
				return style.getBorder().byteValue();
			return JRGraphicElement.PEN_NONE;
		}
		return border.byteValue();
	}

	public Byte getOwnBorder()
	{
		return border;
	}

	/**
	 *
	 */
	public void setBorder(byte border)
	{
		this.border = new Byte(border);
	}

	/**
	 *
	 */
	public Color getBorderColor()
	{
		if (borderColor == null) {
			JRStyle style = getBaseStyle();
			if (style != null && style.getBorderColor() != null)
				return style.getBorderColor();
			return Color.black;
		}
		return borderColor;
	}

	public Color getOwnBorderColor()
	{
		return borderColor;
	}

	/**
	 *
	 */
	public void setBorderColor(Color borderColor)
	{
		this.borderColor = borderColor;
	}

	/**
	 *
	 */
	public int getPadding()
	{
		if (padding == null) {
			JRStyle style = getBaseStyle();
			if (style != null && style.getPadding() != null)
				return style.getPadding().intValue();
			return 0;
		}
		return padding.intValue();
	}

	public Integer getOwnPadding()
	{
		return padding;
	}

	/**
	 *
	 */
	public void setPadding(int padding)
	{
		this.padding = new Integer(padding);
	}

	/**
	 *
	 */
	public byte getTopBorder()
	{
		if (topBorder == null)
		{
			if (border != null)
				return border.byteValue();
			JRStyle style = getBaseStyle();
			if (style != null && style.getTopBorder() != null)
				return style.getTopBorder().byteValue();
			return JRGraphicElement.PEN_NONE;
		}
		return topBorder.byteValue();
	}

	/**
	 *
	 */
	public Byte getOwnTopBorder()
	{
		return topBorder;
	}

	/**
	 *
	 */
	public void setTopBorder(byte topBorder)
	{
		this.topBorder = new Byte(topBorder);
	}

	/**
	 *
	 */
	public Color getTopBorderColor()
	{
		if (topBorderColor == null)
		{
			if (borderColor != null)
				return borderColor;
			JRStyle style = getBaseStyle();
			if (style != null && style.getTopBorderColor() != null)
				return style.getTopBorderColor();
		}
		return topBorderColor;
	}

	/**
	 *
	 */
	public Color getOwnTopBorderColor()
	{
		return topBorderColor;
	}

	/**
	 *
	 */
	public void setTopBorderColor(Color topBorderColor)
	{
		this.topBorderColor = topBorderColor;
	}

	/**
	 *
	 */
	public int getTopPadding()
	{
		if (topPadding == null)
		{
			if (padding != null)
				return padding.intValue();
			JRStyle style = getBaseStyle();
			if (style != null && style.getTopPadding() != null)
				return style.getTopPadding().intValue();
			return 0;
		}
		return topPadding.intValue();
	}

	/**
	 *
	 */
	public Integer getOwnTopPadding()
	{
		return topPadding;
	}

	/**
	 *
	 */
	public void setTopPadding(int topPadding)
	{
		this.topPadding = new Integer(topPadding);
	}

	/**
	 *
	 */
	public byte getLeftBorder()
	{
		if (leftBorder == null)
		{
			if (border != null)
				return border.byteValue();
			JRStyle style = getBaseStyle();
			if (style != null && style.getLeftBorder() != null)
				return style.getLeftBorder().byteValue();
			return JRGraphicElement.PEN_NONE;
		}
		return leftBorder.byteValue();
	}

	/**
	 *
	 */
	public Byte getOwnLeftBorder()
	{
		return leftBorder;
	}

	/**
	 *
	 */
	public void setLeftBorder(byte leftBorder)
	{
		this.leftBorder = new Byte(leftBorder);
	}

	/**
	 *
	 */
	public Color getLeftBorderColor()
	{
		if (leftBorderColor == null)
		{
			if (borderColor != null)
				return borderColor;
			JRStyle style = getBaseStyle();
			if (style != null && style.getLeftBorderColor() != null)
				return style.getLeftBorderColor();
		}
		return leftBorderColor;
	}

	/**
	 *
	 */
	public Color getOwnLeftBorderColor()
	{
		return leftBorderColor;
	}

	/**
	 *
	 */
	public void setLeftBorderColor(Color leftBorderColor)
	{
		this.leftBorderColor = leftBorderColor;
	}

	/**
	 *
	 */
	public int getLeftPadding()
	{
		if (leftPadding == null)
		{
			if (padding != null)
				return padding.intValue();
			JRStyle style = getBaseStyle();
			if (style != null && style.getLeftPadding() != null)
				return style.getLeftPadding().intValue();
			return 0;
		}
		return leftPadding.intValue();
	}

	/**
	 *
	 */
	public Integer getOwnLeftPadding()
	{
		return leftPadding;
	}

	/**
	 *
	 */
	public void setLeftPadding(int leftPadding)
	{
		this.leftPadding = new Integer(leftPadding);
	}

	/**
	 *
	 */
	public byte getBottomBorder()
	{
		if (bottomBorder == null)
		{
			if (border != null)
				return border.byteValue();
			JRStyle style = getBaseStyle();
			if (style != null && style.getBottomBorder() != null)
				return style.getBottomBorder().byteValue();
			return JRGraphicElement.PEN_NONE;
		}
		return bottomBorder.byteValue();
	}

	/**
	 *
	 */
	public Byte getOwnBottomBorder()
	{
		return bottomBorder;
	}

	/**
	 *
	 */
	public void setBottomBorder(byte bottomBorder)
	{
		this.bottomBorder = new Byte(bottomBorder);
	}

	/**
	 *
	 */
	public Color getBottomBorderColor()
	{
		if (bottomBorderColor == null)
		{
			if (borderColor != null)
				return borderColor;
			JRStyle style = getBaseStyle();
			if (style != null && style.getBottomBorderColor() != null)
				return style.getBottomBorderColor();
		}
		return bottomBorderColor;
	}

	/**
	 *
	 */
	public Color getOwnBottomBorderColor()
	{
		return bottomBorderColor;
	}

	/**
	 *
	 */
	public void setBottomBorderColor(Color bottomBorderColor)
	{
		this.bottomBorderColor = bottomBorderColor;
	}

	/**
	 *
	 */
	public int getBottomPadding()
	{
		if (bottomPadding == null)
		{
			if (padding != null)
				return padding.intValue();
			JRStyle style = getBaseStyle();
			if (style != null && style.getBottomPadding() != null)
				return style.getBottomPadding().intValue();
			return JRGraphicElement.PEN_NONE;
		}
		return bottomPadding.intValue();
	}

	/**
	 *
	 */
	public Integer getOwnBottomPadding()
	{
		return bottomPadding;
	}

	/**
	 *
	 */
	public void setBottomPadding(int bottomPadding)
	{
		this.bottomPadding = new Integer(bottomPadding);
	}

	/**
	 *
	 */
	public byte getRightBorder()
	{
		if (rightBorder == null)
		{
			if (border != null)
				return border.byteValue();
			JRStyle style = getBaseStyle();
			if (style != null && style.getRightBorder() != null)
				return style.getRightBorder().byteValue();
			return JRGraphicElement.PEN_NONE;
		}
		return rightBorder.byteValue();
	}

	/**
	 *
	 */
	public Byte getOwnRightBorder()
	{
		return rightBorder;
	}

	/**
	 *
	 */
	public void setRightBorder(byte rightBorder)
	{
		this.rightBorder = new Byte(rightBorder);
	}

	/**
	 *
	 */
	public Color getRightBorderColor()
	{
		if (rightBorderColor == null)
		{
			if (borderColor != null)
				return borderColor;
			JRStyle style = getBaseStyle();
			if (style != null && style.getRightBorderColor() != null)
				return style.getRightBorderColor();
		}
		return rightBorderColor;
	}

	/**
	 *
	 */
	public Color getOwnRightBorderColor()
	{
		return rightBorderColor;
	}

	/**
	 *
	 */
	public void setRightBorderColor(Color rightBorderColor)
	{
		this.rightBorderColor = rightBorderColor;
	}

	/**
	 *
	 */
	public int getRightPadding()
	{
		if (rightPadding == null)
		{
			if (padding != null)
				return padding.intValue();
			JRStyle style = getBaseStyle();
			if (style != null && style.getRightPadding() != null)
				return style.getRightPadding().intValue();
			return JRGraphicElement.PEN_NONE;
		}
		return rightPadding.intValue();
	}

	/**
	 *
	 */
	public Integer getOwnRightPadding()
	{
		return rightPadding;
	}

	/**
	 *
	 */
	public void setRightPadding(int rightPadding)
	{
		this.rightPadding = new Integer(rightPadding);
	}
}
