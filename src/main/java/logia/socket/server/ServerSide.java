package logia.socket.server;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Collection;
import java.util.Collections;
import java.util.Map;
import java.util.TreeMap;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import logia.io.exception.ConnectionErrorException;
import logia.io.exception.ReadDataException;
import logia.socket.Interface.SocketClientInterface;
import logia.socket.Interface.SocketServerInterface;
import logia.socket.listener.AcceptClientListener;

import org.apache.log4j.Logger;

/**
 * The Class ServerSide.
 * 
 * @author Paul Mai
 */
public class ServerSide implements SocketServerInterface {

	/** The logger. */
	private static final Logger                      LOGGER = Logger.getLogger(ServerSide.class);

	/** The thread socket. */
	private static Thread                            threadSocket;

	/** The instance. */
	public final ServerSide                          INSTANCE;

	/** The is running. */
	public boolean                                   isRunning;

	/** The accept client listener. */
	private AcceptClientListener                     acceptClientListener;

	/** The thread check socket live time. */
	private ScheduledExecutorService                 checkRemoteSocketLiveTime;

	/** The clients. */
	private final Map<String, SocketClientInterface> CLIENTS;

	/** The idle live time. */
	private final long                               IDLE_LIVE_TIME;

	/** The max socket live time. */
	private final long                               MAX_LIVE_TIME;

	/** The port. */
	protected final int                              PORT;

	/** The server socket. */
	protected ServerSocket                           serverSocket;

	/** The timeout in milliseconds. */
	protected final int                              TIME_OUT;

	/**
	 * Instantiates a new server side of socket.
	 *
	 * @param __port the port
	 */
	public ServerSide(int __port) {
		this.PORT = __port;
		this.TIME_OUT = 0;
		this.isRunning = false;
		this.IDLE_LIVE_TIME = 0;
		this.MAX_LIVE_TIME = 0;
		this.CLIENTS = Collections.synchronizedSortedMap(new TreeMap<String, SocketClientInterface>());

		this.INSTANCE = this;
	}

	/**
	 * Instantiates a new server side of socket.
	 *
	 * @param __port the port
	 * @param __timeout the timeout of socket when accepted
	 */
	public ServerSide(int __port, int __timeout) {
		this.PORT = __port;
		this.TIME_OUT = __timeout;
		this.isRunning = false;
		this.IDLE_LIVE_TIME = 0;
		this.MAX_LIVE_TIME = 0;
		this.CLIENTS = Collections.synchronizedSortedMap(new TreeMap<String, SocketClientInterface>());

		this.INSTANCE = this;
	}

	/**
	 * Instantiates a new server side of socket.
	 *
	 * @param __port the port
	 * @param __timeout the timeout in milliseconds
	 * @param __maxLiveTime the max client socket live time
	 */
	public ServerSide(int __port, int __timeout, long __maxLiveTime) {
		this.PORT = __port;
		this.TIME_OUT = __timeout;
		this.isRunning = false;
		this.IDLE_LIVE_TIME = 0;
		this.MAX_LIVE_TIME = __maxLiveTime;
		this.CLIENTS = Collections.synchronizedSortedMap(new TreeMap<String, SocketClientInterface>());

		this.INSTANCE = this;
	}

	/**
	 * Instantiates a new server side of socket.
	 *
	 * @param __port the port
	 * @param __timeout the timeout in milliseconds
	 * @param __idleLiveTime the idle client socket live time
	 * @param __maxLiveTime the max client socket live time
	 */
	public ServerSide(int __port, int __timeout, long __idleLiveTime, long __maxLiveTime) {
		this.PORT = __port;
		this.TIME_OUT = __timeout;
		this.isRunning = false;
		this.IDLE_LIVE_TIME = __idleLiveTime;
		this.MAX_LIVE_TIME = __maxLiveTime;
		this.CLIENTS = Collections.synchronizedSortedMap(new TreeMap<String, SocketClientInterface>());

		this.INSTANCE = this;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see logia.socket.Interface.SocketServerInterface#addClient(logia.socket.Interface.SocketClientInterface)
	 */
	@Override
	public void addClient(SocketClientInterface __client) {
		this.CLIENTS.put(__client.getId(), __client);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see logia.socket.Interface.SocketServerInterface#getClient(java.lang.String)
	 */
	@Override
	public SocketClientInterface getClient(String __id) {
		return this.CLIENTS.get(__id);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see logia.socket.Interface.SocketServerInterface#getListClients()
	 */
	@Override
	public Collection<SocketClientInterface> getListClients() {
		return this.CLIENTS.values();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see logia.socket.Interface.SocketServerInterface#isRunning()
	 */
	@Override
	public boolean isRunning() {
		return this.isRunning;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see logia.socket.Interface.SocketServerInterface#removeClient(logia.socket.Interface.SocketClientInterface)
	 */
	@Override
	public void removeClient(SocketClientInterface __client) {
		this.CLIENTS.remove(__client.getId());
		__client = null;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see socket.Interface.SocketServerInterface#run()
	 */
	@Override
	public void run() {
		this.isRunning = true;
		try {
			this.serverSocket = new ServerSocket(this.PORT);
			// this.serverSocket.setReceiveBufferSize(10 * 1024);
			ServerSide.LOGGER.debug("Default receive buffer size: " + this.serverSocket.getReceiveBufferSize());
			while (this.isRunning) {
				final Socket _SOCKET = this.serverSocket.accept();
				if (this.TIME_OUT > 0) {
					_SOCKET.setSoTimeout(this.TIME_OUT);
				}
				if (this.acceptClientListener == null) {
					this.acceptClientListener = new AcceptClientListener() {

						@Override
						public void acceptClient(Socket _socket) throws ConnectionErrorException, ReadDataException {
							SocketClientInterface _clientSocket = new DefaultClientHandler(ServerSide.this.INSTANCE, _socket);
							_clientSocket.connect();
							ServerSide.this.addClient(_clientSocket);
							// new Thread(_clientSocket).start();
						}
					};
				}
				try {
					this.acceptClientListener.acceptClient(_SOCKET);
				}
				catch (ConnectionErrorException _e) {
					ServerSide.LOGGER.error(_e.getMessage(), _e);
				}
				catch (ClassNotFoundException _e) {
					ServerSide.LOGGER.error(_e.getMessage(), _e);
				}
				catch (ReadDataException _e) {
					ServerSide.LOGGER.error(_e.getMessage(), _e);
				}
			}
		}
		catch (IOException _e) {
			ServerSide.LOGGER.error(_e.getMessage(), _e);
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see logia.socket.Interface.SocketServerInterface#setAcceptClientListener(logia.socket.Interface.AcceptClientListener)
	 */
	@Override
	public void setAcceptClientListener(AcceptClientListener __acceptClientListener) {
		this.acceptClientListener = __acceptClientListener;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see logia.socket.Interface.SocketServerInterface#start()
	 */
	@Override
	public void start() {
		ServerSide.threadSocket = new Thread(this);
		ServerSide.threadSocket.start();
		if (this.MAX_LIVE_TIME > 0) {
			this.checkRemoteSocketLiveTime = Executors.newSingleThreadScheduledExecutor();
			if (this.IDLE_LIVE_TIME > 0) {
				this.checkRemoteSocketLiveTime.scheduleWithFixedDelay(new CheckSocketLiveTime(this, this.IDLE_LIVE_TIME, this.MAX_LIVE_TIME),
				        this.MAX_LIVE_TIME, this.MAX_LIVE_TIME, TimeUnit.MINUTES);
			}
			else {
				this.checkRemoteSocketLiveTime.scheduleWithFixedDelay(new CheckSocketLiveTime(this, this.MAX_LIVE_TIME), this.MAX_LIVE_TIME,
				        this.MAX_LIVE_TIME, TimeUnit.MINUTES);
			}
		}
		ServerSide.LOGGER.debug("Server online");
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see logia.socket.Interface.SocketServerInterface#stop()
	 */
	@Override
	public void stop() {
		this.isRunning = false;
		try {
			this.serverSocket.close();
		}
		catch (IOException _e) {
		}
		if (ServerSide.threadSocket != null && ServerSide.threadSocket.isAlive()) {
			ServerSide.threadSocket.interrupt();
		}
		if (this.checkRemoteSocketLiveTime != null) {
			this.checkRemoteSocketLiveTime.shutdown();
		}
		ServerSide.LOGGER.debug("Server offline!!!");
	}

}
