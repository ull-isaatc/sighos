package es.ull.isaatc.simulation.condition;

import es.ull.isaatc.simulation.Element;

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
	 * Check the associated condition and return the contrary value.
	 * @param e Element which want to check the condition.
	 * @return The contary of the associated Condition's result.
	 */
	public boolean check(Element e) {
		return !cond.check(e);
	}

	/**
	 * Obtain the associated Condition.
	 * @return The associated Condition.
	 */
	public Condition getCond() {
		return cond;
	}

	/**
	 * Set a new associated Condition.
	 * @param cond The new associated Condition.
	 */
	public void setCond(Condition cond) {
		this.cond = cond;
	}

}
