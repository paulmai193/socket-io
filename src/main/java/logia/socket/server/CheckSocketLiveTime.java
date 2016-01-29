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

	private static final Logger   LOGGER = Logger.getLogger(CheckSocketLiveTime.class);

	/** The max live time. */
	private long                  maxLiveTime;

	/** The server. */
	private SocketServerInterface server;

	/**
	 * Instantiates a new check socket live time.
	 *
	 * @param __server the server
	 * @param __maxLiveTime the max live time
	 */
	public CheckSocketLiveTime(SocketServerInterface __server, long __maxLiveTime) {
		this.server = __server;
		this.idleLiveTime = 0;
		this.maxLiveTime = __maxLiveTime;
	}

	/**
	 * Instantiates a new check socket live time.
	 *
	 * @param __server the server
	 * @param __idleLiveTime the idle live time
	 * @param __maxLiveTime the max live time
	 */
	public CheckSocketLiveTime(SocketServerInterface __server, long __idleLiveTime, long __maxLiveTime) {
		this.server = __server;
		this.idleLiveTime = __idleLiveTime;
		this.maxLiveTime = __maxLiveTime;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Runnable#run()
	 */
	@Override
	public void run() {
		for (SocketClientInterface _client : this.server.getListClients()) {
			if (this.idleLiveTime > 0 && _client.getLiveTime() > this.idleLiveTime) {
				// TODO Thinking how to resolve

			}
			else if (_client.getLiveTime() > this.maxLiveTime) {
				_client.disconnect();
			}
		}

	}

}
