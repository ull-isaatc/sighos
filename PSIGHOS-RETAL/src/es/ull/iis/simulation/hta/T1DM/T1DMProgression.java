/**
 * 
 */
package es.ull.iis.simulation.hta.T1DM;

import java.util.ArrayList;
import java.util.TreeSet;

/**
 * @author Iván Castilla Rodríguez
 *
 */
public class T1DMProgression {
	private final ArrayList<T1DMProgressionPair> newEvents;
	private final TreeSet<T1DMComorbidity> cancelEvents;

	/**
	 * 
	 */
	public T1DMProgression() {
		newEvents = new ArrayList<>();
		cancelEvents = new TreeSet<>();
	}

	public void addNewEvent(T1DMComorbidity state, long timeToEvent) {
		newEvents.add(new T1DMProgressionPair(state, timeToEvent));
	}
	
	public void addCancelEvent(T1DMComorbidity state) {
		cancelEvents.add(state);
	}

	/**
	 * @return the newEvents
	 */
	public ArrayList<T1DMProgressionPair> getNewEvents() {
		return newEvents;
	}

	/**
	 * @return the cancelEvents
	 */
	public TreeSet<T1DMComorbidity> getCancelEvents() {
		return cancelEvents;
	}
	
}
