package es.ull.isaatc.simulation.model.flow;

import es.ull.isaatc.simulation.model.Model;



/**
 * An AND join flow which passes only when all the incoming branches have been activated once. 
 * It is reset when all the incoming branches are activated exactly once (both true and false).
 * Meets the Synchronization pattern (WFP3). 
 * @author ycallero
 */
public class SynchronizationFlow extends ANDJoinFlow implements es.ull.isaatc.simulation.common.flow.SynchronizationFlow {
		
	/**
	 * Create a new SynchronizationFlow.
	 * @param model Model this flow belongs to
	 */
	public SynchronizationFlow(Model model) {
		super(model);
	}
	
	/**
	 * Create a new Synchronization Flow which can be used in a safe context or a general one.
	 * @param model Model this flow belongs to
	 * @param safe True for safe context; false in other case
	 */
	public SynchronizationFlow(Model model, boolean safe) {
		super(model, safe);
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
