package hr.fer.zemris.java.custom.scripting.elems;

/**
 * ElementConstantInteger is Element derivative which represents a integer
 * number.
 * 
 * @author Filip Klepo
 *
 */
public class ElementConstantInteger extends Element {

	/**
	 * Value of this number.
	 */
	private int value;
	
	/**
	 * Constructs instance of this class with given value.
	 * 
	 * @param value value of new instance of this class
	 */
	public ElementConstantInteger(int value) {
		this.value = value;
	}

	@Override
	public String asText() {
		return String.valueOf(value);
	}
	
}
