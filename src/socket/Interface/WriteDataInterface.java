package socket.Interface;

/**
 * The listener interface for receiving writeData events. The class that is interested in processing
 * a writeData event implements this interface, and the object created with that class is registered
 * with a component using the component's <code>addWriteDataListener<code> method. When the
 * writeData event occurs, that object's appropriate method is invoked.
 * 
 * @author Paul Mai
 */
public interface WriteDataInterface {
	
}
