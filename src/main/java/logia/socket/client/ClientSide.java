package logia.socket.client;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.net.SocketException;
import java.net.UnknownHostException;

import logia.io.parser.DataParser;
import logia.socket.Interface.SocketClientInterface;
import logia.socket.Interface.SocketTimeoutListener;
import logia.socket.Interface.WriteDataInterface;

/**
 * The Class ClientSide.
 * 
 * @author Paul Mai
 */
public class ClientSide implements SocketClientInterface {

	/** The socket. */
	protected Socket              socket;

	/** The input stream. */
	protected InputStream         inputStream;

	/** The output stream. */
	protected OutputStream        outputStream;

	/** The data parser. */
	protected DataParser          parser;

	/** The is connected. */
	protected boolean             isConnected;

	/** The timeout of connection. */
	protected int                 timeout;

	/** The start time. */
	protected final long          startTime;

	/** The id. */
	private String                id;

	/** The host. */
	private String                host;

	/** The port. */
	private int                   port;

	/** The timeout listener. */
	private SocketTimeoutListener timeoutListener;

	/**
	 * Instantiates a new client side.
	 *
	 * @throws IOException Signals that an I/O exception has occurred.
	 */
	public ClientSide() throws IOException {
		this.isConnected = false;
		this.parser = new DataParser();
		this.timeout = 0;
		this.startTime = System.currentTimeMillis();
		this.host = "localhost";
		this.port = 3333;
		this.timeout = 0;
	}

	/**
	 * Instantiates a new client side.
	 *
	 * @param host the host
	 * @param port the port
	 * @param timeout the timeout
	 * @throws IOException Signals that an I/O exception has occurred.
	 */
	public ClientSide(String host, int port, int timeout) throws IOException {
		this.isConnected = false;
		this.parser = new DataParser();
		timeout = 0;
		this.startTime = System.currentTimeMillis();
		this.host = host;
		this.port = port;
		this.timeout = timeout;
	}

	/**
	 * Instantiates a new client side.
	 *
	 * @param host the host
	 * @param port the port
	 * @param timeout the timeout
	 * @param definePath the define path
	 * @throws IOException Signals that an I/O exception has occurred.
	 */
	public ClientSide(String host, int port, int timeout, String definePath) throws IOException {
		this.isConnected = false;
		this.parser = new DataParser(definePath);
		timeout = 0;
		this.startTime = System.currentTimeMillis();
		this.host = host;
		this.port = port;
		this.timeout = timeout;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see socket.client.AbstractClientSocket#connect()
	 */
	/**
	 * Connect.
	 */
	@Override
	public void connect() {
		if (!this.isConnected()) {
			try {
				this.socket = new Socket(this.host, this.port);
				if (this.timeout > 0) {
					this.socket.setSoTimeout(this.timeout);
				}
				this.outputStream = this.socket.getOutputStream();
				this.inputStream = this.socket.getInputStream();
				this.isConnected = true;
				System.out.println("Yeah, connected!");
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

	/*
	 * (non-Javadoc)
	 * 
	 * @see socket.client.AbstractClientSocket#disconnect()
	 */
	/**
	 * Disconnect.
	 */
	@Override
	public void disconnect() {
		this.isConnected = false;
		if (this.outputStream != null) {
			try {
				this.outputStream.close();
			}
			catch (IOException e) {
				e.printStackTrace();
			}
			this.outputStream = null;
		}
		if (this.inputStream != null) {
			try {
				this.inputStream.close();
			}
			catch (IOException e) {
				e.printStackTrace();
			}
			this.inputStream = null;
		}
		if (this.socket != null) {
			try {
				this.socket.close();
			}
			catch (IOException e) {
				e.printStackTrace();
			}
			this.socket = null;
		}
		try {
			this.finalize();
		}
		catch (Throwable e) {
			e.printStackTrace();
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see socket.client.AbstractClientSocket#echo(socket.Interface.WriteDataInterface, int)
	 */
	/**
	 * Echo.
	 *
	 * @param data the data
	 * @param command the command
	 * @throws Exception the exception
	 */
	@Override
	public void echo(WriteDataInterface data, int command) throws Exception {
		synchronized (this) {
			this.connect();
			this.parser.applyOutputStream(this.outputStream, data, command);
		}
	}

	/**
	 * Gets the data parser.
	 *
	 * @return the data parser
	 */
	@Override
	public DataParser getDataParser() {
		return this.parser;
	}

	/**
	 * Gets the id.
	 *
	 * @return the id
	 */
	@Override
	public String getId() {
		return this.id;
	}

	/**
	 * Gets the input stream.
	 *
	 * @return the input stream
	 */
	@Override
	public InputStream getInputStream() {
		return this.inputStream;
	}

	/**
	 * Gets the live time.
	 *
	 * @return the live time
	 */
	@Override
	public long getLiveTime() {
		return System.currentTimeMillis() - this.startTime;
	}

	/**
	 * Gets the output stream.
	 *
	 * @return the output stream
	 */
	@Override
	public OutputStream getOutputStream() {
		return this.outputStream;
	}

	/**
	 * Gets the timeout listener.
	 *
	 * @return the timeout listener
	 */
	@Override
	public SocketTimeoutListener getTimeoutListener() {
		return this.timeoutListener;
	}

	/**
	 * Checks if is connected.
	 *
	 * @return true, if is connected
	 */
	@Override
	public boolean isConnected() {
		return this.isConnected;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see logia.socket.Interface.SocketClientInterface#listen()
	 */
	/**
	 * Listen.
	 */
	@Override
	public void listen() {
		try {
			this.parser.applyInputStream(this);
		}
		catch (Exception e) {
			e.printStackTrace();
			System.err.println(e.getMessage() + ". Disconnect");
			this.disconnect();
		}
	}

	/**
	 * Run.
	 */
	@Override
	public void run() {
		this.listen();
	}

	/**
	 * Sets the timeout listener.
	 *
	 * @param listener the new timeout listener
	 */
	@Override
	public void setTimeoutListener(SocketTimeoutListener listener) {
		this.timeoutListener = listener;
	}
}
