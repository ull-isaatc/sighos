/**
 * 
 */
package es.ull.iis.simulation.parallel.flow;

import java.util.ArrayList;
import java.util.Collection;
import java.util.TreeMap;

import es.ull.iis.simulation.parallel.Activity;
import es.ull.iis.simulation.parallel.Simulation;
import es.ull.iis.simulation.parallel.WorkThread;

/**
 * A structured flow which contains a set of activities which must be performed according to a
 * predefined set of partial orderings. Partial orderings are defined using a collection of activity 
 * arrays. Each array [A1, A2, ... An] defines precedence relations; thus A1 must be excuted before
 * A2, A2 before A3 and so on. </br>If all the activities are presential, meets the Interleaved Parallel 
 * Routing pattern (WFP17).
 * @author Iván Castilla Rodríguez
 *
 */
public class InterleavedParallelRoutingFlow extends StructuredFlow implements es.ull.iis.simulation.core.flow.InterleavedParallelRoutingFlow {
	protected Collection<Activity> acts;
	protected Collection<Activity[]> dependencies;

	/**
	 * Creates a flow which contains a set of activities that must be performed according to a
	 * predefined set of partial orderings.
	 * @param simul The simulation this flow belongs to.
	 * @param acts The set of activities
	 * @param dependencies A set of activity arrays, so that each array indicates precedence relations
	 * among the activities.
	 */
	public InterleavedParallelRoutingFlow(Simulation simul, Collection<Activity> acts, Collection<Activity[]> dependencies) {
		super(simul);
		initialFlow = new ParallelFlow(simul);
		initialFlow.setParent(this);
		finalFlow = new SynchronizationFlow(simul);
		finalFlow.setParent(this);
		
		this.acts = acts;
		this.dependencies = dependencies;
		// Sets the corresponding single flows
		TreeMap<Activity, SingleFlow> fMap = new TreeMap<Activity, SingleFlow>();
		for (Activity a : acts)
			fMap.put(a, new SingleFlow(simul, a));
		
		TreeMap<SingleFlow, Flow> succLink = new TreeMap<SingleFlow, Flow>();
		TreeMap<SingleFlow, Flow> predLink = new TreeMap<SingleFlow, Flow>();
		// Counts predecessors and successors
		for (Activity[] list : dependencies) {
			for (int i = 0; i < list.length - 1; i++) {
				SingleFlow pred = fMap.get(list[i]);
				SingleFlow succ = fMap.get(list[i + 1]);
					
				Flow succFlow = succLink.get(pred);
				// If it has no successor, it's added as its own successor
				if (succFlow == null) 
					succLink.put(pred, pred);
				// If it already has a single successor, a parallel flow is added as successor
				else if (succFlow instanceof SingleFlow) {
					succLink.put(pred, new ParallelFlow(simul));
					pred.link(succLink.get(pred));
				}
				// else, the parallel flow is kept as the successor

				Flow predFlow = predLink.get(succ);
				// If it has no predecessor, it's added as its own predecessor
				if (predFlow == null) 
					predLink.put(succ, succ);
				// If it already has a single predecessor, a sync flow is added as predecessor
				else if (predFlow instanceof SingleFlow) {
					predLink.put(succ, new SynchronizationFlow(simul));
					predLink.get(succ).link(succ);
				}
			}
		}
		
		// Builds the dependencies graph
		for (Activity[] list : dependencies) {
			for (int i = 0; i < list.length - 1; i++) {
				SingleFlow pred = fMap.get(list[i]);
				SingleFlow succ = fMap.get(list[i + 1]);
				succLink.get(pred).link(predLink.get(succ));
			}
		}
		
		// Links the remainder activities to the initial and final flow
		for (SingleFlow f : fMap.values()) {
			if (!predLink.containsKey(f))
				initialFlow.link(f);
			if (!succLink.containsKey(f))
				f.link(finalFlow);
		}
	}
	
	/* (non-Javadoc)
	 * @see es.ull.iis.simulation.TaskFlow#finish(es.ull.iis.simulation.WorkThread)
	 */
	public void finish(WorkThread wThread) {
		afterFinalize(wThread.getElement());
		next(wThread);

	}

	/* (non-Javadoc)
	 * @see es.ull.iis.simulation.Flow#request(es.ull.iis.simulation.WorkThread)
	 */
	public void request(WorkThread wThread) {
		if (!wThread.wasVisited(this)) {
			if (wThread.isExecutable()) {
				if (beforeRequest(wThread.getElement()))
					wThread.getInstanceDescendantWorkThread().requestFlow(initialFlow);
				else {
					wThread.cancel(this);
					next(wThread);				
				}
			}
			else {
				wThread.updatePath(this);
				next(wThread);
			}
		} else
			wThread.notifyEnd();
	}

	@Override
	public Collection<es.ull.iis.simulation.core.flow.ActivityFlow> getActivities() {
		ArrayList<es.ull.iis.simulation.core.flow.ActivityFlow> temp = new ArrayList<es.ull.iis.simulation.core.flow.ActivityFlow>();
		for (Activity a : acts)
			temp.add(a);
		return temp;
	}

	@Override
	public Collection<es.ull.iis.simulation.core.flow.ActivityFlow[]> getDependencies() {
		ArrayList<es.ull.iis.simulation.core.flow.ActivityFlow[]> temp = new ArrayList<es.ull.iis.simulation.core.flow.ActivityFlow[]>();
		for (Activity[] dep : dependencies)
			temp.add(dep);
		return temp;
	}

}
