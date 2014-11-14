package test;



// TODO: Auto-generated Javadoc
/**
 * The Class Test.
 */
public class Test {
	
	/**
	 * The main method.
	 * 
	 * @param args the arguments
	 */
	public static void main(String[] args) {
		Child c = new Child("Child");
		System.out.println(c.getString());
	}
	
	public static abstract class Parent {
		protected String s;
		public Parent() {
			s = "parent";
		}
		public String getString() {
			return s;
		}
	}
	
	public static class Child extends Parent {
		public Child(String s) {
			super();
			this.s = s;
		}
	}
}
