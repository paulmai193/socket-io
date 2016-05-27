package logia.io.util;

import java.io.Closeable;
import java.io.DataInputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.lang.reflect.Field;

import org.apache.commons.io.FileUtils;
import org.apache.log4j.Logger;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import logia.io.exception.FileTooLargeException;

/**
 * The Class Reader.
 *
 * @author Paul Mai
 */
public class Reader implements Closeable {

	/** The Constant LOGGER. */
	private static final Logger	LOGGER = Logger.getLogger(Reader.class);

	/** The data input stream. */
	private DataInputStream		dataInputStream;

	/** The max size buffer. */
	private final int			MAX_SIZE_BUFFER;

	/**
	 * Instantiates a new reader.
	 */
	public Reader() {
		this(10 * 1024);
	}

	/**
     * Instantiates a new reader.
     *
     * @param __bufferSize
     *            the __buffer size
     */
	public Reader(int __bufferSize) {
		this.MAX_SIZE_BUFFER = __bufferSize;
	}

	/**
     * Check arrays too large.
     *
     * @param __length
     *            the __length
     * @throws FileTooLargeException
     *             the file too large exception
     */
	private void checkArraysTooLarge(int __length) throws FileTooLargeException {
		if (__length > this.MAX_SIZE_BUFFER) {
			throw new FileTooLargeException();
		}
	}

	/**
     * Check input stream ready.
     *
     * @throws NullPointerException
     *             the null pointer exception
     */
	private void checkInputStreamReady() throws NullPointerException {
		if (this.dataInputStream == null) {
			throw new NullPointerException("Must set inputstream to this reader");
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.io.Closeable#close()
	 */
	@Override
	public void close() throws IOException {
		this.dataInputStream.close();
	}

	/**
     * Read byte.
     *
     * @return the byte
     * @throws IOException
     *             Signals that an I/O exception has occurred.
     */
	public byte readByte() throws IOException {
		this.checkInputStreamReady();
		return this.dataInputStream.readByte();
	}

	/**
     * Read byte array.
     *
     * @return the byte[]
     * @throws IOException
     *             Signals that an I/O exception has occurred.
     */
	public byte[] readByteArray() throws IOException {
		int _length = this.readInt();
		return this.readByteArray(_length);
	}

	/**
     * Read byte array.
     *
     * @param __length
     *            the __length
     * @return the byte[]
     * @throws IOException
     *             Signals that an I/O exception has occurred.
     */
	public byte[] readByteArray(int __length) throws IOException {
		this.checkInputStreamReady();
		this.checkArraysTooLarge(__length);
		byte[] _bytes = new byte[__length];
		this.dataInputStream.read(_bytes);
		return _bytes;
	}

	/**
     * Read double.
     *
     * @return the double
     * @throws IOException
     *             Signals that an I/O exception has occurred.
     */
	public double readDouble() throws IOException {
		this.checkInputStreamReady();
		return this.dataInputStream.readDouble();
	}

	/**
     * Read file.
     *
     * @return the file
     * @throws IOException
     *             Signals that an I/O exception has occurred.
     */
	public File readFile() throws IOException {
		File _tempFile = File.createTempFile("sockettempfile", "tmp");

		try {
			byte[] _bytes = this.readByteArray();
			FileUtils.writeByteArrayToFile(_tempFile, _bytes, true);

			return _tempFile;
		}
		catch (IOException __ex) {
			_tempFile.delete();
			throw __ex;
		}

	}

	/**
     * Read float.
     *
     * @return the float
     * @throws IOException
     *             Signals that an I/O exception has occurred.
     */
	public float readFloat() throws IOException {
		this.checkInputStreamReady();
		return this.dataInputStream.readFloat();
	}

	/**
     * Read int.
     *
     * @return the int
     * @throws IOException
     *             Signals that an I/O exception has occurred.
     */
	public int readInt() throws IOException {
		this.checkInputStreamReady();
		return this.dataInputStream.readInt();
	}

	/**
     * Read json.
     *
     * @return the json object
     * @throws IOException
     *             Signals that an I/O exception has occurred.
     */
	public JsonObject readJson() throws IOException {
		String _s = this.readString();
		return (JsonObject) new JsonParser().parse(_s);
	}

	/**
     * Read long.
     *
     * @return the long
     * @throws IOException
     *             Signals that an I/O exception has occurred.
     */
	public long readLong() throws IOException {
		this.checkInputStreamReady();
		return this.dataInputStream.readLong();
	}

	/**
     * Read object.
     *
     * @param __clazz
     *            the __clazz
     * @return the object
     * @throws Exception
     *             the exception
     */
	public Object readObject(@SuppressWarnings("rawtypes") Class __clazz) throws Exception {
		Object _object = __clazz.newInstance();
		Field[] _fields = __clazz.getDeclaredFields();
		for (Field _field : _fields) {
			_field.setAccessible(true);
			Object _fieldType = _field.getType().newInstance();
			if (_fieldType instanceof Byte) {
				_field.set(_object, this.readByte());
			}
			else if (_fieldType instanceof Byte[]) {
				_field.set(_object, this.readByteArray());
			}
			else if (_fieldType instanceof Double) {
				_field.set(_object, this.readDouble());
			}
			else if (_fieldType instanceof Float) {
				_field.set(_object, this.readFloat());
			}
			else if (_fieldType instanceof File) {
				_field.set(_object, this.readFile());
			}
			else if (_fieldType instanceof Integer) {
				_field.set(_object, this.readInt());
			}
			else if (_fieldType instanceof Long) {
				_field.set(_object, this.readLong());
			}
			else if (_fieldType instanceof Short) {
				_field.set(_object, this.readShort());
			}
			else if (_fieldType instanceof String) {
				_field.set(_object, this.readString());
			}
		}
		return _object;
	}

	/**
     * Read short.
     *
     * @return the short
     * @throws IOException
     *             Signals that an I/O exception has occurred.
     */
	public short readShort() throws IOException {
		this.checkInputStreamReady();
		return this.dataInputStream.readShort();
	}

	/**
     * Read string.
     *
     * @return the string
     * @throws IOException
     *             Signals that an I/O exception has occurred.
     */
	public String readString() throws IOException {
		this.checkInputStreamReady();
		byte[] _bytes = readByteArray();
		String _s;
		try {
			_s = new String(_bytes, "UTF-8");
		}
		catch (UnsupportedEncodingException e) {
			Reader.LOGGER.warn("Unsupport UTF-8");
			_s = new String(_bytes);
		}
		return _s;
	}

	/**
     * Sets the data input stream.
     *
     * @param __inputStream
     *            the new data input stream
     */
	public void setDataInputStream(InputStream __inputStream) {
		this.dataInputStream = new DataInputStream(__inputStream);
	}
}
