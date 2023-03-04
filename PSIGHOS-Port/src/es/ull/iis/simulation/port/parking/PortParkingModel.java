/**
 * 
 */
package es.ull.iis.simulation.port.parking;

import es.ull.iis.function.TimeFunction;
import es.ull.iis.function.TimeFunctionFactory;
import es.ull.iis.simulation.condition.Condition;
import es.ull.iis.simulation.condition.ElementTypeCondition;
import es.ull.iis.simulation.model.Element;
import es.ull.iis.simulation.model.ElementInstance;
import es.ull.iis.simulation.model.ElementType;
import es.ull.iis.simulation.model.ResourceType;
import es.ull.iis.simulation.model.Simulation;
import es.ull.iis.simulation.model.SimulationTimeFunction;
import es.ull.iis.simulation.model.TimeUnit;
import es.ull.iis.simulation.model.WorkGroup;
import es.ull.iis.simulation.model.flow.DelayFlow;
import es.ull.iis.simulation.model.flow.ExclusiveChoiceFlow;
import es.ull.iis.simulation.model.flow.ReleaseResourcesFlow;
import es.ull.iis.simulation.model.flow.RequestResourcesFlow;
import es.ull.iis.simulation.model.flow.TimeFunctionDelayFlow;
import es.ull.iis.simulation.model.location.MoveFlow;
import es.ull.iis.simulation.port.parking.TruckWaitingManager.NotifyTrucksFlow;
import es.ull.iis.simulation.port.parking.TruckWaitingManager.WaitForVesselFlow;
import simkit.random.RandomVariate;
import simkit.random.RandomVariateFactory;

/**
 * @author Iván Castilla
 * TODO: Añadir al listener un mapeo entre Nodes y coordenadas (me lo pasarán)
 * TODO: Los barcos deben poder hacer operaciones de carga y descarga. Para la descarga, el camión debe estar ahí (como funciona ahora el modelo); para la carga, los camiones empiezan a 
 * llegar antes, y van descargando en el muelle. Después, el barco cargará cuando llegue. Este cambio implica: 1. Distinguir cargas y descargas por tipo de mercancía; 2. crear entidades para 
 * cada tipo de operación, de forma que las órdenes de carga se generen ANTES de la llegada física del barco (2-3 días), y las de descarga, al llegar el barco al anchorage (como ahora); 
 * 3. crear flujos diferentes para cada tipo de orden, incluyendo el de carga del barco dos pasos (descarga de la mercancía en el muelle, carga en el barco).  
 */
public class PortParkingModel extends Simulation {
	/** Maximum load of trucks */
	public static final RandomVariate TRUCK_MAX_LOAD = RandomVariateFactory.getInstance("ConstantVariate", 20);
	/** Size of trucks (for locations) */
	public static final int TRUCK_SIZE = 1;
	/** Size of vessels (for locations) */
	public static final int VESSEL_SIZE = 1;
	public static final TimeFunction T_VESSEL_PAPERWORK_IN = TimeFunctionFactory.getInstance("UniformVariate", 30, 40);
	public static final TimeFunction T_VESSEL_PAPERWORK_OUT = TimeFunctionFactory.getInstance("UniformVariate", 20, 30);
	public static final double TONES_PER_TRUCK = 200.0;
	/** Time unit of the model */
	public static final TimeUnit TIME_UNIT = TimeUnit.MINUTE;
	/** The minimum amount of load that is considered significant. Useful for avoid rounding error */
	public static final double MIN_LOAD = 0.001;
	/** Time to load/unload a container, 30 minutes +- 10 minutes, characterized by a beta distribution */
	private static final TimeFunction TRANSSHIPMENT_OP_TIME =  TimeFunctionFactory.getInstance("ScaledVariate", RandomVariateFactory.getInstance("BetaVariate", 10.0, 10.0), 10, 25);
	private static final TimeFunction T_ENTRANCE_PARKING = TimeFunctionFactory.getInstance("ConstantVariate", 10);
	private static final TimeFunction T_PARKING_EXIT = TimeFunctionFactory.getInstance("ConstantVariate", 10);
	private static final TimeFunction T_FROM_SOURCE_TO_ANCHORAGE = TimeFunctionFactory.getInstance("ConstantVariate", 100);
	private static final long T_FIRST_ARRIVAL = 0L;
	private static final SimulationTimeFunction T_INTERARRIVAL = new SimulationTimeFunction(PortParkingModel.TIME_UNIT, "ConstantVariate", 7700);
	private static final int PARKING_CAPACITY = 5;
	private final TruckWaitingManager truckWaitingManager;
	private final TransshipmentOperationsVesselFlow transVesselFlow;
	
	/**
	 * @param id
	 * @param description
	 * @param startTs
	 * @param endTs
	 */
	public PortParkingModel(int id, long endTs, String fileName) {
		super(id, "Santander Port simulation " + id, TIME_UNIT, 0, endTs);
		
		truckWaitingManager = new TruckWaitingManager(this);
		transVesselFlow = new TransshipmentOperationsVesselFlow(this);
		final TruckCreatorFlow tCreator = createModelForTrucks(PARKING_CAPACITY);		
		createModelForVessels(tCreator, fileName);
	}

	/**
	 * Creates the wokflow for vessels. Each vessel may start from a different source, then goes to the anchorage.
	 * Once in the anchorage, it waits there until there is a free quay that fits its wares. Afterwards, the vessel
	 * goes to the quay, do some paperwork, and starts unloading its wares. 
	 */
	private void createModelForVessels(TruckCreatorFlow tCreator, String fileName) {
		final VesselRouter vesselRouter = new VesselRouter(this, T_FROM_SOURCE_TO_ANCHORAGE);
		final int nQuays = QuayType.values().length;
		final MoveFlow toAnchorageFlow = new MoveFlow(this, "Go to anchorage", Locations.VESSEL_ANCHORAGE.getNode(), vesselRouter);
		final RequestResourcesFlow reqQuayFlow = new RequestResourcesFlow(this, "Check for quay available", 0);
		final ExclusiveChoiceFlow choiceQuayFlow = new ExclusiveChoiceFlow(this);

		toAnchorageFlow.link(tCreator).link(reqQuayFlow).link(choiceQuayFlow);
		
		final DelayFlow paperWorkInDelayFlow = new DelayFlow(this, "Delay due to paper work at docking") {
			
			@Override
			public long getDurationSample(Element elem) {
		    	return Math.max(0, Math.round(T_VESSEL_PAPERWORK_IN.getValue(elem)));
			}
		}; 
		final NotifyTrucksFlow notifyTrucksFlow = truckWaitingManager.getVesselFlow();
		final DelayFlow paperWorkOutDelayFlow = new DelayFlow(this, "Delay due to paper work at undocking") {
			
			@Override
			public long getDurationSample(Element elem) {
		    	return Math.max(0, Math.round(T_VESSEL_PAPERWORK_OUT.getValue(elem)));
			}
		}; 
		// Modeling quays as resources to handle the arrival of vessels in advance
		final ResourceType[] rtQuays = new ResourceType[nQuays];
		final WorkGroup[] wgQuays = new WorkGroup[nQuays];
		for (int i = 0; i < nQuays; i++) {
			rtQuays[i] = new ResourceType(this, "Quay " + i);
			rtQuays[i].addGenericResources(QuayType.values()[i].getCapacity());
			wgQuays[i] = new WorkGroup(this, rtQuays[i], 1);
			reqQuayFlow.newWorkGroupAdder(wgQuays[i]).withPriority(QuayType.values()[i].getPriority()).withCondition(new QuayCondition(QuayType.values()[i])).add();
			choiceQuayFlow.link(
					new MoveFlow(this, "To Quay " + i, QuayType.values()[i].getLocation().getNode(), vesselRouter), 
					new BookedQuayCondition(rtQuays[i])).link(paperWorkInDelayFlow);
		}
		
		final MoveFlow returnFlow = new MoveFlow(this, "Return from Quay", Locations.VESSEL_SRC.getNode(), vesselRouter);			
		paperWorkInDelayFlow.link(notifyTrucksFlow).link(transVesselFlow).link(paperWorkOutDelayFlow).link(returnFlow);
		new VesselCreator(this, toAnchorageFlow, T_INTERARRIVAL, T_FIRST_ARRIVAL, fileName);
	}

	private TruckCreatorFlow createModelForTrucks(int parkingCapacity) {
		// Performs a brief delay until it effectively arrives at the initial location
		final DelayFlow truckCreationDelayFlow = new DelayFlow(this, "Delay due to truck creation") {
			
			@Override
			public long getDurationSample(Element elem) {
		    	return Math.max(0, Math.round(((Truck)elem).getSource().getInitialDelay().getValue(elem)));
			}
			
			@Override
			public void afterFinalize(ElementInstance ei) {
				super.afterFinalize(ei);
				Truck truck = (Truck)ei.getElement(); 
				truck.requiresTransshipmentOperation();
				// First makes the truck spawn
				truck.getSource().getSpawnLocation().getNode().enter(truck);
			}
		};

		// Modeling parking slots as resources to handle the arrival of trucks in advance
		final ResourceType rtParkSpace = new ResourceType(this, "Park slot");
		rtParkSpace.addGenericResources(parkingCapacity);
		final WorkGroup wgParkSlot = new WorkGroup(this, rtParkSpace, 1);
		
		final TruckRouter truckRouter = new TruckRouter(parkingCapacity * TRUCK_SIZE, T_ENTRANCE_PARKING, T_PARKING_EXIT);
		
		final MoveFlow toEntranceFlow = new MoveFlow(this, "Go to the port entrance", truckRouter.getPortEntrance(), truckRouter);
		
		final RequestResourcesFlow reqParkingFlow = new RequestResourcesFlow(this, "Check whether there is a parking slot available", 0);
		reqParkingFlow.newWorkGroupAdder(wgParkSlot).add();
		
		final MoveFlow toParkingFlow = new MoveFlow(this, "Go to the parking", truckRouter.getParking(), truckRouter);
		final WaitForVesselFlow waitForVessel = truckWaitingManager.getTruckFlow();
		final TimeFunctionDelayFlow performTransshipmentFlow = new TimeFunctionDelayFlow(this, "Transshipment operation", TRANSSHIPMENT_OP_TIME) {
			@Override
			public void afterFinalize(ElementInstance ei) {
				super.afterFinalize(ei);
				Truck truck = ((Truck)ei.getElement());
				truck.performTransshipmentOperation();
				transVesselFlow.signal(truck.getServingVessel());
				simul.notifyInfo(new PortInfo(simul, PortInfo.Type.TRUCK_LOADED, truck, truck.getTs()));
				simul.notifyInfo(new PortInfo(simul, PortInfo.Type.VESSEL_UNLOADED, truck.getServingVessel(), truck.getTs()));
			}
		};
		final MoveFlow fromParkingFlow = new MoveFlow(this, "Depart from the parking", truckRouter.getPortExit(), truckRouter);
		final ReleaseResourcesFlow relParkingFlow = new ReleaseResourcesFlow(this, "Free parking slot", wgParkSlot);
		final ExclusiveChoiceFlow choiceDestinationFlow = new ExclusiveChoiceFlow(this);
		truckCreationDelayFlow.link(toEntranceFlow).link(reqParkingFlow).link(toParkingFlow).link(waitForVessel).link(performTransshipmentFlow);
		performTransshipmentFlow.link(fromParkingFlow).link(relParkingFlow).link(choiceDestinationFlow);
		
		// There is one element type per source (useful for flows)
		final ElementType[] truckSources = new ElementType[TruckSource.values().length];
		final TransshipmentPendingCondition trCond = new TransshipmentPendingCondition();
		
		for (TruckSource source : TruckSource.values()) {
			truckSources[source.ordinal()] = new ElementType(this, "Type for truck source " + source.ordinal());
			final MoveFlow toExitFlow = new MoveFlow(this, "Return to exit point", source.getSpawnLocation().getNode(), truckRouter);
			choiceDestinationFlow.link(toExitFlow, new ElementTypeCondition(truckSources[source.ordinal()]));
			final MoveFlow toWarehouseFlow = new MoveFlow(this, "Return to warehouse", source.getWarehouseLocation(), truckRouter);
			final ExclusiveChoiceFlow mustOperateAgainFlow = new ExclusiveChoiceFlow(this);
			toExitFlow.link(toWarehouseFlow).link(mustOperateAgainFlow);
			mustOperateAgainFlow.link(toEntranceFlow, trCond);
		}	
		
		final TruckCreatorFlow tCreator = new TruckCreatorFlow(this, truckSources, truckCreationDelayFlow);
		return tCreator;
	}
	
	class QuayCondition extends Condition<ElementInstance> {
		private final QuayType quay;
		
		public QuayCondition(QuayType quay) {
			this.quay = quay;
		}
		@Override
		public boolean check(ElementInstance fe) {
			return ((Vessel) fe.getElement()).getPotentialQuays().contains(quay);
		}
		
	}

	class TransshipmentPendingCondition extends Condition<ElementInstance> {
		public TransshipmentPendingCondition() {
		}

		@Override
		public boolean check(ElementInstance fe) {
			final Truck truck = (Truck) fe.getElement();
			return truck.requiresTransshipmentOperation();
		}
		
	}
	class BookedQuayCondition extends Condition<ElementInstance> {
		private final ResourceType rtQuay;
		public BookedQuayCondition(ResourceType rtQuay) {
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
