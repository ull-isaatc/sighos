/**
 * 
 */
package es.ull.isaatc.simulation;

/**
 * @author Roberto Muñoz
 *
 */
public class ExitMetaFlow extends MetaFlow {
    

	public ExitMetaFlow(int id) {
		super(id, null, null);
	}
	
	/**
	 * @param parent
	 */
	public ExitMetaFlow(int id, GroupMetaFlow parent) {
		super(id, parent, null);
	}

	/**
	 * @param parent
	 */
	public ExitMetaFlow(int id, OptionMetaFlow parent) {
		super(id, parent, null);
	}

	/**
	 * @param parent
	 */
	public ExitMetaFlow(int id, TypeBranchMetaFlow parent) {
		super(id, parent, null);
	}

	/* (non-Javadoc)
	 * @see es.ull.isaatc.simulation.MetaFlow#getFlow(es.ull.isaatc.simulation.Flow, es.ull.isaatc.simulation.Element)
	 */
	public boolean getFlow(Flow parentFlow, Element e) {
		return false;
	}

	protected void add(MetaFlow descendant) {
	}
}
