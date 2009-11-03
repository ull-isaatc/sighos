/**
 * 
 */
package es.ull.isaatc.simulation.model.flow;

import es.ull.isaatc.simulation.model.Model;

/**
 * Meets the Multiple Instances with a Priori Design-Time Knowledge pattern (WFP13)
 * @author Iván Castilla Rodríguez
 *
 */
public class SynchronizedMultipleInstanceFlow extends StaticPartialJoinMultipleInstancesFlow implements es.ull.isaatc.simulation.common.flow.SynchronizedMultipleInstanceFlow {

	/**
	 * Creates a Synchronized Multiple Instances flow
	 * @param model Model this flow belongs to.
	 * @param nInstances The number of thread instances this flow creates and which must 
	 * finish to pass the control
	 */
	public SynchronizedMultipleInstanceFlow(Model model, int nInstances) {
		super(model, nInstances, nInstances);
	}


}
