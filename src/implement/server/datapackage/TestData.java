package server.datapackage;

import logia.socket.Interface.ReadDataInterface;
import logia.socket.Interface.SocketClientInterface;
import logia.socket.Interface.WriteDataInterface;

/**
 * The Class TestData. This class implements both ReadDataInterface and WriteDataInterface to read /
 * write Test data package
 * 
 * @author Paul Mai
 */
public class TestData implements ReadDataInterface, WriteDataInterface {
	
	/** The number. */
	long number;
	
	/** The name. */
	String name;
	
	/** The message. */
	String message;
	
	/**
	 * Instantiates a new test data.
	 */
	public TestData() {
		
	}
	
	/**
	 * Instantiates a new test data.
	 *
	 * @param number the number
	 * @param name the name
	 * @param message the message
	 */
	public TestData(long number, String name, String message) {
		this.number = number;
		this.name = name;
		this.message = message;
	}
	
	/**
	 * Gets the number.
	 *
	 * @return the number
	 */
	public long getNumber() {
		return number;
	}
	
	/**
	 * Sets the number.
	 *
	 * @param number the number to set
	 */
	public void setNumber(long number) {
		this.number = number;
	}
	
	/**
	 * Gets the name.
	 *
	 * @return the name
	 */
	public String getName() {
		return name;
	}
	
	/**
	 * Sets the name.
	 *
	 * @param name the name to set
	 */
	public void setName(String name) {
		this.name = name;
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
	 * @param message the message to set
	 */
	public void setMessage(String message) {
		this.message = message;
	}
	
	/* (non-Javadoc)
	 * 
	 * @see datapackage.ReadDataListener#executeData(socket.ClientBoundSocket) */
	@Override
	public void executeData(SocketClientInterface clientSocket) {
		System.out.println("Client " + name + " ping number " + number + " with message " + message + " to server");
	}
	
	/* (non-Javadoc)
	 * 
	 * @see socket.listener.ReadDataListener#executeData() */
	@Override
	public void executeData() throws Exception {
		
	}
	
}
