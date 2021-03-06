package logia.io.exception;

/**
 * The Class ConnectionErrorException.
 *
 * @author Paul Mai
 */
public class ConnectionErrorException extends Exception {

	/** The message. */
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
	 * @param __cause the __cause
	 */
	public ConnectionErrorException(Throwable __cause) {
		super(ConnectionErrorException.message, __cause);
	}

}
