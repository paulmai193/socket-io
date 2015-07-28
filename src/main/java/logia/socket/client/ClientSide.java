package logia.socket.client;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.net.SocketException;
import java.net.SocketTimeoutException;
import java.net.UnknownHostException;
import java.util.Queue;
import java.util.concurrent.LinkedBlockingQueue;

import logia.socket.Interface.ParserInterface;
import logia.socket.Interface.ReadDataInterface;
import logia.socket.Interface.SocketClientInterface;
import logia.socket.Interface.WriteDataInterface;
import logia.socket.listener.SocketTimeoutListener;

import org.apache.log4j.Logger;

/**
 * The Class ClientSide.
 * 
 * @author Paul Mai
 */
public class ClientSide implements SocketClientInterface {

	/** The host. */
	private final String             HOST;

	/** The id. */
	private String                   id;

	/** The is wait for response. */
	private boolean                  isWait;

	/** The logger. */
	private final Logger             LOGGER = Logger.getLogger("SOCKET CLIENT");

	/** The port. */
	private final int                PORT;

	/** The returned data. */
	private Queue<ReadDataInterface> returned;

	/** The timeout listener. */
	private SocketTimeoutListener    timeoutListener;

	/** The input stream. */
	protected InputStream            inputStream;

	/** The is connected. */
	protected boolean                isConnected;

	/** The output stream. */
	protected OutputStream           outputStream;

	/** The data parser. */
	protected ParserInterface        parser;

	/** The socket. */
	protected Socket                 socket;

	/** The start time. */
	protected final long             startTime;

	/** The timeout of connection. */
	protected final int              TIME_OUT;

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
		this.startTime = System.currentTimeMillis();
		this.HOST = host;
		this.PORT = port;
		this.TIME_OUT = timeout;
		this.isWait = false;
		this.returned = new LinkedBlockingQueue<ReadDataInterface>();
	}

	/**
	 * Instantiates a new client side.
	 *
	 * @param host the host
	 * @param port the port
	 * @param timeout the timeout in millisecond
	 * @param dataParser the data parser
	 * @throws IOException Signals that an I/O exception has occurred.
	 */
	public ClientSide(String host, int port, int timeout, ParserInterface dataParser) throws IOException {
		this.isConnected = false;
		this.parser = dataParser;
		this.startTime = System.currentTimeMillis();
		this.HOST = host;
		this.PORT = port;
		this.TIME_OUT = timeout;
		this.isWait = false;
		this.returned = new LinkedBlockingQueue<ReadDataInterface>();
	}

	/**
	 * Instantiates a new client side.
	 *
	 * @param host the host
	 * @param port the port
	 * @param timeout the timeout
	 * @param dataParser the data parser
	 * @param timeoutListener the timeout listener
	 * @throws IOException Signals that an I/O exception has occurred.
	 */
	public ClientSide(String host, int port, int timeout, ParserInterface dataParser, SocketTimeoutListener timeoutListener) throws IOException {
		this.isConnected = false;
		this.parser = dataParser;
		this.startTime = System.currentTimeMillis();
		this.HOST = host;
		this.PORT = port;
		this.TIME_OUT = timeout;
		this.timeoutListener = timeoutListener;
		this.isWait = false;
		this.returned = new LinkedBlockingQueue<ReadDataInterface>();
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
				this.socket = new Socket(this.HOST, this.PORT);
				if (this.TIME_OUT > 0) {
					this.socket.setSoTimeout(this.TIME_OUT);
				}
				this.inputStream = this.socket.getInputStream();
				this.outputStream = this.socket.getOutputStream();
				this.isConnected = true;
				this.LOGGER.debug("Yeah, connected!");
			}
			catch (SocketException e) {
				this.LOGGER.error("Cannot connect to socket server", e);
			}
			catch (UnknownHostException e) {
				this.LOGGER.error("Unknown socket server", e);
			}
			catch (IOException e) {
				this.LOGGER.error("Cannot connect to socket server", e);
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
			}
			this.outputStream = null;
		}
		if (this.inputStream != null) {
			try {
				this.inputStream.close();
			}
			catch (IOException e) {
			}
			this.inputStream = null;
		}
		if (this.socket != null) {
			try {
				this.socket.close();
			}
			catch (IOException e) {
			}
			this.socket = null;
		}
		if (this.parser != null) {
			this.parser = null;
		}
		if (this.timeoutListener != null) {
			this.timeoutListener = null;
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see logia.socket.Interface.SocketClientInterface#echo(logia.socket.Interface.WriteDataInterface, int)
	 */
	@Override
	public synchronized void echo(WriteDataInterface data, Object command) throws Exception {
		this.parser.applyOutputStream(this.outputStream, data, command);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see logia.socket.Interface.SocketClientInterface#echoAndWait(logia.socket.Interface.WriteDataInterface, int)
	 */
	@Override
	public synchronized ReadDataInterface echoAndWait(WriteDataInterface data, Object command) throws Exception {
		this.isWait = true;
		this.LOGGER.debug("Set wait response after echo data");
		this.parser.applyOutputStream(this.outputStream, data, command);
		this.LOGGER.debug("Send data to server");
		while (this.returned.size() == 0) {
			// Waiting until have return value
			this.LOGGER.debug("Is waiting response...");
			continue;
		}
		this.isWait = false;
		this.LOGGER.debug("Received data");

		return this.returned.poll();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see logia.socket.Interface.SocketClientInterface#getDataParser()
	 */
	@Override
	public ParserInterface getDataParser() {
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
	 * @see logia.socket.Interface.SocketClientInterface#isWait()
	 */
	@Override
	public boolean isWait() {
		return this.isWait;
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
		catch (SocketException e) {
			this.LOGGER.warn("Socket interrupt because " + e.getMessage());
			this.disconnect();
		}
		catch (IOException e) {
			this.LOGGER.error("Error reading data", e);
			this.disconnect();
		}
		catch (Exception e) {
			this.LOGGER.error(e.getMessage(), e);
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
	 * @see logia.socket.Interface.SocketClientInterface#setDataParser(logia.socket.Interface.ParserInterface)
	 */
	@Override
	public void setDataParser(ParserInterface dataParser) {
		this.parser = dataParser;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see logia.socket.Interface.SocketClientInterface#setId(java.lang.String)
	 */
	@Override
	public void setId(String id) {
		this.id = id;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see logia.socket.Interface.SocketClientInterface#setReturned(logia.socket.Interface.ReadDataInterface)
	 */
	@Override
	public void setReturned(ReadDataInterface returned) {
		this.returned.add(returned);
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
