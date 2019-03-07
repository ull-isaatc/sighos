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
 * new element instances are created from this flow on, when it's requested.
 * @author Iván Castilla Rodríguez
 */
public abstract class MultipleSuccessorFlow extends BasicFlow implements SplitFlow {
	/** Successor list */
	protected final ArrayList<Flow> successorList;

	/**
	 * Creates a flow with multiple successors.
	 * @param model The simulation this flow belongs to.
	 */
	public MultipleSuccessorFlow(final Simulation model) {
		super(model);
		successorList = new ArrayList<Flow>();
	}

	@Override
	public void addPredecessor(final Flow newFlow) {
	}

	@Override
	public Flow link(final Flow successor) {
		successorList.add(successor);
    	successor.addPredecessor(this);
    	return successor;
	}

	/**
	 * Adds a collection of flow's successors. This method must invoke 
	 * <code>successor.addPredecessor</code> to build the graph properly. 
	 * @param succList This flow's successors.
	 */
	public void link(final Collection<Flow> succList) {
        for (Flow succ : succList) {
        	successorList.add(succ);
        	succ.addPredecessor(this);
        }		
	}

	@Override
	public void setRecursiveStructureLink(final StructuredFlow parent, final Set<Flow> visited) {
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

}
