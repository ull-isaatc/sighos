/**
 * 
 */
package es.ull.iis.simulation.sequential.flow;

import java.util.Set;

import es.ull.iis.simulation.core.flow.Flow;
import es.ull.iis.simulation.core.flow.SplitFlow;
import es.ull.iis.simulation.sequential.Simulation;
import es.ull.iis.simulation.sequential.WorkThread;


/**
 * A flow which creates several instances of the current work thread. It physically
 * works as a single successor flow, but functionally as a parallel flow. It should
 * be use with its counterpart Thread Merge pattern (WFP 41).
 * Meets the Thread Split pattern (WFP 42).
 * @author Iv�n Castilla Rodr�guez
 *
 */
public class ThreadSplitFlow extends BasicFlow implements es.ull.iis.simulation.core.flow.ThreadSplitFlow<WorkThread>, SplitFlow<WorkThread> {
	/** Number of outgoing threads produced by this flow */
	protected final int nInstances;
	/** The unique successor of this flow */
	protected Flow<WorkThread> successor;

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
	 * @see es.ull.iis.simulation.Flow#addPredecessor(es.ull.iis.simulation.Flow)
	 */
	public void addPredecessor(Flow<WorkThread> predecessor) {
	}

	/* (non-Javadoc)
	 * @see es.ull.iis.simulation.Flow#request(es.ull.iis.simulation.WorkThread)
	 */
	public void request(WorkThread wThread) {
		if (!wThread.wasVisited(this)) {
			if (wThread.isExecutable()) {
				if (!beforeRequest(wThread.getElement()))
					wThread.setExecutable(false, this);
			} else 
				wThread.updatePath(this);
			next(wThread);
		} else
			wThread.notifyEnd();
	}

	/*
	 * (non-Javadoc)
	 * @see es.ull.iis.simulation.BasicFlow#next(es.ull.iis.simulation.WorkThread)
	 */
	@Override
	public void next(WorkThread wThread) {
		super.next(wThread);
		for (int i = 0; i < nInstances; i++)
			wThread.getElement().addRequestEvent(successor, wThread.getInstanceSubsequentWorkThread(wThread.isExecutable(), this, wThread.getToken()));
        wThread.notifyEnd();			
	}

	public Flow<WorkThread> link(Flow<WorkThread> successor) {
		this.successor = (Flow<WorkThread>)successor;
		successor.addPredecessor(this);
		return successor;
	}

	public void setRecursiveStructureLink(es.ull.iis.simulation.core.flow.StructuredFlow<WorkThread> parent, Set<Flow<WorkThread>> visited) {
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
