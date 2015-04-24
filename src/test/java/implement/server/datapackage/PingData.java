package implement.server.datapackage;

import logia.socket.Interface.WriteDataInterface;

/**
 * The Class PingData. This class implements both ReadDataListener and WriteDataListener to read / write Ping data package
 * 
 * @author Paul Mai
 */
public class PingData implements WriteDataInterface {

	/**
	 * Instantiates a new ping data.
	 */
	public PingData() {
		System.out.println("PING to client to check connection");
	}

}
