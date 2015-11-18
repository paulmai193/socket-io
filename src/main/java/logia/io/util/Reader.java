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

/**
 * The Class Reader read and parse data from inputstream to each data package type.
 * 
 * @author Paul Mai
 */
public class Reader {

	/** The logger. */
	private final Logger LOGGER          = Logger.getLogger(this.getClass());

	/** The max size buffer. */
	private final int    MAX_SIZE_BUFFER = 64 * 1024;

	/**
	 * Instantiates a new reader.
	 * 
	 * @throws IOException Signals that an I/O exception has occurred.
	 */
	public Reader() {

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
		int length = this.readInt(in);
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
		// ByteArrayOutputStream arr = new ByteArrayOutputStream();
		// for (int i = 0; i < 8; i++) {
		// arr.write(this.readByte(in));
		// }
		// return ByteBuffer.wrap(arr.toByteArray()).getDouble();
		return (in.read() << 56) | (in.read() << 48) | (in.read() << 40) | (in.read() << 32) | (in.read() << 24) | (in.read() << 16)
		        | (in.read() << 8) | (in.read());
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
		this.LOGGER.debug("TempFile: " + tempFile.getAbsolutePath());

		// OLD
		// ByteArrayOutputStream arr = new ByteArrayOutputStream(this.MAX_SIZE_BUFFER);
		// for (long i = 1; i <= length; i++) {
		// byte b = this.readByte(in);
		// arr.write(b);
		// if (i % this.MAX_SIZE_BUFFER == 0) {
		// FileUtils.writeByteArrayToFile(tempFile, arr.toByteArray(), true);
		// arr.reset();
		//
		// this.LOGGER.debug("end buffer");
		// }
		// }
		// if (arr.size() > 0) {
		// FileUtils.writeByteArrayToFile(tempFile, arr.toByteArray(), true);
		//
		// this.LOGGER.debug("end buffer");
		// }

		// NEW
		// byte[] bytes = new byte[(int) length];
		// int r = in.read(bytes, 0, (int) length);
		// System.out.println(r);
		// FileUtils.writeByteArrayToFile(tempFile, bytes, true);
		// bytes = null;

		// byte[] bytes = new byte[this.MAX_SIZE_BUFFER];
		// int c = 0;
		// for (long i = 1; i <= length; i++) {
		// byte b = this.readByte(in);
		// bytes[c] = b;
		// c++;
		// if (i % this.MAX_SIZE_BUFFER == 0) {
		// FileUtils.writeByteArrayToFile(tempFile, bytes, true);
		//
		// bytes = new byte[this.MAX_SIZE_BUFFER];
		// c = 0;
		//
		// this.LOGGER.debug("write buffers to file");
		// }
		// }

		int n = (int) (length / this.MAX_SIZE_BUFFER);
		for (int i = 0; i < n; i++) {
			byte[] barray = new byte[this.MAX_SIZE_BUFFER];
			int r = in.read(barray, 0, this.MAX_SIZE_BUFFER);
			System.out.println(r);
			FileUtils.writeByteArrayToFile(tempFile, barray, true);
		}
		byte[] barray = new byte[(int) (length - (this.MAX_SIZE_BUFFER * n))];
		in.read(barray, 0, (int) (length - (this.MAX_SIZE_BUFFER * n)));
		FileUtils.writeByteArrayToFile(tempFile, barray, true);

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
		// ByteArrayOutputStream arr = new ByteArrayOutputStream();
		// for (int i = 0; i < 4; i++) {
		// arr.write(this.readByte(in));
		// }
		// return ByteBuffer.wrap(arr.toByteArray()).getFloat();
		return (in.read() << 24) | (in.read() << 16) | (in.read() << 8) | (in.read());
	}

	/**
	 * Read int.
	 *
	 * @param in the in
	 * @return the int
	 * @throws IOException Signals that an I/O exception has occurred.
	 */
	public int readInt(InputStream in) throws IOException {
		// ByteArrayOutputStream arr = new ByteArrayOutputStream();
		// for (int i = 0; i < 4; i++) {
		// arr.write(this.readByte(in));
		// }
		// return ByteBuffer.wrap(arr.toByteArray()).getInt();
		return (in.read() << 24) | (in.read() << 16) | (in.read() << 8) | (in.read());
	}

	/**
	 * Read long.
	 *
	 * @param in the in
	 * @return the long
	 * @throws IOException Signals that an I/O exception has occurred.
	 */
	public long readLong(InputStream in) throws IOException {
		// ByteArrayOutputStream arr = new ByteArrayOutputStream();
		// for (int i = 0; i < 8; i++) {
		// arr.write(this.readByte(in));
		// }
		// return ByteBuffer.wrap(arr.toByteArray()).getLong();
		return (in.read() << 56) | (in.read() << 48) | (in.read() << 40) | (in.read() << 32) | (in.read() << 24) | (in.read() << 16)
		        | (in.read() << 8) | (in.read());
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
}
