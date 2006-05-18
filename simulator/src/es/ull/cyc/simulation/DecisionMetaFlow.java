/**
 * 
 */
package es.ull.cyc.simulation;

import java.util.ArrayList;
import java.util.Iterator;

import es.ull.cyc.random.*;

/**
 * @author Iv�n Castilla Rodr�guez
 *
 */
public class DecisionMetaFlow extends MetaFlow {
	/**	 Options list */
	protected ArrayList options;
	
	/**
	 * 
	 * @param iterations
	 */
	public DecisionMetaFlow(int id, RandomNumber iterations) {
		super(id, null, iterations);
		options = new ArrayList();
	}
	
	/**
	 * 
	 * @param parent
	 * @param iterations
	 */
	public DecisionMetaFlow(int id, GroupMetaFlow parent, RandomNumber iterations) {
		super(id, parent, iterations);
		options = new ArrayList();
	}

	/**
	 * 
	 * @param parent
	 * @param iterations
	 */
	public DecisionMetaFlow(int id, OptionMetaFlow parent, RandomNumber iterations) {
		super(id, parent, iterations);
		options = new ArrayList();
	}

	/* (non-Javadoc)
	 * @see es.ull.cyc.simulation.MetaFlow#getFlow(es.ull.cyc.simulation.Flow, es.ull.cyc.simulation.Element)
	 */
	public Flow getFlow(Flow parentFlow, Element e) {
		int iter = iterations.sampleInt();
		SequenceFlow sec;
		
		if (iter == 0)
			return null;
		else if (iter == 1) {
			return getDescendantFlow(parentFlow, e);
		}
		else  {			
			if (parentFlow instanceof SequenceFlow)
				sec = (SequenceFlow)parentFlow;
			else
				sec = new SequenceFlow((SimultaneousFlow)parentFlow, e);
			for (int i = 0; i < iter; i++)
				getDescendantFlow(sec, e);
			return sec;
		}
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
		Iterator optIt = options.iterator();
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