/**
 * 
 */
package es.ull.isaatc.simulation.common.flow;

import java.util.Collection;

import es.ull.isaatc.simulation.common.Activity;

/**
 * A {@link StructuredFlow} which contains a set of activities which must be performed according to a
 * predefined set of partial orderings. Partial orderings are defined using a collection of 
 * {@link Activity} arrays. Each array [A1, A2, ... An] defines precedence relations; thus A1 must be 
 * executed before A2, A2 before A3, and so on. </br>
 * If all the activities are presential, meets the Interleaved Parallel Routing pattern (WFP17).
 * @author Iván Castilla Rodríguez
 *
 */
public interface InterleavedParallelRoutingFlow extends StructuredFlow {
	/**
	 * Returns the collection of activities included in this structure.
	 * @return The collection of activities included in this structure
	 */
	public Collection<Activity> getActivities();
	
	/**
	 * Returns the partial orderings defined for this structure. Partial orderings are defined using 
	 * a collection of {@link Activity} arrays. Each array [A1, A2, ... An] defines precedence relations; 
	 * thus A1 must be executed before A2, A2 before A3, and so on. 
	 * @return The partial orderings defined for this structure
	 */
	public Collection<Activity[]> getDependencies();
	
}
