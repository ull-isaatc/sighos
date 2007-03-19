/**
 * 
 */
package es.ull.isaatc.simulation;

import java.util.ArrayList;
import java.util.Iterator;

import es.ull.isaatc.random.RandomNumber;

/**
 * @author Iván Castilla Rodríguez
 *
 */
public abstract class GroupMetaFlow extends MetaFlow {
	protected ArrayList<MetaFlow> descendants;

	/**
	 * 
	 * @param parent
	 * @param iterations
	 */
	public GroupMetaFlow(int id, MetaFlow parent, RandomNumber iterations) {
		super(id, parent, iterations);
		descendants = new ArrayList<MetaFlow>();
	}

	/**
	 * Adds a MetaFlow to the descendants list
	 */
	protected void add(MetaFlow descendant) {
		descendants.add(descendant);
	}
	
	/**
	 * Creates the descendants flows
	 * @param parentFlow
	 * @param e
	 */
	protected boolean getDescendantsFlows(Flow parentFlow, Element e) {
		Iterator<MetaFlow> descIt = descendants.iterator();
		boolean exit = false;
		while (descIt.hasNext() && !exit) {
			exit = !descIt.next().getFlow(parentFlow, e);
		}
		return !exit;
	}
}
