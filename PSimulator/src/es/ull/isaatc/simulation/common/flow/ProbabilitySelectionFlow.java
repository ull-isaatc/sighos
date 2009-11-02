/**
 * 
 */
package es.ull.isaatc.simulation.common.flow;

import java.util.Collection;

/**
 * A flow which selects one outgoing branch among a set of them by using a probability value.
 * Each outgoing branch has a value (0.0 - 1.0] expressing the probability of being chosen of
 * such branch. 
 * @author Iván Castilla Rodríguez
 *
 */
public interface ProbabilitySelectionFlow extends MultipleSuccessorFlow {
	/**
	 * Adds a probabilistic flow's successor.
	 * @param successor This flow's successor
	 * @param prob The probability of this successor of being chosen
	 */
	public void link(Flow successor, double prob);

	/**
	 * Adds a collection of probabilistic flow's successor. 
	 * Size of <code>succList</code> and <code>probList</code> must agree.
	 * @param succList This flow's successors
	 * @param probList The probability of these successors of being chosen
	 */
	public void link(Collection<Flow> succList, Collection<Double> probList);
	
}
