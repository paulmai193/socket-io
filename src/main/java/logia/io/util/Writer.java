package logia.io.util;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.lang.reflect.Field;
import java.nio.ByteBuffer;

import org.apache.log4j.Logger;

/**
 * The Class Writer write data from package into outputstream.
 * 
 * @author Paul Mai
 */
public class Writer {

	/** The logger. */
	private final Logger LOGGER          = Logger.getLogger(this.getClass());

	/** The max size buffer. */
	private final int    MAX_SIZE_BUFFER = 64 * 1024;

	/**
	 * Instantiates a new writer.
	 */
	public Writer() {

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
		this.writeInt(out, length);
		out.write(data);
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
		out.write(bytes);
	}

	/**
	 * Write file.
	 *
	 * @param out the out
	 * @param data the data
	 * @throws IOException Signals that an I/O exception has occurred.
	 */
	public void writeFile(OutputStream out, File data) throws IOException {
		long length = data.length();
		this.writeLong(out, length);
		int n = (int) (length / this.MAX_SIZE_BUFFER);
		try (FileInputStream f = new FileInputStream(data)) {

			// OLD
			for (int i = 0; i < n; i++) {
				byte[] barray = new byte[this.MAX_SIZE_BUFFER];
				f.read(barray, 0, this.MAX_SIZE_BUFFER);
				out.write(barray);
				out.flush();
			}
			byte[] barray = new byte[(int) (length - (this.MAX_SIZE_BUFFER * n))];
			f.read(barray, 0, (int) (length - (this.MAX_SIZE_BUFFER * n)));
			out.write(barray);

			// NEW
			// out.write(IOUtils.toByteArray(f));
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
		out.write(bytes);
	}

	/**
	 * Write int.
	 *
	 * @param out the out
	 * @param data the data
	 * @throws IOException Signals that an I/O exception has occurred.
	 */
	public void writeInt(OutputStream out, int data) throws IOException {
		byte[] bytes = new byte[4];
		ByteBuffer.wrap(bytes).putInt(data);
		out.write(bytes);
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
		out.write(bytes);
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
		for (Field field : fields) {
			field.setAccessible(true);
			Object fieldType = field.getType().newInstance();
			if (fieldType instanceof Byte) {
				this.writeByte(out, field.getByte(data));
			}
			else if (fieldType instanceof Byte[]) {
				this.writeByteArray(out, (byte[]) field.get(data));
			}
			else if (fieldType instanceof Double) {
				this.writeDouble(out, field.getDouble(data));
			}
			else if (fieldType instanceof Float) {
				this.writeFloat(out, field.getFloat(data));
			}
			else if (fieldType instanceof Integer) {
				this.writeInt(out, field.getInt(data));
			}
			else if (fieldType instanceof Long) {
				this.writeLong(out, field.getLong(data));
			}
			else if (fieldType instanceof Short) {
				this.writeShort(out, field.getShort(data));
			}
			else if (fieldType instanceof String) {
				this.writeString(out, (String) field.get(data));
			}
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
		out.write(bytes);
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
		this.writeByteArray(out, bytes);
	}

}
