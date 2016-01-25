package logia.io.util;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.lang.reflect.Field;
import java.nio.ByteBuffer;

import org.apache.commons.io.FileUtils;
import org.apache.log4j.Logger;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

/**
 * The Class Reader read and parse data from inputstream to each data package type.
 * 
 * @author Paul Mai
 */
public class Reader {

	/** The logger. */
	private final Logger LOGGER = Logger.getLogger(this.getClass());

	/** The max size buffer. */
	private final int    MAX_SIZE_BUFFER;

	/**
	 * Instantiates a new reader.
	 * 
	 * @throws IOException Signals that an I/O exception has occurred.
	 */
	public Reader() {
		this.MAX_SIZE_BUFFER = 4 * 1024;
	}

	/**
	 * Instantiates a new reader.
	 *
	 * @param bufferSize the buffer size
	 */
	public Reader(int bufferSize) {
		this.MAX_SIZE_BUFFER = bufferSize;
	}

	/**
	 * Read byte.
	 *
	 * @param in the in
	 * @return the byte
	 * @throws IOException Signals that an I/O exception has occurred.
	 */
	public byte readByte(InputStream in) throws IOException {
		byte b = (byte) in.read();
		// if (b == -1) {
		// throw new IOException();
		// }
		return b;
	}

	/**
	 * Read byte array.
	 *
	 * @param in the in
	 * @return the byte[]
	 * @throws IOException Signals that an I/O exception has occurred.
	 */
	public byte[] readByteArray(InputStream in) throws IOException {
		int length = this.readInt(in);
		this.LOGGER.debug("Array Size: " + length);

		ByteArrayOutputStream arr = new ByteArrayOutputStream();
		for (int i = 0; i < length; i++) {
			arr.write(this.readByte(in));
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

		// OLD
		ByteArrayOutputStream arr = new ByteArrayOutputStream();
		for (int i = 0; i < 8; i++) {
			arr.write(this.readByte(in));
		}

		return ByteBuffer.wrap(arr.toByteArray()).getDouble();
	}

	/**
	 * Read file.
	 *
	 * @param in the in
	 * @return the file
	 * @throws IOException Signals that an I/O exception has occurred.
	 */
	public File readFile(InputStream in) throws IOException {
		long length = this.readLong(in);
		File tempFile = File.createTempFile("sockettempfile", "tmp");
		this.LOGGER.debug("TempFile: " + tempFile.getAbsolutePath() + " - length: " + length);

		// OLD
		ByteArrayOutputStream arr = new ByteArrayOutputStream(this.MAX_SIZE_BUFFER);
		for (long i = 1; i <= length; i++) {
			arr.write(this.readByte(in));
			if (i % this.MAX_SIZE_BUFFER == 0) {
				FileUtils.writeByteArrayToFile(tempFile, arr.toByteArray(), true);
				arr.reset();
			}
		}
		if (arr.size() > 0) {
			FileUtils.writeByteArrayToFile(tempFile, arr.toByteArray(), true);
		}

		// NEW
		// if (length < this.MAX_SIZE_BUFFER) {
		// ByteArrayBuffer buffer = new ByteArrayBuffer(MAX_SIZE_BUFFER);
		// int buf = 0;
		// while (buf < length) {
		// int c = 16 * 1024;
		// if ((length - buf) < c) {
		// c = (int) (length - buf);
		// }
		//
		// byte[] barray = new byte[c];
		// int b = in.read(barray);
		// if (b == -1) {
		// throw new IOException();
		// }
		// buf = buf + b;
		// buffer.append(barray, 0, barray.length);
		// this.LOGGER.debug("1. Read " + b + " bytes");
		// this.LOGGER.debug("2. Read total " + buf + " bytes");
		// }
		// // read last bytes of array
		// byte[] barray = new byte[(int) (length - buf)];
		// int b = in.read(barray);
		// if (b == -1) {
		// throw new IOException();
		// }
		// buf = buf + b;
		// buffer.append(barray, 0, barray.length);
		// this.LOGGER.debug("1. Read " + b + " bytes");
		// this.LOGGER.debug("2. Read total " + buf + " bytes");
		// FileUtils.writeByteArrayToFile(tempFile, buffer.toByteArray(), true);
		// buffer.clear();
		// }
		// else {
		// // int n = (int) (length / this.MAX_SIZE_BUFFER);
		// // for (int i = 0; i < n; i++) {
		// // byte[] barray = new byte[this.MAX_SIZE_BUFFER];
		// // int buf = in.read(barray, 0, this.MAX_SIZE_BUFFER);
		// // this.LOGGER.debug("Read " + buf + " bytes");
		// // FileUtils.writeByteArrayToFile(tempFile, barray, true);
		// // }
		// // byte[] barray = new byte[(int) (length - (this.MAX_SIZE_BUFFER * n))];
		// // int buf = in.read(barray, 0, (int) (length - (this.MAX_SIZE_BUFFER * n)));
		// // this.LOGGER.debug("Read " + buf + " bytes");
		// // FileUtils.writeByteArrayToFile(tempFile, barray, true);
		//
		// int buf = 0;
		// int j = 1;
		// ByteArrayBuffer buffer = new ByteArrayBuffer(MAX_SIZE_BUFFER);
		// while (buf < length) {
		// int c = 16 * 1024;
		// if ((length - buf) < c) {
		// c = (int) (length - buf);
		// }
		//
		// byte[] barray = new byte[c];
		// int b = in.read(barray);
		// if (b == -1) {
		// throw new IOException();
		// }
		// buf = buf + b;
		// buffer.append(barray, 0, barray.length);
		// this.LOGGER.debug("1. Read " + b + " bytes");
		// this.LOGGER.debug("2. Read total " + buf + " bytes");
		// // if (c < (1024)) {
		// // FileUtils.writeByteArrayToFile(tempFile, buffer.toByteArray(), true);
		// // this.LOGGER.debug("3. Current tempFile length: " + tempFile.length());
		// // }
		// // else if (buf <= (this.MAX_SIZE_BUFFER * j)) {
		// // continue;
		// // }
		// // else {
		// // FileUtils.writeByteArrayToFile(tempFile, buffer.toByteArray(), true);
		// // buffer.clear();
		// // this.LOGGER.debug("3. Current tempFile length: " + tempFile.length());
		// // j++;
		// // }
		// if (buffer.length() >= this.MAX_SIZE_BUFFER) {
		// FileUtils.writeByteArrayToFile(tempFile, buffer.toByteArray(), true);
		// buffer.clear();
		// this.LOGGER.debug("3. Current tempFile length: " + tempFile.length());
		// }
		// }
		// // read last bytes of array
		// byte[] barray = new byte[(int) (length - buf)];
		// int b = in.read(barray);
		// if (b == -1) {
		// throw new IOException();
		// }
		// buf = buf + b;
		// buffer.append(barray, 0, barray.length);
		// this.LOGGER.debug("1. Read " + b + " bytes");
		// this.LOGGER.debug("2. Read total " + buf + " bytes");
		// FileUtils.writeByteArrayToFile(tempFile, buffer.toByteArray(), true);
		// this.LOGGER.debug("3. Current tempFile length: " + tempFile.length());
		// }

		return tempFile;
	}

	/**
	 * Read float.
	 *
	 * @param in the in
	 * @return the float
	 * @throws IOException Signals that an I/O exception has occurred.
	 */
	public float readFloat(InputStream in) throws IOException {

		// OLD
		ByteArrayOutputStream arr = new ByteArrayOutputStream();
		for (int i = 0; i < 4; i++) {
			arr.write(this.readByte(in));
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

		// OLD
		ByteArrayOutputStream arr = new ByteArrayOutputStream();
		for (int i = 0; i < 4; i++) {
			arr.write(this.readByte(in));
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
		// OLD
		ByteArrayOutputStream arr = new ByteArrayOutputStream();
		for (int i = 0; i < 8; i++) {
			arr.write(this.readByte(in));
		}
		return ByteBuffer.wrap(arr.toByteArray()).getLong();
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
		for (Field field : fields) {
			field.setAccessible(true);
			Object fieldType = field.getType().newInstance();
			if (fieldType instanceof Byte) {
				field.set(object, this.readByte(in));
			}
			else if (fieldType instanceof Byte[]) {
				field.set(object, this.readByteArray(in));
			}
			else if (fieldType instanceof Double) {
				field.set(object, this.readDouble(in));
			}
			else if (fieldType instanceof Float) {
				field.set(object, this.readFloat(in));
			}
			else if (fieldType instanceof File) {
				field.set(object, this.readFile(in));
			}
			else if (fieldType instanceof Integer) {
				field.set(object, this.readInt(in));
			}
			else if (fieldType instanceof Long) {
				field.set(object, this.readLong(in));
			}
			else if (fieldType instanceof Short) {
				field.set(object, this.readShort(in));
			}
			else if (fieldType instanceof String) {
				field.set(object, this.readString(in));
			}
		}
		return object;
	}

	/**
	 * Read short.
	 *
	 * @param in the in
	 * @return the short
	 * @throws IOException Signals that an I/O exception has occurred.
	 */
	public short readShort(InputStream in) throws IOException {

		// OLD
		ByteArrayOutputStream arr = new ByteArrayOutputStream();
		for (int i = 0; i < 2; i++) {
			arr.write(this.readByte(in));
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
		byte[] arr = this.readByteArray(in);
		String s;
		try {
			s = new String(arr, "UTF-8");
		}
		catch (UnsupportedEncodingException e) {
			this.LOGGER.warn("Unsupport UTF-8");
			s = new String(arr);
		}
		return s;
	}

	/**
	 * Read json.
	 *
	 * @param in the in
	 * @return the json object
	 * @throws IOException Signals that an I/O exception has occurred.
	 */
	public JsonObject readJson(InputStream in) throws IOException {
		String s = this.readString(in);
		return (JsonObject) new JsonParser().parse(s);
	}
}
