package es.ull.isaatc.simulation.common.flow;

import java.util.Collection;

import es.ull.isaatc.simulation.common.condition.Condition;

/**
 * A multiple successor flow whose successors are conditioned, that is, the successor
 * can only be activated if the condition is met. When adding successors, if no condition 
 * is indicated, it is supposed to be true.
 * @author ycallero
 *
 */
public interface ConditionalFlow extends MultipleSuccessorFlow {
	/**
	 * Adds a conditioned flow's successor. 
	 * @param successor This flow's successor
	 * @param cond The condition that has to be met to invoke the successor
	 */
	public void link(Flow successor, Condition cond);

	/**
	 * Adds a collection of conditioned flow's successors. 
	 * Size of <code>succList</code> and <code>condList</code> must agree. 
	 * @param succList This flow's successors
	 * @param condList The conditions attached to each successor
	 */
	public void link(Collection<Flow> succList, Collection<Condition> condList);
	
}