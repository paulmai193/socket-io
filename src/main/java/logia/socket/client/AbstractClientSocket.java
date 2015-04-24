package logia.socket.client;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;

import logia.io.parser.DataParser;
import logia.socket.Interface.SocketClientInterface;

/**
 * The Class AbstractClientSocket.
 * 
 * @author Paul Mai
 */
public abstract class AbstractClientSocket implements SocketClientInterface {

	/** The socket. */
	protected Socket       socket;

	/** The input stream. */
	protected InputStream  inputStream;

	/** The output stream. */
	protected OutputStream outputStream;

	/** The data parser. */
	protected DataParser   parser;

	/** The is connected. */
	protected boolean      isConnected;

	/** The timeout of connection. */
	protected int          timeout;

	/** The start time. */
	protected final long   startTime;

	/** The id. */
	private String         id;

	/**
	 * Instantiates a new abstract client socket.
	 *
	 * @throws IOException Signals that an I/O exception has occurred.
	 */
	public AbstractClientSocket() throws IOException {
		isConnected = false;
		parser = new DataParser();
		timeout = 0;
		startTime = System.currentTimeMillis();
	}

	/**
	 * Instantiates a new abstract client socket.
	 *
	 * @param definePath the path of xml define data parser file
	 * @throws IOException Signals that an I/O exception has occurred.
	 */
	public AbstractClientSocket(String definePath) throws IOException {
		isConnected = false;
		parser = new DataParser(definePath);
		timeout = 0;
		startTime = System.currentTimeMillis();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see socket.Interface.SocketInterface#connect()
	 */
	@Override
	public abstract void connect();

	/*
	 * (non-Javadoc)
	 * 
	 * @see socket.Interface.SocketInterface#disconnect()
	 */
	@Override
	public abstract void disconnect();

	/*
	 * (non-Javadoc)
	 * 
	 * @see socket.Interface.SocketClientInterface#getDataParser()
	 */
	@Override
	public DataParser getDataParser() {
		return parser;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see socket.Interface.SocketInterface#getInputStream()
	 */
	@Override
	public InputStream getInputStream() {
		return inputStream;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see socket.Interface.SocketInterface#getOutputStream()
	 */
	@Override
	public OutputStream getOutputStream() {
		return outputStream;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see logia.socket.Interface.SocketClientInterface#getLiveTime()
	 */
	@Override
	public long getLiveTime() {
		return System.currentTimeMillis() - startTime;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see socket.Interface.SocketInterface#isConnected()
	 */
	@Override
	public boolean isConnected() {
		return isConnected;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Runnable#run()
	 */
	@Override
	public void run() {
		listen();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see logia.socket.Interface.SocketClientInterface#getId()
	 */
	@Override
	public String getId() {
		return id;
	}

	/**
	 * Sets the id.
	 *
	 * @param id the new id
	 */
	protected void setId(String id) {
		this.id = id;
	}

}
