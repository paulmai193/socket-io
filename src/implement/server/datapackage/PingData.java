package implement.server.datapackage;

import socket.Interface.ReadDataInterface;
import socket.Interface.SocketClientInterface;
import socket.Interface.WriteDataInterface;

/**
 * The Class PingData. This class implements both ReadDataListener and WriteDataListener to read /
 * write Ping data package
 * 
 * @author Paul Mai
 */
public class PingData implements ReadDataInterface, WriteDataInterface {
	
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
	public void executeData(SocketClientInterface clientSocket) throws Exception {
		System.out.println("Client ping number " + number + " to server");
	}

	/* (non-Javadoc)
	 * @see socket.listener.ReadDataListener#executeData()
	 */
	@Override
	public void executeData() throws Exception {
		
	}
	
}
