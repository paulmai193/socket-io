package server;

import java.io.IOException;
import java.net.Socket;

import logia.socket.Interface.WriteDataInterface;
import logia.socket.client.AbstractClientSocket;

/**
 * The Class ClientOnServerImpl.
 * 
 * @author Paul Mai
 */
public class ClientOnServerImpl extends AbstractClientSocket {
	
	/** The server socket. */
	private SocketServerImpl serverSocket;
	
	/**
	 * Instantiates a new client on server impl.
	 * 
	 * @param serverSocket the server socket
	 * @param socket the socket
	 * @throws IOException Signals that an I/O exception has occurred.
	 */
	public ClientOnServerImpl(SocketServerImpl serverSocket, Socket socket) throws IOException {
		super();
		this.serverSocket = serverSocket;
		this.socket = socket;
	}
	
	/**
	 * Instantiates a new client on server impl.
	 *
	 * @param serverSocket the server socket
	 * @param socket the socket
	 * @param definePath the define path locate the xml define data package
	 * @throws IOException Signals that an I/O exception has occurred.
	 */
	public ClientOnServerImpl(SocketServerImpl serverSocket, Socket socket, String definePath) throws IOException {
		super(definePath);
		this.serverSocket = serverSocket;
		this.socket = socket;
		connect();
	}
	
	/* (non-Javadoc)
	 * 
	 * @see socket.client.AbstractClientSocket#connect() */
	@Override
	public void connect() {
		if (!isConnected()) {
			try {
				this.inputStream = socket.getInputStream();
				this.outputStream = socket.getOutputStream();
				isConnected = true;
			}
			catch (IOException e) {
				e.printStackTrace();
			}
			
			System.out.println("A client connected, waiting to read data from client...");	
		}
		
		
	}
	
	/* (non-Javadoc)
	 * 
	 * @see socket.client.AbstractClientSocket#disconnect() */
	@Override
	public void disconnect() {
		isConnected = false;
		serverSocket._hashSet.remove(this);
		if (inputStream != null) {
			try {
				inputStream.close();
			}
			catch (IOException e) {
				e.printStackTrace();
			}
			inputStream = null;
		}
		if (outputStream != null) {
			try {
				outputStream.close();
			}
			catch (IOException e) {
				e.printStackTrace();
			}
			outputStream = null;
		}
		if (socket != null) {
			try {
				socket.close();
			}
			catch (IOException e) {
				e.printStackTrace();
			}
			socket = null;
		}
		try {
			this.finalize();
		}
		catch (Throwable e) {
			e.printStackTrace();
		}		
		System.out.println("A client disconnected");
	}
	
	/* (non-Javadoc)
	 * @see socket.client.AbstractClientSocket#echo(socket.Interface.WriteDataInterface, int, java.lang.String)
	 */
	@Override
	public void echo(WriteDataInterface data, int command) throws Exception {
		synchronized (this) {
			connect();
			parser.applyOutputStream(outputStream, data, command);
		}		
	}
	
	/* (non-Javadoc)
	 * 
	 * @see socket.client.AbstractClientSocket#listen() */
	@Override
	public void listen() {
		parser.applyInputStream(this);
	}
}
