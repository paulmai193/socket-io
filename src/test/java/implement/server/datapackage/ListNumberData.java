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

/**
 * The Class ListNumberData. This class implements both ReadDataListener and WriteDataListener to read / write ListNumberData data package
 * 
 * @author Paul Mai
 */
@IOCommand(value = Command.LIST_NUMBER)
public class ListNumberData implements ReadDataInterface, WriteDataInterface {

	/** The listnumber. */
	@IOData(order = 1, type = DataType.LIST)
	private List<Double> listnumber;

	/**
	 * Instantiates a new list number data.
	 */
	public ListNumberData() {
		this.listnumber = new ArrayList<Double>();
	}

	/**
	 * Addnumber.
	 *
	 * @param number the number
	 */
	public void addnumber(Double number) {
		this.listnumber.add(number);
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
		System.out.println("Client send list numbers");
		for (Double number : this.listnumber) {
			System.out.println(number);
		}
	}

	/**
	 * Gets the listnumber.
	 *
	 * @return the listnumber
	 */
	public List<Double> getlistnumber() {
		return this.listnumber;
	}

	/**
	 * Sets the listnumber.
	 *
	 * @param listnumber the new listnumber
	 */
	public void setlistnumber(List<Double> listnumber) {
		this.listnumber = listnumber;
	}

}
