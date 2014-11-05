package socket;

import java.io.IOException;
import java.net.Socket;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

/**
 * The Class ServerSocket.
 * 
 * @author Paul Mai
 */
public class ServerSocket {
	
	/** The server socket. */
	private java.net.ServerSocket serverSocket;
	
	/** The port. */
	private int port;
	
	/** The is running. */
	private boolean isRunning;
	
	/** The _hash set. */
	public Set<ClientConnectSocket> _hashSet = Collections.synchronizedSet(new HashSet<ClientConnectSocket>());
	
	/**
	 * Instantiates a new server socket.
	 * 
	 * @param port the port
	 */
	public ServerSocket(int port) {
		this.port = port;
		isRunning = false;
	}
	
	/**
	 * Start.
	 */
	public void start() {
		isRunning = true;
		try {
			serverSocket = new java.net.ServerSocket(port);
			System.out.println("Server online on port " + port);
			while (isRunning) {
				Socket socket = serverSocket.accept();
				System.out.println("Hello client on " + socket.getRemoteSocketAddress());
				ClientConnectSocket clientSocket = new ClientConnectSocket(this, socket);
				clientSocket.start();
			}
		}
		catch (IOException e) {
			e.printStackTrace();
			
		}
	}
	
	/**
	 * Stop.
	 */
	public void Stop() {
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
		System.out.println("Server offline");
	}
}
