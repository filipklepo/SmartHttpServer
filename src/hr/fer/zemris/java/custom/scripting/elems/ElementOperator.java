package hr.fer.zemris.java.custom.scripting.elems;

/**
 * ElementOperator is Element derivative which represents a operator.
 * 
 * @author Filip Klepo
 *
 */
public class ElementOperator extends Element {

	/**
	 * Symbol of this operator.
	 */
	private String symbol;

	/**
	 * Constructs instance of this class with given symbol as string.
	 * 
	 * @param symbol symbol of this operator
	 */
	public ElementOperator(String symbol) {
		this.symbol = symbol;
	}

	@Override
	public String asText() {
		return symbol;
	}
	
}
