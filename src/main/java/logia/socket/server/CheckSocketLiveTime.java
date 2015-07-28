package logia.socket.server;

import logia.socket.Interface.SocketClientInterface;
import logia.socket.Interface.SocketServerInterface;

import org.apache.log4j.Logger;

/**
 * The Class CheckSocketLiveTime.
 * 
 * @author Paul Mai
 */
public class CheckSocketLiveTime implements Runnable {

	/** The idle live time. */
	private long                  idleLiveTime;

	private final Logger          LOGGER = Logger.getLogger("CHECK REMOTE SOCKET CLIENT LIVE TIME");

	/** The max live time. */
	private long                  maxLiveTime;

	/** The server. */
	private SocketServerInterface server;

	/**
	 * Instantiates a new check socket live time.
	 *
	 * @param server the server
	 * @param maxLiveTime the max live time
	 */
	public CheckSocketLiveTime(SocketServerInterface server, long maxLiveTime) {
		this.server = server;
		this.idleLiveTime = 0;
		this.maxLiveTime = maxLiveTime;
	}

	public CheckSocketLiveTime(SocketServerInterface server, long idleLiveTime, long maxLiveTime) {
		this.server = server;
		this.idleLiveTime = idleLiveTime;
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
			if (this.idleLiveTime > 0 && client.getLiveTime() > this.idleLiveTime) {
				// Thinking TODO
			}
			else if (client.getLiveTime() > this.maxLiveTime) {
				client.disconnect();
			}
		}

	}

}
