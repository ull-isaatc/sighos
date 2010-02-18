package es.ull.isaatc.simulation.xoptGroupedThreaded.flow;

import es.ull.isaatc.simulation.xoptGroupedThreaded.Simulation;


/**
 * An AND join flow which allows only the first true incoming branch to pass. It is
 * reset when all the incoming branches are activated exactly once.
 * Meets the Blocking Discriminator pattern (WFP28). 
 * @author ycallero
 */
public class DiscriminatorFlow extends ANDJoinFlow implements es.ull.isaatc.simulation.common.flow.DiscriminatorFlow {
	
	/**
	 * Create a new DiscriminatorFlow.
	 * @param simul Simulation this flow belongs to.
	 */
	public DiscriminatorFlow(Simulation simul) {
		super(simul, 1);
	}

	/**
	 * Create a new discriminator Flow which can be used in a safe context or a general one.
	 * @param simul Simulation this flow belongs to
	 * @param safe True for safe context; false in other case
	 */
	public DiscriminatorFlow(Simulation simul, boolean safe) {
		super(simul, safe, 1);
	}
	
}
