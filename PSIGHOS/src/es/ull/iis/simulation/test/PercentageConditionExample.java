package es.ull.iis.simulation.test;

import es.ull.iis.simulation.condition.PercentageCondition;
import es.ull.iis.simulation.model.ElementInstance;

public class PercentageConditionExample {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		int acceptedCounter = 0;
		int totalCounter = 10000;
		
		PercentageCondition cond = new PercentageCondition(75);
		ElementInstance e = null;
		
		for (int i = 0; i < totalCounter; i++) {
			if (cond.check(e))
				acceptedCounter++;
		}
		
		System.out.println("Se han realizado " + totalCounter + " pruebas. " + acceptedCounter + " han sido aceptadas.");
	}

}
