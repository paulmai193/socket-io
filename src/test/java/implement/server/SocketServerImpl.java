package implement.server;

import implement.define.Config;
import implement.threadpool.ThreadFactory;

import java.io.IOException;
import java.net.Socket;
import java.net.SocketTimeoutException;

import logia.socket.Interface.SocketClientInterface;
import logia.socket.server.AbstractServerSocket;

/**
 * The Class SocketServerImpl.
 * 
 * @author Paul Mai
 */
public class SocketServerImpl extends AbstractServerSocket {

	/**
	 * Instantiates a new socket server impl.
	 *
	 * @param port the port
	 * @param timeout the timeout
	 * @param maxLiveTime the max live time
	 */
	public SocketServerImpl(int port, int timeout, long maxLiveTime) {
		super(port, timeout, maxLiveTime);
		ThreadFactory.getInstance().connect(10);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see socket.server.AbstractServerSocket#acceptClient(socket.Interface.SocketServerInterface, java.net.Socket)
	 */
	@Override
	public void executeClient(Socket socket) throws SocketTimeoutException, IOException {
		SocketClientInterface clientSocket = new ClientOnServerImpl(this, socket, Config.DATA_PACKAGE_PATH_SERVER);
		addClient(clientSocket);
		ThreadFactory.getInstance().start(clientSocket);
	}

}
