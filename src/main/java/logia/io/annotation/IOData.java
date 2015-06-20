package logia.io.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * The annotation IOData.
 * 
 * @author Paul Mai
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(value = { ElementType.FIELD })
public @interface IOData {

	/**
	 * The data order.
	 *
	 * @return the byte
	 */
	public byte order();

	/**
	 * The data type.
	 *
	 * @return the data type
	 */
	public DataType type();

}
