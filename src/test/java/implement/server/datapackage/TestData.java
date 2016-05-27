package implement.server.datapackage;

import logia.io.annotation.IOCommand;
import logia.io.annotation.IOData;
import logia.io.annotation.type.DataType;
import logia.socket.Interface.ReadDataInterface;
import logia.socket.Interface.SocketClientInterface;

/**
 * The Class TestData.
 *
 * @author Paul Mai
 */
@IOCommand(value = 10)
public class TestData implements ReadDataInterface {

	/** The message. */
	@IOData(order = 3, type = DataType.STRING)
	String message;

	/** The name. */
	@IOData(order = 2, type = DataType.STRING)
	String name;

	/** The number. */
	@IOData(order = 1, type = DataType.LONG)
	long   number;

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
		System.out.println("Client " + this.name + " send number " + this.number + " with message "
		        + this.message + " to server");
		// try {
		// Thread.sleep(10000);
		// }
		// catch (InterruptedException e1) {
		// e1.printStackTrace();
		// }
		// ResultData result = new ResultData("I received package " +
		// this.getClass().getCanonicalName() + " from you, right ?");
		// try {
		// clientSocket.echo(result, 11);
		// }
		// catch (Exception e) {
		// e.printStackTrace();
		// }
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
	 * Gets the name.
	 *
	 * @return the name
	 */
	public String getName() {
		return this.name;
	}

	/**
	 * Gets the number.
	 *
	 * @return the number
	 */
	public long getNumber() {
		return this.number;
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
	 * Sets the name.
	 *
	 * @param name the new name
	 */
	public void setName(String name) {
		this.name = name;
	}

	/**
	 * Sets the number.
	 *
	 * @param number the new number
	 */
	public void setNumber(long number) {
		this.number = number;
	}

}
