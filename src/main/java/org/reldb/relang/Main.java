package org.reldb.relang;

import org.reldb.relang.exceptions.ExceptionSemantic;
import org.reldb.relang.java.DirClassLoader;
import org.reldb.relang.java.ForeignCompilerJava;
import org.reldb.relang.parser.ast.*;
import org.reldb.relang.transpiler.Parser;
import org.reldb.relang.transpiler.ParserDebugger;

import static org.reldb.relang.transpiler.Formatter.indent;

public class Main {
	
	private static void usage() {
		System.out.println("Usage: relang [-d0 | -d1] < <source>");
		System.out.println("          -d0 -- run-time debugging");
		System.out.println("          -d1 -- output AST");
	}
	
	public static void main(String[] args) {
		var debugOnRun = false;
		var debugAST = false;
		if (args.length == 1) {
			if (args[0].equals("-d0"))
				debugOnRun = true;
			else if (args[0].equals("-d1"))
				debugAST = true;
			else {
				usage();
				return;
			}
		}
		var language = new Relang(System.in);
		try {
			var parser = language.code();
			RelangVisitor nodeVisitor;
			if (debugAST)
				nodeVisitor = new ParserDebugger();
			else {
				if (debugOnRun)
					System.out.println("Compiling...");
				nodeVisitor = new Parser();
			}
			// Run the input stream through the translator to get translated code.
			var code = (String)parser.jjtAccept(nodeVisitor, null);
			if (debugAST) {
				return;
			}
			// Dump if debugging
			if (debugOnRun) {
				System.out.println("Compiled:");
				System.out.println(indent(code));
				System.out.println("Executing...");
			}
			// compile translated code
			var compiler = new ForeignCompilerJava(debugOnRun);
			compiler.compileForeignCode(System.out, Parser.generatedCodeClassName, code);
			// load and run translated code
			var classLoader = new DirClassLoader(ForeignCompilerJava.dataDir);
			var generatedClass = classLoader.forName(Parser.generatedCodeClassName);
			var mainMethod = generatedClass.getMethod(Parser.generatedCodeMainMethodName, (Class<?>[])null);
			mainMethod.invoke(null);
		} catch (ExceptionSemantic es) {
			System.out.println(es.getMessage());
		} catch (Throwable e) {
			System.out.println(e.getMessage());
			e.printStackTrace();
		}
	}
}
