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
import es.ull.iis.simulation.model.flow.WaitForSignalFlow;
import es.ull.iis.simulation.model.location.MoveFlow;
import es.ull.iis.simulation.port.parking.TruckWaitingManager.NotifyTrucksFlow;
import es.ull.iis.simulation.port.parking.TruckWaitingManager.WaitForVesselFlow;
import simkit.random.RandomVariateFactory;

/**
 * @author Iván Castilla
 * TODO: Añadir al listener un mapeo entre Nodes y coordenadas (me lo pasarán)
 * TODO: SAcar datos concretos de los barcos históricos de los datos en "Escalas y mercancías 2020-2021" y generar los barcos por bootstrap  
 */
public class PortParkingModel extends Simulation {
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
	private static final SimulationTimeFunction T_INTERARRIVAL = new SimulationTimeFunction(PortParkingModel.TIME_UNIT, "ConstantVariate", 770);
	private final TruckWaitingManager truckWaitingManager;
	private final VesselWaitingManager vesselWaitingManager;
	
	/**
	 * @param id
	 * @param description
	 * @param startTs
	 * @param endTs
	 */
	public PortParkingModel(int id, long endTs, int parkingCapacity) {
		super(id, "Santander Port simulation " + id, TIME_UNIT, 0, endTs);
		
		truckWaitingManager = new TruckWaitingManager(this);
		vesselWaitingManager = new VesselWaitingManager();
		final TruckCreatorFlow tCreator = createModelForTrucks(parkingCapacity);		
		createModelForVessels(tCreator);
	}

	/**
	 * Creates the wokflow for vessels. Each vessel may start from a different source, then goes to the anchorage.
	 * Once in the anchorage, it waits there until there is a free quay that fits its wares. Afterwards, the vessel
	 * goes to the quay, do some paperwork, and starts unloading its wares. 
	 */
	private void createModelForVessels(TruckCreatorFlow tCreator) {
		final VesselRouter vesselRouter = new VesselRouter(this, T_FROM_SOURCE_TO_ANCHORAGE);
		final int nQuays = QuayType.values().length;
		final MoveFlow toAnchorageFlow = new MoveFlow(this, "Go to anchorage", vesselRouter.getAnchorage(), vesselRouter);
		final RequestResourcesFlow reqQuayFlow = new RequestResourcesFlow(this, "Check for quay available", 0);
		final ExclusiveChoiceFlow choiceQuayFlow = new ExclusiveChoiceFlow(this);

		toAnchorageFlow.link(tCreator).link(reqQuayFlow).link(choiceQuayFlow);
		
		// TODO: Sustituir por la interacción con los camiones
//		final DelayFlow atQuayFlow = new TimeFunctionDelayFlow(this, "Unloading at quay", LOAD_TIME * 20);
		final WaitForSignalFlow atQuayFlow = new WaitForSignalFlow(this, "Wait for complete unloading tasks", vesselWaitingManager);
		final DelayFlow paperWorkInDelayFlow = new DelayFlow(this, "Delay due to paper work at docking") {
			
			@Override
			public long getDurationSample(Element elem) {
		    	return Math.max(0, Math.round(((Vessel)elem).getWares().getPaperWorkIn().getValue(elem)));
			}
		}; 
		final NotifyTrucksFlow notifyTrucksFlow = truckWaitingManager.getVesselFlow();
		final DelayFlow paperWorkOutDelayFlow = new DelayFlow(this, "Delay due to paper work at undocking") {
			
			@Override
			public long getDurationSample(Element elem) {
		    	return Math.max(0, Math.round(((Vessel)elem).getWares().getPaperWorkOut().getValue(elem)));
			}
		}; 
		// Modeling quays as resources to handle the arrival of vessels in advance
		final ResourceType[] rtQuays = new ResourceType[nQuays];
		final WorkGroup[] wgQuays = new WorkGroup[nQuays];
		for (int i = 0; i < nQuays; i++) {
			rtQuays[i] = new ResourceType(this, "Quay " + i);
			// Assuming 1 vessel per quay
			rtQuays[i].addGenericResources(1);
			wgQuays[i] = new WorkGroup(this, rtQuays[i], 1);
			reqQuayFlow.newWorkGroupAdder(wgQuays[i]).withCondition(new QuayCondition(QuayType.values()[i])).add();
			choiceQuayFlow.link(
					new MoveFlow(this, "To Quay " + i, QuayType.values()[i].getLocation(), vesselRouter), 
					new BookedQuayCondition(rtQuays[i])).link(paperWorkInDelayFlow);
		}
		
		final MoveFlow returnFlow = new MoveFlow(this, "Return from Quay", vesselRouter.getInitialLocation(), vesselRouter);			
		paperWorkInDelayFlow.link(notifyTrucksFlow).link(atQuayFlow).link(paperWorkOutDelayFlow).link(returnFlow);
		new VesselCreator(this, vesselRouter.getInitialLocation(), toAnchorageFlow, T_INTERARRIVAL, T_FIRST_ARRIVAL);
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
				// First makes the truck spawn
				truck.getSource().getSpawnLocation().enter(truck);
			}
		};

		// Modeling parking slots as resources to handle the arrival of trucks in advance
		final ResourceType rtParkSpace = new ResourceType(this, "Park slot");
		rtParkSpace.addGenericResources(parkingCapacity);
		final WorkGroup wgParkSlot = new WorkGroup(this, rtParkSpace, 1);
		
		final TruckRouter truckRouter = new TruckRouter(parkingCapacity * Truck.SIZE, T_ENTRANCE_PARKING, T_PARKING_EXIT);
		
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
				if (truck.getServingVessel().isEmpty())
					vesselWaitingManager.letVesselGo(truck.getServingVessel());
				simul.notifyInfo(new PortInfo(simul, PortInfo.Type.TRUCK_LOADED, truck, truck.getTs()));
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
			final MoveFlow toExitFlow = new MoveFlow(this, "Return to exit point", source.getSpawnLocation(), truckRouter);
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
			return ((Vessel) fe.getElement()).getWares().getPotentialQuays().contains(quay);
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
