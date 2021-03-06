package logia.io.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * The Interface IOCommand.
 *
 * @author Paul Mai
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(value = { ElementType.TYPE })
public @interface IOCommand {

	/**
	 * Value.
	 *
	 * @return the int
	 */
	public int value();

}
