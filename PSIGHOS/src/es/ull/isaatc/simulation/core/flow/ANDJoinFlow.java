/**
 * 
 */
package es.ull.isaatc.simulation.core.flow;


/**
 * A {@link MergeFlow} which allows only one of the incoming branches to pass. Which one
 * passes depends on a defined acceptance value.
 * @author Iván Castilla Rodríguez
 */
public interface ANDJoinFlow extends MergeFlow {
	/**
	 * Returns the acceptance value for this flow.
	 * @return The acceptance value for this flow
	 */
	public int getAcceptValue();
	
}
