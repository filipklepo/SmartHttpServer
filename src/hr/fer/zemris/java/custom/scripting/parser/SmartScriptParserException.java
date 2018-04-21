package hr.fer.zemris.java.custom.scripting.parser;

/**
 * SmartScriptException is exception derived from RuntimeException which is thrown
 * when error has occurred in process of parsing.
 * 
 * @author Filip Klepo
 *
 */
public class SmartScriptParserException extends RuntimeException {

	/**
	 * Default serial version UID.
	 */
	private static final long serialVersionUID = 1L;

	/**
	 * The default constructor.
	 */
	public SmartScriptParserException() {
		super();
	}

	/**
	 * Constructs instance of this exception with given error message.
	 * 
	 * @param arg0 error message
	 */
	public SmartScriptParserException(String arg0) {
		super(arg0);
	}

}
