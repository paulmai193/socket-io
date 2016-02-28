package logia.io.parser;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.lang.reflect.Field;
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

import com.google.gson.JsonObject;

/**
 * The Class AbstractParser.
 * 
 * @deprecated since version 0.0.8
 * 
 * @author Paul Mai
 */
@Deprecated
public abstract class AbstractParser implements ParserInterface {

	/** The Constant DICTIONARY, use to get data type by input string. */
	protected static final Map<String, Byte> DICTIONARY;

	/** The logger. */
	protected static final Logger            LOGGER            = Logger.getLogger(AbstractParser.class);

	/** The Constant TYPE_BYTE. */
	protected static final byte              TYPE_BYTE         = 1;

	/** The Constant TYPE_BYTE_ARRAY. */
	protected static final byte              TYPE_BYTE_ARRAY   = 2;

	/** The Constant TYPE_DOUBLE. */
	protected static final byte              TYPE_DOUBLE       = 4;

	/** The Constant TYPE_FILE. */
	protected static final byte              TYPE_FILE         = 3;

	/** The Constant TYPE_FLOAT. */
	protected static final byte              TYPE_FLOAT        = 5;

	/** The Constant TYPE_INTERGER. */
	protected static final byte              TYPE_INTERGER     = 6;

	/** The Constant TYPE_JSON. */
	protected static final byte              TYPE_JSON         = 11;

	/** The Constant TYPE_LIST. */
	protected static final byte              TYPE_LIST         = 10;

	/** The Constant TYPE_LONG. */
	protected static final byte              TYPE_LONG         = 7;

	/** The Constant TYPE_SHORT. */
	protected static final byte              TYPE_SHORT        = 8;

	/** The Constant TYPE_STRING. */
	protected static final byte              TYPE_STRING       = 9;

	static {
		DICTIONARY = new HashMap<String, Byte>();

		AbstractParser.DICTIONARY.put("byte", AbstractParser.TYPE_BYTE);
		AbstractParser.DICTIONARY.put("java.lang.byte", AbstractParser.TYPE_BYTE);

		AbstractParser.DICTIONARY.put("byte[]", AbstractParser.TYPE_BYTE_ARRAY);
		AbstractParser.DICTIONARY.put("bytearray", AbstractParser.TYPE_BYTE_ARRAY);
		AbstractParser.DICTIONARY.put("byte_array", AbstractParser.TYPE_BYTE_ARRAY);

		AbstractParser.DICTIONARY.put("double", AbstractParser.TYPE_DOUBLE);
		AbstractParser.DICTIONARY.put("java.lang.double", AbstractParser.TYPE_DOUBLE);

		AbstractParser.DICTIONARY.put("float", AbstractParser.TYPE_FLOAT);
		AbstractParser.DICTIONARY.put("java.lang.float", AbstractParser.TYPE_FLOAT);

		AbstractParser.DICTIONARY.put("java.io.file", AbstractParser.TYPE_FILE);
		AbstractParser.DICTIONARY.put("file", AbstractParser.TYPE_FILE);

		AbstractParser.DICTIONARY.put("int", AbstractParser.TYPE_INTERGER);
		AbstractParser.DICTIONARY.put("integer", AbstractParser.TYPE_INTERGER);
		AbstractParser.DICTIONARY.put("java.lang.integer", AbstractParser.TYPE_INTERGER);

		AbstractParser.DICTIONARY.put("json", AbstractParser.TYPE_JSON);
		AbstractParser.DICTIONARY.put("jsonobject", AbstractParser.TYPE_JSON);

		AbstractParser.DICTIONARY.put("long", AbstractParser.TYPE_LONG);
		AbstractParser.DICTIONARY.put("java.lang.long", AbstractParser.TYPE_LONG);

		AbstractParser.DICTIONARY.put("arraylist", AbstractParser.TYPE_LIST);
		AbstractParser.DICTIONARY.put("list", AbstractParser.TYPE_LIST);

		AbstractParser.DICTIONARY.put("short", AbstractParser.TYPE_SHORT);
		AbstractParser.DICTIONARY.put("java.lang.short", AbstractParser.TYPE_SHORT);

		AbstractParser.DICTIONARY.put("string", AbstractParser.TYPE_STRING);
		AbstractParser.DICTIONARY.put("java.lang.string", AbstractParser.TYPE_STRING);
	}

	/** The _command type. */
	protected String                         commandType;

	/** The path to xml define data parser file . */
	protected String                         definePath;

	/** The _map receive command. */
	protected Map<String, Class<?>>          mapReceiveCommand = new HashMap<String, Class<?>>();

	/** The _map send command. */
	protected Map<String, Class<?>>          mapSendCommand    = new HashMap<String, Class<?>>();

	/** The reader. */
	protected Reader                         reader;

	/** The writer. */
	protected Writer                         writer;

	/** The _xml. */
	protected XmlUtil                        xml;

	/**
	 * Instantiates a new abstract parser.
	 *
	 * @throws ClassNotFoundException the class not found exception
	 */
	public AbstractParser() throws ClassNotFoundException {
		this.reader = new Reader();
		this.writer = new Writer();
		this.definePath = AbstractParser.class.getClassLoader().getResource("data-package.xml").getPath();
		this.xml = new XmlUtil(this.definePath);
		this.contextInitialized();
	}

	/**
	 * Instantiates a new abstract parser.
	 *
	 * @param __bufferSize the __buffer size
	 * @throws ClassNotFoundException the class not found exception
	 */
	public AbstractParser(int __bufferSize) throws ClassNotFoundException {
		this.reader = new Reader(__bufferSize);
		this.writer = new Writer(__bufferSize);
		this.definePath = AbstractParser.class.getClassLoader().getResource("data-package.xml").getPath();
		this.xml = new XmlUtil(this.definePath);
		this.contextInitialized();
	}

	/**
	 * Instantiates a new abstract parser.
	 *
	 * @param __definePath the define path
	 * @throws ClassNotFoundException the class not found exception
	 */
	public AbstractParser(String __definePath) throws ClassNotFoundException {
		this.reader = new Reader();
		this.writer = new Writer();
		this.definePath = __definePath;
		this.xml = new XmlUtil(this.definePath);
		this.contextInitialized();
	}

	/**
	 * Instantiates a new abstract parser.
	 *
	 * @param __definePath the define path
	 * @param __bufferSize the buffer size
	 * @throws ClassNotFoundException the class not found exception
	 */
	public AbstractParser(String __definePath, int __bufferSize) throws ClassNotFoundException {
		this.reader = new Reader(__bufferSize);
		this.writer = new Writer(__bufferSize);
		this.definePath = __definePath;
		this.xml = new XmlUtil(this.definePath);
		this.contextInitialized();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see logia.socket.Interface.ParserInterface#applyInputStream(logia.socket.Interface.SocketClientInterface)
	 */
	@Override
	public void applyInputStream(final SocketClientInterface __clientSocket) {
		new Thread(new Runnable() {

			@Override
			public void run() {
				try {
					ReadDataInterface _data = null;
					while (__clientSocket.isConnected()) {
						long _a = System.currentTimeMillis();
						_data = readData(__clientSocket.getInputStream());
						long _b = System.currentTimeMillis();
						AbstractParser.LOGGER.debug("Finish read data after " + (_b - _a) / 1000 + "s");
						if (_data != null) {
							if (__clientSocket.isWaitForReturn()) {
								__clientSocket.setReturned(_data);
								synchronized (__clientSocket) {
									__clientSocket.notify();
								}
							}
							else {
								_data.executeData(__clientSocket);
							}
						}
						else {
							AbstractParser.LOGGER.warn("Not recognize data from inputstream");
						}
					}
				}
				catch (Exception __e) {
					LOGGER.error(__e.getMessage(), __e);
					__clientSocket.disconnect();
				}

			}
		}).start();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see logia.socket.Interface.ParserInterface#applyOutputStream(java.io.OutputStream, logia.socket.Interface.WriteDataInterface, int)
	 */
	@Override
	public void applyOutputStream(OutputStream __outputStream, WriteDataInterface __dataListener, Object __command) throws Exception {
		long _a = System.currentTimeMillis();
		this.writeData(__command, __outputStream, __dataListener);
		long _b = System.currentTimeMillis();
		AbstractParser.LOGGER.debug("Finish send data after " + (_b - _a) / 1000 + "s");
	}

	/**
	 * Context initialized.
	 *
	 * @throws ClassNotFoundException the class not found exception
	 */
	private void contextInitialized() throws ClassNotFoundException {
		NodeList _list = this.xml.getListNode("define", this.xml.getRoot());
		// List all command
		Node _define = _list.item(0);
		this.commandType = this.xml.getAttribute(_define, "type");
		NodeList _listCommand = this.xml.getListNode("define-command", (Element) _define);
		for (int _i = 0; _i < _listCommand.getLength(); _i++) {
			Node _nodeCommand = _listCommand.item(_i);

			// Get command type
			String _valueCommand = this.xml.getAttribute(_nodeCommand, "value");
			String _className = this.xml.getValue(_nodeCommand);
			String _typePackage = this.xml.getAttribute(_nodeCommand, "type");

			if (_typePackage.equalsIgnoreCase("send")) {
				this.mapSendCommand.put(_valueCommand, Class.forName(_className));
			}
			else if (_typePackage.equalsIgnoreCase("receive")) {
				this.mapReceiveCommand.put(_valueCommand, Class.forName(_className));
			}

		}
	}

	/**
	 * Gets the data type.
	 *
	 * @param __type the type
	 * @return the data type
	 */
	protected Byte getDataType(String __type) {
		return AbstractParser.DICTIONARY.get(__type.toLowerCase());
	}

	/**
	 * Gets the instance read data.
	 *
	 * @param __idCommand the id command
	 * @return the instance read data
	 */
	protected ReadDataInterface getInstanceReadData(String __idCommand) {
		Class<?> _clazz = this.mapReceiveCommand.get(__idCommand);
		if (_clazz != null) {
			try {
				return (ReadDataInterface) _clazz.newInstance();
			}
			catch (InstantiationException | IllegalAccessException e) {
				AbstractParser.LOGGER.error("Get instance of data error", e);
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
	 * @param __idCommand the id command
	 * @return the instance write data
	 */
	protected WriteDataInterface getInstanceWriteData(String __idCommand) {
		Class<?> _clazz = this.mapSendCommand.get(__idCommand);
		if (_clazz != null) {
			try {
				return (WriteDataInterface) _clazz.newInstance();
			}
			catch (InstantiationException | IllegalAccessException e) {
				AbstractParser.LOGGER.error("Get instance of data error", e);
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
	 * @param __inputstream the inputstream
	 * @return the read data interface
	 * @throws Exception the exception
	 */
	protected abstract ReadDataInterface readData(InputStream __inputstream) throws Exception;

	/**
	 * Read data by type.
	 *
	 * @param __typeData the type data
	 * @param __inputstream the inputstream
	 * @return the object
	 * @throws IOException Signals that an I/O exception has occurred.
	 */
	protected Object readDataByType(String __typeData, InputStream __inputstream) throws IOException {
		Object _value = null;
		Byte _objType = this.getDataType(__typeData);
		if (_objType == null) {
			_objType = Byte.MIN_VALUE;
		}
		switch (_objType) {
			case TYPE_BYTE:
				_value = this.reader.readByte(__inputstream);
				break;

			case TYPE_BYTE_ARRAY:
				_value = this.reader.readByteArray(__inputstream);
				break;

			case TYPE_DOUBLE:
				_value = this.reader.readDouble(__inputstream);
				break;

			case TYPE_FLOAT:
				_value = this.reader.readFloat(__inputstream);
				break;

			case TYPE_FILE:
				_value = this.reader.readFile(__inputstream);
				break;

			case TYPE_INTERGER:
				_value = this.reader.readInt(__inputstream);
				break;

			case TYPE_JSON:
				_value = this.reader.readJson(__inputstream);
				break;

			case TYPE_LONG:
				_value = this.reader.readLong(__inputstream);
				break;

			case TYPE_SHORT:
				_value = this.reader.readShort(__inputstream);
				break;

			case TYPE_STRING:
				_value = this.reader.readString(__inputstream);
				break;

			case TYPE_LIST:
				_value = new ArrayList<>();
				break;

			default:
				try {
					_value = Class.forName(__typeData).newInstance();
					// _value = this.reader.readObject(_value.getClass(), __inputstream); // OLD

					Field[] _fields = _value.getClass().getDeclaredFields();
					for (Field _field : _fields) {
						_field.setAccessible(true);
						String _typeData = _field.getType().getCanonicalName();
						_field.set(_value, this.readDataByType(_typeData, __inputstream));
					}
				}
				catch (Exception e) {
					AbstractParser.LOGGER.error("Read data element error", e);
				}
				break;
		}
		return _value;
	}

	/**
	 * Sets the value of.
	 *
	 * @param __clazz the clazz
	 * @param __lookingForValue the looking for value
	 * @param __value the value
	 * @throws Exception the exception
	 */
	protected void setValueOf(Object __clazz, String __lookingForValue, Object __value) throws Exception {
		Field _field = __clazz.getClass().getDeclaredField(__lookingForValue);
		_field.setAccessible(true);
		_field.set(__clazz, __value);
	}

	/**
	 * Write data.
	 *
	 * @param __command the command
	 * @param __out the out
	 * @param __data the data
	 * @throws Exception the exception
	 */
	protected abstract void writeData(Object __command, OutputStream __out, WriteDataInterface __data) throws Exception;

	/**
	 * Write data by type.
	 *
	 * @param __typeData the type data
	 * @param __nameData the name data
	 * @param __out the out
	 * @param __data the data
	 * @return the string
	 * @throws Exception the exception
	 */
	protected String writeDataByType(String __typeData, String __nameData, OutputStream __out, Object __data) throws Exception {
		Byte _objType = this.getDataType(__typeData);
		if (_objType == null) {
			_objType = Byte.MIN_VALUE;
		}
		Field _field;
		String _returnValue = "";
		switch (_objType) {

			case TYPE_BYTE:
				if (__nameData.equals("")) {
					this.writer.writeByte(__out, (byte) __data);
					_returnValue = __data.toString();
				}
				else {
					_field = __data.getClass().getDeclaredField(__nameData);
					_field.setAccessible(true);
					this.writer.writeByte(__out, (byte) _field.get(__data));
					_returnValue = _field.get(__data).toString();
				}
				break;

			case TYPE_BYTE_ARRAY:
				if (__nameData.equals("")) {
					this.writer.writeByteArray(__out, (byte[]) __data);
					_returnValue = __data.toString();
				}
				else {
					_field = __data.getClass().getDeclaredField(__nameData);
					_field.setAccessible(true);
					this.writer.writeByteArray(__out, (byte[]) _field.get(__data));
					_returnValue = _field.get(__data).toString();
				}
				break;

			case TYPE_DOUBLE:
				if (__nameData.equals("")) {
					this.writer.writeDouble(__out, (double) __data);
					_returnValue = __data.toString();
				}
				else {
					_field = __data.getClass().getDeclaredField(__nameData);
					_field.setAccessible(true);
					this.writer.writeDouble(__out, (double) _field.get(__data));
					_returnValue = _field.get(__data).toString();
				}
				break;

			case TYPE_FLOAT:
				if (__nameData.equals("")) {
					this.writer.writeFloat(__out, (float) __data);
					_returnValue = __data.toString();
				}
				else {
					_field = __data.getClass().getDeclaredField(__nameData);
					_field.setAccessible(true);
					this.writer.writeFloat(__out, (float) _field.get(__data));
					_returnValue = _field.get(__data).toString();
				}
				break;

			case TYPE_FILE:
				if (__nameData.equals("")) {
					this.writer.writeFile(__out, (File) __data);
					_returnValue = __data.toString();
				}
				else {
					_field = __data.getClass().getDeclaredField(__nameData);
					_field.setAccessible(true);
					this.writer.writeFile(__out, (File) _field.get(__data));
					_returnValue = _field.get(__data).toString();
				}
				break;

			case TYPE_INTERGER:
				if (__nameData.equals("")) {
					this.writer.writeInt(__out, (int) __data);
					_returnValue = __data.toString();
				}
				else {
					_field = __data.getClass().getDeclaredField(__nameData);
					_field.setAccessible(true);
					this.writer.writeInt(__out, (int) _field.get(__data));
					_returnValue = _field.get(__data).toString();
				}
				break;

			case TYPE_JSON:
				if (__nameData.equals("")) {
					this.writer.writeJson(__out, (JsonObject) __data);
					_returnValue = __data.toString();
				}
				else {
					_field = __data.getClass().getDeclaredField(__nameData);
					_field.setAccessible(true);
					this.writer.writeJson(__out, (JsonObject) _field.get(__data));
					_returnValue = _field.get(__data).toString();
				}
				break;

			case TYPE_LONG:
				if (__nameData.equals("")) {
					this.writer.writeLong(__out, (long) __data);
					_returnValue = __data.toString();
				}
				else {
					_field = __data.getClass().getDeclaredField(__nameData);
					_field.setAccessible(true);
					this.writer.writeLong(__out, (long) _field.get(__data));
					_returnValue = _field.get(__data).toString();
				}
				break;

			case TYPE_SHORT:
				if (__nameData.equals("")) {
					this.writer.writeShort(__out, (short) __data);
					_returnValue = __data.toString();
				}
				else {
					_field = __data.getClass().getDeclaredField(__nameData);
					_field.setAccessible(true);
					this.writer.writeShort(__out, (short) _field.get(__data));
					_returnValue = _field.get(__data).toString();
				}
				break;

			case TYPE_STRING:
				if (__nameData.equals("")) {
					this.writer.writeString(__out, (String) __data);
					_returnValue = __data.toString();
				}
				else {
					_field = __data.getClass().getDeclaredField(__nameData);
					_field.setAccessible(true);
					this.writer.writeString(__out, (String) _field.get(__data));
					_returnValue = _field.get(__data).toString();
				}
				break;

			case TYPE_LIST:
				_field = __data.getClass().getDeclaredField(__nameData);
				_field.setAccessible(true);
				@SuppressWarnings("rawtypes")
				List _list = (List) _field.get(__data);
				this.writer.writeInt(__out, _list.size());
				for (Object _object : _list) {
					String _objectType = _object.getClass().getName();
					this.writeDataByType(_objectType, "", __out, _object);
				}
				break;

			default:
				Field[] _fields = __data.getClass().getDeclaredFields();
				for (Field _eachfield : _fields) {
					String _typeData = _eachfield.getType().getCanonicalName();
					String _nameData = _eachfield.getName();
					// _eachfield.get(__data);
					this.writeDataByType(_typeData, _nameData, __out, __data);
				}
				break;
		}
		return _returnValue;
	}

}
