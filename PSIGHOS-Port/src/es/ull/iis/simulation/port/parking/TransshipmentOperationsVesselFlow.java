/**
 * 
 */
package es.ull.iis.simulation.port.parking;

import java.util.TreeMap;

import es.ull.iis.simulation.model.ElementInstance;
import es.ull.iis.simulation.model.Simulation;
import es.ull.iis.simulation.model.flow.ActionFlow;
import es.ull.iis.simulation.model.flow.Flow;
import es.ull.iis.simulation.model.flow.InitializerFlow;
import es.ull.iis.simulation.model.flow.SingleSuccessorFlow;

/**
 * @author Iván Castilla Rodríguez
 *
 */
public class TransshipmentOperationsVesselFlow extends SingleSuccessorFlow implements InitializerFlow, ActionFlow {
	private final TreeMap<Vessel, ElementInstance> waitingVessels;
 

	/**
	 * Creates the flow
	 * @param simul Simulation this flow belongs to
	 */
	public TransshipmentOperationsVesselFlow(Simulation simul) {
		super(simul);
		waitingVessels = new TreeMap<>();
	}

	@Override
	public void addPredecessor(Flow predecessor) {
	}

	@Override
	public void request(ElementInstance ei) {
		if (!ei.wasVisited(this)) {
			if (ei.isExecutable()) {
				if (beforeRequest(ei)) {
					final Vessel vessel = ((Vessel)ei.getElement());
					// In case the vessel arrived empty
					if (vessel.isEmpty())
						next(ei);
					else {
						waitingVessels.put(vessel, ei);
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
		if (vessel.isEmpty() && (waitingVessels.get(vessel) != null)) {
			final ElementInstance ei = waitingVessels.remove(vessel);
			next(ei);
		}
	}

	@Override
	public String getDescription() {
		return "Wait for the vessel to finish tasks";
	}
}
