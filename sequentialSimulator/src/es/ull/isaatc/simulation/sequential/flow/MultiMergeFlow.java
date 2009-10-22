/**
 * 
 */
package es.ull.isaatc.simulation.sequential.flow;

import es.ull.isaatc.simulation.sequential.Simulation;
import es.ull.isaatc.simulation.sequential.WorkThread;

/**
 * Creates an OR flow which allows all the true incoming branches to pass. 
 * Meets the Multi-Merge pattern (WFP8).
 * @author Iván Castilla Rodríguez
 */
public class MultiMergeFlow extends ORJoinFlow {

	/**
	 * Creates a new MultiMergeFlow.
	 * @param simul Simulation this flow belongs to
	 */
	public MultiMergeFlow(Simulation simul) {
		super(simul);
	}
	
	/* (non-Javadoc)
	 * @see es.ull.isaatc.simulation.MergeFlow#canPass(es.ull.isaatc.simulation.WorkThread)
	 */
	@Override
	protected boolean canPass(WorkThread wThread) {
		return wThread.isExecutable();
	}

}
