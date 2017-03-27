/**
 * 
 */
package es.ull.iis.simulation.model.flow;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.Map;

import es.ull.iis.simulation.model.ElementInstance;
import es.ull.iis.simulation.model.WorkToken;

/**
 * @author Iván Castilla Rodríguez
 *
 */
public class GeneralizedMergeFlowControl extends MergeFlowControl {
	protected Map<Flow, LinkedList<WorkToken>> incBranches;

	/**
	 * @param flow
	 */
	public GeneralizedMergeFlowControl(MergeFlow flow, Map<Flow, LinkedList<WorkToken>> control) {
		super(flow);
		incBranches = control;
	}

	/* (non-Javadoc)
	 * @see es.ull.iis.simulation.MergeFlowControl#arrive(es.ull.iis.simulation.FlowExecutor)
	 */
	@Override
	public void arrive(ElementInstance wThread) {
		// New incoming branch
		if (!incBranches.containsKey(wThread.getLastFlow())) {
			incBranches.put(wThread.getLastFlow(), new LinkedList<WorkToken>());
			if (wThread.isExecutable())
				trueChecked++;
			else
				outgoingFalseToken.addFlow(wThread.getToken().getPath());
		}
		// The new incoming branch is added
		incBranches.get(wThread.getLastFlow()).add(wThread.getToken());
	}

	/* (non-Javadoc)
	 * @see es.ull.iis.simulation.MergeFlowControl#canReset(int)
	 */
	@Override
	public boolean canReset(int checkValue) {
		return (incBranches.size() == checkValue);
	}

	@Override
	public boolean reset() {
		super.reset();
		Iterator<Map.Entry<Flow, LinkedList<WorkToken>>> iter = incBranches.entrySet().iterator();
		while (iter.hasNext()) {
			Map.Entry<Flow, LinkedList<WorkToken>> entry = iter.next();
			entry.getValue().removeFirst();
			if (!entry.getValue().isEmpty()) {
				WorkToken token = entry.getValue().peek();
				if (token.isExecutable())
					trueChecked++;
				else
					outgoingFalseToken.addFlow(token.getPath());
			}
			else
				iter.remove();
		}
		return incBranches.isEmpty();
	}
}
