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
	 * Gets the list clients.
	 *
	 * @return the list clients
	 */
	public Collection<SocketClientInterface> getListClients();

	/**
	 * Checks if is running.
	 *
	 * @return true, if is running
	 */
	public boolean isRunning();

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

	/**
	 * Sets the accept client listener.
	 *
	 * @param acceptClientListener the new accept client listener
	 */
	public void setAcceptClientListener(AcceptClientListener acceptClientListener);

	/**
	 * Start.
	 */
	public void start();

	/**
	 * Stop.
	 */
	public void stop();
}
