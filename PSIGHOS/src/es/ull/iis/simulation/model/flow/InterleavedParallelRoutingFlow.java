/**
 * 
 */
package es.ull.iis.simulation.model.flow;

import java.util.ArrayList;
import java.util.Collection;
import java.util.TreeMap;

import es.ull.iis.simulation.model.ElementInstance;
import es.ull.iis.simulation.model.Simulation;


/**
 * A structured flow which contains a set of activities which must be performed according to a
 * predefined set of partial orderings. Partial orderings are defined using a collection of activity 
 * arrays. Each array [A1, A2, ... An] defines precedence relations; thus A1 must be excuted before
 * A2, A2 before A3 and so on. </br>If all the activities are presential, meets the Interleaved Parallel 
 * Routing pattern (WFP17).
 * @author Iván Castilla Rodríguez
 *
 */
public class InterleavedParallelRoutingFlow extends StructuredFlow {
	protected Collection<ActivityFlow> acts;
	protected Collection<ActivityFlow[]> dependencies;
	/**
	 * Creates a flow which contains a set of activities that must be performed according to a
	 * predefined set of partial orderings.
	 * @param acts The set of activities
	 * @param dependencies A set of activity arrays, so that each array indicates precedence relations
	 * among the activities.
	 */
	public InterleavedParallelRoutingFlow(Simulation model, Collection<ActivityFlow> acts, Collection<ActivityFlow[]> dependencies) {
		super(model);
		initialFlow = new ParallelFlow(model);
		initialFlow.setParent(this);
		finalFlow = new SynchronizationFlow(model);
		finalFlow.setParent(this);
		
		this.acts = acts;
		this.dependencies = dependencies;
		
		TreeMap<ActivityFlow, Flow> succLink = new TreeMap<ActivityFlow, Flow>();
		TreeMap<ActivityFlow, Flow> predLink = new TreeMap<ActivityFlow, Flow>();
		// Counts predecessors and successors
		for (ActivityFlow[] list : dependencies) {
			for (int i = 0; i < list.length - 1; i++) {
				ActivityFlow pred = list[i];
				ActivityFlow succ = list[i + 1];
					
				Flow succFlow = succLink.get(pred);
				// If it has no successor, it's added as its own successor
				if (succFlow == null) 
					succLink.put(pred, pred);
				// If it already has a single successor, a parallel flow is added as successor
				else if (succFlow instanceof ActivityFlow) {
					succLink.put(pred, new ParallelFlow(model));
					pred.link(succLink.get(pred));
				}
				// else, the parallel flow is kept as the successor

				Flow predFlow = predLink.get(succ);
				// If it has no predecessor, it's added as its own predecessor
				if (predFlow == null) 
					predLink.put(succ, succ);
				// If it already has a single predecessor, a sync flow is added as predecessor
				else if (predFlow instanceof ActivityFlow) {
					predLink.put(succ, new SynchronizationFlow(model));
					predLink.get(succ).link(succ);
				}
			}
		}
		
		// Builds the dependencies graph
		for (ActivityFlow[] list : dependencies) {
			for (int i = 0; i < list.length - 1; i++) {
				ActivityFlow pred = list[i];
				ActivityFlow succ = list[i + 1];
				succLink.get(pred).link(predLink.get(succ));
			}
		}
		
		// Links the remainder activities to the initial and final flow
		for (ActivityFlow f : acts) {
			if (!predLink.containsKey(f))
				initialFlow.link(f);
			if (!succLink.containsKey(f))
				f.link(finalFlow);
		}
	}
	
	/**
	 * Returns the collection of activities included in this structure.
	 * @return The collection of activities included in this structure
	 */
	public Collection<ActivityFlow> getActivities() {
		ArrayList<ActivityFlow> temp = new ArrayList<ActivityFlow>();
		for (ActivityFlow a : acts)
			temp.add(a);
		return temp;
	}

	/**
	 * Returns the partial orderings defined for this structure. Partial orderings are defined using 
	 * a collection of {@link ActivityFlow} arrays. Each array [A1, A2, ... An] defines precedence relations; 
	 * thus A1 must be executed before A2, A2 before A3, and so on. 
	 * @return The partial orderings defined for this structure
	 */
	public Collection<ActivityFlow[]> getDependencies() {
		ArrayList<ActivityFlow[]> temp = new ArrayList<ActivityFlow[]>();
		for (ActivityFlow[] dep : dependencies)
			temp.add(dep);
		return temp;
	}

	/* (non-Javadoc)
	 * @see es.ull.iis.simulation.TaskFlow#finish(es.ull.iis.simulation.FlowExecutor)
	 */
	public void finish(ElementInstance wThread) {
		afterFinalize(wThread);
		next(wThread);

	}

}
