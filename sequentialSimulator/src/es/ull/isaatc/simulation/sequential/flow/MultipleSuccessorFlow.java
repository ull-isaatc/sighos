/**
 * 
 */
package es.ull.isaatc.simulation.sequential.flow;

import java.util.ArrayList;
import java.util.Collection;

import es.ull.isaatc.simulation.sequential.Simulation;
import es.ull.isaatc.simulation.sequential.WorkThread;

/**
 * A flow with multiple successors. Multiple successors are split nodes, that is,
 * new work threads are created from this flow on, when it's requested.
 * @author Iván Castilla Rodríguez
 */
public abstract class MultipleSuccessorFlow extends BasicFlow implements SplitFlow {
	/** Successor list */
	protected final ArrayList<Flow> successorList;

	/**
	 * Creates a flow with multiple successors.
	 * @param simul The simulation this flow belongs to.
	 */
	public MultipleSuccessorFlow(Simulation simul) {
		super(simul);
		successorList = new ArrayList<Flow>();
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

	/* (non-Javadoc)
	 * @see es.ull.isaatc.simulation.Flow#addPredecessor(es.ull.isaatc.simulation.Flow)
	 */
	public void addPredecessor(Flow newFlow) {
	}

	/* (non-Javadoc)
	 * @see es.ull.isaatc.simulation.Flow#link(es.ull.isaatc.simulation.Flow)
	 */
	public void link(Flow successor) {
		successorList.add(successor);
    	successor.addPredecessor(this);
	}

	/**
	 * Adds a collection of flow's successors. This method must invoke 
	 * <code>successor.addPredecessor</code> to build the graph properly. 
	 * @param succList This flow's successors.
	 */
	public void link(Collection<Flow> succList) {
        for (Flow succ : succList) {
        	successorList.add(succ);
        	succ.addPredecessor(this);
        }		
	}

	/* (non-Javadoc)
	 * @see es.ull.isaatc.simulation.Flow#setRecursiveStructureLink(es.ull.isaatc.simulation.StructuredFlow)
	 */
	public void setRecursiveStructureLink(StructuredFlow parent) {
		 setParent(parent);
		 for (Flow f : successorList)
			 f.setRecursiveStructureLink(parent); 	
	}

	/**
	 * Returns the list of successor flows which follows this one.
	 * @return the list of successor flows which follows this one.
	 */
	public ArrayList<Flow> getSuccessorList() {
		return successorList;
	}
}
