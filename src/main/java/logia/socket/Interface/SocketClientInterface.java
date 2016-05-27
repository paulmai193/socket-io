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
public interface SocketClientInterface {

	/**
	 * Connect.
	 *
	 * @throws ConnectionErrorException the connection error exception
	 * @throws ReadDataException the read data exception
	 */
	public void connect() throws ConnectionErrorException, ReadDataException;

	/**
	 * Disconnect.
	 */
	public void disconnect();

	/**
	 * Echo.
	 *
	 * @param __data the __data
	 * @throws WriteDataException the write data exception
	 */
	public void echo(WriteDataInterface __data) throws WriteDataException;

	/**
	 * Echo and wait.
	 *
	 * @param __data the __data
	 * @return the read data interface
	 * @throws WriteDataException the write data exception
	 * @throws InterruptedException the interrupted exception
	 */
	public ReadDataInterface echoAndWait(WriteDataInterface __data)
	        throws WriteDataException, InterruptedException;

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
	 * Checks if is wait for return.
	 *
	 * @return true, if is wait for return
	 */
	public boolean isWaitForReturn();

	/**
	 * Listen.
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
	 * @param __returned the new returned
	 */
	public void setReturned(ReadDataInterface __returned);

	/**
	 * Sets the timeout listener.
	 *
	 * @param __listener the new timeout listener
	 */
	public void setTimeoutListener(SocketTimeoutListener __listener);

}
