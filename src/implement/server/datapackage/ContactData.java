package server.datapackage;

import java.util.ArrayList;
import java.util.List;

import logia.socket.Interface.ReadDataInterface;
import logia.socket.Interface.SocketClientInterface;
import logia.socket.Interface.WriteDataInterface;
import server.ClientOnServerImpl;
import define.Command;

/**
 * The Class ContactData. This class implements both ReadDataInterface and WriteDataInterface to read
 * / write list contact data package
 * 
 * @author Paul Mai
 */
public class ContactData implements ReadDataInterface, WriteDataInterface {
	
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
	public void executeData(SocketClientInterface clientSocket) {
		System.out.println("Client send contact list");
		ListNumberData listNumberData = new ListNumberData();
		for (Contact contact : contacts) {
			System.out.println(contact.getName() + " - " + contact.getEmail() + " - " + contact.getPhone());
			listNumberData.addnumber(Double.parseDouble(contact.getPhone()));
		}
		System.out.println("Return list phone to client");
		try {
			((ClientOnServerImpl) clientSocket).echo(listNumberData, Command.LIST_NUMBER);
		}
		catch (Exception e) {
			e.printStackTrace();
		}
//		((ClientConnectSocket)clientSocket).echoClient(listNumberData, Command.LIST_NUMBER);		
	}

	/* (non-Javadoc)
	 * @see socket.listener.ReadDataListener#executeData()
	 */
	@Override
	public void executeData() throws Exception {
		
	}
	
}
