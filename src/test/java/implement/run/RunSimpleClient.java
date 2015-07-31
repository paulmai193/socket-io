package implement.run;

import implement.client.datapackage.MessageData;
import implement.client.datapackage.TestData;
import implement.define.Config;

import java.io.IOException;
import java.net.SocketException;

import logia.io.parser.DataParserByAnnotation;
import logia.socket.Interface.ParserInterface;
import logia.socket.Interface.SocketClientInterface;
import logia.socket.client.ClientSide;

public class RunSimpleClient {

	public static void main(String[] args) {
		try {
			ParserInterface parser = new DataParserByAnnotation(Config.DATA_PACKAGE_PATH_CLIENT);

			final SocketClientInterface client = new ClientSide("localhost", 1234, 0, parser);
			// SocketTimeoutListener timeout = new SocketTimeoutListener() {
			//
			// @Override
			// public void solveTimeout() {
			// synchronized (client) {
			// try {
			// client.wait();
			// System.out.println("CLIENT IDLE");
			// }
			// catch (InterruptedException e) {
			// e.printStackTrace();
			// client.disconnect();
			// }
			// }
			// }
			// };
			// client.setTimeoutListener(timeout);
			client.connect();
			if (client.isConnected()) {
				new Thread(client).start();

				TestData data = new TestData(1203985, "MobSir", "I'm billionare!");
				client.echo(data, 10);

				data = new TestData(203528095, "MobSir", "I'm billionare!");
				client.echo(data, 10);

				MessageData msg = new MessageData(102, "WTF");
				client.echo(msg, 2);

				data = new TestData(23598125, "MobSir", "I'm billionare!");
				client.echo(data, 10);

				msg = new MessageData(12345, "WTF");
				client.echo(msg, 2);

				data = new TestData(89603, "MobSir", "I'm billionare!");
				client.echo(data, 10);

			}
			else {
				throw new SocketException("Cannot connect to socket server");
			}

			Thread.sleep(10000);
		}
		catch (IOException e) {
			e.printStackTrace();
		}
		catch (Exception e) {
			e.printStackTrace();
		}
		finally {
			System.exit(0);
		}
	}
}
