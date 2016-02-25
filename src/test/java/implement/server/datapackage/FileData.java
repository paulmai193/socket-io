package implement.server.datapackage;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;

import logia.io.annotation.IOCommand;
import logia.io.annotation.IOData;
import logia.io.annotation.type.DataType;
import logia.io.exception.WriteDataException;
import logia.socket.Interface.ReadDataInterface;
import logia.socket.Interface.SocketClientInterface;

import org.apache.commons.io.FileUtils;

/**
 * The Class FileData. This class implements ReadDataListener to read write file data package
 * 
 * @author Paul Mai
 */
@IOCommand(value = 5)
public class FileData implements ReadDataInterface {

	/** The file. */
	@IOData(order = 1, type = DataType.FILE, breakValue = "n/a")
	File file;

	/**
	 * Instantiates a new file data.
	 */
	public FileData() {

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
		System.out.println("Client send file to server");
		try {
			long a = System.currentTimeMillis();
			// FileUtils.writeByteArrayToFile(new File("C:/Users/Paul Mai/Desktop/Candy.jpg"), this.bytes);
			FileUtils.copyFile(this.file, new File("C:/Users/Paul Mai/Desktop/Candy_1.mp4"));
			long b = System.currentTimeMillis();
			System.out.println("FINISH WRITE A BUFFER TO FILE AFTER " + (b - a) / 1000 + " s");

			System.out.println("Write file finish");

		}
		catch (FileNotFoundException e) {
			e.printStackTrace();
		}
		catch (IOException e) {
			e.printStackTrace();
		}
		catch (Exception e) {
			e.printStackTrace();
		}
		finally {
			this.file.delete();
			ResultData resultData = new ResultData("Send file success");
			try {
				clientSocket.echo(resultData, 11);
			}
			catch (WriteDataException e) {
				e.printStackTrace();
			}
		}

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
	 * @param file the file to set
	 */
	public void setFile(File file) {
		this.file = file;
	}

}
