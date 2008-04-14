package es.ull.isaatc.HUNSC.test;

public class PP2 extends PP1 {
	public PP2 () {
		
	}

	public class InnerClass {
		public InnerClass() {
			
		}
		public void print() {
			System.out.println("Inner PP2");
		}		
	}

	public void print() {
		System.out.println("PP2");
	}
}
