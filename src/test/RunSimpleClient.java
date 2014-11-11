package test;
import java.io.IOException;

import socket.client.ClientSocket;
import socket.client.datapackage.Contact;
import socket.client.datapackage.ContactData;
import socket.client.datapackage.MessageData;
import define.Command;

public class RunSimpleClient {
	
	public static void main(String[] args) {
		try {			
			// Start socket
			ClientSocket client = new ClientSocket();			
			client.connect();
			new Thread(client).start();
			
			// Waiting 5 seconds before execute test
			Thread.sleep(5000);
			
			// Test 1: Send message
			client.echoServer(new MessageData(100, "Chào thân ái và quyết thắng!!"), Command.RECEIVE_MESSAGE);
			
			// Waiting 5 seconds before execute next test
			Thread.sleep(5000);
			
			// Test 2: Send contact list
			ContactData contacts = new ContactData();
			for (int i = 0; i < 10; i++) {
				Contact contact = new Contact();
				contact.setEmail(i + "@yahoo.com");
				contact.setName("Nguyễn Văn " + i);
				contact.setPhone("0933101959");
				
				contacts.addContact(contact);
			}			
			client.echoServer(contacts, Command.CONTACT);			
			
			// Disconnect after 3 seconds
			Thread.sleep(3000);
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
