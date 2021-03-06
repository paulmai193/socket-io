package implement.server.datapackage;

import logia.socket.Interface.ReadDataInterface;
import logia.socket.Interface.SocketClientInterface;
import logia.socket.Interface.WriteDataInterface;

/**
 * The Class DisconnectData.
 *
 * @author Paul Mai
 */
public class DisconnectData implements ReadDataInterface, WriteDataInterface {

	/**
	 * Instantiates a new disconnect data.
	 */
	public DisconnectData() {

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
		System.out.println("Client want to disconnect");
		clientSocket.disconnect();
	}

}
