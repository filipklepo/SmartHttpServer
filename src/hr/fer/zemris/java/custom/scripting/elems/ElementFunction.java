package hr.fer.zemris.java.custom.scripting.elems;

/**
 * ElementFunction is Element derivative which represents a function.
 * 
 * @author Filip Klepo
 *
 */
public class ElementFunction extends Element {
	
	/**
	 * Name of this function.
	 */
	private String name;

	/**
	 * Constructs instance of this class with given function name.
	 * 
	 * @param name name of function
	 */
	public ElementFunction(String name) {
		this.name = name;
	}

	@Override
	public String asText() {
		return name;
	}
	
}
