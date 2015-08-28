package logia.io.exception;

/**
 * The Class ReadDataException.
 * 
 * @author Paul Mai
 */
public class ReadDataException extends Exception {

	/** The message. */
	private static String     message          = "Receive data error";

	/** The Constant serialVersionUID. */
	private static final long serialVersionUID = 1L;

	/**
	 * Instantiates a new read data exception.
	 */
	public ReadDataException() {
		super(ReadDataException.message);
	}

	/**
	 * Instantiates a new read data exception.
	 *
	 * @param cause the cause
	 */
	public ReadDataException(Throwable cause) {
		super(ReadDataException.message, cause);
	}

}
