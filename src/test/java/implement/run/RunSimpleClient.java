package implement.run;

import implement.client.datapackage.MessageData;
import implement.define.Config;

import java.io.File;
import java.io.IOException;
import java.net.SocketException;

import logia.io.parser.TCPDataParserByAnnotation;
import logia.socket.Interface.ParserInterface;
import logia.socket.Interface.SocketClientInterface;
import logia.socket.client.ClientSide;

public class RunSimpleClient {

	public static void main(String[] args) {
		File testFile = new File("D:/Video/[Yumei-Anime]Doraemon_Stand_By_Me_Ver2.mp4");
		try {
			// Writes to this byte array output stream
			// long a = System.currentTimeMillis();
			// byte[] data = FileUtils.readFileToByteArray(testFile);
			// long b = System.currentTimeMillis();
			// System.out.println("FINISH BUFFER A FILE AFTER " + (b - a) / 1000 + " s");

			ParserInterface parser = new TCPDataParserByAnnotation(Config.DATA_PACKAGE_PATH_CLIENT);

			final SocketClientInterface client = new ClientSide("localhost", 1234, 0, parser);
			client.connect();
			if (client.isConnected()) {
				new Thread(client).start();
				long c = System.currentTimeMillis();
				// FileData sendFileData = new FileData(testFile);
				// ResultData result = (ResultData) client.echoAndWait(sendFileData, 5);
				// result.executeData();

				MessageData data = new MessageData(100, "");
				client.echo(data, 2);
				Thread.sleep(1000);
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
