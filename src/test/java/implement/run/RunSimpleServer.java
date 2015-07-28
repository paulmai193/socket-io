package implement.run;

import implement.define.Config;

import java.io.IOException;
import java.net.Socket;
import java.net.SocketTimeoutException;
import java.util.Arrays;

import logia.io.parser.DataParserByAnnotation;
import logia.socket.Interface.SocketClientInterface;
import logia.socket.listener.AcceptClientListener;
import logia.socket.server.ClientOnServerSide;
import logia.socket.server.ServerSide;
import logia.utility.pool.ThreadPoolFactory;

public class RunSimpleServer extends Thread {

	public static void main(String[] args) throws InterruptedException {
		Runtime runtime = Runtime.getRuntime();
		long maxMemory = runtime.maxMemory();
		long allocatedMemory = runtime.totalMemory();
		long freeMemory = runtime.freeMemory();

		final ThreadPoolFactory pool = new ThreadPoolFactory(10, 10, Thread.MAX_PRIORITY, true);
		final ServerSide server = new ServerSide(1234, 0, 5 * 60000);
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

		Thread.sleep(60000);

		long maxMemory2 = runtime.maxMemory();
		long allocatedMemory2 = runtime.totalMemory();
		long freeMemory2 = runtime.freeMemory();
		System.out.println("using memory: " + (freeMemory - freeMemory2) / 1024);
		System.out.println("free memory: " + (freeMemory / 1024) + " vs " + (freeMemory2 / 1024));
		System.out.println("allocated memory: " + (allocatedMemory / 1024) + " vs " + (allocatedMemory2 / 1024));
		System.out.println("max memory: " + (maxMemory / 1024) + " vs " + (maxMemory2 / 1024));
		System.out.println("total free memory: " + ((freeMemory + (maxMemory - allocatedMemory)) / 1024) + " vs "
		        + ((freeMemory2 + (maxMemory2 - allocatedMemory2)) / 1024));

		System.out.println(Arrays.toString(server.getListClients().toArray()));

	}

}
