package org.reldb.relang.transpiler;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Vector;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.reldb.relang.exceptions.ExceptionFatal;

import static org.reldb.relang.transpiler.Formatter.indent;

/** This class captures information about the operator currently being defined, including its generated code. */
class OperatorDefinition {
	
	private final OperatorDefinition parent;
	private final String name;
	private final HashMap<String, OperatorDefinition> operators = new HashMap<>();
	private final Map<String, Slot> slots = new HashMap<>();
	private final Vector<Parameter> parameters = new Vector<>();

	private Value returnValue;
	private String bodySource = "";
	
	private String getParmDecls() {
		var firstParameterType = (parent != null) ? parent.getSignature() + "_closure" : null;
		var firstParameter = (firstParameterType == null) ? "" : firstParameterType + " __closure";
		var parmlist =
				Stream.concat(
						Stream.of(firstParameter), 
						parameters.stream()
								.map(parm -> parm.getTypeName() + " " + parm.getExpression()))
				.collect(Collectors.joining(", "));
		return "(" + parmlist + ")";
	}
	
	private String getNestedOperatorSource() {
		return operators.values().stream()
				.map(OperatorDefinition::getSource)
				.collect(Collectors.joining());
	}
	
	private String getClosureClassName() {
		return name + "_closure";
	}
	
	private String getClosureDef() {
		var vardefs = new StringBuilder();
		var ctorBody = new StringBuilder();
		var ctorParmDef = new StringBuilder();
		if (parent != null) {
			vardefs.append("\t").append(parent.getSignature()).append("_closure __closure;\n");
			ctorBody.append("\tthis.__closure = __closure;\n");
			ctorParmDef.append(parent.getSignature()).append("_closure __closure");
		}
		for (var slot: slots.values()) {
			vardefs.append("\t").append(slot.getTypeName()).append(" ").append(slot.getExpression()).append(";\n");
			ctorBody.append("\tthis.").append(slot.getExpression()).append(" = ").append(slot.getExpression()).append(";\n");
			if (ctorParmDef.length() > 0)
				ctorParmDef.append(", ");
			ctorParmDef.append(slot.getTypeName()).append(" ").append(slot.getExpression());
		}
		var closureClassName = getClosureClassName();
		return "static class " + closureClassName + " {\n" + 
				vardefs + 
				indent("public " + closureClassName + "(" + ctorParmDef + ") {\n" + ctorBody + "}\n") +
				"}\n";
	}

	private String getClosureConstruction() {
		var slotNames =
				Stream.concat(
						Stream.of("__closure")
								.filter(p -> parent != null),
						slots.values().stream()
								.map(Slot::getExpression))
				.collect(Collectors.joining(", "));
		return "new " + getClosureClassName() + "(" + slotNames + ")";
	}
	
	private String getVarDefs() {
		return slots.values().stream()
				.filter(slot -> slot instanceof Variable)
				.map(slot -> slot.getTypeName() + " " + slot.getExpression() + ";\n")
				.collect(Collectors.joining());
	}
	
	private String getComment() {
		var content = new StringBuilder();
		var opDef = this;
		do {
			if (content.length() > 0)
				content.append(" in ");
			content.append(opDef.getSignature());
			opDef = opDef.getParentOperatorDefinition();
		} while (opDef != null);
		return "/** " + content + " */\n\n";
	}
	
	private void checkSlotDefined(String refname) {
		if (isSlotDefined(refname))
			throw new ExceptionFatal("ERROR: " + refname + " is already defined in " + getSignature());
	}

	/** Ctor for operator definition. */
	OperatorDefinition(String name, OperatorDefinition parent) {
		this.name = name;
		this.parent = parent;
	}
	
	/** Add a nested operator to this operator. */
	void addOperator(OperatorDefinition definition) {
		var signature = definition.getSignature();
		if (isOperatorDefined(signature))
			throw new ExceptionFatal("ERROR: Operator " + signature + " is already defined.");
		operators.put(signature, definition);
	}
	
	/** Return true if an operator exists within this operator. */
	boolean isOperatorDefined(String signature) {
		return (operators.containsKey(signature));
	}
	
	/** Get the signature of this operator. */
	String getSignature() {
		return name;
	}
	
	/** Return true if a variable, parameter, or slot exists. */
	boolean isSlotDefined(String name) {
		return slots.containsKey(name);
	}

	/** Identify whether this operator returns a value or not. */
	void setReturn(Value returnValue) {
		this.returnValue = returnValue;
	}
	
	/** Get parent operator definition.  Null if this is the root operator. */
	OperatorDefinition getParentOperatorDefinition() {
		return parent;
	}

	/** Create a variable. */
	Slot createVariable(String typeName, String refname) {
		checkSlotDefined(refname);
		var variable = new Variable(typeName, refname);
		slots.put(refname, variable);
		return variable;
	}

	/** Add a parameter */
	void addParameter(Parameter parameter) {
		var parameterName = parameter.getExpression();
		checkSlotDefined(parameterName);
		slots.put(parameterName, parameter);
		parameters.add(parameter);
	}
	
	/** Get variable/parameter dereference Java code given name. */
	Slot findReference(String refname) {
		var outRef = new StringBuilder(refname);
		var opDef = this;
		do {
			Slot slot = opDef.slots.get(refname);
			if (slot != null)
				return new Slot(slot.getTypeName(), outRef.toString());
			opDef = opDef.parent;
			outRef.insert(0, "__closure.");
		} while (opDef != null);
		return null;
	}
	
	/** Get function invocation Java code given function name and argument list. */
	Value findInvocation(String fnname, Vector<String> arglist) {
		OperatorDefinition foundOperator;
		var opDef = this;
		var nesting = 0;
		do {
			foundOperator = opDef.operators.get(fnname);
			if (foundOperator != null)
				break;
			opDef = opDef.getParentOperatorDefinition();
			nesting++;
		} while (opDef != null);
		if (foundOperator == null)
			return null;		
		var firstArg = nesting > 0
				? String.join(".", Collections.nCopies(nesting, "__closure"))
				: getClosureConstruction();
		var arglistText = Stream.concat(
				Stream.of(firstArg),
				arglist.stream())
					.collect(Collectors.joining(", "));
		var returnTypeName = foundOperator.returnValue != null
				? foundOperator.returnValue.getTypeName()
				: null;
		return new Value(returnTypeName,fnname + "(" + arglistText + ")");
	}
	
	/** Add Java source code to this definition. */
	void addSource(String source) {
		bodySource += source;
	}

	/** Get the Java source code for this definition. */
	String getSource() {
		return	getNestedOperatorSource() + 
				"\n" +
			 	getComment() +
				getClosureDef() +
			 	"\npublic static " + ((returnValue != null) ? returnValue.getTypeName() + " " : "void ") + name + getParmDecls() + " {\n" +
				indent(getVarDefs() + bodySource) + 
				"}\n";
	}
}
