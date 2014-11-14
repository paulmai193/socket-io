package socket.Interface;

import java.io.InputStream;
import java.io.OutputStream;

import io.util.Reader;
import io.util.Writer;

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
	 * Gets the reader.
	 * 
	 * @return the reader
	 */
	public Reader getReader();
	
	/**
	 * Gets the writer.
	 * 
	 * @return the writer
	 */
	public Writer getWriter();
	
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
	
	/* (non-Javadoc)
	 * 
	 * @see java.lang.Runnable#run() */
	@Override
	public void run();
}
