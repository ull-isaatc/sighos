/**
 * 
 */
package es.ull.isaatc.simulation.parallel.flow;

import es.ull.isaatc.simulation.parallel.Simulation;

/**
 * Meets the Multiple Instances with a Priori Design-Time Knowledge pattern (WFP13)
 * @author Iv�n Castilla Rodr�guez
 *
 */
public class SynchronizedMultipleInstanceFlow extends StaticPartialJoinMultipleInstancesFlow implements es.ull.isaatc.simulation.flow.SynchronizedMultipleInstanceFlow {

	/**
	 * Creates a Synchronized Multiple Instances flow
	 * @param simul Simulation this flow belongs to
	 * @param nInstances The number of thread instances this flow creates and which must 
	 * finish to pass the control
	 */
	public SynchronizedMultipleInstanceFlow(Simulation simul, int nInstances) {
		super(simul, nInstances, nInstances);
	}


}
