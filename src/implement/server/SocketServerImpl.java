package server;

import java.io.IOException;
import java.net.Socket;
import java.net.SocketTimeoutException;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

import logia.socket.server.AbstractServerSocket;
import threadpool.ThreadFactory;
import define.Config;

/**
 * The Class SocketServerImpl.
 * 
 * @author Paul Mai
 */
public class SocketServerImpl extends AbstractServerSocket {
	
	/** The _hash set. */
	public Set<ClientOnServerImpl> _hashSet;
	
	/**
	 * Instantiates a new socket server impl.
	 *
	 * @param port the port
	 */
	public SocketServerImpl(int port) {
		super(port);
		_hashSet = Collections.synchronizedSet(new HashSet<ClientOnServerImpl>());
		ThreadFactory.getInstance().connect(10);
	}
	
	/**
	 * Instantiates a new socket server impl.
	 *
	 * @param port the port
	 * @param timeout the timeout
	 */
	public SocketServerImpl(int port, int timeout) {
		super(port, timeout);
		_hashSet = Collections.synchronizedSet(new HashSet<ClientOnServerImpl>());
		ThreadFactory.getInstance().connect(10);
	}
	
	/* (non-Javadoc)
	 * @see socket.server.AbstractServerSocket#acceptClient(socket.Interface.SocketServerInterface, java.net.Socket)
	 */
	@Override
	public void acceptClient(Socket socket) throws SocketTimeoutException, IOException {
		ClientOnServerImpl clientSocket = new ClientOnServerImpl(this, socket, Config.DATA_PACKAGE_PATH_SERVER);
		_hashSet.add(clientSocket);
		ThreadFactory.getInstance().start(clientSocket);
	}
	
}
