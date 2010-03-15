package es.ull.isaatc.simulation.groupedExtra3Phase.flow;

import es.ull.isaatc.simulation.groupedExtra3Phase.Simulation;



/**
 * An AND join flow which passes only when all the incoming branches have been activated once. 
 * It is reset when all the incoming branches are activated exactly once (both true and false).
 * Meets the Synchronization pattern (WFP3). 
 * @author ycallero
 */
public class SynchronizationFlow extends ANDJoinFlow implements es.ull.isaatc.simulation.common.flow.SynchronizationFlow {
		
	/**
	 * Create a new SynchronizationFlow.
	 * @param simul Simulation this flow belongs to
	 */
	public SynchronizationFlow(Simulation simul) {
		super(simul);
	}
	
	/**
	 * Create a new Synchronization Flow which can be used in a safe context or a general one.
	 * @param simul Simulation this flow belongs to
	 * @param safe True for safe context; false in other case
	 */
	public SynchronizationFlow(Simulation simul, boolean safe) {
		super(simul, safe);
	}
	
	/**
	 * This method is override to count the total amount of incoming branches
	 * and use it as the accept value.
	 * @param newFlow The last step of the incoming branch. 
	 */
	@Override
	public void addPredecessor(es.ull.isaatc.simulation.common.flow.Flow newFlow) {
		super.addPredecessor(newFlow);
		acceptValue = incomingBranches;
	}	
}
