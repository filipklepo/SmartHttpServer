package hr.fer.zemris.java.custom.scripting.lexer;

/**
 * SmartScriptToken represents a token used by SmartScriptLexer.
 * A token is a structure representing a lexeme that explicitly indicates 
 * its categorization for the purpose of parsing.
 * 
 * @author Filip Klepo
 *
 */
public class SmartScriptToken {

	/**
	 * Value of this token.
	 */
	private String value;
	/**
	 * State of this token.
	 */
	private SmartScriptTokenType type;
	
	/**
	 * Constructs instance of {@link SmartScriptToken} with given value and type.
	 * 
	 * @param value value of new token
	 * @param type type of new token
	 */
	public SmartScriptToken(String value, SmartScriptTokenType type) {
		this.value = value;
		this.type = type;
	}

	/**
	 * Gets value of this token.
	 * 
	 * @return value of this token
	 */
	public String getValue() {
		return value;
	}

	/**
	 * Gets type of this token.
	 * 
	 * @return type of this token
	 */
	public SmartScriptTokenType getType() {
		return type;
	}
	
}
