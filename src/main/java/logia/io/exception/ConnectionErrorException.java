package logia.io.exception;

/**
 * The Class ConnectionErrorException.
 * 
 * @author Paul Mai
 */
public class ConnectionErrorException extends Exception {

	/** The Constant serialVersionUID. */
	private static final long serialVersionUID = 1L;

	private static String     message          = "Connection not reachable";

	/**
	 * Instantiates a new connection error exception.
	 */
	public ConnectionErrorException() {
		super(message);
	}

	/**
	 * Instantiates a new connection error exception.
	 *
	 * @param cause the cause
	 */
	public ConnectionErrorException(Throwable cause) {
		super(message, cause);
	}

}
