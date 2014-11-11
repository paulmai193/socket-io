package socket.client;

import io.util.Reader;
import io.util.Writer;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.net.SocketException;
import java.net.UnknownHostException;

import define.Config;

import socket.listener.SocketListener;
import socket.listener.WriteDataListener;

/**
 * The Class ClientSocket.
 * 
 * @author Paul Mai
 */
public class ClientSocket implements Runnable, SocketListener {
	
	/** The host. */
	private String host;
	
	/** The port. */
	private int port;
	
	/** The timeout. */
	private int timeout;
	
	/** The is connected. */
	private boolean isConnected;
	
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
	
	/**
	 * Instantiates a new client socket.
	 * 
	 * @throws IOException Signals that an I/O exception has occurred.
	 */
	public ClientSocket() throws IOException {
		host = "localhost";
		port = 3333;
		timeout = 0;
		isConnected = false;
		reader = new Reader();
		writer = new Writer();
	}
	
	/**
	 * Instantiates a new client socket.
	 * 
	 * @param host the host
	 * @param port the port
	 * @param timeout the timeout
	 * @throws IOException Signals that an I/O exception has occurred.
	 */
	public ClientSocket(String host, int port, int timeout) throws IOException {
		this.host = host;
		this.port = port;
		this.timeout = timeout;
		isConnected = false;
		reader = new Reader();
		writer = new Writer();
	}
	
	/**
	 * Adds the task.
	 * 
	 * @param data the data
	 * @param command the command
	 */
	public synchronized void echoServer(WriteDataListener data, int command) {
		connect();
		
		try {
			writer.applyStream(this.getOutputStream(), data, command, Config.DATA_PACKAGE_PATH_CLIENT);
		}
		catch (IOException e) {
			e.printStackTrace();
		}
		catch (Exception e) {
			e.printStackTrace();
		}
	}

	/* (non-Javadoc)
	 * @see socket.listener.SocketListener#isConnected()
	 */
	@Override
	public boolean isConnected() {
		return isConnected;
	}
	
	/* (non-Javadoc)
	 * 
	 * @see socket.listener.SocketListener#connect() */
	@Override
	public void connect() {
		if (!isConnected) {
			try {
				socket = new Socket(host, port);
				if (timeout > 0) {
					socket.setSoTimeout(timeout);
				}
				outputStream = socket.getOutputStream();
				inputStream = socket.getInputStream();
				isConnected = true;
				System.out.println("Client connected");
			}
			catch (SocketException e) {
				e.printStackTrace();
			}
			catch (UnknownHostException e) {
				e.printStackTrace();
			}
			catch (IOException e) {
				e.printStackTrace();
			}
		}
	}
	
	/* (non-Javadoc)
	 * 
	 * @see socket.listener.SocketListener#disconnect() */
	@Override
	public void disconnect() {
		isConnected = false;
		if (outputStream != null) {
			try {
				outputStream.close();
			}
			catch (IOException e) {
				e.printStackTrace();
			}
			outputStream = null;
		}
		if (inputStream != null) {
			try {
				inputStream.close();
			}
			catch (IOException e) {
				e.printStackTrace();
			}
			inputStream = null;
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
	}
	
	/* (non-Javadoc)
	 * 
	 * @see socket.listener.SocketListener#getReader() */
	@Override
	public Reader getReader() {
		return reader;
	}
	
	/* (non-Javadoc)
	 * 
	 * @see socket.listener.SocketListener#getWriter() */
	@Override
	public Writer getWriter() {
		return writer;
	}
	
	/* (non-Javadoc)
	 * 
	 * @see socket.listener.SocketListener#getInputStream() */
	@Override
	public InputStream getInputStream() {
		return inputStream;
	}
	
	/* (non-Javadoc)
	 * 
	 * @see socket.listener.SocketListener#getOutputStream() */
	@Override
	public OutputStream getOutputStream() {
		return outputStream;
	}
	
	/* (non-Javadoc)
	 * @see java.lang.Runnable#run()
	 */
	@Override
	public void run() {
		reader.applyStream(this, Config.DATA_PACKAGE_PATH_CLIENT);
	}
}
