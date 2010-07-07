/**
 * 
 */
package es.ull.isaatc.simulation.sequential.flow;

import java.util.Set;

import es.ull.isaatc.simulation.sequential.Simulation;
import es.ull.isaatc.simulation.sequential.WorkThread;

/**
 * A flow with a unique successor.
 * @author Iv�n Castilla Rodr�guez
 */
public abstract class SingleSuccessorFlow extends BasicFlow implements es.ull.isaatc.simulation.common.flow.SingleSuccessorFlow {
	/** The unique successor of this flow */
	protected Flow successor;

	/**
	 * Creates a new unique successor flow.
	 * @param simul The simulation this flow belongs to.
	 */
	public SingleSuccessorFlow(Simulation simul) {
		super(simul);
	}
	
	/**
	 * If this flow has a valid successor, requests this successor passing
	 * the same work thread. If not, the work thread finishes here, and, 
	 * if this flow has a valid parent, it's notified that this flow finished.
	 * @param wThread  
	 */
	@Override
	public void next(WorkThread wThread) {
		super.next(wThread);
		if (successor != null)
			// FIXME: I'm creating a new event. This is logically correct, but in terms of efficiency it should be better to invoke the method directly.
			// The same can be applied to every single successor flow
			wThread.getElement().addRequestEvent(successor, wThread);
		else {
			wThread.notifyEnd();
		}
	}
	
	public void setRecursiveStructureLink(es.ull.isaatc.simulation.common.flow.StructuredFlow parent, Set<es.ull.isaatc.simulation.common.flow.Flow> visited) {
		setParent(parent);
		visited.add(this);
		if (successor != null)
			if (!visited.contains(successor))
				successor.setRecursiveStructureLink(parent, visited);			
	}	

	public void link(es.ull.isaatc.simulation.common.flow.Flow succ) {
		successor = (Flow) succ;
		succ.addPredecessor(this);
	}

	/**
	 * @return the successor
	 */
	public Flow getSuccessor() {
		return successor;
	}
	
}
