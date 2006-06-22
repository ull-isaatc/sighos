/**
 * 
 */
package es.ull.cyc.simulation;

import java.util.ArrayList;
import java.util.Iterator;

import es.ull.cyc.random.*;

/**
 * @author Iván Castilla Rodríguez
 *
 */
public class TypeMetaFlow extends MetaFlow {
	/**	 Options list */
	protected ArrayList<MetaFlow> options;
	
	/**
	 * 
	 * @param iterations
	 */
	public TypeMetaFlow(int id, RandomNumber iterations) {
		super(id, null, iterations);
		options = new ArrayList<MetaFlow>();
	}
	
	/**
	 * 
	 * @param parent
	 * @param iterations
	 */
	public TypeMetaFlow(int id, GroupMetaFlow parent, RandomNumber iterations) {
		super(id, parent, iterations);
		options = new ArrayList<MetaFlow>();
	}

	/**
	 * 
	 * @param parent
	 * @param iterations
	 */
	public TypeMetaFlow(int id, OptionMetaFlow parent, RandomNumber iterations) {
		super(id, parent, iterations);
		options = new ArrayList<MetaFlow>();
	}

	/* (non-Javadoc)
	 * @see es.ull.cyc.simulation.MetaFlow#getFlow(es.ull.cyc.simulation.Flow, es.ull.cyc.simulation.Element)
	 */
	public Flow getFlow(Flow parentFlow, Element e) {

		return getTypeBranchFlow(parentFlow, e);
	}

	/**
	 * Selects an option and creates its flow
	 * @param parentFlow
	 * @param e
	 * @return
	 */
	protected Flow getTypeBranchFlow(Flow parentFlow, Element e) {
		Iterator<MetaFlow> optIt = options.iterator();
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
		options.add(descendant);		
	}

}
