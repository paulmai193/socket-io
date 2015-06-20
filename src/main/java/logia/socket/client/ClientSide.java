package logia.socket.client;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.net.SocketException;
import java.net.SocketTimeoutException;
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

	/**
	 * Instantiates a new client side.
	 *
	 * @param host the host
	 * @param port the port
	 * @param timeout the timeout
	 * @param definePath the define path
	 * @param timeoutListener the timeout listener
	 * @throws IOException Signals that an I/O exception has occurred.
	 */
	public ClientSide(String host, int port, int timeout, String definePath, SocketTimeoutListener timeoutListener) throws IOException {
		this.isConnected = false;
		this.parser = new DataParser(definePath);
		timeout = 0;
		this.startTime = System.currentTimeMillis();
		this.host = host;
		this.port = port;
		this.timeout = timeout;
		this.timeoutListener = timeoutListener;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see logia.socket.Interface.SocketClientInterface#connect()
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
	 * @see logia.socket.Interface.SocketClientInterface#disconnect()
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
		if (this.parser != null) {
			this.parser = null;
		}
		if (this.timeoutListener != null) {
			this.timeoutListener = null;
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
	 * @see logia.socket.Interface.SocketClientInterface#echo(logia.socket.Interface.WriteDataInterface, int)
	 */
	@Override
	public void echo(WriteDataInterface data, int command) throws Exception {
		synchronized (this) {
			this.connect();
			this.parser.applyOutputStream(this.outputStream, data, command);
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see logia.socket.Interface.SocketClientInterface#getDataParser()
	 */
	@Override
	public DataParser getDataParser() {
		return this.parser;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see logia.socket.Interface.SocketClientInterface#getId()
	 */
	@Override
	public String getId() {
		return this.id;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see logia.socket.Interface.SocketClientInterface#getInputStream()
	 */
	@Override
	public InputStream getInputStream() {
		return this.inputStream;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see logia.socket.Interface.SocketClientInterface#getLiveTime()
	 */
	@Override
	public long getLiveTime() {
		return System.currentTimeMillis() - this.startTime;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see logia.socket.Interface.SocketClientInterface#getOutputStream()
	 */
	@Override
	public OutputStream getOutputStream() {
		return this.outputStream;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see logia.socket.Interface.SocketClientInterface#getTimeoutListener()
	 */
	@Override
	public SocketTimeoutListener getTimeoutListener() {
		return this.timeoutListener;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see logia.socket.Interface.SocketClientInterface#isConnected()
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
	@Override
	public void listen() {
		try {
			this.parser.applyInputStream(this);
		}
		catch (SocketTimeoutException e) {
			if (this.timeoutListener != null) {
				this.timeoutListener.solveTimeout();
			}
			else {
				this.disconnect();
			}
		}
		catch (Exception e) {
			e.printStackTrace();
			System.err.println(e.getMessage() + ". Disconnect");
			this.disconnect();
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Runnable#run()
	 */
	@Override
	public void run() {
		this.listen();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see logia.socket.Interface.SocketClientInterface#setTimeoutListener(logia.socket.Interface.SocketTimeoutListener)
	 */
	@Override
	public void setTimeoutListener(SocketTimeoutListener listener) {
		this.timeoutListener = listener;
	}

}
