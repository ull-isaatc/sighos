/**
 * 
 */
package es.ull.iis.simulation.model.flow;

import java.util.Set;

import es.ull.iis.simulation.model.ElementInstance;
import es.ull.iis.simulation.model.Simulation;


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
	public ThreadSplitFlow(Simulation model, int nInstances) {
		super(model);
		this.nInstances = nInstances;
	}

	/**
	 * @return the successor
	 */
	public Flow getSuccessor() {
		return successor;
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

	/* (non-Javadoc)
	 * @see es.ull.iis.simulation.Flow#request(es.ull.iis.simulation.FlowExecutor)
	 */
	public void request(ElementInstance wThread) {
		if (!wThread.wasVisited(this)) {
			if (wThread.isExecutable()) {
				if (!beforeRequest(wThread))
					wThread.cancel(this);
			} else 
				wThread.updatePath(this);
			next(wThread);
		} else
			wThread.notifyEnd();
	}

	/*
	 * (non-Javadoc)
	 * @see es.ull.iis.simulation.BasicFlow#next(es.ull.iis.simulation.FlowExecutor)
	 */
	@Override
	public void next(ElementInstance wThread) {
		super.next(wThread);
		for (int i = 0; i < nInstances; i++)
			wThread.getElement().addRequestEvent(successor, wThread.getSubsequentElementInstance(wThread.isExecutable(), this, wThread.getToken()));
        wThread.notifyEnd();			
	}

}
