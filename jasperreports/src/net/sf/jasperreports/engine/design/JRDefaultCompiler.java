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

import net.sf.jasperreports.engine.JRDataset;
import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.JasperReport;
import net.sf.jasperreports.engine.crosstab.JRCrosstab;
import net.sf.jasperreports.engine.fill.JREvaluator;
import net.sf.jasperreports.engine.util.JRClassLoader;
import net.sf.jasperreports.engine.util.JRProperties;


/**
 * @author Teodor Danciu (teodord@users.sourceforge.net)
 * @version $Id$
 */
public class JRDefaultCompiler implements JRCompiler
{


	/**
	 *
	 */
	public JasperReport compileReport(JasperDesign jasperDesign) throws JRException
	{
		JRCompiler jrCompiler = null;

		String compiler = JRProperties.getProperty(JRProperties.COMPILER_CLASS);
		if (compiler == null || compiler.trim().length() == 0)
		{
			try 
			{
				JRClassLoader.loadClassForName("org.eclipse.jdt.internal.compiler.Compiler");
				jrCompiler = new JRJdtCompiler();
			}
			catch (Exception e)
			{
			}
	
			if (jrCompiler == null)
			{
				try 
				{
					JRClassLoader.loadClassForName("com.sun.tools.javac.Main");
					jrCompiler = new JRJdk13Compiler();
				}
				catch (Exception e)
				{
				}
			}
	
			if (jrCompiler == null)
			{
				try 
				{
					JRClassLoader.loadClassForName("sun.tools.javac.Main");
					jrCompiler = new JRJdk12Compiler();
				}
				catch (Exception e)
				{
				}
			}
	
			if (jrCompiler == null)
			{
				jrCompiler = new JRJavacCompiler();
			}
		}
		else
		{
			try 
			{
				Class clazz = JRClassLoader.loadClassForName(compiler);
				jrCompiler = (JRCompiler)clazz.newInstance();
			}
			catch (Exception e)
			{
				throw new JRException("Could not instantiate report compiler : " + compiler, e);
			}
		}

		return jrCompiler.compileReport(jasperDesign);
	}


	private JRCompiler getCompiler(JasperReport jasperReport) throws JRException
	{
		JRCompiler compiler = null;
		
		String compilerClassName = jasperReport.getCompilerClass();

		Class compilerClass = null;
		
		ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
		if (classLoader != null)
		{
			try
			{
				compilerClass = classLoader.loadClass(compilerClassName);
			}
			catch(ClassNotFoundException e)
			{
			}
		}
		
		if (compilerClass == null)
		{
			classLoader = JRDefaultCompiler.class.getClassLoader();
			try
			{
				if (classLoader == null)
				{
					compilerClass = Class.forName(compilerClassName);
				}
				else
				{
					compilerClass = classLoader.loadClass(compilerClassName);
				}
			}
			catch(ClassNotFoundException e)
			{
				throw new JRException("Report compiler class not found : " + compilerClassName);
			}
		}


		try
		{
			compiler = (JRCompiler)compilerClass.newInstance();
		}
		catch (Exception e)
		{
			throw new JRException("Could not instantiate report compiler : " + compilerClassName, e);
		}
		return compiler;
	}

	
	/**
	 *
	 */
	public JREvaluator loadEvaluator(JasperReport jasperReport, JRDataset dataset) throws JRException
	{
		JRCompiler compiler = getCompiler(jasperReport);
		
		return compiler.loadEvaluator(jasperReport, dataset);
	}


	public JREvaluator loadEvaluator(JasperReport jasperReport, JRCrosstab crosstab) throws JRException
	{
		JRCompiler compiler = getCompiler(jasperReport);
		
		return compiler.loadEvaluator(jasperReport, crosstab);
	}


	public JREvaluator loadEvaluator(JasperReport jasperReport) throws JRException
	{
		return loadEvaluator(jasperReport, jasperReport.getMainDataset());
	}


}
