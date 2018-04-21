package hr.fer.zemris.java.custom.scripting.nodes;

import hr.fer.zemris.java.custom.collections.ArrayIndexedCollection;

/**
 * Node is base class node for all graph nodes.
 * 
 * @author Filip Klepo
 *
 */
public abstract class Node {

	/**
	 * Internal collection which holds child Nodes of this Node.
	 */
	private ArrayIndexedCollection collection;
	
	/**
	 * Adds a child Node to this Node.
	 * 
	 * @param child new child Node of this node
	 * @throws IllegalArgumentException if given child Node is null
	 */
	public void addChildNode(Node child) {
		if(collection == null) {
			collection = new ArrayIndexedCollection();
		}
		
		collection.add(child);
	}
	
	/**
	 * Gets number of children stored in this Node.
	 * 
	 * @return number of children stored in this Node
	 */
	public int numberOfChildren() {
		if(collection == null) {
			return 0;
		}
		
		return collection.size();
	}
	
	/**
	 * Gets child of this node at specified index in its internal collection of children.
	 * 
	 * @param index index of child
	 * @throws IndexOutOfBoundsException if index is not in 0 to (size - 1) range
	 * @return Node from specified index
	 */
	public Node getChild(int index) {
		return (Node) collection.get(index);
	}
	
	/**
	 * Accepts given node visitor.
	 * 
	 * @param visitor node visitor
	 */
	public abstract void accept(INodeVisitor visitor);
	
}
