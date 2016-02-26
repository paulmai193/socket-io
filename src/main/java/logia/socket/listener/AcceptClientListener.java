package logia.socket.listener;

import java.net.Socket;

import logia.io.exception.ConnectionErrorException;
import logia.io.exception.ReadDataException;

/**
 * The listener interface for receiving acceptClient events. The class that is interested in processing a acceptClient event implements this
 * interface, and the object created with that class is registered with a component using the component's
 * <code>addAcceptClientListener<code> method. When
 * the acceptClient event occurs, that object's appropriate
 * method is invoked.
 *
 * @author Paul Mai
 * @see AcceptClientEvent
 */
public interface AcceptClientListener {

	/**
	 * Accept socket connection and create new instance of Client on Server side.
	 *
	 * @param __socket the socket
	 * @throws ConnectionErrorException the connection error exception
	 * @throws ClassNotFoundException the class not found exception
	 * @throws ReadDataException the read data exception
	 */
	public void acceptClient(Socket __socket) throws ConnectionErrorException, ClassNotFoundException, ReadDataException;
}
