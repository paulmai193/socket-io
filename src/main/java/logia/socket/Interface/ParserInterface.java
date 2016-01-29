package logia.socket.Interface;

import java.io.IOException;
import java.io.OutputStream;
import java.net.SocketException;
import java.net.SocketTimeoutException;

/**
 * The Interface ParserInterface.
 * 
 * @author Paul Mai
 */
public interface ParserInterface {

	/**
	 * Apply stream from instance of SocketInterface. Read and parse data from stream into instance of ReadDataInterface and execute this data after
	 * reading.
	 *
	 * @param __clientSocket the client socket
	 * @throws SocketTimeoutException the socket timeout exception
	 * @throws SocketException the socket exception
	 * @throws IOException Signals that an I/O exception has occurred.
	 * @throws Exception the exception
	 */
	public void applyInputStream(SocketClientInterface __clientSocket) throws SocketTimeoutException, SocketException, IOException, Exception;

	/**
	 * Apply stream. Get and write data from instance of WriteDataInterface into stream
	 *
	 * @param __outputStream the output stream
	 * @param __dataListener the data listener
	 * @param __command the command
	 * @throws Exception the exception
	 */
	public void applyOutputStream(OutputStream __outputStream, WriteDataInterface __dataListener, Object __command) throws Exception;
}
