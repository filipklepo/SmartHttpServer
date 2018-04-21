package hr.fer.zemris.java.custom.scripting.elems;

/**
 * ElementConstantDouble is Element derivative which represents a double
 * number.
 * 
 * @author Filip Klepo
 *
 */
public class ElementConstantDouble extends Element {

	/**
	 * Value of this number.
	 */
	private double value;
	
	/**
	 * Constructs instance of this class with given value.
	 * 
	 * @param value value of new instance of this class
	 */
	public ElementConstantDouble(double value) {
		this.value = value;
	}

	@Override
	public String asText() {
		return String.valueOf(value);
	}
	
}
