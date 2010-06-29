/**
 * 
 */
package es.ull.isaatc.simulation.groupedExtraThreaded.flow;

import es.ull.isaatc.simulation.groupedExtraThreaded.Element;
import es.ull.isaatc.simulation.groupedExtraThreaded.Simulation;
import es.ull.isaatc.simulation.groupedExtraThreaded.WorkThread;


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
	 * @param simul Simulation this flow belongs to
	 * @param nInstances Number of threads this flow waits for merging
	 */
	public ThreadMergeFlow(Simulation simul, int nInstances) {
		super(simul);
		incomingBranches = nInstances;
		acceptValue = nInstances;
	}
	
	/**
	 * Creates a new thread merge flow
	 * @param simul Simulation this flow belongs to
	 * @param nInstances Number of threads this flow waits for resetting
	 * @param acceptValue Number of threads this flow waits for passing the control
	 */
	public ThreadMergeFlow(Simulation simul, int nInstances, int acceptValue) {
		super(simul);
		this.incomingBranches = nInstances;
		this.acceptValue = acceptValue;
	}
	
	/* (non-Javadoc)
	 * @see es.ull.isaatc.simulation.Flow#addPredecessor(es.ull.isaatc.simulation.Flow)
	 */
	public void addPredecessor(Flow predecessor) {
	}

	/* (non-Javadoc)
	 * @see es.ull.isaatc.simulation.Flow#request(es.ull.isaatc.simulation.WorkThread)
	 */
	public void request(WorkThread wThread) {
		final Element elem = wThread.getElement();
		if (!wThread.wasVisited(this)) {
			if (wThread.isExecutable()) {
				if (!beforeRequest(elem))
					wThread.setExecutable(false, this);
				
				elem.waitProtectedFlow(this);
				arrive(wThread);
				if (canPass(wThread)) {
					control.get(elem).setActivated();
					next(wThread);
				}
				else {
					// If no one of the branches was true, the thread of control must continue anyway
					if (canReset(wThread) && !isActivated(wThread))
						next(wThread.getInstanceSubsequentWorkThread(false, this, control.get(elem).getOutgoingFalseToken()));
					wThread.notifyEnd();
				}
				if (canReset(wThread))
					reset(wThread);
				elem.signalProtectedFlow(this);
			}
		} else
			wThread.notifyEnd();
	}

}
