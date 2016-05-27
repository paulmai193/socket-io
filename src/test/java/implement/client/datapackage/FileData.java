package implement.client.datapackage;

import java.io.File;

import logia.io.annotation.IOCommand;
import logia.io.annotation.IOData;
import logia.io.annotation.type.DataType;
import logia.socket.Interface.WriteDataInterface;

/**
 * The Class FileData.
 *
 * @author Paul Mai
 */
@IOCommand(value = 5)
public class FileData implements WriteDataInterface {

	/** The file. */
	@IOData(order = 1, type = DataType.FILE)
	File file;

	/**
	 * Instantiates a new file data.
	 */
	public FileData() {

	}

	/**
	 * Instantiates a new file data.
	 *
	 * @param file the file
	 */
	public FileData(File file) {
		this.file = file;
	}

	/**
	 * Gets the file.
	 *
	 * @return the file
	 */
	public File getFile() {
		return this.file;
	}

	/**
	 * Sets the file.
	 *
	 * @param file the new file
	 */
	public void setFile(File file) {
		this.file = file;
	}

}
