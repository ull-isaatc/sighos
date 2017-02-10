/**
 * 
 */
package es.ull.iis.simulation.sequential.flow;

import java.util.Set;

import es.ull.iis.simulation.core.flow.Flow;
import es.ull.iis.simulation.sequential.Simulation;
import es.ull.iis.simulation.sequential.WorkThread;

/**
 * A flow with a unique successor.
 * @author Iv�n Castilla Rodr�guez
 */
public abstract class SingleSuccessorFlow extends BasicFlow implements es.ull.iis.simulation.core.flow.SingleSuccessorFlow<WorkThread> {
	/** The unique successor of this flow */
	protected Flow<WorkThread> successor;

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
	
	public void setRecursiveStructureLink(es.ull.iis.simulation.core.flow.StructuredFlow<WorkThread> parent, Set<Flow<WorkThread>> visited) {
		setParent(parent);
		visited.add(this);
		if (successor != null)
			if (!visited.contains(successor))
				successor.setRecursiveStructureLink(parent, visited);			
	}	

	public Flow<WorkThread> link(Flow<WorkThread> succ) {
		successor = (Flow<WorkThread>) succ;
		succ.addPredecessor(this);
		return succ;
	}

	/**
	 * @return the successor
	 */
	public Flow<WorkThread> getSuccessor() {
		return successor;
	}
	
}
