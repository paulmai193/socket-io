package run;

import java.io.IOException;

import client.ClientImpl;
import client.datapackage.TestData;
import define.Config;

public class RunSimpleClient {

	public static void main(String[] args) {
		try {
			// Start socket
			ClientImpl client = new ClientImpl("localhost", 3333, 0,
					Config.DATA_PACKAGE_PATH_CLIENT);
			client.connect();

			new Thread(client).start();

			TestData testData = new TestData(0, null, null);
			client.echo(testData, 1001);

			Thread.sleep(30000);

			client.disconnect();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
