/**
 * 
 */
package es.ull.isaatc.simulation.common.flow;



/**
 * A flow which creates several instances of the current work thread. It physically
 * works as a single successor flow, but functionally as a parallel flow. It should
 * be use with its counterpart Thread Merge pattern (WFP 41).
 * Meets the Thread Split pattern (WFP 42).
 * @author Iván Castilla Rodríguez
 *
 */
public interface ThreadSplitFlow extends SplitFlow {
	/**
	 * @return the nInstances
	 */
	public int getNInstances();
}
