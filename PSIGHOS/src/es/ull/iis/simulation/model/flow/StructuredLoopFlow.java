package es.ull.iis.simulation.model.flow;

import java.util.TreeSet;

import es.ull.iis.simulation.model.Simulation;

/**
 * A structured flow which defines a repetitive subflow. Different subclasses
 * of this class represent different loop structures: while-do, do-while, for...
 * Meets the Structured Loop pattern (WFP21). 
 * @author ycallero
 */
// TODO: Consider merge StructuredLoopFlows into PredefinedStructuredFlows
public abstract class StructuredLoopFlow extends StructuredFlow {
	
	/**
	 * Create a new StructuredLoopFlow starting in <code>initialSubFlow</code> and 
	 * finishing in <code>finalSubFlow</code>.
	 * @param initialSubFlow First step of the internal subflow
	 * @param finalSubFlow Last step of the internal subflow
	 */
	public StructuredLoopFlow(Simulation model, InitializerFlow initialSubFlow, FinalizerFlow finalSubFlow) {
		super(model);
		initialFlow = initialSubFlow;
		finalFlow = finalSubFlow;
		final TreeSet<Flow> visited = new TreeSet<Flow>(); 
		initialFlow.setRecursiveStructureLink(this, visited);
	}

	/**
	 * Create a new StructuredLoopFlow consisting of a unique flow.
	 * @param subFlow A unique flow defining an internal subflow
	 */
	public StructuredLoopFlow(Simulation model, TaskFlow subFlow) {
		this(model, subFlow, subFlow);
	}
}

