package org.reldb.relang.values;

public class ValueRational extends ValueAbstract {

	private final double internalValue;
	
	public ValueRational(double b) {
		internalValue = b;
	}
	
	public String getTypeName() {
		return "rational";
	}
	
	/** Convert this to a primitive double. */
	public double doubleValue() {
		return internalValue;
	}
	
	/** Convert this to a primitive String. */
	public String stringValue() {
		return "" + internalValue;
	}

	public int compareTo(Value v) {
		return Double.compare(internalValue, v.doubleValue());
	}
	
	public Value add(Value v) {
		return new ValueRational(internalValue + v.doubleValue());
	}

	public Value subtract(Value v) {
		return new ValueRational(internalValue - v.doubleValue());
	}

	public Value mult(Value v) {
		return new ValueRational(internalValue * v.doubleValue());
	}

	public Value div(Value v) {
		return new ValueRational(internalValue / v.doubleValue());
	}

	public Value unary_plus() {
		return new ValueRational(internalValue);
	}

	public Value unary_minus() {
		return new ValueRational(-internalValue);
	}
	
	public String toString() {
		return "" + internalValue;
	}
}
