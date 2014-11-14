package socket.client;

import io.util.Reader;
import io.util.Writer;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;

import socket.Interface.SocketClientInterface;
import socket.Interface.WriteDataInterface;

/**
 * The Class AbstractClientSocket.
 */
public abstract class AbstractClientSocket implements SocketClientInterface {
	/** The socket. */
	protected Socket socket;
	
	/** The input stream. */
	protected InputStream inputStream;
	
	/** The output stream. */
	protected OutputStream outputStream;
	
	/** The reader. */
	protected Reader reader;
	
	/** The writer. */
	protected Writer writer;
	
	/** The is connected. */
	protected boolean isConnected;
	
	/** The timeout of connection. */
	protected int timeout;
	
	/**
	 * Instantiates a new abstract client socket.
	 *
	 * @throws IOException Signals that an I/O exception has occurred.
	 */
	public AbstractClientSocket() throws IOException {
		isConnected = false;
		reader = new Reader("");
		writer = new Writer("");
		timeout = 0;
	}
	
	/**
	 * Instantiates a new abstract client socket.
	 *
	 * @param definePath the path of data parser file
	 * @throws IOException Signals that an I/O exception has occurred.
	 */
	public AbstractClientSocket(String definePath) throws IOException {
		isConnected = false;
		reader = new Reader(definePath);
		writer = new Writer(definePath);
		timeout = 0;
	}
	
	/* (non-Javadoc)
	 * @see socket.Interface.SocketInterface#connect()
	 */
	@Override
	public abstract void connect();
	
	/* (non-Javadoc)
	 * @see socket.Interface.SocketInterface#disconnect()
	 */
	@Override
	public abstract void disconnect();
	
	/**
	 * Echo method. Start send data through connection
	 *
	 * @param data the data
	 * @param command the command
	 * @throws Exception the exception
	 */
	public void echo(WriteDataInterface data, int command) throws Exception {
		writer.applyStream(outputStream, data, command);
	}
		
	/* (non-Javadoc)
	 * @see socket.Interface.SocketInterface#getReader()
	 */
	@Override
	public Reader getReader() {
		return reader;
	}
	
	/* (non-Javadoc)
	 * @see socket.Interface.SocketInterface#getWriter()
	 */
	@Override
	public Writer getWriter() {
		return writer;
	}
	
	/* (non-Javadoc)
	 * @see socket.Interface.SocketInterface#getInputStream()
	 */
	@Override
	public InputStream getInputStream() {
		return inputStream;
	}
	
	/* (non-Javadoc)
	 * @see socket.Interface.SocketInterface#getOutputStream()
	 */
	@Override
	public OutputStream getOutputStream() {
		return outputStream;
	}
	
	/* (non-Javadoc)
	 * @see socket.Interface.SocketInterface#isConnected()
	 */
	@Override
	public boolean isConnected() {
		return isConnected;
	}
	
	/**
	 * Listen method. Start reading data send through connection
	 */
	public abstract void listen();
	
	/* (non-Javadoc)
	 * @see java.lang.Runnable#run()
	 */
	@Override
	public void run() {
		listen();
	}
	
}
