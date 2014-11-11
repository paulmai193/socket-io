package socket.server.datapackage;

import java.util.ArrayList;
import java.util.List;

import define.Command;

import socket.listener.ReadDataListener;
import socket.listener.SocketListener;
import socket.listener.WriteDataListener;
import socket.server.ClientConnectSocket;

/**
 * The Class ContactData. This class implements both ReadDataListener and WriteDataListener to read
 * / write Message data package
 * 
 * @author Paul Mai
 */
public class ContactData implements ReadDataListener, WriteDataListener {
	
	/** The contacts. */
	private List<Contact> contacts;
	
	/**
	 * Instantiates a new contact data.
	 */
	public ContactData() {
		this.contacts = new ArrayList<Contact>();
	}
	
	/**
	 * Gets the contacts.
	 * 
	 * @return the contacts
	 */
	public List<Contact> getContacts() {
		return contacts;
	}
	
	/**
	 * Sets the contacts.
	 * 
	 * @param contacts the new contacts
	 */
	public void setContacts(List<Contact> contacts) {
		this.contacts = contacts;
	}
	
	/**
	 * Adds the contact.
	 * 
	 * @param contact the contact
	 */
	public void addContact(Contact contact) {
		this.contacts.add(contact);
	}
	
	/* (non-Javadoc)
	 * 
	 * @see datapackage.ReadDataListener#executeData(socket.ClientBoundSocket) */
	@Override
	public void executeData(SocketListener clientSocket) {
		System.out.println("Client send contact list");
		ListNumberData listNumberData = new ListNumberData();
		for (Contact contact : contacts) {
			System.out.println(contact.getName() + " - " + contact.getEmail() + " - " + contact.getPhone());
			listNumberData.addnumber(Double.parseDouble(contact.getPhone()));
		}
		System.out.println("Return list phone to client");
		((ClientConnectSocket)clientSocket).echoClient(listNumberData, Command.LIST_NUMBER);
		
	}

	/* (non-Javadoc)
	 * @see socket.listener.ReadDataListener#executeData()
	 */
	@Override
	public void executeData() throws Exception {
		
	}
	
}
