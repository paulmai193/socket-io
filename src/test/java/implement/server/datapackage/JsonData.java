package implement.server.datapackage;

import logia.io.annotation.IOCommand;
import logia.io.annotation.IOData;
import logia.io.annotation.type.DataType;
import logia.socket.Interface.ReadDataInterface;
import logia.socket.Interface.SocketClientInterface;

import com.google.gson.JsonObject;

@IOCommand(value = 12)
public class JsonData implements ReadDataInterface {

	@IOData(order = 1, type = DataType.JSON, breakValue = "n/a")
	private JsonObject jsonelement;

	@Override
	public void executeData() throws Exception {

	}

	@Override
	public void executeData(SocketClientInterface clientSocket) throws Exception {
		System.out.println("Client send json " + this.jsonelement.toString());
	}

	public JsonObject getJsonelement() {
		return this.jsonelement;
	}

	public void setJsonelement(JsonObject jsonelement) {
		this.jsonelement = jsonelement;
	}
}
