package implement.server.datapackage;

import logia.io.annotation.IOData;
import logia.io.annotation.type.DataType;

/**
 * The Class Contact.
 *
 * @author Paul Mai
 */
public class Contact {

	/** The email. */
	@IOData(order = 3, type = DataType.STRING)
	String email;

	/** The name. */
	@IOData(order = 1, type = DataType.STRING)
	String name;

	/** The phone. */
	@IOData(order = 2, type = DataType.STRING)
	String phone;

	/**
	 * Instantiates a new contact.
	 */
	public Contact() {

	}

	/**
	 * Gets the email.
	 *
	 * @return the email
	 */
	public String getEmail() {
		return this.email;
	}

	/**
	 * Gets the name.
	 *
	 * @return the name
	 */
	public String getName() {
		return this.name;
	}

	/**
	 * Gets the phone.
	 *
	 * @return the phone
	 */
	public String getPhone() {
		return this.phone;
	}

	/**
	 * Sets the email.
	 *
	 * @param email the new email
	 */
	public void setEmail(String email) {
		this.email = email;
	}

	/**
	 * Sets the name.
	 *
	 * @param name the new name
	 */
	public void setName(String name) {
		this.name = name;
	}

	/**
	 * Sets the phone.
	 *
	 * @param phone the new phone
	 */
	public void setPhone(String phone) {
		this.phone = phone;
	}

}
