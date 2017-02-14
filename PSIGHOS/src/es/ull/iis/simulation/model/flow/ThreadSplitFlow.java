/**
 * 
 */
package es.ull.iis.simulation.model.flow;

import java.util.Set;

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
	 * @param nInstances Number of outgoing threads
	 */
	public ThreadSplitFlow(int nInstances) {
		super();
		this.nInstances = nInstances;
	}

	@Override
	public void addPredecessor(Flow predecessor) {
	}

	@Override
	public Flow link(Flow successor) {
		this.successor = (Flow)successor;
		successor.addPredecessor(this);
		return successor;
	}

	@Override
	public void setRecursiveStructureLink(StructuredFlow parent, Set<Flow> visited) {
		setParent(parent);
		visited.add(this);
		if (successor != null)
			if (!visited.contains(successor))
				successor.setRecursiveStructureLink(parent, visited);		
	}

	/**
	 * Returns the amount of instances to be created.
	 * @return The amount of instances to be created
	 */
	public int getNInstances() {
		return nInstances;
	}
}
