/**
 * 
 */
package es.ull.iis.simulation.model.flow;

import es.ull.iis.simulation.model.FlowExecutor;
import es.ull.iis.simulation.model.Model;


/**
 * Creates an OR flow which allows all the true incoming branches to pass. 
 * Meets the Multi-Merge pattern (WFP8).
 * @author Iv�n Castilla Rodr�guez
 */
public class MultiMergeFlow extends ORJoinFlow {

	/**
	 * Creates a new MultiMergeFlow.
	 */
	public MultiMergeFlow(Model model) {
		super(model);
	}
	
	/* (non-Javadoc)
	 * @see es.ull.iis.simulation.MergeFlow#canPass(es.ull.iis.simulation.FlowExecutor)
	 */
	@Override
	protected boolean canPass(FlowExecutor wThread) {
		return wThread.isExecutable();
	}
}