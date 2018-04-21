package hr.fer.zemris.java.custom.scripting.nodes;

/**
 * TextNode is Node derivative representing a piece of textual data.
 * 
 * @author Filip Klepo
 *
 */
public class TextNode extends Node {

	/**
	 * Value of this TextNode.
	 */
	private String text;
	
	/**
	 * Constructs instance of this class with given text.
	 * 
	 * @param text value of new instance of this class
	 */
	public TextNode(String text) {
		this.text = text;
	}
	
	@Override
	public String toString() {
		return text.replace("{$", "\\{$");
	}

	@Override
	public void accept(INodeVisitor visitor) {
		visitor.visitTextNode(this);
	}
}
