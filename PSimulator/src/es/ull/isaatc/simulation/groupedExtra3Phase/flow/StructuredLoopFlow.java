package es.ull.isaatc.simulation.groupedExtra3Phase.flow;

import java.util.TreeSet;

import es.ull.isaatc.simulation.groupedExtra3Phase.Simulation;


/**
 * A structured flow which defines a repetitive subflow. Different subclasses
 * of this class represent different loop structures: while-do, do-while, for...
 * Meets the Structured Loop pattern (WFP21). 
 * @author ycallero
 */
public abstract class StructuredLoopFlow extends StructuredFlow implements es.ull.isaatc.simulation.common.flow.StructuredLoopFlow {
	
	/**
	 * Create a new StructuredLoopFlow starting in <code>initialSubFlow</code> and 
	 * finishing in <code>finalSubFlow</code>.
	 * @param simul Simulation this flow belongs to
	 * @param initialSubFlow First step of the internal subflow
	 * @param finalSubFlow Last step of the internal subflow
	 */
	public StructuredLoopFlow(Simulation simul, InitializerFlow initialSubFlow, FinalizerFlow finalSubFlow) {
		super(simul);
		initialFlow = initialSubFlow;
		finalFlow = finalSubFlow;
		final TreeSet<es.ull.isaatc.simulation.common.flow.Flow> visited = new TreeSet<es.ull.isaatc.simulation.common.flow.Flow>();
		initialFlow.setRecursiveStructureLink(this, visited);
	}

	/**
	 * Create a new StructuredLoopFlow consisting of a unique flow.
	 * @param simul Simulation this flow belongs to
	 * @param subFlow A unique flow defining an internal subflow
	 */
	public StructuredLoopFlow(Simulation simul, TaskFlow subFlow) {
		this(simul, subFlow, subFlow);
	}
}

