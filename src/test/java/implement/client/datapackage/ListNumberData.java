package implement.client.datapackage;

import java.util.ArrayList;
import java.util.List;

import implement.define.Command;
import logia.io.annotation.IOCommand;
import logia.io.annotation.IOData;
import logia.io.annotation.type.DataType;
import logia.socket.Interface.ReadDataInterface;
import logia.socket.Interface.SocketClientInterface;
import logia.socket.Interface.WriteDataInterface;

/**
 * The Class ListNumberData.
 *
 * @author Paul Mai
 */
@IOCommand(value = Command.LIST_NUMBER)
public class ListNumberData implements ReadDataInterface, WriteDataInterface {

	/** The check. */
	@IOData(order = 1, type = DataType.INTEGER)
	private int			 check;

	/** The listnumber. */
	@IOData(order = 2, type = DataType.LIST)
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
		System.out.println("Server send list numbers");
		for (Double number : this.listnumber) {
			System.out.println(number);
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see datapackage.ReadDataListener#executeData(socket.ClientBoundSocket)
	 */
	@Override
	public void executeData(SocketClientInterface clientSocket) {
		System.out.println("Server send list numbers");
		for (Double number : this.listnumber) {
			System.out.println(number);
		}
	}

	/**
	 * Gets the check.
	 *
	 * @return the check
	 */
	public int getCheck() {
		return this.check;
	}

	/**
	 * Gets the listnumber.
	 *
	 * @return the listnumber
	 */
	public List<Double> getListnumber() {
		return this.listnumber;
	}

	/**
	 * Sets the check.
	 *
	 * @param __check the new check
	 */
	public void setCheck(int __check) {
		this.check = __check;
	}

	/**
	 * Sets the listnumber.
	 *
	 * @param __listnumber the new listnumber
	 */
	public void setListnumber(List<Double> __listnumber) {
		this.listnumber = __listnumber;
	}

}
