/**
 * 
 */
package es.ull.iis.simulation.model.flow;

/**
 * Creates an OR flow which allows all the true incoming branches to pass. 
 * Meets the Multi-Merge pattern (WFP8).
 * @author Iv�n Castilla Rodr�guez
 */
public class MultiMergeFlow extends ORJoinFlow {

	/**
	 * Creates a new MultiMergeFlow.
	 */
	public MultiMergeFlow() {
		super();
	}
	
}
