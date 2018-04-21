package hr.fer.zemris.java.custom.scripting.lexer;

/**
 * Contains all possible states of SmartScriptLexer.
 * 
 * @author Filip Klepo
 *
 */
public enum LexerState {
	
	/**
	 * Initial state from which Lexer automatically
	 * switches to one of other two states depending on the
	 * opening characters of input text.
	 */
	INIT,
	
	/**
	 * State in which input text is read outside
	 * the {$ and $} brackets.
	 */
	TEXT,
	
	/**
	 * State in which input text is read inside
	 * the {$ and $} brackets.
	 */
	TAG
	
}
