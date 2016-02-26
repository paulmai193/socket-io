package logia.socket.Interface;

import java.io.OutputStream;

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
	 */
	public void applyInputStream(SocketClientInterface __clientSocket);

	/**
	 * Apply output stream.
	 *
	 * @param __outputStream the __output stream
	 * @param __writeData the __write data
	 * @throws Exception the exception
	 */
	public void applyOutputStream(OutputStream __outputStream, WriteDataInterface __writeData) throws Exception;

	/**
	 * Apply stream. Get and write data from instance of WriteDataInterface into stream
	 * 
	 * @deprecated since version 0.0.8
	 *
	 * @param __outputStream the output stream
	 * @param __dataListener the data listener
	 * @param __command the command
	 * @throws Exception the exception
	 */
	@Deprecated
	public void applyOutputStream(OutputStream __outputStream, WriteDataInterface __dataListener, Object __command) throws Exception;
}
