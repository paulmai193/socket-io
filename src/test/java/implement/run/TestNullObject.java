package implement.run;

public class TestNullObject {

	public static void main(String[] args) {
		Integer i = null;
		@SuppressWarnings("null")
		int j = i;
		System.out.println((int) j);
	}
}
