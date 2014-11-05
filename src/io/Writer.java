package io;

import java.io.DataOutputStream;
import java.io.IOException;
import java.io.OutputStream;

import datapackage.PingData;
import datapackage.MessageData;
import datapackage.WriteDataListener;
import define.Command;

/**
 * The Class Writer write data from package into outputstream.
 * 
 * @author Paul Mai
 */
public class Writer {
	
	/**
	 * Instantiates a new writer.
	 */
	public Writer() {
		
	}
	
	/**
	 * Write byte.
	 * 
	 * @param dataOutputStream the data output stream
	 * @param data the data
	 * @throws IOException Signals that an I/O exception has occurred.
	 */
	public void writeByte(DataOutputStream dataOutputStream, byte data) throws IOException {
		dataOutputStream.write(data);
	}
	
	/**
	 * Write char.
	 * 
	 * @param dataOutputStream the data output stream
	 * @param data the data
	 * @throws IOException Signals that an I/O exception has occurred.
	 */
	public void writeChar(DataOutputStream dataOutputStream, char data) throws IOException {
		dataOutputStream.writeChar(data);
	}
	
	/**
	 * Write double.
	 * 
	 * @param dataOutputStream the data output stream
	 * @param data the data
	 * @throws IOException Signals that an I/O exception has occurred.
	 */
	public void writeDouble(DataOutputStream dataOutputStream, double data) throws IOException {
		dataOutputStream.writeDouble(data);
	}
	
	/**
	 * Write float.
	 * 
	 * @param dataOutputStream the data output stream
	 * @param data the data
	 * @throws IOException Signals that an I/O exception has occurred.
	 */
	public void writeFloat(DataOutputStream dataOutputStream, float data) throws IOException {
		dataOutputStream.writeFloat(data);
	}
	
	/**
	 * Write int.
	 * 
	 * @param dataOutputStream the data output stream
	 * @param data the data
	 * @throws IOException Signals that an I/O exception has occurred.
	 */
	public void writeInt(DataOutputStream dataOutputStream, int data) throws IOException {
		dataOutputStream.writeInt(data);
	}
	
	/**
	 * Write long.
	 * 
	 * @param dataOutputStream the data output stream
	 * @param data the data
	 * @throws IOException Signals that an I/O exception has occurred.
	 */
	public void writeLong(DataOutputStream dataOutputStream, long data) throws IOException {
		dataOutputStream.writeLong(data);
	}
	
	/**
	 * Write short.
	 * 
	 * @param dataOutputStream the data output stream
	 * @param data the data
	 * @throws IOException Signals that an I/O exception has occurred.
	 */
	public void writeShort(DataOutputStream dataOutputStream, short data) throws IOException {
		dataOutputStream.writeShort(data);
	}
	
	/**
	 * Write string.
	 * 
	 * @param dataOutputStream the data output stream
	 * @param data the data
	 * @throws IOException Signals that an I/O exception has occurred.
	 */
	public void writeString(DataOutputStream dataOutputStream, String data) throws IOException {
		byte[] bytes = data.getBytes("UTF-8");
		writeInt(dataOutputStream, bytes.length);
		dataOutputStream.write(bytes);
	}
	
	/**
	 * Flush.
	 * 
	 * @param dataOutputStream the data output stream
	 * @throws IOException Signals that an I/O exception has occurred.
	 */
	public void flush(DataOutputStream dataOutputStream) throws IOException {
		dataOutputStream.flush();
	}
	
	/**
	 * Apply stream.
	 * 
	 * @param outputStream the output stream
	 * @param dataListener the data listener
	 * @param command the command
	 * @throws IOException Signals that an I/O exception has occurred.
	 */
	public void applyStream(OutputStream outputStream, WriteDataListener dataListener, int command) throws IOException {
		DataOutputStream dataOutputStream = new DataOutputStream(outputStream);
		
		writeInt(dataOutputStream, command);
		
		switch (command) {
			case Command.SEND_MESSAGE:
				writeInt(dataOutputStream, ((MessageData) dataListener).getUser());
				writeString(dataOutputStream, ((MessageData) dataListener).getMessage());
				break;
			
			case Command.PING:
				writeInt(dataOutputStream, ((PingData) dataListener).getNumber());
				break;
			
			default:
				break;
		}
		flush(dataOutputStream);
	}
}
