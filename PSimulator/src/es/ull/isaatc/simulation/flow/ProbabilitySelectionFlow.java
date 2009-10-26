/**
 * 
 */
package es.ull.isaatc.simulation.flow;

import java.util.ArrayList;
import java.util.Collection;

import es.ull.isaatc.simulation.model.Model;

/**
 * A flow which selects one outgoing branch among a set of them by using a probability value.
 * Each outgoing branch has a value (0.0 - 1.0] expressing the probability of being chosen of
 * such branch. 
 * @author Iván Castilla Rodríguez
 *
 */
public class ProbabilitySelectionFlow extends MultipleSuccessorFlow {
	/** List of probabilities associated to each outgoing branch */
	protected final ArrayList<Double> probabilities;
	/** The sum total of all the outgoing branches. This value should be 1.0, but it's 
	 * explicitly declared to avoid misdefinitions. 
	 */
	protected double sum = 0.0;

	/**
	 * Creates a new Probability Selection flow associated to the specified simulation
	 * @param model Model this flow belongs to
	 */
	public ProbabilitySelectionFlow(Model model) {
		super(model);
		probabilities = new ArrayList<Double>();
	}

	/**
	 * Adds a probabilistic flow's successor.
	 * @param successor This flow's successor
	 * @param prob The probability of this successor of being chosen
	 */
	public void link(Flow successor, double prob) {
		super.link(successor);
		probabilities.add(prob);
		sum += prob;
	}

	/**
	 * Adds a probabilistic flow's successor. A probability of 1.0 is associated to this successor
	 * @param successor This flow's successor
	 */
	public void link(Flow successor) {
		link(successor, 1.0);
	}
	
	/**
	 * Adds a collection of probabilistic flow's successor. 
	 * Size of <code>succList</code> and <code>probList</code> must agree.
	 * @param succList This flow's successors
	 * @param probList The probability of these successors of being chosen
	 */
	public void link(Collection<Flow> succList, Collection<Double> probList) {
		super.link(succList);
		probabilities.addAll(probList);	
		for (double val : probList)
			sum += val;
	}
	
	/**
	 * Adds a collection of probabilistic flow's successor. The same probability
	 * is assigned to each successor. 
	 * @param succList This flow's successors
	 */
	public void link(Collection<Flow> succList) {
		ArrayList<Double> probList = new ArrayList<Double>();
		for (int i = 0; i < succList.size(); i++)
			probList.add(1.0 / (double)succList.size());
		link(succList, probList);
	}
}
