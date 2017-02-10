package es.ull.iis.simulation.core.flow;

import es.ull.iis.simulation.condition.Condition;
import es.ull.iis.simulation.core.WorkThread;

/**
 * A {@link StructuredFlow} whose initial step is a {@link MultiChoiceFlow} and whose final step
 * is a {@link SynchronizationFlow}. Meets the Structured Synchronization pattern (WFP7). 
 * @author Yeray Callero
 */
public interface StructuredSynchroMergeFlow<WT extends WorkThread<?>> extends PredefinedStructuredFlow<WT>{
	/**
	 * Variation of <code>addBranch</code> which allows to indicate a condition
	 * @param branch A unique flow defining an internal branch
	 * @param cond This branch's condition.
	 */	
	void addBranch(TaskFlow<WT> branch, Condition cond);
	
	/**
	 * Variation of <code>addBranch</code> which allows to indicate a condition
	 * @param initialBranch First step of the internal branch
	 * @param finalBranch Last step of the internal branch
	 * @param cond This branch's condition.
	 */
	void addBranch(InitializerFlow<WT> initialBranch, FinalizerFlow<WT> finalBranch, Condition cond);
}
