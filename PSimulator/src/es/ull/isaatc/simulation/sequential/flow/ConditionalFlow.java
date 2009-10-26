package es.ull.isaatc.simulation.sequential.flow;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;

import es.ull.isaatc.simulation.sequential.Simulation;
import es.ull.isaatc.simulation.sequential.condition.Condition;
import es.ull.isaatc.simulation.sequential.condition.TrueCondition;

/**
 * A multiple successor flow whose successors are conditioned, that is, the successor
 * can only be activated if the condition is met. When adding successors, if no condition 
 * is indicated, it is supposed to be true.
 * @author ycallero
 *
 */
public abstract class ConditionalFlow extends MultipleSuccessorFlow {
	/** Condition list associated to the successor list. */
	protected final ArrayList<Condition> conditionList;
	
	/**
	 * Creates a new ConditionalFlow.
	 * @param simul Simulation this flow belongs to.
	 */
	public ConditionalFlow(Simulation simul) {
		super(simul);
		conditionList = new ArrayList<Condition>();
	}

	/**
	 * Adds a conditioned flow's successor. The associated condition is set to true by default.
	 * This method must invoke <code>successor.addPredecessor</code> to build the graph properly. 
	 * @param successor This flow's successor.
	 */
	@Override
	public void link(Flow successor) {
		link(successor, new TrueCondition());
	}
	
	/**
	 * Adds a conditioned flow's successor. 
	 * @param successor This flow's successor
	 * @param cond The condition that has to be met to invoke the successor
	 */
	public void link(Flow successor, Condition cond) {
		super.link(successor);
		conditionList.add(cond);
	}

	/**
	 * Adds a collection of conditioned flow's successors. The associated condition is set to 
	 * true by default.
	 * @param succList This flow's successors
	 */
	@Override
	public void link(Collection<Flow> succList) {
		link(succList, Collections.nCopies(succList.size(), (Condition)new TrueCondition()));
	}

	/**
	 * Adds a collection of conditioned flow's successors. 
	 * Size of <code>succList</code> and <code>condList</code> must agree. 
	 * @param succList This flow's successors
	 * @param condList The conditions attached to each successor
	 */
	public void link(Collection<Flow> succList, Collection<Condition> condList) {
		super.link(succList);
		conditionList.addAll(condList);
	}
	
}
