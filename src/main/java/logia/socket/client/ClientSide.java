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

import logia.io.exception.ConnectionErrorException;
import logia.io.exception.ReadDataException;
import logia.io.exception.WriteDataException;
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
	private static final Logger      LOGGER = Logger.getLogger(ClientSide.class);

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
	protected final long             START_TIME;

	/** The timeout of connection. */
	protected final int              TIME_OUT;

	/**
	 * Instantiates a new client side.
	 *
	 * @param __host the host
	 * @param __port the port
	 * @param __timeout the timeout
	 */
	public ClientSide(String __host, int __port, int __timeout) {
		this.isConnected = false;
		this.START_TIME = System.currentTimeMillis();
		this.HOST = __host;
		this.PORT = __port;
		this.TIME_OUT = __timeout;
		this.isWait = false;
		this.returned = new LinkedBlockingQueue<ReadDataInterface>(1);
	}

	/**
	 * Instantiates a new client side.
	 *
	 * @param __host the host
	 * @param __port the port
	 * @param __timeout the timeout in millisecond
	 * @param __dataParser the data parser
	 */
	public ClientSide(String __host, int __port, int __timeout, ParserInterface __dataParser) {
		this.isConnected = false;
		this.parser = __dataParser;
		this.START_TIME = System.currentTimeMillis();
		this.HOST = __host;
		this.PORT = __port;
		this.TIME_OUT = __timeout;
		this.isWait = false;
		this.returned = new LinkedBlockingQueue<ReadDataInterface>(1);
	}

	/**
	 * Instantiates a new client side.
	 *
	 * @param __host the host
	 * @param __port the port
	 * @param __timeout the timeout
	 * @param __dataParser the data parser
	 * @param __timeoutListener the timeout listener
	 */
	public ClientSide(String __host, int __port, int __timeout, ParserInterface __dataParser, SocketTimeoutListener __timeoutListener) {
		this.isConnected = false;
		this.parser = __dataParser;
		this.START_TIME = System.currentTimeMillis();
		this.HOST = __host;
		this.PORT = __port;
		this.TIME_OUT = __timeout;
		this.timeoutListener = __timeoutListener;
		this.isWait = false;
		this.returned = new LinkedBlockingQueue<ReadDataInterface>(1);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see logia.socket.Interface.SocketClientInterface#connect()
	 */
	@Override
	public void connect() throws ConnectionErrorException {
		if (!this.isConnected) {
			try {
				this.socket = new Socket(this.HOST, this.PORT);
				if (this.TIME_OUT > 0) {
					this.socket.setSoTimeout(this.TIME_OUT);
				}
				this.inputStream = this.socket.getInputStream();
				this.outputStream = this.socket.getOutputStream();
				this.isConnected = true;
				ClientSide.LOGGER.debug("Yeah, connected!");
			}
			catch (SocketException _e) {
				ClientSide.LOGGER.error("Cannot connect to socket server", _e);
				throw new ConnectionErrorException(_e);
			}
			catch (UnknownHostException _e) {
				ClientSide.LOGGER.error("Unknown socket server", _e);
				throw new ConnectionErrorException(_e);
			}
			catch (IOException _e) {
				ClientSide.LOGGER.error("Cannot connect to socket server", _e);
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
		if (this.outputStream != null) {
			try {
				this.outputStream.close();
			}
			catch (IOException _e) {
			}
			this.outputStream = null;
		}
		if (this.inputStream != null) {
			try {
				this.inputStream.close();
			}
			catch (IOException _e) {
			}
			this.inputStream = null;
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
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see logia.socket.Interface.SocketClientInterface#echo(logia.socket.Interface.WriteDataInterface, int)
	 */
	@Override
	public void echo(WriteDataInterface __data, Object __command) throws WriteDataException {
		synchronized (this.outputStream) {
			try {
				this.parser.applyOutputStream(this.outputStream, __data, __command);
			}
			catch (Exception _e) {
				ClientSide.LOGGER.error("Send data error", _e);
				throw new WriteDataException(_e);
			}

		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see logia.socket.Interface.SocketClientInterface#echoAndWait(logia.socket.Interface.WriteDataInterface, int)
	 */
	@Override
	public ReadDataInterface echoAndWait(WriteDataInterface __data, Object __command) throws WriteDataException, InterruptedException {
		synchronized (this.outputStream) {
			this.isWait = true;
			ClientSide.LOGGER.debug("Set wait response after echo data");
			try {
				this.parser.applyOutputStream(this.outputStream, __data, __command);
			}
			catch (Exception _e) {
				ClientSide.LOGGER.error("Send data error", _e);
				throw new WriteDataException(_e);
			}
			ClientSide.LOGGER.debug("Send data to server");

			// Waiting until have return value
			ClientSide.LOGGER.debug("Is waiting response...");
			synchronized (this) {
				this.wait(60000);
			}

			this.isWait = false;
			ClientSide.LOGGER.debug("Received data");

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
		return System.currentTimeMillis() - this.START_TIME;
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
		catch (SocketTimeoutException _e) {
			if (this.timeoutListener != null) {
				this.timeoutListener.solveTimeout();
			}
			else {
				throw _e;
			}
		}
		catch (SocketException _e) {
			throw _e;
		}
		catch (ReadDataException _e) {
			throw _e;
		}
		catch (IOException _e) {
			throw new ReadDataException(_e);
		}
		catch (Exception _e) {
			throw new ReadDataException(_e);
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
		catch (ReadDataException _e) {
			ClientSide.LOGGER.error(_e);
		}
		catch (SocketTimeoutException _e) {
			ClientSide.LOGGER.warn("Connection timeout");
		}
		catch (SocketException _e) {
			ClientSide.LOGGER.warn("Socket interrupt because " + _e.getMessage());
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
	public void setDataParser(ParserInterface __dataParser) {
		this.parser = __dataParser;
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
	 * @see logia.socket.Interface.SocketClientInterface#setReturned(logia.socket.Interface.ReadDataInterface)
	 */
	@Override
	public void setReturned(ReadDataInterface __returned) {
		this.returned.add(__returned);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see logia.socket.Interface.SocketClientInterface#setTimeoutListener(logia.socket.Interface.SocketTimeoutListener)
	 */
	@Override
	public void setTimeoutListener(SocketTimeoutListener __listener) {
		this.timeoutListener = __listener;
	}

}
