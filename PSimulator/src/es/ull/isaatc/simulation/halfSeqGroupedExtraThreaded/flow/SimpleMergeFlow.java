package es.ull.isaatc.simulation.halfSeqGroupedExtraThreaded.flow;

import java.util.Collections;
import java.util.Map;
import java.util.TreeMap;

import es.ull.isaatc.simulation.halfSeqGroupedExtraThreaded.Element;
import es.ull.isaatc.simulation.halfSeqGroupedExtraThreaded.Simulation;
import es.ull.isaatc.simulation.halfSeqGroupedExtraThreaded.WorkThread;


/**
 * Creates an OR flow which allows all the true incoming branches to pass. The 
 * outgoing branch is activated only once when several incoming barnches arrive at
 * the same simulation time. 
 * Meets the Simple Merge pattern (WFP5).
 * @author ycallero
 *
 */
public class SimpleMergeFlow extends ORJoinFlow implements es.ull.isaatc.simulation.common.flow.SimpleMergeFlow {
	protected Map<Element, Long> lastTs;
	
	/**
	 * Creates a new SimpleMergeFlow.
	 * @param simul Simulation this flow belongs to.
	 */
	public SimpleMergeFlow(Simulation simul) {
		super(simul);
		lastTs = Collections.synchronizedSortedMap(new TreeMap<Element, Long>());
	}

	@Override
	protected boolean canPass(WorkThread wThread) {
		if (!lastTs.containsKey(wThread.getElement())) {
			lastTs.put(wThread.getElement(), (long)-1);
		}
		if (wThread.isExecutable() && (wThread.getElement().getTs() > lastTs.get(wThread.getElement()))) {
			lastTs.put(wThread.getElement(), wThread.getElement().getTs());
			return true;
		}
		return false;
	}
	
	@Override
	protected void reset(WorkThread wThread) {
		lastTs.remove(wThread.getElement());
		super.reset(wThread);
	}
}
