package logia.socket.server;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketTimeoutException;

import logia.socket.Interface.SocketServerInterface;


/**
 * The Class AbstractServerSocket.
 * 
 * @author Paul Mai
 */
public abstract class AbstractServerSocket implements SocketServerInterface {

	/** The server socket. */
	protected ServerSocket serverSocket;
	
	/** The port. */
	protected int port;
	
	/** The is running. */
	public boolean isRunning;
	
	/** The timeout in milliseconds. */
	protected int timeout;	
	
	/** The thread socket. */
	public Thread threadSocket;

	/**
	 * Instantiates a new abstract server socket.
	 *
	 * @param port the port
	 */
	public AbstractServerSocket(int port) {
		this.port = port;
		this.timeout = 0;
		isRunning = false;
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
	}

	/**
	 * Start.
	 */
	public void start() {
		threadSocket = new Thread(this);
		threadSocket.start();
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
		if (threadSocket.isAlive()) {
			threadSocket.interrupt();
		}
		System.out.println("Server offline!!!");
	}
	
	/* (non-Javadoc)
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
				acceptClient(socket);
			}
		}
		catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * Accept client.
	 *
	 * @param socket the socket
	 * @return the socket client interface
	 * @throws SocketTimeoutException the socket timeout exception
	 * @throws IOException Signals that an I/O exception has occurred.
	 */
	public abstract void acceptClient(Socket socket) throws SocketTimeoutException, IOException;
	
}
