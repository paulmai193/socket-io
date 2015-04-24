package implement.run;

import implement.client.ClientImpl;
import implement.client.datapackage.TestData;
import implement.define.Config;

import java.io.IOException;

import logia.socket.Interface.SocketClientInterface;

public class RunSimpleClient {

	public static void main(String[] args) {
		try {
			// Start socket
			SocketClientInterface client = new ClientImpl("localhost", 3333, 0, Config.DATA_PACKAGE_PATH_CLIENT);
			client.connect();

			new Thread(client).start();

			TestData testData = new TestData(0, null, null);
			client.echo(testData, 1001);

			Thread.sleep(30000);

			client.disconnect();
		}
		catch (IOException e) {
			e.printStackTrace();
		}
		catch (Exception e) {
			e.printStackTrace();
		}
	}
}
