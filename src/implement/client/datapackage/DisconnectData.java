package implement.client.datapackage;

import socket.Interface.ReadDataInterface;
import socket.Interface.SocketClientInterface;
import socket.Interface.WriteDataInterface;

/**
 * The Class DisconnectData. This class implements both ReadDataListener and WriteDataListener to read /
 * write disconnect data package
 * 
 * @author Paul Mai
 */
public class DisconnectData implements ReadDataInterface, WriteDataInterface {
	
	/**
	 * Instantiates a new disconnect data.
	 */
	public DisconnectData() {
		
	}
	
	/* (non-Javadoc)
	 * 
	 * @see datapackage.ReadDataListener#executeData(socket.ClientBoundSocket) */
	@Override
	public void executeData(SocketClientInterface clientSocket) {
		clientSocket.disconnect();
	}

	/* (non-Javadoc)
	 * @see socket.listener.ReadDataListener#executeData()
	 */
	@Override
	public void executeData() throws Exception {
		
	}
	
}
