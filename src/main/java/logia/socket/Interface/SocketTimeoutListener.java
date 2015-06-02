package logia.socket.Interface;

/**
 * The listener interface for receiving socketTimeout events. The class that is interested in processing a socketTimeout event implements this
 * interface, and the object created with that class is registered with a component using the component's
 * <code>addSocketTimeoutListener<code> method. When
 * the socketTimeout event occurs, that object's appropriate
 * method is invoked.
 *
 * @see SocketTimeoutEvent
 * @author Paul Mai
 */
public interface SocketTimeoutListener {

	/**
	 * Solve timeout.
	 */
	public void solveTimeout();
}
