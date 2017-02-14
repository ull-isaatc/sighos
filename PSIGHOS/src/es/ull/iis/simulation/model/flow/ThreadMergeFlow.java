/**
 * 
 */
package es.ull.iis.simulation.model.flow;

/**
 * A flow which merges a specified amount of work threads. It should be used with
 * its counterpart, the Thread Split pattern (WFP 42).
 * Meets the Thread Merge pattern (WFP 41), but has also extra features. Works as
 * a thread discriminator, if <code>acceptValue</code> is set to 1; or as a thread 
 * partial join if any other value greater than one and lower than <code>nInstances</code> 
 * is used.
 * @author Iván Castilla Rodríguez
 *
 */
public class ThreadMergeFlow extends ANDJoinFlow {
	
	/**
	 * Creates a new thread merge flow
	 * @param nInstances Number of threads this flow waits for merging
	 */
	public ThreadMergeFlow(int nInstances) {
		super();
		incomingBranches = nInstances;
		acceptValue = nInstances;
	}
	
	/**
	 * Creates a new thread merge flow
	 * @param nInstances Number of threads this flow waits for resetting
	 * @param acceptValue Number of threads this flow waits for passing the control
	 */
	public ThreadMergeFlow(int nInstances, int acceptValue) {
		super();
		this.incomingBranches = nInstances;
		this.acceptValue = acceptValue;
	}

	@Override
	public void addPredecessor(Flow predecessor) {
	}

}
