/**
 * 
 */
package es.ull.iis.simulation.port.parking;

import es.ull.iis.simulation.model.ElementType;
import es.ull.iis.simulation.model.EventSource;
import es.ull.iis.simulation.model.Simulation;
import es.ull.iis.simulation.model.SimulationPeriodicCycle;
import es.ull.iis.simulation.model.TimeDrivenElementGenerator;
import es.ull.iis.simulation.model.flow.InitializerFlow;

/**
 * @author Iván Castilla
 *
 */
public class TruckCreator extends TimeDrivenElementGenerator {
	private static final int N_TRUCKS_PER_SPAWN = 1;

	/**
	 * @param model
	 * @param nElem
	 * @param et
	 * @param flow
	 * @param cycle
	 */
	public TruckCreator(Simulation model, InitializerFlow flow, TruckCompany co) {
		super(model, N_TRUCKS_PER_SPAWN, new SimulationPeriodicCycle(PortParkingModel.TIME_UNIT, 0L, 
				co.getInterarrivalTime(), model.getEndTs()));
		final ElementType etTruck = new ElementType(model, "Trucks from " + co.getDescription());
		add(new TruckGenerationInfo(etTruck, flow, co));
	}

	
	@Override
	public EventSource createEventSource(int ind, GenerationInfo info) {
		return new Truck(simul, (TruckGenerationInfo) info);
	}
	
	public class TruckGenerationInfo extends GenerationInfo {

		protected TruckGenerationInfo(ElementType et, InitializerFlow flow, TruckCompany co) {
			super(et, flow, Truck.SIZE, co.getInitialLocation(), 1.0);
		}
		
	}
}
