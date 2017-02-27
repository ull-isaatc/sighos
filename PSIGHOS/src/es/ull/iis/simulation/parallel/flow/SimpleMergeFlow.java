package es.ull.iis.simulation.parallel.flow;

import java.util.Collections;
import java.util.Map;
import java.util.TreeMap;

import es.ull.iis.simulation.parallel.Element;
import es.ull.iis.simulation.parallel.Simulation;
import es.ull.iis.simulation.parallel.FlowExecutor;


/**
 * Creates an OR flow which allows all the true incoming branches to pass. The 
 * outgoing branch is activated only once when several incoming barnches arrive at
 * the same simulation time. 
 * Meets the Simple Merge pattern (WFP5).
 * @author ycallero
 *
 */
public class SimpleMergeFlow extends ORJoinFlow implements es.ull.iis.simulation.core.flow.SimpleMergeFlow {
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
	protected boolean canPass(FlowExecutor wThread) {
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
	protected void reset(FlowExecutor wThread) {
		lastTs.remove(wThread.getElement());
		super.reset(wThread);
	}
}
