package socket.client.datapackage;

import socket.listener.ReadDataListener;
import socket.listener.SocketListener;
import socket.listener.WriteDataListener;

/**
 * The Class DisconnectData. This class implements both ReadDataListener and WriteDataListener to read /
 * write disconnect data package
 * 
 * @author Paul Mai
 */
public class DisconnectData implements ReadDataListener, WriteDataListener {
	
	/**
	 * Instantiates a new disconnect data.
	 */
	public DisconnectData() {
		
	}
	
	/* (non-Javadoc)
	 * 
	 * @see datapackage.ReadDataListener#executeData(socket.ClientBoundSocket) */
	@Override
	public void executeData(SocketListener clientSocket) {
		clientSocket.disconnect();
	}

	/* (non-Javadoc)
	 * @see socket.listener.ReadDataListener#executeData()
	 */
	@Override
	public void executeData() throws Exception {
		
	}
	
}
