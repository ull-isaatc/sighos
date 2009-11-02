/**
 * 
 */
package es.ull.isaatc.simulation.common.flow;

/**
 * A flow with a unique successor.
 * @author Iván Castilla Rodríguez
 */
public interface SingleSuccessorFlow extends Flow {
	public Flow getSuccessor();
	
}
