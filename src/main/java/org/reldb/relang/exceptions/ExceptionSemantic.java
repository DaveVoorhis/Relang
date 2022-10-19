package org.reldb.relang.exceptions;

import org.reldb.relang.parser.ast.Node;
import org.reldb.relang.transpiler.BaseASTNode;

/**
 * This exception is thrown when semantic errors are encountered.
 */
public class ExceptionSemantic extends Error {

	static final long serialVersionUID = 0;
	
	public ExceptionSemantic(String message, Node node) {
		super(message + "\nAt line " + 
				((BaseASTNode)node).first_token.beginLine + " column " + 
				((BaseASTNode)node).first_token.beginColumn + " near " + ((BaseASTNode)node).tokenValue);
	}
	
}
