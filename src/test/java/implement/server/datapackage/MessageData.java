package implement.server.datapackage;

import logia.io.annotation.IOCommand;
import logia.io.annotation.IOData;
import logia.io.annotation.type.DataType;
import logia.socket.Interface.ReadDataInterface;
import logia.socket.Interface.SocketClientInterface;
import logia.socket.Interface.WriteDataInterface;

/**
 * The Class ReceiveMessageData. This class implements both ReadDataListener and WriteDataListener to read / write Message data package
 * 
 * @author Paul Mai
 */
@IOCommand(value = 2)
public class MessageData implements ReadDataInterface, WriteDataInterface {

	/** The message. */
	@IOData(order = 1, type = DataType.STRING)
	private String message;

	/** The recipient. */
	@IOData(order = 2, type = DataType.INTEGER)
	private int    user;

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
		this.setUser(user);
		this.setMessage(message);
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
		System.out.println("Receiver message " + this.message + " from client " + user);

	}

	/**
	 * Gets the message.
	 * 
	 * @return the message
	 */
	public String getMessage() {
		return this.message;
	}

	/**
	 * Gets the user.
	 * 
	 * @return the user
	 */
	public int getUser() {
		return this.user;
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
	 * Sets the user.
	 * 
	 * @param user the new user
	 */
	public void setUser(int user) {
		this.user = user;
	}

}
