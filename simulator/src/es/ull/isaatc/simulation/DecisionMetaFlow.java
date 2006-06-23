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
public class DecisionMetaFlow extends MetaFlow {
	/**	 Options list */
	protected ArrayList<MetaFlow> options;
	
	/**
	 * 
	 * @param iterations
	 */
	public DecisionMetaFlow(int id, RandomNumber iterations) {
		super(id, null, iterations);
		options = new ArrayList<MetaFlow>();
	}
	
	/**
	 * 
	 * @param parent
	 * @param iterations
	 */
	public DecisionMetaFlow(int id, GroupMetaFlow parent, RandomNumber iterations) {
		super(id, parent, iterations);
		options = new ArrayList<MetaFlow>();
	}

	/**
	 * 
	 * @param parent
	 * @param iterations
	 */
	public DecisionMetaFlow(int id, OptionMetaFlow parent, RandomNumber iterations) {
		super(id, parent, iterations);
		options = new ArrayList<MetaFlow>();
	}

	/* (non-Javadoc)
	 * @see es.ull.isaatc.simulation.MetaFlow#getFlow(es.ull.isaatc.simulation.Flow, es.ull.isaatc.simulation.Element)
	 */
	public Flow getFlow(Flow parentFlow, Element e) {
		
			return getDescendantFlow(parentFlow, e);
	}

	/**
	 * Selects an option and creates its flow
	 * @param parentFlow
	 * @param e
	 * @return
	 */
	protected Flow getDescendantFlow(Flow parentFlow, Element e) {
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
