/**
 * 
 */
package es.ull.iis.simulation.model.flow;

import es.ull.iis.simulation.model.ElementInstance;
import es.ull.iis.simulation.model.Simulation;

/**
 * A multiple successor flow which creates a new element instance per outgoing branch.
 * Meets the Parallel Split pattern (WFP2) 
 * @author Iván Castilla Rodríguez
 */
public class ParallelFlow extends MultipleSuccessorFlow {

	/**
	 * Creates a new ParallelFlow
	 * @param model The simulation model this flow belongs to
	 */
	public ParallelFlow(final Simulation model) {
		super(model);
	}

	@Override
	public void next(final ElementInstance ei) {
		super.next(ei);
		if (successorList.size() > 0)
			for(Flow succ : successorList)
				ei.getElement().addRequestEvent(succ, ei.getSubsequentElementInstance(ei.isExecutable(), this, ei.getToken()));
        ei.notifyEnd();
	}
}
