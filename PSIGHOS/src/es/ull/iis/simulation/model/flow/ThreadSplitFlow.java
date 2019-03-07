/**
 * 
 */
package es.ull.iis.simulation.model.flow;

import java.util.Set;

import es.ull.iis.simulation.model.ElementInstance;
import es.ull.iis.simulation.model.Simulation;


/**
 * A flow which creates several instances of the current element instance. It physically
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
	 * @param model The simulation model this flow belongs to
	 * @param nInstances Number of outgoing threads
	 */
	public ThreadSplitFlow(final Simulation model, final int nInstances) {
		super(model);
		this.nInstances = nInstances;
	}

	/**
	 * Returns the successor of the flow
	 * @return the successor of the flow
	 */
	public Flow getSuccessor() {
		return successor;
	}

	@Override
	public void addPredecessor(final Flow predecessor) {
	}

	@Override
	public Flow link(final Flow successor) {
		this.successor = (Flow)successor;
		successor.addPredecessor(this);
		return successor;
	}

	@Override
	public void setRecursiveStructureLink(final StructuredFlow parent, final Set<Flow> visited) {
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

	@Override
	public void request(ElementInstance ei) {
		if (!ei.wasVisited(this)) {
			if (ei.isExecutable()) {
				if (!beforeRequest(ei))
					ei.cancel(this);
			} else 
				ei.updatePath(this);
			next(ei);
		} else
			ei.notifyEnd();
	}

	@Override
	public void next(ElementInstance ei) {
		super.next(ei);
		for (int i = 0; i < nInstances; i++)
			ei.getElement().addRequestEvent(successor, ei.getSubsequentElementInstance(ei.isExecutable(), this, ei.getToken()));
        ei.notifyEnd();			
	}

}
