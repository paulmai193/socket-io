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
	private static final Logger LOGGER = Logger.getLogger(Reader.class);

	/** The max size buffer. */
	private final int           MAX_SIZE_BUFFER;

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
	 * @param __bufferSize the buffer size
	 */
	public Reader(int __bufferSize) {
		this.MAX_SIZE_BUFFER = __bufferSize;
	}

	/**
	 * Read byte.
	 *
	 * @param __in the in
	 * @return the byte
	 * @throws IOException Signals that an I/O exception has occurred.
	 */
	public byte readByte(InputStream __in) throws IOException {
		byte _b = (byte) __in.read();
		// if (b == -1) {
		// throw new IOException();
		// }
		return _b;
	}

	/**
	 * Read byte array.
	 *
	 * @param __in the in
	 * @return the byte[]
	 * @throws IOException Signals that an I/O exception has occurred.
	 */
	public byte[] readByteArray(InputStream __in) throws IOException {
		int _length = this.readInt(__in);
		Reader.LOGGER.debug("Array Size: " + _length);

		ByteArrayOutputStream _arr = new ByteArrayOutputStream();
		for (int _i = 0; _i < _length; _i++) {
			_arr.write(this.readByte(__in));
		}
		return _arr.toByteArray();
	}

	/**
	 * Read double.
	 *
	 * @param __in the in
	 * @return the double
	 * @throws IOException Signals that an I/O exception has occurred.
	 */
	public double readDouble(InputStream __in) throws IOException {

		// OLD
		ByteArrayOutputStream _arr = new ByteArrayOutputStream();
		for (int _i = 0; _i < 8; _i++) {
			_arr.write(this.readByte(__in));
		}

		return ByteBuffer.wrap(_arr.toByteArray()).getDouble();
	}

	/**
	 * Read file.
	 *
	 * @param __in the in
	 * @return the file
	 * @throws IOException Signals that an I/O exception has occurred.
	 */
	public File readFile(InputStream __in) throws IOException {
		long _length = this.readLong(__in);
		File _tempFile = File.createTempFile("sockettempfile", "tmp");
		Reader.LOGGER.debug("TempFile: " + _tempFile.getAbsolutePath() + " - length: " + _length);

		// OLD
		ByteArrayOutputStream _arr = new ByteArrayOutputStream(this.MAX_SIZE_BUFFER);
		for (long _i = 1; _i <= _length; _i++) {
			_arr.write(this.readByte(__in));
			if (_i % this.MAX_SIZE_BUFFER == 0) {
				FileUtils.writeByteArrayToFile(_tempFile, _arr.toByteArray(), true);
				_arr.reset();
			}
		}
		if (_arr.size() > 0) {
			FileUtils.writeByteArrayToFile(_tempFile, _arr.toByteArray(), true);
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

		return _tempFile;
	}

	/**
	 * Read float.
	 *
	 * @param __in the in
	 * @return the float
	 * @throws IOException Signals that an I/O exception has occurred.
	 */
	public float readFloat(InputStream __in) throws IOException {

		// OLD
		ByteArrayOutputStream _arr = new ByteArrayOutputStream();
		for (int _i = 0; _i < 4; _i++) {
			_arr.write(this.readByte(__in));
		}
		return ByteBuffer.wrap(_arr.toByteArray()).getFloat();
	}

	/**
	 * Read int.
	 *
	 * @param __in the in
	 * @return the int
	 * @throws IOException Signals that an I/O exception has occurred.
	 */
	public int readInt(InputStream __in) throws IOException {

		// OLD
		ByteArrayOutputStream _arr = new ByteArrayOutputStream();
		for (int _i = 0; _i < 4; _i++) {
			_arr.write(this.readByte(__in));
		}
		return ByteBuffer.wrap(_arr.toByteArray()).getInt();
	}

	/**
	 * Read json.
	 *
	 * @param __in the in
	 * @return the json object
	 * @throws IOException Signals that an I/O exception has occurred.
	 */
	public JsonObject readJson(InputStream __in) throws IOException {
		String _s = this.readString(__in);
		return (JsonObject) new JsonParser().parse(_s);
	}

	/**
	 * Read long.
	 *
	 * @param __in the in
	 * @return the long
	 * @throws IOException Signals that an I/O exception has occurred.
	 */
	public long readLong(InputStream __in) throws IOException {
		// OLD
		ByteArrayOutputStream _arr = new ByteArrayOutputStream();
		for (int _i = 0; _i < 8; _i++) {
			_arr.write(this.readByte(__in));
		}
		return ByteBuffer.wrap(_arr.toByteArray()).getLong();
	}

	/**
	 * Read object.
	 *
	 * @param __clazz the class of object
	 * @param __in the in
	 * @return the object
	 * @throws Exception the exception
	 */
	public Object readObject(@SuppressWarnings("rawtypes") Class __clazz, InputStream __in) throws Exception {
		Object _object = __clazz.newInstance();
		Field[] _fields = __clazz.getDeclaredFields();
		for (Field _field : _fields) {
			_field.setAccessible(true);
			Object _fieldType = _field.getType().newInstance();
			if (_fieldType instanceof Byte) {
				_field.set(_object, this.readByte(__in));
			}
			else if (_fieldType instanceof Byte[]) {
				_field.set(_object, this.readByteArray(__in));
			}
			else if (_fieldType instanceof Double) {
				_field.set(_object, this.readDouble(__in));
			}
			else if (_fieldType instanceof Float) {
				_field.set(_object, this.readFloat(__in));
			}
			else if (_fieldType instanceof File) {
				_field.set(_object, this.readFile(__in));
			}
			else if (_fieldType instanceof Integer) {
				_field.set(_object, this.readInt(__in));
			}
			else if (_fieldType instanceof Long) {
				_field.set(_object, this.readLong(__in));
			}
			else if (_fieldType instanceof Short) {
				_field.set(_object, this.readShort(__in));
			}
			else if (_fieldType instanceof String) {
				_field.set(_object, this.readString(__in));
			}
		}
		return _object;
	}

	/**
	 * Read short.
	 *
	 * @param __in the in
	 * @return the short
	 * @throws IOException Signals that an I/O exception has occurred.
	 */
	public short readShort(InputStream __in) throws IOException {

		// OLD
		ByteArrayOutputStream _arr = new ByteArrayOutputStream();
		for (int _i = 0; _i < 2; _i++) {
			_arr.write(this.readByte(__in));
		}
		return ByteBuffer.wrap(_arr.toByteArray()).getShort();
	}

	/**
	 * Read string.
	 *
	 * @param __in the in
	 * @return the string
	 * @throws IOException Signals that an I/O exception has occurred.
	 */
	public String readString(InputStream __in) throws IOException {
		byte[] _arr = this.readByteArray(__in);
		String _s;
		try {
			_s = new String(_arr, "UTF-8");
		}
		catch (UnsupportedEncodingException e) {
			Reader.LOGGER.warn("Unsupport UTF-8");
			_s = new String(_arr);
		}
		return _s;
	}
}
