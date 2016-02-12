/**
 * 
 */
package es.ull.iis.simulation.parallel.flow;

import java.util.Set;

import es.ull.iis.simulation.parallel.Simulation;
import es.ull.iis.simulation.parallel.WorkThread;


/**
 * A flow which creates several instances of the current work thread. It physically
 * works as a single successor flow, but functionally as a parallel flow. It should
 * be use with its counterpart Thread Merge pattern (WFP 41).
 * Meets the Thread Split pattern (WFP 42).
 * @author Iván Castilla Rodríguez
 *
 */
public class ThreadSplitFlow extends BasicFlow implements SplitFlow, es.ull.iis.simulation.core.flow.ThreadSplitFlow {
	/** Number of outgoing threads produced by this flow */
	protected final int nInstances;
	/** The unique successor of this flow */
	protected Flow successor;

	/**
	 * Creates a new thread split flow
	 * @param simul Simulation this flow belongs to
	 * @param nInstances Number of outgoing threads
	 */
	public ThreadSplitFlow(Simulation simul, int nInstances) {
		super(simul);
		this.nInstances = nInstances;
	}
	
	/* (non-Javadoc)
	 * @see es.ull.isaatc.simulation.Flow#addPredecessor(es.ull.isaatc.simulation.Flow)
	 */
	public void addPredecessor(es.ull.iis.simulation.core.flow.Flow predecessor) {
	}

	/* (non-Javadoc)
	 * @see es.ull.isaatc.simulation.Flow#request(es.ull.isaatc.simulation.WorkThread)
	 */
	public void request(WorkThread wThread) {
		if (!wThread.wasVisited(this)) {
			if (wThread.isExecutable()) {
				if (!beforeRequest(wThread.getElement()))
					wThread.cancel(this);
			} else 
				wThread.updatePath(this);
			next(wThread);
		} else
			wThread.notifyEnd();
	}

	/*
	 * (non-Javadoc)
	 * @see es.ull.isaatc.simulation.BasicFlow#next(es.ull.isaatc.simulation.WorkThread)
	 */
	@Override
	public void next(WorkThread wThread) {
		super.next(wThread);
		for (int i = 0; i < nInstances; i++)
			wThread.getInstanceSubsequentWorkThread(wThread.isExecutable(), this, wThread.getToken()).requestFlow(successor);
        wThread.notifyEnd();			
	}

	public void link(es.ull.iis.simulation.core.flow.Flow successor) {
		this.successor = (Flow)successor;
		successor.addPredecessor(this);
	}

	public void setRecursiveStructureLink(es.ull.iis.simulation.core.flow.StructuredFlow parent, Set<es.ull.iis.simulation.core.flow.Flow> visited) {
		setParent(parent);
		visited.add(this);
		if (successor != null)
			if (!visited.contains(successor))
				successor.setRecursiveStructureLink(parent, visited);		
	}

	@Override
	public int getNInstances() {
		return nInstances;
	}
}
