package es.ull.cyc.simulation;

import es.ull.cyc.random.Fixed;

/**
 * @author Iván Castilla Rodríguez
 *
 */
public class OptionMetaFlow extends MetaFlow {
	protected MetaFlow descendant;
	protected double prob;

	public OptionMetaFlow(int id, DecisionMetaFlow parent, double prob) {
		super(id, parent, new Fixed(1));
		this.prob = prob;
	}

	public Flow getFlow(Flow parentFlow, Element e) {
		return descendant.getFlow(parentFlow, e);
	}

	protected void add(MetaFlow descendant) {
		this.descendant = descendant;
	}
	
	public double getProb() {
		return prob;
	}
}
