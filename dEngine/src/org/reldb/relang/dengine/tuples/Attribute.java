package org.reldb.relang.dengine.tuples;

public class Attribute {
	public final String name;
	public final Class<?> type;
	
	public Attribute(String name, Class<?> type) {
		this.name = name;
		this.type = type;
	}
}