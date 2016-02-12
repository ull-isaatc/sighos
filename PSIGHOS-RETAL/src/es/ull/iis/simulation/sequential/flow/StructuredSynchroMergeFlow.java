package es.ull.iis.simulation.sequential.flow;

import java.util.TreeSet;

import es.ull.iis.simulation.condition.Condition;
import es.ull.iis.simulation.condition.TrueCondition;
import es.ull.iis.simulation.sequential.Simulation;

/**
 * A structured flow whose initial step is a multi-choice flow and whose final step
 * is a synchronization. Meets the Structured Synchronization pattern (WFP7). 
 * @author ycallero
 */
public class StructuredSynchroMergeFlow extends PredefinedStructuredFlow implements es.ull.iis.simulation.core.flow.StructuredSynchroMergeFlow {
	
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
	
	public void addBranch(es.ull.iis.simulation.core.flow.TaskFlow branch, Condition cond) {
		addBranch(branch, branch, cond);
	}
	
	/**
	 * Variation of <code>addBranch</code> which allows to indicate a condition
	 * @param initialBranch First step of the internal branch
	 * @param finalBranch Last step of the internal branch
	 * @param cond This branch's condition.
	 */
	public void addBranch(es.ull.iis.simulation.core.flow.InitializerFlow initialBranch, es.ull.iis.simulation.core.flow.FinalizerFlow finalBranch, Condition cond) {
		final TreeSet<es.ull.iis.simulation.core.flow.Flow> visited = new TreeSet<es.ull.iis.simulation.core.flow.Flow>(); 
		initialBranch.setRecursiveStructureLink(this, visited);
		((MultiChoiceFlow)initialFlow).link(initialBranch, cond);
		finalBranch.link(finalFlow);
	}
	
	@Override
	public void addBranch(es.ull.iis.simulation.core.flow.InitializerFlow initialBranch, es.ull.iis.simulation.core.flow.FinalizerFlow finalBranch) {
		addBranch(initialBranch, finalBranch, new TrueCondition());		
	}
	
	@Override
	public void addBranch(es.ull.iis.simulation.core.flow.TaskFlow initialBranch) {
		addBranch(initialBranch, new TrueCondition());
	}
}
