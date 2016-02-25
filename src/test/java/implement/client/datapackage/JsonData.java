package implement.client.datapackage;

import logia.io.annotation.IOCommand;
import logia.io.annotation.IOData;
import logia.io.annotation.type.DataType;
import logia.socket.Interface.WriteDataInterface;

import com.google.gson.JsonObject;

/**
 * The Class JsonData.
 *
 * @author Paul Mai
 */
@IOCommand(value = 12)
public class JsonData implements WriteDataInterface {

	/** The jsonelement. */
	@IOData(order = 1, type = DataType.JSON, breakValue = "n/a")
	private JsonObject jsonelement;

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
