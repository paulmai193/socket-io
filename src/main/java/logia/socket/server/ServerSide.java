package logia.socket.server;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketTimeoutException;
import java.util.Collection;
import java.util.Collections;
import java.util.Map;
import java.util.TreeMap;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import logia.socket.Interface.AcceptClientListener;
import logia.socket.Interface.SocketClientInterface;
import logia.socket.Interface.SocketServerInterface;

import org.apache.log4j.Logger;

/**
 * The Class ServerSide.
 * 
 * @author Paul Mai
 */
public class ServerSide implements SocketServerInterface {

	/** The logger. */
	private Logger                             LOGGER = Logger.getLogger(this.getClass());

	/** The thread socket. */
	private static Thread                      _threadSocket;

	/** The instance. */
	public final ServerSide                    instance;

	/** The is running. */
	public boolean                             isRunning;

	/** The accept client listener. */
	private AcceptClientListener               acceptClientListener;

	/** The clients. */
	private Map<String, SocketClientInterface> clients;

	/** The thread check socket live time. */
	private ScheduledExecutorService           executorService;

	/** The max socket live time. */
	private final long                         maxLiveTime;

	/** The port. */
	protected int                              port;

	/** The server socket. */
	protected ServerSocket                     serverSocket;

	/** The timeout in milliseconds. */
	protected int                              timeout;

	/**
	 * Instantiates a new server side of socket.
	 *
	 * @param port the port
	 */
	public ServerSide(int port) {
		this.port = port;
		this.timeout = 0;
		this.isRunning = false;
		this.maxLiveTime = 0;
		this.clients = Collections.synchronizedSortedMap(new TreeMap<String, SocketClientInterface>());

		this.instance = this;
	}

	/**
	 * Instantiates a new server side of socket.
	 *
	 * @param port the port
	 * @param timeout the timeout of socket when accepted
	 */
	public ServerSide(int port, int timeout) {
		this.port = port;
		this.timeout = timeout;
		this.isRunning = false;
		this.maxLiveTime = 0;
		this.clients = Collections.synchronizedSortedMap(new TreeMap<String, SocketClientInterface>());

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
		this.port = port;
		this.timeout = timeout;
		this.isRunning = false;
		this.maxLiveTime = maxLiveTime;
		this.clients = Collections.synchronizedSortedMap(new TreeMap<String, SocketClientInterface>());

		this.instance = this;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see logia.socket.Interface.SocketServerInterface#addClient(logia.socket.Interface.SocketClientInterface)
	 */
	@Override
	public void addClient(SocketClientInterface client) {
		this.clients.put(client.getId(), client);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see logia.socket.Interface.SocketServerInterface#getListClients()
	 */
	@Override
	public Collection<SocketClientInterface> getListClients() {
		return this.clients.values();
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
		this.clients.remove(client.getId());
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
			this.serverSocket = new ServerSocket(this.port);
			while (this.isRunning) {
				final Socket socket = this.serverSocket.accept();
				if (this.timeout > 0) {
					socket.setSoTimeout(this.timeout);
				}
				if (this.acceptClientListener == null) {
					this.acceptClientListener = new AcceptClientListener() {

						@Override
						public void acceptClient(Socket socket) throws SocketTimeoutException, IOException {
							SocketClientInterface clientSocket = new ClientOnServerSide(ServerSide.this.instance, socket);
							ServerSide.this.addClient(clientSocket);
							new Thread(clientSocket).start();
						}
					};
				}
				this.acceptClientListener.acceptClient(socket);
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
			this.executorService = Executors.newSingleThreadScheduledExecutor();
			this.executorService.scheduleWithFixedDelay(new CheckSocketLiveTime(this, this.maxLiveTime), 10, 10, TimeUnit.MINUTES);
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
		if (this.executorService != null) {
			this.executorService.shutdown();
		}
		this.LOGGER.debug("Server offline!!!");
	}

}
