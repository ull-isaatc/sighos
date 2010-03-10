package es.ull.isaatc.simulation.bonnThreaded.flow;

import es.ull.isaatc.simulation.bonnThreaded.Simulation;


/**
 * An AND join flow which allows only the n-st true incoming branch to pass. It is
 * reset when all the incoming branches are activated exactly once.
 * Meets the Blocking Partial Join pattern (WFP31). 
 * @author ycallero
 */
public class PartialJoinFlow extends ANDJoinFlow implements es.ull.isaatc.simulation.common.flow.PartialJoinFlow {
	
	/**
	 * Creates a new PartialJoinFlow.
	 * @param simul Simulation this flow belongs to
	 * @param acceptValue Number of incoming branches which activate the flow
	 */
	public PartialJoinFlow(Simulation simul, int acceptValue) {
		super(simul, acceptValue);
	}

	/**
	 * Create a new Partial Join Flow which can be used in a safe context or a general one.
	 * @param simul Simulation this flow belongs to
	 * @param safe True for safe context; false in other case
	 */
	public PartialJoinFlow(Simulation simul, boolean safe, int acceptValue) {
		super(simul, safe, acceptValue);
	}
	
}
