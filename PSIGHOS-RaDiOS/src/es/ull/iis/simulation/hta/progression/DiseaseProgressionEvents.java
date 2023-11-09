/**
 * 
 */
package es.ull.iis.simulation.hta.progression;

import java.util.ArrayList;
import java.util.TreeSet;

/**
 * A change in the progression of a disease. Includes new events related to complications and the cancellation of no longer valid events. 
 * @author Iván Castilla Rodríguez
 *
 */
public class DiseaseProgressionEvents {
	/** New complications to progress to */
	private final ArrayList<DiseaseProgressionEventPair> newEvents;
	/** Already assigned complications that must be cancelled */
	private final TreeSet<DiseaseProgression> cancelEvents;

	/**
	 * Creates a progression instance for T1DM
	 */
	public DiseaseProgressionEvents() {
		newEvents = new ArrayList<>();
		cancelEvents = new TreeSet<>();
	}

	/**
	 * Adds the progression to a new manifestation
	 * @param stage Manifestation to progress to
	 * @param timeToEvent Timestamp when this manifestation is predicted to appear
	 */
	public void addNewEvent(DiseaseProgression stage, long timeToEvent) {
		newEvents.add(new DiseaseProgressionEventPair(stage, timeToEvent));
	}
	
	/**
	 * Adds the cancellation of an already scheduled progression
	 * @param stage Specific complication to cancel
	 */
	public void addCancelEvent(DiseaseProgression stage) {
		cancelEvents.add(stage);
	}

	/**
	 * Returns the list of new complications to be scheduled
	 * @return the list of new complications to be scheduled
	 */
	public ArrayList<DiseaseProgressionEventPair> getNewEvents() {
		return newEvents;
	}

	/**
	 * Returns the list of already scheduled complications to be cancelled 
	 * @return the list of already scheduled complications to be cancelled
	 */
	public TreeSet<DiseaseProgression> getCancelEvents() {
		return cancelEvents;
	}
	
}
