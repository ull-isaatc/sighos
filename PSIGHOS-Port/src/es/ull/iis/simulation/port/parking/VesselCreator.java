/**
 * 
 */
package es.ull.iis.simulation.port.parking;

import es.ull.iis.simulation.model.ElementType;
import es.ull.iis.simulation.model.EventSource;
import es.ull.iis.simulation.model.Simulation;
import es.ull.iis.simulation.model.SimulationPeriodicCycle;
import es.ull.iis.simulation.model.StandardElementGenerationInfo;
import es.ull.iis.simulation.model.TimeDrivenElementGenerator;
import es.ull.iis.simulation.model.flow.InitializerFlow;

/**
 * @author Iván Castilla
 *
 */
public class VesselCreator extends TimeDrivenElementGenerator {
	private static final int N_VESSELS_PER_SPAWN = 1;

	/**
	 * @param model
	 * @param nElem
	 * @param et
	 * @param flow
	 * @param cycle
	 */
	public VesselCreator(Simulation model,  ElementType etVessel, InitializerFlow flow, VesselType vType) {
		super(model, N_VESSELS_PER_SPAWN, new SimulationPeriodicCycle(model.getTimeUnit(), vType.getFirstArrivalTime(), vType.getInterarrivalTime(), model.getEndTs()));
		add(new VesselGenerationInfo(etVessel, flow, vType));
	}

	
	@Override
	public EventSource createEventSource(int ind, StandardElementGenerationInfo info) {
		return new Vessel(simul, (VesselGenerationInfo) info);
	}
	
	public class VesselGenerationInfo extends StandardElementGenerationInfo {

		protected VesselGenerationInfo(ElementType et, InitializerFlow flow, VesselType vType) {
			super(et, flow, Vessel.SIZE, vType.getInitialLocation(), 1.0);
		}
		
	}
}
