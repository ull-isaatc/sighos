/**
 * 
 */
package es.ull.iis.simulation.port;

import es.ull.iis.simulation.sequential.Resource;
import es.ull.iis.simulation.sequential.ResourceType;

import java.util.EnumSet;

import es.ull.iis.function.TimeFunctionFactory;
import es.ull.iis.simulation.condition.ElementTypeCondition;
import es.ull.iis.simulation.sequential.ElementCreator;
import es.ull.iis.simulation.sequential.FlowDrivenActivity;
import es.ull.iis.simulation.core.SimulationPeriodicCycle;
import es.ull.iis.simulation.core.SimulationTimeFunction;
import es.ull.iis.simulation.core.SimulationWeeklyPeriodicCycle;
import es.ull.iis.simulation.sequential.TimeDrivenActivity;
import es.ull.iis.simulation.sequential.TimeDrivenGenerator;
import es.ull.iis.simulation.core.TimeStamp;
import es.ull.iis.simulation.core.TimeUnit;
import es.ull.iis.simulation.sequential.flow.SingleFlow;
import es.ull.iis.simulation.sequential.WorkGroup;
import es.ull.iis.util.WeeklyPeriodicCycle;
import es.ull.iis.simulation.inforeceiver.StdInfoView;
import es.ull.iis.simulation.sequential.ElementType;
import es.ull.iis.simulation.sequential.Simulation;

/**
 * @author Iv�n Castilla
 *
 */
public class PortSimulation extends Simulation {
	final private static String DESCRIPTION = "Port Simulation";
	private static final int N_BERTHS = 1;
	private static final int[] N_QUAYS_PER_BERTH = {2};
	private static final int N_BLOCKS = 1;
	private static final int[] N_QUAYS_PER_BLOCK = {1};
	private static final int N_TRUCKS = 4;
	protected static final String QUAY_CRANE = "Quay Crane";
	protected static final String YARD_CRANE = "Yard Crane";
	private static final String TRUCK = "Truck";
	private static final String CONTAINER = "Container";
	private static final String ACT_CALL_TRUCK = "Call Truck";
	private static final String ACT_UNLOAD = "Unload";
	private static final String ACT_TO_YARD = "Lead to yard";
	private static final String ACT_PLACE = "Place container";
	private static final String ACT_SEA_TO_YARD = "Sea to yard";
	private static final String ID_VAR = "ID";
	private static final String BLOCK_ID_VAR = "BlockId";
	private static final String CONS_VAR = "ConstantVariate";
	private static final double[] TIME_TO_UNLOAD = {15.0};
	private static final double[] TIME_TO_YARD = {20.0};
	private static final double[] TIME_TO_PLACE = {10.0};
	private static final int[] CONTAINERS_PER_DAY = {10};
	private int resIdCounter = 0;
	private int rtIdCounter = 0; 
	private int actCounter = 0;
	
	/**
	 * @param id
	 * @param description
	 * @param unit
	 * @param startTs
	 * @param endTs
	 */
	public PortSimulation(int id, TimeUnit unit, TimeStamp startTs, TimeStamp endTs) {
		super(id, DESCRIPTION + " " + id, unit, startTs, endTs);
		// Creates the main element type representing containers
		final ElementType et = new ElementType(0, this, CONTAINER);
		// Creates the resource types and specific resources
		final ResourceType[] rtQuayCranes = new ResourceType[N_BERTHS];
		for (int i = 0; i < N_BERTHS; i++) {
			rtQuayCranes[i] = new QuayCraneResourceType(rtIdCounter++, this, i);
			for (int j = 0; j < N_QUAYS_PER_BERTH[i]; j++) {
				final Resource res = new Resource(resIdCounter++, this, QUAY_CRANE + " " + i + "." + j);
				res.addTimeTableEntry(SimulationPeriodicCycle.newWeeklyCycle(unit), endTs, rtQuayCranes[i]);
			}
		}
		final ResourceType rtTrucks = new ResourceType(rtIdCounter++, this, TRUCK);
		for (int i = 0; i < N_TRUCKS; i++) {
			final Resource res = new Resource(resIdCounter++, this, TRUCK + " " + i);
			res.addTimeTableEntry(SimulationPeriodicCycle.newWeeklyCycle(unit), endTs, rtTrucks);
		}
		
		final ResourceType[] rtYardCranes = new ResourceType[N_BLOCKS];
		for (int i = 0; i < N_BLOCKS; i++) {
			rtYardCranes[i] = new YardCraneResourceType(rtIdCounter++, this, i);
			for (int j = 0; j < N_QUAYS_PER_BLOCK[i]; j++) {
				final Resource res = new Resource(resIdCounter++, this, YARD_CRANE + " " + i + "." + j);
				res.addTimeTableEntry(SimulationPeriodicCycle.newWeeklyCycle(unit), endTs, rtYardCranes[i]);
			}
		}
		// Defines the needs of the activities in terms of resources
		final WorkGroup wgEmpty = new WorkGroup();
		final WorkGroup wgTruck = new WorkGroup(rtTrucks, 1);
		final WorkGroup []wgUnload = new WorkGroup[N_BERTHS];
		for (int i = 0; i < N_BERTHS; i++) {
			wgUnload[i] = new WorkGroup(rtQuayCranes[i], 1);
		}
		final WorkGroup []wgPlace = new WorkGroup[N_BLOCKS];
		for (int i = 0; i < N_BLOCKS; i++) {
			wgPlace[i] = new WorkGroup(rtYardCranes[i], 1);
		}
		
		// Activities
		final TimeDrivenActivity aCallTruck = new TimeDrivenActivity(actCounter++, this, ACT_CALL_TRUCK);
		aCallTruck.addWorkGroup()
		final TimeDrivenActivity aUnload = new TimeDrivenActivity(actCounter++, this, ACT_UNLOAD); 
		for (int i = 0; i < N_BERTHS; i++) {
			aUnload.addWorkGroup(new SimulationTimeFunction(unit, PortSimulation.CONS_VAR, TIME_TO_UNLOAD[i]), wgUnload[i], new ElementTypeCondition(et[i]));
		}
		final TimeDrivenActivity aToYard = new TimeDrivenActivity(actCounter++, this, ACT_TO_YARD);
		for (int i = 0; i < N_BLOCKS; i++) {
			aToYard.addWorkGroup(new SimulationTimeFunction(unit, PortSimulation.CONS_VAR, TIME_TO_YARD[i]), wgEmpty, 
					simFactory.getCustomizedConditionInstance("", "<%GET(@E." + BLOCK_ID_VAR + ")%> == " + i));
		}
		final TimeDrivenActivity aPlace = new TimeDrivenActivity(actCounter++, this, ACT_PLACE);
		for (int i = 0; i < N_BLOCKS; i++) {
			aPlace.addWorkGroup(new SimulationTimeFunction(unit, PortSimulation.CONS_VAR, TIME_TO_PLACE[i]), wgPlace[i], 
					simFactory.getCustomizedConditionInstance("", "<%GET(@E." + BLOCK_ID_VAR + ")%> == " + i));
		}
		// TODO: Exactly the same as aToYard... Unify?
		final TimeDrivenActivity aTruckReturn = new TimeDrivenActivity(actCounter++, this, ACT_RETURN);
		for (int i = 0; i < N_BLOCKS; i++) {
			aTruckReturn.addWorkGroup(new SimulationTimeFunction(unit, PortSimulation.CONS_VAR, TIME_TO_YARD[i]), wgEmpty, 
					simFactory.getCustomizedConditionInstance("", "<%GET(@E." + BLOCK_ID_VAR + ")%> == " + i));
		}

		// Defines the flow for the former activities
		final SingleFlow sfCallTruck = new SingleFlow(this, aCallTruck);
		final SingleFlow sfUnload = new SingleFlow(this, aUnload);
		final SingleFlow sfToYard = new SingleFlow(this, aToYard);
		final SingleFlow sfPlace = new SingleFlow(this, aPlace);		
		sfCallTruck.link(sfUnload);
		sfUnload.link(sfToYard);
		sfToYard.link(sfPlace);
		
		// defines the main activity that drives the whole process, which involves seizing a truck
		final FlowDrivenActivity mainAct = new FlowDrivenActivity(actCounter++, this, ACT_SEA_TO_YARD);
		mainAct.addWorkGroup(sfCallTruck, sfPlace, wgTruck);
		final SingleFlow sfMain = new SingleFlow(this, mainAct);
		
		// Generate orders for unloading containers
		final ArrivalPlanning planning = new ArrivalPlanning("testStowagePlan1");
		for (int i = 0; i < N_BERTHS; i++) {
			final ContainerCreator cc = new ContainerCreator(this, TimeFunctionFactory.getInstance(CONS_VAR, CONTAINERS_PER_DAY[i]), et, sfMain);
			final TimeDrivenGenerator gen = new TimeDrivenGenerator(this, cc, planning);
		}
		
		addInfoReceivers();
	}

	private void addInfoReceivers() {
		addInfoReceiver(new StdInfoView(this));
	}
	
}
