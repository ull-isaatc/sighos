/**
 * 
 */
package es.ull.iis.simulation.model.flow;

import java.util.Set;

import es.ull.iis.simulation.model.FlowExecutor;
import es.ull.iis.simulation.model.Model;


/**
 * A flow with a unique successor.
 * @author Iván Castilla Rodríguez
 */
public abstract class SingleSuccessorFlow extends BasicFlow {
	/** The unique successor of this flow */
	protected Flow successor;

	/**
	 * Creates a new unique successor flow.
	 */
	public SingleSuccessorFlow(Model model) {
		super(model);
	}
	
	@Override
	public void setRecursiveStructureLink(StructuredFlow parent, Set<Flow> visited) {
		setParent(parent);
		visited.add(this);
		if (successor != null)
			if (!visited.contains(successor))
				successor.setRecursiveStructureLink(parent, visited);			
	}	

	@Override
	public Flow link(Flow succ) {
		successor = (Flow) succ;
		succ.addPredecessor(this);
		return succ;
	}

	/**
	 * @return the successor
	 */
	public Flow getSuccessor() {
		return successor;
	}
	
	/**
	 * If this flow has a valid successor, requests this successor passing
	 * the same work thread. If not, the work thread finishes here, and, 
	 * if this flow has a valid parent, it's notified that this flow finished.
	 * @param wThread  
	 */
	@Override
	public void next(final FlowExecutor wThread) {
		super.next(wThread);
		if (successor != null) {
			wThread.getElement().addRequestEvent(successor, wThread);
		}
		else {
			wThread.notifyEnd();
		}
	}
	
}
