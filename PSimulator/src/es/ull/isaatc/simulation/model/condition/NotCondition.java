package es.ull.isaatc.simulation.model.condition;


/**
 * Condition used to make NOT logical operation. This NotCondition has 
 * other associated Condition and return the contrary value of the 
 * associated Condition's result.
 * @author ycallero
 *
 */
public class NotCondition extends Condition {

	/** Associated Condition */
	Condition cond;
	
	/**
	 * Create a new NotCondition
	 * @param id Identifier
	 * @param newCond Associated Condition
	 */
	public NotCondition(Condition newCond){
		cond = newCond;
	}
	
	/**
	 * Obtain the associated Condition.
	 * @return The associated Condition.
	 */
	public Condition getCond() {
		return cond;
	}
}
