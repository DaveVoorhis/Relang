package org.reldb.relang.exceptions;

/**
 * This exception is thrown when fatal errors are encountered.
 */
public class ExceptionFatal extends Error {

	static final long serialVersionUID = 0;
	
	public ExceptionFatal(String message) {
		super(message);
	}

}
