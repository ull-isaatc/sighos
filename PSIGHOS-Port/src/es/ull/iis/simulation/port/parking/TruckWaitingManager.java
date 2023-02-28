/**
 * 
 */
package es.ull.iis.simulation.port.parking;

import java.util.ArrayList;
import java.util.TreeMap;

import es.ull.iis.simulation.model.ElementInstance;
import es.ull.iis.simulation.model.Simulation;
import es.ull.iis.simulation.model.flow.ActionFlow;
import es.ull.iis.simulation.model.flow.Flow;
import es.ull.iis.simulation.model.flow.InitializerFlow;
import es.ull.iis.simulation.model.flow.SingleSuccessorFlow;
import es.ull.iis.simulation.model.flow.WaitForSignalFlow.Listener;

/**
 * @author masbe
 *
 */
public class TruckWaitingManager {
	private final WaitForVesselFlow truckFlow;
	private final NotifyTrucksFlow vesselFlow;
	private final TreeMap<Vessel, ArrayList<ElementInstance>> waitingTrucks;

	/**
	 * 
	 */
	public TruckWaitingManager(Simulation model) {
		waitingTrucks = new TreeMap<>();
		truckFlow = new WaitForVesselFlow(model);
		vesselFlow = new NotifyTrucksFlow(model);
	}
	
	public WaitForVesselFlow getTruckFlow() {
		return truckFlow;
	}

	public NotifyTrucksFlow getVesselFlow() {
		return vesselFlow;
	}
	
	/**
	 * An element that requests this flow has to wait until a special class (implementing the {@link Listener} interface) sends a signal. Useful for implementing
	 * conditional waitings.  
	 * @author Iván Castilla
	 *
	 */
	public class WaitForVesselFlow extends SingleSuccessorFlow implements InitializerFlow, ActionFlow {

		/**
		 * Creates the flow
		 * @param simul Simulation this flow belongs to
		 */
		public WaitForVesselFlow(Simulation simul) {
			super(simul);
		}

		@Override
		public void addPredecessor(Flow predecessor) {
		}

		@Override
		public void request(ElementInstance ei) {
			if (!ei.wasVisited(this)) {
				if (ei.isExecutable()) {
					if (beforeRequest(ei)) {
						final Truck truck = (Truck) ei.getElement();
						final Vessel vessel = truck.getServingVessel(); 
						if (vessel.isReadyForTransshipment())
							next(ei);
						else {
							ArrayList<ElementInstance> waiting = waitingTrucks.get(vessel);
							if (waiting == null)
								waiting = new ArrayList<>();
							waiting.add(ei);
							waitingTrucks.put(vessel, waiting);
						}
					}
					else {
						ei.cancel(this);
						next(ei);
					}
				}
				else {
					ei.updatePath(this);
					next(ei);
				}
			} else
				ei.notifyEnd();
		}

		/**
		 * Notifies the flow that the specified {@link ElementInstance} can continue to the next step.
		 * @param ei Element instance that must continue
		 * @return True if the flow contained the specified element instance; false otherwise 
		 */
		public void signal(Vessel vessel) {
			ArrayList<ElementInstance> waiting = waitingTrucks.get(vessel);
			if (waiting != null) {
				for (ElementInstance ei : waiting)
					next(ei);
				waitingTrucks.remove(vessel);
			}
		}

		@Override
		public String getDescription() {
			return "Wait for the vessel to arrive";
		}
	}


	/**
	 * An element that requests this flow has to wait until a special class (implementing the {@link Listener} interface) sends a signal. Useful for implementing
	 * conditional waitings.  
	 * @author Iván Castilla
	 *
	 */
	public class NotifyTrucksFlow extends SingleSuccessorFlow implements InitializerFlow, ActionFlow {

		/**
		 * Creates the flow
		 * @param simul Simulation this flow belongs to
		 */
		public NotifyTrucksFlow(Simulation simul) {
			super(simul);
		}

		@Override
		public void addPredecessor(Flow predecessor) {
		}

		@Override
		public void request(ElementInstance ei) {
			if (!ei.wasVisited(this)) {
				if (ei.isExecutable()) {
					if (beforeRequest(ei)) {
						final Vessel vessel = (Vessel) ei.getElement();
						if (vessel.isReadyForTransshipment()) { // FIXME: This condition should be unnecessary
							truckFlow.signal(vessel);
						}
						next(ei);
					}
					else {
						ei.cancel(this);
						next(ei);
					}
				}
				else {
					ei.updatePath(this);
					next(ei);
				}
			} else
				ei.notifyEnd();
		}

		@Override
		public String getDescription() {
			return "Wait for the vessel to arrive";
		}
	}
	
}
