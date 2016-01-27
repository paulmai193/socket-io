package logia.io.exception;

/**
 * The Class ConnectionErrorException.
 * 
 * @author Paul Mai
 */
public class ConnectionErrorException extends Exception {

	private static String     message          = "Connection not reachable";

	/** The Constant serialVersionUID. */
	private static final long serialVersionUID = 1L;

	/**
	 * Instantiates a new connection error exception.
	 */
	public ConnectionErrorException() {
		super(ConnectionErrorException.message);
	}

	/**
	 * Instantiates a new connection error exception.
	 *
	 * @param cause the cause
	 */
	public ConnectionErrorException(Throwable cause) {
		super(ConnectionErrorException.message, cause);
	}

}