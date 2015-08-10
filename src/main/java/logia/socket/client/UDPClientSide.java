package logia.socket.client;

import java.io.InputStream;
import java.io.OutputStream;
import java.net.DatagramSocket;
import java.util.Queue;
import java.util.concurrent.LinkedBlockingQueue;

import logia.socket.Interface.ParserInterface;
import logia.socket.Interface.ReadDataInterface;
import logia.socket.Interface.SocketClientInterface;
import logia.socket.Interface.WriteDataInterface;
import logia.socket.listener.SocketTimeoutListener;

import org.apache.log4j.Logger;

public class UDPClientSide implements SocketClientInterface {

	/** The host. */
	private final String             HOST;

	/** The id. */
	private String                   id;

	/** The is wait for response. */
	private boolean                  isWait;

	/** The logger. */
	private final Logger             LOGGER = Logger.getLogger("TCP SOCKET CLIENT");

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
	protected DatagramSocket         socket;

	/** The start time. */
	protected final long             START_TIME;

	/** The timeout of connection. */
	protected final int              TIME_OUT;

	public UDPClientSide(String host, int port, int timeout) {
		this.isConnected = false;
		this.START_TIME = System.currentTimeMillis();
		this.HOST = host;
		this.PORT = port;
		this.TIME_OUT = timeout;
		this.isWait = false;
		this.returned = new LinkedBlockingQueue<ReadDataInterface>(1);
	}

	@Override
	public void connect() {
		// TODO Auto-generated method stub

	}

	@Override
	public void disconnect() {
		// TODO Auto-generated method stub

	}

	@Override
	public void echo(WriteDataInterface data, Object command) throws Exception {
		// TODO Auto-generated method stub

	}

	@Override
	public ReadDataInterface echoAndWait(WriteDataInterface data, Object command) throws Exception {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public ParserInterface getDataParser() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String getId() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public InputStream getInputStream() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public long getLiveTime() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public OutputStream getOutputStream() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public SocketTimeoutListener getTimeoutListener() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public boolean isConnected() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean isWaitForReturn() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public void listen() {
		// TODO Auto-generated method stub

	}

	@Override
	public void run() {
		// TODO Auto-generated method stub

	}

	@Override
	public void setDataParser(ParserInterface dataParser) {
		// TODO Auto-generated method stub

	}

	@Override
	public void setId(String id) {
		// TODO Auto-generated method stub

	}

	@Override
	public void setReturned(ReadDataInterface returned) {
		// TODO Auto-generated method stub

	}

	@Override
	public void setTimeoutListener(SocketTimeoutListener listener) {
		// TODO Auto-generated method stub

	}

}
