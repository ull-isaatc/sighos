/**
 * 
 */
package es.ull.cyc.simulation;

import java.util.ArrayList;
import java.util.Iterator;

import es.ull.cyc.random.RandomNumber;

/**
 * @author Iv�n Castilla Rodr�guez
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
	protected void getDescendantsFlows(Flow parentFlow, Element e) {
		Iterator<MetaFlow> descIt = descendants.iterator();
		while (descIt.hasNext()) {
			descIt.next().getFlow(parentFlow, e);
		}		
	}
}
