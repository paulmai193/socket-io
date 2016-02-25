package implement.server.datapackage;

import logia.io.annotation.IOCommand;
import logia.io.annotation.IOData;
import logia.io.annotation.type.DataType;
import logia.socket.Interface.ReadDataInterface;
import logia.socket.Interface.SocketClientInterface;

import com.google.gson.JsonObject;

/**
 * The Class JsonData.
 *
 * @author Paul Mai
 */
@IOCommand(value = 12)
public class JsonData implements ReadDataInterface {

	/** The jsonelement. */
	@IOData(order = 1, type = DataType.JSON, breakValue = "n/a")
	private JsonObject jsonelement;

	/* (non-Javadoc)
	 * @see logia.socket.Interface.ReadDataInterface#executeData()
	 */
	@Override
	public void executeData() throws Exception {

	}

	/* (non-Javadoc)
	 * @see logia.socket.Interface.ReadDataInterface#executeData(logia.socket.Interface.SocketClientInterface)
	 */
	@Override
	public void executeData(SocketClientInterface clientSocket) throws Exception {
		System.out.println("Client send json " + this.jsonelement.toString());
	}

	/**
	 * Gets the jsonelement.
	 *
	 * @return the jsonelement
	 */
	public JsonObject getJsonelement() {
		return this.jsonelement;
	}

	/**
	 * Sets the jsonelement.
	 *
	 * @param jsonelement the new jsonelement
	 */
	public void setJsonelement(JsonObject jsonelement) {
		this.jsonelement = jsonelement;
	}
}
