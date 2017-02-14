package es.ull.iis.simulation.model.flow;

/**
 * An AND join flow which allows only the first true incoming branch to pass. It is
 * reset when all the incoming branches are activated exactly once.
 * Meets the Blocking Discriminator pattern (WFP28). 
 * @author ycallero
 */
public class DiscriminatorFlow extends ANDJoinFlow {
	
	/**
	 * Create a new DiscriminatorFlow.
	 * @param simul Simulation this flow belongs to.
	 */
	public DiscriminatorFlow() {
		super(1);
	}

	/**
	 * Create a new discriminator Flow which can be used in a safe context or a general one.
	 * @param simul Simulation this flow belongs to
	 * @param safe True for safe context; false in other case
	 */
	public DiscriminatorFlow(boolean safe) {
		super(safe, 1);
	}
	
}
