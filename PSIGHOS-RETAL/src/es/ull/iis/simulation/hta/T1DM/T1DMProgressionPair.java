/**
 * 
 */
package es.ull.iis.simulation.hta.T1DM;

/**
 * A pair of <complication, time> denoting a new complication to be scheduled and the time when it's predicted to appear.
 * @author Iván Castilla Rodríguez
 *
 */
public final class T1DMProgressionPair {
	/** A specific complication */
	private final T1DMComorbidity state;
	/** The time when the complication is predicted to appear */
	private final long timeToEvent;
	
	/**
	 * Creates a new pair <complication, time>
	 * @param state A specific complication
	 * @param timeToEvent The time when the complication is predicted to appear 
	 */
	public T1DMProgressionPair(T1DMComorbidity state, long timeToEvent) {
		this.state = state;
		this.timeToEvent = timeToEvent;
	}
	/**
	 * Returns a specific complication
	 * @return a specific complication
	 */
	public T1DMComorbidity getState() {
		return state;
	}
	/**
	 * Returns the time when the complication is predicted to appear
	 * @return The time when the complication is predicted to appear
	 */
	public long getTimeToEvent() {
		return timeToEvent;
	}

	@Override
	public String toString() {
		return state.name() + "[" + timeToEvent + "]";
	}
}
