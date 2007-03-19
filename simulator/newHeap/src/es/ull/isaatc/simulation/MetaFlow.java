/**
 * 
 */
package es.ull.isaatc.simulation;

import es.ull.isaatc.random.RandomNumber;

/**
 * @author Iv�n Castilla Rodr�guez
 *
 */
public abstract class MetaFlow {
	/** metaflow id */
	protected int id;
	/** metaflow's parent */
	protected MetaFlow parent;
	/** iterations this flow should be done */ 
	protected RandomNumber iterations;

	/**
	 * 
	 * @param parent
	 * @param iterations
	 */
	public MetaFlow(int id, MetaFlow parent, RandomNumber iterations) {
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
	public RandomNumber getIterations() {
		return iterations;
	}

	/**
	 * If parentFlow is null, it must set the element flow.
	 * @param parentFlow
	 * @param e
	 * @return
	 */
	public abstract boolean getFlow(Flow parentFlow, Element e);

	protected abstract void add(MetaFlow descendant);
}
