package es.ull.iis.simulation.condition;

import es.ull.iis.simulation.model.ElementInstance;

/**
 * A logical condition which is used for creating restrictions or
 * uncertain situations. A {@link Condition} is "checked" by using the 
 * {@link #check(ElementInstance)} method and returns <tt>true</tt> if the condition is
 * satisfied and <tt>false</tt> otherwise.
 * @author Yeray Callero
 */

public class Condition {
	
	/** 
	 * Creates a new Condition.
	 */
	public Condition(){
	}
	
	/**
	 * Checks the condition to obtain the result of the logical operation.
	 * @param fe FlowExecutor which want to check the condition.
	 * @return The boolean result of the logical operation (<tt>true</tt> by default).
	 */
	public boolean check(ElementInstance fe) {
		return true;
	}
	
}
