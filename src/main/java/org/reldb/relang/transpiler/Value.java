package org.reldb.relang.transpiler;

/** A compile-time Value. */
public class Value {

	private final String type;

	private final String expression;

	public Value(String type, String expression) {
		this.type = type;
		this.expression = expression;
	}

	public String getTypeName() {
		return type;
	}

	public String getExpression() {
		return expression;
	}

	public String toString() {
		return getExpression();
	}
}
