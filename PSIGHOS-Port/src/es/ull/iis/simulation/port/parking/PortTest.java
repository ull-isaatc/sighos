/**
 * 
 */
package es.ull.iis.simulation.port.parking;

import java.util.ArrayList;

import es.ull.iis.function.TimeFunction;
import es.ull.iis.function.TimeFunctionFactory;
import es.ull.iis.simulation.model.ElementInstance;
import es.ull.iis.simulation.model.ElementType;
import es.ull.iis.simulation.model.Simulation;
import es.ull.iis.simulation.model.location.MoveFlow;
import es.ull.iis.simulation.port.parking.TransshipmentOrder.OperationType;
import es.ull.iis.simulation.port.parking.TruckWaitingManager.NotifyTrucksFlow;
import es.ull.iis.simulation.port.parking.TruckWaitingManager.WaitForVesselFlow;

/**
 * @author Iván Castilla Rodríguez
 *
 */
public class PortTest extends Simulation {
	private static final TimeFunction T_ENTRANCE_PARKING = TimeFunctionFactory.getInstance("ConstantVariate", 10);
	private static final TimeFunction T_PARKING_EXIT = TimeFunctionFactory.getInstance("ConstantVariate", 10);
	private static final TimeFunction T_FROM_SOURCE_TO_ANCHORAGE = TimeFunctionFactory.getInstance("ConstantVariate", 100);

	private final TruckWaitingManager truckManager;
	private final MoveFlow moveVesselFlow;
	private final ElementType etTestVessel;
	private final WaitForVesselFlow waitForVesselFlow;
	private final ElementType etTestTruck;
	private final VesselRouter vRouter;

	/**
	 * 
	 */
	public PortTest(int id, long endTs, int parkingCapacity) {
		super(id, "Santander Port simulation " + id, PortParkingModel.TIME_UNIT, 0, endTs);

		truckManager = new TruckWaitingManager(this);
		vRouter = new VesselRouter(this, T_FROM_SOURCE_TO_ANCHORAGE);
		moveVesselFlow = new MoveFlow(this, "Move to quay", QuayType.RAOS1.getLocation().getNode(), vRouter);
		final NotifyTrucksFlow notifyTrucksFlow = truckManager.getVesselFlow();
		moveVesselFlow.link(notifyTrucksFlow);
		
		etTestVessel = new ElementType(this, "Test vessel");
		
		final TruckRouter tRouter = new TruckRouter(parkingCapacity * PortParkingModel.TRUCK_SIZE, T_ENTRANCE_PARKING, T_PARKING_EXIT);
		waitForVesselFlow = truckManager.getTruckFlow();
		final MoveFlow moveToEntranceFlow = new MoveFlow(this, "move to entrance", tRouter.getPortEntrance(), tRouter) {
			@Override
			public boolean beforeRequest(ElementInstance ei) {
				TruckSource.TYPE1.getSpawnLocation().getNode().enter(ei.getElement());
				return super.beforeRequest(ei);
			}
		};
		waitForVesselFlow.link(moveToEntranceFlow);
		etTestTruck = new ElementType(this, "Test truck");
	}

	@Override
	public void init() {
		super.init();
		ArrayList<VesselTransshipmentOrder> orders = new ArrayList<>();
		orders.add(new VesselTransshipmentOrder(OperationType.LOAD, WaresType.CONSTRUCTION, 100));
		final Vessel myTestVessel = new Vessel(this, 0, orders, etTestVessel, moveVesselFlow, Locations.VESSEL_SRC.getNode());
		final Truck myTestTruck = new Truck(this, 0, etTestTruck, waitForVesselFlow, myTestVessel, TruckSource.TYPE1);
		addEvent(myTestVessel.onCreate(getTs()));
		addEvent(myTestTruck.onCreate(200));
	}
}
