package implement.run;

import implement.define.Config;

import java.net.Socket;
import java.net.SocketException;

import logia.io.exception.ConnectionErrorException;
import logia.io.parser.DataParserByAnnotation;
import logia.socket.Interface.ParserInterface;
import logia.socket.Interface.SocketClientInterface;
import logia.socket.listener.AcceptClientListener;
import logia.socket.server.DefaultClientHandler;
import logia.socket.server.ServerSide;

/**
 * The Class RunSimpleServer.
 *
 * @author Paul Mai
 */
public class RunSimpleServer extends Thread {

	/**
	 * The main method.
	 *
	 * @param args the arguments
	 * @throws InterruptedException the interrupted exception
	 */
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
				ParserInterface parser = new DataParserByAnnotation(Config.DATA_PACKAGE_PATH_SERVER, bufferSize);
				SocketClientInterface clientSocket = new DefaultClientHandler(server, socket, parser);
				server.addClient(clientSocket);
				new Thread(clientSocket).start();
			}

		});
		server.start();

	}

}
