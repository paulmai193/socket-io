package logia.io.util;

import java.io.Closeable;
import java.io.DataOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.util.Arrays;

import org.apache.commons.io.FileUtils;
import org.apache.log4j.Logger;

import com.google.gson.JsonObject;

import logia.io.exception.FileTooLargeException;

/**
 * The Class Writer.
 *
 * @author Paul Mai
 */
public class Writer implements Closeable {

	/** The Constant LOGGER. */
	private static final Logger	LOGGER = Logger.getLogger(Writer.class);

	/** The max size buffer. */
	private final int			MAX_SIZE_BUFFER;

	/** The data output stream. */
	private DataOutputStream	dataOutputStream;

	/**
	 * Instantiates a new writer.
	 */
	public Writer() {
		this(10 * 1024);
	}

	/**
     * Instantiates a new writer.
     *
     * @param __bufferSize
     *            the __buffer size
     */
	public Writer(int __bufferSize) {
		this.MAX_SIZE_BUFFER = __bufferSize;
	}

	/**
     * Sets the data output stream.
     *
     * @param __outputStream
     *            the new data output stream
     */
	public void setDataOutputStream(OutputStream __outputStream) {
		this.dataOutputStream = new DataOutputStream(__outputStream);
	}

	/**
     * Check output stream ready.
     *
     * @throws NullPointerException
     *             the null pointer exception
     */
	private void checkOutputStreamReady() throws NullPointerException {
		if (this.dataOutputStream == null) {
			throw new NullPointerException("Must set outputstream to this writer");
		}
	}

	/**
     * Check arrays too large.
     *
     * @param __length
     *            the __length
     * @throws FileTooLargeException
     *             the file too large exception
     */
	private void checkArraysTooLarge(long __length) throws FileTooLargeException {
		if (__length > this.MAX_SIZE_BUFFER) {
			throw new FileTooLargeException();
		}
	}

	/**
     * Write byte.
     *
     * @param __data
     *            the __data
     * @throws IOException
     *             Signals that an I/O exception has occurred.
     */
	public void writeByte(byte __data) throws IOException {
		checkOutputStreamReady();
		Writer.LOGGER.debug("Write " + __data + " as byte");
		dataOutputStream.writeByte(__data);
	}

	/**
     * Write byte array.
     *
     * @param __data
     *            the __data
     * @throws IOException
     *             Signals that an I/O exception has occurred.
     */
	public void writeByteArray(byte[] __data) throws IOException {
		Writer.LOGGER.debug("Write " + Arrays.toString(__data) + " as byte array");
		int _length = __data.length;
		checkArraysTooLarge(_length);
		this.writeInt(_length);
		dataOutputStream.write(__data);
	}

	/**
     * Write double.
     *
     * @param __data
     *            the __data
     * @throws IOException
     *             Signals that an I/O exception has occurred.
     */
	public void writeDouble(double __data) throws IOException {
		checkOutputStreamReady();
		Writer.LOGGER.debug("Write " + __data + " as double");
		dataOutputStream.writeDouble(__data);
	}

	/**
     * Write file.
     *
     * @param __data
     *            the __data
     * @throws IOException
     *             Signals that an I/O exception has occurred.
     */
	public void writeFile(File __data) throws IOException {
		Writer.LOGGER.debug("Write " + __data + " as file");
		byte[] _bytes = FileUtils.readFileToByteArray(__data);
		writeByteArray(_bytes);
	}

	/**
     * Write float.
     *
     * @param __data
     *            the __data
     * @throws IOException
     *             Signals that an I/O exception has occurred.
     */
	public void writeFloat(float __data) throws IOException {
		checkOutputStreamReady();
		Writer.LOGGER.debug("Write " + __data + " as float");
		dataOutputStream.writeFloat(__data);
	}

	/**
     * Write int.
     *
     * @param __data
     *            the __data
     * @throws IOException
     *             Signals that an I/O exception has occurred.
     */
	public void writeInt(int __data) throws IOException {
		checkOutputStreamReady();
		Writer.LOGGER.debug("Write " + __data + " as integer");
		dataOutputStream.writeInt(__data);
	}

	/**
     * Write json.
     *
     * @param __data
     *            the __data
     * @throws IOException
     *             Signals that an I/O exception has occurred.
     */
	public void writeJson(JsonObject __data) throws IOException {
		Writer.LOGGER.debug("Write " + __data + " as json object");
		this.writeString(__data.toString());
	}

	/**
     * Write long.
     *
     * @param __data
     *            the __data
     * @throws IOException
     *             Signals that an I/O exception has occurred.
     */
	public void writeLong(long __data) throws IOException {
		checkOutputStreamReady();
		Writer.LOGGER.debug("Write " + __data + " as long");
		dataOutputStream.writeLong(__data);
	}

	/**
     * Write short.
     *
     * @param __data
     *            the __data
     * @throws IOException
     *             Signals that an I/O exception has occurred.
     */
	public void writeShort(short __data) throws IOException {
		checkOutputStreamReady();
		Writer.LOGGER.debug("Write " + __data + " as short");
		dataOutputStream.writeShort(__data);
	}

	/**
     * Write string.
     *
     * @param __data
     *            the __data
     * @throws IOException
     *             Signals that an I/O exception has occurred.
     */
	public void writeString(String __data) throws IOException {
		Writer.LOGGER.debug("Write " + __data + " as string");
		byte[] _bytes;
		try {
			_bytes = __data.getBytes("UTF-8");
		}
		catch (UnsupportedEncodingException __ex) {
			LOGGER.warn("Unsupport UTF-8");
			_bytes = __data.getBytes();
		}
		this.writeByteArray(_bytes);
	}

    /**
     * Flush.
     *
     * @throws IOException
     *             Signals that an I/O exception has occurred.
     */
	public void flush() throws IOException {
		checkOutputStreamReady();
		dataOutputStream.flush();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.io.Closeable#close()
	 */
	@Override
	public void close() throws IOException {
		dataOutputStream.close();
	}

}
