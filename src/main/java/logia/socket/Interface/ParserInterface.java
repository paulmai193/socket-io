package logia.socket.Interface;

/**
 * The Interface ParserInterface.
 *
 * @author Paul Mai
 */
public interface ParserInterface {

	/**
	 * Apply input stream.
	 *
	 * @param __clientSocket the __client socket
	 */
	public void applyInputStream(SocketClientInterface __clientSocket);

	/**
	 * Apply output stream.
	 *
	 * @param __clientSocket the __client socket
	 * @param __writeData the __write data
	 * @throws Exception the exception
	 */
	public void applyOutputStream(SocketClientInterface __clientSocket,
	        WriteDataInterface __writeData) throws Exception;

}
