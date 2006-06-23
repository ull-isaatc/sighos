/**
 * 
 */
package es.ull.isaatc.simulation;

import es.ull.isaatc.simulation.results.ElementStatistics;

/**
 * @author Iván Castilla Rodríguez
 *
 */
public class InterruptedElement extends Element {

	/**
	 * @param id
	 * @param simul
	 */
	public InterruptedElement(int id, Simulation simul, ElementType et) {
		super(id, simul, et);
	}

    /**
     * The element starts requesting its activities.
     */
    protected void startEvents() {
    	simul.addStatistic(new ElementStatistics(id, ElementStatistics.START, ts, 0));
    	// There is no more to do
    }
    

}
