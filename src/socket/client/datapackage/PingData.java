package socket.client.datapackage;

import socket.listener.ReadDataListener;
import socket.listener.SocketListener;
import socket.listener.WriteDataListener;

/**
 * The Class PingData. This class implements both ReadDataListener and WriteDataListener to read /
 * write Ping data package
 * 
 * @author Paul Mai
 */
public class PingData implements ReadDataListener, WriteDataListener {
	
	/** The number. */
	int number;
	
	/**
	 * Instantiates a new ping data.
	 */
	public PingData() {
		
	}
	
	/**
	 * Instantiates a new ping data.
	 *
	 * @param number the number
	 */
	public PingData(int number) {
		setNumber(number);
	}
	
	/**
	 * Gets the number.
	 * 
	 * @return the number
	 */
	public int getNumber() {
		return number;
	}
	
	/**
	 * Sets the number.
	 * 
	 * @param number the new number
	 */
	public void setNumber(int number) {
		this.number = number;
	}
	
	/* (non-Javadoc)
	 * 
	 * @see datapackage.ReadDataListener#executeData(socket.ClientBoundSocket) */
	@Override
	public void executeData(SocketListener clientSocket) {
		System.out.println("Server ping number " + number + " to client");		
	}

	/* (non-Javadoc)
	 * @see socket.listener.ReadDataListener#executeData()
	 */
	@Override
	public void executeData() throws Exception {
		
	}
	
}
