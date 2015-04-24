package implement.client.datapackage;

/**
 * The Class Contact.
 * 
 * @author Paul Mai
 */
public class Contact {

	/** The name. */
	String name;

	/** The phone. */
	String phone;

	/** The email. */
	String email;

	/**
	 * Instantiates a new contact.
	 */
	public Contact() {

	}

	/**
	 * Instantiates a new contact.
	 *
	 * @param name the name
	 * @param phone the phone
	 * @param email the email
	 */
	public Contact(String name, String phone, String email) {
		this.email = email;
		this.phone = phone;
		this.name = name;
	}

	/**
	 * Gets the name.
	 * 
	 * @return the name
	 */
	public String getName() {
		return name;
	}

	/**
	 * Sets the name.
	 * 
	 * @param name the name to set
	 */
	public void setName(String name) {
		this.name = name;
	}

	/**
	 * Gets the phone.
	 * 
	 * @return the phone
	 */
	public String getPhone() {
		return phone;
	}

	/**
	 * Sets the phone.
	 * 
	 * @param phone the phone to set
	 */
	public void setPhone(String phone) {
		this.phone = phone;
	}

	/**
	 * Gets the email.
	 * 
	 * @return the email
	 */
	public String getEmail() {
		return email;
	}

	/**
	 * Sets the email.
	 * 
	 * @param email the email to set
	 */
	public void setEmail(String email) {
		this.email = email;
	}

}
