/**
 * 
 */
package es.ull.iis.simulation.hta.T1DM;

/**
 * @author Iván Castilla Rodríguez
 *
 */
public final class T1DMProgressionPair {
	private final T1DMComorbidity state;
	private final long timeToEvent;
	/**
	 * 
	 */
	public T1DMProgressionPair(T1DMComorbidity state, long timeToEvent) {
		this.state = state;
		this.timeToEvent = timeToEvent;
	}
	/**
	 * @return the state
	 */
	public T1DMComorbidity getState() {
		return state;
	}
	/**
	 * @return the timeToEvent
	 */
	public long getTimeToEvent() {
		return timeToEvent;
	}

	@Override
	public String toString() {
		return state.name() + "[" + timeToEvent + "]";
	}
}
