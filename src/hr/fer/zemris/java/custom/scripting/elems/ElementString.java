package hr.fer.zemris.java.custom.scripting.elems;

/**
 * ElementString is Element derivative which represents a string.
 * 
 * @author Filip Klepo
 *
 */
public class ElementString extends Element {

	/**
	 * Value of this string.
	 */
	private String value;

	/**
	 * Constructs instance of this class with given string.
	 * 
	 * @param value value of new instance of this class
	 */
	public ElementString(String value) {
		this.value = value;
	}

	@Override
	public String asText() {
		return value;
	}
	
}
