package implement.server.datapackage;

import implement.define.Command;

import java.util.ArrayList;
import java.util.List;

import logia.io.annotation.IOCommand;
import logia.io.annotation.IOData;
import logia.io.annotation.type.DataType;
import logia.socket.Interface.ReadDataInterface;
import logia.socket.Interface.SocketClientInterface;
import logia.socket.Interface.WriteDataInterface;
import logia.socket.server.ClientOnServerSide;

/**
 * The Class ContactData. This class implements both ReadDataInterface and WriteDataInterface to read / write list contact data package
 * 
 * @author Paul Mai
 */
@IOCommand(/* type = { CommandType.READER, CommandType.WRITER }, */value = 4)
public class ContactData implements ReadDataInterface, WriteDataInterface {

	/** The contacts. */
	@IOData(order = 1, type = DataType.LIST)
	private List<Contact> contacts;

	/**
	 * Instantiates a new contact data.
	 */
	public ContactData() {
		this.contacts = new ArrayList<Contact>();
	}

	/**
	 * Adds the contact.
	 * 
	 * @param contact the contact
	 */
	public void addContact(Contact contact) {
		this.contacts.add(contact);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see socket.listener.ReadDataListener#executeData()
	 */
	@Override
	public void executeData() throws Exception {

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see datapackage.ReadDataListener#executeData(socket.ClientBoundSocket)
	 */
	@Override
	public void executeData(SocketClientInterface clientSocket) {
		System.out.println("Client send contact list");
		ListNumberData listNumberData = new ListNumberData();
		for (Contact contact : this.contacts) {
			System.out.println(contact.getName() + " - " + contact.getEmail() + " - " + contact.getPhone());
			listNumberData.addnumber(Double.parseDouble(contact.getPhone()));
		}
		System.out.println("Return list phone to client");
		try {
			((ClientOnServerSide) clientSocket).echo(listNumberData, Command.LIST_NUMBER);
		}
		catch (Exception e) {
			e.printStackTrace();
		}
		// ((ClientConnectSocket)clientSocket).echoClient(listNumberData, Command.LIST_NUMBER);
	}

	/**
	 * Gets the contacts.
	 * 
	 * @return the contacts
	 */
	public List<Contact> getContacts() {
		return this.contacts;
	}

	/**
	 * Sets the contacts.
	 * 
	 * @param contacts the new contacts
	 */
	public void setContacts(List<Contact> contacts) {
		this.contacts = contacts;
	}

}
