package logia.socket.server;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.net.SocketException;
import java.net.SocketTimeoutException;

import logia.socket.Interface.ParserInterface;
import logia.socket.Interface.SocketClientInterface;
import logia.socket.Interface.SocketServerInterface;
import logia.socket.Interface.SocketTimeoutListener;
import logia.socket.Interface.WriteDataInterface;

/**
 * The Class ClientOnServerSide.
 * 
 * @author Paul Mai
 */
public class ClientOnServerSide implements SocketClientInterface {

	/** The id. */
	private String                id;

	/** The input stream. */
	private InputStream           inputStream;

	/** The is connected. */
	private boolean               isConnected;

	/** The output stream. */
	private OutputStream          outputStream;

	/** The data parser. */
	private ParserInterface       parser;

	/** The server socket. */
	private SocketServerInterface serverSocket;

	/** The socket. */
	private Socket                socket;

	/** The start time. */
	private final long            startTime;

	/** The timeout listener. */
	private SocketTimeoutListener timeoutListener;

	/**
	 * Instantiates a new client socket on server side.
	 *
	 * @param serverSocket the server socket
	 * @param socket the socket
	 * @throws IOException Signals that an I/O exception has occurred.
	 */
	public ClientOnServerSide(SocketServerInterface serverSocket, Socket socket) throws IOException {
		this.isConnected = false;
		this.startTime = System.currentTimeMillis();
		this.serverSocket = serverSocket;
		this.socket = socket;
		this.setId(socket.getRemoteSocketAddress().toString());

		this.connect();
	}

	/**
	 * Instantiates a new client socket on server side.
	 *
	 * @param serverSocket the server socket
	 * @param socket the socket
	 * @param dataParser the data parser
	 * @throws IOException Signals that an I/O exception has occurred.
	 */
	public ClientOnServerSide(SocketServerInterface serverSocket, Socket socket, ParserInterface dataParser) throws IOException {
		this.isConnected = false;
		this.parser = dataParser;
		this.startTime = System.currentTimeMillis();
		this.serverSocket = serverSocket;
		this.socket = socket;
		this.setId(socket.getRemoteSocketAddress().toString());

		this.connect();
	}

	/**
	 * Instantiates a new client on server side.
	 *
	 * @param serverSocket the server socket
	 * @param socket the socket
	 * @param dataParser the data parser
	 * @param timeoutListener the timeout listener
	 * @throws IOException Signals that an I/O exception has occurred.
	 */
	public ClientOnServerSide(SocketServerInterface serverSocket, Socket socket, ParserInterface dataParser, SocketTimeoutListener timeoutListener)
	        throws IOException {
		this.isConnected = false;
		this.parser = dataParser;
		this.startTime = System.currentTimeMillis();
		this.serverSocket = serverSocket;
		this.socket = socket;
		this.setId(socket.getRemoteSocketAddress().toString());
		this.timeoutListener = timeoutListener;

		this.connect();
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
				this.inputStream = this.socket.getInputStream();
				this.outputStream = this.socket.getOutputStream();
				this.isConnected = true;
			}
			catch (IOException e) {
				e.printStackTrace();
			}

			System.out.println("A client connected, waiting to read data from client...");
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
				e.printStackTrace();
			}
			this.inputStream = null;
		}
		if (this.outputStream != null) {
			try {
				this.outputStream.close();
			}
			catch (IOException e) {
				e.printStackTrace();
			}
			this.outputStream = null;
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
		System.out.println("A client disconnected");
		this.serverSocket.removeClient(this);
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
			e.printStackTrace();
			System.err.println(e.getMessage() + ". Disconnect");
			this.disconnect();
		}
		catch (IOException e) {
			e.printStackTrace();
			System.err.println(e.getMessage() + ". Disconnect");
			this.disconnect();
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
	 * @see logia.socket.Interface.SocketClientInterface#setTimeoutListener(logia.socket.Interface.SocketTimeoutListener)
	 */
	@Override
	public void setTimeoutListener(SocketTimeoutListener listener) {
		this.timeoutListener = listener;
	}

}
