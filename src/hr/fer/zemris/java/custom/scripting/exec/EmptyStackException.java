package hr.fer.zemris.java.custom.scripting.exec;

/**
 * EmptyStackException is exception which is thrown when stack is empty.
 * 
 * @author Filip Klepo
 *
 */
public class EmptyStackException extends RuntimeException {

	/**
	 * Default UID.
	 */
	private static final long serialVersionUID = 1L;

	/**
	 * The defualt constructor.
	 */
	public EmptyStackException() {
		super();
	}

	/**
	 * Instantiates EmptyStackException with given error message.
	 * 
	 * @param message detailed error message
	 */
	public EmptyStackException(String message) {
		super(message);
	}
	
}
