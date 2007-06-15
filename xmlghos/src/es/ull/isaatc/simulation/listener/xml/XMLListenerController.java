/**
 * 
 */
package es.ull.isaatc.simulation.listener.xml;

import java.util.ArrayList;
import java.util.HashMap;

import es.ull.isaatc.simulation.listener.SimulationListener;
import es.ull.isaatc.simulation.xml.util.XMLMarshallUtils;

/**
 * @author Roberto Muñoz
 */
public class XMLListenerController {

	/** List of available listners */
	public enum AvailableListener { ACTIVITY_QUEUE, RESOURCE_USAGE, ELEMENT_TIME, ELEMENT_INDISP_TIME, ELEMENT_TYPE_TIME };

	/** Simulation listeners list */
	private HashMap<Integer, ArrayList<SimulationListener>> listenerList = new HashMap<Integer, ArrayList<SimulationListener>>();
	
	public void addAll(int id, ArrayList<SimulationListener> listeners) {
		listenerList.put(id, listeners);
	}

	/**
	 * @return a string containing the serialization of each listener to XML.
	 */
	public String getXML() {
		StringBuffer result = new StringBuffer();
		for (ArrayList<SimulationListener> listeners : listenerList.values()) {
			for (SimulationListener listener : listeners)
				result.append(XMLMarshallUtils.marshallObject(listener, "es.ull.simulation.info.xml"));
		}
		return result.toString();
	}
	
	public String toString() {
		StringBuffer result = new StringBuffer();
		int i = 1;
		for (ArrayList<SimulationListener> listeners : listenerList.values()) {
			result.append("\nSimulation " + i++);
			for (SimulationListener listener : listeners)
				result.append(listener.toString());
		}
		return result.toString();		
	}
}
