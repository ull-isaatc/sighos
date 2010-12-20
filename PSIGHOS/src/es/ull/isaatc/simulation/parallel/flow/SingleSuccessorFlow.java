/**
 * 
 */
package es.ull.isaatc.simulation.parallel.flow;

import java.util.Set;

import es.ull.isaatc.simulation.parallel.Simulation;
import es.ull.isaatc.simulation.parallel.WorkThread;

/**
 * A flow with a unique successor.
 * @author Iván Castilla Rodríguez
 */
public abstract class SingleSuccessorFlow extends BasicFlow implements es.ull.isaatc.simulation.flow.SingleSuccessorFlow {
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
	public void next(final WorkThread wThread) {
		super.next(wThread);
		if (successor != null) {
			wThread.requestFlow(successor);
		}
		else {
			wThread.notifyEnd();
		}
	}
	
	public void setRecursiveStructureLink(es.ull.isaatc.simulation.flow.StructuredFlow parent, Set<es.ull.isaatc.simulation.flow.Flow> visited) {
		setParent(parent);
		visited.add(this);
		if (successor != null)
			if (!visited.contains(successor))
				successor.setRecursiveStructureLink(parent, visited);			
	}	

	public void link(es.ull.isaatc.simulation.flow.Flow succ) {
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
