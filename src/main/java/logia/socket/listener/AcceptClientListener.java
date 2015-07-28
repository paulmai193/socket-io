package logia.socket.listener;

import java.io.IOException;
import java.net.Socket;
import java.net.SocketTimeoutException;

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
	 * @throws SocketTimeoutException the socket timeout exception
	 * @throws IOException Signals that an I/O exception has occurred.
	 */
	public void acceptClient(Socket socket) throws SocketTimeoutException, IOException;
}
