package hr.fer.zemris.java.custom.scripting.lexer;

/**
 * Contains possible SmartScriptToken states.
 * 
 * @author Filip Klepo
 *
 */
public enum SmartScriptTokenType {

	/**
	 * Part of input text outside {$ and $} brackets.
	 */
	TEXT,
	
	/**
	 * Part of input text between {$ and $} brackets.
	 */
	TAG,
	
	/**
	 * No more input data to parse.
	 */
	EOF
	
}
