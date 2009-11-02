/**
 * 
 */
package es.ull.isaatc.simulation.common.flow;


/**
 * A merge flow which allows only one of the incoming branches to pass. Which one
 * passes depends on the <code>acceptValue</code>.
 * @author Iv�n Castilla Rodr�guez
 */
public interface ANDJoinFlow extends MergeFlow {
	/**
	 * @return the acceptValue
	 */
	public int getAcceptValue();
	
}
