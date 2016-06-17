package logia.io.parser;

import java.io.EOFException;
import java.io.File;
import java.io.IOException;
import java.lang.reflect.Field;
import java.lang.reflect.ParameterizedType;
import java.net.SocketTimeoutException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.ClassUtils;
import org.apache.log4j.Logger;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import com.google.gson.JsonObject;

import logia.io.annotation.IOCommand;
import logia.io.annotation.IOCondition;
import logia.io.annotation.IOData;
import logia.io.annotation.type.ConditionActionType;
import logia.io.annotation.type.ConditionType;
import logia.io.exception.ReadDataException;
import logia.io.util.Reader;
import logia.io.util.Writer;
import logia.socket.Interface.ParserInterface;
import logia.socket.Interface.ReadDataInterface;
import logia.socket.Interface.SocketClientInterface;
import logia.socket.Interface.WriteDataInterface;
import logia.utility.readfile.XmlUtil;

/**
 *
 * @author Paul Mai
 */
public class DataBinaryParser implements ParserInterface {

	/** The Constant DICTIONARY. */
	protected static final Map<String, Byte> DICTIONARY        = new HashMap<String, Byte>();

	/** The Constant LOGGER. */
	protected static final Logger            LOGGER            = Logger.getLogger(DataBinaryParser.class);

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
		DataBinaryParser.DICTIONARY.put("byte", DataBinaryParser.TYPE_BYTE);
		DataBinaryParser.DICTIONARY.put("java.lang.byte", DataBinaryParser.TYPE_BYTE);

		DataBinaryParser.DICTIONARY.put("byte[]", DataBinaryParser.TYPE_BYTE_ARRAY);
		DataBinaryParser.DICTIONARY.put("bytearray", DataBinaryParser.TYPE_BYTE_ARRAY);
		DataBinaryParser.DICTIONARY.put("byte_array", DataBinaryParser.TYPE_BYTE_ARRAY);

		DataBinaryParser.DICTIONARY.put("double", DataBinaryParser.TYPE_DOUBLE);
		DataBinaryParser.DICTIONARY.put("java.lang.double", DataBinaryParser.TYPE_DOUBLE);

		DataBinaryParser.DICTIONARY.put("float", DataBinaryParser.TYPE_FLOAT);
		DataBinaryParser.DICTIONARY.put("java.lang.float", DataBinaryParser.TYPE_FLOAT);

		DataBinaryParser.DICTIONARY.put("java.io.file", DataBinaryParser.TYPE_FILE);
		DataBinaryParser.DICTIONARY.put("file", DataBinaryParser.TYPE_FILE);

		DataBinaryParser.DICTIONARY.put("int", DataBinaryParser.TYPE_INTERGER);
		DataBinaryParser.DICTIONARY.put("integer", DataBinaryParser.TYPE_INTERGER);
		DataBinaryParser.DICTIONARY.put("java.lang.integer", DataBinaryParser.TYPE_INTERGER);

		DataBinaryParser.DICTIONARY.put("json", DataBinaryParser.TYPE_JSON);
		DataBinaryParser.DICTIONARY.put("jsonobject", DataBinaryParser.TYPE_JSON);

		DataBinaryParser.DICTIONARY.put("long", DataBinaryParser.TYPE_LONG);
		DataBinaryParser.DICTIONARY.put("java.lang.long", DataBinaryParser.TYPE_LONG);

		DataBinaryParser.DICTIONARY.put("arraylist", DataBinaryParser.TYPE_LIST);
		DataBinaryParser.DICTIONARY.put("list", DataBinaryParser.TYPE_LIST);

		DataBinaryParser.DICTIONARY.put("short", DataBinaryParser.TYPE_SHORT);
		DataBinaryParser.DICTIONARY.put("java.lang.short", DataBinaryParser.TYPE_SHORT);

		DataBinaryParser.DICTIONARY.put("string", DataBinaryParser.TYPE_STRING);
		DataBinaryParser.DICTIONARY.put("java.lang.string", DataBinaryParser.TYPE_STRING);
	}

	/** The command type. */
	protected String                         commandType;

	//	/** The path to xml define data parser file . */
	//	protected String                         definePath;

	/** The map receive command. */
	protected Map<String, Class<?>>          mapReceiveCommand = new HashMap<String, Class<?>>();

	/** The map send command. */
	protected Map<String, Class<?>>          mapSendCommand    = new HashMap<String, Class<?>>();

	/** The reader. */
	protected Reader                         reader;

	/** The writer. */
	protected Writer                         writer;


	/** The xml. */
	protected XmlUtil                        xml;

	/**
	 * Instantiates a new data parser by annotation.
	 *
	 * @throws ClassNotFoundException the class not found exception
	 */
	public DataBinaryParser() throws ClassNotFoundException {
		this(10*1024);

	}

	/**
	 * Instantiates a new data parser by annotation.
	 *
	 * @param __bufferSize the __buffer size
	 * @throws ClassNotFoundException the class not found exception
	 */
	public DataBinaryParser(int __bufferSize) throws ClassNotFoundException {
		this(DataBinaryParser.class.getClassLoader().getResource("data-package.xml").getPath(), __bufferSize);
	}

	/**
	 * Instantiates a new data parser by annotation.
	 *
	 * @param __definePath the __define path
	 * @throws ClassNotFoundException the class not found exception
	 */
	public DataBinaryParser(String __definePath) throws ClassNotFoundException {
		this(__definePath, 10*1024);
	}

	/**
	 * Instantiates a new data parser by annotation.
	 *
	 * @param __definePath the __define path
	 * @param __bufferSize the __buffer size
	 * @throws ClassNotFoundException the class not found exception
	 */
	public DataBinaryParser(String __definePath, int __bufferSize) throws ClassNotFoundException {
		this.reader = new Reader(__bufferSize);
		this.writer = new Writer(__bufferSize);
		this.xml = new XmlUtil(__definePath);
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
				DataBinaryParser.this.reader.setDataInputStream(__clientSocket.getInputStream());
				ReadDataInterface _data = null;
				while (__clientSocket.isConnected()) {

					try {
						long _a = System.currentTimeMillis();
						_data = DataBinaryParser.this.readData();
						long _b = System.currentTimeMillis();
						DataBinaryParser.LOGGER.debug("Finish read data after " + (_b - _a) / 1000 + "s");
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
							DataBinaryParser.LOGGER.warn("Not recognize data from inputstream");
						}
					}
					catch (SocketTimeoutException _e) {
						if (__clientSocket.getTimeoutListener() != null) {
							__clientSocket.getTimeoutListener().solveTimeout();
						}
						else {
							DataBinaryParser.LOGGER.error(_e.getMessage(), _e);
						}
					}
					catch (EOFException __ex) {
						DataBinaryParser.LOGGER.warn("End of reading stream");
						__clientSocket.disconnect();
						break;
					}
					catch (IOException __ex) {
						if (__ex.getMessage().contentEquals("Socket closed")) {
							DataBinaryParser.LOGGER.warn(__ex.getMessage());
						}
						else {
							DataBinaryParser.LOGGER.error(__ex.getMessage(), __ex);
							__clientSocket.disconnect();
						}
						break;
					}
					catch (Exception __ex) {
						DataBinaryParser.LOGGER.error(__ex.getMessage(), __ex);
						break;
					}
					finally {
						//                        try {
						//                            // TODO WARNING
						//                            DataParserByAnnotation.this.reader.close();
						//                        }
						//                        catch (IOException __ex) {
						//                            // Swallow this exeption
						//                        }
					}
				}
			}
		}).start();

	}

	/*
	 * (non-Javadoc)
	 *
	 * @see logia.socket.Interface.ParserInterface#applyOutputStream(java.io.OutputStream, logia.socket.Interface.WriteDataInterface)
	 */
	@Override
	public void applyOutputStream(SocketClientInterface __clientSocket, WriteDataInterface __writeData) throws Exception {
		long _a = System.currentTimeMillis();
		this.writer.setDataOutputStream(__clientSocket.getOutputStream());
		try {
			this.writeDataToStream(__writeData);
			this.writer.flush();
			long _b = System.currentTimeMillis();
			DataBinaryParser.LOGGER.debug("Finish send data after " + (_b - _a) / 1000 + "s");
		}
		finally {
			//            try {
			//                // TODO WARNING
			//                this.writer.close();
			//            }
			//            catch (Exception ___ex) {
			//                // Swallow this exception
			//            }

		}

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
	 * Read data.
	 *
	 * @param __data the __data
	 * @throws IOException Signals that an I/O exception has occurred.
	 * @throws InstantiationException the instantiation exception
	 * @throws IllegalAccessException the illegal access exception
	 * @throws ClassNotFoundException the class not found exception
	 * @throws NoSuchFieldException the no such field exception
	 * @throws SecurityException the security exception
	 */
	@SuppressWarnings({ "unchecked" })
	private void readData(Object __data) throws IOException, InstantiationException, IllegalAccessException,
	ClassNotFoundException, NoSuchFieldException, SecurityException {
		// Iterate fields with IOCommand annotation
		Field[] _fields = __data.getClass().getDeclaredFields();
		this.sortByOrder(_fields);
		for (Field _field : _fields) {
			_field.setAccessible(true);
			IOData _ioData = _field.getAnnotation(IOData.class);
			if (_ioData != null) {

				// Check condition to take action
				IOCondition[] _conditionFields = _ioData.conditions();
				boolean _isBreak = this.checkBreakCondition(_conditionFields, __data);
				if (_isBreak) {
					break;
				}
				else {
					String _typeData = _ioData.type().toString().toLowerCase();
					Object _fieldData = this.readDataByType(_typeData);
					if (_field.getGenericType() instanceof ParameterizedType) {
						ParameterizedType _pt = (ParameterizedType) _field.getGenericType();
						String _elementType = _pt.getActualTypeArguments()[0].toString().replace("class ", "");

						int _size = this.reader.readInt();
						_fieldData = new ArrayList<Object>();
						for (int _k = 1; _k <= _size; _k++) {
							Object _element = this.readDataByType(_elementType);
							((ArrayList<Object>) _fieldData).add(_element);
						}
					}
					try {
						this.setValueOf(__data, _field.getName(), _fieldData);
					}
					catch (Exception e) {
						DataBinaryParser.LOGGER.error("Try to set value " + _fieldData + " to field " + _field.getName() + " in "
								+ _typeData + " type.", e);
					}
				}
			}
		}
	}

	/**
	 * Sort by order.
	 *
	 * @param __fields the __fields
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
	 * @throws IOException Signals that an I/O exception has occurred.
	 * @throws NoSuchFieldException the no such field exception
	 * @throws SecurityException the security exception
	 * @throws IllegalArgumentException the illegal argument exception
	 * @throws IllegalAccessException the illegal access exception
	 */
	private void writeData(Object __data) throws IOException, NoSuchFieldException, SecurityException, IllegalArgumentException, IllegalAccessException {
		if (ClassUtils.isPrimitiveOrWrapper(__data.getClass())) {
			this.writeDataByType(ClassUtils.getShortCanonicalName(__data.getClass()).toLowerCase(), "",  __data);
		}
		else {
			Field[] _fields = __data.getClass().getDeclaredFields();
			this.sortByOrder(_fields);
			for (Field _field : _fields) {
				_field.setAccessible(true);
				IOData _ioData = _field.getAnnotation(IOData.class);
				if (_ioData != null) {

					// Check condition to skip write this data and move to next one
					IOCondition[] _conditionFields = _ioData.conditions();
					boolean _isBreak = this.checkBreakCondition(_conditionFields, __data);
					if (_isBreak) {
						break;
					}
					else {
						String _typeData = _ioData.type().toString().toLowerCase();
						String _nameData = _field.getName();
						Object _valueData = _field.get(__data);
						this.writeDataByType(_typeData, _nameData,  _valueData);
					}
				}
			}
		}
	}

	/**
	 * Gets the data type.
	 *
	 * @param __type the __type
	 * @return the data type
	 */
	protected Byte getDataType(String __type) {
		return DataBinaryParser.DICTIONARY.get(__type.toLowerCase());
	}

	/**
	 * Gets the instance read data.
	 *
	 * @param __idCommand the __id command
	 * @return the instance read data
	 */
	protected ReadDataInterface getInstanceReadData(String __idCommand) {
		Class<?> _clazz = this.mapReceiveCommand.get(__idCommand);
		if (_clazz != null) {
			try {
				return (ReadDataInterface) _clazz.newInstance();
			}
			catch (InstantiationException | IllegalAccessException e) {
				DataBinaryParser.LOGGER.error("Get instance of data error", e);
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
	 * @param __idCommand the __id command
	 * @return the instance write data
	 */
	protected Object getInstanceWriteData(String __idCommand) {
		Class<?> _clazz = this.mapSendCommand.get(__idCommand);
		if (_clazz != null) {
			try {
				return _clazz.newInstance();
			}
			catch (InstantiationException | IllegalAccessException e) {
				DataBinaryParser.LOGGER.error("Get instance of data error", e);
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
	 * @return the read data interface
	 * @throws Exception the exception
	 */
	protected ReadDataInterface readData() throws Exception {
		ReadDataInterface _data;

		// Read command first
		Object _command = this.readDataByType(this.commandType);
		if (_command != null) {

			// Get instance associate with this command
			_data = this.getInstanceReadData(_command.toString());
			if (_data != null) {
				IOCommand _clazzAnnotation = _data.getClass().getAnnotation(IOCommand.class);
				if (_clazzAnnotation != null) {
					this.readData(_data);
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
			throw new ReadDataException("Read command error!");
		}
	}

	/**
	 * Check break condition.
	 *
	 * @param __conditionFields the __condition fields
	 * @param __data the __data
	 * @return true, if successful
	 * @throws NoSuchFieldException the no such field exception
	 * @throws SecurityException the security exception
	 * @throws IllegalArgumentException the illegal argument exception
	 * @throws IllegalAccessException the illegal access exception
	 */
	private boolean checkBreakCondition(IOCondition[] __conditionFields, Object __data) throws NoSuchFieldException, SecurityException, IllegalArgumentException, IllegalAccessException {
		boolean _isBreak = false;
		for (IOCondition _ioCondition : __conditionFields) {
			if (_isBreak) {
				break;
			}
			ConditionActionType _conditionAction = _ioCondition.action();
			ConditionType _conditionType = _ioCondition.type();
			Field _conditionField = __data.getClass().getDeclaredField(_ioCondition.field());
			_conditionField.setAccessible(true);
			String _conditionValue = _ioCondition.value();
			Object _compareValue = _conditionField.get(__data);
			switch (_conditionType) {
				case DIFFERENT:
					switch (_conditionAction) {
						case BREAK:
							if (_compareValue == null || !_compareValue.toString().equals(_conditionValue)) {
								// Match condtion
								_isBreak = true;
								break;
							}
							else {
								_isBreak = false;
							}
							break;

						default:
							if (_compareValue == null || !_compareValue.toString().equals(_conditionValue)) {
								// Match condtion
								_isBreak = false;
							}
							else {
								_isBreak = true;
								break;
							}
							break;
					}

					break;

				case EQUAL:
					switch (_conditionAction) {
						case BREAK:
							if (_compareValue != null && _compareValue.toString().equals(_conditionValue)) {
								// Match condtion
								_isBreak = true;
								break;
							}
							else {
								_isBreak = false;
							}
							break;

						default:
							if (_compareValue != null && _compareValue.toString().equals(_conditionValue)) {
								// Match condtion
								_isBreak = false;
							}
							else {
								_isBreak = true;
								break;
							}
							break;
					}

					break;

				case GREATER:
					switch (_conditionAction) {
						case BREAK:
							if (_compareValue != null && _compareValue.toString().compareTo(_conditionValue) > 0) {
								// Match condtion
								_isBreak = true;
								break;
							}
							else {
								_isBreak=false;
							}
							break;

						default:
							if (_compareValue != null && _compareValue.toString().compareTo(_conditionValue) > 0) {
								// Match condtion
								_isBreak = false;
							}
							else {
								_isBreak=true;
							}
							break;
					}

					break;

				case GREATER_OR_EQUAL:
					switch (_conditionAction) {
						case BREAK:
							if (_compareValue != null && _compareValue.toString().compareTo(_conditionValue) >= 0) {
								// Match condtion
								_isBreak=true;
							}
							else {
								_isBreak=false;
							}
							break;

						default:
							if (_compareValue != null && _compareValue.toString().compareTo(_conditionValue) >= 0) {
								// Match condtion
								_isBreak=false;
							}
							else {
								_isBreak=true;
							}
							break;
					}

					break;

				case SMALLER:
					switch (_conditionAction) {
						case BREAK:
							if (_compareValue != null && _compareValue.toString().compareTo(_conditionValue) < 0) {
								// Match condtion
								_isBreak=true;
							}
							else {
								_isBreak=false;
							}
							break;

						default:
							if (_compareValue != null && _compareValue.toString().compareTo(_conditionValue) < 0) {
								// Match condtion
								_isBreak=false;
							}
							else {
								_isBreak=true;
							}
							break;
					}

					break;

				default: // SMALLER OR EQUAL
					switch (_conditionAction) {
						case BREAK:
							if (_compareValue != null && _compareValue.toString().compareTo(_conditionValue) <= 0) {
								// Match condtion
								_isBreak=true;
							}
							else {
								_isBreak=false;
							}
							break;

						default:
							if (_compareValue != null && _compareValue.toString().compareTo(_conditionValue) <= 0) {
								// Match condtion
								_isBreak=false;
							}
							else {
								_isBreak=true;
							}
							break;
					}

					break;
			}
		}
		return _isBreak;
	}

	/**
	 * Read data by type.
	 *
	 * @param __typeData the __type data
	 * @return the object
	 * @throws IOException Signals that an I/O exception has occurred.
	 * @throws InstantiationException the instantiation exception
	 * @throws IllegalAccessException the illegal access exception
	 * @throws ClassNotFoundException the class not found exception
	 * @throws NoSuchFieldException the no such field exception
	 * @throws SecurityException the security exception
	 */
	protected Object readDataByType(String __typeData) throws IOException, InstantiationException, IllegalAccessException, ClassNotFoundException, NoSuchFieldException, SecurityException  {
		Object _value = null;
		Byte _objType = this.getDataType(__typeData);
		if (_objType == null) {
			_objType = Byte.MIN_VALUE;
		}
		switch (_objType) {
			case TYPE_BYTE:
				_value = this.reader.readByte();
				break;

			case TYPE_BYTE_ARRAY:
				_value = this.reader.readByteArray();
				break;

			case TYPE_DOUBLE:
				_value = this.reader.readDouble();
				break;

			case TYPE_FLOAT:
				_value = this.reader.readFloat();
				break;

			case TYPE_FILE:
				_value = this.reader.readFile();
				break;

			case TYPE_INTERGER:
				_value = this.reader.readInt();
				break;

			case TYPE_JSON:
				_value = this.reader.readJson();
				break;

			case TYPE_LONG:
				_value = this.reader.readLong();
				break;

			case TYPE_SHORT:
				_value = this.reader.readShort();
				break;

			case TYPE_STRING:
				_value = this.reader.readString();
				break;

			case TYPE_LIST:
				_value = new ArrayList<>();
				break;

			default:
				_value = Class.forName(__typeData).newInstance();
				this.readData(_value);
				break;
		}
		return _value;
	}

	/**
	 * Sets the value of.
	 *
	 * @param __clazz the __clazz
	 * @param __lookingForValue the __looking for value
	 * @param __value the __value
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
	 * @param __data the __data
	 * @throws Exception the exception
	 */
	protected void writeDataToStream(WriteDataInterface __data) throws Exception {
		// Get annotation of this instance
		IOCommand _ioCommand = __data.getClass().getAnnotation(IOCommand.class);
		if (_ioCommand != null) {
			// Get command value
			int _commandValue = _ioCommand.value();

			// Write command first
			this.writeDataByType("integer", "",  _commandValue);

			// Write each element
			this.writeData(__data);
		}
	}

	/**
	 * Write data by type.
	 *
	 * @param __typeData the __type data
	 * @param __fieldName the __field name
	 * @param __data the __data
	 * @return the string
	 * @throws IOException Signals that an I/O exception has occurred.
	 * @throws NoSuchFieldException the no such field exception
	 * @throws SecurityException the security exception
	 * @throws IllegalArgumentException the illegal argument exception
	 * @throws IllegalAccessException the illegal access exception
	 */
	protected String writeDataByType(String __typeData, String __fieldName, Object __data) throws IOException, NoSuchFieldException, SecurityException, IllegalArgumentException, IllegalAccessException {
		Byte _objType = this.getDataType(__typeData);
		if (_objType == null) {
			_objType = Byte.MIN_VALUE;
		}
		String _returnValue = "";
		if (__data != null) {
			switch (_objType) {

				case TYPE_BYTE:
					this.writer.writeByte((byte) __data);
					_returnValue = __data.toString();
					break;

				case TYPE_BYTE_ARRAY:
					this.writer.writeByteArray((byte[]) __data);
					_returnValue = __data.toString();
					break;

				case TYPE_DOUBLE:
					this.writer.writeDouble((double) __data);
					_returnValue = __data.toString();
					break;

				case TYPE_FLOAT:
					this.writer.writeFloat((float) __data);
					_returnValue = __data.toString();
					break;

				case TYPE_FILE:
					this.writer.writeFile((File) __data);
					_returnValue = __data.toString();
					break;

				case TYPE_INTERGER:
					this.writer.writeInt((int) __data);
					_returnValue = __data.toString();
					break;

				case TYPE_JSON:
					this.writer.writeJson((JsonObject) __data);
					_returnValue = __data.toString();
					break;

				case TYPE_LONG:
					this.writer.writeLong((long) __data);
					_returnValue = __data.toString();
					break;

				case TYPE_SHORT:
					this.writer.writeShort((short) __data);
					_returnValue = __data.toString();
					break;

				case TYPE_STRING:
					this.writer.writeString((String) __data);
					_returnValue = __data.toString();
					break;

				case TYPE_LIST:
					List<?> _list = (List<?>) __data;
					if (_list != null) {
						this.writer.writeInt(_list.size());
						for (Object _object : _list) {
							this.writeData(_object);
						}
					}
					break;

				default:
					this.writeData(__data);
					break;
			}
		}

		return _returnValue;
	}

	@Override
	public void close() throws Exception {
		// Close reader
		try {
			this.reader.close();
		}
		catch (IOException __ex) {
			// Swallow this exeption
		}

		// CLose writer
		try {
			this.writer.close();
		}
		catch (Exception ___ex) {
			// Swallow this exception
		}
	}
}
