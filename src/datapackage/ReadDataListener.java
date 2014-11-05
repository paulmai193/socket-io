package datapackage;

import socket.ClientConnectSocket;

/**
 * The listener interface for receiving readData events. The class that is interested in processing
 * a readData event implements this interface, and the object created with that class is registered
 * with a component using the component's <code>addReadDataListener<code> method. When the readData
 * event occurs, that object's appropriate method is invoked.
 * 
 * @see ReadDataEvent
 * @author Paul Mai
 */
public interface ReadDataListener {
	
	/**
	 * Execute data when read data from stream finish.
	 * 
	 * @param clientSocket the client socket
	 */
	public void executeData(ClientConnectSocket clientSocket);
}
