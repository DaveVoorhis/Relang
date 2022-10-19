package org.reldb.relang.values;

import java.io.PrintStream;

/** An abstract Value, that defines all possible operations on abstract ValueS.
 * <p>
 *  If an operation is not supported, throw SemanticException.
 */
public interface Value extends Comparable<Value> {
	
	/** Output name of the type of this Value. */
	String getTypeName();
	
	/** Output this Value to a PrintStream. */
	void toStream(PrintStream p, int depth);
	
	/** Write as parsable string. */
	String toParsableString();
	
	/** Write as final string. */
	String toString();
	
	/** Compare this value and another. */
	int compareTo(Value v);

	/** Check for equality. */
	boolean equals(Object o);
	
	/** Perform logical XOR on this value and another. */
	Value xor(Value v);
	
	/** Perform logical OR on this value and another. */
	Value or(Value v);
	
	/** Perform logical AND on this value and another. */
	Value and(Value v);
	
	/** Perform logical NOT on this value. */
	Value not();
	
	/** Add this value to another. */
	Value add(Value v);
	
	/** Subtract another value from this. */
	Value subtract(Value v);
	
	/** Multiply this value with another. */
	Value mult(Value v);
	
	/** Divide another value by this. */
	Value div(Value v);
	
	/** Return unary plus of this value. */
	Value unary_plus();
	
	/** Return unary minus of this value. */
	Value unary_minus();
	
	/** Convert this to a primitive boolean. */
	boolean booleanValue();
	
	/** Convert this to a primitive long. */
	long longValue();
	
	/** Convert this to a primitive double. */
	double doubleValue();

	/** Convert this to a primitive string. */
	String stringValue();
	
	/** Test this value and another for equality. */
	Value eq(Value v);
	
	/** Test this value and another for non-equality. */
	Value neq(Value v);
	
	/** Test this value and another for >= */
	Value gte(Value v);
	
	/** Test this value and another for <= */
	Value lte(Value v);
	
	/** Test this value and another for > */
	Value gt(Value v);
	
	/** Test this value and another for < */
	Value lt(Value v);
}
