/**
 * 
 */
package es.ull.iis.simulation.core.flow;



/**
 * A {@link SplitFlow} which creates several instances of the current work thread. It physically
 * works as a {@link SingleSuccessorFlow}, but functionally as a {@link ParallelFlow}. It should
 * be use with its counterpart {@link ThreadMergeFlow} (WFP 41).
 * Meets the Thread Split pattern (WFP 42).
 * @author Iván Castilla Rodríguez
 *
 */
public interface ThreadSplitFlow extends SplitFlow {
	/**
	 * Returns the amount of instances to be created.
	 * @return The amount of instances to be created
	 */
	public int getNInstances();
}
