package es.ull.isaatc.simulation.flow;

import es.ull.isaatc.simulation.condition.Condition;
import es.ull.isaatc.simulation.condition.TrueCondition;
import es.ull.isaatc.simulation.model.Model;

/**
 * A structured flow whose initial step is a multi-choice flow and whose final step
 * is a synchronization. Meets the Structured Synchronization pattern (WFP7). 
 * @author ycallero
 */
public class StructuredSynchroMergeFlow extends PredefinedStructuredFlow{
	
	/**
	 * Create a new StructuredSynchroMergeMetaFlow.
	 * @param model Model this flow belongs to.
	 */
	public StructuredSynchroMergeFlow(Model model) {
		super(model);
		initialFlow = new MultiChoiceFlow(model);
		initialFlow.setParent(this);
		finalFlow = new SynchronizationFlow(model);
		finalFlow.setParent(this);
	}

	/**
	 * Variation of <code>addBranch</code> which allows to indicate a condition
	 * @param branch A unique flow defining an internal branch
	 * @param cond This branch's condition.
	 */
	
	public void addBranch(TaskFlow branch, Condition cond) {
		addBranch(branch, branch, cond);
	}
	
	/**
	 * Variation of <code>addBranch</code> which allows to indicate a condition
	 * @param initialBranch First step of the internal branch
	 * @param finalBranch Last step of the internal branch
	 * @param cond This branch's condition.
	 */
	public void addBranch(InitializerFlow initialBranch, FinalizerFlow finalBranch, Condition cond) {
		initialBranch.setRecursiveStructureLink(this);
		((MultiChoiceFlow)initialFlow).link(initialBranch, cond);
		finalBranch.link(finalFlow);
	}
	
	@Override
	public void addBranch(InitializerFlow initialBranch, FinalizerFlow finalBranch) {
		addBranch(initialBranch, finalBranch, new TrueCondition());		
	}
	
	@Override
	public void addBranch(TaskFlow initialBranch) {
		addBranch(initialBranch, new TrueCondition());
	}
}
