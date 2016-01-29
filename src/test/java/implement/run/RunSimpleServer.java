package implement.run;

import implement.define.Config;

import java.net.Socket;
import java.net.SocketException;

import logia.io.exception.ConnectionErrorException;
import logia.io.parser.DataParserByXML;
import logia.socket.Interface.ParserInterface;
import logia.socket.Interface.SocketClientInterface;
import logia.socket.listener.AcceptClientListener;
import logia.socket.server.ClientOnServerSide;
import logia.socket.server.ServerSide;

public class RunSimpleServer extends Thread {

	public static void main(String[] args) throws InterruptedException {

		final ServerSide server = new ServerSide(1234, 0, 5 * 60000);

		server.setAcceptClientListener(new AcceptClientListener() {

			@Override
			public void acceptClient(Socket socket) throws ConnectionErrorException, ClassNotFoundException {
				int bufferSize = 10 * 1024 * 1024;
				try {
					socket.setReceiveBufferSize(10 * 1024 * 1024);
				}
				catch (SocketException e) {
					e.printStackTrace();
				}
				ParserInterface parser = new DataParserByXML(Config.DATA_PACKAGE_PATH_SERVER, bufferSize);
				SocketClientInterface clientSocket = new ClientOnServerSide(server, socket, parser);
				server.addClient(clientSocket);
				new Thread(clientSocket).start();
			}

		});
		server.start();

	}

}
