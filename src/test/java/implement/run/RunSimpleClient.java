package implement.run;

import implement.client.datapackage.TestData;
import implement.define.Config;

import java.io.IOException;

import logia.io.parser.DataParserByAnnotation;
import logia.socket.Interface.ReadDataInterface;
import logia.socket.Interface.SocketClientInterface;
import logia.socket.client.ClientSide;

public class RunSimpleClient {

	public static void main(String[] args) {
		try {
			DataParserByAnnotation parser = new DataParserByAnnotation(Config.DATA_PACKAGE_PATH_CLIENT);
			SocketClientInterface client = new ClientSide("localhost", 1234, 0, parser);
			client.connect();

			new Thread(client).start();

			TestData testData = new TestData(19204, "Mai Bat Hu", "1 billion");

			ReadDataInterface returnData = client.echoAndWait(testData, 10);
			returnData.executeData(client);

			// client.echo(testData, 10);
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
