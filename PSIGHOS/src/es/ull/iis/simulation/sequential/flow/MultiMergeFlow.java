/**
 * 
 */
package es.ull.iis.simulation.sequential.flow;

import es.ull.iis.simulation.sequential.Simulation;
import es.ull.iis.simulation.sequential.WorkThread;

/**
 * Creates an OR flow which allows all the true incoming branches to pass. 
 * Meets the Multi-Merge pattern (WFP8).
 * @author Iván Castilla Rodríguez
 */
public class MultiMergeFlow extends ORJoinFlow implements es.ull.iis.simulation.core.flow.MultiMergeFlow {

	/**
	 * Creates a new MultiMergeFlow.
	 * @param simul Simulation this flow belongs to
	 */
	public MultiMergeFlow(Simulation simul) {
		super(simul);
	}
	
	/* (non-Javadoc)
	 * @see es.ull.iis.simulation.MergeFlow#canPass(es.ull.iis.simulation.WorkThread)
	 */
	@Override
	protected boolean canPass(WorkThread wThread) {
		return wThread.isExecutable();
	}

}
