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

import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

/**
 * The Class DataParser. Read data from inputstream and Write data to outputstream base on data
 * structure define by XML DataPackage file
 * 
 * @author Paul Mai
 */
public class DataParser {
	
	/** The Constant TYPE_BYTE. */
	private static final byte TYPE_BYTE = 1;
	
	/** The Constant TYPE_BYTE_ARRAY. */
	private static final byte TYPE_BYTE_ARRAY = 2;
	
	/** The Constant TYPE_DOUBLE. */
	private static final byte TYPE_DOUBLE = 4;
	
	/** The Constant TYPE_FLOAT. */
	private static final byte TYPE_FLOAT = 5;
	
	/** The Constant TYPE_INTERGER. */
	private static final byte TYPE_INTERGER = 6;
	
	/** The Constant TYPE_LONG. */
	private static final byte TYPE_LONG = 7;
	
	/** The Constant TYPE_SHORT. */
	private static final byte TYPE_SHORT = 8;
	
	/** The Constant TYPE_STRING. */
	private static final byte TYPE_STRING = 9;
	
	/** The Constant TYPE_LIST. */
	private static final byte TYPE_LIST = 10;
	
	/** The Constant DICTIONARY, use to get data type by input string. */
	private static final Map<String, Byte> DICTIONARY;
	static {
		DICTIONARY = new HashMap<String, Byte>();
		
		DICTIONARY.put("byte", TYPE_BYTE);
		DICTIONARY.put("Byte", TYPE_BYTE);
		DICTIONARY.put("java.lang.Byte", TYPE_BYTE);
		
		DICTIONARY.put("byte[]", TYPE_BYTE_ARRAY);
		DICTIONARY.put("bytearray", TYPE_BYTE_ARRAY);
		DICTIONARY.put("Bytearray", TYPE_BYTE_ARRAY);
		DICTIONARY.put("ByteArray", TYPE_BYTE_ARRAY);
		
		DICTIONARY.put("double", TYPE_DOUBLE);
		DICTIONARY.put("Double", TYPE_DOUBLE);
		DICTIONARY.put("java.lang.Double", TYPE_DOUBLE);
		
		DICTIONARY.put("float", TYPE_FLOAT);
		DICTIONARY.put("Float", TYPE_FLOAT);
		DICTIONARY.put("java.lang.Float", TYPE_FLOAT);
		
		DICTIONARY.put("int", TYPE_INTERGER);
		DICTIONARY.put("Integer", TYPE_INTERGER);
		DICTIONARY.put("java.lang.Integer", TYPE_INTERGER);
		
		DICTIONARY.put("long", TYPE_LONG);
		DICTIONARY.put("Long", TYPE_LONG);
		DICTIONARY.put("java.langLong", TYPE_LONG);
		
		DICTIONARY.put("arraylist", TYPE_LIST);
		DICTIONARY.put("ArrayList", TYPE_LIST);
		DICTIONARY.put("list", TYPE_LIST);
		DICTIONARY.put("List", TYPE_LIST);
		
		DICTIONARY.put("short", TYPE_SHORT);
		DICTIONARY.put("Short", TYPE_SHORT);
		DICTIONARY.put("java.lang.Short", TYPE_SHORT);
		
		DICTIONARY.put("string", TYPE_STRING);
		DICTIONARY.put("String", TYPE_STRING);
		DICTIONARY.put("java.lang.String", TYPE_STRING);
	}
	
	/** The path to xml define data parser file . */
	private String definePath;
	
	/** The reader. */
	private Reader reader;
	
	/** The writer. */
	private Writer writer;
	
	/** The is ping. */
	boolean isPing;
	
	/**
	 * Instantiates a new data parser.
	 */
	public DataParser() {
		reader = new Reader();
		writer = new Writer();
		definePath = "";
		isPing = false;
	}
	
	/**
	 * Instantiates a new data parser.
	 * 
	 * @param definePath the path of xml define data parser file
	 */
	public DataParser(String definePath) {
		reader = new Reader();
		writer = new Writer();
		this.definePath = definePath;
		isPing = false;
	}
	
	/**
	 * Apply data from stream. Read parse data from stream into instance of ReadDataInterface
	 * 
	 * @param inputstream the inputstream
	 */
	public void applyInputStream(InputStream inputstream) {
		ReadDataInterface data = null;
		try {
			do {
				data = readData(inputstream);
				if (data != null) {
					data.executeData();
				}
				else {
					System.err.println("Error when read data");
					break;
				}
			} while (true);
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
	 * Apply stream from instance of SocketInterface. Read and parse data from stream into instance
	 * of ReadDataInterface. The reader was set timeout, so when the socket timeout, it will test
	 * the connection with <b>Minimum byte number</b>, if target response, the timeout will reset.
	 * 
	 * @param clientSocket the instance of SocketInterface
	 */
	public void applyInputStream(SocketClientInterface clientSocket) {
		ReadDataInterface data = null;
		try {
			do {
				try {
					data = readData(clientSocket.getInputStream());
					if (data != null) {
						data.executeData(clientSocket);
					}
					else {
						System.err.println("Not recognize data from stream");
						break;
					}
				}
				catch (SocketTimeoutException e) {
					try {
						if (isPing) {
							System.out.println("Time out !");
							break;
						}
						else {
							clientSocket.echo(new WriteDataInterface() {
							}, Byte.MIN_VALUE);
							isPing = true;	
						}						
					}
					catch (Exception e1) {
						e1.printStackTrace();
					}
				}
				
			} while (clientSocket.isConnected());
		}
		catch (SocketException e) {
			e.printStackTrace();
			System.out.println("Socket close");
		}
		catch (IOException e) {
			e.printStackTrace();
		}
		catch (Exception e) {
			e.printStackTrace();
		}
		finally {
			clientSocket.disconnect();
		}
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
		writeData(command, outputStream, dataListener);
	}
	
	/**
	 * Gets the data type by String.
	 * 
	 * @param type the type string
	 * @return the data type. NULL if type given by parameter not defined
	 */
	public Byte getDataType(String type) {
		return DICTIONARY.get(type);
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
		int idCommand = reader.readInt(inputstream);
		isPing = false;
		if (idCommand == Byte.MIN_VALUE) {
			data = new ReadDataInterface() {
				
				@Override
				public void executeData(SocketClientInterface clientSocket) throws Exception {
					System.out.println("Ping");
					clientSocket.echo(new WriteDataInterface() {
					}, Byte.MAX_VALUE);
				}
				
				@Override
				public void executeData() throws Exception {
					
				}
			};
			return data;
		}
		else if (idCommand == Byte.MAX_VALUE) {
			data = new ReadDataInterface() {
				
				@Override
				public void executeData(SocketClientInterface clientSocket) throws Exception {
					System.out.println("Pong");
				}
				
				@Override
				public void executeData() throws Exception {
					
				}
			};
			return data;
		}
		else if (idCommand != -1) {
			XmlParser xml = new XmlParser(definePath);
			data = getInstanceReadData(idCommand, xml);
			if (data != null) {
				NodeList listCommand = xml.getListNode("command", xml.getRoot());
				for (int i = 0; i < listCommand.getLength(); i++) {
					Node nodeCommand = listCommand.item(i);
					String valueCommand = xml.getAttribute(nodeCommand, "value");
					if (Integer.parseInt(valueCommand) == idCommand) {
						readDataInstance(xml, (Element) nodeCommand, data, inputstream);
						break;
					}
				}
			}
			return data;
		}
		else {
			throw new SocketException();
		}
	}
	
	/**
	 * Gets the data instance.
	 * 
	 * @param idCommand the id command
	 * @param xml the xml
	 * @return the instance of ReadDataInterface
	 */
	private ReadDataInterface getInstanceReadData(int idCommand, XmlParser xml) {
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
	 * Read and parse data into instance.
	 * 
	 * @param xml the xml
	 * @param elementCommand the element command
	 * @param data the data instance
	 * @param inputstream the byte buffer
	 * @throws Exception the exception
	 */
	@SuppressWarnings({ "unchecked", "rawtypes" })
	private void readDataInstance(XmlParser xml, Element elementCommand, Object data, InputStream inputstream) throws Exception {
		NodeList listData = xml.getListNode("data", elementCommand);
		for (int j = 0; j < listData.getLength(); j++) {
			Node nodeData = listData.item(j);
			String nameData = xml.getAttribute(nodeData, "name");
			String typeData = xml.getAttribute(nodeData, "type");
			String checkData = xml.getAttribute(nodeData, "checkvalue");
			Object objData = readDataByType(typeData, inputstream);
			if (objData instanceof ArrayList) {
				int size = reader.readInt(inputstream);
				String elementType = xml.getAttribute(nodeData, "elementtype");
				objData = new ArrayList<Object>();
				for (int k = 0; k < size; k++) {
					Object element = readDataByType(elementType, inputstream);
					((ArrayList) objData).add(element);
				}
			}
			setValueOf(data, nameData, objData);
			if (checkData != null && checkData.toString().equals(objData.toString())) {
				break;
			}
		}
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
		Byte objType = getDataType(typeData);
		if (objType == null) {
			objType = Byte.MIN_VALUE;
		}
		switch (objType) {
			case TYPE_BYTE:
				value = reader.readByte(inputstream);
				break;
			
			case TYPE_BYTE_ARRAY:
				value = reader.readByteArray(inputstream);
				break;
			
			case TYPE_DOUBLE:
				value = reader.readDouble(inputstream);
				break;
			
			case TYPE_FLOAT:
				value = reader.readFloat(inputstream);
				break;
			
			case TYPE_INTERGER:
				value = reader.readInt(inputstream);
				break;
			
			case TYPE_LONG:
				value = reader.readLong(inputstream);
				break;
			
			case TYPE_SHORT:
				value = reader.readShort(inputstream);
				break;
			
			case TYPE_STRING:
				value = reader.readString(inputstream);
				break;
			
			case TYPE_LIST:
				value = new ArrayList<>();
				break;
			
			default:
				try {
					value = Class.forName(typeData).newInstance();
					value = reader.readObject(value.getClass(), inputstream);
				}
				catch (Exception e) {
					e.printStackTrace();
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
	private void setValueOf(Object clazz, String lookingForValue, Object value) throws Exception {
		Field field = clazz.getClass().getDeclaredField(lookingForValue);
		field.setAccessible(true);
		field.set(clazz, value);
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
		XmlParser xml = new XmlParser(definePath);
		WriteDataInterface writedata = getInstanceWriteData(command, xml);
		if (data != null) {
			// Write command first
			writer.writeInt(out, command);
			
			data.getClass().cast(writedata);
			NodeList listCommand = xml.getListNode("command", xml.getRoot());
			for (int i = 0; i < listCommand.getLength(); i++) {
				Node nodeCommand = listCommand.item(i);
				String valueCommand = xml.getAttribute(nodeCommand, "value");
				if (Integer.parseInt(valueCommand) == command) {
					writeDataInstance(xml, (Element) nodeCommand, data, out);
					break;
				}
			}
			out.flush();
		}
	}
	
	/**
	 * Gets the instance write data.
	 * 
	 * @param idCommand the id command
	 * @param xml the xml
	 * @return the instance of WriteDataInterface
	 */
	private WriteDataInterface getInstanceWriteData(int idCommand, XmlParser xml) {
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
	 * Write data instance.
	 * 
	 * @param xml the xml
	 * @param elementCommand the element command
	 * @param data the data
	 * @param out the data output stream
	 * @throws Exception the exception
	 */
	private void writeDataInstance(XmlParser xml, Element elementCommand, Object data, OutputStream out) throws Exception {
		NodeList listData = xml.getListNode("data", elementCommand);
		for (int j = 0; j < listData.getLength(); j++) {
			Node nodeData = listData.item(j);
			String nameData = xml.getAttribute(nodeData, "name");
			String typeData = xml.getAttribute(nodeData, "type");
			String checkValue = xml.getAttribute(nodeData, "checkvalue");
			String checkData = writeDataByType(typeData, nameData, out, data);
			if (checkValue != null && checkValue.toString().equals(checkData.toString())) {
				break;
			}
		}
	}
	
	/**
	 * Write data by type.
	 * 
	 * @param typeData the data type of each field in data object
	 * @param nameData the data name of each field in data object. If field name is null, the data
	 *            is not object's field
	 * @param out the data output stream
	 * @param data the data
	 * @return 
	 * @throws Exception the exception
	 */
	private String writeDataByType(String typeData, String nameData, OutputStream out, Object data) throws Exception {
		Byte objType = getDataType(typeData);
		if (objType == null) {
			objType = Byte.MIN_VALUE;
		}
		Field field;
		String returnValue = "";
		switch (objType) {
		
			case TYPE_BYTE:
				if (nameData.equals("")) {
					writer.writeByte(out, (byte) data);
					returnValue = data.toString();
				}
				else {
					field = data.getClass().getDeclaredField(nameData);
					field.setAccessible(true);
					writer.writeByte(out, field.getByte(data));
					returnValue = field.get(data).toString();
				}
				break;
			
			case TYPE_BYTE_ARRAY:
				if (nameData.equals("")) {
					writer.writeByteArray(out, (byte[]) data);
					returnValue = data.toString();
				}
				else {
					field = data.getClass().getDeclaredField(nameData);
					field.setAccessible(true);
					writer.writeByteArray(out, (byte[]) field.get(data));
					returnValue = field.get(data).toString();
				}
				break;
			
			case TYPE_DOUBLE:
				if (nameData.equals("")) {
					writer.writeDouble(out, (double) data);
					returnValue = data.toString();
				}
				else {
					field = data.getClass().getDeclaredField(nameData);
					field.setAccessible(true);
					writer.writeDouble(out, field.getDouble(data));
					returnValue = field.get(data).toString();
				}
				break;
			
			case TYPE_FLOAT:
				if (nameData.equals("")) {
					writer.writeFloat(out, (float) data);
					returnValue = data.toString();
				}
				else {
					field = data.getClass().getDeclaredField(nameData);
					field.setAccessible(true);
					writer.writeFloat(out, field.getFloat(data));
					returnValue = field.get(data).toString();
				}
				break;
			
			case TYPE_INTERGER:
				if (nameData.equals("")) {
					writer.writeInt(out, (int) data);
					returnValue = data.toString();
				}
				else {
					field = data.getClass().getDeclaredField(nameData);
					field.setAccessible(true);
					writer.writeInt(out, field.getInt(data));
					returnValue = field.get(data).toString();
				}
				break;
			
			case TYPE_LONG:
				if (nameData.equals("")) {
					writer.writeLong(out, (long) data);
					returnValue = data.toString();
				}
				else {
					field = data.getClass().getDeclaredField(nameData);
					field.setAccessible(true);
					writer.writeLong(out, field.getLong(data));
					returnValue = field.get(data).toString();
				}
				break;
			
			case TYPE_SHORT:
				if (nameData.equals("")) {
					writer.writeShort(out, (short) data);
					returnValue = data.toString();
				}
				else {
					field = data.getClass().getDeclaredField(nameData);
					field.setAccessible(true);
					writer.writeShort(out, field.getShort(data));
					returnValue = field.get(data).toString();
				}
				break;
			
			case TYPE_STRING:
				if (nameData.equals("")) {
					writer.writeString(out, (String) data);
					returnValue = data.toString();
				}
				else {
					field = data.getClass().getDeclaredField(nameData);
					field.setAccessible(true);
					writer.writeString(out, (String) field.get(data));
					returnValue = field.get(data).toString();
				}
				break;
			
			case TYPE_LIST:
				field = data.getClass().getDeclaredField(nameData);
				field.setAccessible(true);
				@SuppressWarnings("rawtypes")
				List list = (List) field.get(data);
				writer.writeInt(out, list.size());
				for (Object object : list) {
					String objectType = object.getClass().getName();
					writeDataByType(objectType, "", out, object);
				}
				break;
			
			default:
				writer.writeObject(out, data);
				returnValue = data.toString();
				break;
		}
		return returnValue;
	}
}
