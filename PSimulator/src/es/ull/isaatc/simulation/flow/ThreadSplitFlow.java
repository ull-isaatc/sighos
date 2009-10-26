/**
 * 
 */
package es.ull.isaatc.simulation.flow;

import es.ull.isaatc.simulation.model.Model;


/**
 * A flow which creates several instances of the current work thread. It physically
 * works as a single successor flow, but functionally as a parallel flow. It should
 * be use with its counterpart Thread Merge pattern (WFP 41).
 * Meets the Thread Split pattern (WFP 42).
 * @author Iván Castilla Rodríguez
 *
 */
public class ThreadSplitFlow extends BasicFlow implements SplitFlow {
	/** Number of outgoing threads produced by this flow */
	protected final int nInstances;
	/** The unique successor of this flow */
	protected Flow successor;

	/**
	 * Creates a new thread split flow
	 * @param model Model this flow belongs to
	 * @param nInstances Number of outgoing threads
	 */
	public ThreadSplitFlow(Model model, int nInstances) {
		super(model);
		this.nInstances = nInstances;
	}
	
	/* (non-Javadoc)
	 * @see es.ull.isaatc.simulation.Flow#addPredecessor(es.ull.isaatc.simulation.Flow)
	 */
	public void addPredecessor(Flow predecessor) {
	}

	public void link(Flow successor) {
		this.successor = successor;
		successor.addPredecessor(this);
	}

	public void setRecursiveStructureLink(StructuredFlow parent) {
		setParent(parent);
		if (successor != null)
			successor.setRecursiveStructureLink(parent);		
	}
}
