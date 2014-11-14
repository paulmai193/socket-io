package socket.Interface;

/**
 * The Interface ReadDataInterface.
 * 
 * @author Paul Mai
 */
public interface ReadDataInterface {
	
	/**
	 * Execute data when read data from stream finish.
	 *
	 * @throws Exception the exception
	 */
	public void executeData() throws Exception;
	
	/**
	 * Execute data when read data from stream finish.
	 *
	 * @param clientSocket the client socket
	 * @throws Exception the exception
	 */
	public void executeData(SocketClientInterface clientSocket) throws Exception;
}
