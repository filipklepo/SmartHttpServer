package hr.fer.zemris.java.custom.scripting.elems;

/**
 * ElementVariable is Element derivative which represents a variable.
 * 
 * @author Filip Klepo
 *
 */
public class ElementVariable extends Element {

	/**
	 * Name of this variable.
	 */
	private String name;
	
	/**
	 * Constructs instance of this class with given variable name.
	 * 
	 * @param name name of new ElementVariable
	 */
	public ElementVariable(String name) {
		this.name = name;
	}
	
	@Override
	public String asText() {
		return name;
	}
	
}
