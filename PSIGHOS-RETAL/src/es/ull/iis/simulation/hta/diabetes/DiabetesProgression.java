/**
 * 
 */
package es.ull.iis.simulation.hta.diabetes;

import java.util.ArrayList;
import java.util.TreeSet;

/**
 * A change in the progression of diabetes. Includes new events related to complications and the cancellation of no longer valid events. 
 * @author Iv�n Castilla Rodr�guez
 *
 */
public class DiabetesProgression {
	/** New complications to progress to */
	private final ArrayList<DiabetesProgressionPair> newEvents;
	/** Already assigned complications that must be cancelled */
	private final TreeSet<DiabetesComplicationStage> cancelEvents;

	/**
	 * Creates a progression instance for T1DM
	 */
	public DiabetesProgression() {
		newEvents = new ArrayList<>();
		cancelEvents = new TreeSet<>();
	}

	/**
	 * Adds the progression to a new complication
	 * @param stage Stage of the complication to progress to
	 * @param timeToEvent Timestamp when this complication is predicted to appear
	 */
	public void addNewEvent(DiabetesComplicationStage stage, long timeToEvent) {
		newEvents.add(new DiabetesProgressionPair(stage, timeToEvent));
	}
	
	/**
	 * Adds the progression to a new complication
	 * @param stage Stage of the complication to progress to
	 * @param timeToEvent Timestamp when this complication is predicted to appear
	 */
	public void addNewEvent(DiabetesComplicationStage stage, long timeToEvent, boolean causesDeath) {
		newEvents.add(new DiabetesProgressionPair(stage, timeToEvent, causesDeath));
	}
	
	/**
	 * Adds the cancellation of an already scheduled complication
	 * @param stage Specific complication to cancel
	 */
	public void addCancelEvent(DiabetesComplicationStage stage) {
		cancelEvents.add(stage);
	}

	/**
	 * Returns the list of new complications to be scheduled
	 * @return the list of new complications to be scheduled
	 */
	public ArrayList<DiabetesProgressionPair> getNewEvents() {
		return newEvents;
	}

	/**
	 * Returns the list of already scheduled complications to be cancelled 
	 * @return the list of already scheduled complications to be cancelled
	 */
	public TreeSet<DiabetesComplicationStage> getCancelEvents() {
		return cancelEvents;
	}
	
}
