package hr.fer.zemris.java.custom.scripting.nodes;

/**
 * Node visitor is a interface which models the way how {@link Node} objects 
 * should be handled, depending on their type.
 * 
 * @author Filip Klepo
 *
 */
public interface INodeVisitor {

	/**
	 * Visits the text node.
	 * 
	 * @param node text node
	 */
	public void visitTextNode(TextNode node);
	/**
	 * Visits the for-loop node,
	 * 
	 * @param node for-loop node
	 */
	public void visitForLoopNode(ForLoopNode node);
	/**
	 * Visits the echo node.
	 * 
	 * @param node echo node
	 */
	public void visitEchoNode(EchoNode node);
	/**
	 * Visits the document node.
	 * 
	 * @param node document node
	 */
	public void visitDocumentNode(DocumentNode node);
	
}
