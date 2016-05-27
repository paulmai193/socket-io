package implement.server.datapackage;

import logia.socket.Interface.ReadDataInterface;
import logia.socket.Interface.SocketClientInterface;

/**
 * The Class PongData.
 *
 * @author Paul Mai
 */
public class PongData implements ReadDataInterface {

	/**
	 * Instantiates a new pong data.
	 */
	public PongData() {

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see logia.socket.Interface.ReadDataInterface#executeData()
	 */
	@Override
	public void executeData() throws Exception {
		System.out.println("Receive PING from client");
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see logia.socket.Interface.ReadDataInterface#executeData(logia.socket.Interface.SocketClientInterface)
	 */
	@Override
	public void executeData(SocketClientInterface clientSocket) throws Exception {
		System.out.println("Receive PING from client");
	}

}
