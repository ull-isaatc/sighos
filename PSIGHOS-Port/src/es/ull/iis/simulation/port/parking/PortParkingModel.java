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
import es.ull.iis.simulation.model.flow.InitializerFlow;
import es.ull.iis.simulation.model.flow.ParallelFlow;
import es.ull.iis.simulation.model.flow.ReleaseResourcesFlow;
import es.ull.iis.simulation.model.flow.RequestResourcesFlow;
import es.ull.iis.simulation.model.flow.TimeFunctionDelayFlow;
import es.ull.iis.simulation.model.location.MoveFlow;
import es.ull.iis.simulation.port.parking.TransshipmentOrder.OperationType;
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
	/** Time unit of the model */
	public static final TimeUnit TIME_UNIT = TimeUnit.MINUTE;
	/** Maximum load of trucks */
	public static final RandomVariate TRUCK_MAX_LOAD = RandomVariateFactory.getInstance("ConstantVariate", 20);
	/** Size of trucks (for locations) */
	public static final int TRUCK_SIZE = 1;
	/** Size of vessels (for locations) */
	public static final int VESSEL_SIZE = 1;
	public static final TimeFunction T_VESSEL_ADVANCE_ARRIVAL = TimeFunctionFactory.getInstance("UniformVariate", TIME_UNIT.convert(2, TimeUnit.DAY), TIME_UNIT.convert(3, TimeUnit.DAY));
	public static final TimeFunction T_VESSEL_PAPERWORK_IN = TimeFunctionFactory.getInstance("UniformVariate", 30, 40);
	public static final TimeFunction T_VESSEL_PAPERWORK_OUT = TimeFunctionFactory.getInstance("UniformVariate", 20, 30);
	public static final double TONES_PER_TRUCK = 200.0;
	/** The minimum amount of load that is considered significant. Useful for avoid rounding error */
	public static final double MIN_LOAD = 0.001;
	/** Time to unload wares from the {@link Vessel} to a {@link Truck}: 30 minutes +- 5 minutes, characterized by a beta distribution */
	private static final TimeFunction UNLOAD_TIME =  TimeFunctionFactory.getInstance("ScaledVariate", RandomVariateFactory.getInstance("BetaVariate", 10.0, 10.0), 10, 25);
	/** Time to load wares to the {@link Vessel} from the quay: 25 minutes +- 5 minutes, characterized by a beta distribution */
	private static final TimeFunction LOAD_TIME =  TimeFunctionFactory.getInstance("ScaledVariate", RandomVariateFactory.getInstance("BetaVariate", 10.0, 10.0), 10, 20);
	/** Time to unload wares from a {@link Truck} to the quay: 10 minutes +- 2 minutes, characterized by a beta distribution */
	private static final TimeFunction UNLOAD_TRUCK_TIME =  TimeFunctionFactory.getInstance("ScaledVariate", RandomVariateFactory.getInstance("BetaVariate", 10.0, 10.0), 4, 8);
	private static final TimeFunction T_ENTRANCE_PARKING = TimeFunctionFactory.getInstance("ConstantVariate", 10);
	private static final TimeFunction T_PARKING_EXIT = TimeFunctionFactory.getInstance("ConstantVariate", 10);
	private static final TimeFunction T_FROM_SOURCE_TO_ANCHORAGE = TimeFunctionFactory.getInstance("ConstantVariate", 100);
	private static final long T_FIRST_ARRIVAL = 0L;
	private static final SimulationTimeFunction T_INTERARRIVAL = new SimulationTimeFunction(PortParkingModel.TIME_UNIT, "ConstantVariate", 7700);
	private static final int PARKING_CAPACITY = 5;
	private final TruckWaitingManager truckWaitingForVesselAtQuayManager;
	private final TruckWaitingManager truckWaitingForVesselAtAnchorageManager;
	private final TransshipmentOperationsVesselFlow transVesselFlow;
	
	/**
	 * @param id
	 * @param description
	 * @param startTs
	 * @param endTs
	 */
	public PortParkingModel(int id, long endTs, String fileName) {
		super(id, "Santander Port simulation " + id, TIME_UNIT, 0, endTs);
		truckWaitingForVesselAtQuayManager = new TruckWaitingManager(this);
		truckWaitingForVesselAtAnchorageManager = new TruckWaitingManager(this);
		transVesselFlow = new TransshipmentOperationsVesselFlow(this);
		final TruckCreatorFlow tCreator = createModelForTrucks();		
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

		// Performs a brief delay until it effectively arrives at the initial location
		final DelayFlow vesselCreationDelayFlow = new DelayFlow(this, "Delay before the vessel effectively appears") {
			
			@Override
			public long getDurationSample(Element elem) {
				final Vessel vessel = (Vessel)elem;
				// There is no delay if the vessel does not include any load operation
				if (!vessel.includesLoadOperations()) {
					return 0;
				}
		    	return Math.max(0, Math.round(T_VESSEL_ADVANCE_ARRIVAL.getValue(elem)));
			}
			
			@Override
			public void afterFinalize(ElementInstance ei) {
				super.afterFinalize(ei);
				final Vessel vessel = (Vessel)ei.getElement();
				Locations.VESSEL_SRC.getNode().enter(vessel);
			}
		};
		
		final MoveFlow toAnchorageFlow = new MoveFlow(this, "Go to anchorage", Locations.VESSEL_ANCHORAGE.getNode(), vesselRouter) {
			@Override
			public void afterFinalize(ElementInstance fe) {
				super.afterFinalize(fe);
				final Vessel vessel = (Vessel) fe.getElement();
				simul.notifyInfo(new PortInfo(simul, PortInfo.Type.VESSEL_CREATED, vessel, vessel.getTs()));
			}
		};
		final RequestResourcesFlow reqQuayFlow = new RequestResourcesFlow(this, "Check for quay available", 0);
		final ExclusiveChoiceFlow choiceQuayFlow = new ExclusiveChoiceFlow(this);
		final NotifyTrucksFlow notifyTrucksAtAnchorageFlow = truckWaitingForVesselAtAnchorageManager.getVesselFlow();

		tCreator.link(vesselCreationDelayFlow).link(toAnchorageFlow).link(notifyTrucksAtAnchorageFlow).link(reqQuayFlow).link(choiceQuayFlow);
		
		final DelayFlow paperWorkInDelayFlow = new TimeFunctionDelayFlow(this, "Delay due to paper work at docking", T_VESSEL_PAPERWORK_IN); 
		final NotifyTrucksFlow notifyTrucksAtQuayFlow = truckWaitingForVesselAtQuayManager.getVesselFlow();
		final DelayFlow paperWorkOutDelayFlow = new TimeFunctionDelayFlow(this, "Delay due to paper work at undocking", T_VESSEL_PAPERWORK_OUT); 
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
		paperWorkInDelayFlow.link(notifyTrucksAtQuayFlow).link(transVesselFlow).link(paperWorkOutDelayFlow).link(returnFlow);
		new VesselCreator(this, tCreator, T_INTERARRIVAL, T_FIRST_ARRIVAL, fileName);
	}

	private TruckCreatorFlow createModelForTrucks() {
		final TruckRouter truckRouter = new TruckRouter(PARKING_CAPACITY * TRUCK_SIZE, T_ENTRANCE_PARKING, T_PARKING_EXIT);
		
		final ExclusiveChoiceFlow selectTranssType = new ExclusiveChoiceFlow(this) {
			@Override
			public boolean beforeRequest(ElementInstance ei) {
				Truck truck = (Truck)ei.getElement(); 
				truck.requiresTransshipmentOperation();
				return super.beforeRequest(ei);
			}
		};
		
		// There is one element type per source (useful for flows)
		final ElementType[] truckSources = new ElementType[TruckSource.values().length];
		
		for (TruckSource source : TruckSource.values()) {
			truckSources[source.ordinal()] = new ElementType(this, "Type for truck source " + source.ordinal());
		}
		selectTranssType.link(createFlowForUnloadOperations(truckRouter, truckSources), new SpecificOperationCondition(OperationType.UNLOAD));
		selectTranssType.link(createFlowForLoadOperations(truckRouter, truckSources), new SpecificOperationCondition(OperationType.LOAD));
		
		final TruckCreatorFlow tCreator = new TruckCreatorFlow(this, truckSources, selectTranssType);
		return tCreator;
	}

	private InitializerFlow createFlowForLoadOperations(TruckRouter truckRouter, ElementType[] truckSources) {
		// Performs a brief delay until it effectively arrives at the initial location
		final DelayFlow initialDelayFlow = new DelayFlow(this, "Delay when the trucks appear") {
			
			@Override
			public long getDurationSample(Element elem) {
		    	return Math.max(0, Math.round(((Truck)elem).getSource().getInitialDelay().getValue(elem)));
			}
			
			@Override
			public void afterFinalize(ElementInstance ei) {
				super.afterFinalize(ei);
				Truck truck = (Truck)ei.getElement(); 
				// First makes the truck spawn
				truck.getSource().getSpawnLocation().getNode().enter(truck);
			}
		};
		final WaitForVesselFlow waitForVesselAtQuayFlow = truckWaitingForVesselAtQuayManager.getTruckFlow(this);
		
		// Modeling parking slots as resources to handle the arrival of trucks in advance
		final ResourceType rtParkSpace = new ResourceType(this, "Park slot");
		rtParkSpace.addGenericResources(PARKING_CAPACITY);
		final WorkGroup wgParkSlot = new WorkGroup(this, rtParkSpace, 1);
		
		final MoveFlow toWaitingAreaFlow = new MoveFlow(this, "Go to the waiting area", truckRouter.getWaitingArea(), truckRouter);
		
		final RequestResourcesFlow reqParkingFlow = new RequestResourcesFlow(this, "Check whether there is a parking slot available", 0);
		reqParkingFlow.newWorkGroupAdder(wgParkSlot).add();
		
		final MoveFlow toTransshipmentAreaFlow = new MoveFlow(this, "Go to the transshipment area", truckRouter.getTransshipmentArea(), truckRouter);
		final TimeFunctionDelayFlow performUnloadFlow = new TimeFunctionDelayFlow(this, "Unload truck operation", UNLOAD_TRUCK_TIME) {
			@Override
			public void afterFinalize(ElementInstance ei) {
				super.afterFinalize(ei);
				Truck truck = ((Truck)ei.getElement());
				truck.unloadOperation();
			}
		};
		// TODO: Ahora mismo todas las cargas pueden ser simultáneas. Habría que limitarlo
		final TimeFunctionDelayFlow performLoadVesselFlow = new TimeFunctionDelayFlow(this, "Load vessel operation", LOAD_TIME) {
			@Override
			public void afterFinalize(ElementInstance ei) {
				super.afterFinalize(ei);
				Truck truck = ((Truck)ei.getElement());
				truck.loadVesselOperation();
				transVesselFlow.signal(truck.getServingVessel());
			}
		};
		final MoveFlow fromParkingFlow = new MoveFlow(this, "Depart from the parking", truckRouter.getPortExit(), truckRouter);
		final ReleaseResourcesFlow relParkingFlow = new ReleaseResourcesFlow(this, "Free parking slot", wgParkSlot);
		final ParallelFlow parFlow = new ParallelFlow(this);
		initialDelayFlow.link(toWaitingAreaFlow).link(reqParkingFlow).link(toTransshipmentAreaFlow).link(performUnloadFlow).link(parFlow);
		parFlow.link(waitForVesselAtQuayFlow).link(performLoadVesselFlow);
		final ExclusiveChoiceFlow choiceDestinationFlow = new ExclusiveChoiceFlow(this);
		parFlow.link(fromParkingFlow).link(relParkingFlow).link(choiceDestinationFlow);
		
		final TransshipmentPendingCondition trCond = new TransshipmentPendingCondition();
		
		for (TruckSource source : TruckSource.values()) {
			final MoveFlow toExitFlow = new MoveFlow(this, "Return to exit point", source.getSpawnLocation().getNode(), truckRouter);
			choiceDestinationFlow.link(toExitFlow, new ElementTypeCondition(truckSources[source.ordinal()]));
			final MoveFlow toWarehouseFlow = new MoveFlow(this, "Return to warehouse", source.getWarehouseLocation(), truckRouter);
			final ExclusiveChoiceFlow mustOperateAgainFlow = new ExclusiveChoiceFlow(this);
			toExitFlow.link(toWarehouseFlow).link(mustOperateAgainFlow);
			mustOperateAgainFlow.link(toWaitingAreaFlow, trCond);
		}		
		return initialDelayFlow;
		
	}
	
	private InitializerFlow createFlowForUnloadOperations(TruckRouter truckRouter, ElementType[] truckSources) {
		// Performs a brief delay until it effectively arrives at the initial location
		final DelayFlow initialDelayFlow = new DelayFlow(this, "Delay when the trucks appear") {
			
			@Override
			public long getDurationSample(Element elem) {
		    	return Math.max(0, Math.round(((Truck)elem).getSource().getInitialDelay().getValue(elem)));
			}
			
			@Override
			public void afterFinalize(ElementInstance ei) {
				super.afterFinalize(ei);
				Truck truck = (Truck)ei.getElement(); 
				// First makes the truck spawn
				truck.getSource().getSpawnLocation().getNode().enter(truck);
			}
		};
		final WaitForVesselFlow waitForVesselAtAnchorageFlow = truckWaitingForVesselAtAnchorageManager.getTruckFlow(this);
		final WaitForVesselFlow waitForVesselAtQuayFlow = truckWaitingForVesselAtQuayManager.getTruckFlow(this);
		waitForVesselAtAnchorageFlow.link(initialDelayFlow);
		
		// Modeling parking slots as resources to handle the arrival of trucks in advance
		final ResourceType rtParkSpace = new ResourceType(this, "Park slot");
		rtParkSpace.addGenericResources(PARKING_CAPACITY);
		final WorkGroup wgParkSlot = new WorkGroup(this, rtParkSpace, 1);
		
		final MoveFlow toWaitingAreaFlow = new MoveFlow(this, "Go to the waiting area", truckRouter.getWaitingArea(), truckRouter);
		
		final RequestResourcesFlow reqParkingFlow = new RequestResourcesFlow(this, "Check whether there is a parking slot available", 0);
		reqParkingFlow.newWorkGroupAdder(wgParkSlot).add();
		
		final MoveFlow toTransshipmentAreaFlow = new MoveFlow(this, "Go to the transshipment area", truckRouter.getTransshipmentArea(), truckRouter);
		final TimeFunctionDelayFlow performTransshipmentFlow = new TimeFunctionDelayFlow(this, "Unload vessel operation", UNLOAD_TIME) {
			@Override
			public void afterFinalize(ElementInstance ei) {
				super.afterFinalize(ei);
				Truck truck = ((Truck)ei.getElement());
				truck.unloadVesselOperation();
				transVesselFlow.signal(truck.getServingVessel());
				simul.notifyInfo(new PortInfo(simul, PortInfo.Type.VESSEL_UNLOADED, truck.getServingVessel(), truck.getTs()));
				simul.notifyInfo(new PortInfo(simul, PortInfo.Type.TRUCK_LOADED, truck, truck.getTs()));
			}
		};
		final MoveFlow fromParkingFlow = new MoveFlow(this, "Depart from the parking", truckRouter.getPortExit(), truckRouter);
		final ReleaseResourcesFlow relParkingFlow = new ReleaseResourcesFlow(this, "Free parking slot", wgParkSlot);
		final ExclusiveChoiceFlow choiceDestinationFlow = new ExclusiveChoiceFlow(this);
		initialDelayFlow.link(toWaitingAreaFlow).link(reqParkingFlow).link(toTransshipmentAreaFlow).link(waitForVesselAtQuayFlow).link(performTransshipmentFlow);
		performTransshipmentFlow.link(fromParkingFlow).link(relParkingFlow).link(choiceDestinationFlow);
		
		final TransshipmentPendingCondition trCond = new TransshipmentPendingCondition();
		
		for (TruckSource source : TruckSource.values()) {
			final MoveFlow toExitFlow = new MoveFlow(this, "Return to exit point", source.getSpawnLocation().getNode(), truckRouter);
			choiceDestinationFlow.link(toExitFlow, new ElementTypeCondition(truckSources[source.ordinal()]));
			final MoveFlow toWarehouseFlow = new MoveFlow(this, "Return to warehouse", source.getWarehouseLocation(), truckRouter);
			final ExclusiveChoiceFlow mustOperateAgainFlow = new ExclusiveChoiceFlow(this);
			toExitFlow.link(toWarehouseFlow).link(mustOperateAgainFlow);
			mustOperateAgainFlow.link(toWaitingAreaFlow, trCond);
		}		
		return waitForVesselAtAnchorageFlow;
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
	
	class SpecificOperationCondition extends  Condition<ElementInstance> {
		private final OperationType opType;
		public SpecificOperationCondition(OperationType opType) {
			this.opType = opType;
		}

		@Override
		public boolean check(ElementInstance fe) {
			final Truck truck = (Truck) fe.getElement();
			return opType.equals(truck.getOrder().getOpType());
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
