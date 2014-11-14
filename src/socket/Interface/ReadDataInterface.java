package socket.Interface;


/**
 * The listener interface for receiving readData events. The class that is interested in processing
 * a readData event implements this interface, and the object created with that class is registered
 * with a component using the component's <code>addReadDataListener<code> method. When the readData
 * event occurs, that object's appropriate method is invoked.
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
	 */
	public void executeData(SocketClientInterface clientSocket) throws Exception;
}
