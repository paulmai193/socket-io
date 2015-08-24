package logia.socket.listener;

import java.net.Socket;

import logia.io.exception.ConnectionErrorException;

/**
 * The listener interface for receiving acceptClient events. The class that is interested in processing a acceptClient event implements this
 * interface, and the object created with that class is registered with a component using the component's
 * <code>addAcceptClientListener<code> method. When
 * the acceptClient event occurs, that object's appropriate
 * method is invoked.
 *
 * @see AcceptClientEvent
 * @author Paul Mai
 */
public interface AcceptClientListener {

	/**
	 * Accept socket connection and create new instance of Client on Server side.
	 *
	 * @param socket the socket
	 * @throws ConnectionErrorException the connection error exception
	 */
	public void acceptClient(Socket socket) throws ConnectionErrorException;
}
