package socket;

import io.Reader;
import io.Writer;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;

/**
 * The Class ClientConnectSocket.
 * 
 * @author Paul Mai
 */
public class ClientConnectSocket extends Thread {
	
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
	}
	
	/**
	 * Disconnect.
	 */
	public void disconnect() {
		try {
			if (inputStream != null) {
				inputStream.close();
			}
			if (outputStream != null) {
				outputStream.close();
			}
			if (socket != null) {
				socket.close();
			}
			
		}
		catch (IOException e) {
			e.printStackTrace();
		}
		try {
			this.finalize();
		}
		catch (Throwable e) {
			e.printStackTrace();
		}
		server._hashSet.remove(this);
	}
	
	/* (non-Javadoc)
	 * 
	 * @see java.lang.Thread#start() */
	@Override
	public synchronized void start() {
		reader.applyStream(inputStream, this);
	}
	
	/**
	 * Gets the reader.
	 * 
	 * @return the reader
	 */
	public Reader getReader() {
		return reader;
	}
	
	/**
	 * Gets the writer.
	 * 
	 * @return the writer
	 */
	public Writer getWriter() {
		return writer;
	}
	
	/**
	 * Gets the server.
	 * 
	 * @return the server
	 */
	public ServerSocket getServer() {
		return server;
	}
	
	/**
	 * Gets the input stream.
	 * 
	 * @return the input stream
	 */
	public InputStream getInputStream() {
		return inputStream;
	}
	
	/**
	 * Gets the output stream.
	 * 
	 * @return the output stream
	 */
	public OutputStream getOutputStream() {
		return outputStream;
	}
}
