package logia.io.exception;

import java.io.IOException;

/**
 * The Class FileTooLargeException.
 *
 * @author Paul Mai
 */
public class FileTooLargeException extends IOException {

	/** The Constant serialVersionUID. */
	private static final long serialVersionUID = 1L;

	/**
	 * Instantiates a new file too large exception.
	 */
	public FileTooLargeException() {
		super("File too large");
	}

	/**
	 * Instantiates a new file too large exception.
	 *
	 * @param __message the __message
	 */
	public FileTooLargeException(String __message) {
		super(__message);
	}

}
