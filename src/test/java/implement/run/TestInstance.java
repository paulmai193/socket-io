package implement.run;

import org.apache.commons.lang3.ClassUtils;

/**
 * The Class TestInstance.
 *
 * @author Paul Mai
 */
public class TestInstance {

	/**
	 * The main method.
	 *
	 * @param args the arguments
	 */
	public static void main(String[] args) {
		Integer i = 1;
		Object j = i;
		System.out.println(ClassUtils.isPrimitiveOrWrapper(i.getClass()));
	}
}
