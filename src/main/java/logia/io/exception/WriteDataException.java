package logia.io.exception;

/**
 * The Class WriteDataException.
 *
 * @author Paul Mai
 */
public class WriteDataException extends Exception {

	/** The message. */
	private static String     message          = "Send data error";

	/** The Constant serialVersionUID. */
	private static final long serialVersionUID = 1L;

	/**
	 * Instantiates a new write data exception.
	 */
	public WriteDataException() {
		super(WriteDataException.message);
	}

	/**
	 * Instantiates a new write data exception.
	 *
	 * @param __cause the __cause
	 */
	public WriteDataException(Throwable __cause) {
		super(WriteDataException.message, __cause);
	}

}
