/**
 * 
 */
package es.ull.cyc.simulation;

import es.ull.cyc.simulation.results.ElementStatistics;

/**
 * @author Iván Castilla Rodríguez
 *
 */
public class InterruptedElement extends Element {

	/**
	 * @param id
	 * @param simul
	 */
	public InterruptedElement(int id, Simulation simul) {
		super(id, simul);
	}

    /**
     * The element starts requesting its activities.
     */
    protected void startEvents() {
    	simul.addStatistic(new ElementStatistics(id, ElementStatistics.START, ts, 0));
    	// There is no more to do
    }
    

}
