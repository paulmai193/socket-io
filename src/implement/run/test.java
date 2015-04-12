package implement.run;

public class test {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		String s = "0";
		Object o1 = s;
		Integer i = 0;
		Object o2 = i;
		
		System.out.println(o1.toString().equals(o2.toString()));
	}

}
