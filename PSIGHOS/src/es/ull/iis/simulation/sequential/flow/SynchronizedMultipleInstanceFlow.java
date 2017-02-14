/**
 * 
 */
package es.ull.iis.simulation.sequential.flow;

import es.ull.iis.simulation.sequential.Simulation;
import es.ull.iis.simulation.sequential.WorkThread;

/**
 * Meets the Multiple Instances with a Priori Design-Time Knowledge pattern (WFP13)
 * @author Iván Castilla Rodríguez
 *
 */
public class SynchronizedMultipleInstanceFlow extends StaticPartialJoinMultipleInstancesFlow implements es.ull.iis.simulation.core.flow.SynchronizedMultipleInstanceFlow {

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
