/**
 * 
 */
package es.ull.isaatc.simulation.core.flow;

/**
 * A {@link Flow} with a unique successor.
 * @author Iv�n Castilla Rodr�guez
 */
public interface SingleSuccessorFlow extends Flow {
	/**
	 * Returns this {@link Flow}'s unique successor. 
	 * @return This {@link Flow}'s unique successor
	 */
	public Flow getSuccessor();
	
}
