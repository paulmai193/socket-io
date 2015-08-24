package implement.client.datapackage;

import logia.io.annotation.IOCommand;
import logia.io.annotation.IOData;
import logia.io.annotation.type.DataType;
import logia.socket.Interface.WriteDataInterface;

/**
 * The Class TestData. This class implements both ReadDataInterface and WriteDataInterface to read / write Test data package
 * 
 * @author Paul Mai
 */
@IOCommand(/* type = { CommandType.WRITER }, */value = 10)
public class TestData implements WriteDataInterface {

	/** The message. */
	@IOData(order = 3, type = DataType.STRING, breakValue = "n/a")
	String message;

	/** The name. */
	@IOData(order = 2, type = DataType.STRING, breakValue = "n/a")
	String name;

	/** The number. */
	@IOData(order = 1, type = DataType.LONG, breakValue = "n/a")
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
	 * @param message the message to set
	 */
	public void setMessage(String message) {
		this.message = message;
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
	 * Sets the number.
	 *
	 * @param number the number to set
	 */
	public void setNumber(long number) {
		this.number = number;
	}

}
