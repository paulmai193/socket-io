package logia.io.util;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.ByteBuffer;

import org.apache.log4j.Logger;

import com.google.gson.JsonObject;

/**
 * The Class Writer write data from package into outputstream.
 * 
 * @author Paul Mai
 */
public class Writer {

	/** The logger. */
	private static final Logger LOGGER = Logger.getLogger(Writer.class);

	/** The max size buffer. */
	private final int           MAX_SIZE_BUFFER;

	/**
	 * Instantiates a new writer.
	 */
	public Writer() {
		this.MAX_SIZE_BUFFER = 20 * 1024;
	}

	/**
	 * Instantiates a new writer.
	 *
	 * @param __bufferSize the buffer size
	 */
	public Writer(int __bufferSize) {
		this.MAX_SIZE_BUFFER = __bufferSize;
	}

	/**
	 * Write byte.
	 * 
	 * @param __out the data output stream
	 * @param __data the data
	 * @throws IOException Signals that an I/O exception has occurred.
	 */
	public void writeByte(OutputStream __out, byte __data) throws IOException {
		__out.write(__data);
	}

	/**
	 * Write byte array.
	 * 
	 * @param __out the data output stream
	 * @param __data the data
	 * @throws IOException Signals that an I/O exception has occurred.
	 */
	public void writeByteArray(OutputStream __out, byte[] __data) throws IOException {
		int _length = __data.length;
		this.writeInt(__out, _length);
		__out.write(__data);
	}

	/**
	 * Write double.
	 * 
	 * @param __out the data output stream
	 * @param __data the data
	 * @throws IOException Signals that an I/O exception has occurred.
	 */
	public void writeDouble(OutputStream __out, double __data) throws IOException {
		byte[] _bytes = new byte[8];
		ByteBuffer.wrap(_bytes).putDouble(__data);
		__out.write(_bytes);
	}

	/**
	 * Write file.
	 *
	 * @param __out the out
	 * @param __data the data
	 * @throws IOException Signals that an I/O exception has occurred.
	 */
	public void writeFile(OutputStream __out, File __data) throws IOException {
		// OLD
		long length = __data.length();
		this.writeLong(__out, length);
		try (FileInputStream _f = new FileInputStream(__data)) {
			byte[] _barray = new byte[this.MAX_SIZE_BUFFER];
			while (_f.read(_barray, 0, this.MAX_SIZE_BUFFER) != -1) {
				__out.write(_barray);
				__out.flush();
			}
		}

		// NEW
		// long length = data.length();
		// this.writeLong(out, length);
		// int n = (int) (length / this.MAX_SIZE_BUFFER);
		// try (FileInputStream f = new FileInputStream(data)) {
		//
		// // OLD
		// for (int i = 0; i < n; i++) {
		// byte[] barray = new byte[this.MAX_SIZE_BUFFER];
		// int buf = f.read(barray, 0, this.MAX_SIZE_BUFFER);
		// this.LOGGER.debug("Write " + buf + " bytes");
		// out.write(barray);
		// out.flush();
		// }
		// byte[] barray = new byte[(int) (length - (this.MAX_SIZE_BUFFER * n))];
		// int buf = f.read(barray, 0, (int) (length - (this.MAX_SIZE_BUFFER * n)));
		// this.LOGGER.debug("Write " + buf + " bytes");
		// out.write(barray);
		//
		// // NEW
		// // out.write(IOUtils.toByteArray(f));
		// }
	}

	/**
	 * Write float.
	 * 
	 * @param __out the data output stream
	 * @param __data the data
	 * @throws IOException Signals that an I/O exception has occurred.
	 */
	public void writeFloat(OutputStream __out, float __data) throws IOException {
		byte[] _bytes = new byte[4];
		ByteBuffer.wrap(_bytes).putFloat(__data);
		__out.write(_bytes);
	}

	/**
	 * Write int.
	 *
	 * @param __out the out
	 * @param __data the data
	 * @throws IOException Signals that an I/O exception has occurred.
	 */
	public void writeInt(OutputStream __out, int __data) throws IOException {
		byte[] _bytes = new byte[4];
		ByteBuffer.wrap(_bytes).putInt(__data);
		__out.write(_bytes);
	}

	/**
	 * Write json.
	 *
	 * @param __out the out
	 * @param __data the data
	 * @throws IOException Signals that an I/O exception has occurred.
	 */
	public void writeJson(OutputStream __out, JsonObject __data) throws IOException {
		this.writeString(__out, __data.toString());
	}

	/**
	 * Write long.
	 * 
	 * @param __out the data output stream
	 * @param __data the data
	 * @throws IOException Signals that an I/O exception has occurred.
	 */
	public void writeLong(OutputStream __out, long __data) throws IOException {
		byte[] _bytes = new byte[8];
		ByteBuffer.wrap(_bytes).putLong(__data);
		__out.write(_bytes);
	}

	/**
	 * Write short.
	 * 
	 * @param __out the data output stream
	 * @param __data the data
	 * @throws IOException Signals that an I/O exception has occurred.
	 */
	public void writeShort(OutputStream __out, short __data) throws IOException {
		byte[] _bytes = new byte[2];
		ByteBuffer.wrap(_bytes).putShort(__data);
		__out.write(_bytes);
	}

	/**
	 * Write string.
	 * 
	 * @param __out the data output stream
	 * @param __data the data
	 * @throws IOException Signals that an I/O exception has occurred.
	 */
	public void writeString(OutputStream __out, String __data) throws IOException {
		byte[] _bytes = __data.getBytes("UTF-8");
		this.writeByteArray(__out, _bytes);
	}

}
