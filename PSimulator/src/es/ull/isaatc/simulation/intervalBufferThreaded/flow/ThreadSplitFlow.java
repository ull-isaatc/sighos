/**
 * 
 */
package es.ull.isaatc.simulation.intervalBufferThreaded.flow;

import es.ull.isaatc.simulation.intervalBufferThreaded.Simulation;
import es.ull.isaatc.simulation.intervalBufferThreaded.WorkThread;


/**
 * A flow which creates several instances of the current work thread. It physically
 * works as a single successor flow, but functionally as a parallel flow. It should
 * be use with its counterpart Thread Merge pattern (WFP 41).
 * Meets the Thread Split pattern (WFP 42).
 * @author Iván Castilla Rodríguez
 *
 */
public class ThreadSplitFlow extends BasicFlow implements SplitFlow, es.ull.isaatc.simulation.common.flow.ThreadSplitFlow {
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
	public void addPredecessor(es.ull.isaatc.simulation.common.flow.Flow predecessor) {
	}

	/* (non-Javadoc)
	 * @see es.ull.isaatc.simulation.Flow#request(es.ull.isaatc.simulation.WorkThread)
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
	 * @see es.ull.isaatc.simulation.BasicFlow#next(es.ull.isaatc.simulation.WorkThread)
	 */
	@Override
	public void next(WorkThread wThread) {
		super.next(wThread);
		for (int i = 0; i < nInstances; i++)
			wThread.getElement().addRequestEvent(successor, wThread.getInstanceSubsequentWorkThread(wThread.isExecutable(), this, wThread.getToken()));
        wThread.notifyEnd();			
	}

	public void link(es.ull.isaatc.simulation.common.flow.Flow successor) {
		this.successor = (Flow)successor;
		successor.addPredecessor(this);
	}

	public void setRecursiveStructureLink(es.ull.isaatc.simulation.common.flow.StructuredFlow parent) {
		setParent(parent);
		if (successor != null)
			successor.setRecursiveStructureLink(parent);		
	}

	@Override
	public int getNInstances() {
		return nInstances;
	}
}
