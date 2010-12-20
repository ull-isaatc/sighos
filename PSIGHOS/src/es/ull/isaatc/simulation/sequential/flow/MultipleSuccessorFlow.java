/**
 * 
 */
package es.ull.isaatc.simulation.sequential.flow;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Set;

import es.ull.isaatc.simulation.sequential.Simulation;
import es.ull.isaatc.simulation.sequential.WorkThread;

/**
 * A flow with multiple successors. Multiple successors are split nodes, that is,
 * new work threads are created from this flow on, when it's requested.
 * @author Iv�n Castilla Rodr�guez
 */
public abstract class MultipleSuccessorFlow extends BasicFlow implements es.ull.isaatc.simulation.flow.MultipleSuccessorFlow, SplitFlow {
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
	public void addPredecessor(es.ull.isaatc.simulation.flow.Flow newFlow) {
	}

	/* (non-Javadoc)
	 * @see es.ull.isaatc.simulation.Flow#link(es.ull.isaatc.simulation.Flow)
	 */
	public void link(es.ull.isaatc.simulation.flow.Flow successor) {
		successorList.add((Flow)successor);
    	successor.addPredecessor(this);
	}

	/**
	 * Adds a collection of flow's successors. This method must invoke 
	 * <code>successor.addPredecessor</code> to build the graph properly. 
	 * @param succList This flow's successors.
	 */
	public void link(Collection<es.ull.isaatc.simulation.flow.Flow> succList) {
        for (es.ull.isaatc.simulation.flow.Flow succ : succList) {
        	successorList.add((Flow)succ);
        	succ.addPredecessor(this);
        }		
	}

	/* (non-Javadoc)
	 * @see es.ull.isaatc.simulation.Flow#setRecursiveStructureLink(es.ull.isaatc.simulation.StructuredFlow)
	 */
	public void setRecursiveStructureLink(es.ull.isaatc.simulation.flow.StructuredFlow parent, Set<es.ull.isaatc.simulation.flow.Flow> visited) {
		 setParent(parent);
		 visited.add(this);
		 for (Flow f : successorList)
			 if (!visited.contains(f))
				 f.setRecursiveStructureLink(parent, visited); 	
	}

	/**
	 * Returns the list of successor flows which follows this one.
	 * @return the list of successor flows which follows this one.
	 */
	public ArrayList<es.ull.isaatc.simulation.flow.Flow> getSuccessorList() {
		ArrayList<es.ull.isaatc.simulation.flow.Flow> newSuccList = new ArrayList<es.ull.isaatc.simulation.flow.Flow>();
		for (Flow f : successorList)
			newSuccList.add(f);
		return newSuccList;
	}
}
