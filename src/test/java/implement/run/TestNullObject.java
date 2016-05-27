package implement.run;

/**
 * The Class TestNullObject.
 *
 * @author Paul Mai
 */
public class TestNullObject {

	/**
	 * The main method.
	 *
	 * @param args the arguments
	 */
	public static void main(String[] args) {
		Integer i = null;
		@SuppressWarnings("null")
		int j = i;
		System.out.println((int) j);
	}
}
