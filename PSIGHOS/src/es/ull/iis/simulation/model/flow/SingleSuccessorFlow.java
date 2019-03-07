/**
 * 
 */
package es.ull.iis.simulation.model.flow;

import java.util.Set;

import es.ull.iis.simulation.model.ElementInstance;
import es.ull.iis.simulation.model.Simulation;


/**
 * A flow with a unique successor.
 * @author Iván Castilla Rodríguez
 */
public abstract class SingleSuccessorFlow extends BasicFlow {
	/** The unique successor of this flow */
	protected Flow successor = null;

	/**
	 * Creates a new unique successor flow.
	 * @param model The simulation model this flow belongs to
	 */
	public SingleSuccessorFlow(final Simulation model) {
		super(model);
	}
	
	@Override
	public void setRecursiveStructureLink(final StructuredFlow parent, final Set<Flow> visited) {
		setParent(parent);
		visited.add(this);
		if (successor != null)
			if (!visited.contains(successor))
				successor.setRecursiveStructureLink(parent, visited);			
	}	

	@Override
	public Flow link(final Flow succ) {
		if (successor != null) {
			Simulation.error("Trying to link already linked flow " + this.getClass() + " " + this);
		}
		else {
			successor = (Flow) succ;
			succ.addPredecessor(this);
		}
		return succ;
	}

	/**
	 * Returns the successor of the flow
	 * @return the successor of the flow
	 */
	public Flow getSuccessor() {
		return successor;
	}
	
	/**
	 * {@inheritDoc}
	 * 
	 * If this flow has a valid successor, requests this successor passing
	 * the same element instance. If not, the element instance finishes here, and, 
	 * if this flow has a valid parent, it's notified that this flow finished.
	 */
	public void next(final ElementInstance ei) {
		super.next(ei);
		if (successor != null) {
			ei.getElement().addRequestEvent(successor, ei);
		}
		else {
			ei.notifyEnd();
		}
	}
	
}
