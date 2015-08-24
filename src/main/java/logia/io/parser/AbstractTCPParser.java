package logia.io.parser;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.lang.reflect.Field;
import java.net.SocketException;
import java.net.SocketTimeoutException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import logia.io.util.Reader;
import logia.io.util.Writer;
import logia.socket.Interface.ParserInterface;
import logia.socket.Interface.ReadDataInterface;
import logia.socket.Interface.SocketClientInterface;
import logia.socket.Interface.WriteDataInterface;
import logia.utility.readfile.XmlUtil;

import org.apache.log4j.Logger;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

/**
 * The Class AbstractParser.
 * 
 * @author Paul Mai
 */
public abstract class AbstractTCPParser implements ParserInterface {

	/** The Constant DICTIONARY, use to get data type by input string. */
	protected static final Map<String, Byte> DICTIONARY;

	/** The Constant TYPE_BYTE. */
	protected static final byte              TYPE_BYTE       = 1;

	/** The Constant TYPE_BYTE_ARRAY. */
	protected static final byte              TYPE_BYTE_ARRAY = 2;

	/** The Constant TYPE_DOUBLE. */
	protected static final byte              TYPE_DOUBLE     = 4;

	/** The Constant TYPE_FLOAT. */
	protected static final byte              TYPE_FLOAT      = 5;

	/** The Constant TYPE_FILE. */
	protected static final byte              TYPE_FILE       = 3;

	/** The Constant TYPE_INTERGER. */
	protected static final byte              TYPE_INTERGER   = 6;

	/** The Constant TYPE_LIST. */
	protected static final byte              TYPE_LIST       = 10;

	/** The Constant TYPE_LONG. */
	protected static final byte              TYPE_LONG       = 7;

	/** The Constant TYPE_SHORT. */
	protected static final byte              TYPE_SHORT      = 8;

	/** The Constant TYPE_STRING. */
	protected static final byte              TYPE_STRING     = 9;

	static {
		DICTIONARY = new HashMap<String, Byte>();

		AbstractTCPParser.DICTIONARY.put("byte", AbstractTCPParser.TYPE_BYTE);
		AbstractTCPParser.DICTIONARY.put("Byte", AbstractTCPParser.TYPE_BYTE);
		AbstractTCPParser.DICTIONARY.put("java.lang.Byte", AbstractTCPParser.TYPE_BYTE);

		AbstractTCPParser.DICTIONARY.put("byte[]", AbstractTCPParser.TYPE_BYTE_ARRAY);
		AbstractTCPParser.DICTIONARY.put("bytearray", AbstractTCPParser.TYPE_BYTE_ARRAY);
		AbstractTCPParser.DICTIONARY.put("byte_array", AbstractTCPParser.TYPE_BYTE_ARRAY);
		AbstractTCPParser.DICTIONARY.put("Bytearray", AbstractTCPParser.TYPE_BYTE_ARRAY);
		AbstractTCPParser.DICTIONARY.put("ByteArray", AbstractTCPParser.TYPE_BYTE_ARRAY);

		AbstractTCPParser.DICTIONARY.put("double", AbstractTCPParser.TYPE_DOUBLE);
		AbstractTCPParser.DICTIONARY.put("Double", AbstractTCPParser.TYPE_DOUBLE);
		AbstractTCPParser.DICTIONARY.put("java.lang.Double", AbstractTCPParser.TYPE_DOUBLE);

		AbstractTCPParser.DICTIONARY.put("float", AbstractTCPParser.TYPE_FLOAT);
		AbstractTCPParser.DICTIONARY.put("Float", AbstractTCPParser.TYPE_FLOAT);
		AbstractTCPParser.DICTIONARY.put("java.lang.Float", AbstractTCPParser.TYPE_FLOAT);

		AbstractTCPParser.DICTIONARY.put("java.io.File", AbstractTCPParser.TYPE_FILE);
		AbstractTCPParser.DICTIONARY.put("File", AbstractTCPParser.TYPE_FILE);
		AbstractTCPParser.DICTIONARY.put("file", AbstractTCPParser.TYPE_FILE);

		AbstractTCPParser.DICTIONARY.put("int", AbstractTCPParser.TYPE_INTERGER);
		AbstractTCPParser.DICTIONARY.put("integer", AbstractTCPParser.TYPE_INTERGER);
		AbstractTCPParser.DICTIONARY.put("Integer", AbstractTCPParser.TYPE_INTERGER);
		AbstractTCPParser.DICTIONARY.put("java.lang.Integer", AbstractTCPParser.TYPE_INTERGER);

		AbstractTCPParser.DICTIONARY.put("long", AbstractTCPParser.TYPE_LONG);
		AbstractTCPParser.DICTIONARY.put("Long", AbstractTCPParser.TYPE_LONG);
		AbstractTCPParser.DICTIONARY.put("java.lang.Long", AbstractTCPParser.TYPE_LONG);

		AbstractTCPParser.DICTIONARY.put("arraylist", AbstractTCPParser.TYPE_LIST);
		AbstractTCPParser.DICTIONARY.put("ArrayList", AbstractTCPParser.TYPE_LIST);
		AbstractTCPParser.DICTIONARY.put("list", AbstractTCPParser.TYPE_LIST);
		AbstractTCPParser.DICTIONARY.put("List", AbstractTCPParser.TYPE_LIST);

		AbstractTCPParser.DICTIONARY.put("short", AbstractTCPParser.TYPE_SHORT);
		AbstractTCPParser.DICTIONARY.put("Short", AbstractTCPParser.TYPE_SHORT);
		AbstractTCPParser.DICTIONARY.put("java.lang.Short", AbstractTCPParser.TYPE_SHORT);

		AbstractTCPParser.DICTIONARY.put("string", AbstractTCPParser.TYPE_STRING);
		AbstractTCPParser.DICTIONARY.put("String", AbstractTCPParser.TYPE_STRING);
		AbstractTCPParser.DICTIONARY.put("java.lang.String", AbstractTCPParser.TYPE_STRING);
	}

	/** The _command type. */
	protected String                         _commandType;

	/** The path to xml define data parser file . */
	protected String                         _definePath;

	/** The _command. */
	protected Map<String, Class<?>>          _mapCommand     = new HashMap<String, Class<?>>();

	/** The reader. */
	protected Reader                         _reader;

	/** The writer. */
	protected Writer                         _writer;

	/** The _xml. */
	protected XmlUtil                        _xml;

	/** The logger. */
	protected final Logger                   LOGGER          = Logger.getLogger(getClass());

	/**
	 * Instantiates a new abstract parser.
	 */
	public AbstractTCPParser() {
		this._reader = new Reader();
		this._writer = new Writer();
		this._definePath = AbstractTCPParser.class.getClassLoader().getResource("data-package.xml").getPath();
		this._xml = new XmlUtil(this._definePath);
		this.contextInitialized();
	}

	/**
	 * Instantiates a new abstract parser.
	 *
	 * @param definePath the define path
	 */
	public AbstractTCPParser(String definePath) {
		this._reader = new Reader();
		this._writer = new Writer();
		this._definePath = definePath;
		this._xml = new XmlUtil(this._definePath);
		this.contextInitialized();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see logia.socket.Interface.ParserInterface#applyInputStream(logia.socket.Interface.SocketClientInterface)
	 */
	@Override
	public void applyInputStream(SocketClientInterface clientSocket) throws SocketTimeoutException, SocketException, IOException, Exception {
		ReadDataInterface data = null;
		while (clientSocket.isConnected()) {
			long a = System.currentTimeMillis();
			data = this.readData(clientSocket.getInputStream());
			long b = System.currentTimeMillis();
			this.LOGGER.debug("Finish read data after " + (b - a) / 1000 + "s");
			if (data != null) {
				if (clientSocket.isWaitForReturn()) {
					clientSocket.setReturned(data);
					synchronized (clientSocket) {
						clientSocket.notify();
					}
				}
				else {
					data.executeData(clientSocket);
				}
			}
			else {
				this.LOGGER.warn("Not recognize data from inputstream");
			}
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see logia.socket.Interface.ParserInterface#applyOutputStream(java.io.OutputStream, logia.socket.Interface.WriteDataInterface, int)
	 */
	@Override
	public void applyOutputStream(OutputStream outputStream, WriteDataInterface dataListener, Object command) throws Exception {
		long a = System.currentTimeMillis();
		this.writeData(command, outputStream, dataListener);
		long b = System.currentTimeMillis();
		this.LOGGER.debug("Finish send data after " + (b - a) / 1000 + "s");
	}

	/**
	 * Context initialized.
	 */
	private void contextInitialized() {
		NodeList list = this._xml.getListNode("define", this._xml.getRoot());
		try {
			// List all command
			Node define = list.item(0);
			this._commandType = this._xml.getAttribute(define, "type");
			NodeList listCommand = this._xml.getListNode("define-command", (Element) define);
			for (int i = 0; i < listCommand.getLength(); i++) {
				Node nodeCommand = listCommand.item(i);

				// Get command type
				String valueCommand = this._xml.getAttribute(nodeCommand, "value");
				String className = this._xml.getValue(nodeCommand);

				this._mapCommand.put(valueCommand, Class.forName(className));
			}
		}
		catch (Exception e) {
			this.LOGGER.error("Could not initialized parser context", e);
		}
	}

	/**
	 * Gets the data type.
	 *
	 * @param type the type
	 * @return the data type
	 */
	protected Byte getDataType(String type) {
		return AbstractTCPParser.DICTIONARY.get(type);
	}

	/**
	 * Gets the instance read data.
	 *
	 * @param idCommand the id command
	 * @return the instance read data
	 */
	protected ReadDataInterface getInstanceReadData(String idCommand) {
		Class<?> clazz = this._mapCommand.get(idCommand);
		if (clazz != null) {
			try {
				return (ReadDataInterface) clazz.newInstance();
			}
			catch (InstantiationException | IllegalAccessException e) {
				this.LOGGER.error("Get instance of data error", e);
				return null;
			}
		}
		else {
			return null;
		}
	}

	/**
	 * Gets the instance write data.
	 *
	 * @param idCommand the id command
	 * @return the instance write data
	 */
	protected WriteDataInterface getInstanceWriteData(String idCommand) {
		Class<?> clazz = this._mapCommand.get(idCommand);
		if (clazz != null) {
			try {
				return (WriteDataInterface) clazz.newInstance();
			}
			catch (InstantiationException | IllegalAccessException e) {
				this.LOGGER.error("Get instance of data error", e);
				return null;
			}
		}
		else {
			return null;
		}
	}

	/**
	 * Read data.
	 *
	 * @param inputstream the inputstream
	 * @return the read data interface
	 * @throws Exception the exception
	 */
	protected abstract ReadDataInterface readData(InputStream inputstream) throws Exception;

	/**
	 * Read data by type.
	 *
	 * @param typeData the type data
	 * @param inputstream the inputstream
	 * @return the object
	 * @throws IOException Signals that an I/O exception has occurred.
	 */
	protected Object readDataByType(String typeData, InputStream inputstream) throws IOException {
		Object value = null;
		Byte objType = this.getDataType(typeData);
		if (objType == null) {
			objType = Byte.MIN_VALUE;
		}
		switch (objType) {
			case TYPE_BYTE:
				value = this._reader.readByte(inputstream);
				break;

			case TYPE_BYTE_ARRAY:
				value = this._reader.readByteArray(inputstream);
				break;

			case TYPE_DOUBLE:
				value = this._reader.readDouble(inputstream);
				break;

			case TYPE_FLOAT:
				value = this._reader.readFloat(inputstream);
				break;

			case TYPE_FILE:
				value = this._reader.readFile(inputstream);
				break;

			case TYPE_INTERGER:
				value = this._reader.readInt(inputstream);
				break;

			case TYPE_LONG:
				value = this._reader.readLong(inputstream);
				break;

			case TYPE_SHORT:
				value = this._reader.readShort(inputstream);
				break;

			case TYPE_STRING:
				value = this._reader.readString(inputstream);
				break;

			case TYPE_LIST:
				value = new ArrayList<>();
				break;

			default:
				try {
					value = Class.forName(typeData).newInstance();
					value = this._reader.readObject(value.getClass(), inputstream);
				}
				catch (Exception e) {
					this.LOGGER.error("Read data element error", e);
				}
				break;
		}
		return value;
	}

	/**
	 * Sets the value of.
	 *
	 * @param clazz the clazz
	 * @param lookingForValue the looking for value
	 * @param value the value
	 * @throws Exception the exception
	 */
	protected void setValueOf(Object clazz, String lookingForValue, Object value) throws Exception {
		Field field = clazz.getClass().getDeclaredField(lookingForValue);
		field.setAccessible(true);
		field.set(clazz, value);
	}

	/**
	 * Write data.
	 *
	 * @param command the command
	 * @param out the out
	 * @param data the data
	 * @throws Exception the exception
	 */
	protected abstract void writeData(Object command, OutputStream out, WriteDataInterface data) throws Exception;

	/**
	 * Write data by type.
	 *
	 * @param typeData the type data
	 * @param nameData the name data
	 * @param out the out
	 * @param data the data
	 * @return the string
	 * @throws Exception the exception
	 */
	protected String writeDataByType(String typeData, String nameData, OutputStream out, Object data) throws Exception {
		Byte objType = this.getDataType(typeData);
		if (objType == null) {
			objType = Byte.MIN_VALUE;
		}
		Field field;
		String returnValue = "";
		switch (objType) {

			case TYPE_BYTE:
				if (nameData.equals("")) {
					this._writer.writeByte(out, (byte) data);
					returnValue = data.toString();
				}
				else {
					field = data.getClass().getDeclaredField(nameData);
					field.setAccessible(true);
					this._writer.writeByte(out, field.getByte(data));
					returnValue = field.get(data).toString();
				}
				break;

			case TYPE_BYTE_ARRAY:
				if (nameData.equals("")) {
					this._writer.writeByteArray(out, (byte[]) data);
					returnValue = data.toString();
				}
				else {
					field = data.getClass().getDeclaredField(nameData);
					field.setAccessible(true);
					this._writer.writeByteArray(out, (byte[]) field.get(data));
					returnValue = field.get(data).toString();
				}
				break;

			case TYPE_DOUBLE:
				if (nameData.equals("")) {
					this._writer.writeDouble(out, (double) data);
					returnValue = data.toString();
				}
				else {
					field = data.getClass().getDeclaredField(nameData);
					field.setAccessible(true);
					this._writer.writeDouble(out, field.getDouble(data));
					returnValue = field.get(data).toString();
				}
				break;

			case TYPE_FLOAT:
				if (nameData.equals("")) {
					this._writer.writeFloat(out, (float) data);
					returnValue = data.toString();
				}
				else {
					field = data.getClass().getDeclaredField(nameData);
					field.setAccessible(true);
					this._writer.writeFloat(out, field.getFloat(data));
					returnValue = field.get(data).toString();
				}
				break;

			case TYPE_FILE:
				if (nameData.equals("")) {
					this._writer.writeFile(out, (File) data);
					returnValue = data.toString();
				}
				else {
					field = data.getClass().getDeclaredField(nameData);
					field.setAccessible(true);
					this._writer.writeFile(out, (File) field.get(data));
					returnValue = field.get(data).toString();
				}
				break;

			case TYPE_INTERGER:
				if (nameData.equals("")) {
					this._writer.writeInt(out, (int) data);
					returnValue = data.toString();
				}
				else {
					field = data.getClass().getDeclaredField(nameData);
					field.setAccessible(true);
					this._writer.writeInt(out, field.getInt(data));
					returnValue = field.get(data).toString();
				}
				break;

			case TYPE_LONG:
				if (nameData.equals("")) {
					this._writer.writeLong(out, (long) data);
					returnValue = data.toString();
				}
				else {
					field = data.getClass().getDeclaredField(nameData);
					field.setAccessible(true);
					this._writer.writeLong(out, field.getLong(data));
					returnValue = field.get(data).toString();
				}
				break;

			case TYPE_SHORT:
				if (nameData.equals("")) {
					this._writer.writeShort(out, (short) data);
					returnValue = data.toString();
				}
				else {
					field = data.getClass().getDeclaredField(nameData);
					field.setAccessible(true);
					this._writer.writeShort(out, field.getShort(data));
					returnValue = field.get(data).toString();
				}
				break;

			case TYPE_STRING:
				if (nameData.equals("")) {
					this._writer.writeString(out, (String) data);
					returnValue = data.toString();
				}
				else {
					field = data.getClass().getDeclaredField(nameData);
					field.setAccessible(true);
					this._writer.writeString(out, (String) field.get(data));
					returnValue = field.get(data).toString();
				}
				break;

			case TYPE_LIST:
				field = data.getClass().getDeclaredField(nameData);
				field.setAccessible(true);
				@SuppressWarnings("rawtypes")
				List list = (List) field.get(data);
				this._writer.writeInt(out, list.size());
				for (Object object : list) {
					String objectType = object.getClass().getName();
					this.writeDataByType(objectType, "", out, object);
				}
				break;

			default:
				this._writer.writeObject(out, data);
				returnValue = data.toString();
				break;
		}
		return returnValue;
	}

}
