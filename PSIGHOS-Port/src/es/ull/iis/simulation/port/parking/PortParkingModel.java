/**
 * 
 */
package es.ull.iis.simulation.port.parking;

import java.util.ArrayList;

import es.ull.iis.function.TimeFunction;
import es.ull.iis.function.TimeFunctionFactory;
import es.ull.iis.simulation.condition.Condition;
import es.ull.iis.simulation.condition.ElementTypeCondition;
import es.ull.iis.simulation.model.Element;
import es.ull.iis.simulation.model.ElementInstance;
import es.ull.iis.simulation.model.ElementType;
import es.ull.iis.simulation.model.Resource;
import es.ull.iis.simulation.model.ResourceType;
import es.ull.iis.simulation.model.Simulation;
import es.ull.iis.simulation.model.SimulationTimeFunction;
import es.ull.iis.simulation.model.SimulationWeeklyPeriodicCycle;
import es.ull.iis.simulation.model.TimeUnit;
import es.ull.iis.simulation.model.WorkGroup;
import es.ull.iis.simulation.model.flow.ActivityFlow;
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
import es.ull.iis.util.cycle.WeeklyPeriodicCycle;
import simkit.random.RandomVariate;
import simkit.random.RandomVariateFactory;

/**
 * @author Iván Castilla
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
	public static final TimeFunction T_VESSEL_ADVANCE_ARRIVAL = TimeFunctionFactory.getInstance("UniformVariate", TIME_UNIT.convert(1.5, TimeUnit.DAY), TIME_UNIT.convert(2, TimeUnit.DAY));
	public static final TimeFunction T_VESSEL_PAPERWORK_IN = TimeFunctionFactory.getInstance("UniformVariate", 30, 40);
	public static final TimeFunction T_VESSEL_PAPERWORK_OUT = TimeFunctionFactory.getInstance("UniformVariate", 20, 30);
	public static final double TONES_PER_TRUCK = 200.0;
	/** The percentage variation of times when there is uncertainty on their value */
	public static final double TIME_UNCERTAINTY = 0.05;
	/** The minimum amount of load that is considered significant. Useful for avoid rounding error */
	public static final double MIN_LOAD = 0.001;
	/** Time to unload wares from the {@link Vessel} to a {@link Truck}: 30 minutes +- 5 minutes, characterized by a beta distribution */
	private static final TimeFunction UNLOAD_TIME =  TimeFunctionFactory.getInstance("ScaledVariate", RandomVariateFactory.getInstance("BetaVariate", 10.0, 10.0), 10, 25);
	/** Time to load wares to the {@link Vessel} from the quay: 25 minutes +- 5 minutes, characterized by a beta distribution */
	private static final TimeFunction LOAD_TIME =  TimeFunctionFactory.getInstance("ScaledVariate", RandomVariateFactory.getInstance("BetaVariate", 10.0, 10.0), 10, 20);
	/** Time to unload wares from a {@link Truck} to the quay: 10 minutes +- 2 minutes, characterized by a beta distribution */
	private static final TimeFunction UNLOAD_TRUCK_TIME =  TimeFunctionFactory.getInstance("ScaledVariate", RandomVariateFactory.getInstance("BetaVariate", 10.0, 10.0), 4, 8);
	private static final TimeFunction T_WAITING_TO_TRANSSHIPMENT_AREA = TimeFunctionFactory.getInstance("UniformVariate", 10*(1-PortParkingModel.TIME_UNCERTAINTY), 10*(1+PortParkingModel.TIME_UNCERTAINTY));
	private static final TimeFunction T_TRANSSHIPMENT_TO_EXIT_AREA = TimeFunctionFactory.getInstance("UniformVariate", 10*(1-PortParkingModel.TIME_UNCERTAINTY), 10*(1+PortParkingModel.TIME_UNCERTAINTY));
	private static final TimeFunction T_VESSEL_FROM_SOURCE_TO_ANCHORAGE = TimeFunctionFactory.getInstance("UniformVariate", 6*60*(1-PortParkingModel.TIME_UNCERTAINTY), 6*60*(1+PortParkingModel.TIME_UNCERTAINTY));
	private static final long T_VESSEL_FIRST_ARRIVAL = 0L;
	private static final SimulationTimeFunction T_VESSEL_INTERARRIVAL = new SimulationTimeFunction(PortParkingModel.TIME_UNIT, "ConstantVariate", 9900);
	private static final SimulationTimeFunction T_VESSEL_TEST_INTERARRIVAL = new SimulationTimeFunction(PortParkingModel.TIME_UNIT, "ConstantVariate", 99000);
	private static final int TRANSS_AREA_CAPACITY = 10;
	private static final int MAX_SIMULTANEOUS_LOADS = 5;
	/** The random seed to generate vessels. E.g. use "21" to select a vessel with only load operations; use "31" to select a vessel with only unload operations; use "47" to select a vessel with load and unload operations */
	public static final int RND_SEED_VESSELS = 31;
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
		createModelForVessels(tCreator);
		new VesselCreator(this, tCreator, T_VESSEL_INTERARRIVAL, T_VESSEL_FIRST_ARRIVAL, fileName);
	}

	/**
	 * @param id
	 * @param description
	 * @param startTs
	 * @param endTs
	 */
	public PortParkingModel(int id, long endTs, ArrayList<VesselTransshipmentOrder> operations) {
		super(id, "Santander Port simulation " + id, TIME_UNIT, 0, endTs);
		truckWaitingForVesselAtQuayManager = new TruckWaitingManager(this);
		truckWaitingForVesselAtAnchorageManager = new TruckWaitingManager(this);
		transVesselFlow = new TransshipmentOperationsVesselFlow(this);
		final TruckCreatorFlow tCreator = createModelForTrucks();		
		createModelForVessels(tCreator);
		new VesselCreator(this, tCreator, T_VESSEL_TEST_INTERARRIVAL, T_VESSEL_FIRST_ARRIVAL, operations);
	}
	
	/**
	 * Creates the wokflow for vessels. Each vessel may start from a different source, then goes to the anchorage.
	 * Once in the anchorage, it waits there until there is a free quay that fits its wares. Afterwards, the vessel
	 * goes to the quay, do some paperwork, and starts unloading its wares. 
	 */
	private void createModelForVessels(TruckCreatorFlow tCreator) {
		final VesselRouter vesselRouter = new VesselRouter(this, T_VESSEL_FROM_SOURCE_TO_ANCHORAGE);
		final int nQuays = QuayType.values().length;

		// Performs a brief delay until it effectively arrives at the initial location
		final DelayFlow vesselCreationDelayFlow = new DelayFlow(this, "Delay before the vessel effectively appears") {
			
			@Override
			public boolean beforeRequest(ElementInstance ei) {
				final Vessel vessel = (Vessel) ei.getElement();
				simul.notifyInfo(new PortInfo(simul, vessel, vessel.getTs()));
				return super.beforeRequest(ei);
			}
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
		
		final MoveFlow toAnchorageFlow = new MoveFlow(this, "Go to anchorage", Locations.VESSEL_ANCHORAGE.getNode(), vesselRouter);
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
	}

	private TruckCreatorFlow createModelForTrucks() {
		final TruckRouter truckRouter = new TruckRouter(TRANSS_AREA_CAPACITY * TRUCK_SIZE, T_WAITING_TO_TRANSSHIPMENT_AREA, T_TRANSSHIPMENT_TO_EXIT_AREA);
		
		final ExclusiveChoiceFlow selectInitTranssType = new ExclusiveChoiceFlow(this) {
			@Override
			public boolean beforeRequest(ElementInstance ei) {
				Truck truck = (Truck)ei.getElement(); 
				truck.requiresTransshipmentOperation();
				return super.beforeRequest(ei);
			}
		};

		// Modeling transshipment area slots as resources to handle the arrival of trucks in advance
		final ResourceType rtParkSpace = new ResourceType(this, "Transshipment area slot");
		// Transshipment area is only available from 8:00 to 20:00
		for (int i = 0; i < TRANSS_AREA_CAPACITY; i++) {
			final Resource resParkSpace = new Resource(this, "Transshipment area slot #" + i);
			resParkSpace.newTimeTableOrCancelEntriesAdder(rtParkSpace).withDuration(new SimulationWeeklyPeriodicCycle(TIME_UNIT, WeeklyPeriodicCycle.WEEKDAYS, 480, getEndTs()), 720).addTimeTableEntry();
		}
//		rtParkSpace.addGenericResources(PARKING_CAPACITY);
		final WorkGroup wgParkSlot = new WorkGroup(this, rtParkSpace, 1);

		// There is one element type per source (useful for flows)
		final ElementType[] truckSources = new ElementType[TruckSource.values().length];
		
		final ExclusiveChoiceFlow mustOperateAgainFlow = new ExclusiveChoiceFlow(this);
		final TransshipmentPendingCondition trCond = new TransshipmentPendingCondition();
		final ExclusiveChoiceFlow choiceDestinationFlow = new ExclusiveChoiceFlow(this);
		final MoveFlow[] toWarehouseFlow = new MoveFlow[TruckSource.values().length];
		
		for (TruckSource source : TruckSource.values()) {
			int index = source.ordinal();
			truckSources[index] = new ElementType(this, "Type for truck source " + index);
			final MoveFlow toExitFlow = new MoveFlow(this, "Return to exit point", source.getSpawnLocation().getNode(), truckRouter);
			choiceDestinationFlow.link(toExitFlow, new ElementTypeCondition(truckSources[index]));
			toWarehouseFlow[index] = new MoveFlow(this, "Return to warehouse", source.getWarehouseLocation(), truckRouter);
			toExitFlow.link(toWarehouseFlow[index]).link(mustOperateAgainFlow);
		}
		
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
		waitForVesselAtAnchorageFlow.link(initialDelayFlow);

		selectInitTranssType.link(waitForVesselAtAnchorageFlow, new SpecificOperationCondition(OperationType.UNLOAD));
		selectInitTranssType.link(initialDelayFlow);

		final ExclusiveChoiceFlow selectTranssType = new ExclusiveChoiceFlow(this);
		initialDelayFlow.link(selectTranssType);
		selectTranssType.link(createFlowForUnloadOperations(truckRouter, truckSources, wgParkSlot, choiceDestinationFlow), new SpecificOperationCondition(OperationType.UNLOAD));
		selectTranssType.link(createFlowForLoadOperations(truckRouter, truckSources, wgParkSlot, choiceDestinationFlow), new SpecificOperationCondition(OperationType.LOAD));
		
		mustOperateAgainFlow.link(selectTranssType, trCond);
		final TruckCreatorFlow tCreator = new TruckCreatorFlow(this, truckSources, selectInitTranssType);
		return tCreator;
	}

	private InitializerFlow createFlowForLoadOperations(final TruckRouter truckRouter, final ElementType[] truckSources, final WorkGroup wgParkSlot, ExclusiveChoiceFlow choiceDestinationFlow) {
		final WaitForVesselFlow waitForVesselAtQuayFlow = truckWaitingForVesselAtQuayManager.getTruckFlow(this);
		
		
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
		
		// Modeling parking slots as resources to handle the arrival of trucks in advance
		final ResourceType rtLoadResource = new ResourceType(this, "Resource for loading operations");
		rtLoadResource.addGenericResources(MAX_SIMULTANEOUS_LOADS);
		final WorkGroup wgLoadResource = new WorkGroup(this, rtLoadResource, 1);
		
		final ActivityFlow performLoadVesselFlow = new ActivityFlow(this, "Load vessel operation") {
			@Override
			public void afterFinalize(ElementInstance ei) {
				super.afterFinalize(ei);
				Truck truck = ((Truck)ei.getElement());
				truck.loadVesselOperation();
				transVesselFlow.signal(truck.getServingVessel());
			}
		};
		performLoadVesselFlow.newWorkGroupAdder(wgLoadResource).withDelay(LOAD_TIME).add();
		
		final MoveFlow fromParkingFlow = new MoveFlow(this, "Depart from the parking", truckRouter.getPortExit(), truckRouter);
		final ReleaseResourcesFlow relParkingFlow = new ReleaseResourcesFlow(this, "Free parking slot", wgParkSlot);
		final ParallelFlow parFlow = new ParallelFlow(this);
		toWaitingAreaFlow.link(reqParkingFlow).link(toTransshipmentAreaFlow).link(performUnloadFlow).link(parFlow);
		parFlow.link(waitForVesselAtQuayFlow).link(performLoadVesselFlow);
		parFlow.link(fromParkingFlow).link(relParkingFlow).link(choiceDestinationFlow);
		
		return toWaitingAreaFlow;
		
	}
	
	private InitializerFlow createFlowForUnloadOperations(final TruckRouter truckRouter, final ElementType[] truckSources, final WorkGroup wgParkSlot, ExclusiveChoiceFlow choiceDestinationFlow) {
		final WaitForVesselFlow waitForVesselAtQuayFlow = truckWaitingForVesselAtQuayManager.getTruckFlow(this);
		
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
			}
		};
		final MoveFlow fromParkingFlow = new MoveFlow(this, "Depart from the parking", truckRouter.getPortExit(), truckRouter);
		final ReleaseResourcesFlow relParkingFlow = new ReleaseResourcesFlow(this, "Free parking slot", wgParkSlot);
		toWaitingAreaFlow.link(reqParkingFlow).link(toTransshipmentAreaFlow).link(waitForVesselAtQuayFlow).link(performTransshipmentFlow);
		performTransshipmentFlow.link(fromParkingFlow).link(relParkingFlow).link(choiceDestinationFlow);
		
		return toWaitingAreaFlow;
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
