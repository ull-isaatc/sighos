package es.ull.iis.simulation.condition;

import es.ull.iis.simulation.model.Element;
import es.ull.iis.simulation.core.Simulation;

/**
 * A logical condition which is used for creating restrictions or
 * uncertain situations. A {@link Condition} is "checked" by using the 
 * {@link #check(Element)} method and returns <tt>true</tt> if the condition is
 * satisfied and <tt>false</tt> otherwise.
 * @author Yeray Callero
 */

public class Condition {
	
	/** Current simulation, which is used to communicate with simulation variables. */
	public Simulation simul;
	
	/** 
	 * Creates a new Condition.
	 */
	public Condition(){
	}
	
	/**
	 * Creates a new Condition.
	 * @param simul Current simulation.
	 */
	public Condition(Simulation simul){
		this.simul = simul;
	}

	/**
	 * Checks the condition to obtain the result of the logical operation.
	 * @param e Element which want to check the condition.
	 * @return The boolean result of the logical operation (<tt>true</tt> by default).
	 */
	public boolean check(Element e) {
		return true;
	}
	
	/**
	 * Returns the current simulation.
	 * @return The simulation.
	 */
	public Simulation getSimul() {
		return simul;
	}

	/**
	 * Sets the current simulation.
	 * @param sim Simulation.
	 */
	public void setSimul(Simulation sim) {
		simul = sim;
	}
}
