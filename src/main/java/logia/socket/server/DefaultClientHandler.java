package logia.socket.server;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.net.SocketException;
import java.net.SocketTimeoutException;
import java.util.Queue;
import java.util.concurrent.LinkedBlockingQueue;

import org.apache.log4j.Logger;

import logia.io.exception.ConnectionErrorException;
import logia.io.exception.ReadDataException;
import logia.io.exception.WriteDataException;
import logia.socket.Interface.ParserInterface;
import logia.socket.Interface.ReadDataInterface;
import logia.socket.Interface.SocketClientInterface;
import logia.socket.Interface.SocketServerInterface;
import logia.socket.Interface.WriteDataInterface;
import logia.socket.listener.SocketTimeoutListener;

/**
 * The Class DefaultClientHandler.
 *
 * @author Paul Mai
 */
public class DefaultClientHandler implements SocketClientInterface {

	/** The Constant LOGGER. */
	private static final Logger			LOGGER = Logger.getLogger(DefaultClientHandler.class);

	/** The id. */
	private String						id;

	/** The input stream. */
	private InputStream					inputStream;

	/** The is connected. */
	private boolean						isConnected;

	/** The is wait. */
	private boolean						isWait;

	/** The output stream. */
	private OutputStream				outputStream;

	/** The parser. */
	private ParserInterface				parser;

	/** The returned. */
	private Queue<ReadDataInterface>	returned;

	/** The server socket. */
	private final SocketServerInterface	SERVER_SOCKET;

	/** The socket. */
	private Socket						socket;

	/** The start time. */
	private final long					startTime;

	/** The timeout listener. */
	private SocketTimeoutListener		timeoutListener;

	/**
	 * Instantiates a new default client handler.
	 *
	 * @param __serverSocket the __server socket
	 * @param __socket the __socket
	 * @throws ConnectionErrorException the connection error exception
	 */
	public DefaultClientHandler(SocketServerInterface __serverSocket, Socket __socket)
	        throws ConnectionErrorException {
		this.isConnected = false;
		this.startTime = System.currentTimeMillis();
		this.SERVER_SOCKET = __serverSocket;
		this.socket = __socket;
		this.setId(__socket.getRemoteSocketAddress().toString());
		this.returned = new LinkedBlockingQueue<ReadDataInterface>(1);
	}

	/**
	 * Instantiates a new default client handler.
	 *
	 * @param __serverSocket the __server socket
	 * @param __socket the __socket
	 * @param __dataParser the __data parser
	 * @throws ConnectionErrorException the connection error exception
	 */
	public DefaultClientHandler(SocketServerInterface __serverSocket, Socket __socket,
	        ParserInterface __dataParser) throws ConnectionErrorException {
		this.isConnected = false;
		this.parser = __dataParser;
		this.startTime = System.currentTimeMillis();
		this.SERVER_SOCKET = __serverSocket;
		this.socket = __socket;
		this.setId(__socket.getRemoteSocketAddress().toString());
		this.returned = new LinkedBlockingQueue<ReadDataInterface>(1);
	}

	/**
	 * Instantiates a new default client handler.
	 *
	 * @param __serverSocket the __server socket
	 * @param __socket the __socket
	 * @param __dataParser the __data parser
	 * @param __timeoutListener the __timeout listener
	 * @throws ConnectionErrorException the connection error exception
	 */
	public DefaultClientHandler(SocketServerInterface __serverSocket, Socket __socket,
	        ParserInterface __dataParser, SocketTimeoutListener __timeoutListener)
	        throws ConnectionErrorException {
		this.isConnected = false;
		this.parser = __dataParser;
		this.startTime = System.currentTimeMillis();
		this.SERVER_SOCKET = __serverSocket;
		this.socket = __socket;
		this.setId(__socket.getRemoteSocketAddress().toString());
		this.timeoutListener = __timeoutListener;
		this.returned = new LinkedBlockingQueue<ReadDataInterface>(1);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see logia.socket.Interface.SocketClientInterface#connect()
	 */
	@Override
	public void connect() throws ConnectionErrorException, ReadDataException {
		if (!this.isConnected()) {
			try {
				this.inputStream = this.socket.getInputStream();
				this.outputStream = this.socket.getOutputStream();
				this.isConnected = true;

				this.listen();

				DefaultClientHandler.LOGGER
				        .debug("A client connected, waiting to read data from client...");
			}
			catch (IOException _e) {
				DefaultClientHandler.LOGGER.error("Cannot connect to socket server", _e);
				throw new ConnectionErrorException(_e);
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
		if (this.inputStream != null) {
			try {
				this.inputStream.close();
			}
			catch (IOException _e) {
			}
			this.inputStream = null;
		}
		if (this.outputStream != null) {
			try {
				this.outputStream.close();
			}
			catch (IOException e) {
			}
			this.outputStream = null;
		}
		if (this.socket != null) {
			try {
				this.socket.close();
			}
			catch (IOException _e) {
			}
			this.socket = null;
		}
		if (this.parser != null) {
			this.parser = null;
		}
		if (this.timeoutListener != null) {
			this.timeoutListener = null;
		}
		DefaultClientHandler.LOGGER.debug("A client disconnected");
		this.SERVER_SOCKET.removeClient(this);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * logia.socket.Interface.SocketClientInterface#echo(logia.socket.Interface.WriteDataInterface)
	 */
	@Override
	public void echo(WriteDataInterface __data) throws WriteDataException {
		synchronized (this.outputStream) {
			try {
				this.parser.applyOutputStream(this, __data);
			}
			catch (Exception _e) {
				DefaultClientHandler.LOGGER.error("Send data error", _e);
				throw new WriteDataException(_e);
			}
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see logia.socket.Interface.SocketClientInterface#echoAndWait(logia.socket.Interface.
	 * WriteDataInterface)
	 */
	@Override
	public ReadDataInterface echoAndWait(WriteDataInterface __data)
	        throws WriteDataException, InterruptedException {
		synchronized (this.outputStream) {
			this.isWait = true;
			DefaultClientHandler.LOGGER.debug("Set wait response after echo data");
			try {
				this.parser.applyOutputStream(this, __data);
			}
			catch (Exception _e) {
				DefaultClientHandler.LOGGER.error("Send data error", _e);
				throw new WriteDataException(_e);
			}
			DefaultClientHandler.LOGGER.debug("Send data to server");

			// Waiting until have return value
			DefaultClientHandler.LOGGER.debug("Is waiting response...");
			synchronized (this) {
				this.wait(60000);
			}

			this.isWait = false;
			DefaultClientHandler.LOGGER.debug("Received data");

			return this.returned.poll();
		}
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
	public boolean isWaitForReturn() {
		return this.isWait;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see logia.socket.Interface.SocketClientInterface#listen()
	 */
	@Override
	public void listen() throws ReadDataException, SocketTimeoutException, SocketException {
		this.parser.applyInputStream(this);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Runnable#run()
	 */
	// @Override
	// public void run() {
	// try {
	// this.listen();
	// }
	// catch (SocketTimeoutException _e) {
	// DefaultClientHandler.LOGGER.warn("Connection timeout");
	// this.disconnect();
	// }
	// catch (SocketException _e) {
	// DefaultClientHandler.LOGGER.warn("Socket interrupt because " + _e.getMessage());
	// this.disconnect();
	// }
	// catch (ReadDataException _e) {
	// DefaultClientHandler.LOGGER.error(_e);
	// }
	// finally {
	// // this.disconnect();
	// }
	// }

	/*
	 * (non-Javadoc)
	 * 
	 * @see logia.socket.Interface.SocketClientInterface#setDataParser(logia.socket.Interface.
	 * ParserInterface)
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
	public void setId(String __id) {
		this.id = __id;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see logia.socket.Interface.SocketClientInterface#setReturned(logia.socket.Interface.
	 * ReadDataInterface)
	 */
	@Override
	public void setReturned(ReadDataInterface __returned) {
		this.returned.add(__returned);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see logia.socket.Interface.SocketClientInterface#setTimeoutListener(logia.socket.Interface.
	 * SocketTimeoutListener)
	 */
	@Override
	public void setTimeoutListener(SocketTimeoutListener __listener) {
		this.timeoutListener = __listener;
	}

}
