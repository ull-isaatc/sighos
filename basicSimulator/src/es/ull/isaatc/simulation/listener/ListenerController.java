/**
 * 
 */
package es.ull.isaatc.simulation.listener;

import java.util.ArrayList;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.Semaphore;

import es.ull.isaatc.simulation.info.SimulationEndInfo;
import es.ull.isaatc.simulation.info.SimulationInfo;
import es.ull.isaatc.simulation.info.SimulationObjectInfo;
import es.ull.isaatc.simulation.info.SimulationStartInfo;
import es.ull.isaatc.simulation.info.TimeChangeInfo;

/**
 * Manages the listeners during the simulation time. Then, it processes 
 * the information provided by the listeners.
 * @author Iván Castilla Rodríguez
 *
 */
public class ListenerController extends Thread {
	/** List of info listeners */
	private ArrayList<EventListener> listeners;
	/** List of simulation listeners */
	private ArrayList<SimulationListener> simListeners;
	/** List of simulation object listeners */
	private ArrayList<SimulationObjectListener> simObjListeners;
	/** List of time change listeners */
	private ArrayList<TimeChangeListener> timeListeners;
	/** The queue of pending info */
	private ConcurrentLinkedQueue<SimulationInfo> infoQueue;
	/** Lock to control whether is a new info event or not */
	private Semaphore sem;

	/**
	 * 
	 */
	public ListenerController() {
		simListeners = new ArrayList<SimulationListener>();
		simObjListeners = new ArrayList<SimulationObjectListener>();
		timeListeners = new ArrayList<TimeChangeListener>();
		listeners = new ArrayList<EventListener>();
		infoQueue = new ConcurrentLinkedQueue<SimulationInfo>();
		sem = new Semaphore(1);
	}

	public void run() {
		boolean noEnd = true;
		while(noEnd) {
			try {
				sem.acquire();
				SimulationInfo info = infoQueue.poll();
				if (info instanceof SimulationObjectInfo)
					for (SimulationObjectListener il : simObjListeners)
						il.infoEmited((SimulationObjectInfo)info);
				else if (info instanceof SimulationStartInfo)  
					for (SimulationListener il : simListeners)
						il.infoEmited((SimulationStartInfo)info);
				else if (info instanceof SimulationEndInfo) {  
					for (SimulationListener il : simListeners)
						il.infoEmited((SimulationEndInfo)info);
					noEnd = false;
				}
				else if (info instanceof TimeChangeInfo)
					for (TimeChangeListener il : timeListeners)
						il.infoEmited((TimeChangeInfo)info);			
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
		end();
	}
	
	public void end() {
		
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
	 * Informs the simulation's listener controller of a new event.
	 * 
	 * @param info
	 *            An event that contains simulation information.
	 */
	public void notifyListeners(SimulationInfo info) {
		infoQueue.add(info);
		sem.release();
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
