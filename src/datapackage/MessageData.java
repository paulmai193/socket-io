package datapackage;

import socket.ClientConnectSocket;

/**
 * The Class ReceiveMessageData. This class implements both ReadDataListener and WriteDataListener
 * to read / write Message data package
 * 
 * @author Paul Mai
 */
public class MessageData implements ReadDataListener, WriteDataListener {
	
	/** The recipient. */
	int user;
	
	/** The message. */
	String message;
	
	/**
	 * Instantiates a new receive message data.
	 */
	public MessageData() {
		
	}
	
	/**
	 * Gets the message.
	 * 
	 * @return the message
	 */
	public String getMessage() {
		return message;
	}
	
	/**
	 * Sets the message.
	 * 
	 * @param message the new message
	 */
	public void setMessage(String message) {
		this.message = message;
	}
	
	/**
	 * Gets the user.
	 * 
	 * @return the user
	 */
	public int getUser() {
		return user;
	}
	
	/**
	 * Sets the user.
	 * 
	 * @param user the new user
	 */
	public void setUser(int user) {
		this.user = user;
	}
	
	/* (non-Javadoc)
	 * 
	 * @see datapackage.ReadDataListener#executeData(socket.ClientBoundSocket) */
	@Override
	public void executeData(ClientConnectSocket clientSocket) {
		System.out.println("Client send message '" + message + "' to recipient have ID " + user);
	}
	
}
