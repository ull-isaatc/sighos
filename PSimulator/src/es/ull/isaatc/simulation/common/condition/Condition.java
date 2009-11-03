package es.ull.isaatc.simulation.common.condition;

import es.ull.isaatc.simulation.common.Element;
import es.ull.isaatc.simulation.common.Model;

/**
 * A logical condition which is used for create restrictions or 
 * uncertainty situations.
 * 
 * @author ycallero
 */

public class Condition {
	
	/** Current simulation. It's used to comunicate whith simulation vars. */
	public Model simul;
	
	/** 
	 * Creates a new Condition.
	 * @param id Condition's identifier
	 */
	public Condition(){
	}
	
	/**
	 * Creates a new Condition.
	 * @param id Identifier.
	 * @param simul Actual simulation.
	 */
	public Condition(Model simul){
		this.simul = simul;
	}

	/**
	 * Check the condition to obtain the result of the logical operation.
	 * @param e Element which want to check the condition.
	 * @return The boolean result of the logical operation.
	 */
	public boolean check(Element e) {
		return true;
	}
	
	/**
	 * Returns the current simulation.
	 * @return The simulation.
	 */
	public Model getSimul() {
		return simul;
	}

	/**
	 * Set the actual simulation.
	 * @param sim Simulation.
	 */
	public void setSimul(Model sim) {
		simul = sim;
	}
}
