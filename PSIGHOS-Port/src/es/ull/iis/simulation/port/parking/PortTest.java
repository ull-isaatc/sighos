/**
 * 
 */
package es.ull.iis.simulation.port.parking;

import es.ull.iis.function.TimeFunction;
import es.ull.iis.function.TimeFunctionFactory;
import es.ull.iis.simulation.model.ElementInstance;
import es.ull.iis.simulation.model.ElementType;
import es.ull.iis.simulation.model.Simulation;
import es.ull.iis.simulation.model.flow.WaitForSignalFlow;
import es.ull.iis.simulation.model.location.MoveFlow;

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
	private final WaitForSignalFlow waitForVesselFlow;
	private final ElementType etTestTruck;
	private final VesselRouter vRouter;

	/**
	 * 
	 */
	public PortTest(int id, long endTs, int parkingCapacity) {
		super(id, "Santander Port simulation " + id, PortParkingModel.TIME_UNIT, 0, endTs);

		truckManager = new TruckWaitingManager();
		vRouter = new VesselRouter(this, T_FROM_SOURCE_TO_ANCHORAGE);
		moveVesselFlow = new MoveFlow(this, "Move to anchorage", vRouter.getAnchorage(), vRouter) {
			@Override
			public void afterFinalize(ElementInstance fe) {
				super.afterFinalize(fe);
				truckManager.letTrucksStart((Vessel) fe.getElement());
			}
		}; 
		etTestVessel = new ElementType(this, "Test vessel");
		
		final TruckRouter tRouter = new TruckRouter(parkingCapacity * Truck.SIZE, T_ENTRANCE_PARKING, T_PARKING_EXIT);
		waitForVesselFlow = new WaitForSignalFlow(this, "wait for vessel", truckManager);
		final MoveFlow moveToEntranceFlow = new MoveFlow(this, "move to entrance", tRouter.getPortEntrance(), tRouter) {
			@Override
			public boolean beforeRequest(ElementInstance ei) {
				TruckSource.TYPE1.getSpawnLocation().enter(ei.getElement());
				return super.beforeRequest(ei);
			}
		};
		waitForVesselFlow.link(moveToEntranceFlow);
		etTestTruck = new ElementType(this, "Test truck");
	}

	@Override
	public void init() {
		super.init();
		final Vessel myTestVessel = new Vessel(this, WaresType.TYPE1, etTestVessel, moveVesselFlow, vRouter.getInitialLocation());
		final Truck myTestTruck = new Truck(this, etTestTruck, waitForVesselFlow, myTestVessel, TruckSource.TYPE1);
		addEvent(myTestVessel.onCreate(getTs()));
		addEvent(myTestTruck.onCreate(getTs()));
	}
}
