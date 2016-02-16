package implement.client.datapackage;

import logia.io.annotation.IOCommand;
import logia.io.annotation.IOData;
import logia.io.annotation.type.DataType;
import logia.socket.Interface.WriteDataInterface;

import com.google.gson.JsonObject;

@IOCommand(value = 12)
public class JsonData implements WriteDataInterface {

	@IOData(order = 1, type = DataType.JSON, breakValue = "n/a")
	private JsonObject jsonelement;

	public JsonObject getJsonelement() {
		return this.jsonelement;
	}

	public void setJsonelement(JsonObject jsonelement) {
		this.jsonelement = jsonelement;
	}
}
