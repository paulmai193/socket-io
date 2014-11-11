package socket.listener;

import java.io.InputStream;
import java.io.OutputStream;

import io.util.Reader;
import io.util.Writer;

/**
 * The listener interface for receiving socket events.
 * The class that is interested in processing a socket
 * event implements this interface, and the object created
 * with that class is registered with a component using the
 * component's <code>addSocketListener<code> method. When
 * the socket event occurs, that object's appropriate
 * method is invoked.
 *
 * @author Paul Mai
 */
public interface SocketListener {
	
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
}
