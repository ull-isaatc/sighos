/**
 * 
 */
package es.ull.iis.simulation.parallel.flow;

import es.ull.iis.simulation.parallel.Simulation;
import es.ull.iis.simulation.parallel.FlowExecutor;

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
	protected boolean canPass(FlowExecutor wThread) {
		return wThread.isExecutable();
	}

}
