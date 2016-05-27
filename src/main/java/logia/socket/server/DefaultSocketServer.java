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

import org.apache.log4j.Logger;

import logia.io.exception.ConnectionErrorException;
import logia.io.exception.ReadDataException;
import logia.socket.Interface.SocketClientInterface;
import logia.socket.Interface.SocketServerInterface;
import logia.socket.listener.AcceptClientListener;

/**
 * The Class DefaultSocketServer.
 *
 * @author Paul Mai
 */
public class DefaultSocketServer implements SocketServerInterface {

	/** The Constant LOGGER. */
	protected static final Logger					   LOGGER = Logger
	        .getLogger(DefaultSocketServer.class);

	/** The thread socket. */
	protected static Thread							   threadSocket;

	/** The instance. */
	protected final DefaultSocketServer				   INSTANCE;

	/** The is running. */
	protected boolean								   isRunning;

	/** The accept client listener. */
	protected AcceptClientListener					   acceptClientListener;

	/** The check remote socket live time. */
	protected ScheduledExecutorService				   checkRemoteSocketLiveTime;

	/** The clients. */
	protected final Map<String, SocketClientInterface> CLIENTS;

	/** The idle live time. */
	protected final long							   IDLE_LIVE_TIME;

	/** The max live time. */
	protected final long							   MAX_LIVE_TIME;

	/** The port. */
	protected final int								   PORT;

	/** The server socket. */
	protected ServerSocket							   serverSocket;

	/** The time out. */
	protected final int								   TIME_OUT;

	/**
	 * Instantiates a new default socket server.
	 *
	 * @param __port the __port
	 */
	public DefaultSocketServer(int __port) {
		this.PORT = __port;
		this.TIME_OUT = 0;
		this.isRunning = false;
		this.IDLE_LIVE_TIME = 0;
		this.MAX_LIVE_TIME = 0;
		this.CLIENTS = Collections
		        .synchronizedSortedMap(new TreeMap<String, SocketClientInterface>());

		this.INSTANCE = this;
	}

	/**
	 * Instantiates a new default socket server.
	 *
	 * @param __port the __port
	 * @param __timeout the __timeout
	 */
	public DefaultSocketServer(int __port, int __timeout) {
		this.PORT = __port;
		this.TIME_OUT = __timeout;
		this.isRunning = false;
		this.IDLE_LIVE_TIME = 0;
		this.MAX_LIVE_TIME = 0;
		this.CLIENTS = Collections
		        .synchronizedSortedMap(new TreeMap<String, SocketClientInterface>());

		this.INSTANCE = this;
	}

	/**
	 * Instantiates a new default socket server.
	 *
	 * @param __port the __port
	 * @param __timeout the __timeout
	 * @param __maxLiveTime the __max live time
	 */
	public DefaultSocketServer(int __port, int __timeout, long __maxLiveTime) {
		this.PORT = __port;
		this.TIME_OUT = __timeout;
		this.isRunning = false;
		this.IDLE_LIVE_TIME = 0;
		this.MAX_LIVE_TIME = __maxLiveTime;
		this.CLIENTS = Collections
		        .synchronizedSortedMap(new TreeMap<String, SocketClientInterface>());

		this.INSTANCE = this;
	}

	/**
	 * Instantiates a new default socket server.
	 *
	 * @param __port the __port
	 * @param __timeout the __timeout
	 * @param __idleLiveTime the __idle live time
	 * @param __maxLiveTime the __max live time
	 */
	public DefaultSocketServer(int __port, int __timeout, long __idleLiveTime, long __maxLiveTime) {
		this.PORT = __port;
		this.TIME_OUT = __timeout;
		this.isRunning = false;
		this.IDLE_LIVE_TIME = __idleLiveTime;
		this.MAX_LIVE_TIME = __maxLiveTime;
		this.CLIENTS = Collections
		        .synchronizedSortedMap(new TreeMap<String, SocketClientInterface>());

		this.INSTANCE = this;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see logia.socket.Interface.SocketServerInterface#addClient(logia.socket.Interface.
	 * SocketClientInterface)
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
	 * @see logia.socket.Interface.SocketServerInterface#removeClient(logia.socket.Interface.
	 * SocketClientInterface)
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
			DefaultSocketServer.LOGGER.debug(
			        "Default receive buffer size: " + this.serverSocket.getReceiveBufferSize());
			while (this.isRunning) {
				final Socket _SOCKET = this.serverSocket.accept();
				if (this.TIME_OUT > 0) {
					_SOCKET.setSoTimeout(this.TIME_OUT);
				}
				if (this.acceptClientListener == null) {
					// Default implement of accept client listenter
					this.acceptClientListener = new AcceptClientListener() {

						@Override
						public void acceptClient(Socket __socket)
					            throws ConnectionErrorException, ReadDataException {
							SocketClientInterface _clientSocket = new DefaultClientHandler(
					                DefaultSocketServer.this.INSTANCE, __socket);
							_clientSocket.connect();
							DefaultSocketServer.this.addClient(_clientSocket);
						}
					};
				}

				try {
					this.acceptClientListener.acceptClient(_SOCKET);
				}
				catch (ConnectionErrorException _e) {
					DefaultSocketServer.LOGGER.error(_e.getMessage(), _e);
				}
				catch (ClassNotFoundException _e) {
					DefaultSocketServer.LOGGER.error(_e.getMessage(), _e);
				}
				catch (ReadDataException _e) {
					DefaultSocketServer.LOGGER.error(_e.getMessage(), _e);
				}
			}
		}
		catch (IOException _e) {
			if (_e.getMessage().contentEquals("Socket closed")) {
				// Swallow this exception, because the server close normally
			}
			else {
				DefaultSocketServer.LOGGER.error(_e.getMessage(), _e);
			}
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * logia.socket.Interface.SocketServerInterface#setAcceptClientListener(logia.socket.Interface.
	 * AcceptClientListener)
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
		DefaultSocketServer.threadSocket = new Thread(this);
		DefaultSocketServer.threadSocket.start();
		if (this.MAX_LIVE_TIME > 0) {
			this.checkRemoteSocketLiveTime = Executors.newSingleThreadScheduledExecutor();
			if (this.IDLE_LIVE_TIME > 0) {
				this.checkRemoteSocketLiveTime.scheduleWithFixedDelay(
				        new CheckSocketLiveTime(this, this.IDLE_LIVE_TIME, this.MAX_LIVE_TIME),
				        this.MAX_LIVE_TIME, this.MAX_LIVE_TIME, TimeUnit.MINUTES);
			}
			else {
				this.checkRemoteSocketLiveTime.scheduleWithFixedDelay(
				        new CheckSocketLiveTime(this, this.MAX_LIVE_TIME), this.MAX_LIVE_TIME,
				        this.MAX_LIVE_TIME, TimeUnit.MINUTES);
			}
		}
		DefaultSocketServer.LOGGER.debug("Server online");
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
		if (DefaultSocketServer.threadSocket != null
		        && DefaultSocketServer.threadSocket.isAlive()) {
			DefaultSocketServer.threadSocket.interrupt();
		}
		if (this.checkRemoteSocketLiveTime != null) {
			this.checkRemoteSocketLiveTime.shutdown();
		}
		DefaultSocketServer.LOGGER.debug("Server offline!!!");
	}

}
