package logia.socket.server;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketTimeoutException;
import java.util.Collection;
import java.util.Collections;
import java.util.Map;
import java.util.TreeMap;

import logia.socket.Interface.SocketClientInterface;
import logia.socket.Interface.SocketServerInterface;

/**
 * The Class AbstractServerSocket.
 * 
 * @author Paul Mai
 */
public abstract class AbstractServerSocket implements SocketServerInterface {

	/** The server socket. */
	protected ServerSocket                     serverSocket;

	/** The port. */
	protected int                              port;

	/** The timeout in milliseconds. */
	protected int                              timeout;

	/** The is running. */
	public boolean                             isRunning;

	/** The clients. */
	private Map<String, SocketClientInterface> clients;

	/** The max socket live time. */
	private final long                         maxLiveTime;

	/** The thread socket. */
	private static Thread                      _threadSocket;

	/** The thread check socket live time. */
	private static Thread                      _threadCheckSocket;

	/**
	 * Instantiates a new abstract server socket.
	 *
	 * @param port the port
	 */
	public AbstractServerSocket(int port) {
		this.port = port;
		this.timeout = 0;
		isRunning = false;
		maxLiveTime = 0;
		clients = Collections.synchronizedSortedMap(new TreeMap<String, SocketClientInterface>());
	}

	/**
	 * Instantiates a new abstract server socket.
	 *
	 * @param port the port
	 * @param timeout the timeout of socket when accepted
	 */
	public AbstractServerSocket(int port, int timeout) {
		this.port = port;
		this.timeout = timeout;
		isRunning = false;
		maxLiveTime = 0;
		clients = Collections.synchronizedSortedMap(new TreeMap<String, SocketClientInterface>());
	}

	/**
	 * Instantiates a new abstract server socket.
	 *
	 * @param port the port
	 * @param timeout the timeout
	 * @param maxLiveTime the max client socket live time
	 */
	public AbstractServerSocket(int port, int timeout, long maxLiveTime) {
		this.port = port;
		this.timeout = timeout;
		isRunning = false;
		this.maxLiveTime = maxLiveTime;
		clients = Collections.synchronizedSortedMap(new TreeMap<String, SocketClientInterface>());
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see logia.socket.Interface.SocketServerInterface#addClient(logia.socket.Interface.SocketClientInterface)
	 */
	@Override
	public void addClient(SocketClientInterface client) {
		clients.put(client.getId(), client);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see logia.socket.Interface.SocketServerInterface#getListClients()
	 */
	@Override
	public Collection<SocketClientInterface> getListClients() {
		return clients.values();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see logia.socket.Interface.SocketServerInterface#isRunning()
	 */
	@Override
	public boolean isRunning() {
		return isRunning;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see logia.socket.Interface.SocketServerInterface#removeClient(logia.socket.Interface.SocketClientInterface)
	 */
	@Override
	public void removeClient(SocketClientInterface client) {
		clients.remove(client.getId());
	}

	/**
	 * Start.
	 */
	public void start() {
		_threadSocket = new Thread(this);
		_threadSocket.start();
		if (maxLiveTime > 0) {
			_threadCheckSocket = new Thread(new CheckSocketLiveTime(this, maxLiveTime));
			_threadCheckSocket.start();
		}

	}

	/**
	 * Stop.
	 */
	public void stop() {
		isRunning = false;
		try {
			serverSocket.close();
		}
		catch (IOException e1) {
			e1.printStackTrace();
		}
		try {
			this.finalize();
		}
		catch (Throwable e) {
			e.printStackTrace();
		}
		if (_threadSocket.isAlive()) {
			_threadSocket.interrupt();
		}
		if (_threadCheckSocket.isAlive()) {
			_threadCheckSocket.interrupt();
		}
		System.out.println("Server offline!!!");
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see socket.Interface.SocketServerInterface#run()
	 */
	@Override
	public void run() {
		isRunning = true;
		try {
			serverSocket = new ServerSocket(port);
			while (isRunning) {
				Socket socket = serverSocket.accept();
				if (timeout > 0) {
					socket.setSoTimeout(timeout);
				}
				executeClient(socket);
			}
		}
		catch (IOException e) {
			e.printStackTrace();
		}
	}

	/**
	 * Create new instance and way to execute implement client socket.
	 *
	 * @param socket the socket
	 * @return the socket client interface
	 * @throws SocketTimeoutException the socket timeout exception
	 * @throws IOException Signals that an I/O exception has occurred.
	 */
	public abstract void executeClient(Socket socket) throws SocketTimeoutException, IOException;

}
