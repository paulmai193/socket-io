package logia.socket.server;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.net.SocketException;
import java.net.SocketTimeoutException;
import java.util.Queue;
import java.util.concurrent.LinkedBlockingQueue;

import logia.io.exception.ConnectionErrorException;
import logia.io.exception.ReadDataException;
import logia.io.exception.WriteDataException;
import logia.socket.Interface.ParserInterface;
import logia.socket.Interface.ReadDataInterface;
import logia.socket.Interface.SocketClientInterface;
import logia.socket.Interface.SocketServerInterface;
import logia.socket.Interface.WriteDataInterface;
import logia.socket.listener.SocketTimeoutListener;

import org.apache.log4j.Logger;

/**
 * The Class ClientOnServerSide.
 * 
 * @author Paul Mai
 */
public class ClientOnServerSide implements SocketClientInterface {

	/** The id. */
	private String                      id;

	/** The input stream. */
	private InputStream                 inputStream;

	/** The is connected. */
	private boolean                     isConnected;

	/** The is wait for response. */
	private boolean                     isWait;

	/** The logger. */
	private final Logger                LOGGER = Logger.getLogger(getClass());

	/** The output stream. */
	private OutputStream                outputStream;

	/** The data parser. */
	private ParserInterface             parser;

	/** The returned data. */
	private Queue<ReadDataInterface>    returned;

	/** The server socket. */
	private final SocketServerInterface SERVER_SOCKET;

	/** The socket. */
	private Socket                      socket;

	/** The start time. */
	private final long                  startTime;

	/** The timeout listener. */
	private SocketTimeoutListener       timeoutListener;

	/**
	 * Instantiates a new client socket on server side.
	 *
	 * @param serverSocket the server socket
	 * @param socket the socket
	 * @throws ConnectionErrorException
	 */
	public ClientOnServerSide(SocketServerInterface serverSocket, Socket socket) throws ConnectionErrorException {
		this.isConnected = false;
		this.startTime = System.currentTimeMillis();
		this.SERVER_SOCKET = serverSocket;
		this.socket = socket;
		this.setId(socket.getRemoteSocketAddress().toString());
		this.returned = new LinkedBlockingQueue<ReadDataInterface>(1);

		this.connect();
	}

	/**
	 * Instantiates a new client socket on server side.
	 *
	 * @param serverSocket the server socket
	 * @param socket the socket
	 * @param dataParser the data parser
	 * @throws ConnectionErrorException
	 */
	public ClientOnServerSide(SocketServerInterface serverSocket, Socket socket, ParserInterface dataParser) throws ConnectionErrorException {
		this.isConnected = false;
		this.parser = dataParser;
		this.startTime = System.currentTimeMillis();
		this.SERVER_SOCKET = serverSocket;
		this.socket = socket;
		this.setId(socket.getRemoteSocketAddress().toString());
		this.returned = new LinkedBlockingQueue<ReadDataInterface>(1);

		this.connect();
	}

	/**
	 * Instantiates a new client on server side.
	 *
	 * @param serverSocket the server socket
	 * @param socket the socket
	 * @param dataParser the data parser
	 * @param timeoutListener the timeout listener
	 * @throws ConnectionErrorException
	 */
	public ClientOnServerSide(SocketServerInterface serverSocket, Socket socket, ParserInterface dataParser, SocketTimeoutListener timeoutListener)
	        throws ConnectionErrorException {
		this.isConnected = false;
		this.parser = dataParser;
		this.startTime = System.currentTimeMillis();
		this.SERVER_SOCKET = serverSocket;
		this.socket = socket;
		this.setId(socket.getRemoteSocketAddress().toString());
		this.timeoutListener = timeoutListener;
		this.returned = new LinkedBlockingQueue<ReadDataInterface>(1);

		this.connect();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see logia.socket.Interface.SocketClientInterface#connect()
	 */
	@Override
	public void connect() throws ConnectionErrorException {
		if (!this.isConnected()) {
			try {
				this.inputStream = this.socket.getInputStream();
				this.outputStream = this.socket.getOutputStream();
				this.isConnected = true;

				this.LOGGER.debug("A client connected, waiting to read data from client...");
			}
			catch (IOException e) {
				this.LOGGER.error("Cannot connect to socket server", e);
				throw new ConnectionErrorException(e);
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
			catch (IOException e) {
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
		this.LOGGER.debug("A client disconnected");
		this.SERVER_SOCKET.removeClient(this);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see logia.socket.Interface.SocketClientInterface#echo(logia.socket.Interface.WriteDataInterface, int)
	 */
	@Override
	public void echo(WriteDataInterface data, Object command) throws WriteDataException {
		synchronized (this.outputStream) {
			try {
				this.parser.applyOutputStream(this.outputStream, data, command);
			}
			catch (Exception e) {
				LOGGER.error("Send data error", e);
				throw new WriteDataException(e);
			}
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see logia.socket.Interface.SocketClientInterface#echoAndWait(logia.socket.Interface.WriteDataInterface, int)
	 */
	@Override
	public ReadDataInterface echoAndWait(WriteDataInterface data, Object command) throws WriteDataException, InterruptedException {
		synchronized (this.outputStream) {
			this.isWait = true;
			this.LOGGER.debug("Set wait response after echo data");
			try {
				this.parser.applyOutputStream(this.outputStream, data, command);
			}
			catch (Exception e) {
				LOGGER.error("Send data error", e);
				throw new WriteDataException(e);
			}
			this.LOGGER.debug("Send data to server");

			// Waiting until have return value
			this.LOGGER.debug("Is waiting response...");
			synchronized (this) {
				wait();
			}

			this.isWait = false;
			this.LOGGER.debug("Received data");

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
		try {
			this.parser.applyInputStream(this);
		}
		catch (SocketTimeoutException e) {
			if (this.timeoutListener != null) {
				this.timeoutListener.solveTimeout();
			}
			else {
				throw e;
			}
		}
		catch (SocketException e) {
			throw e;
		}
		catch (IOException e) {
			throw new ReadDataException(e);
		}
		catch (Exception e) {
			throw new ReadDataException(e);
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Runnable#run()
	 */
	@Override
	public void run() {
		try {
			this.listen();
		}
		catch (SocketTimeoutException e) {
			this.LOGGER.warn("Connection timeout");
		}
		catch (SocketException e) {
			this.LOGGER.warn("Socket interrupt because " + e.getMessage());
		}
		catch (ReadDataException e) {
			this.LOGGER.error(e);
		}
		finally {
			this.disconnect();
		}
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
