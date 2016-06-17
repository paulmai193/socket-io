package implement.run;

import java.io.IOException;
import java.net.SocketException;

import implement.client.datapackage.Contact;
import implement.client.datapackage.ContactData;
import implement.define.Config;
import logia.io.parser.DataBinaryParser;
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

			ParserInterface parser = new DataBinaryParser(Config.DATA_PACKAGE_PATH_CLIENT);

			final SocketClientInterface client = new DefaultSocketClient("localhost", 1234,
			        5 * 60000, parser);
			client.connect();
			if (client.isConnected()) {

				long c = System.currentTimeMillis();

				ContactData data = new ContactData();
				data.addContact(new Contact("Dai", "0933101959", ""));
				// data.addContact(new Contact("", "", ""));
				client.echo(data);

				// ListNumberData result = (ListNumberData) client.echoAndWait(data);
				// result.executeData();

				data = new ContactData();
				data.addContact(new Contact("Thuan", "01655051756", ""));
				client.echo(data);

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
