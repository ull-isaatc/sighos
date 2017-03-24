/**
 * 
 */
package es.ull.iis.simulation.model.flow;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Set;

import es.ull.iis.simulation.model.ElementInstance;
import es.ull.iis.simulation.model.Simulation;


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
	public MultipleSuccessorFlow(Simulation model) {
		super(model);
		successorList = new ArrayList<Flow>();
	}

	@Override
	public void addPredecessor(Flow newFlow) {
	}

	@Override
	public Flow link(Flow successor) {
		successorList.add(successor);
    	successor.addPredecessor(this);
    	return successor;
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

	@Override
	public void setRecursiveStructureLink(StructuredFlow parent, Set<Flow> visited) {
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
	public ArrayList<Flow> getSuccessorList() {
		ArrayList<Flow> newSuccList = new ArrayList<Flow>();
		for (Flow f : successorList)
			newSuccList.add(f);
		return newSuccList;
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

}
