package implement.run;

import java.net.Socket;

import implement.define.Config;
import logia.io.exception.ConnectionErrorException;
import logia.io.exception.ReadDataException;
import logia.io.parser.DataBinaryParser;
import logia.socket.Interface.ParserInterface;
import logia.socket.Interface.SocketClientInterface;
import logia.socket.listener.AcceptClientListener;
import logia.socket.server.DefaultClientHandler;
import logia.socket.server.DefaultSocketServer;

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

		final DefaultSocketServer server = new DefaultSocketServer(1234, 0, 5 * 60000);

		server.setAcceptClientListener(new AcceptClientListener() {

			@Override
			public void acceptClient(Socket socket) throws ConnectionErrorException, ClassNotFoundException, ReadDataException {
                ParserInterface parser = new DataBinaryParser(
                        Config.DATA_PACKAGE_PATH_SERVER);
				SocketClientInterface clientSocket = new DefaultClientHandler(server, socket, parser);
				clientSocket.connect();
				server.addClient(clientSocket);
				// new Thread(clientSocket).start();
			}

		});
		server.start();

	}

}
