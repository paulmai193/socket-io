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
 * @author Paul Mai
 */
public class DataParserByXML extends AbstractParser {

	/**
	 * Instantiates a new data parser with default define package reader is data-package.xml file.
	 */
	public DataParserByXML() {
		super();
	}

	/**
	 * Instantiates a new data parser by xml.
	 *
	 * @param bufferSize the buffer size
	 */
	public DataParserByXML(int bufferSize) {
		super(bufferSize);
	}

	/**
	 * Instantiates a new abstract parser.
	 *
	 * @param definePath the define path
	 */
	public DataParserByXML(String definePath) {
		super(definePath);
	}

	/**
	 * Instantiates a new data parser by xml.
	 *
	 * @param definePath the define path
	 * @param bufferSize the buffer size
	 */
	public DataParserByXML(String definePath, int bufferSize) {
		super(definePath, bufferSize);
	}

	/**
	 * Read data instance.
	 *
	 * @param xml the xml
	 * @param elementCommand the element command
	 * @param data the data
	 * @param inputstream the inputstream
	 * @throws Exception the exception
	 */
	@SuppressWarnings({ "unchecked", "rawtypes" })
	private void readDataInstance(XmlUtil xml, Element elementCommand, ReadDataInterface data, InputStream inputstream) throws Exception {
		NodeList listData = xml.getListNode("data", elementCommand);
		for (int j = 0; j < listData.getLength(); j++) {
			Node nodeData = listData.item(j);
			String nameData = xml.getAttribute(nodeData, "name");
			String typeData = xml.getAttribute(nodeData, "type");
			String checkData = xml.getAttribute(nodeData, "breakvalue");
			Object fieldData = this.readDataByType(typeData, inputstream);
			if (fieldData instanceof ArrayList) {
				int size = this._reader.readInt(inputstream);
				String elementType = xml.getAttribute(nodeData, "elementtype");
				fieldData = new ArrayList<Object>();
				for (int k = 0; k < size; k++) {
					Object element = this.readDataByType(elementType, inputstream);
					((ArrayList) fieldData).add(element);
				}
			}
			try {
				this.setValueOf(data, nameData, fieldData);
			}
			catch (Exception e) {
				this.LOGGER.error("Try to set value " + fieldData + " to field " + nameData + " in " + typeData + " type.", e);
			}

			if (checkData != null && checkData.toString().equals(fieldData.toString())) {
				break;
			}
		}
	}

	/**
	 * Write data instance.
	 *
	 * @param _xml the _xml
	 * @param elementCommand the element command
	 * @param data the data
	 * @param out the out
	 * @throws Exception the exception
	 */
	private void writeDataInstance(XmlUtil _xml, Element elementCommand, WriteDataInterface data, OutputStream out) throws Exception {
		NodeList listData = _xml.getListNode("data", elementCommand);
		for (int j = 0; j < listData.getLength(); j++) {
			Node nodeData = listData.item(j);
			String nameData = _xml.getAttribute(nodeData, "name");
			String typeData = _xml.getAttribute(nodeData, "type");
			String checkValue = _xml.getAttribute(nodeData, "breakvalue");
			String checkData = this.writeDataByType(typeData, nameData, out, data);
			if (checkValue != null && checkValue.toString().equals(checkData.toString())) {
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
	protected ReadDataInterface readData(InputStream inputstream) throws Exception {
		ReadDataInterface data;

		// Read command
		Object command = this.readDataByType(this._commandType, inputstream);
		if (command != null && !command.toString().equals("-1")) {
			// Get instance which this command
			data = this.getInstanceReadData(command.toString());
			if (data != null) {
				// Read each data
				NodeList listCommand = this._xml.getListNode("command", this._xml.getRoot());
				if (listCommand != null && listCommand.getLength() > 0) {
					for (int i = 0; i < listCommand.getLength(); i++) {
						Node nodeCommand = listCommand.item(i);
						String valueCommand = this._xml.getAttribute(nodeCommand, "value");
						if (valueCommand.equals(command.toString())) {
							this.readDataInstance(this._xml, (Element) nodeCommand, data, inputstream);
							return data;
							// break;
						}
						else {
							continue;
						}
					}
					throw new ReadDataException("Not recogize data from command " + command.toString());
				}
				else {
					throw new ReadDataException("Commands document is empty");
				}
				// return data;
			}
			else {
				throw new ReadDataException("Not recogize data from command " + command.toString());
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
	protected void writeData(Object command, OutputStream out, WriteDataInterface data) throws Exception {
		WriteDataInterface writedata = this.getInstanceWriteData(command.toString());
		if (data != null) {
			// Write command first
			this.LOGGER.debug("Command type: " + this._commandType);
			this.writeDataByType(this._commandType, "", out, command);

			// Write each element
			data.getClass().cast(writedata);
			NodeList listCommand = this._xml.getListNode("command", this._xml.getRoot());
			for (int i = 0; i < listCommand.getLength(); i++) {
				Node nodeCommand = listCommand.item(i);
				String valueCommand = this._xml.getAttribute(nodeCommand, "value");
				if (valueCommand.equals(command.toString())) {
					this.writeDataInstance(this._xml, (Element) nodeCommand, data, out);
					break;
				}
			}
			out.flush();
		}
	}
}
