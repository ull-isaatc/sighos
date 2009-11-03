package es.ull.isaatc.simulation.sequential.flow;

import es.ull.isaatc.simulation.sequential.Simulation;
import es.ull.isaatc.simulation.sequential.condition.Condition;
import es.ull.isaatc.simulation.sequential.condition.TrueCondition;

/**
 * A structured flow whose initial step is a multi-choice flow and whose final step
 * is a synchronization. Meets the Structured Synchronization pattern (WFP7). 
 * @author ycallero
 */
public class StructuredSynchroMergeFlow extends PredefinedStructuredFlow{
	
	/**
	 * Create a new StructuredSynchroMergeMetaFlow.
	 * @param simul Simulation this flow belongs to
	 */
	public StructuredSynchroMergeFlow(Simulation simul) {
		super(simul);
		initialFlow = new MultiChoiceFlow(simul);
		initialFlow.setParent(this);
		finalFlow = new SynchronizationFlow(simul);
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