package implement.client.datapackage;

import logia.io.annotation.IOCommand;
import logia.io.annotation.IOData;
import logia.io.annotation.type.DataType;
import logia.socket.Interface.ReadDataInterface;
import logia.socket.Interface.SocketClientInterface;

/**
 * The Class ResultData.
 *
 * @author Paul Mai
 */
@IOCommand(/* type = { CommandType.READER }, */value = 11)
public class ResultData implements ReadDataInterface {

	/** The message. */
	@IOData(order = 1, type = DataType.STRING, breakValue = "n/a")
	private String message;

	/* (non-Javadoc)
	 * @see logia.socket.Interface.ReadDataInterface#executeData()
	 */
	@Override
	public void executeData() throws Exception {
		System.out.println("Receive result from server with message: " + this.message);

	}

	/* (non-Javadoc)
	 * @see logia.socket.Interface.ReadDataInterface#executeData(logia.socket.Interface.SocketClientInterface)
	 */
	@Override
	public void executeData(SocketClientInterface clientSocket) throws Exception {
		System.out.println("Receive result from server with message: " + this.message);
	}

}
