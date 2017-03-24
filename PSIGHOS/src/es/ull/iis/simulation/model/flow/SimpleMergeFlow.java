package es.ull.iis.simulation.model.flow;

import java.util.Map;
import java.util.TreeMap;

import es.ull.iis.simulation.model.Element;
import es.ull.iis.simulation.model.ElementInstance;
import es.ull.iis.simulation.model.Simulation;


/**
 * Creates an OR flow which allows all the true incoming branches to pass. The 
 * outgoing branch is activated only once when several incoming barnches arrive at
 * the same simulation time. 
 * Meets the Simple Merge pattern (WFP5).
 * @author ycallero
 *
 */
public class SimpleMergeFlow extends ORJoinFlow {
	protected Map<Element, Long> lastTs = new TreeMap<Element, Long>();
	
	/**
	 * Creates a new SimpleMergeFlow.
	 * @param simul Simulation this flow belongs to.
	 */
	public SimpleMergeFlow(Simulation model) {
		super(model);
	}

	@Override
	protected boolean canPass(ElementInstance wThread) {
		if (!lastTs.containsKey(wThread.getElement())) {
			lastTs.put(wThread.getElement(), (long)-1);
		}
		if (wThread.isExecutable() && (wThread.getTime() > lastTs.get(wThread.getElement()))) {
			lastTs.put(wThread.getElement(), (long)wThread.getTime());
			return true;
		}
		return false;
	}
	
	@Override
	protected void reset(ElementInstance wThread) {
		lastTs.remove(wThread.getElement());
		super.reset(wThread);
	}
}
