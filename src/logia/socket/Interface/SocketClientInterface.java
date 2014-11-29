package logia.socket.Interface;


import java.io.InputStream;
import java.io.OutputStream;

import logia.io.parser.DataParser;

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
	public DataParser getDataParser();
	
	/**
	 * Gets the input stream.
	 * 
	 * @return the input stream
	 */
	public InputStream getInputStream();
	
	/**
	 * Gets the output stream.
	 * 
	 * @return the output stream
	 */
	public OutputStream getOutputStream();
	
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
}
