package implement.server.datapackage;

import logia.io.annotation.IOCommand;
import logia.io.annotation.IOData;
import logia.io.annotation.type.DataType;
import logia.socket.Interface.WriteDataInterface;

@IOCommand(/* type = { CommandType.WRITER }, */value = 11)
public class ResultData implements WriteDataInterface {

	@IOData(order = 1, type = DataType.STRING)
	private String message;

	public ResultData() {

	}

	public ResultData(String message) {
		this.message = message;
	}

}
