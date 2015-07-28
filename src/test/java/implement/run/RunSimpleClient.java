package implement.run;

import implement.define.Config;

import java.io.IOException;

import logia.io.parser.DataParserByAnnotation;
import logia.socket.client.ClientSide;

public class RunSimpleClient {

	public static void main(String[] args) {
		try {
			DataParserByAnnotation parser = new DataParserByAnnotation(Config.DATA_PACKAGE_PATH_CLIENT);

			new ClientSide("localhost", 1234, 0, parser).connect();

			Thread.sleep(1000);

			new ClientSide("localhost", 1234, 0, parser).connect();

			Thread.sleep(1000);

			new ClientSide("localhost", 1234, 0, parser).connect();

			Thread.sleep(1000);

			new ClientSide("localhost", 1234, 0, parser).connect();

			Thread.sleep(1000);

			new ClientSide("localhost", 1234, 0, parser).connect();

			Thread.sleep(10000);

			new ClientSide("localhost", 1234, 0, parser).connect();

			Thread.sleep(10000);

			new ClientSide("localhost", 1234, 0, parser).connect();

			Thread.sleep(10000);

			new ClientSide("localhost", 1234, 0, parser).connect();

			Thread.sleep(10000);

			new ClientSide("localhost", 1234, 0, parser).connect();

			Thread.sleep(10000);

			new ClientSide("localhost", 1234, 0, parser).connect();

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
