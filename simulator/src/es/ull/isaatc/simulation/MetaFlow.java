/**
 * 
 */
package es.ull.isaatc.simulation;

import simkit.random.RandomVariate;

/**
 * @author Iván Castilla Rodríguez
 *
 */
public abstract class MetaFlow {
	/** Metaflow unique identifier */
	protected int id;
	/** Metaflow's parent */
	protected MetaFlow parent;
	/** Iterations this flow should be done */ 
	protected RandomVariate iterations;

	/**
	 * 
	 * @param parent
	 * @param iterations
	 */
	public MetaFlow(int id, MetaFlow parent, RandomVariate iterations) {
		this.id = id;
		this.parent = parent;
		if (parent != null)
			parent.add(this);
		this.iterations = iterations;
	}
	/**
	 * @return Returns this metaflow's id.
	 */
	public int getId() {
		return id;
	}
	
	/**
	 * @param id the id to set
	 */
	public void setId(int id) {
		this.id = id;
	}
	
	/**
	 * @return Returns this metaflow's parent.
	 */
	public MetaFlow getParent() {
		return parent;
	}

	/**
	 * @return Returns the iterations.
	 */
	public RandomVariate getIterations() {
		return iterations;
	}

	/**
	 * If parentFlow is null, it must set the element flow.
	 * @param parentFlow
	 * @param e
	 * @return
	 */
	public abstract boolean getFlow(Flow parentFlow, Element e);

	/**
	 * Adds a descendant metaflow
	 * @param descendant The descendant metaflow
	 */
	protected abstract void add(MetaFlow descendant);
}
