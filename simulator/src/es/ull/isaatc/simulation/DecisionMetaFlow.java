/**
 * 
 */
package es.ull.isaatc.simulation;

import java.util.ArrayList;
import java.util.Iterator;

import simkit.random.RandomVariate;

/**
 * @author Iván Castilla Rodríguez
 *
 */
public class DecisionMetaFlow extends MetaFlow {
	/**	 Options list */
	protected ArrayList<MetaFlow> options;
	
	/**
	 * 
	 * @param iterations
	 */
	public DecisionMetaFlow(int id, RandomVariate iterations) {
		super(id, null, iterations);
		options = new ArrayList<MetaFlow>();
	}
	
	/**
	 * 
	 * @param parent
	 * @param iterations
	 */
	public DecisionMetaFlow(int id, GroupMetaFlow parent, RandomVariate iterations) {
		super(id, parent, iterations);
		options = new ArrayList<MetaFlow>();
	}

	/**
	 * 
	 * @param parent
	 * @param iterations
	 */
	public DecisionMetaFlow(int id, OptionMetaFlow parent, RandomVariate iterations) {
		super(id, parent, iterations);
		options = new ArrayList<MetaFlow>();
	}

	/**
	 * 
	 * @param parent
	 * @param iterations
	 */
	public DecisionMetaFlow(int id, TypeBranchMetaFlow parent, RandomVariate iterations) {
		super(id, parent, iterations);
		options = new ArrayList<MetaFlow>();
	}

	/* (non-Javadoc)
	 * @see es.ull.isaatc.simulation.MetaFlow#getFlow(es.ull.isaatc.simulation.Flow, es.ull.isaatc.simulation.Element)
	 */
	public boolean getFlow(Flow parentFlow, Element e) {
		
			return getDescendantFlow(parentFlow, e);
	}

	/**
	 * Selects an option and creates its flow
	 * @param parentFlow
	 * @param e
	 * @return
	 */
	protected boolean getDescendantFlow(Flow parentFlow, Element e) {
		double selProb = Math.random();
		double delta = 0.0;
		Iterator<MetaFlow> optIt = options.iterator();
		OptionMetaFlow opt = null;
		
		while (optIt.hasNext()) {
			opt = (OptionMetaFlow)optIt.next();
			delta += opt.getProb();
			if (delta >= selProb)
				return opt.getFlow(parentFlow, e);
		}
		return opt.getFlow(parentFlow, e);		
	}
	
	/**
	 * Adds a MetaFlow as an option. 
	 */
	protected void add(MetaFlow descendant) {
		options.add(descendant);		
	}
}
