package org.reldb.relang;

import org.reldb.relang.exceptions.ExceptionSemantic;
import org.reldb.relang.java.DirClassLoader;
import org.reldb.relang.java.JavaCompiler;
import org.reldb.relang.parser.ast.*;
import org.reldb.relang.transpiler.Parser;
import org.reldb.relang.transpiler.ParserDebugger;

import java.io.InputStream;
import java.lang.reflect.InvocationTargetException;

import static org.reldb.relang.transpiler.Formatter.indent;

public class Main {

	public enum DebugModes {
		NORMAL,
		VERBOSE_RUN,
		EMIT_AST_AND_QUIT
	}

	private void usage() {
		System.out.println("Usage: relang [-d0 | -d1] < <source>");
		System.out.println("          -d0 -- run-time debugging");
		System.out.println("          -d1 -- output AST");
	}

	private static long unique = 0;

	private static String getClassName() {
		return "RelangGenerated" + unique++;
	}

	private DebugModes debugMode = DebugModes.NORMAL;

	public void setDebugMode(DebugModes _debugMode) {
		debugMode = _debugMode;
	}

	public Object evaluate(InputStream inputStream)
			throws InvocationTargetException,
			IllegalAccessException,
			NoSuchMethodException,
			ClassNotFoundException,
			ParseException {
		var relang = new Relang(inputStream);
		var className = getClassName();
		var parser = debugMode == DebugModes.EMIT_AST_AND_QUIT
				? new ParserDebugger()
				: new Parser(className);
		if (debugMode == DebugModes.VERBOSE_RUN) {
			System.out.println("Compiling...");
		}
		// Run the input stream through the translator to get translated code.
		var java = (String)relang.evaluate().jjtAccept(parser, null);
		if (debugMode == DebugModes.EMIT_AST_AND_QUIT) {
			return null;
		}
		// Dump if debugging
		if (debugMode == DebugModes.VERBOSE_RUN) {
			System.out.println("Compiled:");
			System.out.println(indent(java));
			System.out.println("Executing...");
		}
		// compile translated code
		var compiler = new JavaCompiler(debugMode == DebugModes.VERBOSE_RUN);
		compiler.compile(className, java);
		// load and run translated code
		var classLoader = new DirClassLoader(JavaCompiler.dataDir);
		var generatedClass = classLoader.forName(className);
		Object returnValue;
		try {
			var mainMethod = generatedClass.getMethod(Parser.generatedCodeMainMethodName, (Class<?>[]) null);
			returnValue = mainMethod.invoke(null);
		} finally {
			classLoader.unload(className);
		}
		return returnValue;
	}

	public void execute(InputStream inputStream)
			throws InvocationTargetException,
				IllegalAccessException,
				NoSuchMethodException,
				ClassNotFoundException,
				ParseException {
		var relang = new Relang(inputStream);
		var className = getClassName();
		var parser = debugMode == DebugModes.EMIT_AST_AND_QUIT
			? new ParserDebugger()
			: new Parser(className);
		if (debugMode == DebugModes.VERBOSE_RUN) {
			System.out.println("Compiling...");
		}
		// Run the input stream through the translator to get translated code.
		var java = (String)relang.execute().jjtAccept(parser, null);
		if (debugMode == DebugModes.EMIT_AST_AND_QUIT) {
			return;
		}
		// Dump if debugging
		if (debugMode == DebugModes.VERBOSE_RUN) {
			System.out.println("Compiled:");
			System.out.println(indent(java));
			System.out.println("Executing...");
		}
		// compile translated code
		var compiler = new JavaCompiler(debugMode == DebugModes.VERBOSE_RUN);
		compiler.compile(className, java);
		// load and run translated code
		var classLoader = new DirClassLoader(JavaCompiler.dataDir);
		var generatedClass = classLoader.forName(className);
		try {
			var mainMethod = generatedClass.getMethod(Parser.generatedCodeMainMethodName, (Class<?>[]) null);
			mainMethod.invoke(null);
		} finally {
			classLoader.unload(className);
		}
	}

	public static void main(String[] args) {
		var relang = new Main();
		if (args.length == 1) {
			if (args[0].equals("-d0"))
				relang.setDebugMode(DebugModes.VERBOSE_RUN);
			else if (args[0].equals("-d1"))
				relang.setDebugMode(DebugModes.EMIT_AST_AND_QUIT);
			else {
				relang.usage();
				return;
			}
		}
		try {
			relang.execute(System.in);
		} catch (ExceptionSemantic es) {
			System.out.println(es.getMessage());
		} catch (Throwable e) {
			System.out.println(e.getMessage());
			e.printStackTrace();
		}
	}
}
