package logia.socket.Interface;

import java.util.Collection;

import logia.socket.listener.AcceptClientListener;

/**
 * The Interface SocketServerInterface.
 * 
 * @author Paul Mai
 */
public interface SocketServerInterface extends Runnable {

	/**
	 * Adds the client.
	 *
	 * @param __client the client
	 * @return true, if successful
	 */
	public void addClient(SocketClientInterface __client);

	/**
	 * Gets the client.
	 *
	 * @param __id the ID client
	 * @return the client
	 */
	public SocketClientInterface getClient(String __id);

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
	 * @param __client the client
	 * @return true, if successful
	 */
	public void removeClient(SocketClientInterface __client);

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
	 * @param __acceptClientListener the new accept client listener
	 */
	public void setAcceptClientListener(AcceptClientListener __acceptClientListener);

	/**
	 * Start.
	 */
	public void start();

	/**
	 * Stop.
	 */
	public void stop();
}
