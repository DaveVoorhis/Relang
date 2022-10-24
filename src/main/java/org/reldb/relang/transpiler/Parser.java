package org.reldb.relang.transpiler;

import java.util.Vector;

import org.reldb.relang.exceptions.ExceptionSemantic;
import org.reldb.relang.parser.ast.*;

import static org.reldb.relang.transpiler.Formatter.indent;

public class Parser implements RelangVisitor {
	public static final String generatedCodeClassName = "RelangGenerated";
	public static final String generatedCodeMainMethodName = "__main";

	// Reference to current operator definition.
	private OperatorDefinition currentOperatorDefinition = null;

	private void checkSlotDefined(String name, Node node) {
		if (currentOperatorDefinition.isSlotDefined(name))
			throw new ExceptionSemantic(name + " is already defined in operator " + currentOperatorDefinition.getSignature(), node);		
	}
	
	private void beginOperatorDefinition(String fnname, Node node) {
		// Begin operator definition nested inside currentOperatorDefinition.
		var parent = currentOperatorDefinition;
		currentOperatorDefinition = new OperatorDefinition(fnname, currentOperatorDefinition);
		if (parent != null) {
			if (parent.isOperatorDefined(currentOperatorDefinition.getSignature()))
				throw new ExceptionSemantic(fnname + " is already defined in operator " + parent.getSignature(), node);
			parent.addOperator(currentOperatorDefinition);
		}
	}
	
	public void endOperatorDefinition() {
		// Finish nested operator, i.e., restore parent operator definition context.
		currentOperatorDefinition = currentOperatorDefinition.getParentOperatorDefinition();
	}

	// Return the number of children of the given node
	private static int getChildCount(SimpleNode node) {
		return node.jjtGetNumChildren();
	}
	
	// Compile a given child of the given node and return the resulting Java source.
	private Object compileChild(SimpleNode node, int childIndex, Object data) {
		return node.jjtGetChild(childIndex).jjtAccept(this, data);
	}
	
	// Compile all children of the given node, and return the resulting Java source.
	private Object compileChildren(SimpleNode node, Object data) {
		var out = new StringBuilder();
		for (var i = 0; i < getChildCount(node); i++) {
			Object compileResult = compileChild(node, i, data);
			if (compileResult != null)
				out.append(compileResult);
		}
		return out.toString();
	}
	
	// Get the ith child as a BaseASTNode
	private static BaseASTNode getChild(SimpleNode node, int childIndex) {
		return (BaseASTNode)node.jjtGetChild(childIndex);
	}
	
	// Get the token of the ith child.
	private static String getTokenOfChild(SimpleNode node, int childIndex) {
		return getChild(node, childIndex).tokenValue;
	}
	
	public Object visit(SimpleNode node, Object data) {
		System.out.println(node + ": acceptor not implemented in subclass?");
		return data;
	}
	
	// Transpile (to Java) a Relang program.
	public Object visit(ASTCode node, Object data) {
		beginOperatorDefinition(generatedCodeMainMethodName, node);
		var mainOperatorDefinition = currentOperatorDefinition;
		currentOperatorDefinition.addSource(compileChildren(node, data).toString());
		endOperatorDefinition();
		return 
			"public class " + generatedCodeClassName + " {\n" +
			indent(mainOperatorDefinition.getSource()) +
			"}\n";
	}
	
	// Compile a statement
	public Object visit(ASTStatement node, Object data) {
		return compileChildren(node, data);
	}

	// Compile a block
	public Object visit(ASTBlock node, Object data) {
		return compileChildren(node, data);	
	}

	// Function definition (pure)
	public Object visit(ASTFnDefPure node, Object data) {
		// Child 0 - identifier (fn name)
		var fnname = getTokenOfChild(node, 0);
		beginOperatorDefinition(fnname, node);
		// Child 1 - function definition parameter list
		compileChild(node, 1, data);
		// Child 2 - return expression
		var returnExpression = (Value)compileChild(node, 2, null);
		currentOperatorDefinition.setReturn(returnExpression);
		currentOperatorDefinition.addSource(returnExpression.toString());
		endOperatorDefinition();
		return data;
	}

	// Function definition (impure)
	public Object visit(ASTFnDef node, Object data) {
		// Child 0 - identifier (fn name)
		var fnname = getTokenOfChild(node, 0);
		beginOperatorDefinition(fnname, node);
		// Child 1 - function definition parameter list
		compileChild(node, 1, data);
		// Child 2 - function body
		currentOperatorDefinition.addSource(compileChild(node, 2, data).toString());
		// optional Child 3 - return expression
		if (getChildCount(node) == 4) {
			var returnExpression = (Value)compileChild(node, 3, null);
			currentOperatorDefinition.setReturn(returnExpression);
			currentOperatorDefinition.addSource(returnExpression.toString());
		}
		endOperatorDefinition();
		return data;
	}

	// Function definition parameter def: type name
	public Object visit(ASTParameter node, Object data) {
		var parameterTypeName = getTokenOfChild(node, 0);
		var parameterName = getTokenOfChild(node, 1);
		return new Parameter(parameterTypeName, parameterName);
	}

	// Function definition parameter list.
	public Object visit(ASTParmlist node, Object data) {
		for (var i=0; i<getChildCount(node); i++) {
			var parameter = (Parameter)compileChild(node, i, data);
			checkSlotDefined(parameter.getExpression(), node);
			currentOperatorDefinition.addParameter(parameter);
		}
		return data;
	}
	
	// Function body
	public Object visit(ASTFnBody node, Object data) {
		return compileChildren(node, data);
	}
	
	// Function return expression
	public Object visit(ASTReturnExpression node, Object data) {
		var returnExpression = (Value)compileChild(node, 0, data);
		return new Value(returnExpression.getTypeName(), "return " + returnExpression + ";\n");
	}
	
	// Function invocation argument list. Return as Vector<String> of argument expression source text.
	public Object visit(ASTArgList node, Object data) {
		var argumentSource = new Vector<String>();
		// Compile arguments
		for (var i=0; i < node.jjtGetNumChildren(); i++)
			argumentSource.add(compileChild(node, i, data).toString());
		return argumentSource;
	}
	
	private Value fnInvoke(SimpleNode node) {
		// Child 0 - identifier (fn name)
		var fnname = getTokenOfChild(node, 0);
		// Child 1 - arglist
		@SuppressWarnings("unchecked")
		var arglist = (Vector<String>)compileChild(node, 1, null);
		var opInvoke = currentOperatorDefinition.findInvocation(fnname, arglist);
		if (opInvoke == null)
			throw new ExceptionSemantic("Can't find operator " + fnname, node);	
		return opInvoke;
	}
	
	// Function call
	public Object visit(ASTCall node, Object data) {
		var invocation = fnInvoke(node);
		return new Value(invocation.getTypeName(), invocation.getExpression() + ";\n");
	}
	
	// Function invocation in an expression
	public Object visit(ASTFnInvoke node, Object data) {
		return fnInvoke(node);
	}

	// Compile an IF 
	public Object visit(ASTIfStatement node, Object data) {
		return "if (" + compileChild(node, 0, data) + ") {\n" +
				indent(compileChild(node, 1, data)) +
				"} " + ((node.ifHasElse) ? "else {\n" + indent(compileChild(node, 2, data)) : "") +
				"}\n";
	}
	
	// Compile a FOR loop
	public Object visit(ASTForLoop node, Object data) {
		return "for (" + 
				compileChild(node, 0, Boolean.TRUE) + "; " + 
				"(" + compileChild(node, 1, data) + "); " +
				compileChild(node, 2, Boolean.TRUE) + ") {\n" +
				indent(compileChild(node, 3, data)) +
				"}\n";
	}
	
	// Process an identifier
	// This doesn't do anything, but needs to be here because we need an ASTIdentifier node.
	public Object visit(ASTIdentifier node, Object data) {
		return data;
	}
	
	// Compile the WRITE statement
	public Object visit(ASTWrite node, Object data) {
		return "System.out.println(" + compileChild(node, 0, data) + ");\n";
	}
	
	// Compile dereference of a variable or parameter
	public Object visit(ASTDereference node, Object data) {
		var refname = node.tokenValue;
		var deref = currentOperatorDefinition.findReference(refname);
		if (deref == null)
			throw new ExceptionSemantic("ERROR: Variable " + refname + " has not been initialised.", node);
		return deref;
	}
	
	// Compile an assignment statement; evaluate a value and assign it to a variable or parameter.
	public Object visit(ASTAssignment node, Object data) {
		// if data is a true Boolean, do not emit the semicolon because we're the last assignment of a for loop
		var emitSemicolon = !(data instanceof Boolean && (Boolean) data);
		var targetName = getTokenOfChild(node, 0);
		var source = (Value)compileChild(node, 1, data);
		var target = currentOperatorDefinition.findReference(targetName);
		if (target == null) {
			target = currentOperatorDefinition.createVariable(source.getTypeName(), targetName);
		} else {
			// TODO verify type of source is compatible with target
		}
		return target + " = " + source + ((emitSemicolon) ? ";\n" : "");
	}

	// OR
	public Object visit(ASTOr node, Object data) {
		return binary(node, data, "||", "boolean");
	}

	// AND
	public Object visit(ASTAnd node, Object data) {
		return binary(node, data, "&&", "boolean");
	}

	// ==
	public Object visit(ASTCompEqual node, Object data) {
		return binary(node, data, "==", "boolean");
	}

	// !=
	public Object visit(ASTCompNequal node, Object data) {
		return binary(node, data, "!=", "boolean");
	}

	// >=
	public Object visit(ASTCompGTE node, Object data) {
		return binary(node, data, ">=", "boolean");
	}

	// <=
	public Object visit(ASTCompLTE node, Object data) {
		return binary(node, data, "<=", "boolean");
	}

	// >
	public Object visit(ASTCompGT node, Object data) {
		return binary(node, data, ">", "boolean");
	}

	// <
	public Object visit(ASTCompLT node, Object data) {
		return binary(node, data, "<", "boolean");
	}

	// +
	public Object visit(ASTAdd node, Object data) {
		return binary(node, data, "+");
	}

	// -
	public Object visit(ASTSubtract node, Object data) {
		return binary(node, data, "-");
	}

	// *
	public Object visit(ASTTimes node, Object data) {
		return binary(node, data, "*");
	}

	// /
	public Object visit(ASTDivide node, Object data) {
		return binary(node, data, "/");
	}

	private Value binary(SimpleNode node, Object data, String operator) {
		var operand0 = (Value)compileChild(node, 0, data);
		var operand1 = (Value)compileChild(node, 1, data);
		var type = operand0.getTypeName();
		return new Value(type, "(" + operand0 + ") " + operator + " (" + operand1 + ")");
	}

	private Value binary(SimpleNode node, Object data, String operator, String type) {
		var operand0 = (Value)compileChild(node, 0, data);
		var operand1 = (Value)compileChild(node, 1, data);
		return new Value(type, "(" + operand0 + ") " + operator + " (" + operand1 + ")");
	}

	// NOT
	public Object visit(ASTUnaryNot node, Object data) {
		return unary(node, data, "!");
	}

	// + (unary)
	public Object visit(ASTUnaryPlus node, Object data) {
		return unary(node, data, "+");
	}

	// - (unary)
	public Object visit(ASTUnaryMinus node, Object data) {
		return unary(node, data, "-");
	}

	private Value unary(SimpleNode node, Object data, String operator) {
		var operand = (Value)compileChild(node, 0, data);
		return new Value(operand.getTypeName(), operator + "(" + operand + ")");
	}

	// integer literal
	public Object visit(ASTInteger node, Object data) {
		return new Value("long", node.tokenValue);
	}

	// floating point literal
	public Object visit(ASTRational node, Object data) {
		return new Value("double", node.tokenValue);
	}

	// true literal
	public Object visit(ASTTrue node, Object data) {
		return new Value("boolean", "true");
	}

	// false literal
	public Object visit(ASTFalse node, Object data) {
		return new Value("boolean", "false");
	}
}
