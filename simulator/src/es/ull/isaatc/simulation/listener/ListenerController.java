/**
 * 
 */
package es.ull.isaatc.simulation.listener;

import java.util.ArrayList;

import es.ull.isaatc.simulation.info.SimulationEndInfo;
import es.ull.isaatc.simulation.info.SimulationObjectInfo;
import es.ull.isaatc.simulation.info.SimulationStartInfo;
import es.ull.isaatc.simulation.info.TimeChangeInfo;

/**
 * @author Iván Castilla Rodríguez
 *
 */
public class ListenerController {
	/** List of info listeners */
	private ArrayList<EventListener> listeners;
	/** List of simulation listeners */
	private ArrayList<SimulationListener> simListeners;
	/** List of simulation object listeners */
	private ArrayList<SimulationObjectListener> simObjListeners;
	/** List of time change listeners */
	private ArrayList<TimeChangeListener> timeListeners;

	/**
	 * 
	 */
	public ListenerController() {
		simListeners = new ArrayList<SimulationListener>();
		simObjListeners = new ArrayList<SimulationObjectListener>();
		timeListeners = new ArrayList<TimeChangeListener>();
		listeners = new ArrayList<EventListener>();
	}

	/**
	 * Listener adapter. Adds a new listener to the listener list.
	 * 
	 * @param listener
	 *            A simulation's listener
	 */
	public void addListener(EventListener listener) {
		listeners.add(listener);
		if (listener instanceof SimulationListener)
			simListeners.add((SimulationListener)listener);
		if (listener instanceof SimulationObjectListener)
			simObjListeners.add((SimulationObjectListener)listener);
		if (listener instanceof TimeChangeListener)
			timeListeners.add((TimeChangeListener)listener);
	}

	/**
	 * Informs the simulation's listeners of a new event.
	 * 
	 * @param info
	 *            An event that contains simulation information.
	 */
	public synchronized void notifyListeners(SimulationObjectInfo info) {
		for (SimulationObjectListener il : simObjListeners)
			il.infoEmited(info);
	}

	/**
	 * Informs the simulation's listeners of a new event.
	 * 
	 * @param info
	 *            An event that contains simulation information.
	 */
	public synchronized void notifyListeners(SimulationStartInfo info) {
		for (SimulationListener il : simListeners)
			il.infoEmited(info);
	}

	/**
	 * Informs the simulation's listeners of a new event.
	 * 
	 * @param info
	 *            An event that contains simulation information.
	 */
	public synchronized void notifyListeners(SimulationEndInfo info) {
		for (SimulationListener il : simListeners)
			il.infoEmited(info);
	}

	/**
	 * Informs the simulation's listeners of a new event.
	 * 
	 * @param info
	 *            An event that contains simulation information.
	 */
	public synchronized void notifyListeners(TimeChangeInfo info) {
		for (TimeChangeListener il : timeListeners)
			il.infoEmited(info);
	}

	/**
	 * Returns the list of listeners of the simulation
	 * @return the list of listeners of the simulation
	 */
	public ArrayList<EventListener> getListeners() {
		return listeners;
	}

	public String [] getListenerResults() {
		String []listenerRes = new String[getListeners().size()];
		int count = 0;
		for (EventListener listener : listeners)
			listenerRes[count++] = listener.toString();
		return listenerRes;
	}

}
