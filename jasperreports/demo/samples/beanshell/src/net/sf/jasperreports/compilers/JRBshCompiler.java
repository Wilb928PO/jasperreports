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
package net.sf.jasperreports.compilers;

import java.io.File;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import net.sf.jasperreports.engine.JRDataset;
import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.JRReport;
import net.sf.jasperreports.engine.JasperReport;
import net.sf.jasperreports.engine.design.JRClassGenerator;
import net.sf.jasperreports.engine.design.JRCompiler;
import net.sf.jasperreports.engine.design.JRDesignDataset;
import net.sf.jasperreports.engine.design.JRVerifier;
import net.sf.jasperreports.engine.design.JasperDesign;
import net.sf.jasperreports.engine.fill.JRCalculator;
import net.sf.jasperreports.engine.util.JRProperties;
import net.sf.jasperreports.engine.util.JRSaver;


/**
 * @author Teodor Danciu (teodord@users.sourceforge.net)
 * @version $Id$
 */
public class JRBshCompiler implements JRCompiler
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
		
		boolean isKeepJavaFile = JRProperties.getBooleanProperty(JRProperties.COMPILER_KEEP_JAVA_FILE);
		File tempDirFile = null;
		if (isKeepJavaFile) 
		{
			String tempDirStr = JRProperties.getProperty(JRProperties.COMPILER_TEMP_DIR);

			tempDirFile = new File(tempDirStr);
			if (!tempDirFile.exists() || !tempDirFile.isDirectory())
			{
				throw new JRException("Temporary directory not found : " + tempDirStr);
			}
		}

		//Generating BeanShell script for report expressions
		String bshScript = generateSource(jasperDesign, jasperDesign.getMainDesignDataset(), tempDirFile);
		
		Map datasetMap = jasperDesign.getDatasetMap();
		Map datasetSources = new HashMap();
		for (Iterator it = datasetMap.entrySet().iterator(); it.hasNext();)
		{
			Map.Entry entry = (Map.Entry) it.next();
			JRDesignDataset dataset = (JRDesignDataset) entry.getValue();
			String datasetScript = generateSource(jasperDesign, dataset, tempDirFile);
			datasetSources.put(entry.getKey(), datasetScript);
		}
		
		jasperReport = 
			new JasperReport(
				jasperDesign,
				getClass().getName(),
				bshScript,
				datasetSources
				);

		/*   */
		verifyScript(jasperDesign, jasperDesign.getMainDesignDataset(), bshScript);
		
		for (Iterator it = datasetMap.entrySet().iterator(); it.hasNext();)
		{
			Map.Entry entry = (Map.Entry) it.next();
			JRDesignDataset dataset = (JRDesignDataset) entry.getValue();
			String script = (String) datasetSources.get(entry.getKey());
			verifyScript(jasperDesign, dataset, script);
		}

		return jasperReport;
	}


	private String generateSource(JasperDesign jasperDesign, JRDesignDataset dataset, File saveSourceDir) throws JRException
	{
		String bshScript = JRBshGenerator.generateScript(jasperDesign, dataset);

		if (saveSourceDir != null) 
		{
			File javaFile = new File(saveSourceDir, JRClassGenerator.getClassName(jasperDesign, dataset) + ".bsh");
			
			JRSaver.saveClassSource(bshScript, javaFile);
		}
		
		return bshScript;
	}

	
	/**
	 *
	 */
	private void verifyScript(JasperDesign jasperDesign, JRDesignDataset dataset, String bshScript) throws JRException
	{
		ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
		
		// trick for detecting the Ant class loader
		try
		{
			classLoader.loadClass(JRCalculator.class.getName());
		}
		catch(ClassNotFoundException e)
		{
			classLoader = getClass().getClassLoader();
		}

		ClassLoader oldContextClassLoader = Thread.currentThread().getContextClassLoader();
		Thread.currentThread().setContextClassLoader(classLoader);

		JRBshCalculator bshCalculator = new JRBshCalculator(bshScript);
		bshCalculator.verify(jasperDesign.getExpressions(dataset));

		Thread.currentThread().setContextClassLoader(oldContextClassLoader);
	}


	/**
	 *
	 */
	public JRCalculator loadCalculator(JasperReport jasperReport) throws JRException
	{
		return new JRBshCalculator((String)jasperReport.getCompileData());
	}


	public JRCalculator loadCalculator(JasperReport jasperReport, JRDataset dataset) throws JRException
	{
		return new JRBshCalculator((String)jasperReport.getDatasetCompileData(dataset));
	}


}
