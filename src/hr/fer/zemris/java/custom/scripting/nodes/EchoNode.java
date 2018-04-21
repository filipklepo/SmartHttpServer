package hr.fer.zemris.java.custom.scripting.nodes;

import hr.fer.zemris.java.custom.scripting.elems.Element;

/**
 * A Node derivative representing a command which generates some textual output dynamically.
 * 
 * @author Filip Klepo
 *
 */
public class EchoNode extends Node {

	/**
	 * Internal Element array which holds elements of this EchoNode.
	 */
	private Element[] elements;
	
	/**
	 * Constructs instance of EchoNode with given elements.
	 * 
	 * @param elements Element array which holds elements of new instance of this class
	 */
	public EchoNode(Object[] elements) {
		this.elements = new Element[elements.length];
		
		for(int i = 0; i < elements.length; ++i) {
			this.elements[i] = (Element) elements[i];
		}
	}
	
	/**
	 * Gets elements of this EchoNode.
	 * 
	 * @return elements of this EchoNode
	 */
	public Element[] getElements() {
		return elements;
	}
	
	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append("{$= ");
		
		for(Element element : elements) {
			sb.append(element.asText() + " ");
		}
		
		sb.append("$}");
		
		return sb.toString();
	}

	@Override
	public void accept(INodeVisitor visitor) {
		visitor.visitEchoNode(this);
	}
	
}
