package logia.socket.Interface;

import java.util.Collection;

/**
 * The Interface SocketServerInterface.
 * 
 * @author Paul Mai
 */
public interface SocketServerInterface extends Runnable {

	/**
	 * Adds the client.
	 *
	 * @param client the client
	 * @return true, if successful
	 */
	public void addClient(SocketClientInterface client);

	/**
	 * Checks if is running.
	 *
	 * @return true, if is running
	 */
	public boolean isRunning();

	/**
	 * Gets the list clients.
	 *
	 * @return the list clients
	 */
	public Collection<SocketClientInterface> getListClients();

	/**
	 * Removes the client.
	 *
	 * @param client the client
	 * @return true, if successful
	 */
	public void removeClient(SocketClientInterface client);

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Runnable#run()
	 */
	@Override
	public void run();
}
