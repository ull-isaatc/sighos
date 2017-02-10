/**
 * 
 */
package es.ull.iis.simulation.sequential.flow;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.Map;
import java.util.SortedMap;
import java.util.TreeMap;

import es.ull.iis.simulation.core.flow.Flow;
import es.ull.iis.simulation.sequential.WorkThread;
import es.ull.iis.simulation.sequential.WorkToken;

/**
 * @author Iv�n Castilla Rodr�guez
 *
 */
public class GeneralizedMergeFlowControl extends MergeFlowControl {
	protected SortedMap<Flow<WorkThread>, LinkedList<WorkToken>> incBranches;

	/**
	 * @param flow
	 */
	public GeneralizedMergeFlowControl(MergeFlow flow) {
		super(flow);
		incBranches = new TreeMap<Flow<WorkThread>, LinkedList<WorkToken>>();
	}

	/* (non-Javadoc)
	 * @see es.ull.iis.simulation.MergeFlowControl#arrive(es.ull.iis.simulation.WorkThread)
	 */
	@Override
	public void arrive(WorkThread wThread) {
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
		Iterator<Map.Entry<Flow<WorkThread>, LinkedList<WorkToken>>> iter = incBranches.entrySet().iterator();
		while (iter.hasNext()) {
			Map.Entry<Flow<WorkThread>, LinkedList<WorkToken>> entry = iter.next();
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
