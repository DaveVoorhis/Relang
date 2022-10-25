package org.reldb.relang.java;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.io.PrintWriter;
import java.util.StringTokenizer;

import org.eclipse.jdt.core.compiler.CompilationProgress;

import org.reldb.relang.exceptions.ExceptionFatal;

public class JavaCompiler {

	public final static String dataDir = "data";

	private final boolean verbose;

	public JavaCompiler(boolean verbose) {
		this.verbose = verbose;
	}

	/** Compile foreign code using Eclipse JDT compiler. */
	public void compile(String className, String src) {
		var messageStream = new ByteArrayOutputStream();
		var warningStream = new ByteArrayOutputStream();
		var warningSetting = "allDeprecation,"
				+ "assertIdentifier,"
				+ "charConcat,"
				+ "conditionAssign,"
				+ "constructorName,"
				+ "deprecation,"
				+ "emptyBlock,"
				+ "fieldHiding,"
				+ "finalBound,"
				+ "finally,"
				+ "indirectStatic,"
				+ "intfNonInherited,"
				+ "maskedCatchBlocks,"
				+ "noEffectAssign,"
				+ "pkgDefaultMethod,"
				+ "serial,"
				+ "semicolon,"
				+ "specialParamHiding,"
				+ "staticReceiver,"
				+ "syntheticAccess,"
				+ "unqualifiedField,"
				+ "unnecessaryElse,"
				+ "uselessTypeCheck,"
				+ "unsafe,"
				+ "unusedImport,"
				+ "unusedLocal,"
				+ "unusedPrivate,"
				+ "unusedThrown";

		// If resource directory doesn't exist, create it.
		var resourceDir = new File(dataDir);
		if (!(resourceDir.exists()))
			if (!resourceDir.mkdirs())
				throw new ExceptionFatal("Unable to create " + resourceDir);
		File sourcef;
		try {
			// Write source to a Java source file
			sourcef = new File(resourceDir + java.io.File.separator + getStrippedClassname(className) + ".java");
			var sourcePS = new PrintStream(new FileOutputStream(sourcef));
			sourcePS.print(src);
			sourcePS.close();
		} catch (IOException ioe) {
			throw new ExceptionFatal("Unable to save Java source: " + ioe);
		}

		var classpath = cleanClassPath(System.getProperty("java.class.path")) + java.io.File.pathSeparatorChar
				+ resourceDir.getAbsolutePath();

		notify("ForeignCompilerJava: classpath = " + classpath);

		// Start compilation using JDT
		var commandLine = "-1.8 -source 1.8 -warn:" + warningSetting + " " + "-cp " + classpath + " \"" + sourcef
				+ "\"";
		var compiled = org.eclipse.jdt.core.compiler.batch.BatchCompiler.compile(commandLine,
				new PrintWriter(messageStream), new PrintWriter(warningStream), new CompilationProgress() {
					@Override
					public void begin(int arg0) {
					}

					@Override
					public void done() {
					}

					@Override
					public boolean isCanceled() {
						return false;
					}

					@Override
					public void setTaskName(String arg0) {
						JavaCompiler.this.notify(arg0);
					}

					@Override
					public void worked(int arg0, int arg1) {
					}
				});

		var compilerMessages = new StringBuilder();
		// Parse the messages and the warnings.
		var br = new BufferedReader(
				new InputStreamReader(new ByteArrayInputStream(messageStream.toByteArray())));
		while (true) {
			String str = null;
			try {
				str = br.readLine();
			} catch (IOException e) {
				e.printStackTrace();
			}
			if (str == null) {
				break;
			}
			compilerMessages.append(str).append('\n');
		}
		var brWarnings = new BufferedReader(
				new InputStreamReader(new ByteArrayInputStream(warningStream.toByteArray())));
		while (true) {
			String str = null;
			try {
				str = brWarnings.readLine();
			} catch (IOException e) {
				e.printStackTrace();
			}
			if (str == null) {
				break;
			}
			compilerMessages.append(str).append('\n');
		}

		if (!compiled)
			throw new ExceptionFatal("Compilation failed due to errors: \n" + compilerMessages + "\n");
	}

	/**
	 * Return a classpath cleaned of non-existent files. Classpath elements with
	 * spaces are converted to quote-delimited strings.
	 */
	private static String cleanClassPath(String path) {
		if (java.io.File.separatorChar == '/')
			path = path.replace('\\', '/');
		else
			path = path.replace('/', '\\');
		var outstr = new StringBuilder();
		var stringTokenizer = new StringTokenizer(path, java.io.File.pathSeparator);
		while (stringTokenizer.hasMoreElements()) {
			var element = (String) stringTokenizer.nextElement();
			var file = new File(element);
			if (file.exists()) {
				String fname = file.toString();
				if (fname.indexOf(' ') >= 0)
					fname = '"' + fname + '"';
				outstr.append((outstr.length() > 0) ? File.pathSeparator : "").append(fname);
			}
		}
		return outstr.toString();
	}

	/** Get a stripped name. Only return text after the final '.' */
	private static String getStrippedName(String name) {
		var lastDot = name.lastIndexOf('.');
		if (lastDot >= 0)
			return name.substring(lastDot + 1);
		else
			return name;
	}

	/** Get stripped Java Class name. */
	private static String getStrippedClassname(String name) {
		return getStrippedName(name);
	}

	private void notify(String s) {
		if (verbose)
			System.out.println(s);
	}

}
