/**
 * 
 */
package es.ull.iis.simulation.port.parking;

import java.util.ArrayList;

import es.ull.iis.function.TimeFunction;
import es.ull.iis.function.TimeFunctionFactory;
import es.ull.iis.simulation.condition.ElementTypeCondition;
import es.ull.iis.simulation.model.ElementType;
import es.ull.iis.simulation.model.ResourceType;
import es.ull.iis.simulation.model.Simulation;
import es.ull.iis.simulation.model.TimeUnit;
import es.ull.iis.simulation.model.WorkGroup;
import es.ull.iis.simulation.model.flow.DelayFlow;
import es.ull.iis.simulation.model.flow.ExclusiveChoiceFlow;
import es.ull.iis.simulation.model.flow.ReleaseResourcesFlow;
import es.ull.iis.simulation.model.flow.RequestResourcesFlow;
import es.ull.iis.simulation.model.location.MoveFlow;

/**
 * @author Iván Castilla
 *
 */
public class PortParkingModel extends Simulation {
	public static final TimeUnit TIME_UNIT = TimeUnit.MINUTE;
	private static final long LOAD_TIME = 10;
	private static final TimeFunction T_ENTRANCE_PARKING = TimeFunctionFactory.getInstance("ConstantVariate", 10);
	private static final TimeFunction T_PARKING_EXIT = TimeFunctionFactory.getInstance("ConstantVariate", 10);

	/**
	 * @param id
	 * @param description
	 * @param startTs
	 * @param endTs
	 */
	public PortParkingModel(int id, long endTs, int parkingCapacity, ArrayList<Double> pathTimes, int nQuays) {
		super(id, "Santander Port simulation " + id, TIME_UNIT, 0, endTs);
		
		createModelForTrucks(parkingCapacity);
		
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

	private void createModelForTrucks(int parkingCapacity) {
		final TruckRouter truckRouter = new TruckRouter(parkingCapacity * Truck.SIZE, T_ENTRANCE_PARKING, T_PARKING_EXIT);
		
		// Modeling parking slots as resources to handle the arrival of trucks in advance
		final ResourceType rtParkSpace = new ResourceType(this, "Park slot");
		rtParkSpace.addGenericResources(parkingCapacity);
		final WorkGroup wgParkSlot = new WorkGroup(this, rtParkSpace, 1);
		
		final MoveFlow toEntranceFlow = new MoveFlow(this, "Go to the port entrance", truckRouter.getPortEntrance(), truckRouter);
		
		final RequestResourcesFlow reqParkingFlow = new RequestResourcesFlow(this, "Check whether there is a parking slot available", 0);
		reqParkingFlow.newWorkGroupAdder(wgParkSlot).add();
		
		final MoveFlow toParkingFlow = new MoveFlow(this, "Go to the parking", truckRouter.getParking(), truckRouter);
		final DelayFlow parkedFlow = new DelayFlow(this, "Load tasks at the parking", LOAD_TIME);
		final MoveFlow fromParkingFlow = new MoveFlow(this, "Depart from the parking", truckRouter.getPortExit(), truckRouter);
		final ReleaseResourcesFlow relParkingFlow = new ReleaseResourcesFlow(this, "Free parking slot", wgParkSlot);
		final ExclusiveChoiceFlow choiceDestinationFlow = new ExclusiveChoiceFlow(this);
		toEntranceFlow.link(reqParkingFlow).link(toParkingFlow).link(parkedFlow);
		parkedFlow.link(fromParkingFlow).link(relParkingFlow).link(choiceDestinationFlow);
		
		for (TruckCompany co : TruckCompany.values()) {
			final ElementType etTruck = new ElementType(this, "Trucks from " + co.getDescription());
			new TruckCreator(this, etTruck, toEntranceFlow, co);
			final MoveFlow returnFlow = new MoveFlow(this, "Return to warehouse", co.getInitialLocation(), truckRouter);
			choiceDestinationFlow.link(returnFlow, new ElementTypeCondition(etTruck));
		}		
	}
}
