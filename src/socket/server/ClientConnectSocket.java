package socket.server;

import io.util.Reader;
import io.util.Writer;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;

import define.Config;

import socket.listener.SocketListener;
import socket.listener.WriteDataListener;

/**
 * The Class ClientConnectSocket.
 * 
 * @author Paul Mai
 */
public class ClientConnectSocket implements Runnable, SocketListener {
	
	/** The server. */
	private ServerSocket server;
	
	/** The socket. */
	private Socket socket;
	
	/** The input stream. */
	private InputStream inputStream;
	
	/** The output stream. */
	private OutputStream outputStream;
	
	/** The reader. */
	private Reader reader;
	
	/** The writer. */
	private Writer writer;	
	
	/** The is connected. */
	private boolean isConnected;
	
	/**
	 * Instantiates a new client connect socket.
	 * 
	 * @param serverSocket the server socket
	 * @param socket the socket
	 * @throws IOException Signals that an I/O exception has occurred.
	 */
	public ClientConnectSocket(ServerSocket serverSocket, Socket socket) throws IOException {
		server = serverSocket;
		this.socket = socket;
		try {
			inputStream = socket.getInputStream();
			outputStream = socket.getOutputStream();
		}
		catch (IOException e) {
			e.printStackTrace();			
		}
		reader = new Reader();
		writer = new Writer();
		server._hashSet.add(this);
		isConnected = true;
	}
	
	/**
	 * Echo client.
	 *
	 * @param data the data
	 * @param command the command
	 */
	public synchronized void echoClient(WriteDataListener data, int command) {
		try {
			writer.applyStream(this.getOutputStream(), data, command, Config.DATA_PACKAGE_PATH_SERVER);
		}
		catch (IOException e) {
			e.printStackTrace();
		}
		catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * Gets the server.
	 * 
	 * @return the server
	 */
	public ServerSocket getServer() {
		return server;
	}
	
	/* (non-Javadoc)
	 * @see socket.listener.SocketListener#disconnect()
	 */
	@Override
	public void disconnect() {
		isConnected = false;
		server._hashSet.remove(this);
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
	 * @see socket.listener.SocketListener#connect()
	 */
	@Override
	public void connect() {
		System.out.println("A client connected, waiting to read data from client...");
		reader.applyStream(this, Config.DATA_PACKAGE_PATH_SERVER);
	}
	
	/* (non-Javadoc)
	 * @see java.lang.Runnable#run()
	 */
	@Override
	public void run() {
		connect();
	}
	
	/* (non-Javadoc)
	 * @see socket.listener.SocketListener#getReader()
	 */
	@Override
	public Reader getReader() {
		return reader;
	}
	
	/* (non-Javadoc)
	 * @see socket.listener.SocketListener#getWriter()
	 */
	@Override
	public Writer getWriter() {
		return writer;
	}
	
	/* (non-Javadoc)
	 * @see socket.listener.SocketListener#getInputStream()
	 */
	@Override
	public InputStream getInputStream() {
		return inputStream;
	}
	
	/* (non-Javadoc)
	 * @see socket.listener.SocketListener#getOutputStream()
	 */
	@Override
	public OutputStream getOutputStream() {
		return outputStream;
	}
	
	/* (non-Javadoc)
	 * @see socket.listener.SocketListener#isConnected()
	 */
	@Override
	public boolean isConnected() {
		return isConnected;
	}
}
