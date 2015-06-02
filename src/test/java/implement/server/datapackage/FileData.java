package implement.server.datapackage;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;

import logia.socket.Interface.ReadDataInterface;
import logia.socket.Interface.SocketClientInterface;
import logia.socket.Interface.WriteDataInterface;

/**
 * The Class FileData. This class implements both ReadDataListener and WriteDataListener to read write file data package
 * 
 * @author Paul Mai
 */
public class FileData implements ReadDataInterface, WriteDataInterface {

	/** The picture. */
	byte[] file;

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
			OutputStream out = new FileOutputStream("C:/Users/Paul Mai/Desktop/Candy.jpg");
			for (byte element : this.file) {
				out.write(element);
			}
			out.close();
			System.out.println("Write file finish");
		}
		catch (FileNotFoundException e) {
			e.printStackTrace();
		}
		catch (IOException e) {
			e.printStackTrace();
		}

	}

	/**
	 * Gets the file.
	 * 
	 * @return the file
	 */
	public byte[] getFile() {
		return this.file;
	}

	/**
	 * Sets the file.
	 * 
	 * @param picture the new file
	 */
	public void setFile(byte[] file) {
		this.file = file;
	}

}
