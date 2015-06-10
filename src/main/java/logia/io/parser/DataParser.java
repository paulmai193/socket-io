package logia.io.parser;

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
import logia.socket.Interface.ReadDataInterface;
import logia.socket.Interface.SocketClientInterface;
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
public class DataParser {

	/** The Constant TYPE_BYTE. */
	private static final byte              TYPE_BYTE       = 1;

	/** The Constant TYPE_BYTE_ARRAY. */
	private static final byte              TYPE_BYTE_ARRAY = 2;

	/** The Constant TYPE_DOUBLE. */
	private static final byte              TYPE_DOUBLE     = 4;

	/** The Constant TYPE_FLOAT. */
	private static final byte              TYPE_FLOAT      = 5;

	/** The Constant TYPE_INTERGER. */
	private static final byte              TYPE_INTERGER   = 6;

	/** The Constant TYPE_LONG. */
	private static final byte              TYPE_LONG       = 7;

	/** The Constant TYPE_SHORT. */
	private static final byte              TYPE_SHORT      = 8;

	/** The Constant TYPE_STRING. */
	private static final byte              TYPE_STRING     = 9;

	/** The Constant TYPE_LIST. */
	private static final byte              TYPE_LIST       = 10;

	/** The Constant DICTIONARY, use to get data type by input string. */
	private static final Map<String, Byte> DICTIONARY;
	static {
		DICTIONARY = new HashMap<String, Byte>();

		DataParser.DICTIONARY.put("byte", DataParser.TYPE_BYTE);
		DataParser.DICTIONARY.put("Byte", DataParser.TYPE_BYTE);
		DataParser.DICTIONARY.put("java.lang.Byte", DataParser.TYPE_BYTE);

		DataParser.DICTIONARY.put("byte[]", DataParser.TYPE_BYTE_ARRAY);
		DataParser.DICTIONARY.put("bytearray", DataParser.TYPE_BYTE_ARRAY);
		DataParser.DICTIONARY.put("Bytearray", DataParser.TYPE_BYTE_ARRAY);
		DataParser.DICTIONARY.put("ByteArray", DataParser.TYPE_BYTE_ARRAY);

		DataParser.DICTIONARY.put("double", DataParser.TYPE_DOUBLE);
		DataParser.DICTIONARY.put("Double", DataParser.TYPE_DOUBLE);
		DataParser.DICTIONARY.put("java.lang.Double", DataParser.TYPE_DOUBLE);

		DataParser.DICTIONARY.put("float", DataParser.TYPE_FLOAT);
		DataParser.DICTIONARY.put("Float", DataParser.TYPE_FLOAT);
		DataParser.DICTIONARY.put("java.lang.Float", DataParser.TYPE_FLOAT);

		DataParser.DICTIONARY.put("int", DataParser.TYPE_INTERGER);
		DataParser.DICTIONARY.put("Integer", DataParser.TYPE_INTERGER);
		DataParser.DICTIONARY.put("java.lang.Integer", DataParser.TYPE_INTERGER);

		DataParser.DICTIONARY.put("long", DataParser.TYPE_LONG);
		DataParser.DICTIONARY.put("Long", DataParser.TYPE_LONG);
		DataParser.DICTIONARY.put("java.lang.Long", DataParser.TYPE_LONG);

		DataParser.DICTIONARY.put("arraylist", DataParser.TYPE_LIST);
		DataParser.DICTIONARY.put("ArrayList", DataParser.TYPE_LIST);
		DataParser.DICTIONARY.put("list", DataParser.TYPE_LIST);
		DataParser.DICTIONARY.put("List", DataParser.TYPE_LIST);

		DataParser.DICTIONARY.put("short", DataParser.TYPE_SHORT);
		DataParser.DICTIONARY.put("Short", DataParser.TYPE_SHORT);
		DataParser.DICTIONARY.put("java.lang.Short", DataParser.TYPE_SHORT);

		DataParser.DICTIONARY.put("string", DataParser.TYPE_STRING);
		DataParser.DICTIONARY.put("String", DataParser.TYPE_STRING);
		DataParser.DICTIONARY.put("java.lang.String", DataParser.TYPE_STRING);
	}

	/** The path to xml define data parser file . */
	private String                         definePath;

	/** The reader. */
	private Reader                         reader;

	/** The writer. */
	private Writer                         writer;

	/**
	 * Instantiates a new data parser with default define package reader is data-package.xml file.
	 */
	public DataParser() {
		this.reader = new Reader();
		this.writer = new Writer();
		this.definePath = DataParser.class.getClassLoader().getResource("data-package.xml").getPath();
	}

	/**
	 * Instantiates a new data parser.
	 * 
	 * @param definePath the path of xml define data parser file
	 */
	public DataParser(String definePath) {
		this.reader = new Reader();
		this.writer = new Writer();
		this.definePath = definePath;
	}

	/**
	 * Apply data from stream. Read parse data from stream into instance of ReadDataInterface and execute this data after reading.
	 * 
	 * @param inputstream the inputstream
	 */
	public void applyInputStream(InputStream inputstream) {
		ReadDataInterface data = null;
		try {
			do {
				data = this.readData(inputstream);
				if (data != null) {
					data.executeData();
				}
				else {
					System.err.println("Error when read data");
					break;
				}
			}
			while (true);
		}
		catch (SocketException e) {
			System.out.println("Socket close");
		}
		catch (IOException e) {
			e.printStackTrace();
		}
		catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * Apply stream from instance of SocketInterface. Read and parse data from stream into instance of ReadDataInterface and execute this data after
	 * reading.
	 *
	 * @param clientSocket the instance of SocketInterface
	 * @throws SocketTimeoutException the socket timeout exception
	 * @throws SocketException the socket exception
	 * @throws IOException Signals that an I/O exception has occurred.
	 * @throws Exception the exception
	 */
	public void applyInputStream(SocketClientInterface clientSocket) throws SocketTimeoutException, SocketException, IOException, Exception {
		ReadDataInterface data = null;
		do {
			data = this.readData(clientSocket.getInputStream());
			if (data != null) {
				data.executeData(clientSocket);
			}
			else {
				System.err.println("Not recognize data from stream");
				break;
			}
		}
		while (clientSocket.isConnected());
	}

	/**
	 * Apply stream. Get and write data from instance of WriteDataInterface into stream
	 * 
	 * @param outputStream the output stream
	 * @param dataListener the instance of WriteDataInterface
	 * @param command the command
	 * @throws Exception the exception
	 */
	public void applyOutputStream(OutputStream outputStream, WriteDataInterface dataListener, int command) throws Exception {
		this.writeData(command, outputStream, dataListener);
	}

	/**
	 * Gets the data type by String.
	 * 
	 * @param type the type string
	 * @return the data type. NULL if type given by parameter not defined
	 */
	public Byte getDataType(String type) {
		return DataParser.DICTIONARY.get(type);
	}

	/**
	 * Read data.
	 * 
	 * @param inputstream the byte buffer
	 * @return the read data object which implements ReadDataInterface
	 * @throws Exception the exception
	 */
	public ReadDataInterface readData(InputStream inputstream) throws Exception {
		ReadDataInterface data;
		int idCommand = this.reader.readInt(inputstream);
		if (idCommand != -1) {
			XmlUtil xml = new XmlUtil(this.definePath);
			data = this.getInstanceReadData(idCommand, xml);
			if (data != null) {
				NodeList listCommand = xml.getListNode("command", xml.getRoot());
				if (listCommand != null && listCommand.getLength() > 0) {
					for (int i = 0; i < listCommand.getLength(); i++) {
						Node nodeCommand = listCommand.item(i);
						String valueCommand = xml.getAttribute(nodeCommand, "value");
						if (Integer.parseInt(valueCommand) == idCommand) {
							this.readDataInstance(xml, (Element) nodeCommand, data, inputstream);
							break;
						}
					}    
                }
                else {

                }
				
			}
			return data;
		}
		else {
			throw new SocketException();
		}
	}

	/**
	 * Write data.
	 * 
	 * @param command the command
	 * @param out the data output stream
	 * @param data the data object which implements WriteDataInterface
	 * @return the data output stream
	 * @throws Exception the exception
	 */
	public void writeData(int command, OutputStream out, WriteDataInterface data) throws Exception {
		// Get object data by command
		XmlUtil xml = new XmlUtil(this.definePath);
		WriteDataInterface writedata = this.getInstanceWriteData(command, xml);
		if (data != null) {
			// Write command first
			this.writer.writeInt(out, command);

			data.getClass().cast(writedata);
			NodeList listCommand = xml.getListNode("command", xml.getRoot());
			for (int i = 0; i < listCommand.getLength(); i++) {
				Node nodeCommand = listCommand.item(i);
				String valueCommand = xml.getAttribute(nodeCommand, "value");
				if (Integer.parseInt(valueCommand) == command) {
					this.writeDataInstance(xml, (Element) nodeCommand, data, out);
					break;
				}
			}
			out.flush();
		}
	}

	/**
	 * Gets the data instance.
	 * 
	 * @param idCommand the id command
	 * @param xml the xml
	 * @return the instance of ReadDataInterface
	 */
	private ReadDataInterface getInstanceReadData(int idCommand, XmlUtil xml) {
		ReadDataInterface data = null;
		NodeList list = xml.getListNode("define", xml.getRoot());
		try {
			Node define = list.item(0);
			NodeList listCommand = xml.getListNode("define-command", (Element) define);
			for (int i = 0; i < listCommand.getLength(); i++) {
				Node nodeCommand = listCommand.item(i);
				String valueCommand = xml.getAttribute(nodeCommand, "value");
				String className = xml.getValue(nodeCommand);
				if (Integer.parseInt(valueCommand) == idCommand) {
					data = (ReadDataInterface) Class.forName(className).newInstance();
					break;
				}
			}
		}
		catch (Exception e) {
			e.printStackTrace();
		}
		return data;
	}

	/**
	 * Gets the instance write data.
	 * 
	 * @param idCommand the id command
	 * @param xml the xml
	 * @return the instance of WriteDataInterface
	 */
	private WriteDataInterface getInstanceWriteData(int idCommand, XmlUtil xml) {
		WriteDataInterface data = null;
		NodeList list = xml.getListNode("define", xml.getRoot());
		try {
			Node define = list.item(0);
			NodeList listCommand = xml.getListNode("define-command", (Element) define);
			for (int i = 0; i < listCommand.getLength(); i++) {
				Node nodeCommand = listCommand.item(i);
				String valueCommand = xml.getAttribute(nodeCommand, "value");
				String className = xml.getValue(nodeCommand);
				if (Integer.parseInt(valueCommand) == idCommand) {
					data = (WriteDataInterface) Class.forName(className).newInstance();
					break;
				}
			}
		}
		catch (Exception e) {
			e.printStackTrace();
		}
		return data;
	}

	/**
	 * Read data by type.
	 * 
	 * @param typeData the type data
	 * @param inputstream the byte buffer
	 * @return the object
	 * @throws IOException Signals that an I/O exception has occurred.
	 */
	private Object readDataByType(String typeData, InputStream inputstream) throws IOException {
		Object value = null;
		Byte objType = this.getDataType(typeData);
		if (objType == null) {
			objType = Byte.MIN_VALUE;
		}
		switch (objType) {
			case TYPE_BYTE:
				value = this.reader.readByte(inputstream);
				break;

			case TYPE_BYTE_ARRAY:
				value = this.reader.readByteArray(inputstream);
				break;

			case TYPE_DOUBLE:
				value = this.reader.readDouble(inputstream);
				break;

			case TYPE_FLOAT:
				value = this.reader.readFloat(inputstream);
				break;

			case TYPE_INTERGER:
				value = this.reader.readInt(inputstream);
				break;

			case TYPE_LONG:
				value = this.reader.readLong(inputstream);
				break;

			case TYPE_SHORT:
				value = this.reader.readShort(inputstream);
				break;

			case TYPE_STRING:
				value = this.reader.readString(inputstream);
				break;

			case TYPE_LIST:
				value = new ArrayList<>();
				break;

			default:
				try {
					value = Class.forName(typeData).newInstance();
					value = this.reader.readObject(value.getClass(), inputstream);
				}
				catch (Exception e) {
					e.printStackTrace();
				}
				break;
		}
		return value;
	}

	/**
	 * Read and parse data into instance.
	 * 
	 * @param xml the xml
	 * @param elementCommand the element command
	 * @param data the data instance
	 * @param inputstream the byte buffer
	 * @throws Exception the exception
	 */
	@SuppressWarnings({ "unchecked", "rawtypes" })
	private void readDataInstance(XmlUtil xml, Element elementCommand, Object data, InputStream inputstream) throws Exception {
		NodeList listData = xml.getListNode("data", elementCommand);
		for (int j = 0; j < listData.getLength(); j++) {
			Node nodeData = listData.item(j);
			String nameData = xml.getAttribute(nodeData, "name");
			String typeData = xml.getAttribute(nodeData, "type");
			String checkData = xml.getAttribute(nodeData, "breakvalue");
			Object fieldData = this.readDataByType(typeData, inputstream);
			if (fieldData instanceof ArrayList) {
				int size = this.reader.readInt(inputstream);
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
				System.err.println("Try to set value " + fieldData + " to field " + nameData + " in " + typeData + " type.");
			}

			if (checkData != null && checkData.toString().equals(fieldData.toString())) {
				break;
			}
		}
	}

	/**
	 * Sets the value of.
	 * 
	 * @param clazz the clazz
	 * @param lookingForValue the looking for value
	 * @param value the value
	 * @throws Exception the exception
	 */
	private void setValueOf(Object clazz, String lookingForValue, Object value) throws Exception {
		Field field = clazz.getClass().getDeclaredField(lookingForValue);
		field.setAccessible(true);
		field.set(clazz, value);
	}

	/**
	 * Write data by type.
	 * 
	 * @param typeData the data type of each field in data object
	 * @param nameData the data name of each field in data object. If field name is null, the data is not object's field
	 * @param out the data output stream
	 * @param data the data
	 * @return
	 * @throws Exception the exception
	 */
	private String writeDataByType(String typeData, String nameData, OutputStream out, Object data) throws Exception {
		Byte objType = this.getDataType(typeData);
		if (objType == null) {
			objType = Byte.MIN_VALUE;
		}
		Field field;
		String returnValue = "";
		switch (objType) {

			case TYPE_BYTE:
				if (nameData.equals("")) {
					this.writer.writeByte(out, (byte) data);
					returnValue = data.toString();
				}
				else {
					field = data.getClass().getDeclaredField(nameData);
					field.setAccessible(true);
					this.writer.writeByte(out, field.getByte(data));
					returnValue = field.get(data).toString();
				}
				break;

			case TYPE_BYTE_ARRAY:
				if (nameData.equals("")) {
					this.writer.writeByteArray(out, (byte[]) data);
					returnValue = data.toString();
				}
				else {
					field = data.getClass().getDeclaredField(nameData);
					field.setAccessible(true);
					this.writer.writeByteArray(out, (byte[]) field.get(data));
					returnValue = field.get(data).toString();
				}
				break;

			case TYPE_DOUBLE:
				if (nameData.equals("")) {
					this.writer.writeDouble(out, (double) data);
					returnValue = data.toString();
				}
				else {
					field = data.getClass().getDeclaredField(nameData);
					field.setAccessible(true);
					this.writer.writeDouble(out, field.getDouble(data));
					returnValue = field.get(data).toString();
				}
				break;

			case TYPE_FLOAT:
				if (nameData.equals("")) {
					this.writer.writeFloat(out, (float) data);
					returnValue = data.toString();
				}
				else {
					field = data.getClass().getDeclaredField(nameData);
					field.setAccessible(true);
					this.writer.writeFloat(out, field.getFloat(data));
					returnValue = field.get(data).toString();
				}
				break;

			case TYPE_INTERGER:
				if (nameData.equals("")) {
					this.writer.writeInt(out, (int) data);
					returnValue = data.toString();
				}
				else {
					field = data.getClass().getDeclaredField(nameData);
					field.setAccessible(true);
					this.writer.writeInt(out, field.getInt(data));
					returnValue = field.get(data).toString();
				}
				break;

			case TYPE_LONG:
				if (nameData.equals("")) {
					this.writer.writeLong(out, (long) data);
					returnValue = data.toString();
				}
				else {
					field = data.getClass().getDeclaredField(nameData);
					field.setAccessible(true);
					this.writer.writeLong(out, field.getLong(data));
					returnValue = field.get(data).toString();
				}
				break;

			case TYPE_SHORT:
				if (nameData.equals("")) {
					this.writer.writeShort(out, (short) data);
					returnValue = data.toString();
				}
				else {
					field = data.getClass().getDeclaredField(nameData);
					field.setAccessible(true);
					this.writer.writeShort(out, field.getShort(data));
					returnValue = field.get(data).toString();
				}
				break;

			case TYPE_STRING:
				if (nameData.equals("")) {
					this.writer.writeString(out, (String) data);
					returnValue = data.toString();
				}
				else {
					field = data.getClass().getDeclaredField(nameData);
					field.setAccessible(true);
					this.writer.writeString(out, (String) field.get(data));
					returnValue = field.get(data).toString();
				}
				break;

			case TYPE_LIST:
				field = data.getClass().getDeclaredField(nameData);
				field.setAccessible(true);
				@SuppressWarnings("rawtypes")
				List list = (List) field.get(data);
				this.writer.writeInt(out, list.size());
				for (Object object : list) {
					String objectType = object.getClass().getName();
					this.writeDataByType(objectType, "", out, object);
				}
				break;

			default:
				this.writer.writeObject(out, data);
				returnValue = data.toString();
				break;
		}
		return returnValue;
	}

	/**
	 * Write data instance.
	 * 
	 * @param xml the xml
	 * @param elementCommand the element command
	 * @param data the data
	 * @param out the data output stream
	 * @throws Exception the exception
	 */
	private void writeDataInstance(XmlUtil xml, Element elementCommand, Object data, OutputStream out) throws Exception {
		NodeList listData = xml.getListNode("data", elementCommand);
		for (int j = 0; j < listData.getLength(); j++) {
			Node nodeData = listData.item(j);
			String nameData = xml.getAttribute(nodeData, "name");
			String typeData = xml.getAttribute(nodeData, "type");
			String checkValue = xml.getAttribute(nodeData, "breakvalue");
			String checkData = this.writeDataByType(typeData, nameData, out, data);
			if (checkValue != null && checkValue.toString().equals(checkData.toString())) {
				break;
			}
		}
	}
}
