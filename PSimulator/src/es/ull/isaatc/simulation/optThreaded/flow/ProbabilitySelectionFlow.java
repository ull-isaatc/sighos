/**
 * 
 */
package es.ull.isaatc.simulation.optThreaded.flow;

import java.util.ArrayList;
import java.util.Collection;

import es.ull.isaatc.simulation.optThreaded.Simulation;
import es.ull.isaatc.simulation.optThreaded.WorkThread;

/**
 * A flow which selects one outgoing branch among a set of them by using a probability value.
 * Each outgoing branch has a value (0.0 - 1.0] expressing the probability of being chosen of
 * such branch. 
 * @author Iván Castilla Rodríguez
 *
 */
public class ProbabilitySelectionFlow extends MultipleSuccessorFlow implements es.ull.isaatc.simulation.common.flow.ProbabilitySelectionFlow {
	/** List of probabilities associated to each outgoing branch */
	protected final ArrayList<Double> probabilities;
	/** The sum total of all the outgoing branches. This value should be 1.0, but it's 
	 * explicitly declared to avoid misdefinitions. 
	 */
	protected double sum = 0.0;

	/**
	 * Creates a new Probability Selection flow associated to the specified simulation
	 * @param simul The simulation this flow belongs to
	 */
	public ProbabilitySelectionFlow(Simulation simul) {
		super(simul);
		probabilities = new ArrayList<Double>();
	}

	/**
	 * Adds a probabilistic flow's successor.
	 * @param successor This flow's successor
	 * @param prob The probability of this successor of being chosen
	 */
	public void link(es.ull.isaatc.simulation.common.flow.Flow successor, double prob) {
		super.link(successor);
		probabilities.add(prob);
		sum += prob;
	}

	/**
	 * Adds a probabilistic flow's successor. A probability of 1.0 is associated to this successor
	 * @param successor This flow's successor
	 */
	public void link(es.ull.isaatc.simulation.common.flow.Flow successor) {
		link(successor, 1.0);
	}
	
	/**
	 * Adds a collection of probabilistic flow's successor. 
	 * Size of <code>succList</code> and <code>probList</code> must agree.
	 * @param succList This flow's successors
	 * @param probList The probability of these successors of being chosen
	 */
	public void link(Collection<es.ull.isaatc.simulation.common.flow.Flow> succList, Collection<Double> probList) {
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
	public void link(Collection<es.ull.isaatc.simulation.common.flow.Flow> succList) {
		ArrayList<Double> probList = new ArrayList<Double>();
		for (int i = 0; i < succList.size(); i++)
			probList.add(1.0 / (double)succList.size());
		link(succList, probList);
	}

	@Override
	public void next(WorkThread wThread) {
		super.next(wThread);
		if (wThread.isExecutable()) {
			double ref = Math.random() * sum;
			double aux = 0.0;
			for (int i = 0; i < successorList.size(); i++) {
				boolean res = (ref >= aux) && (ref < (aux + probabilities.get(i)));
				aux += probabilities.get(i);
				successorList.get(i).request(wThread.getInstanceSubsequentWorkThread(res, this, wThread.getToken()));					
			}			
		}
		else
			for (int i = 0; i < successorList.size(); i++)
				successorList.get(i).request(wThread.getInstanceSubsequentWorkThread(false, this, wThread.getToken()));
		wThread.notifyEnd();
	}
}
