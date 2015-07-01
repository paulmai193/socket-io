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
	 */
	public DataParserByAnnotation() {
		super();
	}

	/**
	 * Instantiates a new data parser by annotation.
	 *
	 * @param definePath the define path
	 */
	public DataParserByAnnotation(String definePath) {
		super(definePath);
	}

	/**
	 * Sort fields by data order.
	 *
	 * @param fields the fields
	 */
	private void sortByOrder(Field[] fields) {
		Arrays.sort(fields, new Comparator<Field>() {

			/*
			 * (non-Javadoc)
			 * 
			 * @see java.util.Comparator#compare(java.lang.Object, java.lang.Object)
			 */
			@Override
			public int compare(Field o1, Field o2) {
				IOData order1 = o1.getAnnotation(IOData.class);
				IOData order2 = o2.getAnnotation(IOData.class);
				if (order1 != null && order2 != null) {
					return order1.order() - order2.order();
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
	protected ReadDataInterface readData(InputStream inputstream) throws Exception {
		ReadDataInterface data;

		// Read command
		Object command = this.readDataByType(this._commandType, inputstream);
		if (command != null && !command.toString().equals("-1")) {
			// Get instance which this command
			data = this.getInstanceReadData(command.toString());
			if (data != null) {
				IOCommand clazzAnnotation = data.getClass().getAnnotation(IOCommand.class);
				if (clazzAnnotation != null) {
					// Iterate fields with IOCommand annotation
					Field[] fields = data.getClass().getDeclaredFields();
					this.sortByOrder(fields);
					for (Field field : fields) {
						field.setAccessible(true);
						IOData fieldAnnotation = field.getAnnotation(IOData.class);
						if (fieldAnnotation != null) {
							String typeData = fieldAnnotation.type().toString().toLowerCase();
							String checkData = fieldAnnotation.breakValue();
							Object fieldData = this.readDataByType(typeData, inputstream);
							if (field.getGenericType() instanceof ParameterizedType) {
								ParameterizedType pt = (ParameterizedType) field.getGenericType();
								String elementType = pt.getActualTypeArguments()[0].toString().replace("class ", "");

								int size = this._reader.readInt(inputstream);
								fieldData = new ArrayList<Object>();
								for (int k = 0; k < size; k++) {
									Object element = this.readDataByType(elementType, inputstream);
									((ArrayList) fieldData).add(element);
								}
							}
							try {
								this.setValueOf(data, field.getName(), fieldData);
							}
							catch (Exception e) {
								System.err.println("Try to set value " + fieldData + " to field " + field.getName() + " in " + typeData + " type.");
							}

							if (checkData != null && checkData.toString().equals(fieldData.toString())) {
								break;
							}
						}
					}
				}
			}
			return data;
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
			this.writeDataByType(this._commandType, "", out, command);

			// Write each element
			data.getClass().cast(writedata);
			Field[] fields = data.getClass().getDeclaredFields();
			this.sortByOrder(fields);
			for (Field field : fields) {
				field.setAccessible(true);
				IOData fieldAnnotation = field.getAnnotation(IOData.class);
				if (fieldAnnotation != null) {
					String typeData = fieldAnnotation.type().toString().toLowerCase();
					String checkValue = fieldAnnotation.breakValue();
					String nameData = field.getName();
					field.get(data);
					String checkData = this.writeDataByType(typeData, nameData, out, data);
					if (checkValue != null && checkValue.toString().equals(checkData.toString())) {
						break;
					}
				}
			}
			out.flush();
		}
	}
}
