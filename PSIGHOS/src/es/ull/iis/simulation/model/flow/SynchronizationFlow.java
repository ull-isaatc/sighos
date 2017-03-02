package es.ull.iis.simulation.model.flow;

import es.ull.iis.simulation.model.Simulation;

/**
 * An AND join flow which passes only when all the incoming branches have been activated once. 
 * It is reset when all the incoming branches are activated exactly once (both true and false).
 * Meets the Synchronization pattern (WFP3). 
 * @author ycallero
 */
public class SynchronizationFlow extends ANDJoinFlow {
		
	/**
	 * Create a new SynchronizationFlow.
	 */
	public SynchronizationFlow(Simulation model) {
		super(model);
	}
	
	/**
	 * Create a new Synchronization Flow which can be used in a safe context or a general one.
	 * @param safe True for safe context; false in other case
	 */
	public SynchronizationFlow(Simulation model, boolean safe) {
		super(model, safe);
	}
	
	/**
	 * This method is override to count the total amount of incoming branches
	 * and use it as the accept value.
	 * @param newFlow The last step of the incoming branch. 
	 */
	@Override
	public void addPredecessor(Flow newFlow) {
		super.addPredecessor(newFlow);
		acceptValue = incomingBranches;
	}

}
