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
	/** A new stage, manifestation, group of manifestations... */
	private final DiseaseProgression progression;
	/** The time when the disease progression is predicted to start */
	private final long timeToEvent;
	
	/**
	 * Creates a new pair <DiseaseProgression, time>
	 * @param progression A new stage, manifestation, group of manifestations...
	 * @param timeToEvent The time when the disease progression is predicted to start 
	 */
	public DiseaseProgressionEventPair(DiseaseProgression progression, long timeToEvent) {
		this.progression = progression;
		this.timeToEvent = timeToEvent;
	}
	
	/**
	 * Returns the progression of the disease
	 * @return the progression of the disease
	 */
	public DiseaseProgression getDiseaseProgression() {
		return progression;
	}
	
	/**
	 * Returns the time when the disease progression is predicted to start
	 * @return The time when the disease progression is predicted to start
	 */
	public long getTimeToEvent() {
		return timeToEvent;
	}

	@Override
	public String toString() {
		return progression.name() + "[" + timeToEvent + "]";
	}
}
