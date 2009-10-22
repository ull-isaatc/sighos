package es.ull.isaatc.simulation.sequential.test.condition;

import es.ull.isaatc.simulation.sequential.condition.PercentageCondition;
import es.ull.isaatc.simulation.sequential.Element;

public class PercentageConditionExample {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		int acceptedCounter = 0;
		int totalCounter = 10000;
		
		PercentageCondition cond = new PercentageCondition(75);
		Element e = null;
		
		for (int i = 0; i < totalCounter; i++) {
			if (cond.check(e))
				acceptedCounter++;
		}
		
		System.out.println("Se han realizado " + totalCounter + " pruebas. " + acceptedCounter + " han sido aceptadas.");
	}

}
