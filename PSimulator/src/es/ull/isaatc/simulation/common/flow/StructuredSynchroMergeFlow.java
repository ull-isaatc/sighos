package es.ull.isaatc.simulation.common.flow;

import es.ull.isaatc.simulation.common.condition.Condition;

/**
 * A {@link StructuredFlow} whose initial step is a {@link MultiChoiceFlow} and whose final step
 * is a {@link SynchronizationFlow}. Meets the Structured Synchronization pattern (WFP7). 
 * @author Yeray Callero
 */
public interface StructuredSynchroMergeFlow extends PredefinedStructuredFlow{
	/**
	 * Variation of <code>addBranch</code> which allows to indicate a condition
	 * @param branch A unique flow defining an internal branch
	 * @param cond This branch's condition.
	 */	
	void addBranch(TaskFlow branch, Condition cond);
	
	/**
	 * Variation of <code>addBranch</code> which allows to indicate a condition
	 * @param initialBranch First step of the internal branch
	 * @param finalBranch Last step of the internal branch
	 * @param cond This branch's condition.
	 */
	void addBranch(InitializerFlow initialBranch, FinalizerFlow finalBranch, Condition cond);
}
