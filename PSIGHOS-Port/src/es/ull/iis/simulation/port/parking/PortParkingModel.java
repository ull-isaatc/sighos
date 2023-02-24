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

/**
 * @author Iván Castilla
 * TODO: Añadir al listener un mapeo entre Nodes y coordenadas (me lo pasarán)
 * TODO: SAcar datos concretos de los barcos históricos de los datos en "Escalas y mercancías 2020-2021" y generar los barcos por bootstrap  
 */
public class PortParkingModel extends Simulation {
	public static final TimeUnit TIME_UNIT = TimeUnit.MINUTE;
	private static final TimeFunction LOAD_TIME =  TimeFunctionFactory.getInstance("UniformVariate", 25, 35); // TODO: Debería ser media 30
	private static final TimeFunction T_ENTRANCE_PARKING = TimeFunctionFactory.getInstance("ConstantVariate", 10);
	private static final TimeFunction T_PARKING_EXIT = TimeFunctionFactory.getInstance("ConstantVariate", 10);
	private static final long T_FIRST_ARRIVAL = 0L;
	private static final SimulationTimeFunction T_INTERARRIVAL = new SimulationTimeFunction(PortParkingModel.TIME_UNIT, "ConstantVariate", 220);
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
		
		truckWaitingManager = new TruckWaitingManager();
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
		final VesselRouter vesselRouter = new VesselRouter(this);
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
			
			@Override
			public void afterFinalize(ElementInstance ei) {
				super.afterFinalize(ei);
				truckWaitingManager.letTrucksStart((Vessel)ei.getElement());
			}
		}; 
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
		
		final ExclusiveChoiceFlow choiceReturnSpotFlow = new ExclusiveChoiceFlow(this);
		paperWorkInDelayFlow.link(atQuayFlow).link(paperWorkOutDelayFlow).link(choiceReturnSpotFlow);
		// There is one element type per source (useful for flows)
		final ElementType[] vesselSources = new ElementType[VesselSource.values().length];
		for (int i = 0; i < VesselSource.values().length; i++) {
			vesselSources[i] = new ElementType(this, "Type for vessel source " + i);
			final MoveFlow returnFlow = new MoveFlow(this, "Return from Quay", VesselSource.values()[i].getInitialLocation(), vesselRouter);			
			choiceReturnSpotFlow.link(returnFlow, new ElementTypeCondition(vesselSources[i]));
		}
		new VesselCreator(this, vesselSources, toAnchorageFlow, T_INTERARRIVAL, T_FIRST_ARRIVAL);
	}

	private TruckCreatorFlow createModelForTrucks(int parkingCapacity) {
		// Performs a brief delay until it effectively arrives at the initial location
		final DelayFlow truckCreationDelayFlow = new DelayFlow(this, "Delay due to truck creation") {
			
			@Override
			public long getDurationSample(Element elem) {
		    	return Math.max(0, Math.round(((Truck)elem).getSource().getTimeToInitialLocation().getValue(elem)));
			}
			
			@Override
			public void afterFinalize(ElementInstance ei) {
				super.afterFinalize(ei);
				Truck truck = (Truck)ei.getElement(); 
				truck.getSource().getInitialLocation().enter(truck);
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
		// TODO: Relacionar con la carga a descargar y que esté el barco
		final WaitForSignalFlow waitForVessel = new WaitForSignalFlow(this, "Wait for the vessel to arrive", truckWaitingManager);
		final TimeFunctionDelayFlow parkedFlow = new TimeFunctionDelayFlow(this, "Load tasks at the parking", LOAD_TIME) {
			@Override
			public void afterFinalize(ElementInstance ei) {
				super.afterFinalize(ei);
				Truck truck = ((Truck)ei.getElement());
				truck.load();
				if (truck.getServingVessel().isEmpty())
					vesselWaitingManager.letVesselGo(truck.getServingVessel());
				simul.notifyInfo(new PortInfo(simul, PortInfo.Type.TRUCK_LOADED, truck, truck.getTs()));
			}
		};
		final MoveFlow fromParkingFlow = new MoveFlow(this, "Depart from the parking", truckRouter.getPortExit(), truckRouter);
		final ReleaseResourcesFlow relParkingFlow = new ReleaseResourcesFlow(this, "Free parking slot", wgParkSlot);
		final ExclusiveChoiceFlow choiceDestinationFlow = new ExclusiveChoiceFlow(this);
		truckCreationDelayFlow.link(toEntranceFlow).link(reqParkingFlow).link(toParkingFlow).link(waitForVessel).link(parkedFlow);
		parkedFlow.link(fromParkingFlow).link(relParkingFlow).link(choiceDestinationFlow);
		
		// There is one element type per source (useful for flows)
		final ElementType[] truckSources = new ElementType[TruckSource.values().length];
		
		for (TruckSource source : TruckSource.values()) {
			truckSources[source.ordinal()] = new ElementType(this, "Type for truck source " + source.ordinal());
			final MoveFlow returnFlow = new MoveFlow(this, "Return to warehouse", source.getInitialLocation(), truckRouter);
			choiceDestinationFlow.link(returnFlow, new ElementTypeCondition(truckSources[source.ordinal()]));
//			final DelayFlow goAndBackFlow 
			// TODO: En este momento, el camión debe "desaparecer" de la localización física, esperar un rato que simula la ruta ida y vuelta a su almacén; y volver, pero solo en caso de que haya que descargar más.
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
