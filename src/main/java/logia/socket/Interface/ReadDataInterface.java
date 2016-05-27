package logia.socket.Interface;

/**
 * The Interface ReadDataInterface.
 *
 * @author Paul Mai
 */
public interface ReadDataInterface {

	/**
	 * Execute data.
	 *
	 * @throws Exception the exception
	 */
	public void executeData() throws Exception;

	/**
	 * Execute data.
	 *
	 * @param __clientSocket the __client socket
	 * @throws Exception the exception
	 */
	public void executeData(SocketClientInterface __clientSocket) throws Exception;
}
