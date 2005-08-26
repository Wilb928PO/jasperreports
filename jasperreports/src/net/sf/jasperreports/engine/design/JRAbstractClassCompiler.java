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
package net.sf.jasperreports.engine.design;

import java.io.File;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.JRReport;
import net.sf.jasperreports.engine.JasperReport;
import net.sf.jasperreports.engine.util.JRLoader;
import net.sf.jasperreports.engine.util.JRProperties;
import net.sf.jasperreports.engine.util.JRSaver;


/**
 * @author Teodor Danciu (teodord@users.sourceforge.net)
 * @version $Id$
 */
public abstract class JRAbstractClassCompiler extends JRAbstractJavaCompiler implements JRMultiClassCompiler
{

	/**
	 *
	 */
	public JasperReport compileReport(JasperDesign jasperDesign) throws JRException
	{
		JasperReport jasperReport = null;
		
		if (!JRReport.LANGUAGE_JAVA.equals(jasperDesign.getLanguage()))
		{
			throw 
				new JRException(
					"Language \"" + jasperDesign.getLanguage() 
					+ "\" not supported by this report compiler.\n"
					+ "Expecting \"java\" instead."
					);
		}
		
		Collection brokenRules = JRVerifier.verifyDesign(jasperDesign);
		if (brokenRules != null && brokenRules.size() > 0)
		{
			StringBuffer sbuffer = new StringBuffer();
			sbuffer.append("Report design not valid : ");
			int i = 1;
			for(Iterator it = brokenRules.iterator(); it.hasNext(); i++)
			{
				sbuffer.append("\n\t " + i + ". " + (String)it.next());
			}
			throw new JRException(sbuffer.toString());
		}

		//Report design OK

		String tempDirStr = JRProperties.getProperty(JRProperties.COMPILER_TEMP_DIR);

		File tempDirFile = new File(tempDirStr);
		if (!tempDirFile.exists() || !tempDirFile.isDirectory())
		{
			throw new JRException("Temporary directory not found : " + tempDirStr);
		}

		File[] javaFiles = new File[jasperDesign.getDatasets().length + 1];
		File[] classFiles = new File[jasperDesign.getDatasets().length + 1];
		
		File[] files = generateSource(jasperDesign, jasperDesign.getMainDesignDataset(), tempDirFile);
		javaFiles[0] = files[0];
		classFiles[0] = files[1];
		
		Map datasetMap = jasperDesign.getDatasetMap();

		Map datasetClasses = new HashMap();
		int sourcesCount = 1;
		for (Iterator it = datasetMap.entrySet().iterator(); it.hasNext(); ++sourcesCount)
		{
			Map.Entry  entry = (Map.Entry ) it.next();
			JRDesignDataset dataset = (JRDesignDataset) entry.getValue();
			
			files = generateSource(jasperDesign, dataset, tempDirFile);
			
			javaFiles[sourcesCount] = files[0];
			classFiles[sourcesCount] = files[1];
			
			datasetClasses.put(entry.getKey(), new Integer(sourcesCount));
		}

		
		String classpath = JRProperties.getProperty(JRProperties.COMPILER_CLASSPATH);

		boolean isKeepJavaFile = JRProperties.getBooleanProperty(JRProperties.COMPILER_KEEP_JAVA_FILE); 

		try
		{
			//Compiling expression class source file
			String compileErrors = compileClasses(javaFiles, classpath);
			if (compileErrors != null)
			{
				throw new JRException("Errors were encountered when compiling report expressions class file:\n" + compileErrors);
			}
			
			for (Iterator it = datasetClasses.entrySet().iterator(); it.hasNext();)
			{
				Map.Entry entry = (Map.Entry) it.next();
				int sourceIndex = ((Integer) entry.getValue()).intValue();
				entry.setValue(JRLoader.loadBytes(classFiles[sourceIndex]));
			}

			//Reading class byte codes from compiled class file
			jasperReport = 
				new JasperReport(
					jasperDesign,
					getClass().getName(),
					JRLoader.loadBytes(classFiles[0]),
					datasetClasses
					);
		}
		catch (JRException e)
		{
			throw e;
		}
		catch (Exception e)
		{
			throw new JRException("Error compiling report design.", e);
		}
		finally
		{
			for (int i = 0; i < javaFiles.length; ++i)
			{
				if (!isKeepJavaFile)
				{
					javaFiles[i].delete();
				}
				classFiles[i].delete();
			}
		}

		return jasperReport;
	}
	
	
	/**
	 * Generates a Java source file for a dataset.
	 * 
	 * @param jasperDesign the report
	 * @param dataset the dataset
	 * @param tempDirFile the temporary directory
	 * @return an array containing the *.java and *.class files
	 * @throws JRException
	 */
	protected File[] generateSource(JasperDesign jasperDesign, JRDesignDataset dataset, File tempDirFile) throws JRException
	{
		//Generating expressions class source code
		String sourceCode = JRClassGenerator.generateClass(jasperDesign, dataset);

		String className = JRClassGenerator.getClassName(jasperDesign, dataset);
		File javaFile = new File(tempDirFile, className + ".java");
		File classFile = new File(tempDirFile, className + ".class");

		//Creating expression class source file
		JRSaver.saveClassSource(sourceCode, javaFile);

		return new File[]{javaFile, classFile};
	}

}
