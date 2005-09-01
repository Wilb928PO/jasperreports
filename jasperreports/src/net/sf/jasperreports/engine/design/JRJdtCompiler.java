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

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.JRExpressionCollector;
import net.sf.jasperreports.engine.JRReport;
import net.sf.jasperreports.engine.JRRuntimeException;
import net.sf.jasperreports.engine.util.JRClassLoader;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.eclipse.jdt.core.compiler.IProblem;
import org.eclipse.jdt.internal.compiler.ClassFile;
import org.eclipse.jdt.internal.compiler.CompilationResult;
import org.eclipse.jdt.internal.compiler.Compiler;
import org.eclipse.jdt.internal.compiler.DefaultErrorHandlingPolicies;
import org.eclipse.jdt.internal.compiler.ICompilerRequestor;
import org.eclipse.jdt.internal.compiler.IErrorHandlingPolicy;
import org.eclipse.jdt.internal.compiler.IProblemFactory;
import org.eclipse.jdt.internal.compiler.classfmt.ClassFileReader;
import org.eclipse.jdt.internal.compiler.env.IBinaryType;
import org.eclipse.jdt.internal.compiler.env.ICompilationUnit;
import org.eclipse.jdt.internal.compiler.env.INameEnvironment;
import org.eclipse.jdt.internal.compiler.env.NameEnvironmentAnswer;
import org.eclipse.jdt.internal.compiler.impl.CompilerOptions;
import org.eclipse.jdt.internal.compiler.problem.DefaultProblemFactory;

/**
 * 
 */
public class JRJdtCompiler extends JRAbstractJavaCompiler
{

	/**
	 *  
	 */
	static final Log log = LogFactory.getLog(JRJdtCompiler.class);
	
	private final ClassLoader classLoader;

	Constructor constrNameEnvAnsBin;
	Constructor constrNameEnvAnsCompUnit;
	
	boolean is2ArgsConstr;
	Constructor constrNameEnvAnsBin2Args;
	Constructor constrNameEnvAnsCompUnit2Args;

	public JRJdtCompiler ()
	{
		classLoader = getClassLoader();
		
		try
		{
			constrNameEnvAnsBin = NameEnvironmentAnswer.class.getConstructor(new Class[]{IBinaryType.class});
			constrNameEnvAnsCompUnit = NameEnvironmentAnswer.class.getConstructor(new Class[]{ICompilationUnit.class});
			is2ArgsConstr = false;
		}
		catch (NoSuchMethodException e)
		{
			// trying 3.1 classes
			try
			{
				Class classAccessRestriction = loadClass("org.eclipse.jdt.internal.compiler.env.AccessRestriction");
				constrNameEnvAnsBin2Args = NameEnvironmentAnswer.class.getConstructor(new Class[]{IBinaryType.class, classAccessRestriction});
				constrNameEnvAnsCompUnit2Args = NameEnvironmentAnswer.class.getConstructor(new Class[]{ICompilationUnit.class, classAccessRestriction});
				is2ArgsConstr = true;
			}
			catch (ClassNotFoundException ex)
			{
				throw new JRRuntimeException("Not able to load JDT classes", ex);
			}
			catch (NoSuchMethodException ex)
			{
				throw new JRRuntimeException("Not able to load JDT classes", ex);
			}
		}
	}	
	
	
	/**
	 *
	 */
	protected String compileUnits(final JRCompilationUnit[] units, String classpath, File tempDirFile) throws JRException
	{
		final StringBuffer problemBuffer = new StringBuffer();


		class CompilationUnit implements ICompilationUnit 
		{
			protected String srcCode;
			protected String className;

			public CompilationUnit(String srcCode, String className) 
			{
				this.srcCode = srcCode;
				this.className = className;
			}

			public char[] getFileName() 
			{
				return className.toCharArray();
			}

			public char[] getContents() 
			{
				return srcCode.toCharArray();
			}

			public char[] getMainTypeName() 
			{
				return className.toCharArray();
			}

			public char[][] getPackageName() 
			{
				return new char[0][0];
			}
		}

		
		final INameEnvironment env = new INameEnvironment() 
		{
			public NameEnvironmentAnswer findType(char[][] compoundTypeName) 
			{
				String result = "";
				String sep = "";
				for (int i = 0; i < compoundTypeName.length; i++) {
					result += sep;
					result += new String(compoundTypeName[i]);
					sep = ".";
				}
				return findType(result);
			}

			public NameEnvironmentAnswer findType(char[] typeName, char[][] packageName) 
			{
				String result = "";
				String sep = "";
				for (int i = 0; i < packageName.length; i++) {
					result += sep;
					result += new String(packageName[i]);
					sep = ".";
				}
				result += sep;
				result += new String(typeName);
				return findType(result);
			}

			private int getClassIndex(String className)
			{
				int classIdx;
				for (classIdx = 0; classIdx < units.length; ++classIdx)
				{
					if (className.equals(units[classIdx].getName()))
					{
						break;
					}
				}
				
				if (classIdx >= units.length)
				{
					classIdx = -1;
				}

				return classIdx;
			}
			
			private NameEnvironmentAnswer findType(String className) 
			{
				try 
				{
					int classIdx = getClassIndex(className);
					
					if (classIdx >= 0)
					{
						ICompilationUnit compilationUnit = 
							new CompilationUnit(
								units[classIdx].getSourceCode(), className);
						if (is2ArgsConstr)
						{
							return (NameEnvironmentAnswer) constrNameEnvAnsCompUnit2Args.newInstance(new Object[] { compilationUnit, null });
						}

						return (NameEnvironmentAnswer) constrNameEnvAnsCompUnit.newInstance(new Object[] { compilationUnit });
					}
					
					String resourceName = className.replace('.', '/') + ".class";
					InputStream is = getResource(resourceName);
					if (is != null) 
					{
						byte[] classBytes;
						byte[] buf = new byte[8192];
						ByteArrayOutputStream baos = new ByteArrayOutputStream(buf.length);
						int count;
						while ((count = is.read(buf, 0, buf.length)) > 0) 
						{
							baos.write(buf, 0, count);
						}
						baos.flush();
						classBytes = baos.toByteArray();
						char[] fileName = className.toCharArray();
						ClassFileReader classFileReader = 
							new ClassFileReader(classBytes, fileName, true);
						
						if (is2ArgsConstr)
						{
							return (NameEnvironmentAnswer) constrNameEnvAnsBin2Args.newInstance(new Object[] { classFileReader, null });
						}

						return (NameEnvironmentAnswer) constrNameEnvAnsBin.newInstance(new Object[] { classFileReader });
					}
				}
				catch (IOException exc) 
				{
					log.error("Compilation error", exc);
				}
				catch (org.eclipse.jdt.internal.compiler.classfmt.ClassFormatException exc) 
				{
					log.error("Compilation error", exc);
				}
				catch (InvocationTargetException e)
				{
					throw new JRRuntimeException("Not able to create NameEnvironmentAnswer", e);
				}
				catch (IllegalArgumentException e)
				{
					throw new JRRuntimeException("Not able to create NameEnvironmentAnswer", e);
				}
				catch (InstantiationException e)
				{
					throw new JRRuntimeException("Not able to create NameEnvironmentAnswer", e);
				}
				catch (IllegalAccessException e)
				{
					throw new JRRuntimeException("Not able to create NameEnvironmentAnswer", e);
				}
				return null;
			}

			private boolean isPackage(String result) 
			{
				int classIdx = getClassIndex(result);
				if (classIdx >= 0) 
				{
					return false;
				}
				
				String resourceName = result.replace('.', '/') + ".class";
				InputStream is = getResource(resourceName);
				return is == null;
			}

			public boolean isPackage(char[][] parentPackageName, char[] packageName) 
			{
				String result = "";
				String sep = "";
				if (parentPackageName != null) 
				{
					for (int i = 0; i < parentPackageName.length; i++) 
					{
						result += sep;
						String str = new String(parentPackageName[i]);
						result += str;
						sep = ".";
					}
				}
				String str = new String(packageName);
				if (Character.isUpperCase(str.charAt(0))) 
				{
					if (!isPackage(result)) 
					{
						return false;
					}
				}
				result += sep;
				result += str;
				return isPackage(result);
			}

			public void cleanup() 
			{
			}

		};

		final IErrorHandlingPolicy policy = 
			DefaultErrorHandlingPolicies.proceedWithAllProblems();

		final Map settings = new HashMap();
		settings.put(CompilerOptions.OPTION_LineNumberAttribute, CompilerOptions.GENERATE);
		settings.put(CompilerOptions.OPTION_SourceFileAttribute, CompilerOptions.GENERATE);
		settings.put(CompilerOptions.OPTION_ReportDeprecation, CompilerOptions.IGNORE);
//		if (ctxt.getOptions().getJavaEncoding() != null) 
//		{
//			settings.put(CompilerOptions.OPTION_Encoding, ctxt.getOptions().getJavaEncoding());
//		}
//		if (ctxt.getOptions().getClassDebugInfo()) 
//		{
//			settings.put(CompilerOptions.OPTION_LocalVariableAttribute, CompilerOptions.GENERATE);
//		}

		final IProblemFactory problemFactory = 
			new DefaultProblemFactory(Locale.getDefault());

		final ICompilerRequestor requestor = 
			new ICompilerRequestor() 
			{
				public void acceptResult(CompilationResult result) 
				{
					String className = ((CompilationUnit) result.getCompilationUnit()).className;
					
					int classIdx;
					for (classIdx = 0; classIdx < units.length; ++classIdx)
					{
						if (className.equals(units[classIdx].getName()))
						{
							break;
						}
					}
					
					if (result.hasErrors()) 
					{
						String sourceCode = units[classIdx].getSourceCode();
						
						IProblem[] problems = result.getErrors();
						for (int i = 0; i < problems.length; i++) 
						{
							IProblem problem = problems[i];
							//if (problem.isError()) 
							{
								problemBuffer.append(i + 1);
								problemBuffer.append(". ");
								problemBuffer.append(problem.getMessage());

								if (
									problem.getSourceStart() >= 0
									&& problem.getSourceEnd() >= 0
									)
								{									
									int problemStartIndex = sourceCode.lastIndexOf("\n", problem.getSourceStart()) + 1;
									int problemEndIndex = sourceCode.indexOf("\n", problem.getSourceEnd());
									if (problemEndIndex < 0)
									{
										problemEndIndex = sourceCode.length();
									}
									
									problemBuffer.append("\n");
									problemBuffer.append(
										sourceCode.substring(
											problemStartIndex,
											problemEndIndex
											)
										);
									problemBuffer.append("\n");
									for(int j = problemStartIndex; j < problem.getSourceStart(); j++)
									{
										problemBuffer.append(" ");
									}
									if (problem.getSourceStart() == problem.getSourceEnd())
									{
										problemBuffer.append("^");
									}
									else
									{
										problemBuffer.append("<");
										for(int j = problem.getSourceStart() + 1; j < problem.getSourceEnd(); j++)
										{
											problemBuffer.append("-");
										}
										problemBuffer.append(">");
									}
								}

								problemBuffer.append("\n");
							}
						}
						problemBuffer.append(problems.length);
						problemBuffer.append(" errors\n");
					}
					if (problemBuffer.length() == 0) 
					{
						ClassFile[] resultClassFiles = result.getClassFiles();
						for (int i = 0; i < resultClassFiles.length; i++) 
						{
							units[classIdx].setCompileData(resultClassFiles[i].getBytes());
						}
					}
				}
			};

		ICompilationUnit[] compilationUnits = new ICompilationUnit[units.length];
		for (int i = 0; i < compilationUnits.length; i++)
		{
			compilationUnits[i] = new CompilationUnit(units[i].getSourceCode(), units[i].getName());
		}

		Compiler compiler = 
			new Compiler(env, policy, settings, requestor, problemFactory);
		compiler.compile(compilationUnits);

		if (problemBuffer.length() > 0) 
		{
			return problemBuffer.toString();
		}

		return null;  
	}

	
	/**
	 *
	 */
	private ClassLoader getClassLoader()
	{
		ClassLoader clsLoader = Thread.currentThread().getContextClassLoader();

		if (clsLoader != null)
		{
			try
			{
				Class.forName(JRJdtCompiler.class.getName(), true, clsLoader);
			}
			catch (ClassNotFoundException e)
			{
				clsLoader = null;
				//if (log.isWarnEnabled())
				//	log.warn("Failure using Thread.currentThread().getContextClassLoader() in JRJdtCompiler class. Using JRJdtCompiler.class.getClassLoader() instead.");
			}
		}
	
		if (clsLoader == null)
		{
			clsLoader = JRClassLoader.class.getClassLoader();
		}

		return clsLoader;
	}
	
	protected InputStream getResource (String resourceName)
	{
		if (classLoader == null)
		{
			return JRJdtCompiler.class.getResourceAsStream("/" + resourceName);
		}
		return classLoader.getResourceAsStream(resourceName);
	}
	
	
	protected Class loadClass (String className) throws ClassNotFoundException
	{
		if (classLoader == null)
		{
			return Class.forName(className);
		}
		return classLoader.loadClass(className);
	}

	
	protected void checkLanguage(String language) throws JRException
	{		
		if (!JRReport.LANGUAGE_JAVA.equals(language))
		{
			throw 
				new JRException(
					"Language \"" + language 
					+ "\" not supported by this report compiler.\n"
					+ "Expecting \"java\" instead."
					);
		}
	}

	protected String generateSourceCode(JasperDesign jasperDesign, JRDesignDataset dataset, JRExpressionCollector expressionCollector) throws JRException
	{
		return JRClassGenerator.generateClass(jasperDesign, dataset, expressionCollector);
	}


	protected boolean needsSourceFile()
	{
		return false;
	}

	protected String getSourceFileName(String unitName)
	{
		return unitName + ".java";
	}
}
