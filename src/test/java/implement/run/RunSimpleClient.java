package implement.run;

import implement.client.datapackage.FileData;
import implement.client.datapackage.ResultData;
import implement.define.Config;

import java.io.File;
import java.io.IOException;
import java.net.SocketException;

import logia.io.parser.DataParserByXML;
import logia.socket.Interface.ParserInterface;
import logia.socket.Interface.SocketClientInterface;
import logia.socket.client.ClientSide;

public class RunSimpleClient {

	public static void main(String[] args) {
		File testFile = new File("C:/Users/Paul Mai/Desktop/fm.mp4");
		try {

			ParserInterface parser = new DataParserByXML(Config.DATA_PACKAGE_PATH_CLIENT);

			final SocketClientInterface client = new ClientSide("localhost", 1234, 0, parser);
			client.connect();
			if (client.isConnected()) {
				new Thread(client).start();
				long c = System.currentTimeMillis();
				FileData sendFileData = new FileData(testFile);
				ResultData result = (ResultData) client.echoAndWait(sendFileData, 5);
				result.executeData();

				long d = System.currentTimeMillis();
				System.out.println("FINISH SEND FILE AFTER " + (d - c) / 1000 + " s");
			}
			else {
				throw new SocketException("Cannot connect to socket server");
			}

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
