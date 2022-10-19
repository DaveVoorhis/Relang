package org.reldb.relang.transpiler;

import java.util.regex.Pattern;

/** Indentation tool. */

public class Formatter {
	
	public static String indent(Object o) {
		if (o == null)
			return null;
		// Put a tab at the start of every line
		return Pattern.compile("^", Pattern.MULTILINE).matcher(o.toString()).replaceAll("\t");
	}

}
