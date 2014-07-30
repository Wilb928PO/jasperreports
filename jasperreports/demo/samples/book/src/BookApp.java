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
import java.io.File;
import java.util.HashMap;
import java.util.Map;

import net.sf.jasperreports.engine.JREmptyDataSource;
import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.JasperFillManager;
import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.engine.PrintBook;
import net.sf.jasperreports.engine.SimplePrintBook;
import net.sf.jasperreports.engine.export.HtmlExporter;
import net.sf.jasperreports.engine.export.JRCsvExporter;
import net.sf.jasperreports.engine.export.JRCsvMetadataExporter;
import net.sf.jasperreports.engine.export.JRPdfExporter;
import net.sf.jasperreports.engine.export.JRPrintServiceExporter;
import net.sf.jasperreports.engine.export.JRRtfExporter;
import net.sf.jasperreports.engine.export.JRXlsExporter;
import net.sf.jasperreports.engine.export.JRXlsMetadataExporter;
import net.sf.jasperreports.engine.export.oasis.JROdsExporter;
import net.sf.jasperreports.engine.export.oasis.JROdtExporter;
import net.sf.jasperreports.engine.export.ooxml.JRDocxExporter;
import net.sf.jasperreports.engine.export.ooxml.JRPptxExporter;
import net.sf.jasperreports.engine.export.ooxml.JRXlsxExporter;
import net.sf.jasperreports.engine.util.AbstractSampleApp;
import net.sf.jasperreports.engine.util.JRLoader;
import net.sf.jasperreports.engine.util.JRSaver;
import net.sf.jasperreports.export.SimpleExporterInput;
import net.sf.jasperreports.export.SimpleHtmlExporterOutput;
import net.sf.jasperreports.export.SimpleOutputStreamExporterOutput;
import net.sf.jasperreports.export.SimplePrintServiceExporterConfiguration;
import net.sf.jasperreports.export.SimpleWriterExporterOutput;
import net.sf.jasperreports.export.SimpleXlsMetadataReportConfiguration;
import net.sf.jasperreports.export.SimpleXlsReportConfiguration;
import net.sf.jasperreports.export.SimpleXlsxReportConfiguration;


/**
 * @author Teodor Danciu (teodord@users.sourceforge.net)
 * @version $Id: JasperApp.java 6805 2014-01-10 12:42:19Z teodord $
 */
public class BookApp extends AbstractSampleApp
{


	/**
	 *
	 */
	public static void main(String[] args) 
	{
		main(new BookApp(), args);
	}
	
	
	/**
	 *
	 */
	public void test() throws JRException
	{
		fill();
		pdf();
		xmlEmbed();
		xml();
		html();
		rtf();
		xls();
		csv();
		csvMetadata();
		xlsMetadata();
		odt();
		ods();
		docx();
		xlsx();
		pptx();
	}
	
	
	/**
	 *
	 */
	public void fill() throws JRException
	{
		long start = System.currentTimeMillis();
		JasperPrint jasperPrint1 = JasperFillManager.fillReport(
			"build/reports/Report1.jasper",
			null, 
			new JREmptyDataSource(2)
			);
		JasperPrint jasperPrint2 = JasperFillManager.fillReport(
			"build/reports/Report2.jasper",
			null, 
			new JREmptyDataSource(2)
			);
		JasperPrint jasperPrint3 = JasperFillManager.fillReport(
			"build/reports/Report3.jasper",
			null, 
			new JREmptyDataSource(2)
			);
		
		SimplePrintBook printBook = new SimplePrintBook();
		printBook.setName("PrintBook");
		printBook.addJasperPrint(jasperPrint1);
		printBook.addJasperPrint(jasperPrint2);
		printBook.addJasperPrint(jasperPrint3);
		
		JRSaver.saveObject(printBook, "build/reports/PrintBook.jrpbook");
		
		System.err.println("Filling time : " + (System.currentTimeMillis() - start));
	}
	
	
	/**
	 *
	 */
	public void print() throws JRException
	{
		long start = System.currentTimeMillis();
		File sourceFile = new File("build/reports/PrintBook.jrpbook");
		
		PrintBook printBook = (PrintBook)JRLoader.loadObject(sourceFile);
		
		JRPrintServiceExporter exporter = new JRPrintServiceExporter();

		exporter.setExporterInput(SimpleExporterInput.getInstance(printBook.getJasperPrintList()));
		
		SimplePrintServiceExporterConfiguration configuration = new SimplePrintServiceExporterConfiguration();
		configuration.setDisplayPrintDialog(true);
		exporter.setConfiguration(configuration);
		
		exporter.exportReport();
		
		System.err.println("Printing time : " + (System.currentTimeMillis() - start));
	}
	
	
	/**
	 *
	 */
	public void pdf() throws JRException
	{
		long start = System.currentTimeMillis();
		File sourceFile = new File("build/reports/PrintBook.jrpbook");
		
		PrintBook printBook = (PrintBook)JRLoader.loadObject(sourceFile);
		
		File destFile = new File(sourceFile.getParent(), printBook.getName() + ".pdf");

		JRPdfExporter exporter = new JRPdfExporter();

		exporter.setExporterInput(SimpleExporterInput.getInstance(printBook.getJasperPrintList()));
		exporter.setExporterOutput(new SimpleOutputStreamExporterOutput(destFile));
		
		exporter.exportReport();
		
		System.err.println("PDF creation time : " + (System.currentTimeMillis() - start));
	}
	
	
	/**
	 * FIXMEBOOK
	 */
	public void xml() throws JRException
	{
//		long start = System.currentTimeMillis();
//		JasperExportManager.exportReportToXmlFile("build/reports/FirstJasper.jrprint", false);
//		System.err.println("XML creation time : " + (System.currentTimeMillis() - start));
	}
	
	
	/**
	 * FIXMEBOOK
	 */
	public void xmlEmbed() throws JRException
	{
//		long start = System.currentTimeMillis();
//		JasperExportManager.exportReportToXmlFile("build/reports/FirstJasper.jrprint", true);
//		System.err.println("XML creation time : " + (System.currentTimeMillis() - start));
	}
	
	
	/**
	 *
	 */
	public void html() throws JRException
	{
		long start = System.currentTimeMillis();
		File sourceFile = new File("build/reports/PrintBook.jrpbook");

		PrintBook printBook = (PrintBook)JRLoader.loadObject(sourceFile);

		File destFile = new File(sourceFile.getParent(), printBook.getName() + ".html");
		
		HtmlExporter exporter = new HtmlExporter();
		
		exporter.setExporterInput(SimpleExporterInput.getInstance(printBook.getJasperPrintList()));
		exporter.setExporterOutput(new SimpleHtmlExporterOutput(destFile));
		
		exporter.exportReport();

		System.err.println("HTML creation time : " + (System.currentTimeMillis() - start));
	}
	
	
	/**
	 *
	 */
	public void rtf() throws JRException
	{
		long start = System.currentTimeMillis();
		File sourceFile = new File("build/reports/PrintBook.jrpbook");

		PrintBook printBook = (PrintBook)JRLoader.loadObject(sourceFile);

		File destFile = new File(sourceFile.getParent(), printBook.getName() + ".rtf");
		
		JRRtfExporter exporter = new JRRtfExporter();
		
		exporter.setExporterInput(SimpleExporterInput.getInstance(printBook.getJasperPrintList()));
		exporter.setExporterOutput(new SimpleWriterExporterOutput(destFile));
		
		exporter.exportReport();

		System.err.println("RTF creation time : " + (System.currentTimeMillis() - start));
	}
	
	
	/**
	 *
	 */
	public void xls() throws JRException
	{
		long start = System.currentTimeMillis();
		File sourceFile = new File("build/reports/PrintBook.jrpbook");

		PrintBook printBook = (PrintBook)JRLoader.loadObject(sourceFile);
		
		File destFile = new File(sourceFile.getParent(), printBook.getName() + ".xls");
		
		Map<String, String> dateFormats = new HashMap<String, String>();
		dateFormats.put("EEE, MMM d, yyyy", "ddd, mmm d, yyyy");

		JRXlsExporter exporter = new JRXlsExporter();
		
		exporter.setExporterInput(SimpleExporterInput.getInstance(printBook.getJasperPrintList()));
		exporter.setExporterOutput(new SimpleOutputStreamExporterOutput(destFile));
		SimpleXlsReportConfiguration configuration = new SimpleXlsReportConfiguration();
		configuration.setOnePagePerSheet(true);
		configuration.setDetectCellType(true);
		configuration.setFormatPatternsMap(dateFormats);
		exporter.setConfiguration(configuration);
		
		exporter.exportReport();

		System.err.println("XLS creation time : " + (System.currentTimeMillis() - start));
	}
	
	
	/**
	 *
	 */
	public void xlsMetadata() throws JRException
	{
		long start = System.currentTimeMillis();
		File sourceFile = new File("build/reports/PrintBook.jrpbook");

		PrintBook printBook = (PrintBook)JRLoader.loadObject(sourceFile);

		File destFile = new File(sourceFile.getParent(), printBook.getName() + ".xls.metadata.xls");

		Map<String, String> dateFormats = new HashMap<String, String>();
		dateFormats.put("EEE, MMM d, yyyy", "ddd, mmm d, yyyy");

		JRXlsMetadataExporter exporter = new JRXlsMetadataExporter();

		exporter.setExporterInput(SimpleExporterInput.getInstance(printBook.getJasperPrintList()));
		exporter.setExporterOutput(new SimpleOutputStreamExporterOutput(destFile));
		SimpleXlsMetadataReportConfiguration configuration = new SimpleXlsMetadataReportConfiguration();
		configuration.setOnePagePerSheet(true);
		configuration.setDetectCellType(true);
		configuration.setFormatPatternsMap(dateFormats);
		exporter.setConfiguration(configuration);

		exporter.exportReport();

		System.err.println("XLS creation time : " + (System.currentTimeMillis() - start));
	}
	
	
	/**
	 *
	 */
	public void csv() throws JRException
	{
		long start = System.currentTimeMillis();
		File sourceFile = new File("build/reports/PrintBook.jrpbook");

		PrintBook printBook = (PrintBook)JRLoader.loadObject(sourceFile);

		File destFile = new File(sourceFile.getParent(), printBook.getName() + ".csv");
		
		JRCsvExporter exporter = new JRCsvExporter();
		
		exporter.setExporterInput(SimpleExporterInput.getInstance(printBook.getJasperPrintList()));
		exporter.setExporterOutput(new SimpleWriterExporterOutput(destFile));
		
		exporter.exportReport();

		System.err.println("CSV creation time : " + (System.currentTimeMillis() - start));
	}
	
	
	/**
	 *
	 */
	public void csvMetadata() throws JRException
	{
		long start = System.currentTimeMillis();
		File sourceFile = new File("build/reports/PrintBook.jrpbook");

		PrintBook printBook = (PrintBook)JRLoader.loadObject(sourceFile);

		File destFile = new File(sourceFile.getParent(), printBook.getName() + ".metadata.csv");
		
		JRCsvMetadataExporter exporter = new JRCsvMetadataExporter();
		
		exporter.setExporterInput(SimpleExporterInput.getInstance(printBook.getJasperPrintList()));
		exporter.setExporterOutput(new SimpleWriterExporterOutput(destFile));
		
		exporter.exportReport();

		System.err.println("CSV creation time : " + (System.currentTimeMillis() - start));
	}
	
	
	/**
	 *
	 */
	public void odt() throws JRException
	{
		long start = System.currentTimeMillis();
		File sourceFile = new File("build/reports/PrintBook.jrpbook");

		PrintBook printBook = (PrintBook)JRLoader.loadObject(sourceFile);

		File destFile = new File(sourceFile.getParent(), printBook.getName() + ".odt");

		JROdtExporter exporter = new JROdtExporter();

		exporter.setExporterInput(SimpleExporterInput.getInstance(printBook.getJasperPrintList()));
		exporter.setExporterOutput(new SimpleOutputStreamExporterOutput(destFile));

		exporter.exportReport();

		System.err.println("ODT creation time : " + (System.currentTimeMillis() - start));
	}
	
	
	/**
	 *
	 */
	public void ods() throws JRException
	{
		long start = System.currentTimeMillis();
		File sourceFile = new File("build/reports/PrintBook.jrpbook");

		PrintBook printBook = (PrintBook)JRLoader.loadObject(sourceFile);

		File destFile = new File(sourceFile.getParent(), printBook.getName() + ".ods");

		JROdsExporter exporter = new JROdsExporter();

		exporter.setExporterInput(SimpleExporterInput.getInstance(printBook.getJasperPrintList()));
		exporter.setExporterOutput(new SimpleOutputStreamExporterOutput(destFile));

		exporter.exportReport();

		System.err.println("ODS creation time : " + (System.currentTimeMillis() - start));
	}
	
	
	/**
	 *
	 */
	public void docx() throws JRException
	{
		long start = System.currentTimeMillis();
		File sourceFile = new File("build/reports/PrintBook.jrpbook");

		PrintBook printBook = (PrintBook)JRLoader.loadObject(sourceFile);

		File destFile = new File(sourceFile.getParent(), printBook.getName() + ".docx");

		JRDocxExporter exporter = new JRDocxExporter();

		exporter.setExporterInput(SimpleExporterInput.getInstance(printBook.getJasperPrintList()));
		exporter.setExporterOutput(new SimpleOutputStreamExporterOutput(destFile));

		exporter.exportReport();

		System.err.println("DOCX creation time : " + (System.currentTimeMillis() - start));
	}
	
	
	/**
	 *
	 */
	public void xlsx() throws JRException
	{
		long start = System.currentTimeMillis();
		File sourceFile = new File("build/reports/PrintBook.jrpbook");

		PrintBook printBook = (PrintBook)JRLoader.loadObject(sourceFile);

		File destFile = new File(sourceFile.getParent(), printBook.getName() + ".xlsx");

		Map<String, String> dateFormats = new HashMap<String, String>();
		dateFormats.put("EEE, MMM d, yyyy", "ddd, mmm d, yyyy");

		JRXlsxExporter exporter = new JRXlsxExporter();

		exporter.setExporterInput(SimpleExporterInput.getInstance(printBook.getJasperPrintList()));
		exporter.setExporterOutput(new SimpleOutputStreamExporterOutput(destFile));
		SimpleXlsxReportConfiguration configuration = new SimpleXlsxReportConfiguration();
		configuration.setDetectCellType(true);
		configuration.setFormatPatternsMap(dateFormats);
		exporter.setConfiguration(configuration);

		exporter.exportReport();

		System.err.println("XLSX creation time : " + (System.currentTimeMillis() - start));
	}
	
	
	/**
	 *
	 */
	public void pptx() throws JRException
	{
		long start = System.currentTimeMillis();
		File sourceFile = new File("build/reports/PrintBook.jrpbook");

		PrintBook printBook = (PrintBook)JRLoader.loadObject(sourceFile);

		File destFile = new File(sourceFile.getParent(), printBook.getName() + ".pptx");
		
		JRPptxExporter exporter = new JRPptxExporter();
		
		exporter.setExporterInput(SimpleExporterInput.getInstance(printBook.getJasperPrintList()));
		exporter.setExporterOutput(new SimpleOutputStreamExporterOutput(destFile));

		exporter.exportReport();

		System.err.println("PPTX creation time : " + (System.currentTimeMillis() - start));
	}


}
