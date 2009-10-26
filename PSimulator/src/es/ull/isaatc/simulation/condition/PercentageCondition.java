package es.ull.isaatc.simulation.condition;


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
}
