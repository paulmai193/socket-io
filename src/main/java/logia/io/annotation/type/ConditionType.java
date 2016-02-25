package logia.io.annotation.type;

/**
 * The Enum ConditionType.
 *
 * @author Paul Mai
 */
public enum ConditionType {

	/** All conditions different. */
	DIFFERENT,

	/** All conditions equal. */
	EQUAL,

	/** At least 1 condition different. */
	OR_DIFFERENT,

	/** At least 1 condition equal. */
	OR_EQUAL
}
