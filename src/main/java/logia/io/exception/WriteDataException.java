package logia.io.exception;

/**
 * The Class WriteDataException.
 * 
 * @author Paul Mai
 */
public class WriteDataException extends Exception {

	/** The Constant serialVersionUID. */
	private static final long serialVersionUID = 1L;

	/** The message. */
	private static String     message          = "Send data error";

	/**
	 * Instantiates a new write data exception.
	 */
	public WriteDataException() {
		super(message);
	}

	/**
	 * Instantiates a new write data exception.
	 *
	 * @param cause the cause
	 */
	public WriteDataException(Throwable cause) {
		super(message, cause);
	}

}
