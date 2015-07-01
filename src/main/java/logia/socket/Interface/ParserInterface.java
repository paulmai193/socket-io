package logia.socket.Interface;

import java.io.IOException;
import java.io.InputStream;
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
	 * Apply data from stream. Read parse data from stream into instance of ReadDataInterface and execute this data after reading.
	 *
	 * @param inputstream the inputstream
	 * @throws IOException Signals that an I/O exception has occurred.
	 * @throws Exception the exception
	 */
	public void applyInputStream(InputStream inputstream) throws IOException, Exception;

	/**
	 * Apply stream from instance of SocketInterface. Read and parse data from stream into instance of ReadDataInterface and execute this data after
	 * reading.
	 *
	 * @param clientSocket the client socket
	 * @throws SocketTimeoutException the socket timeout exception
	 * @throws SocketException the socket exception
	 * @throws IOException Signals that an I/O exception has occurred.
	 * @throws Exception the exception
	 */
	public void applyInputStream(SocketClientInterface clientSocket) throws SocketTimeoutException, SocketException, IOException, Exception;

	/**
	 * Apply stream. Get and write data from instance of WriteDataInterface into stream
	 *
	 * @param outputStream the output stream
	 * @param dataListener the data listener
	 * @param command the command
	 * @throws Exception the exception
	 */
	public void applyOutputStream(OutputStream outputStream, WriteDataInterface dataListener, int command) throws Exception;
}
