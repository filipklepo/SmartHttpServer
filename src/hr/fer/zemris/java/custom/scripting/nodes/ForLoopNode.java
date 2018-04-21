package hr.fer.zemris.java.custom.scripting.nodes;

import hr.fer.zemris.java.custom.scripting.elems.Element;
import hr.fer.zemris.java.custom.scripting.elems.ElementVariable;

/**
 * ForLoopNode is Node derivative representing a single for-loop construct.
 * 
 * @author Filip Klepo
 *
 */
public class ForLoopNode extends Node {
	
	/**
	 * Variable of this for-loop node.
	 */
	private ElementVariable variable;
	/**
	 * Start expression of this for-loop node.
	 */
	private Element startExpression;
	/**
	 * End expression of this for-loop node.
	 */
	private Element endExpression;
	/**
	 * Step expression of this for-loop node.
	 */
	private Element stepExpression;
	
	/**
	 * Constructs instance of ForLoopNode instance with given parameters.
	 * 
	 * @param variable variable of this for-loop node
	 * @param startExpression start expression of this for-loop node
	 * @param endExpression end expression of this for-loop node
	 * @param stepExpression step expression of this for-loop node
	 */
	public ForLoopNode(ElementVariable variable, Element startExpression, 
			Element endExpression, Element stepExpression) {
		this.variable = variable;
		this.startExpression = startExpression;
		this.endExpression = endExpression;
		this.stepExpression = stepExpression;
	}

	/**
	 * Gets variable of this for-loop node.
	 * 
	 * @return variable of this for-loop node
	 */
	public ElementVariable getVariable() {
		return variable;
	}

	/**
	 * Gets start expression of this for-loop node.
	 * 
	 * @return start expression of this for-loop node
	 */
	public Element getStartExpression() {
		return startExpression;
	}

	/**
	 * Gets end expression of this for-loop node.
	 * 
	 * @return end expression of this for-loop node
	 */
	public Element getEndExpression() {
		return endExpression;
	}

	/**
	 * Gets step expression of this for-loop node.
	 * 
	 * @return step expression of this for-loop node
	 */
	public Element getStepExpression() {
		return stepExpression;
	}
	
	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		
		sb.append("{$ for ");
		sb.append(variable.asText() + " ");
		sb.append(startExpression.asText() + " ");
		sb.append(endExpression.asText() + " ");
		
		if(stepExpression != null) {
			sb.append(stepExpression.asText() + " ");
		}
		
		sb.append("$}");
		
		for(int i = 0; i < this.numberOfChildren(); ++i) {
			sb.append(this.getChild(i).toString());
		}
		
		if(sb.charAt(sb.length() - 1) != '\n') {
			sb.append(System.lineSeparator());
		}
		
		sb.append("{$END$}");
		
		return sb.toString();
	}

	@Override
	public void accept(INodeVisitor visitor) {
		visitor.visitForLoopNode(this);
	}
	
}
