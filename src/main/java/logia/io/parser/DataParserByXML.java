package logia.io.parser;

import java.io.InputStream;
import java.io.OutputStream;
import java.net.SocketException;
import java.util.ArrayList;

import logia.io.exception.ReadDataException;
import logia.socket.Interface.ReadDataInterface;
import logia.socket.Interface.WriteDataInterface;
import logia.utility.readfile.XmlUtil;

import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

/**
 * The Class DataParser. Read data from inputstream and Write data to outputstream base on data structure define by XML DataPackage file
 * 
 * @deprecated since version 0.0.8
 * 
 * @author Paul Mai
 */
@Deprecated
public class DataParserByXML extends AbstractParser {

	/**
	 * Instantiates a new data parser with default define package reader is data-package.xml file.
	 *
	 * @throws ClassNotFoundException the class not found exception
	 */
	public DataParserByXML() throws ClassNotFoundException {
		super();
	}

	/**
	 * Instantiates a new data parser by xml.
	 *
	 * @param __bufferSize the buffer size
	 * @throws ClassNotFoundException the class not found exception
	 */
	public DataParserByXML(int __bufferSize) throws ClassNotFoundException {
		super(__bufferSize);
	}

	/**
	 * Instantiates a new abstract parser.
	 *
	 * @param __definePath the define path
	 * @throws ClassNotFoundException the class not found exception
	 */
	public DataParserByXML(String __definePath) throws ClassNotFoundException {
		super(__definePath);
	}

	/**
	 * Instantiates a new data parser by xml.
	 *
	 * @param __definePath the define path
	 * @param __bufferSize the buffer size
	 * @throws ClassNotFoundException the class not found exception
	 */
	public DataParserByXML(String __definePath, int __bufferSize) throws ClassNotFoundException {
		super(__definePath, __bufferSize);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see logia.socket.Interface.ParserInterface#applyOutputStream(java.io.OutputStream, logia.socket.Interface.WriteDataInterface)
	 */
	@Override
	public void applyOutputStream(OutputStream __outputStream, WriteDataInterface __writeData) throws Exception {
		// TODO Auto-generated method stub

	}

	/**
	 * Read data instance.
	 *
	 * @param __xml the xml
	 * @param __elementCommand the element command
	 * @param __data the data
	 * @param __inputstream the inputstream
	 * @throws Exception the exception
	 */
	@SuppressWarnings({ "unchecked", "rawtypes" })
	private void readDataInstance(XmlUtil __xml, Element __elementCommand, ReadDataInterface __data, InputStream __inputstream) throws Exception {
		NodeList _listData = __xml.getListNode("data", __elementCommand);
		for (int _j = 0; _j < _listData.getLength(); _j++) {
			Node _nodeData = _listData.item(_j);
			String _nameData = __xml.getAttribute(_nodeData, "name");
			String _typeData = __xml.getAttribute(_nodeData, "type");
			String _breakData = __xml.getAttribute(_nodeData, "breakvalue");
			String _continueData = __xml.getAttribute(_nodeData, "continuevalue");
			Object _fieldData = this.readDataByType(_typeData, __inputstream);
			if (_fieldData instanceof ArrayList) {
				int _size = this.reader.readInt(__inputstream);
				String _elementType = __xml.getAttribute(_nodeData, "elementtype");
				_fieldData = new ArrayList<Object>();
				for (int _k = 0; _k < _size; _k++) {
					Object _element = this.readDataByType(_elementType, __inputstream);
					((ArrayList) _fieldData).add(_element);
				}
			}
			try {
				this.setValueOf(__data, _nameData, _fieldData);
			}
			catch (Exception _e) {
				AbstractParser.LOGGER.error("Try to set value " + _fieldData + " to field " + _nameData + " in " + _typeData + " type.", _e);
			}

			if (_breakData != null && _breakData.equals(_fieldData.toString())) {
				break;
			}

			if (_continueData != null && !_continueData.equals("n/a") && !_continueData.equals(_fieldData.toString())) {
				break;
			}
		}
	}

	/**
	 * Write data instance.
	 *
	 * @param __xml the _xml
	 * @param __elementCommand the element command
	 * @param __data the data
	 * @param __out the out
	 * @throws Exception the exception
	 */
	private void writeDataInstance(XmlUtil __xml, Element __elementCommand, WriteDataInterface __data, OutputStream __out) throws Exception {
		NodeList _listData = __xml.getListNode("data", __elementCommand);
		for (int _j = 0; _j < _listData.getLength(); _j++) {
			Node _nodeData = _listData.item(_j);
			String _nameData = __xml.getAttribute(_nodeData, "name");
			String _typeData = __xml.getAttribute(_nodeData, "type");
			String _checkData = this.writeDataByType(_typeData, _nameData, __out, __data);
			String _breakValue = __xml.getAttribute(_nodeData, "breakvalue");
			if (_breakValue != null && _breakValue.equals(_checkData.toString())) {
				break;
			}
			String _continueValue = __xml.getAttribute(_nodeData, "continuevalue");
			if (_continueValue != null && !_continueValue.equals("n/a") && !_continueValue.equals(_checkData)) {
				break;
			}
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see logia.io.parser.AbstractParser#readData(java.io.InputStream)
	 */
	@Override
	protected ReadDataInterface readData(InputStream __inputstream) throws Exception {
		ReadDataInterface _data;

		// Read command
		Object _command = this.readDataByType(this.commandType, __inputstream);
		if (_command != null && !_command.toString().equals("-1")) {
			// Get instance which this command
			_data = this.getInstanceReadData(_command.toString());
			if (_data != null) {
				// Read each data
				NodeList _listCommand = this.xml.getListNode("command", this.xml.getRoot());
				if (_listCommand != null && _listCommand.getLength() > 0) {
					for (int _i = 0; _i < _listCommand.getLength(); _i++) {
						Node _nodeCommand = _listCommand.item(_i);
						String _valueCommand = this.xml.getAttribute(_nodeCommand, "value");
						if (_valueCommand.equals(_command.toString())) {
							this.readDataInstance(this.xml, (Element) _nodeCommand, _data, __inputstream);
							return _data;
						}
						else {
							continue;
						}
					}
					throw new ReadDataException("Not recogize data from command " + _command.toString());
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
			AbstractParser.LOGGER.debug("Command type: " + this.commandType);
			this.writeDataByType(this.commandType, "", __out, __command);

			// Write each element
			__data.getClass().cast(_writedata);
			NodeList _listCommand = this.xml.getListNode("command", this.xml.getRoot());
			for (int _i = 0; _i < _listCommand.getLength(); _i++) {
				Node _nodeCommand = _listCommand.item(_i);
				String _valueCommand = this.xml.getAttribute(_nodeCommand, "value");
				if (_valueCommand.equals(__command.toString())) {
					this.writeDataInstance(this.xml, (Element) _nodeCommand, __data, __out);
					break;
				}
			}
			__out.flush();
		}
	}
}
