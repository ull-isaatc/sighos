/**
 * 
 */
package es.ull.isaatc.simulation.model.flow;

import java.util.Collection;

import es.ull.isaatc.simulation.common.Activity;
import es.ull.isaatc.simulation.model.Model;

/**
 * A structured flow which contains a set of activities which must be performed according to a
 * predefined set of partial orderings. Partial orderings are defined using a collection of activity 
 * arrays. Each array [A1, A2, ... An] defines precedence relations; thus A1 must be excuted before
 * A2, A2 before A3 and so on. </br>If all the activities are presential, meets the Interleaved Parallel 
 * Routing pattern (WFP17).
 * @author Iván Castilla Rodríguez
 *
 */
public class InterleavedParallelRoutingFlow extends StructuredFlow implements es.ull.isaatc.simulation.common.flow.InterleavedParallelRoutingFlow {
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
	public InterleavedParallelRoutingFlow(Model model, Collection<Activity> acts, Collection<Activity[]> dependencies) {
		super(model);
		this.acts = acts;
		this.dependencies = dependencies;
	}
	
	/**
	 * @return the acts
	 */
	public Collection<Activity> getActivities() {
		return acts;
	}
	
	/**
	 * @return the dependencies
	 */
	public Collection<Activity[]> getDependencies() {
		return dependencies;
	}
	
}
