/**
 * 
 */
package es.ull.iis.simulation.model.flow;

import es.ull.iis.simulation.model.Simulation;
import es.ull.iis.simulation.model.Element;
import es.ull.iis.simulation.model.ElementInstance;


/**
 * A flow which merges a specified amount of element instances. It should be used with
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
	 * @param model The simulation model this flow belongs to
	 * @param nInstances Number of threads this flow waits for merging
	 */
	public ThreadMergeFlow(final Simulation model, final int nInstances) {
		super(model);
		incomingBranches = nInstances;
		acceptValue = nInstances;
	}
	
	/**
	 * Creates a new thread merge flow
	 * @param nInstances Number of threads this flow waits for resetting
	 * @param acceptValue Number of threads this flow waits for passing the control
	 */
	public ThreadMergeFlow(final Simulation model, final int nInstances, final int acceptValue) {
		super(model);
		this.incomingBranches = nInstances;
		this.acceptValue = acceptValue;
	}

	@Override
	public void addPredecessor(final Flow predecessor) {
	}

	@Override
	public void request(final ElementInstance ei) {
		final Element elem = ei.getElement();
		if (!ei.wasVisited(this)) {
			if (ei.isExecutable()) {
				if (!beforeRequest(ei))
					ei.cancel(this);
				elem.getEngine().waitProtectedFlow(this);
				arrive(ei);
				if (canPass(ei)) {
					control.get(elem).setActivated();
					next(ei);
				}
				else {
					// If no one of the branches was true, the thread of control must continue anyway
					if (canReset(ei) && !isActivated(ei))
						next(ei.getSubsequentElementInstance(false, this, control.get(elem).getOutgoingFalseToken()));
					ei.notifyEnd();
				}
				if (canReset(ei))
					reset(ei);
				elem.getEngine().signalProtectedFlow(this);
			}
		} else
			ei.notifyEnd();
	}
}
