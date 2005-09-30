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
package net.sf.jasperreports.engine.xml;

import java.awt.Color;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.StringWriter;
import java.io.Writer;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import net.sf.jasperreports.charts.JRAreaPlot;
import net.sf.jasperreports.charts.JRBar3DPlot;
import net.sf.jasperreports.charts.JRBarPlot;
import net.sf.jasperreports.charts.JRBubblePlot;
import net.sf.jasperreports.charts.JRCandlestickPlot;
import net.sf.jasperreports.charts.JRCategoryDataset;
import net.sf.jasperreports.charts.JRCategorySeries;
import net.sf.jasperreports.charts.JRHighLowDataset;
import net.sf.jasperreports.charts.JRHighLowPlot;
import net.sf.jasperreports.charts.JRLinePlot;
import net.sf.jasperreports.charts.JRPie3DPlot;
import net.sf.jasperreports.charts.JRPieDataset;
import net.sf.jasperreports.charts.JRScatterPlot;
import net.sf.jasperreports.charts.JRTimePeriodDataset;
import net.sf.jasperreports.charts.JRTimePeriodSeries;
import net.sf.jasperreports.charts.JRTimeSeries;
import net.sf.jasperreports.charts.JRTimeSeriesDataset;
import net.sf.jasperreports.charts.JRTimeSeriesPlot;
import net.sf.jasperreports.charts.JRXyDataset;
import net.sf.jasperreports.charts.JRXySeries;
import net.sf.jasperreports.charts.JRXyzDataset;
import net.sf.jasperreports.charts.JRXyzSeries;
import net.sf.jasperreports.engine.JRAlignment;
import net.sf.jasperreports.engine.JRBand;
import net.sf.jasperreports.engine.JRBox;
import net.sf.jasperreports.engine.JRChart;
import net.sf.jasperreports.engine.JRChartDataset;
import net.sf.jasperreports.engine.JRChartPlot;
import net.sf.jasperreports.engine.JRChild;
import net.sf.jasperreports.engine.JRDataset;
import net.sf.jasperreports.engine.JRDatasetRun;
import net.sf.jasperreports.engine.JRElement;
import net.sf.jasperreports.engine.JRElementGroup;
import net.sf.jasperreports.engine.JREllipse;
import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.JRExpression;
import net.sf.jasperreports.engine.JRField;
import net.sf.jasperreports.engine.JRFont;
import net.sf.jasperreports.engine.JRGraphicElement;
import net.sf.jasperreports.engine.JRGroup;
import net.sf.jasperreports.engine.JRHyperlink;
import net.sf.jasperreports.engine.JRImage;
import net.sf.jasperreports.engine.JRLine;
import net.sf.jasperreports.engine.JRParameter;
import net.sf.jasperreports.engine.JRQuery;
import net.sf.jasperreports.engine.JRRectangle;
import net.sf.jasperreports.engine.JRReport;
import net.sf.jasperreports.engine.JRReportFont;
import net.sf.jasperreports.engine.JRRuntimeException;
import net.sf.jasperreports.engine.JRStaticText;
import net.sf.jasperreports.engine.JRSubreport;
import net.sf.jasperreports.engine.JRSubreportParameter;
import net.sf.jasperreports.engine.JRSubreportReturnValue;
import net.sf.jasperreports.engine.JRTextElement;
import net.sf.jasperreports.engine.JRTextField;
import net.sf.jasperreports.engine.JRVariable;
import net.sf.jasperreports.engine.crosstab.JRCellContents;
import net.sf.jasperreports.engine.crosstab.JRCrosstab;
import net.sf.jasperreports.engine.crosstab.JRCrosstabBucket;
import net.sf.jasperreports.engine.crosstab.JRCrosstabCell;
import net.sf.jasperreports.engine.crosstab.JRCrosstabColumnGroup;
import net.sf.jasperreports.engine.crosstab.JRCrosstabMeasure;
import net.sf.jasperreports.engine.crosstab.JRCrosstabParameter;
import net.sf.jasperreports.engine.crosstab.JRCrosstabRowGroup;
import net.sf.jasperreports.engine.design.crosstab.JRDesignCrosstab;
import net.sf.jasperreports.engine.fill.crosstab.calculation.Bucket;
import net.sf.jasperreports.engine.util.XmlWriter;
import net.sf.jasperreports.engine.xml.crosstab.JRCellContentsFactory;
import net.sf.jasperreports.engine.xml.crosstab.JRCrosstabBucketFactory;
import net.sf.jasperreports.engine.xml.crosstab.JRCrosstabCellFactory;
import net.sf.jasperreports.engine.xml.crosstab.JRCrosstabColumnGroupFactory;
import net.sf.jasperreports.engine.xml.crosstab.JRCrosstabFactory;
import net.sf.jasperreports.engine.xml.crosstab.JRCrosstabGroupFactory;
import net.sf.jasperreports.engine.xml.crosstab.JRCrosstabMeasureFactory;
import net.sf.jasperreports.engine.xml.crosstab.JRCrosstabRowGroupFactory;

import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.renderer.category.BarRenderer3D;


/**
 * @author Teodor Danciu (teodord@users.sourceforge.net)
 * @version $Id$
 */
public class JRXmlWriter
{


	/**
	 *
	 */
	private JRReport report = null;
	private String encoding = null;

	/**
	 *
	 */
	private XmlWriter writer;
	private Map fontsMap = new HashMap();


	/**
	 *
	 */
	protected JRXmlWriter(JRReport report, String encoding)
	{
		this.report = report;
		this.encoding = encoding;
	}


	/**
	 *
	 */
	public static String writeReport(JRReport report, String encoding)
	{
		JRXmlWriter writer = new JRXmlWriter(report, encoding);
		StringWriter buffer = new StringWriter();
		try
		{
			writer.writeReport(buffer);
		}
		catch (IOException e)
		{
			// doesn't actually happen
			throw new JRRuntimeException("Error writing report design.", e);
		}
		return buffer.toString();
	}


	/**
	 *
	 */
	public static void writeReport(
		JRReport report,
		String destFileName,
		String encoding
		) throws JRException
	{		
		FileOutputStream fos = null;

		try
		{
			fos = new FileOutputStream(destFileName);
			Writer out = new OutputStreamWriter(fos, encoding);
			JRXmlWriter writer = new JRXmlWriter(report, encoding);
			writer.writeReport(out);
		}
		catch (IOException e)
		{
			throw new JRException("Error writing to file : " + destFileName, e);
		}
		finally
		{
			if (fos != null)
			{
				try
				{
					fos.close();
				}
				catch(IOException e)
				{
				}
			}
		}
	}


	/**
	 *
	 */
	public static void writeReport(
		JRReport report,
		OutputStream outputStream,
		String encoding
		) throws JRException
	{
		try
		{
			Writer out = new OutputStreamWriter(outputStream, encoding);
			JRXmlWriter writer = new JRXmlWriter(report, encoding);
			writer.writeReport(out);
		}
		catch (Exception e)
		{
			throw new JRException("Error writing to OutputStream : " + report.getName(), e);
		}
	}


	/**
	 *
	 */
	protected void writeReport(Writer out) throws IOException
	{
		writer = new XmlWriter(out);
		
		writer.writeProlog(encoding);
		writer.writePublicDoctype("jasperReport", "-//JasperReports//DTD Report Design//EN", "http://jasperreports.sourceforge.net/dtds/jasperreport.dtd");

		writer.startElement("jasperReport");
		writer.addAttribute("name", report.getName());
		writer.addAttribute("language", report.getLanguage(), JRReport.LANGUAGE_JAVA);
		writer.addAttribute("columnCount", report.getColumnCount(), 1);
		writer.addAttribute("printOrder", report.getPrintOrder(), JRXmlConstants.getPrintOrderMap(), JRReport.PRINT_ORDER_VERTICAL);
		writer.addAttribute("pageWidth", report.getPageWidth());
		writer.addAttribute("pageHeight", report.getPageHeight());
		writer.addAttribute("orientation", report.getOrientation(), JRXmlConstants.getOrientationMap(), JRReport.ORIENTATION_PORTRAIT);
		writer.addAttribute("whenNoDataType", report.getWhenNoDataType(), JRXmlConstants.getWhenNoDataTypeMap(), JRReport.WHEN_NO_DATA_TYPE_NO_PAGES);
		writer.addAttribute("columnWidth", report.getColumnWidth());
		writer.addAttribute("columnSpacing", report.getColumnSpacing(), 0);
		writer.addAttribute("leftMargin", report.getLeftMargin());
		writer.addAttribute("rightMargin", report.getRightMargin());
		writer.addAttribute("topMargin", report.getTopMargin());
		writer.addAttribute("bottomMargin", report.getBottomMargin());
		writer.addAttribute("isTitleNewPage", report.isTitleNewPage(), false);
		writer.addAttribute("isSummaryNewPage", report.isSummaryNewPage(), false);
		writer.addAttribute("isFloatColumnFooter", report.isFloatColumnFooter(), false);
		writer.addAttribute("scriptletClass", report.getScriptletClass());
		writer.addAttribute("resourceBundle", report.getResourceBundle());
		writer.addAttribute("whenResourceMissingType", report.getWhenResourceMissingType(), JRXmlConstants.getWhenResourceMissingTypeMap(), JRReport.WHEN_RESOURCE_MISSING_TYPE_NULL);
		
		/*   */
		String[] propertyNames = report.getPropertyNames();
		if (propertyNames != null && propertyNames.length > 0)
		{
			for(int i = 0; i < propertyNames.length; i++)
			{
				String value = report.getProperty(propertyNames[i]);
				if (value != null)
				{
					writer.startElement("property");
					writer.addAttribute("name", propertyNames[i]);
					writer.addEncodedAttribute("value", value);
					writer.closeElement();
				}
			}
		}

		/*   */
		String[] imports = report.getImports();
		if (imports != null && imports.length > 0)
		{
			for(int i = 0; i < imports.length; i++)
			{
				String value = imports[i];
				if (value != null)
				{
					writer.startElement("import");
					writer.addEncodedAttribute("value", value);
					writer.closeElement();
				}
			}
		}

		/*   */
		JRReportFont[] fonts = report.getFonts();
		if (fonts != null && fonts.length > 0)
		{
			for(int i = 0; i < fonts.length; i++)
			{
				fontsMap.put(fonts[i].getName(), fonts[i]);
				writeReportFont(fonts[i]);
			}
		}
		
		JRDataset[] datasets = report.getDatasets();
		if (datasets != null && datasets.length > 0)
		{
			for (int i = 0; i < datasets.length; ++i)
			{
				writeDataset(datasets[i]);
			}
		}

		writeDatasetContents(report.getMainDataset());
		
		if (report.getBackground() != null)
		{
			writer.startElement("background");
			writeBand(report.getBackground());
			writer.closeElement();
		}

		if (report.getTitle() != null)
		{
			writer.startElement("title");
			writeBand(report.getTitle());
			writer.closeElement();
		}

		if (report.getPageHeader() != null)
		{
			writer.startElement("pageHeader");
			writeBand(report.getPageHeader());
			writer.closeElement();
		}

		if (report.getColumnHeader() != null)
		{
			writer.startElement("columnHeader");
			writeBand(report.getColumnHeader());
			writer.closeElement();
		}

		if (report.getDetail() != null)
		{
			writer.startElement("detail");
			writeBand(report.getDetail());
			writer.closeElement();
		}

		if (report.getColumnFooter() != null)
		{
			writer.startElement("columnFooter");
			writeBand(report.getColumnFooter());
			writer.closeElement();
		}

		if (report.getPageFooter() != null)
		{
			writer.startElement("pageFooter");
			writeBand(report.getPageFooter());
			writer.closeElement();
		}

		if (report.getLastPageFooter() != null)
		{
			writer.startElement("lastPageFooter");
			writeBand(report.getLastPageFooter());
			writer.closeElement();
		}

		if (report.getSummary() != null)
		{
			writer.startElement("summary");
			writeBand(report.getSummary());
			writer.closeElement();
		}

		writer.closeElement();
		
		out.flush();
	}


	/**
	 *
	 */
	private void writeReportFont(JRReportFont font) throws IOException
	{
		writer.startElement("reportFont");
		writer.addAttribute("name", font.getName());
		writer.addAttribute("isDefault", font.isDefault());
		writer.addAttribute("fontName", font.getFontName());
		writer.addAttribute("size", font.getSize());
		writer.addAttribute("isBold", font.isBold());
		writer.addAttribute("isItalic", font.isItalic());
		writer.addAttribute("isUnderline", font.isUnderline());
		writer.addAttribute("isStrikeThrough", font.isStrikeThrough());
		writer.addAttribute("pdfFontName", font.getPdfFontName());
		writer.addAttribute("pdfEncoding", font.getPdfEncoding());
		writer.addAttribute("isPdfEmbedded", font.isPdfEmbedded());
		writer.closeElement();
	}


	/**
	 *
	 */
	private void writeParameter(JRParameter parameter) throws IOException
	{
		writer.startElement("parameter");
		writer.addAttribute("name", parameter.getName());
		writer.addAttribute("class", parameter.getValueClassName());
		writer.addAttribute("isForPrompting", parameter.isForPrompting(), true);

		writer.writeCDATAElement("parameterDescription", parameter.getDescription());
		writer.writeCDATAElement("parameterDescription", parameter.getDescription());
		writer.writeExpression("defaultValueExpression", parameter.getDefaultValueExpression(), false);

		writer.closeElement();
	}


	/**
	 *
	 */
	private void writeQuery(JRQuery query) throws IOException
	{
		writer.writeCDATAElement("queryString", query.getText());
	}


	/**
	 *
	 */
	private void writeField(JRField field) throws IOException
	{
		writer.startElement("field");
		writer.addAttribute("name", field.getName());
		writer.addAttribute("class", field.getValueClassName());

		writer.writeCDATAElement("fieldDescription", field.getDescription());
		
		writer.closeElement();
	}


	/**
	 *
	 */
	private void writeVariable(JRVariable variable) throws IOException
	{
		writer.startElement("variable");
		writer.addAttribute("name", variable.getName());
		writer.addAttribute("class", variable.getValueClassName());
		writer.addAttribute("resetType", variable.getResetType(), JRXmlConstants.getResetTypeMap(), JRVariable.RESET_TYPE_REPORT);
		if (variable.getResetGroup() != null)
		{
			writer.addAttribute("resetGroup", variable.getResetGroup().getName());
		}
		writer.addAttribute("incrementType", variable.getIncrementType(), JRXmlConstants.getResetTypeMap(), JRVariable.RESET_TYPE_NONE);
		if (variable.getIncrementGroup() != null)
		{
			writer.addAttribute("incrementGroup", variable.getIncrementGroup().getName());
		}
		writer.addAttribute("calculation", variable.getCalculation(), JRXmlConstants.getCalculationMap(), JRVariable.CALCULATION_NOTHING);
		writer.addAttribute("incrementerFactoryClass", variable.getIncrementerFactoryClassName());

		writer.writeExpression("variableExpression", variable.getExpression(), false);
		writer.writeExpression("initialValueExpression", variable.getInitialValueExpression(), false);
		
		writer.closeElement();
	}


	/**
	 *
	 */
	private void writeGroup(JRGroup group) throws IOException
	{
		writer.startElement("group");
		writer.addAttribute("name", group.getName());
		writer.addAttribute("isStartNewColumn", group.isStartNewColumn(), false);
		writer.addAttribute("isStartNewPage", group.isStartNewPage(), false);
		writer.addAttribute("isResetPageNumber", group.isResetPageNumber(), false);
		writer.addAttribute("isReprintHeaderOnEachPage", group.isReprintHeaderOnEachPage(), false);
		writer.addAttributePositive("minHeightToStartNewPage", group.getMinHeightToStartNewPage());

		writer.writeExpression("groupExpression", group.getExpression(), false);

		if (group.getGroupHeader() != null)
		{
			writer.startElement("groupHeader");
			writeBand(group.getGroupHeader());
			writer.closeElement();
		}

		if (group.getGroupFooter() != null)
		{
			writer.startElement("groupFooter");
			writeBand(group.getGroupFooter());
			writer.closeElement();
		}

		writer.closeElement();
	}


	/**
	 *
	 */
	private void writeBand(JRBand band) throws IOException
	{
		writer.startElement("band");
		writer.addAttributePositive("height", band.getHeight());
		writer.addAttribute("isSplitAllowed", band.isSplitAllowed(), true);

		writer.writeExpression("printWhenExpression", band.getPrintWhenExpression(), false);

		/*   */
		List children = band.getChildren();
		if (children != null && children.size() > 0)
		{
			for(int i = 0; i < children.size(); i++)
			{
				((JRChild)children.get(i)).writeXml(this);
			}
		}

		writer.closeElement();
	}


	/**
	 *
	 */
	public void writeElementGroup(JRElementGroup elementGroup) throws IOException
	{
		writer.startElement("elementGroup");

		/*   */
		List children = elementGroup.getChildren();
		if (children != null && children.size() > 0)
		{
			for(int i = 0; i < children.size(); i++)
			{
				JRChild child = (JRChild)children.get(i);
				child.writeXml(this);
			}
		}

		writer.closeElement();
	}


	/**
	 *
	 */
	public void writeLine(JRLine line) throws IOException
	{
		writer.startElement("line");
		writer.addAttribute("direction", line.getDirection(), JRXmlConstants.getDirectionMap(), JRLine.DIRECTION_TOP_DOWN);

		writeReportElement(line);
		writeGraphicElement(line);

		writer.closeElement();
	}


	/**
	 *
	 */
	private void writeReportElement(JRElement element) throws IOException
	{
		writer.startElement("reportElement");
		writer.addAttribute("key", element.getKey());
		writer.addAttribute("positionType", element.getPositionType(), JRXmlConstants.getPositionTypeMap(), JRElement.POSITION_TYPE_FIX_RELATIVE_TO_TOP);
		writer.addAttribute("stretchType", element.getStretchType(), JRXmlConstants.getStretchTypeMap(), JRElement.STRETCH_TYPE_NO_STRETCH);
		writer.addAttribute("isPrintRepeatedValues", element.isPrintRepeatedValues(), true);

		if (
			(element instanceof JRLine && element.getMode() != JRElement.MODE_OPAQUE) ||
			(element instanceof JRRectangle && element.getMode() != JRElement.MODE_OPAQUE) ||
			(element instanceof JREllipse && element.getMode() != JRElement.MODE_OPAQUE) ||
			(element instanceof JRImage && element.getMode() != JRElement.MODE_TRANSPARENT) ||
			(element instanceof JRTextElement && element.getMode() != JRElement.MODE_TRANSPARENT) ||
			(element instanceof JRSubreport && element.getMode() != JRElement.MODE_TRANSPARENT) ||
			(element instanceof JRCrosstab && element.getMode() != JRElement.MODE_TRANSPARENT)
			)
		{
			writer.addAttribute("mode", element.getMode(), JRXmlConstants.getModeMap());
		}

		writer.addAttribute("x", element.getX());
		writer.addAttribute("y", element.getY());
		writer.addAttribute("width", element.getWidth());
		writer.addAttribute("height", element.getHeight());
		writer.addAttribute("isRemoveLineWhenBlank", element.isRemoveLineWhenBlank(), false);
		writer.addAttribute("isPrintInFirstWholeBand", element.isPrintInFirstWholeBand(), false);
		writer.addAttribute("isPrintWhenDetailOverflows", element.isPrintWhenDetailOverflows(), false);

		if (element.getPrintWhenGroupChanges() != null)
		{
			writer.addAttribute("printWhenGroupChanges", element.getPrintWhenGroupChanges().getName());
		}
		
		writer.addAttribute("forecolor", element.getForecolor(), Color.black);
		writer.addAttribute("backcolor", element.getBackcolor(), Color.white);
		
		writer.writeExpression("printWhenExpression", element.getPrintWhenExpression(), false);
		writer.closeElement();
	}


	/**
	 *
	 */
	private void writeGraphicElement(JRGraphicElement element) throws IOException
	{
		writer.startElement("graphicElement");

		if (
			(element instanceof JRLine && element.getPen() != JRGraphicElement.PEN_1_POINT) ||
			(element instanceof JRRectangle && element.getPen() != JRGraphicElement.PEN_1_POINT) ||
			(element instanceof JREllipse && element.getPen() != JRGraphicElement.PEN_1_POINT) ||
			(element instanceof JRImage && element.getPen() != JRGraphicElement.PEN_NONE)
			)
		{
			writer.addAttribute("pen", element.getPen(), JRXmlConstants.getPenMap());
		}

		writer.addAttribute("fill", element.getFill(), JRXmlConstants.getFillMap(), JRGraphicElement.FILL_SOLID);
		
		writer.closeElement();
	}


	/**
	 *
	 */
	public void writeRectangle(JRRectangle rectangle) throws IOException
	{
		writer.startElement("rectangle");
		writer.addAttribute("radius", rectangle.getRadius(), 0);

		writeReportElement(rectangle);
		writeGraphicElement(rectangle);

		writer.closeElement();
	}


	/**
	 *
	 */
	public void writeEllipse(JREllipse ellipse) throws IOException
	{
		writer.startElement("ellipse");

		writeReportElement(ellipse);
		writeGraphicElement(ellipse);

		writer.closeElement();
	}


	/**
	 *
	 */
	public void writeImage(JRImage image) throws IOException
	{
		writer.startElement("image");
		writer.addAttribute("scaleImage", image.getScaleImage(), JRXmlConstants.getScaleImageMap(), JRImage.SCALE_IMAGE_RETAIN_SHAPE);
		writer.addAttribute("hAlign", image.getHorizontalAlignment(), JRXmlConstants.getHorizontalAlignMap(), JRAlignment.HORIZONTAL_ALIGN_LEFT);
		writer.addAttribute("vAlign", image.getVerticalAlignment(), JRXmlConstants.getVerticalAlignMap(), JRAlignment.VERTICAL_ALIGN_TOP);
		writer.addAttribute("isUsingCache", image.isUsingCache(), true);
		writer.addAttribute("isLazy", image.isLazy(), false);
		writer.addAttribute("onErrorType", image.getOnErrorType(), JRXmlConstants.getOnErrorTypeMap(), JRImage.ON_ERROR_TYPE_ERROR);
		writer.addAttribute("evaluationTime", image.getEvaluationTime(), JRXmlConstants.getEvaluationTimeMap(), JRExpression.EVALUATION_TIME_NOW);

		if (image.getEvaluationGroup() != null)
		{
			writer.addAttribute("evaluationGroup", image.getEvaluationGroup().getName());
		}

		writer.addAttribute("hyperlinkType", image.getHyperlinkType(), JRXmlConstants.getHyperlinkTypeMap(), JRHyperlink.HYPERLINK_TYPE_NONE);
		writer.addAttribute("hyperlinkTarget", image.getHyperlinkTarget(), JRXmlConstants.getHyperlinkTargetMap(), JRHyperlink.HYPERLINK_TARGET_SELF);

		writeReportElement(image);
		writeBox(image.getBox());
		writeGraphicElement(image);

		//FIXME class is mandatory in verifier
		
		writer.writeExpression("imageExpression", image.getExpression(), true);
		writer.writeExpression("anchorNameExpression", image.getAnchorNameExpression(), false);
		writer.writeExpression("hyperlinkReferenceExpression", image.getHyperlinkReferenceExpression(), false);
		writer.writeExpression("hyperlinkAnchorExpression", image.getHyperlinkAnchorExpression(), false);
		writer.writeExpression("hyperlinkPageExpression", image.getHyperlinkPageExpression(), false);
		
		writer.closeElement();
	}


	/**
	 *
	 */
	private void writeBox(JRBox box) throws IOException
	{
		if (box != null)
		{
			writer.startElement("box");
			writer.addAttribute("border", box.getBorder(), JRXmlConstants.getPenMap(), JRGraphicElement.PEN_NONE);
			writer.addAttribute("borderColor", box.getBorderColor());
			writer.addAttributePositive("padding", box.getPadding());
			
			writer.addAttribute("topBorder", box.getOwnTopBorder(), JRXmlConstants.getPenMap());
			writer.addAttribute("topBorderColor", box.getOwnTopBorderColor());
			writer.addAttribute("topPadding", box.getOwnTopPadding());
			
			writer.addAttribute("leftBorder", box.getOwnLeftBorder(), JRXmlConstants.getPenMap());
			writer.addAttribute("leftBorderColor", box.getOwnLeftBorderColor());
			writer.addAttribute("leftPadding", box.getOwnLeftPadding());
			
			writer.addAttribute("bottomBorder", box.getOwnBottomBorder(), JRXmlConstants.getPenMap());
			writer.addAttribute("bottomBorderColor", box.getOwnBottomBorderColor());
			writer.addAttribute("bottomPadding", box.getOwnBottomPadding());

			
			writer.addAttribute("rightBorder", box.getOwnRightBorder(), JRXmlConstants.getPenMap());
			writer.addAttribute("rightBorderColor", box.getOwnRightBorderColor());
			writer.addAttribute("rightPadding", box.getOwnRightPadding());
		
			writer.closeElement(true);
		}
	}


	/**
	 *
	 */
	public void writeStaticText(JRStaticText staticText) throws IOException
	{
		writer.startElement("staticText");

		writeReportElement(staticText);
		writeBox(staticText.getBox());
		writeTextElement(staticText);

		writer.writeCDATAElement("text", staticText.getText());

		writer.closeElement();
	}


	/**
	 *
	 */
	private void writeTextElement(JRTextElement textElement) throws IOException
	{
		writer.startElement("textElement");
		writer.addAttribute("textAlignment", textElement.getHorizontalAlignment(), JRXmlConstants.getHorizontalAlignMap(), JRAlignment.HORIZONTAL_ALIGN_LEFT);
		writer.addAttribute("verticalAlignment", textElement.getVerticalAlignment(), JRXmlConstants.getVerticalAlignMap(), JRAlignment.VERTICAL_ALIGN_TOP);
		writer.addAttribute("rotation", textElement.getRotation(), JRXmlConstants.getRotationMap(), JRTextElement.ROTATION_NONE);
		writer.addAttribute("lineSpacing", textElement.getLineSpacing(), JRXmlConstants.getLineSpacingMap(), JRTextElement.LINE_SPACING_SINGLE);
		writer.addAttribute("isStyledText", textElement.isStyledText(), false);

		writeFont(textElement.getFont());
		
		writer.closeElement();
	}


	/**
	 *
	 */
	private void writeFont(JRFont font) throws IOException
	{
		if (font != null)
		{
			writer.startElement("font");
			if (font.getReportFont() != null)
			{
				JRFont baseFont = 
					(JRFont)fontsMap.get(
						font.getReportFont().getName()
						);
				if(baseFont != null)
				{
					writer.addAttribute("reportFont", font.getReportFont().getName());
				}
				else
				{
					throw 
						new JRRuntimeException(
							"Referenced report font not found : " 
							+ font.getReportFont().getName()
							);
				}
			}
		
			writer.addAttribute("fontName", font.getOwnFontName());
			writer.addAttribute("size", font.getOwnSize());
			writer.addAttribute("isBold", font.isOwnBold());
			writer.addAttribute("isItalic", font.isOwnItalic());
			writer.addAttribute("isUnderline", font.isOwnUnderline());
			writer.addAttribute("isStrikeThrough", font.isOwnStrikeThrough());
			writer.addAttribute("pdfFontName", font.getOwnPdfFontName());
			writer.addAttribute("pdfEncoding", font.getOwnPdfEncoding());
			writer.addAttribute("isPdfEmbedded", font.isOwnPdfEmbedded());
			writer.closeElement(true);
		}
	}


	/**
	 *
	 */
	public void writeTextField(JRTextField textField) throws IOException
	{
		writer.startElement("textField");
		writer.addAttribute("isStretchWithOverflow", textField.isStretchWithOverflow(), false);
		writer.addAttribute("evaluationTime", textField.getEvaluationTime(), JRXmlConstants.getEvaluationTimeMap(), JRExpression.EVALUATION_TIME_NOW);

		if (textField.getEvaluationGroup() != null)
		{
			writer.addAttribute("evaluationGroup", textField.getEvaluationGroup().getName());
		}

		writer.addAttribute("pattern", textField.getPattern());
		writer.addAttribute("isBlankWhenNull", textField.isBlankWhenNull(), false);
		
		writer.addAttribute("hyperlinkType", textField.getHyperlinkType(), JRXmlConstants.getHyperlinkTypeMap(), JRHyperlink.HYPERLINK_TYPE_NONE);
		writer.addAttribute("hyperlinkTarget", textField.getHyperlinkTarget(), JRXmlConstants.getHyperlinkTargetMap(), JRHyperlink.HYPERLINK_TARGET_SELF);

		writeReportElement(textField);
		writeBox(textField.getBox());
		writeTextElement(textField);

		writer.writeExpression("textFieldExpression", textField.getExpression(), true);
		
		writer.writeExpression("anchorNameExpression", textField.getAnchorNameExpression(), false);
		writer.writeExpression("hyperlinkReferenceExpression", textField.getHyperlinkReferenceExpression(), false);
		writer.writeExpression("hyperlinkAnchorExpression", textField.getHyperlinkAnchorExpression(), false);
		writer.writeExpression("hyperlinkPageExpression", textField.getHyperlinkPageExpression(), false);
		
		writer.closeElement();
	}


	/**
	 *
	 */
	public void writeSubreport(JRSubreport subreport) throws IOException
	{
		writer.startElement("subreport");
		writer.addAttribute("isUsingCache", subreport.isUsingCache(), true);

		writeReportElement(subreport);

		writer.writeExpression("parametersMapExpression", subreport.getParametersMapExpression(), false);

		/*   */
		JRSubreportParameter[] parameters = subreport.getParameters();
		if (parameters != null && parameters.length > 0)
		{
			for(int i = 0; i < parameters.length; i++)
			{
				writeSubreportParameter(parameters[i]);
			}
		}

		writer.writeExpression("connectionExpression", subreport.getConnectionExpression(), false);
		writer.writeExpression("dataSourceExpression", subreport.getDataSourceExpression(), false);

		JRSubreportReturnValue[] returnValues = subreport.getReturnValues();
		if (returnValues != null && returnValues.length > 0)
		{
			for(int i = 0; i < returnValues.length; i++)
			{
				writeSubreportReturnValue(returnValues[i]);
			}
		}

		writer.writeExpression("subreportExpression", subreport.getExpression(), true);
		
		writer.closeElement();
	}


	/**
	 *
	 */
	private void writeSubreportParameter(JRSubreportParameter subreportParameter) throws IOException
	{
		writer.startElement("subreportParameter");
		writer.addAttribute("name", subreportParameter.getName());

		writer.writeExpression("subreportParameterExpression", subreportParameter.getExpression(), false);
		
		writer.closeElement();
	}


	/**
	 *
	 */
	private void writeChart(JRChart chart) throws IOException
	{
		writer.startElement("chart");
		writer.addAttribute("isShowLegend", chart.isShowLegend(), true);
		writer.addAttribute("evaluationTime", chart.getEvaluationTime(), JRXmlConstants.getEvaluationTimeMap(), JRExpression.EVALUATION_TIME_NOW);

		if (chart.getEvaluationTime() == JRExpression.EVALUATION_TIME_GROUP)
		{
			writer.addAttribute("evaluationGroup", chart.getEvaluationGroup().getName());
		}

		writeReportElement(chart);
		writeBox(chart.getBox());

		// write title
		if (chart.getTitleExpression() != null) {
			writer.startElement("chartTitle");
			writer.addAttribute("position", chart.getTitlePosition(), JRXmlConstants.getChartTitlePositionMap(), JRChart.TITLE_POSITION_TOP);
			writer.addAttribute("color", chart.getTitleColor());
			writeFont(chart.getTitleFont());
			writer.writeExpression("titleExpression", chart.getTitleExpression(), false);
			writer.closeElement();
		}

		// write subtitle
		if (chart.getSubtitleExpression() != null) {
			writer.startElement("chartSubitle");
			writer.addAttribute("color", chart.getSubtitleColor());
			writeFont(chart.getSubtitleFont());
			writer.writeExpression("subtitleExpression", chart.getSubtitleExpression(), false);
			writer.closeElement();
		}

		writer.closeElement();
	}


	/**
	 *
	 */
	private void writeChartDataset(JRChartDataset dataset) throws IOException
	{
		writer.startElement("dataset");
		writer.addAttribute("resetType", dataset.getResetType(), JRXmlConstants.getResetTypeMap(), JRVariable.RESET_TYPE_REPORT);

		if (dataset.getResetType() == JRVariable.RESET_TYPE_GROUP)
		{
			writer.addAttribute("resetGroup", dataset.getResetGroup().getName());
		}
		writer.addAttribute("incrementType", dataset.getIncrementType(), JRXmlConstants.getResetTypeMap(), JRVariable.RESET_TYPE_NONE);

		if (dataset.getIncrementType() == JRVariable.RESET_TYPE_GROUP)
		{
			writer.addAttribute("incrementGroup", dataset.getIncrementGroup().getName());
		}

		JRDatasetRun datasetRun = dataset.getDatasetRun();
		if (datasetRun != null)
		{
			writeDatasetRun(datasetRun);
		}

		writer.closeElement();		
	}


	/**
	 *
	 */
	private void writeCategoryDataSet(JRCategoryDataset dataset) throws IOException
	{
		writer.startElement("categoryDataset");

		writeChartDataset(dataset);

		/*   */
		JRCategorySeries[] categorySeries = dataset.getSeries();
		if (categorySeries != null && categorySeries.length > 0)
		{
			for(int i = 0; i < categorySeries.length; i++)
			{
				writeCategorySeries(categorySeries[i]);
			}
		}

		writer.closeElement();
	}
	
	
	private void writeTimeSeriesDataset(JRTimeSeriesDataset dataset) throws IOException
	{
		writer.startElement("timeSeriesDataset");
		writer.addAttribute("timePeriod", JRXmlConstants.getTimePeriodName(dataset.getTimePeriod()));
		
		writeChartDataset( dataset );
		
		JRTimeSeries[] timeSeries = dataset.getSeries();
		if( timeSeries != null && timeSeries.length > 0 )
		{
			for( int i = 0; i < timeSeries.length; i++ )
		{
				writeTimeSeries( timeSeries[i] );
			}
		}

		writer.closeElement();
	}
	
	
	private void writeTimePeriodDataset(JRTimePeriodDataset dataset) throws IOException
	{
		writer.startElement("timePeriodDataset");
		writeChartDataset(dataset);
		
		JRTimePeriodSeries[] timePeriodSeries = dataset.getSeries();
		if( timePeriodSeries != null && timePeriodSeries.length > 0 )
		{
			for( int i = 0; i < timePeriodSeries.length; i++ )
			{
				writeTimePeriodSeries(timePeriodSeries[i]);
			}
		}
		writer.closeElement();
	}


	/**
	 *
	 */
	private void writeCategorySeries(JRCategorySeries categorySeries) throws IOException
	{
		writer.startElement("categorySeries");

		writer.writeExpression("seriesExpression", categorySeries.getSeriesExpression(), false);
		writer.writeExpression("categoryExpression", categorySeries.getCategoryExpression(), false);
		writer.writeExpression("valueExpression", categorySeries.getValueExpression(), false);
		writer.writeExpression("labelExpression", categorySeries.getLabelExpression(), false);

		writer.closeElement();
	}

	/**
	 * 
	 */
	private void writeXyzDataset(JRXyzDataset dataset) throws IOException
	{
		writer.startElement("xyzDataset");
		writeChartDataset(dataset);
		
		JRXyzSeries[] series = dataset.getSeries();
		if( series != null && series.length > 0 )
		{
			for( int i = 0; i < series.length; i++ )
			{
				writeXyzSeries(series[i]); 
			}
		}

		writer.closeElement();
	}
	
	
	/**
	 * 
	 */
	private void writeXyzSeries(JRXyzSeries series) throws IOException
	{
		writer.startElement("xyzSeries");
		
		writer.writeExpression("seriesExpression", series.getSeriesExpression(), false);
		writer.writeExpression("xValueExpression", series.getXValueExpression(), false);
		writer.writeExpression("yValueExpression", series.getYValueExpression(), false);
		writer.writeExpression("zValueExpression", series.getZValueExpression(), false);

		writer.closeElement();
	}

	/**
	 *
	 */
	private void writeXySeries(JRXySeries xySeries) throws IOException
	{
		writer.startElement("xySeries");

		writer.writeExpression("seriesExpression", xySeries.getSeriesExpression(), false);
		writer.writeExpression("xValueExpression", xySeries.getXValueExpression(), false);
		writer.writeExpression("yValueExpression", xySeries.getYValueExpression(), false);
		writer.writeExpression("labelExpression", xySeries.getLabelExpression(), false);

		writer.closeElement();
	}


	/**
	 *
	 */
	private void writeXyDataset(JRXyDataset dataset) throws IOException
	{
		writer.startElement("xyDataset");

		writeChartDataset(dataset);

		/*   */
		JRXySeries[] xySeries = dataset.getSeries();
		if (xySeries != null && xySeries.length > 0)
		{
			for(int i = 0; i < xySeries.length; i++)
			{
				writeXySeries(xySeries[i]);
			}
		}

		writer.closeElement();
	}


	/**
	 *
	 */
	private void writeTimeSeries(JRTimeSeries timeSeries) throws IOException
	{
		writer.startElement("timeSeries");

		writer.writeExpression("seriesExpression", timeSeries.getSeriesExpression(), false);
		writer.writeExpression("timePeriodExpression", timeSeries.getTimePeriodExpression(), false);
		writer.writeExpression("valueExpression", timeSeries.getValueExpression(), false);
		writer.writeExpression("labelExpression", timeSeries.getLabelExpression(), false);
		
		writer.closeElement();
	}
	
	
	private void writeTimePeriodSeries(JRTimePeriodSeries timePeriodSeries) throws IOException
	{
		writer.startElement("timePeriodSeries");
		
		writer.writeExpression("seriesExpression", timePeriodSeries.getSeriesExpression(), false);
		writer.writeExpression("startDateExpression", timePeriodSeries.getStartDateExpression(), false);
		writer.writeExpression("endDateExpression", timePeriodSeries.getEndDateExpression(), false);
		writer.writeExpression("valueExpression", timePeriodSeries.getValueExpression(), false);
		writer.writeExpression("labelExpression", timePeriodSeries.getLabelExpression(), false);
		
		writer.closeElement();
	}

	/**
	 *
	 */
	private void writePlot(JRChartPlot plot) throws IOException
	{
		writer.startElement("plot");
		writer.addAttribute("backcolor", plot.getBackcolor());
		writer.addAttribute("orientation", plot.getOrientation(), JRXmlConstants.getPlotOrientationMap(), PlotOrientation.VERTICAL);
		writer.addAttribute("backgroundAlpha", plot.getBackgroundAlpha(), 1.0f);
		writer.addAttribute("foregroundAlpha", plot.getForegroundAlpha(), 1.0f);
		
		writer.closeElement();
	}


	/**
	 *
	 */
	public void writePieChart(JRChart chart) throws IOException
	{
		writer.startElement("pieChart");
		writeChart(chart);

		// write dataset
		JRPieDataset dataset = (JRPieDataset) chart.getDataset();
		writer.startElement("pieDataset");

		writeChartDataset(dataset);

		writer.writeExpression("keyExpression", dataset.getKeyExpression(), false);
		writer.writeExpression("valueExpression", dataset.getValueExpression(), false);
		writer.writeExpression("labelExpression", dataset.getLabelExpression(), false);
		writer.closeElement();

		// write plot
		writer.startElement("piePlot");
		writePlot(chart.getPlot());
		writer.closeElement();

		writer.closeElement();
	}


	/**
	 *
	 */
	public void writePie3DChart(JRChart chart) throws IOException
	{
		writer.startElement("pie3DChart");
		writeChart(chart);

		// write dataset
		JRPieDataset dataset = (JRPieDataset) chart.getDataset();
		writer.startElement("pieDataset");

		writeChartDataset(dataset);

		writer.writeExpression("keyExpression", dataset.getKeyExpression(), false);
		writer.writeExpression("valueExpression", dataset.getValueExpression(), false);
		writer.writeExpression("labelExpression", dataset.getLabelExpression(), false);

		writer.closeElement();

		// write plot
		JRPie3DPlot plot = (JRPie3DPlot) chart.getPlot();
		writer.startElement("pie3DPlot");
		writer.addAttribute("depthFactor", plot.getDepthFactor(), JRPie3DPlot.DEPTH_FACTOR_DEFAULT);
		writePlot(chart.getPlot());
		writer.closeElement();

		writer.closeElement();
	}


	/**
	 *
	 */
	private void writeBarPlot(JRBarPlot plot) throws IOException
	{
		writer.startElement("barPlot");
		writer.addAttribute("isShowTickLabels", plot.isShowTickLabels(), true);
		writer.addAttribute("isShowTickMarks", plot.isShowTickMarks(), true);
		writePlot(plot);

		writer.writeExpression("categoryAxisLabelExpression", plot.getCategoryAxisLabelExpression(), false);
		writer.writeExpression("valueAxisLabelExpression", plot.getValueAxisLabelExpression(), false);

		writer.closeElement();
	}
	
	
	/**
	 * 
	 */
	private void writeBubblePlot(JRBubblePlot plot) throws IOException
	{
		writer.startElement("bubblePlot");
		writer.addAttribute("scaleType", plot.getScaleType(), JRXmlConstants.getScaleTypeMap());
		writePlot(plot);
		writer.writeExpression("xAxisLabelExpression", plot.getXAxisLabelExpression(), false);
		writer.writeExpression("yAxisLabelExpression", plot.getYAxisLabelExpression(), false);

		writer.closeElement();
	}


	/**
	 *
	 */
	private void writeLinePlot(JRLinePlot plot) throws IOException
	{
		writer.startElement("linePlot");
		writer.addAttribute("isShowLines", plot.isShowLines(), true);
		writer.addAttribute("isShowShapes", plot.isShowShapes(), true);

		writePlot(plot);

		writer.writeExpression("categoryAxisLabelExpression", plot.getCategoryAxisLabelExpression(), false);
		writer.writeExpression("valueAxisLabelExpression", plot.getValueAxisLabelExpression(), false);

		writer.closeElement();
	}
	
	
	private void writeTimeSeriesPlot(JRTimeSeriesPlot plot) throws IOException
	{
		writer.startElement("timeSeriesPlot");
		writer.addAttribute("isShowLines", plot.isShowLines(), true);
		writer.addAttribute("isShowShapes", plot.isShowShapes(), true);
		
		writePlot( plot );
		
		writer.writeExpression("timeAxisLabelExpression", plot.getTimeAxisLabelExpression(), false);
		writer.writeExpression("valueAxisLabelExpression", plot.getValueAxisLabelExpression(), false);

		writer.closeElement();
	}


	/**
	 *
	 */
	public void writeBar3DPlot(JRBar3DPlot plot) throws IOException
	{
		writer.startElement("bar3DPlot");
		writer.addAttribute("xOffset", plot.getXOffset(), BarRenderer3D.DEFAULT_X_OFFSET);
		writer.addAttribute("yOffset", plot.getYOffset(), BarRenderer3D.DEFAULT_Y_OFFSET);

		writePlot(plot);

		writer.writeExpression("categoryAxisLabelExpression", plot.getCategoryAxisLabelExpression(), false);
		writer.writeExpression("valueAxisLabelExpression", plot.getValueAxisLabelExpression(), false);

		writer.closeElement();
	}


	/**
	 *
	 */
	public void writeBarChart(JRChart chart) throws IOException
	{
		writer.startElement("barChart");

		writeChart(chart);
		writeCategoryDataSet((JRCategoryDataset) chart.getDataset());
		writeBarPlot((JRBarPlot) chart.getPlot());

		writer.closeElement();
	}


	/**
	 *
	 */
	public void writeBar3DChart(JRChart chart) throws IOException
	{
		writer.startElement("bar3DChart");

		writeChart(chart);
		writeCategoryDataSet((JRCategoryDataset) chart.getDataset());
		writeBar3DPlot((JRBar3DPlot) chart.getPlot());

		writer.closeElement();
	}
	
	
	/**
	 * 
	 */
	public void writeBubbleChart(JRChart chart) throws IOException
	{
		writer.startElement("bubbleChart");
		writeChart(chart);
		writeXyzDataset((JRXyzDataset) chart.getDataset());
		writeBubblePlot((JRBubblePlot) chart.getPlot());
		writer.closeElement();
	}


	/**
	 *
	 */
	public void writeStackedBarChart(JRChart chart) throws IOException
	{
		writer.startElement("stackedBarChart");

		writeChart(chart);
		writeCategoryDataSet((JRCategoryDataset) chart.getDataset());
		writeBarPlot((JRBarPlot) chart.getPlot());

		writer.closeElement();
	}


	/**
	 *
	 */
	public void writeStackedBar3DChart(JRChart chart) throws IOException
	{
		writer.startElement("stackedBar3DChart");

		writeChart(chart);
		writeCategoryDataSet((JRCategoryDataset) chart.getDataset());
		writeBar3DPlot((JRBar3DPlot) chart.getPlot());
		writer.closeElement();
	}


	/**
	 *
	 */
	public void writeLineChart(JRChart chart) throws IOException
	{
		writer.startElement("lineChart");

		writeChart(chart);
		writeCategoryDataSet((JRCategoryDataset) chart.getDataset());
		writeLinePlot((JRLinePlot) chart.getPlot());
		writer.closeElement();
	}
	
	
	public void writeTimeSeriesChart(JRChart chart) throws IOException
	{
		writer.startElement("timeSeriesChart");
		writeChart(chart);
		writeTimeSeriesDataset((JRTimeSeriesDataset)chart.getDataset());
		writeTimeSeriesPlot((JRTimeSeriesPlot)chart.getPlot());
		writer.closeElement();
	}

	public void writeHighLowDataset(JRHighLowDataset dataset) throws IOException
	{
		writer.startElement("highLowDataset");

		writeChartDataset(dataset);

		writer.writeExpression("seriesExpression", dataset.getSeriesExpression(), false);
		writer.writeExpression("dateExpression", dataset.getDateExpression(), false);
		writer.writeExpression("highExpression", dataset.getHighExpression(), false);
		writer.writeExpression("lowExpression", dataset.getLowExpression(), false);
		writer.writeExpression("openExpression", dataset.getOpenExpression(), false);
		writer.writeExpression("closeExpression", dataset.getCloseExpression(), false);
		writer.writeExpression("volumeExpression", dataset.getVolumeExpression(), false);

		writer.closeElement();
	}


	public void writeHighLowChart(JRChart chart) throws IOException
	{
		writer.startElement("highLowChart");

		writeChart(chart);
		writeHighLowDataset((JRHighLowDataset) chart.getDataset());

		JRHighLowPlot plot = (JRHighLowPlot) chart.getPlot();
		writer.startElement("highLowPlot");
		writer.addAttribute("isShowOpenTicks", plot.isShowOpenTicks(), true);
		writer.addAttribute("isShowCloseTicks", plot.isShowCloseTicks(), true);

		writePlot(plot);

		writer.writeExpression("timeAxisLabelExpression", plot.getTimeAxisLabelExpression(), false);
		writer.writeExpression("valueAxisLabelExpression", plot.getValueAxisLabelExpression(), false);

		writer.closeElement();
		writer.closeElement();
	}


	public void writeCandlestickChart(JRChart chart) throws IOException
	{
		writer.startElement("candlestickChart");

		writeChart(chart);
		writeHighLowDataset((JRHighLowDataset) chart.getDataset());

		JRCandlestickPlot plot = (JRCandlestickPlot) chart.getPlot();
		writer.startElement("candlestickPlot");
		writer.addAttribute("isShowVolume", plot.isShowVolume(), true);

		writePlot(plot);

		writer.writeExpression("timeAxisLabelExpression", plot.getTimeAxisLabelExpression(), false);
		writer.writeExpression("valueAxisLabelExpression", plot.getValueAxisLabelExpression(), false);

		writer.closeElement();
		writer.closeElement();
	}

	/**
	 *
	 */
	private void writeAreaPlot(JRAreaPlot plot) throws IOException
	{
		writer.startElement("areaPlot");
		writePlot(plot);

		writer.writeExpression("categoryAxisLabelExpression", plot.getCategoryAxisLabelExpression(), false);
		writer.writeExpression("valueAxisLabelExpression", plot.getValueAxisLabelExpression(), false);

		writer.closeElement();
	}


	/**
	 *
	 */
	public void writeAreaChart(JRChart chart) throws IOException
	{
		writer.startElement("areaChart");

		writeChart(chart);
		writeCategoryDataSet((JRCategoryDataset) chart.getDataset());
		writeAreaPlot((JRAreaPlot) chart.getPlot());

		writer.closeElement();
	}


	/**
	 *
	 */
	private void writeScatterPlot(JRScatterPlot plot) throws IOException
	{
		writer.startElement("scatterPlot");
		writer.addAttribute("isShowLines", plot.isShowLines(), true);
		writer.addAttribute("isShowShapes", plot.isShowShapes(), true);

		writePlot(plot);

		writer.writeExpression("xAxisLabelExpression", plot.getXAxisLabelExpression(), false);
		writer.writeExpression("yAxisLabelExpression", plot.getYAxisLabelExpression(), false);

		writer.closeElement();
	}


	/**
	 *
	 */
	public void writeScatterChart(JRChart chart) throws IOException
	{
		writer.startElement("scatterChart");

		writeChart(chart);
		writeXyDataset((JRXyDataset) chart.getDataset());
		writeScatterPlot((JRScatterPlot) chart.getPlot());

		writer.closeElement();
	}


	/**
	 *
	 */
	public void writeXyAreaChart(JRChart chart) throws IOException
	{
		writer.startElement("xyAreaChart");

		writeChart(chart);
		writeXyDataset((JRXyDataset) chart.getDataset());
		writeAreaPlot((JRAreaPlot) chart.getPlot());

		writer.closeElement();
	}


	/**
	 *
	 */
	public void writeXyBarChart(JRChart chart) throws IOException
	{
		writer.startElement("xyBarChart");

		writeChart(chart);
		JRChartDataset dataset = chart.getDataset();
		
		if( dataset.getDatasetType() == JRChartDataset.TIMESERIES_DATASET ){
			writeTimeSeriesDataset( (JRTimeSeriesDataset)dataset );
		}
		else if( dataset.getDatasetType() == JRChartDataset.TIMEPERIOD_DATASET ){
			writeTimePeriodDataset( (JRTimePeriodDataset)dataset );
		}
		else if( dataset.getDatasetType() == JRChartDataset.XY_DATASET ){
			writeXyDataset( (JRXyDataset)dataset );
		}
		
		writeBarPlot((JRBarPlot) chart.getPlot());

		writer.closeElement();
	}


	/**
	 *
	 */
	public void writeXyLineChart(JRChart chart) throws IOException
	{
		writer.startElement("xyLineChart");

		writeChart(chart);
		writeXyDataset((JRXyDataset) chart.getDataset());
		writeLinePlot((JRLinePlot) chart.getPlot());

		writer.closeElement();
	}


	public void writeChartTag(JRChart chart) throws IOException
	{
		switch(chart.getChartType()) {
			case JRChart.CHART_TYPE_AREA:
				writeAreaChart(chart);
				break;
			case JRChart.CHART_TYPE_BAR:
				writeBarChart(chart);
				break;
			case JRChart.CHART_TYPE_BAR3D:
				writeBar3DChart(chart);
				break;
			case JRChart.CHART_TYPE_BUBBLE:
				writeBubbleChart(chart);
				break;
			case JRChart.CHART_TYPE_CANDLESTICK:
				writeCandlestickChart(chart);
				break;
			case JRChart.CHART_TYPE_HIGHLOW:
				writeHighLowChart(chart);
				break;
			case JRChart.CHART_TYPE_LINE:
				writeLineChart(chart);
				break;
			case JRChart.CHART_TYPE_PIE:
				writePieChart(chart);
				break;
			case JRChart.CHART_TYPE_PIE3D:
				writePie3DChart(chart);
				break;
			case JRChart.CHART_TYPE_SCATTER:
				writeScatterChart(chart);
				break;
			case JRChart.CHART_TYPE_STACKEDBAR:
				writeStackedBarChart(chart);
				break;
			case JRChart.CHART_TYPE_STACKEDBAR3D:
				writeStackedBar3DChart(chart);
				break;
			case JRChart.CHART_TYPE_TIMESERIES:
				writeTimeSeriesChart( chart );
				break;
			case JRChart.CHART_TYPE_XYAREA:
				writeXyAreaChart(chart);
				break;
			case JRChart.CHART_TYPE_XYBAR:
				writeXyBarChart(chart);
				break;
			case JRChart.CHART_TYPE_XYLINE:
				writeXyLineChart(chart);
				break;
			default:
				throw new JRRuntimeException("Chart type not supported.");
		}
	}


	private void writeSubreportReturnValue(JRSubreportReturnValue returnValue) throws IOException
	{
		writer.startElement("returnValue");
		writer.addAttribute("subreportVariable", returnValue.getSubreportVariable());
		writer.addAttribute("toVariable", returnValue.getToVariable());
		writer.addAttribute("calculation", returnValue.getCalculation(), JRXmlConstants.getCalculationMap(), JRVariable.CALCULATION_NOTHING);
		writer.addAttribute("incrementerFactoryClass", returnValue.getIncrementerFactoryClassName());
		writer.closeElement();
	}


	public void writeCrosstab(JRCrosstab crosstab) throws IOException
	{
		writer.startElement("crosstab");
		writer.addAttribute(JRCrosstabFactory.ATTRIBUTE_name, crosstab.getName());
		writer.addAttribute(JRCrosstabFactory.ATTRIBUTE_isDataPreSorted, crosstab.isDataPreSorted(), false);
		writer.addAttribute(JRCrosstabFactory.ATTRIBUTE_isRepeatColumnHeaders, crosstab.isRepeatColumnHeaders(), true);
		writer.addAttribute(JRCrosstabFactory.ATTRIBUTE_isRepeatRowHeaders, crosstab.isRepeatRowHeaders(), true);
		writer.addAttribute(JRCrosstabFactory.ATTRIBUTE_columnBreakOffset, crosstab.getColumnBreakOffset(), JRCrosstab.DEFAULT_COLUMN_BREAK_OFFSET);
		
		writeReportElement(crosstab);
		
		JRCrosstabParameter[] parameters = crosstab.getParameters();
		if (parameters != null)
		{
			for (int i = 0; i < parameters.length; i++)
			{
				writeCrosstabParameter(parameters[i]);
			}
		}
		
		writer.writeExpression("parametersMapExpression", crosstab.getParametersMapExpression(), false);
		
		writeChartDataset(crosstab.getDataset());
		
		JRCrosstabRowGroup[] rowGroups = crosstab.getRowGroups();
		for (int i = 0; i < rowGroups.length; i++)
		{
			writeCrosstabRowGroup(rowGroups[i]);
		}
		
		JRCrosstabColumnGroup[] columnGroups = crosstab.getColumnGroups();
		for (int i = 0; i < columnGroups.length; i++)
		{
			writeCrosstabColumnGroup(columnGroups[i]);
		}
		
		JRCrosstabMeasure[] measures = crosstab.getMeasures();
		for (int i = 0; i < measures.length; i++)
		{
			writeCrosstabMeasure(measures[i]);
		}
		
		if (crosstab instanceof JRDesignCrosstab)
		{
			List cellsList = ((JRDesignCrosstab) crosstab).getCellsList();
			for (Iterator it = cellsList.iterator(); it.hasNext();)
			{
				JRCrosstabCell cell = (JRCrosstabCell) it.next();
				writeCrosstabCell(cell);
			}
		}
		else
		{
			JRCrosstabCell[][] cells = crosstab.getCells();
			Set cellsSet = new HashSet();
			for (int i = cells.length - 1; i >= 0 ; --i)
			{
				for (int j = cells[i].length - 1; j >= 0 ; --j)
				{
					JRCrosstabCell cell = cells[i][j];
					if (cell != null && cellsSet.add(cell))
					{
						writeCrosstabCell(cell);
					}
				}
			}
		}
		
		writer.closeElement();
	}
	
	
	protected void writeCrosstabRowGroup(JRCrosstabRowGroup group) throws IOException
	{
		writer.startElement("rowGroup");
		writer.addAttribute(JRCrosstabGroupFactory.ATTRIBUTE_name, group.getName());
		writer.addAttribute(JRCrosstabRowGroupFactory.ATTRIBUTE_width, group.getWidth());
		writer.addAttribute(JRCrosstabGroupFactory.ATTRIBUTE_totalPosition, group.getTotalPosition(), JRXmlConstants.getCrosstabTotalPositionMap(), Bucket.TOTAL_POSITION_NONE);
		writer.addAttribute(JRCrosstabRowGroupFactory.ATTRIBUTE_headerPosition, group.getPosition(), JRXmlConstants.getCrosstabRowPositionMap(), JRCellContents.POSITION_Y_TOP);

		writeBucket(group.getBucket());
		
		JRCellContents header = group.getHeader();
		if (header != null)
		{
			writer.startElement("crosstabRowHeader");
			writeCellContents(header);
			writer.closeElement();
		}
		
		JRCellContents totalHeader = group.getTotalHeader();
		if (totalHeader != null)
		{
			writer.startElement("crosstabTotalRowHeader");
			writeCellContents(totalHeader);
			writer.closeElement();
		}
		
		writer.closeElement();
		
	}
	
	
	protected void writeCrosstabColumnGroup(JRCrosstabColumnGroup group) throws IOException
	{
		writer.startElement("columnGroup");
		writer.addAttribute(JRCrosstabGroupFactory.ATTRIBUTE_name, group.getName());
		writer.addAttribute(JRCrosstabColumnGroupFactory.ATTRIBUTE_height, group.getHeight());
		writer.addAttribute(JRCrosstabGroupFactory.ATTRIBUTE_totalPosition, group.getTotalPosition(), JRXmlConstants.getCrosstabTotalPositionMap(), Bucket.TOTAL_POSITION_NONE);
		writer.addAttribute(JRCrosstabColumnGroupFactory.ATTRIBUTE_headerPosition, group.getPosition(), JRXmlConstants.getCrosstabColumnPositionMap(), JRCellContents.POSITION_X_LEFT);

		writeBucket(group.getBucket());
		
		JRCellContents header = group.getHeader();
		if (header != null)
		{
			writer.startElement("crosstabColumnHeader");
			writeCellContents(header);
			writer.closeElement();
		}
		
		JRCellContents totalHeader = group.getTotalHeader();
		if (totalHeader != null)
		{
			writer.startElement("crosstabTotalColumnHeader");
			writeCellContents(totalHeader);
			writer.closeElement();
		}
		
		writer.closeElement();
		
	}


	protected void writeBucket(JRCrosstabBucket bucket) throws IOException
	{
		writer.startElement("bucket");
		writer.addAttribute(JRCrosstabBucketFactory.ATTRIBUTE_order, bucket.getOrder(), JRXmlConstants.getCrosstabBucketOrderMap(), Bucket.ORDER_ASCENDING);
		writer.writeExpression("bucketExpression", bucket.getExpression(), true);
		writer.writeExpression("comparatorExpression", bucket.getComparatorExpression(), false);		
		writer.closeElement();
	}


	protected void writeCrosstabMeasure(JRCrosstabMeasure measure) throws IOException
	{
		writer.startElement("measure");
		writer.addAttribute(JRCrosstabMeasureFactory.ATTRIBUTE_name, measure.getName());
		writer.addAttribute(JRCrosstabMeasureFactory.ATTRIBUTE_class, measure.getValueClassName());
		writer.addAttribute(JRCrosstabMeasureFactory.ATTRIBUTE_calculation, measure.getCalculation(), JRXmlConstants.getCalculationMap(), JRVariable.CALCULATION_NOTHING);
		writer.addAttribute(JRCrosstabMeasureFactory.ATTRIBUTE_percentageOf, measure.getPercentageOfType(), JRXmlConstants.getCrosstabPercentageMap(), JRCrosstabMeasure.PERCENTAGE_TYPE_NONE);
		writer.addAttribute(JRCrosstabMeasureFactory.ATTRIBUTE_percentageCalculatorClass, measure.getPercentageCalculatorClassName());
		writer.writeExpression("measureExpression", measure.getValueExpression(), false);
		writer.closeElement();
	}


	protected void writeCrosstabCell(JRCrosstabCell cell) throws IOException
	{
		writer.startElement("crosstabCell");
		writer.addAttribute(JRCrosstabCellFactory.ATTRIBUTE_width, cell.getWidth());
		writer.addAttribute(JRCrosstabCellFactory.ATTRIBUTE_height, cell.getHeight());
		writer.addAttribute(JRCrosstabCellFactory.ATTRIBUTE_rowTotalGroup, cell.getRowTotalGroup());
		writer.addAttribute(JRCrosstabCellFactory.ATTRIBUTE_columnTotalGroup, cell.getColumnTotalGroup());
		
		writeCellContents(cell.getContents());
		
		writer.closeElement();
	}


	protected void writeCellContents(JRCellContents contents) throws IOException
	{
		if (contents != null)
		{
			writer.startElement("cellContents");
			writer.addAttribute(JRCellContentsFactory.ATTRIBUTE_backcolor, contents.getBackcolor());
			
			writeBox(contents.getBox());
			
			List children = contents.getChildren();
			if (children != null)
			{
				for (Iterator it = children.iterator(); it.hasNext();)
				{
					JRChild element = (JRChild) it.next();
					element.writeXml(this);
				}
			}
			
			writer.closeElement();
		}
	}


	protected void writeCrosstabParameter(JRCrosstabParameter parameter) throws IOException
	{
		writer.startElement("crosstabParameter");
		writer.addAttribute("name", parameter.getName());
		writer.addAttribute("class", parameter.getValueClassName(), "java.lang.String");
		writer.writeExpression("parameterValueExpression", parameter.getExpression(), false);
		writer.closeElement();
	}


	public void writeDataset(JRDataset dataset) throws IOException
	{
		writer.startElement("subDataset");
		writer.addAttribute("name", dataset.getName());
		writer.addAttribute("scriptletClass", dataset.getScriptletClass());
		writer.addAttribute("resourceBundle", dataset.getResourceBundle());
		writer.addAttribute("whenResourceMissingType", dataset.getWhenResourceMissingType(), JRXmlConstants.getWhenResourceMissingTypeMap(), JRReport.WHEN_RESOURCE_MISSING_TYPE_NULL);
		
		writeDatasetContents(dataset);
		
		writer.closeElement();
	}
	
	protected void writeDatasetContents(JRDataset dataset) throws IOException
	{
		/*   */
		JRParameter[] parameters = dataset.getParameters();
		if (parameters != null && parameters.length > 0)
		{
			for(int i = 0; i < parameters.length; i++)
			{
				if (!parameters[i].isSystemDefined())
				{
					writeParameter(parameters[i]);
				}
			}
		}

		/*   */
		if(dataset.getQuery() != null)
		{
			writeQuery(dataset.getQuery());
		}

		/*   */
		JRField[] fields = dataset.getFields();
		if (fields != null && fields.length > 0)
		{
			for(int i = 0; i < fields.length; i++)
			{
				writeField(fields[i]);
			}
		}

		/*   */
		JRVariable[] variables = dataset.getVariables();
		if (variables != null && variables.length > 0)
		{
			for(int i = 0; i < variables.length; i++)
			{
				if (!variables[i].isSystemDefined())
				{
					writeVariable(variables[i]);
				}
			}
		}

		/*   */
		JRGroup[] groups = dataset.getGroups();
		if (groups != null && groups.length > 0)
		{
			for(int i = 0; i < groups.length; i++)
			{
				writeGroup(groups[i]);
			}
		}
	}
	
	
	protected void writeDatasetRun(JRDatasetRun datasetRun) throws IOException
	{
		writer.startElement("datasetRun");
		writer.addAttribute("subDataset", datasetRun.getDatasetName());
		
		writer.writeExpression("parametersMapExpression", datasetRun.getParametersMapExpression(), false);

		/*   */
		JRSubreportParameter[] parameters = datasetRun.getParameters();
		if (parameters != null && parameters.length > 0)
		{
			for(int i = 0; i < parameters.length; i++)
			{
				writeSubreportParameter(parameters[i]);
			}
		}

		writer.writeExpression("connectionExpression", datasetRun.getConnectionExpression(), false);
		writer.writeExpression("dataSourceExpression", datasetRun.getDataSourceExpression(), false);

		writer.closeElement();
	}
}
