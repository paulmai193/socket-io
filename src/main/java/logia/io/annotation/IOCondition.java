package logia.io.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import logia.io.annotation.type.ConditionActionType;
import logia.io.annotation.type.ConditionType;

/**
 * The Interface IOCondition.
 *
 * @author Paul Mai
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(value = { ElementType.METHOD })
public @interface IOCondition {

	/**
	 * Field.
	 *
	 * @return the string
	 */
	public String field();

	/**
	 * Type.
	 *
	 * @return the condition type
	 */
	public ConditionType type();

	/**
	 * Value.
	 *
	 * @return the string
	 */
	public String value();

	/**
	 * Action.
	 *
	 * @return the condition action type
	 */
	public ConditionActionType action();

}
