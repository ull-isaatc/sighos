package es.ull.iis.simulation.model.flow;

/**
 * An AND join flow which allows only the n-st true incoming branch to pass. It is
 * reset when all the incoming branches are activated exactly once.
 * Meets the Blocking Partial Join pattern (WFP31). 
 * @author ycallero
 */
public class PartialJoinFlow extends ANDJoinFlow {
	
	/**
	 * Creates a new PartialJoinFlow.
	 * @param acceptValue Number of incoming branches which activate the flow
	 */
	public PartialJoinFlow(int acceptValue) {
		super(acceptValue);
	}

	/**
	 * Create a new Partial Join Flow which can be used in a safe context or a general one.
	 * @param safe True for safe context; false in other case
	 * @param acceptValue Number of incoming branches which activate the flow
	 */
	public PartialJoinFlow(boolean safe, int acceptValue) {
		super(safe, acceptValue);
	}
	
}
