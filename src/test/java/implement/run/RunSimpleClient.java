package implement.run;

import implement.client.datapackage.Contact;
import implement.client.datapackage.ContactData;
import implement.define.Config;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import logia.io.parser.DataParserByAnnotation;
import logia.socket.Interface.SocketClientInterface;
import logia.socket.client.ClientSide;

public class RunSimpleClient {

	public static void main(String[] args) {
		try {
			DataParserByAnnotation parser = new DataParserByAnnotation(Config.DATA_PACKAGE_PATH_CLIENT);
			SocketClientInterface client = new ClientSide("localhost", 1234, 0, parser);
			client.connect();

			new Thread(client).start();

			List<Contact> contacts = new ArrayList<Contact>();
			for (int i = 1; i < 11; i++) {
				Contact contact = new Contact("Name " + i, "Phone " + i, "Email " + i);
				contacts.add(contact);
			}
			ContactData contactData = new ContactData();
			contactData.setContacts(contacts);
			client.echo(contactData, 4);

			Thread.sleep(30000);

			client.disconnect();
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
