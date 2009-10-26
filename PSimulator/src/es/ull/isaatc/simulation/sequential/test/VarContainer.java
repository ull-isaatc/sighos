package es.ull.isaatc.simulation.sequential.test;

import java.util.Map;
import java.util.TreeMap;


public class VarContainer {

	public Integer integer1;
	public Map<String, Integer> integer2;
	int counter = 0;

	public VarContainer(){
		integer1 = new Integer(0);
		integer2 = new TreeMap<String, Integer>();
		integer2.put("I", new Integer(1));
	}

	public Double getVar(String name) {

		int value = 0;
		for (int i = 0; i < name.length(); i++) {
			value += Character.getNumericValue(name.charAt(i));
		}
		switch (Integer.valueOf(value)) {
		case 142 : return new Double(integer1);
		default: return null;
		}	
	}

	public Integer getInteger1() {
		return integer1;
	}

	public void setInteger1(Integer integer1) {
		this.integer1 = integer1;
	}

}

