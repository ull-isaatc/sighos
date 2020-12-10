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
public final class DiseaseProgressionPair {
	/** A manifestation */
	private final Manifestation manif;
	/** The time when the complication stage is predicted to start */
	private final long timeToEvent;
	/** If true, the event will produce the death of the patient */
	private final boolean causesDeath;
	
	/**
	 * Creates a new pair <complication, time> which will not cause the death of the patient
	 * @param manif A complication stage or acute event
	 * @param timeToEvent The time when the complication is predicted to start 
	 */
	public DiseaseProgressionPair(Manifestation manif, long timeToEvent) {
		this(manif, timeToEvent, false);
	}

	/**
	 * Creates a new pair <complication, time> which may cause the death of the patient
	 * @param manif A complication stage or acute event
	 * @param timeToEvent The time when the complication is predicted to start 
	 * @param causesDeath If true, the event will produce the death of the patient
	 */
	public DiseaseProgressionPair(Manifestation manif, long timeToEvent, boolean causesDeath) {
		this.manif = manif;
		this.timeToEvent = timeToEvent;
		this.causesDeath = causesDeath;
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
	
	/**
	 * Returns true if the event will produce the death of the patient; false otherwise 
	 * @return True if the event will produce the death of the patient; false otherwise
	 */
	public boolean causesDeath() {
		return causesDeath;
	}

	@Override
	public String toString() {
		return manif.name() + "[" + timeToEvent + "]";
	}
}
