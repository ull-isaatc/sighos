/**
 * 
 */
package es.ull.iis.simulation.hta.T1DM;

import java.util.ArrayList;
import java.util.TreeSet;

/**
 * A change in the progression of T1DM. Includes new events related to complications and the cancellation of no longer valid events. 
 * @author Iván Castilla Rodríguez
 *
 */
public class T1DMProgression {
	/** New complications to progress to */
	private final ArrayList<T1DMProgressionPair> newEvents;
	/** Already assigned complications that must be cancelled */
	private final TreeSet<T1DMComorbidity> cancelEvents;

	/**
	 * Creates a progression instance for T1DM
	 */
	public T1DMProgression() {
		newEvents = new ArrayList<>();
		cancelEvents = new TreeSet<>();
	}

	/**
	 * Adds the progression to a new complication
	 * @param state Spscific complication to progress to
	 * @param timeToEvent Timestamp when this complication is predicted to appear
	 */
	public void addNewEvent(T1DMComorbidity state, long timeToEvent) {
		newEvents.add(new T1DMProgressionPair(state, timeToEvent));
	}
	
	/**
	 * Adds the cancellation of an already scheduled complication
	 * @param state Specific complication to cancel
	 */
	public void addCancelEvent(T1DMComorbidity state) {
		cancelEvents.add(state);
	}

	/**
	 * Returns the list of new complications to be scheduled
	 * @return the list of new complications to be scheduled
	 */
	public ArrayList<T1DMProgressionPair> getNewEvents() {
		return newEvents;
	}

	/**
	 * Returns the list of already scheduled complications to be cancelled 
	 * @return the list of already scheduled complications to be cancelled
	 */
	public TreeSet<T1DMComorbidity> getCancelEvents() {
		return cancelEvents;
	}
	
}
