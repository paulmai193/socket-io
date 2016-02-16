package logia.io.parser;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.lang.reflect.Field;
import java.lang.reflect.ParameterizedType;
import java.net.SocketException;
import java.net.SocketTimeoutException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import logia.io.annotation.IOCommand;
import logia.io.annotation.IOData;
import logia.io.annotation.type.ConditionType;
import logia.io.exception.ReadDataException;
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
 * The Class DataParser. Read data from inputstream and Write data to outputstream base on data structure defined in {@code @IOData annotation}
 * 
 * @author Paul Mai
 */
public class DataParserByAnnotation implements ParserInterface {

	/** The Constant DICTIONARY, use to get data type by input string. */
	protected static final Map<String, Byte> DICTIONARY        = new HashMap<String, Byte>();

	/** The logger. */
	protected static final Logger            LOGGER            = Logger.getLogger(DataParserByAnnotation.class);

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
		DataParserByAnnotation.DICTIONARY.put("byte", DataParserByAnnotation.TYPE_BYTE);
		DataParserByAnnotation.DICTIONARY.put("java.lang.byte", DataParserByAnnotation.TYPE_BYTE);

		DataParserByAnnotation.DICTIONARY.put("byte[]", DataParserByAnnotation.TYPE_BYTE_ARRAY);
		DataParserByAnnotation.DICTIONARY.put("bytearray", DataParserByAnnotation.TYPE_BYTE_ARRAY);
		DataParserByAnnotation.DICTIONARY.put("byte_array", DataParserByAnnotation.TYPE_BYTE_ARRAY);

		DataParserByAnnotation.DICTIONARY.put("double", DataParserByAnnotation.TYPE_DOUBLE);
		DataParserByAnnotation.DICTIONARY.put("java.lang.double", DataParserByAnnotation.TYPE_DOUBLE);

		DataParserByAnnotation.DICTIONARY.put("float", DataParserByAnnotation.TYPE_FLOAT);
		DataParserByAnnotation.DICTIONARY.put("java.lang.float", DataParserByAnnotation.TYPE_FLOAT);

		DataParserByAnnotation.DICTIONARY.put("java.io.file", DataParserByAnnotation.TYPE_FILE);
		DataParserByAnnotation.DICTIONARY.put("file", DataParserByAnnotation.TYPE_FILE);

		DataParserByAnnotation.DICTIONARY.put("int", DataParserByAnnotation.TYPE_INTERGER);
		DataParserByAnnotation.DICTIONARY.put("integer", DataParserByAnnotation.TYPE_INTERGER);
		DataParserByAnnotation.DICTIONARY.put("java.lang.integer", DataParserByAnnotation.TYPE_INTERGER);

		DataParserByAnnotation.DICTIONARY.put("json", DataParserByAnnotation.TYPE_JSON);
		DataParserByAnnotation.DICTIONARY.put("jsonobject", DataParserByAnnotation.TYPE_JSON);

		DataParserByAnnotation.DICTIONARY.put("long", DataParserByAnnotation.TYPE_LONG);
		DataParserByAnnotation.DICTIONARY.put("java.lang.long", DataParserByAnnotation.TYPE_LONG);

		DataParserByAnnotation.DICTIONARY.put("arraylist", DataParserByAnnotation.TYPE_LIST);
		DataParserByAnnotation.DICTIONARY.put("list", DataParserByAnnotation.TYPE_LIST);

		DataParserByAnnotation.DICTIONARY.put("short", DataParserByAnnotation.TYPE_SHORT);
		DataParserByAnnotation.DICTIONARY.put("java.lang.short", DataParserByAnnotation.TYPE_SHORT);

		DataParserByAnnotation.DICTIONARY.put("string", DataParserByAnnotation.TYPE_STRING);
		DataParserByAnnotation.DICTIONARY.put("java.lang.string", DataParserByAnnotation.TYPE_STRING);
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
	 * Instantiates a new data parser by annotation.
	 *
	 * @throws ClassNotFoundException the class not found exception
	 */
	public DataParserByAnnotation() throws ClassNotFoundException {
		this.reader = new Reader();
		this.writer = new Writer();
		this.definePath = DataParserByAnnotation.class.getClassLoader().getResource("data-package.xml").getPath();
		this.xml = new XmlUtil(this.definePath);
		this.contextInitialized();
	}

	/**
	 * Instantiates a new data parser by annotation.
	 *
	 * @param __bufferSize the buffer size
	 * @throws ClassNotFoundException the class not found exception
	 */
	public DataParserByAnnotation(int __bufferSize) throws ClassNotFoundException {
		this.reader = new Reader(__bufferSize);
		this.writer = new Writer(__bufferSize);
		this.definePath = DataParserByAnnotation.class.getClassLoader().getResource("data-package.xml").getPath();
		this.xml = new XmlUtil(this.definePath);
		this.contextInitialized();
	}

	/**
	 * Instantiates a new data parser by annotation.
	 *
	 * @param __definePath the define path
	 * @throws ClassNotFoundException the class not found exception
	 */
	public DataParserByAnnotation(String __definePath) throws ClassNotFoundException {
		this.reader = new Reader();
		this.writer = new Writer();
		this.definePath = __definePath;
		this.xml = new XmlUtil(this.definePath);
		this.contextInitialized();
	}

	/**
	 * Instantiates a new data parser by annotation.
	 *
	 * @param __definePath the define path
	 * @param __bufferSize the buffer size
	 * @throws ClassNotFoundException the class not found exception
	 */
	public DataParserByAnnotation(String __definePath, int __bufferSize) throws ClassNotFoundException {
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
	public void applyInputStream(SocketClientInterface __clientSocket) throws SocketTimeoutException, SocketException, IOException, Exception {
		ReadDataInterface _data = null;
		while (__clientSocket.isConnected()) {
			long _a = System.currentTimeMillis();
			_data = this.readData(__clientSocket.getInputStream());
			long _b = System.currentTimeMillis();
			DataParserByAnnotation.LOGGER.debug("Finish read data after " + (_b - _a) / 1000 + "s");
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
				DataParserByAnnotation.LOGGER.warn("Not recognize data from inputstream");
			}
		}
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
		DataParserByAnnotation.LOGGER.debug("Finish send data after " + (_b - _a) / 1000 + "s");
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
		return DataParserByAnnotation.DICTIONARY.get(__type.toLowerCase());
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
				DataParserByAnnotation.LOGGER.error("Get instance of data error", e);
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
				DataParserByAnnotation.LOGGER.error("Get instance of data error", e);
				return null;
			}
		}
		else {
			return null;
		}
	}

	@SuppressWarnings({ "unchecked", "rawtypes" })
	protected ReadDataInterface readData(InputStream __inputstream) throws Exception {
		ReadDataInterface _data;

		// Read command
		Object _command = this.readDataByType(this.commandType, __inputstream);
		if (_command != null && !_command.toString().equals("-1")) {
			// Get instance which this command
			_data = this.getInstanceReadData(_command.toString());
			if (_data != null) {
				IOCommand _clazzAnnotation = _data.getClass().getAnnotation(IOCommand.class);
				if (_clazzAnnotation != null) {
					// Iterate fields with IOCommand annotation
					Field[] _fields = _data.getClass().getDeclaredFields();
					this.sortByOrder(_fields);
					for (Field _field : _fields) {
						_field.setAccessible(true);
						IOData _fieldAnnotation = _field.getAnnotation(IOData.class);
						if (_fieldAnnotation != null) {
							// Check condition to skip read this data and move to next one
							String _conditionField = _fieldAnnotation.conditionField();
							String _conditionValue = _fieldAnnotation.conditionValue();
							if (!_conditionField.equals("n/a") && !_conditionValue.equals("n/a")) {
								Field _checkField = _data.getClass().getDeclaredField(_conditionField);
								_checkField.setAccessible(true);
								Object _checkData = _checkField.get(_data);
								ConditionType _conditionType = _fieldAnnotation.conditionType();
								if (_conditionType.equals(ConditionType.EQUAL) && !_conditionValue.equals(_checkData.toString())) {
									continue;
								}
								else if (_conditionType.equals(ConditionType.DIFFERENT) && _conditionValue.equals(_checkData.toString())) {
									continue;
								}
							}

							String _typeData = _fieldAnnotation.type().toString().toLowerCase();
							Object _fieldData = this.readDataByType(_typeData, __inputstream);
							if (_field.getGenericType() instanceof ParameterizedType) {
								ParameterizedType _pt = (ParameterizedType) _field.getGenericType();
								String _elementType = _pt.getActualTypeArguments()[0].toString().replace("class ", "");

								int _size = this.reader.readInt(__inputstream);
								_fieldData = new ArrayList<Object>();
								for (int _k = 0; _k < _size; _k++) {
									Object _element = this.readDataByType(_elementType, __inputstream);
									((ArrayList) _fieldData).add(_element);
								}
							}
							try {
								this.setValueOf(_data, _field.getName(), _fieldData);
							}
							catch (Exception e) {
								DataParserByAnnotation.LOGGER.error("Try to set value " + _fieldData + " to field " + _field.getName() + " in "
								        + _typeData + " type.", e);
							}

							// Check condition to stop reading
							String _breakData = _fieldAnnotation.breakValue();
							if (_breakData.equals(_fieldData.toString())) {
								break;
							}
							String _continueData = _fieldAnnotation.continueValue();
							if (!_continueData.equals("n/a") && !_continueData.equals(_fieldData.toString())) {
								break;
							}

						}
					}
					return _data;
				}
				else {
					throw new ReadDataException("Commands document is empty");
				}
			}
			else {
				throw new ReadDataException("Not recogize data from command " + _command.toString());
			}
		}
		else {
			throw new SocketException();
		}
	}

	@SuppressWarnings({ "unchecked", "rawtypes" })
	private Object readData(Object _data, InputStream __inputstream) throws IOException, InstantiationException, IllegalAccessException,
	        ClassNotFoundException {
		// Iterate fields with IOCommand annotation
		Field[] _fields = _data.getClass().getDeclaredFields();
		this.sortByOrder(_fields);
		for (Field _field : _fields) {
			_field.setAccessible(true);
			IOData _fieldAnnotation = _field.getAnnotation(IOData.class);
			if (_fieldAnnotation != null) {
				String _typeData = _fieldAnnotation.type().toString().toLowerCase();
				String _breakData = _fieldAnnotation.breakValue();
				String _continueData = _fieldAnnotation.continueValue();
				Object _fieldData = this.readDataByType(_typeData, __inputstream);
				if (_field.getGenericType() instanceof ParameterizedType) {
					ParameterizedType _pt = (ParameterizedType) _field.getGenericType();
					String _elementType = _pt.getActualTypeArguments()[0].toString().replace("class ", "");

					int _size = this.reader.readInt(__inputstream);
					_fieldData = new ArrayList<Object>();
					for (int _k = 0; _k < _size; _k++) {
						Object _element = this.readDataByType(_elementType, __inputstream);
						((ArrayList) _fieldData).add(_element);
					}
				}
				try {
					this.setValueOf(_data, _field.getName(), _fieldData);
				}
				catch (Exception e) {
					DataParserByAnnotation.LOGGER.error("Try to set value " + _fieldData + " to field " + _field.getName() + " in " + _typeData
					        + " type.", e);
				}

				if (_breakData.equals(_fieldData.toString())) {
					break;
				}

				if (!_continueData.equals("n/a") && !_continueData.equals(_fieldData.toString())) {
					break;
				}
			}
		}
		return _data;
	}

	/**
	 * Read data by type.
	 *
	 * @param __typeData the type data
	 * @param __inputstream the inputstream
	 * @return the object
	 * @throws IOException Signals that an I/O exception has occurred.
	 * @throws InstantiationException the instantiation exception
	 * @throws IllegalAccessException the illegal access exception
	 * @throws ClassNotFoundException the class not found exception
	 */
	protected Object readDataByType(String __typeData, InputStream __inputstream) throws IOException, InstantiationException, IllegalAccessException,
	        ClassNotFoundException {
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
				// try {
				// _value = Class.forName(__typeData).newInstance();
				// // _value = this.reader.readObject(_value.getClass(), __inputstream); // OLD
				//
				// Field[] _fields = _value.getClass().getDeclaredFields();
				// this.sortByOrder(_fields);
				// for (Field _field : _fields) {
				// _field.setAccessible(true);
				// IOData _fieldAnnotation = _field.getAnnotation(IOData.class);
				// if (_fieldAnnotation != null) {
				// String _typeData = _field.getType().getCanonicalName();
				// _field.set(_value, this.readDataByType(_typeData, __inputstream));
				// }
				//
				// }
				// }
				// catch (Exception e) {
				// DataParserByAnnotation.LOGGER.error("Read data element error", e);
				// }
				_value = Class.forName(__typeData).newInstance();
				_value = readData(_value, __inputstream);
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
	 * Sort fields by data order.
	 *
	 * @param __fields the fields
	 */
	private void sortByOrder(Field[] __fields) {
		Arrays.sort(__fields, new Comparator<Field>() {

			/*
			 * (non-Javadoc)
			 * 
			 * @see java.util.Comparator#compare(java.lang.Object, java.lang.Object)
			 */
			@Override
			public int compare(Field __o1, Field __o2) {
				IOData _order1 = __o1.getAnnotation(IOData.class);
				IOData _order2 = __o2.getAnnotation(IOData.class);
				if (_order1 != null && _order2 != null) {
					return _order1.order() - _order2.order();
				}
				else {
					return 0;
				}
			}
		});
	}

	/**
	 * Write data.
	 *
	 * @param __data the __data
	 * @param __out the __out
	 * @throws Exception the exception
	 */
	private void writeData(Object __data, OutputStream __out) throws Exception {
		Field[] _fields = __data.getClass().getDeclaredFields();
		this.sortByOrder(_fields);
		for (Field _field : _fields) {
			_field.setAccessible(true);
			IOData _fieldAnnotation = _field.getAnnotation(IOData.class);
			if (_fieldAnnotation != null) {
				// Check condition to skip read this data and move to next one
				String _conditionField = _fieldAnnotation.conditionField();
				String _conditionValue = _fieldAnnotation.conditionValue();
				if (!_conditionField.equals("n/a") && !_conditionValue.equals("n/a")) {
					Field _checkField = __data.getClass().getDeclaredField(_conditionField);
					_checkField.setAccessible(true);
					Object _checkData = _checkField.get(__data);
					ConditionType _conditionType = _fieldAnnotation.conditionType();
					if (_conditionType.equals(ConditionType.EQUAL) && !_conditionValue.equals(_checkData.toString())) {
						continue;
					}
					else if (_conditionType.equals(ConditionType.DIFFERENT) && _conditionValue.equals(_checkData.toString())) {
						continue;
					}
				}

				String _typeData = _fieldAnnotation.type().toString().toLowerCase();
				String _nameData = _field.getName();
				_field.get(__data);
				String _checkData = this.writeDataByType(_typeData, _nameData, __out, __data);

				// Check condition to stop writing
				String _breakValue = _fieldAnnotation.breakValue();
				if (_breakValue != null && _breakValue.equals(_checkData.toString())) {
					break;
				}
				String _continueValue = _fieldAnnotation.continueValue();
				if (!_continueValue.equals("n/a") && !_continueValue.equals(_checkData.toString())) {
					break;
				}
			}
		}
	}

	protected void writeData(Object __command, OutputStream __out, WriteDataInterface __data) throws Exception {
		WriteDataInterface _writedata = this.getInstanceWriteData(__command.toString());
		if (__data != null) {
			// Write command first
			this.writeDataByType(this.commandType, "", __out, __command);

			// Write each element

			__data.getClass().cast(_writedata);
			// Field[] _fields = __data.getClass().getDeclaredFields();
			// this.sortByOrder(_fields);
			// for (Field _field : _fields) {
			// _field.setAccessible(true);
			// IOData _fieldAnnotation = _field.getAnnotation(IOData.class);
			// if (_fieldAnnotation != null) {
			// String _typeData = _fieldAnnotation.type().toString().toLowerCase();
			// String _nameData = _field.getName();
			// _field.get(__data);
			// String _checkData = this.writeDataByType(_typeData, _nameData, __out, __data);
			// String _breakValue = _fieldAnnotation.breakValue();
			// if (_breakValue != null && _breakValue.equals(_checkData.toString())) {
			// break;
			// }
			// String _continueValue = _fieldAnnotation.continueValue();
			// if (!_continueValue.equals("n/a") && !_continueValue.equals(_checkData.toString())) {
			// break;
			// }
			// }
			// }
			writeData(__data, __out);
			__out.flush();
		}
	}

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
				// Field[] _fields = __data.getClass().getDeclaredFields();
				// for (Field _eachfield : _fields) {
				// String _typeData = _eachfield.getType().getCanonicalName();
				// String _nameData = _eachfield.getName();
				// // _eachfield.get(__data);
				// this.writeDataByType(_typeData, _nameData, __out, __data);
				// }

				this.writeData(__data, __out);

				break;
		}
		return _returnValue;
	}
}
