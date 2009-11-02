/**
 * 
 */
package es.ull.isaatc.simulation.common.flow;

/**
 * A flow with a unique successor.
 * @author Iv�n Castilla Rodr�guez
 */
public interface SingleSuccessorFlow extends Flow {
	public Flow getSuccessor();
	
}
