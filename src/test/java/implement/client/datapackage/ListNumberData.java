package implement.client.datapackage;

import implement.define.Command;

import java.util.ArrayList;
import java.util.List;

import logia.io.annotation.IOCommand;
import logia.io.annotation.IOData;
import logia.io.annotation.type.ConditionType;
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
	@IOData(order = 2, type = DataType.LIST, conditionField = "check", conditionType = ConditionType.EQUAL, conditionValue = "2")
	private List<Double> listnumber;

	/** The check. */
	@IOData(order = 1, type = DataType.INTEGER, breakValue = "-1")
	private int          check;

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
	 * @return the listnumber
	 */
	public List<Double> getListnumber() {
		return this.listnumber;
	}

	/**
	 * @param __listnumber the listnumber to set
	 */
	public void setListnumber(List<Double> __listnumber) {
		this.listnumber = __listnumber;
	}

	/**
	 * @return the check
	 */
	public int getCheck() {
		return this.check;
	}

	/**
	 * @param __check the check to set
	 */
	public void setCheck(int __check) {
		this.check = __check;
	}

}
