package es.ull.iis.simulation.condition;

import es.ull.iis.simulation.core.Element;

/**
 * Condition used to build NOT logical operations. This NotCondition 
 * returns <tt>true</tt> if the associated {@link Condition} returns <tt>false</tt>,
 * and vice versa.
 * @author Yeray Callero
 *
 */
public final class NotCondition extends Condition {
	/** Associated Condition */
	final private Condition cond;
	
	/**
	 * Create a new NotCondition
	 * @param newCond Associated Condition
	 */
	public NotCondition(Condition newCond){
		cond = newCond;
	}
	
	/**
	 * Checks the associated condition and return the negated value.
	 * @param e Element used to check the condition.
	 * @return The negated result of the associated Condition
	 */
	public boolean check(Element<?> e) {
		return !cond.check(e);
	}

	/**
	 * Returns the associated Condition.
	 * @return The associated Condition.
	 */
	public Condition getCond() {
		return cond;
	}

}
