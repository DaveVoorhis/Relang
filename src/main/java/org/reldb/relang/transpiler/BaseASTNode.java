package org.reldb.relang.transpiler;

import org.reldb.relang.parser.ast.Token;

/** This is the base class for every AST node.  
 * <p>
 * tokenValue contains the actual value from which the token was constructed.
 * <p>
 * ifHasElse is set at parse-time to indicate to the compiler whether an IF clause has an ELSE.
 *
 */
public class BaseASTNode {
	public String tokenValue = null;
	public boolean ifHasElse = false;
	public Token first_token;
	public Token last_token;	
}
