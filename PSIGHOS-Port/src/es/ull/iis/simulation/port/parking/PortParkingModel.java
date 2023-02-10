/**
 * 
 */
package es.ull.iis.simulation.port.parking;

import java.util.ArrayList;

import es.ull.iis.simulation.model.ElementType;
import es.ull.iis.simulation.model.ResourceType;
import es.ull.iis.simulation.model.Simulation;
import es.ull.iis.simulation.model.TimeUnit;
import es.ull.iis.simulation.model.WorkGroup;
import es.ull.iis.simulation.model.flow.DelayFlow;
import es.ull.iis.simulation.model.flow.ReleaseResourcesFlow;
import es.ull.iis.simulation.model.flow.RequestResourcesFlow;
import es.ull.iis.simulation.model.location.MoveFlow;

/**
 * @author Iván Castilla
 *
 */
public class PortParkingModel extends Simulation {
	private static final long LOAD_TIME = 10;

	/**
	 * @param id
	 * @param description
	 * @param startTs
	 * @param endTs
	 */
	public PortParkingModel(int id, long endTs, int parkingCapacity, ArrayList<Double> pathTimes, int nQuays) {
		super(id, "Santander Port simulation " + id, TimeUnit.MINUTE, 0, endTs);
		
		final TruckRouter truckRouter = new TruckRouter(parkingCapacity * Truck.SIZE, pathTimes);
		
		// Modeling parking slots as resources to handle the arrival of trucks in advance
		final ResourceType rtParkSpace = new ResourceType(this, "Park slot");
		rtParkSpace.addGenericResources(parkingCapacity);
		final WorkGroup wgParkSlot = new WorkGroup(this, rtParkSpace, 1);
		
		final RequestResourcesFlow reqParkingFlow = new RequestResourcesFlow(this, "Check whether there is a parking slot available", 0);
		reqParkingFlow.newWorkGroupAdder(wgParkSlot).add();
		
		final MoveFlow toParkingFlow = new MoveFlow(this, "Go to the parking", truckRouter.getParking(), truckRouter);
		final DelayFlow parkedFlow = new DelayFlow(this, "Load tasks at the parking", LOAD_TIME);
		final MoveFlow fromParkingFlow = new MoveFlow(this, "Depart from the parking", truckRouter.getEnding(), truckRouter);
		final ReleaseResourcesFlow relParkingFlow = new ReleaseResourcesFlow(this, "Free parking slot", wgParkSlot);
		reqParkingFlow.link(toParkingFlow).link(parkedFlow).link(fromParkingFlow).link(relParkingFlow);
		
		new TruckCreator(this, reqParkingFlow, truckRouter.getStarting());

		// Modeling quays as resources to handle the arrival of vessels in advance
		final ResourceType[] rtQuays = new ResourceType[nQuays];
		final WorkGroup[] wgQuays = new WorkGroup[nQuays];
		for (int i = 0; i < nQuays; i++) {
			rtQuays[i] = new ResourceType(this, "Quay " + i);
			// Assuming 1 vessel per quay
			rtQuays[i].addGenericResources(1);
			wgQuays[i] = new WorkGroup(this, rtQuays[i], 1);
		}

	}

}
