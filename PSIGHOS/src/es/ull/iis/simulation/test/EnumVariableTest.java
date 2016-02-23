package es.ull.iis.simulation.test;

import es.ull.iis.simulation.variable.EnumType;
import es.ull.iis.simulation.variable.EnumVariable;

class EnumTest1 {

	EnumVariable var;
	
	EnumTest1(){
		var = new EnumVariable(new EnumType("fd1", "fd2"), 0);
	}
	
	public void run() {
		var.setValue(new Integer(5));
		System.out.println (var.getValue());
	}
}

public class EnumVariableTest {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		new EnumTest1().run();
	}

}
