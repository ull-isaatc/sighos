/**
 * 
 */
package es.ull.iis.simulation.port.parking;

import java.util.ArrayList;
import java.util.TreeMap;

import es.ull.iis.simulation.model.ElementInstance;
import es.ull.iis.simulation.model.flow.WaitForSignalFlow;
import es.ull.iis.simulation.model.flow.WaitForSignalFlow.Listener;

/**
 * @author masbe
 *
 */
public class TruckWaitingManager implements Listener {
	private WaitForSignalFlow truckFlow = null;
	private final TreeMap<Vessel, ArrayList<ElementInstance>> waitingTrucks;

	/**
	 * 
	 */
	public TruckWaitingManager() {
		waitingTrucks = new TreeMap<>();
	}
	
	public void letTrucksStart(Vessel vessel) {
		// TODO: Chequear que esta condición está bien puesta
		if (waitingTrucks.get(vessel) != null) {
			for (ElementInstance ei : waitingTrucks.get(vessel))
				this.truckFlow.signal(ei);
		}
	}

	@Override
	public void register(WaitForSignalFlow flow) {
		this.truckFlow = flow;
	}

	@Override
	public void notifyArrival(WaitForSignalFlow flow, ElementInstance ei) {
		if (truckFlow.equals(flow)) {
			final Vessel vessel = ((Truck)ei.getElement()).getServingVessel();
			ArrayList<ElementInstance> trucks = waitingTrucks.get(vessel);
			if (trucks == null)
				trucks = new ArrayList<>();
			trucks.add(ei);
			waitingTrucks.put(vessel, trucks);
		}
	}

}
