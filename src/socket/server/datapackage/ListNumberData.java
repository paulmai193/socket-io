package socket.server.datapackage;

import java.util.ArrayList;
import java.util.List;

import socket.listener.ReadDataListener;
import socket.listener.SocketListener;
import socket.listener.WriteDataListener;

/**
 * The Class ListNumberData. This class implements both ReadDataListener and WriteDataListener to read
 * / write ListNumberData data package
 * 
 * @author Paul Mai
 */
public class ListNumberData implements ReadDataListener, WriteDataListener {
	
	/** The listnumber. */
	private List<Double> listnumber;

	/**
	 * Instantiates a new list number data.
	 */
	public ListNumberData() {
		this.listnumber = new ArrayList<Double>();
	}

	/**
	 * Gets the listnumber.
	 *
	 * @return the listnumber
	 */
	public List<Double> getlistnumber() {
		return listnumber;
	}

	/**
	 * Sets the listnumber.
	 *
	 * @param listnumber the new listnumber
	 */
	public void setlistnumber(List<Double> listnumber) {
		this.listnumber = listnumber;
	}

	/**
	 * Addnumber.
	 *
	 * @param number the number
	 */
	public void addnumber(Double number) {
		this.listnumber.add(number);
	}
	
	/* (non-Javadoc)
	 * 
	 * @see datapackage.ReadDataListener#executeData(socket.ClientBoundSocket) */
	@Override
	public void executeData(SocketListener clientSocket) {
		System.out.println("Client send list numbers");
		for (Double number : listnumber) {
			System.out.println(number);			
		}
	}

	/* (non-Javadoc)
	 * @see socket.listener.ReadDataListener#executeData()
	 */
	@Override
	public void executeData() throws Exception {
		
	}
	
}
