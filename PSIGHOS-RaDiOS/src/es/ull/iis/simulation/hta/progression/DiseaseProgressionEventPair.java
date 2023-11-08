/**
 * 
 */
package es.ull.iis.simulation.hta.progression;

/**
 * A pair of <complication, time> denoting a new complication to be scheduled and the time when 
 * it's predicted to start.
 * @author Iván Castilla Rodríguez
 *
 */
public final class DiseaseProgressionEventPair {
	/** A manifestation */
	private final Manifestation manif;
	/** The time when the complication stage is predicted to start */
	private final long timeToEvent;
	
	/**
	 * Creates a new pair <manifestation, time>
	 * @param manif A manifestation
	 * @param timeToEvent The time when the manifestation is predicted to start 
	 */
	public DiseaseProgressionEventPair(Manifestation manif, long timeToEvent) {
		this.manif = manif;
		this.timeToEvent = timeToEvent;
	}
	
	/**
	 * Returns the complication or acute event
	 * @return a complication or acute event
	 */
	public Manifestation getManifestation() {
		return manif;
	}
	
	/**
	 * Returns the time when the complication is predicted to start
	 * @return The time when the complication is predicted to start
	 */
	public long getTimeToEvent() {
		return timeToEvent;
	}

	@Override
	public String toString() {
		return manif.name() + "[" + timeToEvent + "]";
	}
}
