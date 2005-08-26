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
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Locale;
import java.util.Map;

import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.JRReport;
import net.sf.jasperreports.engine.JRRuntimeException;
import net.sf.jasperreports.engine.JasperReport;
import net.sf.jasperreports.engine.util.JRClassLoader;
import net.sf.jasperreports.engine.util.JRProperties;
import net.sf.jasperreports.engine.util.JRSaver;

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

		//Generating expressions class source code
		String sourceCode = generateSourceCode(jasperDesign, jasperDesign.getMainDesignDataset(), tempDirFile);
		
		Map datasetMap = jasperDesign.getDatasetMap();
		
		String[] sources = new String[datasetMap.size() + 1];
		String[] classNames = new String[datasetMap.size() + 1];
		sources[0] = sourceCode;
		classNames[0] = jasperDesign.getName();
		
		Map datasetClasses = new HashMap();
		int sourcesCount = 1;
		for (Iterator it = datasetMap.entrySet().iterator(); it.hasNext(); ++sourcesCount)
		{
			Map.Entry  entry = (Map.Entry ) it.next();
			JRDesignDataset dataset = (JRDesignDataset) entry.getValue();
			
			String datasetCode = generateSourceCode(jasperDesign, dataset, tempDirFile);
			
			sources[sourcesCount] = datasetCode;
			classNames[sourcesCount] = JRClassGenerator.getClassName(jasperDesign, dataset);
			
			datasetClasses.put(entry.getKey(), new Integer(sourcesCount));
		}
		
		try
		{
			ClassFile[] classFiles = new ClassFile[sources.length];
			
			//Compiling expression class source file
			String compileErrors = compileClasses(sources, classNames, classFiles);
			if (compileErrors != null)
			{
				throw new JRException("Errors were encountered when compiling report expressions class file:\n" + compileErrors);
			}
			
			for (Iterator it = datasetClasses.entrySet().iterator(); it.hasNext();)
			{
				Map.Entry entry = (Map.Entry) it.next();
				int sourceIndex = ((Integer) entry.getValue()).intValue();
				entry.setValue(classFiles[sourceIndex].getBytes());
			}

			//Reading class byte codes from compiled class file
			jasperReport = 
				new JasperReport(
					jasperDesign,
					JRJavacCompiler.class.getName(),
					classFiles[0].getBytes(),
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

		return jasperReport;
	}

	
	private String generateSourceCode(JasperDesign jasperDesign, JRDesignDataset dataset, File saveSourceDir) throws JRException
	{
		String sourceCode = JRClassGenerator.generateClass(jasperDesign, dataset);
		
		if (saveSourceDir != null)
		{
			File javaFile = new File(saveSourceDir, JRClassGenerator.getClassName(jasperDesign, dataset) + ".java");

			//Creating expression class source file
			JRSaver.saveClassSource(sourceCode, javaFile);
		}
		
		return sourceCode;
	}
	
	
	/**
	 *
	 */
	private String compileClasses(final String[] sources, final String[] targetClassNames, final ClassFile[] classFiles)
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
				for (classIdx = 0; classIdx < targetClassNames.length; ++classIdx)
				{
					if (className.equals(targetClassNames[classIdx]))
					{
						break;
					}
				}
				
				if (classIdx >= targetClassNames.length)
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
								sources[classIdx], className);
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
					for (classIdx = 0; classIdx < targetClassNames.length; ++classIdx)
					{
						if (className.equals(targetClassNames[classIdx]))
						{
							break;
						}
					}
					
					if (result.hasErrors()) 
					{
						String sourceCode = sources[classIdx];
						
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
							classFiles[classIdx] = resultClassFiles[i];
						}
					}
				}
			};

		ICompilationUnit[] compilationUnits = new ICompilationUnit[sources.length];
		for (int i = 0; i < compilationUnits.length; i++)
		{
			compilationUnits[i] = new CompilationUnit(sources[i], targetClassNames[i]);
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
}
