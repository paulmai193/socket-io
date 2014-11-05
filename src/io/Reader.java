package io;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;

import org.apache.commons.io.IOUtils;

import socket.ClientConnectSocket;
import datapackage.MessageData;
import datapackage.PingData;
import datapackage.ReadDataListener;
import define.Command;

/**
 * The Class Reader read and parse data from inputstream to each data package type.
 * 
 * @author Paul Mai
 */
public class Reader {
	
	/**
	 * Instantiates a new reader.
	 * 
	 * @throws IOException Signals that an I/O exception has occurred.
	 */
	public Reader() throws IOException {
		
	}
	
	/**
	 * Read byte.
	 * 
	 * @param byteBuffer the byte buffer
	 * @return the byte
	 */
	public byte readByte(ByteBuffer byteBuffer) {
		return byteBuffer.get();
	}
	
	/**
	 * Read char.
	 * 
	 * @param byteBuffer the byte buffer
	 * @return the char
	 */
	public char readChar(ByteBuffer byteBuffer) {
		return byteBuffer.getChar();
	}
	
	/**
	 * Read double.
	 * 
	 * @param byteBuffer the byte buffer
	 * @return the double
	 */
	public double readDouble(ByteBuffer byteBuffer) {
		return byteBuffer.getDouble();
	}
	
	/**
	 * Read float.
	 * 
	 * @param byteBuffer the byte buffer
	 * @return the float
	 */
	public float readFloat(ByteBuffer byteBuffer) {
		return byteBuffer.getFloat();
	}
	
	/**
	 * Read int.
	 * 
	 * @param byteBuffer the byte buffer
	 * @return the int
	 */
	public int readInt(ByteBuffer byteBuffer) {
		return byteBuffer.getInt();
	}
	
	/**
	 * Read long.
	 * 
	 * @param byteBuffer the byte buffer
	 * @return the long
	 */
	public long readLong(ByteBuffer byteBuffer) {
		return byteBuffer.getLong();
	}
	
	/**
	 * Read short.
	 * 
	 * @param byteBuffer the byte buffer
	 * @return the short
	 */
	public short readShort(ByteBuffer byteBuffer) {
		return byteBuffer.getShort();
	}
	
	/**
	 * Read string.
	 * 
	 * @param byteBuffer the byte buffer
	 * @return the string
	 */
	public String readString(ByteBuffer byteBuffer) {
		int length = readInt(byteBuffer);
		ByteArrayOutputStream array = new ByteArrayOutputStream();
		while (length-- > 0) {
			array.write(byteBuffer.get());
		}
		String s = "";
		s = new String(array.toByteArray());
		return s;
	}
	
	/**
	 * Apply stream.
	 * 
	 * @param inputStream the input stream
	 * @param clientSocket the client socket
	 */
	public void applyStream(InputStream inputStream, ClientConnectSocket clientSocket) {
		ReadDataListener data = null;
		try {
			ByteBuffer byteBuffer = ByteBuffer.wrap(IOUtils.toByteArray(inputStream));
			do {
				// Read command
				switch (readInt(byteBuffer)) {
					case Command.RECEIVE_MESSAGE:
						data = new MessageData();
						
						// Read ID recipient 1st
						((MessageData) data).setUser(readInt(byteBuffer));
						
						// Read message 2nd
						((MessageData) data).setMessage(readString(byteBuffer));
						break;
					
					case Command.PING:
						data = new PingData();
						
						// Read number
						((PingData) data).setNumber(readInt(byteBuffer));
						break;
					
					// Continue reading data which command...
					default:
						break;
				}
				
				if (data != null) {
					data.executeData(clientSocket);
				}
				else {
					System.err.println("Error when read data");
					break;
				}
			} while (byteBuffer.hasRemaining());
			
		}
		catch (IOException e) {
			e.printStackTrace();
		}
		// return data;
	}
}
