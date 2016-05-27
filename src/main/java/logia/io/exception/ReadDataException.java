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
	 * @param __message the __message
	 */
	public ReadDataException(String __message) {
		super(__message);
	}

	/**
	 * Instantiates a new read data exception.
	 *
	 * @param __cause the __cause
	 */
	public ReadDataException(Throwable __cause) {
		super(__cause.getMessage(), __cause);
	}

}
