/**
 * 
 */
package es.ull.isaatc.simulation.flow;

import es.ull.isaatc.simulation.model.Model;

/**
 * A flow with a unique successor.
 * @author Iván Castilla Rodríguez
 */
public abstract class SingleSuccessorFlow extends BasicFlow {
	/** The unique successor of this flow */
	protected Flow successor;

	/**
	 * Creates a new unique successor flow.
	 * @param model Model this flow belongs to
	 */
	public SingleSuccessorFlow(Model model) {
		super(model);
	}
	
	public void setRecursiveStructureLink(StructuredFlow parent) {
		setParent(parent);
		if (successor != null)
			successor.setRecursiveStructureLink(parent);			
	}	

	public void link(Flow succ) {
		successor = succ;
		succ.addPredecessor(this);
	}

	/**
	 * @return the successor
	 */
	public Flow getSuccessor() {
		return successor;
	}
	
}
