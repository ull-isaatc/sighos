/**
 * 
 */
package es.ull.iis.simulation.port.parking;

import java.util.TreeMap;

import es.ull.iis.simulation.model.ElementInstance;
import es.ull.iis.simulation.model.flow.WaitForSignalFlow;
import es.ull.iis.simulation.model.flow.WaitForSignalFlow.Listener;

/**
 * @author masbe
 *
 */
public class VesselWaitingManager implements Listener {
	private WaitForSignalFlow vesselFlow = null;
	private final TreeMap<Vessel, ElementInstance> waitingVessels;

	/**
	 * 
	 */
	public VesselWaitingManager() {
		waitingVessels= new TreeMap<>();
	}
	
	public void letVesselGo(Vessel vessel) {
		this.vesselFlow.signal(waitingVessels.get(vessel));
	}

	@Override
	public void register(WaitForSignalFlow flow) {
		this.vesselFlow = flow;
	}

	@Override
	public void notifyArrival(WaitForSignalFlow flow, ElementInstance ei) {
		if (vesselFlow.equals(flow)) {
			final Vessel vessel = ((Vessel)ei.getElement());
			waitingVessels.put(vessel, ei);
		}
	}

}
