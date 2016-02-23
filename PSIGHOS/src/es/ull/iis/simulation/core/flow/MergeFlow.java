package es.ull.iis.simulation.core.flow;


/**
 * A {@link JoinFlow} which merges several incoming branches into a single outgoing branch. 
 * A class implementing this interface must handle the incoming branches in an internal 
 * structure and must also manage separately each {@link es.ull.iis.simulation.core.Element}.
 * @author Yeray Callero
 */
public interface MergeFlow extends SingleSuccessorFlow, JoinFlow {
	/**
	 * Returns the safety of this flow. 
	 * @return The safety of this flow
	 */
	public boolean isSafe();

	/**
	 * Returns how many incoming branches this flow has.
	 * @return How many incoming branches this flow has
	 */
	public int getIncomingBranches();
}


