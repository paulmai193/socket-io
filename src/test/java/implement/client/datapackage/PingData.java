package implement.client.datapackage;

import implement.define.Command;
import logia.socket.Interface.ReadDataInterface;
import logia.socket.Interface.SocketClientInterface;

/**
 * The Class PingData. This class implements ReadDataListener to read Ping data package
 * 
 * @author Paul Mai
 */
public class PingData implements ReadDataInterface {

	/**
	 * Instantiates a new ping data.
	 */
	public PingData() {

	}

	@Override
	public void executeData() throws Exception {
		System.out.println("Receive PING from server");
	}

	@Override
	public void executeData(SocketClientInterface clientSocket) throws Exception {
		System.out.println("Receive PING from server, PONG again");
		clientSocket.echo(new PongData(), Command.PONG);
	}

}
