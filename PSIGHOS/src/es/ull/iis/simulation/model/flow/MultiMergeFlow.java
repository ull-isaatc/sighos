/**
 * 
 */
package es.ull.iis.simulation.model.flow;

import es.ull.iis.simulation.model.ElementInstance;
import es.ull.iis.simulation.model.Simulation;


/**
 * Creates an OR flow which allows all the true incoming branches to pass. 
 * Meets the Multi-Merge pattern (WFP8).
 * @author Iván Castilla Rodríguez
 */
public class MultiMergeFlow extends ORJoinFlow {

	/**
	 * Creates a new MultiMergeFlow.
	 */
	public MultiMergeFlow(Simulation model) {
		super(model);
	}
	
	/* (non-Javadoc)
	 * @see es.ull.iis.simulation.MergeFlow#canPass(es.ull.iis.simulation.FlowExecutor)
	 */
	@Override
	protected boolean canPass(ElementInstance wThread) {
		return wThread.isExecutable();
	}
}
