package socket.server;

import java.io.IOException;
import java.net.Socket;
import java.net.SocketTimeoutException;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

import factory.threadpool.ThreadFactory;

/**
 * The Class ServerSocket.
 * 
 * @author Paul Mai
 */
public class ServerSocket implements Runnable {
	
	/** The server socket. */
	protected java.net.ServerSocket serverSocket;
	
	/** The port. */
	protected int port;
	
	/** The is running. */
	public boolean isRunning;
	
	/** The timeout in milliseconds. */
	protected int timeout;
	
	/** The _hash set. */
	public Set<ClientConnectSocket> _hashSet = Collections.synchronizedSet(new HashSet<ClientConnectSocket>());
	
	/** The thread socket. */
	public Thread threadSocket;
	
	/**
	 * Instantiates a new server socket.
	 * 
	 * @param port the port
	 */
	public ServerSocket(int port) {
		this.port = port;
		this.timeout = 0;
		isRunning = false;
	}
	
	/**
	 * Instantiates a new server socket.
	 * 
	 * @param port the port
	 * @param timeout the timeout
	 */
	public ServerSocket(int port, int timeout) {
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
	 * @see java.lang.Runnable#run()
	 */
	@Override
	public void run() {
		isRunning = true;
		try {
			serverSocket = new java.net.ServerSocket(port);
			if (timeout > 0) {
				serverSocket.setSoTimeout(timeout);
			}
			System.out.println("Server online on port " + port + " waiting for client...");
			while (isRunning) {
				Socket socket = serverSocket.accept();
				System.out.println("Hello client on " + socket.getRemoteSocketAddress());
				ClientConnectSocket clientSocket = new ClientConnectSocket(this, socket);
				ThreadFactory.getInstance().start(clientSocket);
			}
		}
		catch (SocketTimeoutException e) {
			System.out.println("Ok its time out, no more connection");
			stop();
		}
		catch (IOException e) {
			e.printStackTrace();
		}
	}
}
