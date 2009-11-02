/**
 * 
 */
package es.ull.isaatc.simulation.common.flow;

import java.util.Collection;

import es.ull.isaatc.simulation.common.Activity;

/**
 * A structured flow which contains a set of activities which must be performed according to a
 * predefined set of partial orderings. Partial orderings are defined using a collection of activity 
 * arrays. Each array [A1, A2, ... An] defines precedence relations; thus A1 must be excuted before
 * A2, A2 before A3 and so on. </br>If all the activities are presential, meets the Interleaved Parallel 
 * Routing pattern (WFP17).
 * @author Iván Castilla Rodríguez
 *
 */
public interface InterleavedParallelRoutingFlow extends StructuredFlow {
	/**
	 * @return the acts
	 */
	public Collection<Activity> getActivities();
	
	/**
	 * @return the dependencies
	 */
	public Collection<Activity[]> getDependencies();
	
}
