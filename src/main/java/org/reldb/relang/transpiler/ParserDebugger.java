package org.reldb.relang.transpiler;

import org.reldb.relang.parser.ast.*;

public class ParserDebugger implements RelangVisitor {
	
	private int indent = 0;
	
	private String indentString() {
		return " ".repeat(Math.max(0, indent));
	}
	
	/** Debugging dump of a node. */
	private Object dump(SimpleNode node, Object data) {
		System.out.println(indentString() + node);
		++indent;
		data = node.childrenAccept(this, data);
		--indent;
		return data;
	}
	
	public Object visit(SimpleNode node, Object data) {
		System.out.println(node + ": acceptor not implemented in subclass?");
		return data;
	}
	
	// Execute a program
	public Object visit(ASTExecute node, Object data) {
		return dump(node, data);
	}

	// Evaluate an expression, possibly preceded by arbitrary multiple statements.
	@Override
	public Object visit(ASTEvaluate node, Object data) {
		return dump(node, data);
	}

	// Execute a statement
	public Object visit(ASTStatement node, Object data) {
		return dump(node, data);
	}

	// Execute a block
	public Object visit(ASTBlock node, Object data) {
		return dump(node, data);
	}

	// Execute an IF 
	public Object visit(ASTIfStatement node, Object data) {
		return dump(node, data);
	}

	// Function definition parameter def -- type name pair
	public Object visit(ASTParameter node, Object data) {
		return dump(node, data);
	}
	
	// Function definition parameter list
	public Object visit(ASTParmlist node, Object data) {
		return dump(node, data);
	}
	
	// Function body
	public Object visit(ASTFnBody node, Object data) {
		return dump(node, data);
	}

	// Function return expression
	public Object visit(ASTReturnExpression node, Object data) {
		return dump(node, data);
	}

	// Function definition (pure)
	public Object visit(ASTFnDefPure node, Object data) {
		return dump(node, data);
	}

	// Function definition (impure)
	public Object visit(ASTFnDef node, Object data) {
		return dump(node, data);
	}
	
	// Function argument list
	public Object visit(ASTArgList node, Object data) {
		return dump(node, data);
	}
	
	// Function call
	public Object visit(ASTCall node, Object data) {
		return dump(node, data);
	}
	
	// Function invocation in an expression
	public Object visit(ASTFnInvoke node, Object data) {
		return dump(node, data);
	}
	
	// Dereference a variable, and push its value onto the stack
	public Object visit(ASTDereference node, Object data) {
		return dump(node, data);
	}
	
	// Execute a FOR loop
	public Object visit(ASTForLoop node, Object data) {
		return dump(node, data);
	}
	
	// Process an identifier
	// This doesn't do anything, but needs to be here because we need an ASTIdentifier node.
	public Object visit(ASTIdentifier node, Object data) {
		return dump(node, data);
	}
	
	// Execute the WRITE statement
	public Object visit(ASTWrite node, Object data) {
		return dump(node, data);
	}
	
	// Execute an assignment statement, by popping a value off the stack and assigning it
	// to a variable.
	public Object visit(ASTAssignment node, Object data) {
		return dump(node, data);
	}

	// OR
	public Object visit(ASTOr node, Object data) {
		return dump(node, data);
	}

	// AND
	public Object visit(ASTAnd node, Object data) {
		return dump(node, data);
	}

	// ==
	public Object visit(ASTCompEqual node, Object data) {
		return dump(node, data);
	}

	// !=
	public Object visit(ASTCompNequal node, Object data) {
		return dump(node, data);
	}

	// >=
	public Object visit(ASTCompGTE node, Object data) {
		return dump(node, data);
	}

	// <=
	public Object visit(ASTCompLTE node, Object data) {
		return dump(node, data);
	}

	// >
	public Object visit(ASTCompGT node, Object data) {
		return dump(node, data);
	}

	// <
	public Object visit(ASTCompLT node, Object data) {
		return dump(node, data);
	}

	// +
	public Object visit(ASTAdd node, Object data) {
		return dump(node, data);
	}

	// -
	public Object visit(ASTSubtract node, Object data) {
		return dump(node, data);
	}

	// *
	public Object visit(ASTTimes node, Object data) {
		return dump(node, data);
	}

	// /
	public Object visit(ASTDivide node, Object data) {
		return dump(node, data);
	}

	// NOT
	public Object visit(ASTUnaryNot node, Object data) {
		return dump(node, data);
	}

	// + (unary)
	public Object visit(ASTUnaryPlus node, Object data) {
		return dump(node, data);
	}

	// - (unary)
	public Object visit(ASTUnaryMinus node, Object data) {
		return dump(node, data);
	}

	@Override
	public Object visit(ASTLambdaPure node, Object data) {
		return dump(node, data);
	}

	@Override
	public Object visit(ASTLambdaImpure node, Object data) {
		return dump(node, data);
	}

	// Push integer literal to stack
	public Object visit(ASTInteger node, Object data) {
		return dump(node, data);
	}

	// Push floating point literal to stack
	public Object visit(ASTRational node, Object data) {
		return dump(node, data);
	}

	// Push true literal to stack
	public Object visit(ASTTrue node, Object data) {
		return dump(node, data);
	}

	// Push false literal to stack
	public Object visit(ASTFalse node, Object data) {
		return dump(node, data);
	}

}
