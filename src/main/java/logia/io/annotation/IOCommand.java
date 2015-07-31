package logia.io.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * The annotation IOCommand.
 * 
 * @author Paul Mai
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(value = { ElementType.TYPE })
public @interface IOCommand {

	/**
	 * Type of command: Reader or Writer.
	 *
	 * @return the command type
	 */
	// public CommandType[] type() default CommandType.NOT_SET;

	/**
	 * Command value.
	 *
	 * @return the int
	 */
	public int value();
}
