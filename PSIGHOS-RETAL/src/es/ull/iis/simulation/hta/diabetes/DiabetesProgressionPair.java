/**
 * 
 */
package es.ull.iis.simulation.hta.diabetes;

/**
 * A pair of <complication stage, time> denoting a new complication stage to be scheduled and the time when 
 * it's predicted to start.
 * @author Iván Castilla Rodríguez
 *
 */
public final class DiabetesProgressionPair {
	/** A chronic complication stage */
	private final DiabetesComplicationStage stage;
	/** The time when the complication stage is predicted to start */
	private final long timeToEvent;
	/** If true, the event will produce the death of the patient */
	private final boolean causesDeath;
	
	/**
	 * Creates a new pair <complication stage, time> which will not cause the death of the patient
	 * @param stage A chronic complication stage
	 * @param timeToEvent The time when the complication stage is predicted to start 
	 */
	public DiabetesProgressionPair(DiabetesComplicationStage stage, long timeToEvent) {
		this(stage, timeToEvent, false);
	}

	/**
	 * Creates a new pair <complication stage, time> which may cause the death of the patient
	 * @param stage A chronic complication stage
	 * @param timeToEvent The time when the complication stage is predicted to start 
	 * @param causesDeath If true, the event will produce the death of the patient
	 */
	public DiabetesProgressionPair(DiabetesComplicationStage stage, long timeToEvent, boolean causesDeath) {
		this.stage = stage;
		this.timeToEvent = timeToEvent;
		this.causesDeath = causesDeath;
	}
	
	/**
	 * Returns the complication stage
	 * @return a complication stage
	 */
	public DiabetesComplicationStage getState() {
		return stage;
	}
	
	/**
	 * Returns the time when the complication stage is predicted to start
	 * @return The time when the complication stage is predicted to start
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
		return stage.name() + "[" + timeToEvent + "]";
	}
}
