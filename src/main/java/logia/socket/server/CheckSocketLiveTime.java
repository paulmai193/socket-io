package logia.socket.server;

import logia.socket.Interface.SocketClientInterface;
import logia.socket.Interface.SocketServerInterface;

/**
 * The Class CheckSocketLiveTime.
 * 
 * @author Paul Mai
 */
public class CheckSocketLiveTime implements Runnable {

	/** The server. */
	SocketServerInterface server;

	/** The max live time. */
	long                  maxLiveTime;

	/**
	 * Instantiates a new check socket live time.
	 *
	 * @param server the server
	 * @param maxLiveTime the max live time
	 */
	public CheckSocketLiveTime(SocketServerInterface server, long maxLiveTime) {
		this.server = server;
		this.maxLiveTime = maxLiveTime;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Runnable#run()
	 */
	@Override
	public void run() {
		for (SocketClientInterface client : this.server.getListClients()) {
			if (client.getLiveTime() > this.maxLiveTime) {
				client.disconnect();
			}
		}

	}

}
