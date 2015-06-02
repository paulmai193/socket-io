package implement.run;

import implement.define.Config;

import java.io.IOException;
import java.net.Socket;
import java.net.SocketTimeoutException;

import logia.socket.Interface.AcceptClientListener;
import logia.socket.Interface.SocketClientInterface;
import logia.socket.server.ClientOnServerSide;
import logia.socket.server.ServerSide;
import logia.utility.pool.ThreadPoolFactory;

public class RunSimpleServer extends Thread {

	public static void main(String[] args) {
		ThreadPoolFactory.getInstance().connect(1, 5, Thread.MAX_PRIORITY);
		final ServerSide server = new ServerSide(3333, 5000, 360000);
		server.setAcceptClientListener(new AcceptClientListener() {

			@Override
			public void acceptClient(Socket socket) throws SocketTimeoutException, IOException {
				SocketClientInterface clientSocket = new ClientOnServerSide(server, socket, Config.DATA_PACKAGE_PATH_SERVER);
				server.addClient(clientSocket);
				ThreadPoolFactory.getInstance().start(clientSocket);
			}

		});
		server.start();

	}

}
