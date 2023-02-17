/**
 * 
 */
package es.ull.iis.simulation.port.parking;

import es.ull.iis.function.TimeFunction;
import es.ull.iis.function.TimeFunctionFactory;
import es.ull.iis.simulation.condition.Condition;
import es.ull.iis.simulation.condition.ElementTypeCondition;
import es.ull.iis.simulation.model.ElementInstance;
import es.ull.iis.simulation.model.ElementType;
import es.ull.iis.simulation.model.ResourceType;
import es.ull.iis.simulation.model.Simulation;
import es.ull.iis.simulation.model.TimeUnit;
import es.ull.iis.simulation.model.WorkGroup;
import es.ull.iis.simulation.model.flow.DelayFlow;
import es.ull.iis.simulation.model.flow.ExclusiveChoiceFlow;
import es.ull.iis.simulation.model.flow.GeneratorFlow;
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
	public PortParkingModel(int id, long endTs, int parkingCapacity, TimeFunction[] pathTimesPerQuay) {
		super(id, "Santander Port simulation " + id, TIME_UNIT, 0, endTs);
		
//		createModelForTrucks(parkingCapacity);		
		createModelForVessels(pathTimesPerQuay);
	}
	
	private void createModelForVessels(TimeFunction []pathTimesPerQuay) {
		final int nQuays = pathTimesPerQuay.length;
		// Ok
		final VesselRouter vesselRouter = new VesselRouter(pathTimesPerQuay);
		final MoveFlow toAnchorageFlow = new MoveFlow(this, "Go to anchorage", vesselRouter.getAnchorage(), vesselRouter);
		// TODO: La eleeción del muelle tiene una lógica asociada
		final RequestResourcesFlow reqQuayFlow = new RequestResourcesFlow(this, "Check for quay available", 0);
		// Ok: Cada barco tiene su propio "tipo" de mercancía y de camiones
		//final GeneratorFlow genTrucksFlow = new GeneratorFlow(this, "Create trucks to serve vessel", new Truck)
		// TODO: La eleeción del muelle tiene una lógica asociada
		final ExclusiveChoiceFlow choiceQuayFlow = new ExclusiveChoiceFlow(this);

		toAnchorageFlow.link(reqQuayFlow).link(choiceQuayFlow);
		
		// TODO: Añadir un pequeño retardo para el papeleo al llegar al muelle (y otro antes de irse)
		// TODO: Sustituir por la interacción con los camiones
		final DelayFlow atQuayFlow = new DelayFlow(this, "Unloading at quay", LOAD_TIME * 20);
		
		// Modeling quays as resources to handle the arrival of vessels in advance
		final ResourceType[] rtQuays = new ResourceType[nQuays];
		final WorkGroup[] wgQuays = new WorkGroup[nQuays];
		for (int i = 0; i < nQuays; i++) {
			rtQuays[i] = new ResourceType(this, "Quay " + i);
			// Assuming 1 vessel per quay
			rtQuays[i].addGenericResources(1);
			wgQuays[i] = new WorkGroup(this, rtQuays[i], 1);
			reqQuayFlow.newWorkGroupAdder(wgQuays[i]).add();
			choiceQuayFlow.link(
					new MoveFlow(this, "To Quay " + i, vesselRouter.getQuay(i), vesselRouter), 
					new QuayCondition(rtQuays[i])).link(atQuayFlow);
		}
		
		final ExclusiveChoiceFlow choiceReturnSpotFlow = new ExclusiveChoiceFlow(this);
		atQuayFlow.link(choiceReturnSpotFlow);
		for (VesselType vType : VesselType.values()) {
			final MoveFlow returnFlow = new MoveFlow(this, "Return from Quay", vType.getInitialLocation(), vesselRouter);
			
			final ElementType etVessel = new ElementType(this, "Vessels from " + vType.getDescription());
			new VesselCreator(this, etVessel, toAnchorageFlow, vType);
			choiceReturnSpotFlow.link(returnFlow, new ElementTypeCondition(etVessel));
		}
	}

	private void createModelForTrucks(int parkingCapacity) {
		
		// Modeling parking slots as resources to handle the arrival of trucks in advance
		final ResourceType rtParkSpace = new ResourceType(this, "Park slot");
		rtParkSpace.addGenericResources(parkingCapacity);
		final WorkGroup wgParkSlot = new WorkGroup(this, rtParkSpace, 1);
		
		final TruckRouter truckRouter = new TruckRouter(parkingCapacity * Truck.SIZE, T_ENTRANCE_PARKING, T_PARKING_EXIT);
		
		// TODO: Al crearse, deben esperar un tiempo aleatorio antes de aparecer en el punto de origen
		final MoveFlow toEntranceFlow = new MoveFlow(this, "Go to the port entrance", truckRouter.getPortEntrance(), truckRouter);
		
		final RequestResourcesFlow reqParkingFlow = new RequestResourcesFlow(this, "Check whether there is a parking slot available", 0);
		reqParkingFlow.newWorkGroupAdder(wgParkSlot).add();
		
		final MoveFlow toParkingFlow = new MoveFlow(this, "Go to the parking", truckRouter.getParking(), truckRouter);
		// TODO: Relacionar con la carga a descargar y que esté el barco
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
			// TODO: En este momento, el camión debe "desaparecer" de la localización física, esperar un rato que simula la ruta ida y vuelta a su almacén; y volver, pero solo en caso de que haya que descargar más.
		}		
	}
	
	class QuayCondition extends Condition<ElementInstance> {
		private final ResourceType rtQuay;
		public QuayCondition(ResourceType rtQuay) {
			this.rtQuay = rtQuay;
		}

		@Override
		public boolean check(ElementInstance fe) {
			if (fe.getElement().isAcquiredResourceType(rtQuay)) {
				return true;
			}
			return false;
		}
		
	}
}
