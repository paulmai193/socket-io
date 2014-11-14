package io.util;

import io.parser.DataParser;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.lang.reflect.Field;
import java.net.SocketException;
import java.nio.ByteBuffer;

import socket.Interface.ReadDataInterface;
import socket.Interface.SocketClientInterface;

/**
 * The Class Reader read and parse data from inputstream to each data package type.
 * 
 * @author Paul Mai
 */
public class Reader {
	
	/** The path to xml define data parser file . */
	private String definePath;
	
	/**
	 * Instantiates a new reader.
	 * 
	 * @throws IOException Signals that an I/O exception has occurred.
	 */
	public Reader() throws IOException {
		
	}
	
	/**
	 * Instantiates a new reader.
	 *
	 * @param definePath the path of data parser file
	 * @throws IOException Signals that an I/O exception has occurred.
	 */
	public Reader(String definePath) throws IOException {
		this.definePath = definePath;
	}

	/**
	 * Read byte.
	 *
	 * @param in the in
	 * @return the byte
	 * @throws IOException Signals that an I/O exception has occurred.
	 */
	public byte readByte(InputStream in) throws IOException {
		return (byte) in.read();
	}

	/**
	 * Read byte array.
	 *
	 * @param in the in
	 * @return the byte[]
	 * @throws IOException Signals that an I/O exception has occurred.
	 */
	public byte[] readByteArray(InputStream in) throws IOException {
		int length = readInt(in);
		ByteArrayOutputStream arr = new ByteArrayOutputStream();
		for (int i = 0; i < length; i++) {
			arr.write(readByte(in));
		}
		return arr.toByteArray();
	}

	/**
	 * Read double.
	 *
	 * @param in the in
	 * @return the double
	 * @throws IOException Signals that an I/O exception has occurred.
	 */
	public double readDouble(InputStream in) throws IOException {
		ByteArrayOutputStream arr = new ByteArrayOutputStream();
		for (int i = 0; i < 8; i++) {
			arr.write(readByte(in));
		}
		return ByteBuffer.wrap(arr.toByteArray()).getDouble();
	}

	/**
	 * Read float.
	 *
	 * @param in the in
	 * @return the float
	 * @throws IOException Signals that an I/O exception has occurred.
	 */
	public float readFloat(InputStream in) throws IOException {
		ByteArrayOutputStream arr = new ByteArrayOutputStream();
		for (int i = 0; i < 4; i++) {
			arr.write(readByte(in));
		}
		return ByteBuffer.wrap(arr.toByteArray()).getFloat();
	}

	/**
	 * Read int.
	 *
	 * @param in the in
	 * @return the int
	 * @throws IOException Signals that an I/O exception has occurred.
	 */
	public int readInt(InputStream in) throws IOException {
		ByteArrayOutputStream arr = new ByteArrayOutputStream();
		for (int i = 0; i < 4; i++) {
			arr.write(readByte(in));
		}
		return ByteBuffer.wrap(arr.toByteArray()).getInt();
	}

	/**
	 * Read long.
	 *
	 * @param in the in
	 * @return the long
	 * @throws IOException Signals that an I/O exception has occurred.
	 */
	public long readLong(InputStream in) throws IOException {
		ByteArrayOutputStream arr = new ByteArrayOutputStream();
		for (int i = 0; i < 8; i++) {
			arr.write(readByte(in));
		}
		return ByteBuffer.wrap(arr.toByteArray()).getLong();
	}

	/**
	 * Read short.
	 *
	 * @param in the in
	 * @return the short
	 * @throws IOException Signals that an I/O exception has occurred.
	 */
	public short readShort(InputStream in) throws IOException {
		ByteArrayOutputStream arr = new ByteArrayOutputStream();
		for (int i = 0; i < 2; i++) {
			arr.write(readByte(in));
		}
		return ByteBuffer.wrap(arr.toByteArray()).getShort();
	}

	/**
	 * Read string.
	 *
	 * @param in the in
	 * @return the string
	 * @throws IOException Signals that an I/O exception has occurred.
	 */
	public String readString(InputStream in) throws IOException {
		byte[] arr = readByteArray(in);
		String s;
		try {
			s = new String(arr, "UTF-8");	
		}
		catch (UnsupportedEncodingException e) {
			s = new String(arr);
		}		
		return s;
	}

	/**
	 * Read object.
	 *
	 * @param clazz the class of object
	 * @param in the in
	 * @return the object
	 * @throws Exception the exception
	 */
	public Object readObject(@SuppressWarnings("rawtypes") Class clazz, InputStream in) throws Exception {
		Object object = clazz.newInstance();
		Field[] fields = clazz.getDeclaredFields();
		for (int i = 0; i < fields.length; i++) {
			Field field = fields[i];
			field.setAccessible(true);
			Object fieldType = field.getType().newInstance();
			if (fieldType instanceof Byte) {
				field.set(object, readByte(in));
			}
			else if (fieldType instanceof Byte[]) {
				field.set(object, readByteArray(in));
			}
			else if (fieldType instanceof Double) {
				field.set(object, readDouble(in));
			}
			else if (fieldType instanceof Float) {
				field.set(object, readFloat(in));
			}
			else if (fieldType instanceof Integer) {
				field.set(object, readInt(in));
			}
			else if (fieldType instanceof Long) {
				field.set(object, readLong(in));
			}
			else if (fieldType instanceof Short) {
				field.set(object, readShort(in));
			}
			else if (fieldType instanceof String) {
				field.set(object, readString(in));
			}
		}
		return object;
	}

	/**
	 * Apply data from stream. Read parse data from stream into instance of ReadDataInterface
	 *
	 * @param inputstream the inputstream
	 */
	public synchronized void applyStream(InputStream inputstream) {
		ReadDataInterface data = null;
		try {
			do {
				DataParser parser = new DataParser();
				data = parser.readData(inputstream, this, definePath);
				if (data != null) {
					data.executeData();
				}
				else {
					System.err.println("Error when read data");
					break;
				}
			} while (true);
		}
		catch (SocketException e) {
			System.out.println("Socket close");
		}
		catch (IOException e) {
			e.printStackTrace();
		}
		catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * Apply stream from instance of SocketInterface. Read and parse data from stream into instance of ReadDataInterface
	 *
	 * @param clientSocket the instance of SocketInterface
	 * @param definePath the xml define path
	 */
	public synchronized void applyStream(SocketClientInterface clientSocket) {
		ReadDataInterface data = null;
		try {
			do {
				DataParser parser = new DataParser();
				data = parser.readData(clientSocket.getInputStream(), this, definePath);
				if (data != null) {
					data.executeData(clientSocket);
				}
				else {
					System.err.println("Not recognize data from stream");
					break;
				}
			} while (clientSocket.isConnected());
		}
		catch (SocketException e) {
			e.printStackTrace();
			System.out.println("Socket close");
		}
		catch (IOException e) {
			e.printStackTrace();
		}
		catch (Exception e) {
			e.printStackTrace();
		}
		finally {
			clientSocket.disconnect();
		}
	}
}
