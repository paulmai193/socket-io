package implement.client;

import java.io.IOException;
import java.net.Socket;
import java.net.SocketException;
import java.net.UnknownHostException;

import logia.socket.Interface.WriteDataInterface;
import logia.socket.client.AbstractClientSocket;

/**
 * The Class ClientImpl.
 * 
 * @author Paul Mai
 */
public class ClientImpl extends AbstractClientSocket {

	/** The host. */
	private String host;

	/** The port. */
	private int    port;

	/** The timeout. */
	private int    timeout;

	/**
	 * The is connected.
	 * 
	 * @throws IOException Signals that an I/O exception has occurred.
	 */

	public ClientImpl() throws IOException {
		super();
		host = "localhost";
		port = 3333;
		timeout = 0;
	}

	/**
	 * Instantiates a new client impl.
	 * 
	 * @param host the host
	 * @param port the port
	 * @param timeout the timeout
	 * @throws IOException Signals that an I/O exception has occurred.
	 */
	public ClientImpl(String host, int port, int timeout) throws IOException {
		super();
		this.host = host;
		this.port = port;
		this.timeout = timeout;
	}

	/**
	 * Instantiates a new client impl.
	 *
	 * @param host the host
	 * @param port the port
	 * @param timeout the timeout of socket when connected
	 * @param definePath the define path locate the xml define data package
	 * @throws IOException Signals that an I/O exception has occurred.
	 */
	public ClientImpl(String host, int port, int timeout, String definePath) throws IOException {
		super(definePath);
		this.host = host;
		this.port = port;
		this.timeout = timeout;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see socket.client.AbstractClientSocket#connect()
	 */
	@Override
	public void connect() {
		if (!isConnected()) {
			try {
				socket = new Socket(host, port);
				if (timeout > 0) {
					socket.setSoTimeout(timeout);
				}
				outputStream = socket.getOutputStream();
				inputStream = socket.getInputStream();
				isConnected = true;
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
	@Override
	public void disconnect() {
		isConnected = false;
		if (outputStream != null) {
			try {
				outputStream.close();
			}
			catch (IOException e) {
				e.printStackTrace();
			}
			outputStream = null;
		}
		if (inputStream != null) {
			try {
				inputStream.close();
			}
			catch (IOException e) {
				e.printStackTrace();
			}
			inputStream = null;
		}
		if (socket != null) {
			try {
				socket.close();
			}
			catch (IOException e) {
				e.printStackTrace();
			}
			socket = null;
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
	@Override
	public void echo(WriteDataInterface data, int command) throws Exception {
		synchronized (this) {
			connect();
			parser.applyOutputStream(outputStream, data, command);
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see logia.socket.Interface.SocketClientInterface#listen()
	 */
	@Override
	public void listen() {
		try {
			parser.applyInputStream(this);
		}
		catch (Exception e) {
			System.err.println(e.getMessage() + ". Disconnect");
			disconnect();
		}
	}
}
