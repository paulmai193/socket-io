package implement.server.datapackage;

import implement.define.Command;
import logia.io.annotation.IOCommand;
import logia.io.annotation.IOData;
import logia.io.annotation.type.CommandType;
import logia.io.annotation.type.DataType;
import logia.socket.Interface.ReadDataInterface;
import logia.socket.Interface.SocketClientInterface;
import logia.socket.Interface.WriteDataInterface;

/**
 * The Class TestData. This class implements both ReadDataInterface and WriteDataInterface to read / write Test data package
 * 
 * @author Paul Mai
 */
@IOCommand(type = { CommandType.READER, CommandType.WRITER }, value = 10)
public class TestData implements ReadDataInterface, WriteDataInterface {

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
		System.out.println("Client " + this.name + " ping number " + this.number + " with message " + this.message + " to server");
		try {
			clientSocket.echo(new DisconnectData(), Command.DISCONNECT);
		}
		catch (Exception e) {
			e.printStackTrace();
		}
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
