/**
 * 
 */
package es.ull.iis.simulation.hta.T1DM;

/**
 * A pair of <complication stage, time> denoting a new complication stage to be scheduled and the time when 
 * it's predicted to start.
 * @author Iván Castilla Rodríguez
 *
 */
public final class T1DMProgressionPair {
	/** A chronic complication stage */
	private final T1DMComplicationStage stage;
	/** The time when the complication stage is predicted to start */
	private final long timeToEvent;
	
	/**
	 * Creates a new pair <complication stage, time>
	 * @param stage A chronic complication stage
	 * @param timeToEvent The time when the complication stage is predicted to start 
	 */
	public T1DMProgressionPair(T1DMComplicationStage stage, long timeToEvent) {
		this.stage = stage;
		this.timeToEvent = timeToEvent;
	}
	/**
	 * Returns the complication stage
	 * @return a complication stage
	 */
	public T1DMComplicationStage getState() {
		return stage;
	}
	/**
	 * Returns the time when the complication stage is predicted to start
	 * @return The time when the complication stage is predicted to start
	 */
	public long getTimeToEvent() {
		return timeToEvent;
	}

	@Override
	public String toString() {
		return stage.name() + "[" + timeToEvent + "]";
	}
}
