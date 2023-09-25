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
public class DiseaseProgression {
	/** New complications to progress to */
	private final ArrayList<DiseaseProgressionPair> newEvents;
	/** Already assigned complications that must be cancelled */
	private final TreeSet<Manifestation> cancelEvents;

	/**
	 * Creates a progression instance for T1DM
	 */
	public DiseaseProgression() {
		newEvents = new ArrayList<>();
		cancelEvents = new TreeSet<>();
	}

	/**
	 * Adds the progression to a new manifestation
	 * @param stage Manifestation to progress to
	 * @param timeToEvent Timestamp when this manifestation is predicted to appear
	 */
	public void addNewEvent(Manifestation stage, long timeToEvent) {
		newEvents.add(new DiseaseProgressionPair(stage, timeToEvent));
	}
	
	/**
	 * Adds the cancellation of an already scheduled complication
	 * @param stage Specific complication to cancel
	 */
	public void addCancelEvent(Manifestation stage) {
		cancelEvents.add(stage);
	}

	/**
	 * Returns the list of new complications to be scheduled
	 * @return the list of new complications to be scheduled
	 */
	public ArrayList<DiseaseProgressionPair> getNewEvents() {
		return newEvents;
	}

	/**
	 * Returns the list of already scheduled complications to be cancelled 
	 * @return the list of already scheduled complications to be cancelled
	 */
	public TreeSet<Manifestation> getCancelEvents() {
		return cancelEvents;
	}
	
}
