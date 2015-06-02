package logia.socket.server;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.net.SocketException;
import java.net.SocketTimeoutException;

import logia.io.parser.DataParser;
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

	/** The server socket. */
	private SocketServerInterface serverSocket;

	/** The timeout listener. */
	private SocketTimeoutListener timeoutListener;

	/** The socket. */
	private Socket                socket;

	/** The input stream. */
	private InputStream           inputStream;

	/** The output stream. */
	private OutputStream          outputStream;

	/** The data parser. */
	private DataParser            parser;

	/** The is connected. */
	private boolean               isConnected;

	/** The start time. */
	private final long            startTime;

	/** The id. */
	private String                id;

	/**
	 * Instantiates a new client socket on server side.
	 *
	 * @param serverSocket the server socket
	 * @param socket the socket
	 * @throws IOException Signals that an I/O exception has occurred.
	 */
	public ClientOnServerSide(SocketServerInterface serverSocket, Socket socket) throws IOException {
		this.isConnected = false;
		this.parser = new DataParser();
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
	 * @param definePath the define path
	 * @throws IOException Signals that an I/O exception has occurred.
	 */
	public ClientOnServerSide(SocketServerInterface serverSocket, Socket socket, String definePath) throws IOException {
		this.isConnected = false;
		this.parser = new DataParser(definePath);
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
	 * @param definePath the define path
	 * @param timeoutListener the timeout listener
	 * @throws IOException Signals that an I/O exception has occurred.
	 */
	public ClientOnServerSide(SocketServerInterface serverSocket, Socket socket, String definePath, SocketTimeoutListener timeoutListener)
			throws IOException {
		this.isConnected = false;
		this.parser = new DataParser(definePath);
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
	 * @see socket.Interface.SocketInterface#connect()
	 */
	/**
	 * Connect.
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
	 * @see socket.Interface.SocketInterface#disconnect()
	 */
	/**
	 * Disconnect.
	 */
	@Override
	public void disconnect() {
		this.isConnected = false;
		this.serverSocket.removeClient(this);
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
		try {
			this.finalize();
		}
		catch (Throwable e) {
			e.printStackTrace();
		}
		System.out.println("A client disconnected");
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see logia.socket.Interface.SocketClientInterface#echo(logia.socket.Interface.WriteDataInterface, int)
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

	/*
	 * (non-Javadoc)
	 * 
	 * @see socket.Interface.SocketClientInterface#getDataParser()
	 */
	/**
	 * Gets the data parser.
	 *
	 * @return the data parser
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
	/**
	 * Gets the id.
	 *
	 * @return the id
	 */
	@Override
	public String getId() {
		return this.id;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see socket.Interface.SocketInterface#getInputStream()
	 */
	/**
	 * Gets the input stream.
	 *
	 * @return the input stream
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
	/**
	 * Gets the live time.
	 *
	 * @return the live time
	 */
	@Override
	public long getLiveTime() {
		return System.currentTimeMillis() - this.startTime;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see socket.Interface.SocketInterface#getOutputStream()
	 */
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

	/*
	 * (non-Javadoc)
	 * 
	 * @see socket.Interface.SocketInterface#isConnected()
	 */
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

	/**
	 * Sets the ID for current socket client.
	 *
	 * @param id the new id
	 */
	protected void setId(String id) {
		this.id = id;
	}

}
