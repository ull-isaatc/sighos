/**
 * 
 */
package es.ull.isaatc.simulation.listener.xml;

import java.util.ArrayList;
import java.util.HashMap;

import es.ull.isaatc.simulation.listener.SimulationListener;
import es.ull.isaatc.simulation.listener.xml.ListenerInfo.AverageResults;
import es.ull.isaatc.simulation.listener.xml.ListenerInfo.SimulationResults;
import es.ull.isaatc.simulation.xml.util.XMLMarshallUtils;

/**
 * @author Roberto Muñoz
 * @author Yurena Garcia-Hevia
 */
public class XMLListenerController {

	/** Number of experiment */
	private int experiments;

	/** List of available listeners */
	public enum AvailableListener {
		ACTIVITY_QUEUE, RESOURCE_USAGE, ELEMENT_TIME, ELEMENT_INDISP_TIME, ELEMENT_TYPE_TIME
	};

	/** Simulation listeners list */
	private HashMap<Integer, ArrayList<SimulationListener>> listenerList = new HashMap<Integer, ArrayList<SimulationListener>>();

	/** XMLListenerProcessor list */
	private HashMap<Integer, XMLListenerProcessor> listenerProcessor = new HashMap<Integer, XMLListenerProcessor>();
	
	
	public void add(int id, SimulationListener listener) {
		if (listenerList.get(id) == null)
			listenerList.put(id, new ArrayList<SimulationListener>());
		listenerList.get(id).add(listener);
	}

	public void addAll(int id, ArrayList<SimulationListener> listeners) {
		if (listenerList.get(id) == null)
			listenerList.put(id, new ArrayList<SimulationListener>());
		listenerList.get(id).addAll(listeners);
	}

	/**
	 * initializes the Average Results with the listeners used in the simulation
	 * @param listInfo
	 */
	private void getAverage(ListenerInfo listInfo) {

		// For each simulation
		for (SimulationResults simulation : listInfo.getSimulationResults()) { 
			// For each listener increments the values of each listener
			for (int i = 0; i < simulation.getListener().size(); i++) {
				es.ull.isaatc.simulation.listener.xml.SimulationListener simListener = simulation.getListener().get(i);
				// initialize the listenerProcessor hash with the listener results
				// of the first simulation
				if (listenerProcessor.get(i) == null) {
					if (simListener instanceof ActivityListener) {
						listenerProcessor.put(i, new XMLActivityListenerProcessor(experiments, simListener));
					}
					else if (simListener instanceof SelectableActivityListener) {
						listenerProcessor.put(i, new XMLSelectableActivityListenerProcessor(experiments, simListener));
					}
					else if (simListener instanceof ActivityTimeListener) {
						listenerProcessor.put(i, new XMLActivityTimeListenerProcessor(experiments, simListener));
					}
					else if (simListener instanceof SelectableActivityTimeListener) {
						listenerProcessor.put(i, new XMLSelectableActivityTimeListenerProcessor(experiments, simListener));
					}
					else if (simListener instanceof ElementStartFinishListener) {
						listenerProcessor.put(i, new XMLElementStartFinishListenerProcessor(experiments, simListener));
					}
					else if (simListener instanceof SimulationTimeListener) {
						listenerProcessor.put(i, new XMLSimulationTimeListenerProcessor(experiments, simListener));
					}
					else if (simListener instanceof ElementTypeTimeListener) {
						listenerProcessor.put(i, new XMLElementTypeTimeListenerProcessor(experiments, simListener));
					}
					else if (simListener instanceof ResourceStdUsageListener) {
						listenerProcessor.put(i, new XMLResourceStdUsageListenerProcessor(experiments, simListener));
					}
				// process the listener of the other simulation 
				} else {
					listenerProcessor.get(i).process(simListener);
				}
			}
		}
		// Calc the average
		for (XMLListenerProcessor i : listenerProcessor.values()) {
			i.average();
		}
		// Put the info into the XML AverageResults
		AverageResults avgResults = new ListenerInfo.AverageResults();
		for (XMLListenerProcessor i : listenerProcessor.values()) {
			avgResults.getListener().add(i.getListener());
		}
		listInfo.setAverageResults(avgResults);
	}


	/**
	 * @return a string containing the serialization of each listener to XML.
	 */
	public String getXML() {

		ListenerInfo listInfo = new ListenerInfo();

		listInfo.setNExperiments(getExperiments());

		// complete the XML information of each simulation
		for (ArrayList<SimulationListener> listeners : listenerList.values()) {
			SimulationResults simList = new SimulationResults();
			for (SimulationListener listener : listeners) {
				XMLListenerFactory.getXMLListener(listener);
				simList.getListener().add(
						XMLListenerFactory.getXMLListener(listener));
			}
			listInfo.getSimulationResults().add(simList);
		}
		// get the average results
		getAverage(listInfo);

		return XMLMarshallUtils.marshallObject(listInfo,
				"es.ull.isaatc.simulation.listener.xml");
	}

	/**
	 * @return a string containing the simulation listener info
	 */
	public String toString() {
		StringBuffer result = new StringBuffer();
		int i = 1;
		for (ArrayList<SimulationListener> listeners : listenerList.values()) {
			result.append("\nSimulation " + i++ + "\n");
			for (SimulationListener listener : listeners)
				result.append(listener.toString());
		}
		return result.toString();
	}

	/**
	 * Gets the number of experiments in the simulation.
	 * @return number of experiments
	 */
	public int getExperiments() {
		return experiments;
	}

	/**
	 * Sets the number of experiments in the simulation.
	 * @param experiments
	 */
	public void setExperiments(int experiments) {
		this.experiments = experiments;
	}
}
