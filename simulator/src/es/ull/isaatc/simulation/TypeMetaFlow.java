/**
 * 
 */
package es.ull.isaatc.simulation;

import java.util.ArrayList;
import java.util.Iterator;

import es.ull.isaatc.random.*;

/**
 * @author Iván Castilla Rodríguez
 *
 */
public class TypeMetaFlow extends MetaFlow {
	/**	 Options list */
	protected ArrayList<MetaFlow> branches;
	
	/**
	 * 
	 * @param iterations
	 */
	public TypeMetaFlow(int id, RandomNumber iterations) {
		super(id, null, iterations);
		branches = new ArrayList<MetaFlow>();
	}
	
	/**
	 * 
	 * @param parent
	 * @param iterations
	 */
	public TypeMetaFlow(int id, GroupMetaFlow parent, RandomNumber iterations) {
		super(id, parent, iterations);
		branches = new ArrayList<MetaFlow>();
	}

	/**
	 * 
	 * @param parent
	 * @param iterations
	 */
	public TypeMetaFlow(int id, OptionMetaFlow parent, RandomNumber iterations) {
		super(id, parent, iterations);
		branches = new ArrayList<MetaFlow>();
	}

	/**
	 * 
	 * @param parent
	 * @param iterations
	 */
	public TypeMetaFlow(int id, TypeBranchMetaFlow parent, RandomNumber iterations) {
		super(id, parent, iterations);
		branches = new ArrayList<MetaFlow>();
	}

	/* (non-Javadoc)
	 * @see es.ull.isaatc.simulation.MetaFlow#getFlow(es.ull.isaatc.simulation.Flow, es.ull.isaatc.simulation.Element)
	 */
	public boolean getFlow(Flow parentFlow, Element e) {

		return getTypeBranchFlow(parentFlow, e);
	}

	/**
	 * Selects an option and creates its flow
	 * @param parentFlow
	 * @param e
	 * @return
	 */
	protected Flow getTypeBranchFlow(Flow parentFlow, Element e) {
		Iterator<MetaFlow> optIt = branches.iterator();
		TypeBranchMetaFlow branch = null;
		
		while (optIt.hasNext()) {
			branch = (TypeBranchMetaFlow)optIt.next();
			if (branch.hasElementType(e.getElementType()))
				return branch.getFlow(parentFlow, e);
		}
		return null;		
	}
	
	/**
	 * Adds a MetaFlow as an option. 
	 */
	protected void add(MetaFlow descendant) {
		branches.add(descendant);		
	}

}
