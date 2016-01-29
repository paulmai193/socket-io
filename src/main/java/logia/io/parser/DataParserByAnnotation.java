package logia.io.parser;

import java.io.InputStream;
import java.io.OutputStream;
import java.lang.reflect.Field;
import java.lang.reflect.ParameterizedType;
import java.net.SocketException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;

import logia.io.annotation.IOCommand;
import logia.io.annotation.IOData;
import logia.io.exception.ReadDataException;
import logia.socket.Interface.ReadDataInterface;
import logia.socket.Interface.WriteDataInterface;

/**
 * The Class DataParser. Read data from inputstream and Write data to outputstream base on data structure defined in {@code @IOData annotation}
 * 
 * @author Paul Mai
 */
public class DataParserByAnnotation extends AbstractParser {

	/**
	 * Instantiates a new data parser by annotation.
	 *
	 * @throws ClassNotFoundException the class not found exception
	 */
	public DataParserByAnnotation() throws ClassNotFoundException {
		super();
	}

	/**
	 * Instantiates a new data parser by annotation.
	 *
	 * @param __bufferSize the buffer size
	 * @throws ClassNotFoundException the class not found exception
	 */
	public DataParserByAnnotation(int __bufferSize) throws ClassNotFoundException {
		super(__bufferSize);
	}

	/**
	 * Instantiates a new data parser by annotation.
	 *
	 * @param __definePath the define path
	 * @throws ClassNotFoundException the class not found exception
	 */
	public DataParserByAnnotation(String __definePath) throws ClassNotFoundException {
		super(__definePath);
	}

	/**
	 * Instantiates a new data parser by annotation.
	 *
	 * @param __definePath the define path
	 * @param __bufferSize the buffer size
	 * @throws ClassNotFoundException the class not found exception
	 */
	public DataParserByAnnotation(String __definePath, int __bufferSize) throws ClassNotFoundException {
		super(__definePath, __bufferSize);
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

	/*
	 * (non-Javadoc)
	 * 
	 * @see logia.io.parser.AbstractParser#readData(java.io.InputStream)
	 */
	@SuppressWarnings({ "unchecked", "rawtypes" })
	@Override
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
								DataParserByAnnotation.LOGGER.error("Try to set value " + _fieldData + " to field " + _field.getName() + " in "
								        + _typeData + " type.", e);
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
				else {
					throw new ReadDataException("Commands document is empty");
				}
			}
			else {
				throw new ReadDataException("Not recogize data from command " + _command.toString());
			}
			// return data;
		}
		else {
			throw new SocketException();
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see logia.io.parser.AbstractParser#writeData(java.lang.Object, java.io.OutputStream, logia.socket.Interface.WriteDataInterface)
	 */
	@Override
	protected void writeData(Object __command, OutputStream __out, WriteDataInterface __data) throws Exception {
		WriteDataInterface _writedata = this.getInstanceWriteData(__command.toString());
		if (__data != null) {
			// Write command first
			this.writeDataByType(this.commandType, "", __out, __command);

			// Write each element
			__data.getClass().cast(_writedata);
			Field[] _fields = __data.getClass().getDeclaredFields();
			this.sortByOrder(_fields);
			for (Field _field : _fields) {
				_field.setAccessible(true);
				IOData _fieldAnnotation = _field.getAnnotation(IOData.class);
				if (_fieldAnnotation != null) {
					String _typeData = _fieldAnnotation.type().toString().toLowerCase();
					String _breakValue = _fieldAnnotation.breakValue();
					String _nameData = _field.getName();
					_field.get(__data);
					String _checkData = this.writeDataByType(_typeData, _nameData, __out, __data);
					if (_breakValue != null && _breakValue.equals(_checkData.toString())) {
						break;
					}
					String _continueValue = _fieldAnnotation.continueValue();
					if (!_continueValue.equals("n/a") && !_continueValue.equals(_checkData.toString())) {
						break;
					}
				}
			}
			__out.flush();
		}
	}
}
