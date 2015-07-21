package implement.run;

import implement.define.Config;

import java.io.IOException;
import java.net.Socket;
import java.net.SocketTimeoutException;

import logia.io.parser.DataParserByAnnotation;
import logia.socket.Interface.AcceptClientListener;
import logia.socket.Interface.SocketClientInterface;
import logia.socket.server.ClientOnServerSide;
import logia.socket.server.ServerSide;
import logia.utility.pool.ThreadPoolFactory;

public class RunSimpleServer extends Thread {

	public static void main(String[] args) {
		final ThreadPoolFactory pool = new ThreadPoolFactory(1, 5, Thread.MAX_PRIORITY, true);
		final ServerSide server = new ServerSide(1234, 0, 360000);
		final DataParserByAnnotation parser = new DataParserByAnnotation(Config.DATA_PACKAGE_PATH_SERVER);
		server.setAcceptClientListener(new AcceptClientListener() {

			@Override
			public void acceptClient(Socket socket) throws SocketTimeoutException, IOException {
				SocketClientInterface clientSocket = new ClientOnServerSide(server, socket, parser);
				server.addClient(clientSocket);
				pool.start(clientSocket);
			}

		});
		server.start();

	}

}
