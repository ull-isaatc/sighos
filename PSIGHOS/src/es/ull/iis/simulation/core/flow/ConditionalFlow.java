package es.ull.iis.simulation.core.flow;

import java.util.Collection;

import es.ull.iis.simulation.condition.Condition;
import es.ull.iis.simulation.core.WorkThread;

/**
 * A {@link MultipleSuccessorFlow} whose successors are conditioned, that is, the successor
 * can only be activated if certain condition is met. When adding successors, if no condition 
 * is indicated, it is supposed to be <tt>true</tt>.
 * @author Yeray Callero
 *
 */
public interface ConditionalFlow<WT extends WorkThread<?>> extends MultipleSuccessorFlow<WT> {
	/**
	 * Adds a conditioned flow's successor. 
	 * @param successor This flow's successor
	 * @param cond The condition that has to be met to invoke the successor
	 * @return TODO
	 */
	public Flow<WT> link(Flow<WT> successor, Condition cond);

	/**
	 * Adds a collection of conditioned flow's successors. 
	 * Size of <tt>succList</tt> and <tt>condList</tt> must be equal. 
	 * @param succList This flow's successors
	 * @param condList The conditions attached to each successor
	 */
	public void link(Collection<Flow<WT>> succList, Collection<Condition> condList);
	
}
