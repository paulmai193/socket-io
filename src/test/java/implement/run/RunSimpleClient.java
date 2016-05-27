package implement.run;

import implement.client.datapackage.Contact;
import implement.client.datapackage.ContactData;
import implement.client.datapackage.ListNumberData;
import implement.define.Config;

import java.io.IOException;
import java.net.SocketException;

import logia.io.parser.DataParserByAnnotation;
import logia.socket.Interface.ParserInterface;
import logia.socket.Interface.SocketClientInterface;
import logia.socket.client.DefaultSocketClient;

/**
 * The Class RunSimpleClient.
 *
 * @author Paul Mai
 */
public class RunSimpleClient {

	/**
	 * The main method.
	 *
	 * @param args the arguments
	 */
	public static void main(String[] args) {
		try {

			ParserInterface parser = new DataParserByAnnotation(Config.DATA_PACKAGE_PATH_CLIENT, 1024 * 1024);

			final SocketClientInterface client = new DefaultSocketClient("localhost", 1234, 5 * 60000, parser);
			client.connect();
			if (client.isConnected()) {

				long c = System.currentTimeMillis();

				ContactData data = new ContactData();
				data.addContact(new Contact("Dai", "0933101959", ""));
				data.addContact(new Contact("Thuan", "0165505756", ""));
				ListNumberData result = (ListNumberData) client.echoAndWait(data);
				result.executeData();

				long d = System.currentTimeMillis();
				System.out.println("FINISH SEND FILE AFTER " + (d - c) / 1000 + " s");

				client.disconnect();
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
