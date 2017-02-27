/**
 * 
 */
package es.ull.iis.simulation.parallel.flow;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Set;

import es.ull.iis.simulation.parallel.Simulation;
import es.ull.iis.simulation.parallel.FlowExecutor;

/**
 * A flow with multiple successors. Multiple successors are split nodes, that is,
 * new work threads are created from this flow on, when it's requested.
 * @author Iván Castilla Rodríguez
 */
public abstract class MultipleSuccessorFlow extends BasicFlow implements SplitFlow, es.ull.iis.simulation.core.flow.MultipleSuccessorFlow {
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
	 * @see es.ull.iis.simulation.Flow#request(es.ull.iis.simulation.WorkThread)
	 */
	public void request(FlowExecutor wThread) {
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

	/* (non-Javadoc)
	 * @see es.ull.iis.simulation.Flow#addPredecessor(es.ull.iis.simulation.Flow)
	 */
	public void addPredecessor(es.ull.iis.simulation.core.flow.Flow newFlow) {
	}

	/* (non-Javadoc)
	 * @see es.ull.iis.simulation.Flow#link(es.ull.iis.simulation.Flow)
	 */
	public es.ull.iis.simulation.core.flow.Flow link(es.ull.iis.simulation.core.flow.Flow successor) {
		successorList.add((Flow)successor);
    	successor.addPredecessor(this);
	}

	/**
	 * Adds a collection of flow's successors. This method must invoke 
	 * <code>successor.addPredecessor</code> to build the graph properly. 
	 * @param succList This flow's successors.
	 */
	public void link(Collection<es.ull.iis.simulation.core.flow.Flow> succList) {
        for (es.ull.iis.simulation.core.flow.Flow succ : succList) {
        	successorList.add((Flow)succ);
        	succ.addPredecessor(this);
        }		
	}

	/* (non-Javadoc)
	 * @see es.ull.iis.simulation.Flow#setRecursiveStructureLink(es.ull.iis.simulation.StructuredFlow)
	 */
	public void setRecursiveStructureLink(es.ull.iis.simulation.core.flow.StructuredFlow parent, Set<es.ull.iis.simulation.core.flow.Flow> visited) {
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
	public ArrayList<es.ull.iis.simulation.core.flow.Flow> getSuccessorList() {
		ArrayList<es.ull.iis.simulation.core.flow.Flow> newSuccList = new ArrayList<es.ull.iis.simulation.core.flow.Flow>();
		for (Flow f : successorList)
			newSuccList.add(f);
		return newSuccList;
	}
	
}
