package es.ull.iis.simulation.model.flow;

/**
 * Creates an OR flow which allows all the true incoming branches to pass. The 
 * outgoing branch is activated only once when several incoming barnches arrive at
 * the same simulation time. 
 * Meets the Simple Merge pattern (WFP5).
 * @author ycallero
 *
 */
public class SimpleMergeFlow extends ORJoinFlow {
	
	/**
	 * Creates a new SimpleMergeFlow.
	 * @param simul Simulation this flow belongs to.
	 */
	public SimpleMergeFlow() {
		super();
	}

}
