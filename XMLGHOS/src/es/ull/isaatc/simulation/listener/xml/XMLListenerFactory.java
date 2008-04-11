/**
 * 
 */
package es.ull.isaatc.simulation.listener.xml;

import es.ull.isaatc.simulation.listener.ElementTypeTimeListener.ElementTypeTime;

/**
 * @author Roberto Muñoz
 * @author Yurena Garcia-Hevia
 */
public class XMLListenerFactory {
	public static SimulationListener getXMLListener(
			es.ull.isaatc.simulation.listener.SimulationListener simListener) {

		// Activity Listener
		if (simListener instanceof es.ull.isaatc.simulation.listener.ActivityListener)
			return getXMLActivityListener((es.ull.isaatc.simulation.listener.ActivityListener) simListener);
		// Activity Time Listener
		else if (simListener instanceof es.ull.isaatc.simulation.listener.ActivityTimeListener)
			return getXMLActivityTimeListener((es.ull.isaatc.simulation.listener.ActivityTimeListener) simListener);
		// Element Start Finish Listener
		else if (simListener instanceof es.ull.isaatc.simulation.listener.ElementStartFinishListener)
			return getXMLElementStartFinishListener((es.ull.isaatc.simulation.listener.ElementStartFinishListener) simListener);
		// Element Type Time Listener
		else if (simListener instanceof es.ull.isaatc.simulation.listener.ElementTypeTimeListener)
			return getXMLElementTypeTimeListener((es.ull.isaatc.simulation.listener.ElementTypeTimeListener) simListener);
		// Resource Std Usage Listener
		else if (simListener instanceof es.ull.isaatc.simulation.listener.ResourceStdUsageListener)
			return getXMLResourceStdUsageListener((es.ull.isaatc.simulation.listener.ResourceStdUsageListener) simListener);
		// Selectable Activity Listener
		else if (simListener instanceof es.ull.isaatc.simulation.listener.SelectableActivityListener)
			return getXMLSelectableActivityListener((es.ull.isaatc.simulation.listener.SelectableActivityListener) simListener);
		// Selectable Activity Time Listener
		else if (simListener instanceof es.ull.isaatc.simulation.listener.SelectableActivityTimeListener)
			return getXMLSelectableActivityTimeListener((es.ull.isaatc.simulation.listener.SelectableActivityTimeListener) simListener);
		// Simulation Time Listener
		else if (simListener instanceof es.ull.isaatc.simulation.listener.SimulationTimeListener)
			return getXMLSimulationTimeListener((es.ull.isaatc.simulation.listener.SimulationTimeListener) simListener);
		// Other NULL
		else
			return null;
	}

	/**
	 * Initialize one xml PeriodicListener 
	 * @param xmlListener
	 * @param simListener
	 */
	public static void initXMLPeriodicListener(PeriodicListener xmlListener,
			es.ull.isaatc.simulation.listener.PeriodicListener simListener) {
		xmlListener.setStartTs(simListener.getSimStart());
		xmlListener.setEndTs(simListener.getSimEnd());
		xmlListener.setPeriod(simListener.getPeriod());
	}

	/**
	 * Get the xml representation for the SimulationTimeListener
	 * @param simListener
	 * @return the xml structure for the SimulationTimeListener results
	 */
	public static SimulationTimeListener getXMLSimulationTimeListener(
			es.ull.isaatc.simulation.listener.SimulationTimeListener simListener) {
		es.ull.isaatc.simulation.listener.xml.SimulationTimeListener xmlListener = new es.ull.isaatc.simulation.listener.xml.SimulationTimeListener();
		// for each simulation sets the simulation time
		xmlListener.setSimulationTime(simListener.getEndT() - simListener.getIniT());
		return xmlListener;
	}

	/**
	 * Get the xml representation for the ActivityListener
	 * @param simListener
	 * @return the xml structure for the ActivityListener results
	 */
	public static ActivityListener getXMLActivityListener(
			es.ull.isaatc.simulation.listener.ActivityListener simListener) {

		es.ull.isaatc.simulation.listener.xml.ActivityListener xmlListener = new es.ull.isaatc.simulation.listener.xml.ActivityListener();
		initXMLPeriodicListener(xmlListener, simListener);

		// for each activity
		for (int id : simListener.getActQueues().keySet()) {
			es.ull.isaatc.simulation.listener.xml.ActivityListener.Activity activity = new es.ull.isaatc.simulation.listener.xml.ActivityListener.Activity();
			es.ull.isaatc.simulation.listener.xml.ActivityListener.Activity.ActQueue actQueue = new es.ull.isaatc.simulation.listener.xml.ActivityListener.Activity.ActQueue();
			es.ull.isaatc.simulation.listener.xml.ActivityListener.Activity.ActPerformed actPerformed = new es.ull.isaatc.simulation.listener.xml.ActivityListener.Activity.ActPerformed();

			activity.setActId(id);

			// fill the activity queue
			for (int q : simListener.getActQueues().get(id))
				actQueue.getQueue().add((double)q);
			activity.setActQueue(actQueue);

			// fill the activity performed
			for (int p : simListener.getActPerformed().get(id))
				actPerformed.getPerformed().add((double)p);
			activity.setActPerformed(actPerformed);
			
			xmlListener.getActivity().add(activity);
		}

		return xmlListener;
	}

	/**
	 * Get the xml representation for the ActivityTimeListener
	 * @param simListener
	 * @return the xml structure for the ActivityTimeListener results
	 */
	public static ActivityTimeListener getXMLActivityTimeListener(
			es.ull.isaatc.simulation.listener.ActivityTimeListener simListener) {

		es.ull.isaatc.simulation.listener.xml.ActivityTimeListener xmlListener = new es.ull.isaatc.simulation.listener.xml.ActivityTimeListener();
		initXMLPeriodicListener(xmlListener, simListener);

		// for each activity
		for (int id : simListener.getActUsage().keySet()) {
			es.ull.isaatc.simulation.listener.xml.ActivityTimeListener.Activity activity = new es.ull.isaatc.simulation.listener.xml.ActivityTimeListener.Activity();
			activity.setActId(id);

			// fill the activity time
			for (double t : simListener.getActUsage().get(id))
				activity.getTime().add(t);
			xmlListener.getActivity().add(activity);
		}

		return xmlListener;
	}

	/**
	 * Get the xml representation for the SelectableActivityTimeListener
	 * @param simListener
	 * @return the xml structure for the SelectableActivityTimeListener results
	 */
	public static SelectableActivityTimeListener getXMLSelectableActivityTimeListener(
			es.ull.isaatc.simulation.listener.SelectableActivityTimeListener simListener) {

		es.ull.isaatc.simulation.listener.xml.SelectableActivityTimeListener xmlListener = new es.ull.isaatc.simulation.listener.xml.SelectableActivityTimeListener();
		initXMLPeriodicListener(xmlListener, simListener);

		// for each selected activity
		for (int id : simListener.getActUsage().keySet()) {
			es.ull.isaatc.simulation.listener.xml.SelectableActivityTimeListener.Activity activity = new es.ull.isaatc.simulation.listener.xml.SelectableActivityTimeListener.Activity();
			activity.setActId(id);

			// fill the activity time
			for (double t : simListener.getActUsage().get(id))
				activity.getTime().add(t);
			xmlListener.getActivity().add(activity);
		}

		return xmlListener;
	}

	/**
	 * Get the xml representation for the ResourceStdUsageListener
	 * @param simListener
	 * @return the xml structure for the ResourceStdUsageListener results
	 */
	public static ResourceStdUsageListener getXMLResourceStdUsageListener(
			es.ull.isaatc.simulation.listener.ResourceStdUsageListener simListener) {

		es.ull.isaatc.simulation.listener.xml.ResourceStdUsageListener xmlListener = new es.ull.isaatc.simulation.listener.xml.ResourceStdUsageListener();
		initXMLPeriodicListener(xmlListener, simListener);

		// for each resource
		for (int id : simListener.getResUsage().keySet()) {		
			// Resource initialize
			es.ull.isaatc.simulation.listener.xml.ResourceStdUsageListener.Resource resource = new es.ull.isaatc.simulation.listener.xml.ResourceStdUsageListener.Resource();
			es.ull.isaatc.simulation.listener.xml.ResourceStdUsageListener.Resource.Usage usage = new es.ull.isaatc.simulation.listener.xml.ResourceStdUsageListener.Resource.Usage();
			es.ull.isaatc.simulation.listener.xml.ResourceStdUsageListener.Resource.Available available = new es.ull.isaatc.simulation.listener.xml.ResourceStdUsageListener.Resource.Available();

			resource.setId(id);
			
			// for each resource type sets the usage
			for (int rtId : simListener.getResUsage().get(id).getUsageTime().keySet()) {
				es.ull.isaatc.simulation.listener.xml.ResourceStdUsageListener.Resource.Usage.Rt rt = new es.ull.isaatc.simulation.listener.xml.ResourceStdUsageListener.Resource.Usage.Rt();
				rt.setId(rtId);

				// for each period
				for (double u : simListener.getResUsage().get(id).getUsageTime(rtId))
					rt.getValue().add(u);
				usage.getRt().add(rt);
			}
			resource.setUsage(usage);

			// for each resource type sets the availability
			for (int rtId : simListener.getResUsage().get(id).getAvalTime().keySet()) {
				es.ull.isaatc.simulation.listener.xml.ResourceStdUsageListener.Resource.Available.Rt rt = new es.ull.isaatc.simulation.listener.xml.ResourceStdUsageListener.Resource.Available.Rt();
				rt.setId(rtId);

				// for each period
				for (double a : simListener.getResUsage().get(id).getAvalTime(rtId))
					rt.getValue().add(a);
				available.getRt().add(rt);
			}
			resource.setAvailable(available);
			xmlListener.getResource().add(resource);
		}
		return xmlListener;
	}

	/**
	 * Get the xml representation for the ElementTypeTimeListener
	 * @param simListener
	 * @return the xml structure for the ElementTypeTimeListener results
	 */
	public static ElementTypeTimeListener getXMLElementTypeTimeListener(
			es.ull.isaatc.simulation.listener.ElementTypeTimeListener simListener) {

		es.ull.isaatc.simulation.listener.xml.ElementTypeTimeListener xmlListener = new es.ull.isaatc.simulation.listener.xml.ElementTypeTimeListener();
		initXMLPeriodicListener(xmlListener, simListener);

		// for each elementTypeTimes
		for (ElementTypeTime ett : simListener.getElementTypeTimes().values()) {

			es.ull.isaatc.simulation.listener.xml.ElementTypeTimeListener.Et XMLet = new es.ull.isaatc.simulation.listener.xml.ElementTypeTimeListener.Et();
			es.ull.isaatc.simulation.listener.xml.ElementTypeTimeListener.Et.Created created = new es.ull.isaatc.simulation.listener.xml.ElementTypeTimeListener.Et.Created();
			es.ull.isaatc.simulation.listener.xml.ElementTypeTimeListener.Et.Finished finished = new es.ull.isaatc.simulation.listener.xml.ElementTypeTimeListener.Et.Finished();
			es.ull.isaatc.simulation.listener.xml.ElementTypeTimeListener.Et.WorkTime workTime = new es.ull.isaatc.simulation.listener.xml.ElementTypeTimeListener.Et.WorkTime();

			XMLet.setId(ett.getTypeId());

			// for each period sets created and finished elements and its worktime
			for (int e = 0; e < ett.getNPeriods(); e++) {
				created.getValue().add((double)ett.getCreatedElement()[e]);
				finished.getValue().add((double)ett.getFinishedElement()[e]);
				workTime.getValue().add((double)ett.getWorkTime()[e]);
			}
			XMLet.setCreated(created);
			XMLet.setFinished(finished);
			XMLet.setWorkTime(workTime);

			xmlListener.getEt().add(XMLet);
		}
		return xmlListener;
	}

	/**
	 * Get the xml representation for the ElementStartFinishListener
	 * @param simListener
	 * @return the xml structure for the ElementStartFinishListener results
	 */
	public static ElementStartFinishListener getXMLElementStartFinishListener(
			es.ull.isaatc.simulation.listener.ElementStartFinishListener simListener) {

		es.ull.isaatc.simulation.listener.xml.ElementStartFinishListener xmlListener = new es.ull.isaatc.simulation.listener.xml.ElementStartFinishListener();
		initXMLPeriodicListener(xmlListener, simListener);

		xmlListener.setFirstElement(simListener.getFirstElementId());
		xmlListener.setLastElement(simListener.getLastElementId());

		es.ull.isaatc.simulation.listener.xml.ElementStartFinishListener.Created created = new es.ull.isaatc.simulation.listener.xml.ElementStartFinishListener.Created();
		es.ull.isaatc.simulation.listener.xml.ElementStartFinishListener.Finished finished = new es.ull.isaatc.simulation.listener.xml.ElementStartFinishListener.Finished();

		// for each period sets the created and finished elements
		for (int p = 0; p < simListener.getNumberOfPeriods(); p++) {
			created.getValue().add((double)simListener.getElemStarted()[p]);
			finished.getValue().add((double)simListener.getElemFinish()[p]);
		}
		xmlListener.setCreated(created);
		xmlListener.setFinished(finished);
		return xmlListener;
	}

	/**
	 * Get the xml representation for the SelectableActivityListener
	 * @param simListener
	 * @return the xml structure for the SelectableActivityListener results
	 */
	public static SelectableActivityListener getXMLSelectableActivityListener(
			es.ull.isaatc.simulation.listener.SelectableActivityListener simListener) {
	
		es.ull.isaatc.simulation.listener.xml.SelectableActivityListener xmlListener = new es.ull.isaatc.simulation.listener.xml.SelectableActivityListener();
		initXMLPeriodicListener(xmlListener, simListener);

		// for each activity
		for (int id : simListener.getActQueues().keySet()) {

			es.ull.isaatc.simulation.listener.xml.SelectableActivityListener.Activity activity = new es.ull.isaatc.simulation.listener.xml.SelectableActivityListener.Activity();
			es.ull.isaatc.simulation.listener.xml.SelectableActivityListener.Activity.ActQueue actQueue = new es.ull.isaatc.simulation.listener.xml.SelectableActivityListener.Activity.ActQueue();
			es.ull.isaatc.simulation.listener.xml.SelectableActivityListener.Activity.ActPerformed actPerformed = new es.ull.isaatc.simulation.listener.xml.SelectableActivityListener.Activity.ActPerformed();

			activity.setActId(id);

			// fill the activities queue
			for (int q : simListener.getActQueues().get(id))
				actQueue.getQueue().add((double)q);

			activity.setActQueue(actQueue);

			// fill the activities performed
			for (int p : simListener.getActPerformed().get(id))
				actPerformed.getPerformed().add((double)p);
			activity.setActPerformed(actPerformed);

			xmlListener.getActivity().add(activity);
		}
		return xmlListener;
	}

}
