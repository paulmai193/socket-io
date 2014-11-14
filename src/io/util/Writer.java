package io.util;

import io.parser.DataParser;

import java.io.IOException;
import java.io.OutputStream;
import java.lang.reflect.Field;
import java.nio.ByteBuffer;

import socket.Interface.WriteDataInterface;

/**
 * The Class Writer write data from package into outputstream.
 * 
 * @author Paul Mai
 */
public class Writer {
	/** The path to xml define data parser file . */
	private String definePath;
	
	/**
	 * Instantiates a new writer.
	 */
	public Writer() {
		
	}
	
	/**
	 * Instantiates a new writer.
	 *
	 * @param definePath the path of data parser file
	 */
	public Writer(String definePath) {
		this.definePath = definePath;
	}
	
	/**
	 * Write byte.
	 * 
	 * @param out the data output stream
	 * @param data the data
	 * @throws IOException Signals that an I/O exception has occurred.
	 */
	public void writeByte(OutputStream out, byte data) throws IOException {
		out.write(data);
	}
	
	/**
	 * Write byte array.
	 * 
	 * @param out the data output stream
	 * @param data the data
	 * @throws IOException Signals that an I/O exception has occurred.
	 */
	public void writeByteArray(OutputStream out, byte[] data) throws IOException {
		int length = data.length;
		writeInt(out, length);
		for (int i = 0; i < length; i++) {
			writeByte(out, data[i]);
		}
	}
	
	/**
	 * Write double.
	 * 
	 * @param out the data output stream
	 * @param data the data
	 * @throws IOException Signals that an I/O exception has occurred.
	 */
	public void writeDouble(OutputStream out, double data) throws IOException {
		byte[] bytes = new byte[8];
		ByteBuffer.wrap(bytes).putDouble(data);
		for (byte b : bytes) {
			out.write(b);
		}
	}
	
	/**
	 * Write float.
	 * 
	 * @param out the data output stream
	 * @param data the data
	 * @throws IOException Signals that an I/O exception has occurred.
	 */
	public void writeFloat(OutputStream out, float data) throws IOException {
		byte[] bytes = new byte[4];
		ByteBuffer.wrap(bytes).putFloat(data);
		for (byte b : bytes) {
			out.write(b);
		}
	}
	
	/**
	 * Write int.
	 * 
	 * @param dataOutputStream the data output stream
	 * @param data the data
	 * @throws IOException Signals that an I/O exception has occurred.
	 */
	public void writeInt(OutputStream out, int data) throws IOException {
		byte[] bytes = new byte[4];
		ByteBuffer.wrap(bytes).putInt(data);
		for (byte b : bytes) {
			out.write(b);
		}
	}
	
	/**
	 * Write long.
	 * 
	 * @param out the data output stream
	 * @param data the data
	 * @throws IOException Signals that an I/O exception has occurred.
	 */
	public void writeLong(OutputStream out, long data) throws IOException {
		byte[] bytes = new byte[8];
		ByteBuffer.wrap(bytes).putLong(data);
		for (byte b : bytes) {
			out.write(b);
		}
	}
	
	/**
	 * Write short.
	 * 
	 * @param out the data output stream
	 * @param data the data
	 * @throws IOException Signals that an I/O exception has occurred.
	 */
	public void writeShort(OutputStream out, short data) throws IOException {
		byte[] bytes = new byte[2];
		ByteBuffer.wrap(bytes).putShort(data);
		for (byte b : bytes) {
			out.write(b);
		}
	}
	
	/**
	 * Write string.
	 * 
	 * @param out the data output stream
	 * @param data the data
	 * @throws IOException Signals that an I/O exception has occurred.
	 */
	public void writeString(OutputStream out, String data) throws IOException {
		byte[] bytes = data.getBytes("UTF-8");
		writeByteArray(out, bytes);
	}
	
	/**
	 * Write object.
	 * 
	 * @param out the data output stream
	 * @param data the data
	 * @throws IllegalArgumentException the illegal argument exception
	 * @throws IllegalAccessException the illegal access exception
	 * @throws IOException Signals that an I/O exception has occurred.
	 * @throws InstantiationException the instantiation exception
	 */
	public void writeObject(OutputStream out, Object data) throws IllegalArgumentException, IllegalAccessException, IOException,
			InstantiationException {
		Field[] fields = data.getClass().getDeclaredFields();
		for (int i = 0; i < fields.length; i++) {
			Field field = fields[i];
			field.setAccessible(true);
			Object fieldType = field.getType().newInstance();
			if (fieldType instanceof Byte) {
				writeByte(out, field.getByte(data));
			}
			else if (fieldType instanceof Byte[]) {
				writeByteArray(out, (byte[]) field.get(data));
			}
			else if (fieldType instanceof Double) {
				writeDouble(out, field.getDouble(data));
			}
			else if (fieldType instanceof Float) {
				writeFloat(out, field.getFloat(data));
			}
			else if (fieldType instanceof Integer) {
				writeInt(out, field.getInt(data));
			}
			else if (fieldType instanceof Long) {
				writeLong(out, field.getLong(data));
			}
			else if (fieldType instanceof Short) {
				writeShort(out, field.getShort(data));
			}
			else if (fieldType instanceof String) {
				writeString(out, (String) field.get(data));
			}
		}
	}
	
	/**
	 * Apply stream. Get and write data from instance of WriteDataInterface into stream
	 *
	 * @param outputStream the output stream
	 * @param dataListener the instance of WriteDataInterface
	 * @param command the command
	 * @throws Exception the exception
	 */
	public synchronized void applyStream(OutputStream outputStream, WriteDataInterface dataListener, int command) throws Exception {
		DataParser parser = new DataParser();
		parser.writeData(this, command, outputStream, dataListener, definePath);
	}
}
