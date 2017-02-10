/**
 * 
 */
package es.ull.iis.simulation.sequential.flow;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Set;

import es.ull.iis.simulation.core.flow.Flow;
import es.ull.iis.simulation.core.flow.SplitFlow;
import es.ull.iis.simulation.sequential.Simulation;
import es.ull.iis.simulation.sequential.WorkThread;

/**
 * A flow with multiple successors. Multiple successors are split nodes, that is,
 * new work threads are created from this flow on, when it's requested.
 * @author Iván Castilla Rodríguez
 */
public abstract class MultipleSuccessorFlow extends BasicFlow implements es.ull.iis.simulation.core.flow.MultipleSuccessorFlow<WorkThread>, SplitFlow<WorkThread> {
	/** Successor list */
	protected final ArrayList<Flow<WorkThread>> successorList;

	/**
	 * Creates a flow with multiple successors.
	 * @param simul The simulation this flow belongs to.
	 */
	public MultipleSuccessorFlow(Simulation simul) {
		super(simul);
		successorList = new ArrayList<Flow<WorkThread>>();
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

	/* (non-Javadoc)
	 * @see es.ull.iis.simulation.Flow#addPredecessor(es.ull.iis.simulation.Flow)
	 */
	public void addPredecessor(Flow<WorkThread> newFlow) {
	}

	/* (non-Javadoc)
	 * @see es.ull.iis.simulation.Flow#link(es.ull.iis.simulation.Flow)
	 */
	public Flow<WorkThread> link(Flow<WorkThread> successor) {
		successorList.add(successor);
    	successor.addPredecessor(this);
    	return successor;
	}

	/**
	 * Adds a collection of flow's successors. This method must invoke 
	 * <code>successor.addPredecessor</code> to build the graph properly. 
	 * @param succList This flow's successors.
	 */
	public void link(Collection<Flow<WorkThread>> succList) {
        for (Flow<WorkThread> succ : succList) {
        	successorList.add(succ);
        	succ.addPredecessor(this);
        }		
	}

	/* (non-Javadoc)
	 * @see es.ull.iis.simulation.Flow#setRecursiveStructureLink(es.ull.iis.simulation.StructuredFlow)
	 */
	public void setRecursiveStructureLink(es.ull.iis.simulation.core.flow.StructuredFlow<WorkThread> parent, Set<Flow<WorkThread>> visited) {
		 setParent(parent);
		 visited.add(this);
		 for (Flow<WorkThread> f : successorList)
			 if (!visited.contains(f))
				 f.setRecursiveStructureLink(parent, visited); 	
	}

	/**
	 * Returns the list of successor flows which follows this one.
	 * @return the list of successor flows which follows this one.
	 */
	public ArrayList<Flow<WorkThread>> getSuccessorList() {
		ArrayList<Flow<WorkThread>> newSuccList = new ArrayList<Flow<WorkThread>>();
		for (Flow<WorkThread> f : successorList)
			newSuccList.add(f);
		return newSuccList;
	}
}
