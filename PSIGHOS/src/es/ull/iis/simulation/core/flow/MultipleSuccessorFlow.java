/**
 * 
 */
package es.ull.iis.simulation.core.flow;

import java.util.ArrayList;
import java.util.Collection;

import es.ull.iis.simulation.core.WorkThread;


/**
 * A {@link SplitFlow} with multiple successors. Multiple successors are split nodes, that is,
 * new work threads are created from this flow on, when it's requested.
 * @author Iván Castilla Rodríguez
 */
public interface MultipleSuccessorFlow<WT extends WorkThread<?>> extends SplitFlow<WT> {
	/**
	 * Adds a collection of flow's successors. This method must invoke 
	 * the successor's {@link #addPredecessor(Flow)} method to build the graph properly. 
	 * @param succList This flow's successors.
	 */
	public void link(Collection<Flow<WT>> succList);

	/**
	 * Returns the list of successor flows which follows this one.
	 * @return the list of successor flows which follows this one.
	 */
	public ArrayList<Flow<WT>> getSuccessorList();
}
