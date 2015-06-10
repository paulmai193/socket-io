package implement.run;

import implement.client.datapackage.TestData;
import implement.define.Config;

import java.io.IOException;

import logia.socket.Interface.SocketClientInterface;
import logia.socket.client.ClientSide;

public class RunSimpleClient {

	public static void main(String[] args) {
		try {
			SocketClientInterface client = new ClientSide("192.168.1.27", 3333, 0, Config.DATA_PACKAGE_PATH_CLIENT);
			client.connect();

			new Thread(client).start();

			TestData testData = new TestData(Long.MAX_VALUE, "Babe", "Hello");
			client.echo(testData, 10);

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
