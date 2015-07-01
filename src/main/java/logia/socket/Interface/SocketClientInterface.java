package logia.socket.Interface;

import java.io.InputStream;
import java.io.OutputStream;

/**
 * The Interface SocketClientInterface.
 * 
 * @author Paul Mai
 */
public interface SocketClientInterface extends Runnable {

	/**
	 * Socket connect.
	 */
	public void connect();

	/**
	 * Socket disconnect.
	 */
	public void disconnect();

	/**
	 * Echo method. Start send data through connection
	 *
	 * @param data the data
	 * @param command the command
	 * @throws Exception the exception
	 */
	public void echo(WriteDataInterface data, int command) throws Exception;

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
	 * Listen method. Start reading data send through connection
	 */
	public void listen();

	/**
	 * Sets the data parser.
	 *
	 * @param dataParser the new data parser
	 */
	public void setDataParser(ParserInterface dataParser);

	/**
	 * Sets the id.
	 *
	 * @param id the new id
	 */
	public void setId(String id);

	/**
	 * Sets the timeout listener.
	 *
	 * @param listener the new timeout listener
	 */
	public void setTimeoutListener(SocketTimeoutListener listener);
}
