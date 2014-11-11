package socket.server.datapackage;

import socket.listener.ReadDataListener;
import socket.listener.SocketListener;
import socket.listener.WriteDataListener;

/**
 * The Class ReceiveMessageData. This class implements both ReadDataListener and WriteDataListener
 * to read / write Message data package
 * 
 * @author Paul Mai
 */
public class MessageData implements ReadDataListener, WriteDataListener {
	
	/** The recipient. */
	private int user;
	
	/** The message. */
	private String message;
	
	/**
	 * Instantiates a new receive message data.
	 */
	public MessageData() {
		
	}
	
	/**
	 * Instantiates a new message data.
	 * 
	 * @param user the user
	 * @param message the message
	 */
	public MessageData(int user, String message) {
		setUser(user);
		setMessage(message);
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
	public void executeData(SocketListener clientSocket) {
		System.out.println("Receiver message " + message + " from client");
		
	}

	/* (non-Javadoc)
	 * @see socket.listener.ReadDataListener#executeData()
	 */
	@Override
	public void executeData() throws Exception {
		
	}
	
}
