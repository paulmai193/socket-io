package test;
import implement.client.ClientImpl;
import implement.client.datapackage.Contact;
import implement.client.datapackage.ContactData;

import java.io.IOException;

import define.Command;
import define.Config;

public class RunSimpleClient {
	
	public static void main(String[] args) {
		try {			
			// Start socket
			ClientImpl client = new ClientImpl("localhost", 3333, 0, Config.DATA_PACKAGE_PATH_CLIENT);
			client.connect();
			
			new Thread(client).start();			
			ContactData contactData = new ContactData();
			for (int j = 1; j < 20; j++) {
				Contact contact = new Contact("Nguyễn Văn A " + j, "0933101959", "a" + j + "@abc.com");
				contactData.addContact(contact);
			}
			client.echo(contactData, Command.CONTACT);	
						
//			client.disconnect();
		}
		catch (IOException e) {
			e.printStackTrace();
		}
		catch (Exception e) {
			e.printStackTrace();
		}
	}
}
