package datapackage;

import java.io.IOException;

import socket.ClientConnectSocket;

import define.Command;

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
	public void executeData(ClientConnectSocket clientSocket) {
		System.out.println("Client ping number " + number + " to server");
		
		try {
			clientSocket.getWriter().applyStream(clientSocket.getOutputStream(), this, Command.PING);
		}
		catch (IOException e) {
			e.printStackTrace();
		}
	}
	
}
