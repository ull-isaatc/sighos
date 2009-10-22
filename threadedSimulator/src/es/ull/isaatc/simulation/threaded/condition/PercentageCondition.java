package es.ull.isaatc.simulation.threaded.condition;

import es.ull.isaatc.simulation.threaded.BasicElement;

/**
 * Class used to simulate probability's conditions.
 * @author ycallero
 *
 */
public class PercentageCondition extends Condition {

	/** Pribability of success */
	double percentage;
	
	/**
	 * Create a new PercentageCondition.
	 * @param id Identifier
	 * @param percentage Percentage of success
	 */
	public PercentageCondition (double percentage) {
		this.percentage = percentage / 100;
	}
	
	/**
	 * This function calculates a random number. If that number is
	 * between 0 and percentage of success, the function returns true.
	 * @param e Element which want to check the condition.
	 * @return Return true dependeing on the percentage of success. 
	 */
	public boolean check(BasicElement e) {
		double randomProb = Math.random();
		if (randomProb < percentage)
			return true;
		return false;
	}

}
