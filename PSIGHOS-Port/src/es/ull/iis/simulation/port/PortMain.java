/**
 * 
 */
package es.ull.iis.simulation.port;

import java.util.EnumSet;

import es.ull.iis.function.TimeFunctionFactory;
import es.ull.iis.simulation.condition.ElementTypeCondition;
import es.ull.iis.simulation.core.ElementCreator;
import es.ull.iis.simulation.core.ElementType;
import es.ull.iis.simulation.core.FlowDrivenActivity;
import es.ull.iis.simulation.core.Resource;
import es.ull.iis.simulation.core.ResourceType;
import es.ull.iis.simulation.core.Simulation;
import es.ull.iis.simulation.core.SimulationPeriodicCycle;
import es.ull.iis.simulation.core.SimulationTimeFunction;
import es.ull.iis.simulation.core.SimulationWeeklyPeriodicCycle;
import es.ull.iis.simulation.core.TimeDrivenActivity;
import es.ull.iis.simulation.core.TimeDrivenActivity.Modifier;
import es.ull.iis.simulation.core.TimeStamp;
import es.ull.iis.simulation.core.TimeUnit;
import es.ull.iis.simulation.core.WorkGroup;
import es.ull.iis.simulation.core.flow.SingleFlow;
import es.ull.iis.simulation.factory.SimulationFactory;
import es.ull.iis.simulation.factory.SimulationFactory.SimulationType;
import es.ull.iis.simulation.inforeceiver.StdInfoView;
import es.ull.iis.util.WeeklyPeriodicCycle;
import es.ull.iis.simulation.factory.SimulationObjectFactory;

/**
 * The main simulation class for a port. A port is divided into three areas: sea, yard and earth. Ships arrive at a 
 * specific berth, which counts on a fixed number of quay cranes to unload the charge. Each ship carries M containers, 
 * and quay cranes unload a container at a time. To unload a container, a truck must be available. Trucks lead the 
 * container to a specific block in the yard area. At his block, a yard crane puts the container in a free space. 
 * @author Iván Castilla
 *
 */
public class PortMain {
	private static final int NSIM = 1;
	private static final TimeUnit PORT_TIME_UNIT = TimeUnit.MINUTE;
	private static final TimeStamp START_TS = TimeStamp.getZero();
	private static final TimeStamp END_TS = TimeStamp.getWeek();
	private static final String DESCRIPTION = "Port of Santa Cruz";
	private static final int N_BERTHS = 1;
	private static final int[] N_QUAYS_PER_BERTH = {2};
	private static final int N_BLOCKS = 1;
	private static final int[] N_QUAYS_PER_BLOCK = {1};
	private static final int N_TRUCKS = 4;
	private static final String QUAY_CRANE = "Quay Crane";
	private static final String YARD_CRANE = "Yard Crane";
	private static final String TRUCK = "Truck";
	private static final String SHIP_TO_YARD_PROCESS = "Ship-to-yard process";
	private static final String ACT_UNLOAD = "Unload";
	private static final String ACT_TO_YARD = "Lead to yard";
	private static final String ACT_PLACE = "Place container";
	private static final String ACT_RETURN = "Return truck to ship";
	private static final String ID_VAR = "ID";
	private static final String BLOCK_ID_VAR = "BlockId";
	private static final String CONS_VAR = "ConstantVariate";
	private static final double[] TIME_TO_UNLOAD = {15.0};
	private static final double[] TIME_TO_YARD = {20.0};
	private static final double[] TIME_TO_PLACE = {10.0};
	private static final int[] CONTAINERS_PER_DAY = {10};

	/**
	 */
	public PortMain() {
	}

	private static Simulation createSimulation(int id) {
		// Instantiates a factory
		final SimulationObjectFactory simFactory = SimulationFactory.getInstance(SimulationType.SEQUENTIAL, id, DESCRIPTION + " " + id, PORT_TIME_UNIT, START_TS, END_TS);
		// Creates the main element types that drive the simulation, i.e., the processes for each berth
		final ElementType[] et = new ElementType[N_BERTHS]; 
		for (int i = 0; i < N_BERTHS; i++) {
			et[i] = simFactory.getElementTypeInstance(SHIP_TO_YARD_PROCESS + " " + i);
			et[i].putVar(ID_VAR, i);
			// The destination block
			et[i].addElementVar(BLOCK_ID_VAR, -1);
		}
		// Creates the resource types and specific resources
		final ResourceType[] rtQuayCranes = new ResourceType[N_BERTHS];
		for (int i = 0; i < N_BERTHS; i++) {
			rtQuayCranes[i] = simFactory.getResourceTypeInstance(QUAY_CRANE + " " + i);
			rtQuayCranes[i].putVar(ID_VAR, i);
			for (int j = 0; j < N_QUAYS_PER_BERTH[i]; j++) {
				final Resource res = simFactory.getResourceInstance(QUAY_CRANE + " " + i + "." + j);
				res.addTimeTableEntry(SimulationPeriodicCycle.newWeeklyCycle(PORT_TIME_UNIT), END_TS, rtQuayCranes[i]);
			}
		}
		final ResourceType rtTrucks = simFactory.getResourceTypeInstance(TRUCK);
		for (int i = 0; i < N_TRUCKS; i++) {
			final Resource res = simFactory.getResourceInstance(TRUCK + " " + i);
			res.addTimeTableEntry(SimulationPeriodicCycle.newWeeklyCycle(PORT_TIME_UNIT), END_TS, rtTrucks);
		}
		
		final ResourceType[] rtYardCranes = new ResourceType[N_BLOCKS];
		for (int i = 0; i < N_BLOCKS; i++) {
			rtYardCranes[i] = simFactory.getResourceTypeInstance(YARD_CRANE + " " + i);
			rtYardCranes[i].putVar(ID_VAR, i);
			for (int j = 0; j < N_QUAYS_PER_BLOCK[i]; j++) {
				final Resource res = simFactory.getResourceInstance(YARD_CRANE + " " + i + "." + j);
				res.addTimeTableEntry(SimulationPeriodicCycle.newWeeklyCycle(PORT_TIME_UNIT), END_TS, rtYardCranes[i]);
			}
		}
		
		// Defines the needs of the activities in terms of resources
		final WorkGroup wgEmpty = simFactory.getWorkGroupInstance(new ResourceType[] {}, new int[] {});
		final WorkGroup wgTruck = simFactory.getWorkGroupInstance(new ResourceType[] {rtTrucks}, new int[] {1});
		final WorkGroup []wgUnload = new WorkGroup[N_BERTHS];
		for (int i = 0; i < N_BERTHS; i++) {
			wgUnload[i] = simFactory.getWorkGroupInstance(new ResourceType[] {rtQuayCranes[i]}, new int[] {1});
		}
		final WorkGroup []wgPlace = new WorkGroup[N_BLOCKS];
		for (int i = 0; i < N_BLOCKS; i++) {
			wgPlace[i] = simFactory.getWorkGroupInstance(new ResourceType[] {rtYardCranes[i]}, new int[] {1});
		}
		
		// Activities
		final TimeDrivenActivity aUnload = simFactory.getTimeDrivenActivityInstance(ACT_UNLOAD, 0, EnumSet.noneOf(Modifier.class)); 
		for (int i = 0; i < N_BERTHS; i++) {
			aUnload.addWorkGroup(new SimulationTimeFunction(PORT_TIME_UNIT, CONS_VAR, TIME_TO_UNLOAD[i]), wgUnload[i], new ElementTypeCondition(et[i]));
		}
		final TimeDrivenActivity aToYard = simFactory.getTimeDrivenActivityInstance(ACT_TO_YARD, 0, EnumSet.noneOf(Modifier.class));
		for (int i = 0; i < N_BLOCKS; i++) {
			aToYard.addWorkGroup(new SimulationTimeFunction(PORT_TIME_UNIT, CONS_VAR, TIME_TO_YARD[i]), wgEmpty, 
					simFactory.getCustomizedConditionInstance("", "<%GET(@E." + BLOCK_ID_VAR + ")%> == " + i));
		}
		final TimeDrivenActivity aPlace = simFactory.getTimeDrivenActivityInstance(ACT_PLACE, 0, EnumSet.noneOf(Modifier.class));
		for (int i = 0; i < N_BLOCKS; i++) {
			aPlace.addWorkGroup(new SimulationTimeFunction(PORT_TIME_UNIT, CONS_VAR, TIME_TO_PLACE[i]), wgPlace[i], 
					simFactory.getCustomizedConditionInstance("", "<%GET(@E." + BLOCK_ID_VAR + ")%> == " + i));
		}
		// TODO: Exactly the same as aToYard... Unify?
		final TimeDrivenActivity aTruckReturn = simFactory.getTimeDrivenActivityInstance(ACT_RETURN, 0, EnumSet.noneOf(Modifier.class));
		for (int i = 0; i < N_BLOCKS; i++) {
			aTruckReturn.addWorkGroup(new SimulationTimeFunction(PORT_TIME_UNIT, CONS_VAR, TIME_TO_YARD[i]), wgEmpty, 
					simFactory.getCustomizedConditionInstance("", "<%GET(@E." + BLOCK_ID_VAR + ")%> == " + i));
		}

		// Defines the flow for the former activities
		final SingleFlow sfUnload = (SingleFlow)simFactory.getFlowInstance("SingleFlow", aUnload);
		final SingleFlow sfToYard = (SingleFlow)simFactory.getFlowInstance("SingleFlow", aToYard);
		final SingleFlow sfPlace = (SingleFlow)simFactory.getFlowInstance("SingleFlow", aPlace);		
		final SingleFlow sfTruckReturn = (SingleFlow)simFactory.getFlowInstance("SingleFlow", aTruckReturn);
		sfUnload.link(sfToYard);
		sfToYard.link(sfPlace);
		sfPlace.link(sfTruckReturn);
		
		// defines the main activity that drives the whole process, which involves seizing a truck
		final FlowDrivenActivity mainAct = simFactory.getFlowDrivenActivityInstance(SHIP_TO_YARD_PROCESS);
		mainAct.addWorkGroup(sfUnload, sfTruckReturn, wgTruck);
		final SingleFlow sfMain = (SingleFlow)simFactory.getFlowInstance("SingleFlow", mainAct);
		
		// Generate orders for unloading containers
		final SimulationWeeklyPeriodicCycle cGen = new SimulationWeeklyPeriodicCycle(PORT_TIME_UNIT, WeeklyPeriodicCycle.WEEKDAYS, START_TS, END_TS);
		for (int i = 0; i < N_BERTHS; i++) {
			final ElementCreator ec = simFactory.getElementCreatorInstance(TimeFunctionFactory.getInstance(CONS_VAR, CONTAINERS_PER_DAY), et[i], sfMain);
			simFactory.getTimeDrivenGeneratorInstance(ec, cGen);
		}
		
		return simFactory.getSimulation();
	}
	
	private static void addListeners(Simulation sim) {
		sim.addInfoReceiver(new StdInfoView(sim));
	}
	
	public static void main(String[] args) {
		for (int id = 0; id < NSIM; id++) {
			final Simulation sim = createSimulation(id);
			addListeners(sim);
			sim.run();
		}
	}
}
