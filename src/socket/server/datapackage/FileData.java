package socket.server.datapackage;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;

import socket.listener.ReadDataListener;
import socket.listener.SocketListener;
import socket.listener.WriteDataListener;

/**
 * The Class FileData. This class implements both ReadDataListener and WriteDataListener to read
 * write file data package
 * 
 * @author Paul Mai
 */
public class FileData implements ReadDataListener, WriteDataListener {
	
	/** The picture. */
	byte[] file;
	
	/**
	 * Instantiates a new file data.
	 */
	public FileData() {
		
	}
	
	/**
	 * Gets the file.
	 * 
	 * @return the file
	 */
	public byte[] getFile() {
		return file;
	}
	
	/**
	 * Sets the file.
	 * 
	 * @param picture the new file
	 */
	public void setFile(byte[] file) {
		this.file = file;
	}
	
	/* (non-Javadoc)
	 * 
	 * @see datapackage.ReadDataListener#executeData(socket.ClientBoundSocket) */
	@Override
	public void executeData(SocketListener clientSocket) {
		System.out.println("Client send file to server");
		try {
			OutputStream out = new FileOutputStream("C:/Users/Paul Mai/Desktop/Candy.jpg");
			for (int i = 0; i < this.file.length; i++) {
				out.write(file[i]);
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

	/* (non-Javadoc)
	 * @see socket.listener.ReadDataListener#executeData()
	 */
	@Override
	public void executeData() throws Exception {
		
	}
	
}
