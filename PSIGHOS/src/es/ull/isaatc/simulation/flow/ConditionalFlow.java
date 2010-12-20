package es.ull.isaatc.simulation.flow;

import java.util.Collection;

import es.ull.isaatc.simulation.condition.Condition;

/**
 * A {@link MultipleSuccessorFlow} whose successors are conditioned, that is, the successor
 * can only be activated if certain condition is met. When adding successors, if no condition 
 * is indicated, it is supposed to be <tt>true</tt>.
 * @author Yeray Callero
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
	 * Size of <tt>succList</tt> and <tt>condList</tt> must be equal. 
	 * @param succList This flow's successors
	 * @param condList The conditions attached to each successor
	 */
	public void link(Collection<Flow> succList, Collection<Condition> condList);
	
}
