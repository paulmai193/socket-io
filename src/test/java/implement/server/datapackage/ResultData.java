package implement.server.datapackage;

import logia.io.annotation.IOCommand;
import logia.io.annotation.IOData;
import logia.io.annotation.type.DataType;
import logia.socket.Interface.WriteDataInterface;

/**
 * The Class ResultData.
 *
 * @author Paul Mai
 */
@IOCommand(/* type = { CommandType.WRITER }, */value = 11)
public class ResultData implements WriteDataInterface {

	/** The message. */
	@IOData(order = 1, type = DataType.STRING)
	private String message;

	/**
	 * Instantiates a new result data.
	 */
	public ResultData() {

	}

	/**
	 * Instantiates a new result data.
	 *
	 * @param message the message
	 */
	public ResultData(String message) {
		this.message = message;
	}

}
