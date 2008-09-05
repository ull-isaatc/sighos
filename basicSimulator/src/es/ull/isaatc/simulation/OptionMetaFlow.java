package es.ull.isaatc.simulation;

import simkit.random.RandomVariateFactory;

/**
 * @author Iván Castilla Rodríguez
 *
 */
public class OptionMetaFlow extends MetaFlow {
	protected MetaFlow descendant;
	protected double prob;

	public OptionMetaFlow(int id, DecisionMetaFlow parent, double prob) {
		super(id, parent, RandomVariateFactory.getInstance("ConstantVariate", 1));
		this.prob = prob;
	}

	public boolean getFlow(Flow parentFlow, Element e) {
		return descendant.getFlow(parentFlow, e);
	}

	protected void add(MetaFlow descendant) {
		this.descendant = descendant;
	}
	
	public double getProb() {
		return prob;
	}
}
