/**
 * 
 */
package es.ull.isaatc.simulation.model.flow;

import es.ull.isaatc.simulation.model.Model;


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
public class ThreadMergeFlow extends ANDJoinFlow implements es.ull.isaatc.simulation.common.flow.ThreadMergeFlow {
	
	/**
	 * Creates a new thread merge flow
	 * @param model Model this flow belongs to
	 * @param nInstances Number of threads this flow waits for merging
	 */
	public ThreadMergeFlow(Model model, int nInstances) {
		super(model);
		incomingBranches = nInstances;
		acceptValue = nInstances;
	}
	
	/**
	 * Creates a new thread merge flow
	 * @param model Model this flow belongs to
	 * @param nInstances Number of threads this flow waits for resetting
	 * @param acceptValue Number of threads this flow waits for passing the control
	 */
	public ThreadMergeFlow(Model model, int nInstances, int acceptValue) {
		super(model);
		this.incomingBranches = nInstances;
		this.acceptValue = acceptValue;
	}
	
	/* (non-Javadoc)
	 * @see es.ull.isaatc.simulation.Flow#addPredecessor(es.ull.isaatc.simulation.Flow)
	 */
	public void addPredecessor(Flow predecessor) {
	}

}
