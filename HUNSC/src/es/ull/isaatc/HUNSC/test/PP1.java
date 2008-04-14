package es.ull.isaatc.HUNSC.test;

public class PP1 {
	public PP1 () {
		
	}
	
	public void makeSth() {
		print();
		new InnerClass().print();
	}
	
	public class InnerClass {
		public InnerClass() {
			
		}
		public void print() {
			System.out.println("Inner PP1");
		}		
	}
	public void print() {
		System.out.println("PP1");
	}
}
