/**
 * 
 */
package es.ull.iis.simulation.port.parking;

import es.ull.iis.simulation.model.ElementType;
import es.ull.iis.simulation.model.EventSource;
import es.ull.iis.simulation.model.Generator;
import es.ull.iis.simulation.model.Simulation;
import es.ull.iis.simulation.model.StandardElementGenerationInfo;
import es.ull.iis.simulation.model.flow.InitializerFlow;

/**
 * @author Iván Castilla
 *
 */
public class TruckCreator extends Generator<TruckCreator.TruckGenerationInfo> {
	private static final int N_TRUCKS_PER_SPAWN = 1;

	/**
	 * @param model
	 * @param nElem
	 * @param et
	 * @param flow
	 * @param cycle
	 */
	public TruckCreator(Simulation model, ElementType etTruck, InitializerFlow flow, TruckCompany co) {
		super(model, 0, N_TRUCKS_PER_SPAWN);
		add(new TruckGenerationInfo(etTruck, flow, co));
	}

	
	@Override
	public EventSource createEventSource(int ind, TruckGenerationInfo info) {
		return new Truck(simul, info);
	}
	
	public class TruckGenerationInfo extends StandardElementGenerationInfo {

		protected TruckGenerationInfo(ElementType et, InitializerFlow flow, TruckCompany co) {
			super(et, flow, Truck.SIZE, co.getInitialLocation(), 1.0);
		}
		
	}
}
