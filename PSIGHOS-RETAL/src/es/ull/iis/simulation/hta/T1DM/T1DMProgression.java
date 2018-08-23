/**
 * 
 */
package es.ull.iis.simulation.hta.T1DM;

/**
 * @author Iván Castilla Rodríguez
 *
 */
public final class T1DMProgression {
	private final T1DMHealthState state;
	private final long timeToEvent;
	/**
	 * 
	 */
	public T1DMProgression(T1DMHealthState state, long timeToEvent) {
		this.state = state;
		this.timeToEvent = timeToEvent;
	}
	/**
	 * @return the state
	 */
	public T1DMHealthState getState() {
		return state;
	}
	/**
	 * @return the timeToEvent
	 */
	public long getTimeToEvent() {
		return timeToEvent;
	}

}
