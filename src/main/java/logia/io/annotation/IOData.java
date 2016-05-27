package logia.io.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import logia.io.annotation.type.DataType;

/**
 * The Interface IOData.
 *
 * @author Paul Mai
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(value = { ElementType.FIELD })
public @interface IOData {

	/**
	 * Conditions.
	 *
	 * @return the IO condition[]
	 */
	public IOCondition[] conditions() default {};

	/**
	 * Order.
	 *
	 * @return the byte
	 */
	public byte order();

	/**
	 * Type.
	 *
	 * @return the data type
	 */
	public DataType type();

}
