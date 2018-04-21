package hr.fer.zemris.java.custom.scripting.nodes;

/**
 * DocumentNode is Node which represents a document model.
 * 
 * @author Filip Klepo
 *
 */
public class DocumentNode extends Node {

	@Override
	public void accept(INodeVisitor visitor) {
		visitor.visitDocumentNode(this);
	}
	
	
}
