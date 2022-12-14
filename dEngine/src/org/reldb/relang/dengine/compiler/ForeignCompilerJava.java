package org.reldb.relang.dengine.compiler;

import static org.reldb.relang.dengine.strings.Strings.*;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.io.PrintWriter;

import org.reldb.relang.dengine.exceptions.ExceptionFatal;
import org.reldb.relang.dengine.strings.Str;

/**
 * Machinery for compiling Java code.
 * 
 * @author Dave
 *
 */
public class ForeignCompilerJava {
	private final static boolean verbose = false;
	
	private String userSourcePath;
	
	public ForeignCompilerJava(String userSourcePath) {
		this.userSourcePath = userSourcePath;
	}
	   
    public static class CompilationResults {
    	public final boolean compiled;
    	public final String compilerMessages;
		public CompilationResults(boolean compiled, String compilerMessages) {
			this.compiled = compiled;
			this.compilerMessages = compilerMessages;
		}
		public String toString() {
			return "CompilationResults:\n" + compilerMessages;
		}
    }
    
    /** Compile foreign code using Eclipse JDT compiler. */
    public CompilationResults compileForeignCode(String className, String src) {
    	ByteArrayOutputStream messageStream = new ByteArrayOutputStream();
    	ByteArrayOutputStream warningStream = new ByteArrayOutputStream();
    	String warningSetting = new String("allDeprecation,"
    			+ "allJavadoc," + "assertIdentifier," + "charConcat,"
    			+ "conditionAssign," + "constructorName," + "deprecation,"
    			+ "emptyBlock," + "fieldHiding," + "finalBound,"
    			+ "finally," + "indirectStatic," + "intfNonInherited,"
    			+ "javadoc," + "localHiding," + "maskedCatchBlocks,"
    			+ "noEffectAssign," + "pkgDefaultMethod," + "serial,"
    			+ "semicolon," + "specialParamHiding," + "staticReceiver,"
    			+ "syntheticAccess," + "unqualifiedField,"
    			+ "unnecessaryElse," + "uselessTypeCheck," + "unsafe,"
    			+ "unusedArgument," + "unusedImport," + "unusedLocal,"
    			+ "unusedPrivate," + "unusedThrown");

    	String classpath = 
    			cleanClassPath(System.getProperty("java.class.path")) + 
    			java.io.File.pathSeparatorChar + 
    			cleanClassPath(getLocalClasspath());

        // If resource directory doesn't exist, create it.
        File resourceDir = new File(userSourcePath); 
        if (!(resourceDir.exists()))
            resourceDir.mkdirs();
    	File sourcef;
    	try {
    		// Write source to a Java source file
    		sourcef = new File(userSourcePath + java.io.File.separator + getStrippedClassname(className) + ".java");
    		PrintStream sourcePS = new PrintStream(new FileOutputStream(sourcef));
    		sourcePS.print(src);
    		sourcePS.close();
    	} catch (IOException ioe) {
    		throw new ExceptionFatal(Str.ing(ErrSavingJavaSource, ioe.toString()));
    	}
 
    	if (verbose) {
    		System.out.println(src);
    		System.out.println("\nCompile:\n" + sourcef);
    	}
    	
    	// Start compilation using JDT
   		String commandLine = "-1.9 -source 1.9 -warn:" + 
    			warningSetting + " " + 
    			"-cp " + classpath + " \"" + sourcef + "\"";
    	boolean compiled = org.eclipse.jdt.core.compiler.batch.BatchCompiler.compile(
    			commandLine,
    			new PrintWriter(messageStream), 
    			new PrintWriter(warningStream), 
    			null);
 
    	String compilerMessages = "";
    	// Parse the messages and the warnings.
    	BufferedReader br = new BufferedReader(new InputStreamReader(new ByteArrayInputStream(messageStream.toByteArray())));
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
    		compilerMessages += str + '\n';
    	}
    	BufferedReader brWarnings = new BufferedReader(new InputStreamReader(new ByteArrayInputStream(warningStream.toByteArray())));
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
    		compilerMessages += str + '\n';
    	}
    	if (verbose)
    		System.out.println(compilerMessages);
    	return new CompilationResults(compiled, compilerMessages);
    }
	
    /** Return a classpath cleaned of non-existent files and Web Start's deploy.jar.  
     * Classpath elements with spaces are converted to quote-delimited strings. */
    private final static String cleanClassPath(String s) {
    	if (java.io.File.separatorChar == '/')
    		s = s.replace('\\', '/');
    	else
    		s = s.replace('/', '\\');
        String outstr = "";
        java.util.StringTokenizer st = new java.util.StringTokenizer(s, java.io.File.pathSeparator);
        while (st.hasMoreElements()) {
            String element = (String)st.nextElement();
            java.io.File f = new java.io.File(element);
            if (f.exists() && !element.contains("deploy.jar")) {
            	String fname = f.toString();
            	if (fname.indexOf(' ')>=0)
            		fname = '"' + fname + '"';
                outstr += ((outstr.length()>0) ? java.io.File.pathSeparator : "") + fname;
            }
        }
        return outstr;
    }
    
	/** Return classpath to the Relang core. */
    private String getLocalClasspath() {
        String classPath = System.getProperty("user.dir") + 
        	   java.io.File.pathSeparatorChar + userSourcePath;
        return classPath;
    }
    
    /** Get a stripped name.  Only return text after the final '.' */
    private static String getStrippedName(String name) {
        int lastDot = name.lastIndexOf('.');
        if (lastDot >= 0)
            return name.substring(lastDot + 1);
        else
            return name;
    }
    
    /** Get stripped Java Class name. */
    private static String getStrippedClassname(String name) {
    	return getStrippedName(name);
    }
    
}
