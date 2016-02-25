package logia.socket.Interface;

import java.io.InputStream;
import java.io.OutputStream;
import java.net.SocketException;
import java.net.SocketTimeoutException;

import logia.io.exception.ConnectionErrorException;
import logia.io.exception.ReadDataException;
import logia.io.exception.WriteDataException;
import logia.socket.listener.SocketTimeoutListener;

/**
 * The Interface SocketClientInterface.
 * 
 * @author Paul Mai
 */
public interface SocketClientInterface extends Runnable {

	/**
	 * Socket connect.
	 *
	 * @throws ConnectionErrorException the connection error exception
	 */
	public void connect() throws ConnectionErrorException;

	/**
	 * Socket disconnect.
	 */
	public void disconnect();

	/**
	 * Echo method. Start send data through connection
	 *
	 * @param __data the data
	 * @throws WriteDataException the write data exception
	 */
	public void echo(WriteDataInterface __data) throws WriteDataException;

	/**
	 * Echo method. Start send data through connection
	 * 
	 * @deprecated since version 0.0.8
	 *
	 * @param __data the data
	 * @param __command the command
	 * @throws WriteDataException the write data exception
	 */
	@Deprecated
	public void echo(WriteDataInterface __data, Object __command) throws WriteDataException;

	/**
	 * Echo method. Start send data through connection and waiting a response
	 *
	 * @param __data the data
	 * @return the response data
	 * @throws WriteDataException the write data exception
	 * @throws InterruptedException the interrupted exception
	 */
	public ReadDataInterface echoAndWait(WriteDataInterface __data) throws WriteDataException, InterruptedException;

	/**
	 * Echo method. Start send data through connection and waiting a response
	 * 
	 * @deprecated since version 0.0.8
	 *
	 * @param __data the data
	 * @param __command the command
	 * @return the response data
	 * @throws WriteDataException the write data exception
	 * @throws InterruptedException the interrupted exception
	 */
	@Deprecated
	public ReadDataInterface echoAndWait(WriteDataInterface __data, Object __command) throws WriteDataException, InterruptedException;

	/**
	 * Gets the data parser.
	 *
	 * @return the data parser
	 */
	public ParserInterface getDataParser();

	/**
	 * Gets the id.
	 *
	 * @return the id
	 */
	public String getId();

	/**
	 * Gets the input stream.
	 * 
	 * @return the input stream
	 */
	public InputStream getInputStream();

	/**
	 * Gets the live time.
	 *
	 * @return the live time
	 */
	public long getLiveTime();

	/**
	 * Gets the output stream.
	 * 
	 * @return the output stream
	 */
	public OutputStream getOutputStream();

	/**
	 * Gets the timeout listener.
	 *
	 * @return the timeout listener
	 */
	public SocketTimeoutListener getTimeoutListener();

	/**
	 * Checks if is connected.
	 * 
	 * @return true, if is connected
	 */
	public boolean isConnected();

	/**
	 * Checks if is connection is waiting for return response.
	 *
	 * @return the isWait
	 */
	public boolean isWaitForReturn();

	/**
	 * Listen method. Start reading data send through connection
	 *
	 * @throws ReadDataException the read data exception
	 * @throws SocketTimeoutException the socket timeout exception
	 * @throws SocketException the socket exception
	 */
	public void listen() throws ReadDataException, SocketTimeoutException, SocketException;

	/**
	 * Sets the data parser.
	 *
	 * @param __dataParser the new data parser
	 */
	public void setDataParser(ParserInterface __dataParser);

	/**
	 * Sets the id.
	 *
	 * @param __id the new id
	 */
	public void setId(String __id);

	/**
	 * Sets the returned.
	 *
	 * @param __returned the returned to set
	 */
	public void setReturned(ReadDataInterface __returned);

	/**
	 * Sets the timeout listener.
	 *
	 * @param __listener the new timeout listener
	 */
	public void setTimeoutListener(SocketTimeoutListener __listener);

}
