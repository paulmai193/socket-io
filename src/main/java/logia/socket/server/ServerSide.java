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

	/** The thread socket. */
	private static Thread                            _threadSocket;

	/** The instance. */
	public final ServerSide                          instance;

	/** The is running. */
	public boolean                                   isRunning;

	/** The accept client listener. */
	private AcceptClientListener                     acceptClientListener;

	/** The thread check socket live time. */
	private ScheduledExecutorService                 checkRemoteSocketLiveTime;

	/** The clients. */
	private final Map<String, SocketClientInterface> CLIENTS;

	/** The idle live time. */
	private final long                               idleLiveTime;

	/** The logger. */
	private final Logger                             LOGGER = Logger.getLogger(this.getClass());

	/** The max socket live time. */
	private final long                               maxLiveTime;

	/** The port. */
	protected final int                              PORT;

	/** The server socket. */
	protected ServerSocket                           serverSocket;

	/** The timeout in milliseconds. */
	protected final int                              TIME_OUT;

	/** The data buffer size. */
	protected final int                              DATA_BUFFER_SIZE;

	/**
	 * Instantiates a new server side of socket.
	 *
	 * @param port the port
	 */
	public ServerSide(int port) {
		this.PORT = port;
		this.TIME_OUT = 0;
		this.isRunning = false;
		this.idleLiveTime = 0;
		this.maxLiveTime = 0;
		this.CLIENTS = Collections.synchronizedSortedMap(new TreeMap<String, SocketClientInterface>());
		this.DATA_BUFFER_SIZE = 65536;

		this.instance = this;
	}

	/**
	 * Instantiates a new server side of socket.
	 *
	 * @param port the port
	 * @param timeout the timeout of socket when accepted
	 */
	public ServerSide(int port, int timeout) {
		this.PORT = port;
		this.TIME_OUT = timeout;
		this.isRunning = false;
		this.idleLiveTime = 0;
		this.maxLiveTime = 0;
		this.CLIENTS = Collections.synchronizedSortedMap(new TreeMap<String, SocketClientInterface>());
		this.DATA_BUFFER_SIZE = 65536;

		this.instance = this;
	}

	/**
	 * Instantiates a new server side of socket.
	 *
	 * @param port the port
	 * @param timeout the timeout in milliseconds
	 * @param maxLiveTime the max client socket live time
	 */
	public ServerSide(int port, int timeout, long maxLiveTime) {
		this.PORT = port;
		this.TIME_OUT = timeout;
		this.isRunning = false;
		this.idleLiveTime = 0;
		this.maxLiveTime = maxLiveTime;
		this.CLIENTS = Collections.synchronizedSortedMap(new TreeMap<String, SocketClientInterface>());
		this.DATA_BUFFER_SIZE = 65536;

		this.instance = this;
	}

	/**
	 * Instantiates a new server side of socket.
	 *
	 * @param port the port
	 * @param timeout the timeout in milliseconds
	 * @param idleLiveTime the idle client socket live time
	 * @param maxLiveTime the max client socket live time
	 */
	public ServerSide(int port, int timeout, long idleLiveTime, long maxLiveTime) {
		this.PORT = port;
		this.TIME_OUT = timeout;
		this.isRunning = false;
		this.idleLiveTime = idleLiveTime;
		this.maxLiveTime = maxLiveTime;
		this.CLIENTS = Collections.synchronizedSortedMap(new TreeMap<String, SocketClientInterface>());
		this.DATA_BUFFER_SIZE = 65536;

		this.instance = this;
	}

	/**
	 * Instantiates a new server side.
	 *
	 * @param port the port
	 * @param timeout the timeout
	 * @param idleLiveTime the idle live time
	 * @param maxLiveTime the max live time
	 * @param dataBufferSize the receive buffer size
	 */
	public ServerSide(int port, int timeout, long idleLiveTime, long maxLiveTime, int dataBufferSize) {
		this.PORT = port;
		this.TIME_OUT = timeout;
		this.isRunning = false;
		this.idleLiveTime = idleLiveTime;
		this.maxLiveTime = maxLiveTime;
		this.CLIENTS = Collections.synchronizedSortedMap(new TreeMap<String, SocketClientInterface>());
		this.DATA_BUFFER_SIZE = dataBufferSize;

		this.instance = this;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see logia.socket.Interface.SocketServerInterface#addClient(logia.socket.Interface.SocketClientInterface)
	 */
	@Override
	public void addClient(SocketClientInterface client) {
		this.CLIENTS.put(client.getId(), client);
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
	public void removeClient(SocketClientInterface client) {
		this.CLIENTS.remove(client.getId());
		client = null;
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
			this.serverSocket.setReceiveBufferSize(this.DATA_BUFFER_SIZE);
			while (this.isRunning) {
				final Socket socket = this.serverSocket.accept();
				if (this.TIME_OUT > 0) {
					socket.setSoTimeout(this.TIME_OUT);
				}
				if (this.acceptClientListener == null) {
					this.acceptClientListener = new AcceptClientListener() {

						@Override
						public void acceptClient(Socket socket) throws ConnectionErrorException {
							SocketClientInterface clientSocket = new ClientOnServerSide(ServerSide.this.instance, socket);
							ServerSide.this.addClient(clientSocket);
							new Thread(clientSocket).start();
						}
					};
				}
				try {
					this.acceptClientListener.acceptClient(socket);
				}
				catch (ConnectionErrorException e) {
					this.LOGGER.error(e.getMessage(), e);
				}
			}
		}
		catch (IOException e) {
			this.LOGGER.error(e.getMessage(), e);
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see logia.socket.Interface.SocketServerInterface#setAcceptClientListener(logia.socket.Interface.AcceptClientListener)
	 */
	@Override
	public void setAcceptClientListener(AcceptClientListener acceptClientListener) {
		this.acceptClientListener = acceptClientListener;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see logia.socket.Interface.SocketServerInterface#start()
	 */
	@Override
	public void start() {
		ServerSide._threadSocket = new Thread(this);
		ServerSide._threadSocket.start();
		if (this.maxLiveTime > 0) {
			this.checkRemoteSocketLiveTime = Executors.newSingleThreadScheduledExecutor();
			if (this.idleLiveTime > 0) {
				this.checkRemoteSocketLiveTime.scheduleWithFixedDelay(new CheckSocketLiveTime(this, this.idleLiveTime, this.maxLiveTime),
				        this.maxLiveTime, this.maxLiveTime, TimeUnit.MINUTES);
			}
			else {
				this.checkRemoteSocketLiveTime.scheduleWithFixedDelay(new CheckSocketLiveTime(this, this.maxLiveTime), this.maxLiveTime,
				        this.maxLiveTime, TimeUnit.MINUTES);
			}
		}
		this.LOGGER.debug("Server online");
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
		catch (IOException e) {
		}
		if (ServerSide._threadSocket != null && ServerSide._threadSocket.isAlive()) {
			ServerSide._threadSocket.interrupt();
		}
		if (this.checkRemoteSocketLiveTime != null) {
			this.checkRemoteSocketLiveTime.shutdown();
		}
		this.LOGGER.debug("Server offline!!!");
	}

}
