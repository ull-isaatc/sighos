/**
 * 
 */
package es.ull.iis.simulation.model.flow;

/**
 * A merge flow which allows only one of the incoming branches to pass. Which one
 * passes depends on the <code>acceptValue</code>.
 * @author Iván Castilla Rodríguez
 */
public abstract class ANDJoinFlow extends MergeFlow {
	/** The number of branches which have to arrive to pass the control thread */
	protected int acceptValue;

	/**
	 * Creates a new AND flow.
	 */
	public ANDJoinFlow() {
		this(true, 0);
	}
	
	/**
	 * Creates a new AND flow
	 * @param acceptValue The number of branches which have to arrive to pass the control thread
	 */
	public ANDJoinFlow(int acceptValue) {
		this(true, acceptValue);
	}
	
	/**
	 * Create a new AND Flow which can be used in a safe context or a general one.
	 * @param safe True for safe context; false in other case
	 */
	public ANDJoinFlow(boolean safe) {
		this(safe, 0);
	}
	
	/**
	 * Create a new AND Flow which can be used in a safe context or a general one.
	 * @param safe True for safe context; false in other case
	 * @param acceptValue The number of branches which have to arrive to pass the control thread
	 */
	public ANDJoinFlow(boolean safe, int acceptValue) {
		super(safe);
		this.acceptValue = acceptValue;
	}
	
	/**
	 * Returns the acceptance value for this flow.
	 * @return The acceptance value for this flow
	 */
	public int getAcceptValue() {
		return acceptValue;
	}	
	
}
