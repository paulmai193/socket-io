package implement.client.datapackage;

import logia.socket.Interface.ReadDataInterface;
import logia.socket.Interface.SocketClientInterface;

/**
 * The Class PingData.
 *
 * @author Paul Mai
 */
public class PingData implements ReadDataInterface {

	/**
	 * Instantiates a new ping data.
	 */
	public PingData() {

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see logia.socket.Interface.ReadDataInterface#executeData()
	 */
	@Override
	public void executeData() throws Exception {
		System.out.println("Receive PING from server");
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see logia.socket.Interface.ReadDataInterface#executeData(logia.socket.Interface.
	 * SocketClientInterface)
	 */
	@Override
	public void executeData(SocketClientInterface clientSocket) throws Exception {
		System.out.println("Receive PING from server, PONG again");
		clientSocket.echo(new PongData());
	}

}
