/**
 * 
 */
package es.ull.iis.simulation.port;

import es.ull.iis.simulation.condition.Condition;
import es.ull.iis.simulation.core.Element;
import es.ull.iis.simulation.core.SimulationTimeFunction;
import es.ull.iis.simulation.core.TimeStamp;
import es.ull.iis.simulation.core.TimeUnit;
import es.ull.iis.simulation.inforeceiver.StdInfoView;
import es.ull.iis.simulation.sequential.ElementType;
import es.ull.iis.simulation.sequential.FlowDrivenActivity;
import es.ull.iis.simulation.sequential.Resource;
import es.ull.iis.simulation.sequential.ResourceType;
import es.ull.iis.simulation.sequential.Simulation;
import es.ull.iis.simulation.sequential.TimeDrivenActivity;
import es.ull.iis.simulation.sequential.TimeDrivenGenerator;
import es.ull.iis.simulation.sequential.WorkGroup;
import es.ull.iis.simulation.sequential.flow.SingleFlow;

/**
 * @author Iván Castilla
 *
 */
public class PortSimulation extends Simulation {
	final private static String DESCRIPTION = "Port Simulation";
	private static final int N_BERTHS = 1;
	private static final int[] N_QUAYS_PER_BERTH = {2};
	private static final int N_BLOCKS = 3;
	private static final int[] N_QUAYS_PER_BLOCK = {1, 1, 1};
	private static final int N_TRUCKS = 4;
	protected static final String QUAY_CRANE = "Quay Crane";
	protected static final String YARD_CRANE = "Yard Crane";
	private static final String TRUCK = "Truck";
	private static final String CONTAINER = "Container";
	private static final String ACT_TRUCK_RETURN = "Truck returning";
	private static final String ACT_UNLOAD = "Unload";
	private static final String ACT_TO_YARD = "Lead to yard";
	private static final String ACT_PLACE = "Place container";
	private static final String ACT_SEA_TO_YARD = "Sea to yard";
	private static final String CONS_VAR = "ConstantVariate";
	private static final double[] TIME_TO_UNLOAD = {15.0};
	private static final long[][] TIME_FROM_BERTH_TO_BLOCK = {{20, 30, 40}};
	private static final double[] TIME_TO_PLACE = {10.0, 10.0, 10.0};
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
				res.addTimeTableEntry(rtQuayCranes[i]);
			}
		}
		final ResourceType rtTrucks = new ResourceType(rtIdCounter++, this, TRUCK);
		for (int i = 0; i < N_TRUCKS; i++) {
			final Resource res = new Resource(resIdCounter++, this, TRUCK + " " + i);
			res.addTimeTableEntry(rtTrucks);
		}
		
		final ResourceType[] rtYardCranes = new ResourceType[N_BLOCKS];
		for (int i = 0; i < N_BLOCKS; i++) {
			rtYardCranes[i] = new YardCraneResourceType(rtIdCounter++, this, i);
			for (int j = 0; j < N_QUAYS_PER_BLOCK[i]; j++) {
				final Resource res = new Resource(resIdCounter++, this, YARD_CRANE + " " + i + "." + j);
				res.addTimeTableEntry(rtYardCranes[i]);
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
		final TimeDrivenActivity aUnload = new TimeDrivenActivity(actCounter++, this, ACT_UNLOAD); 
		for (int i = 0; i < N_BERTHS; i++) {
			final int berthId = i;
			aUnload.addWorkGroup(new SimulationTimeFunction(unit, PortSimulation.CONS_VAR, TIME_TO_UNLOAD[i]), wgUnload[i], new Condition() {
				@Override
				public boolean check(Element e) {
					return (((Container)e).getBerth() == berthId);
				}
			});
		}
		final TimeDrivenActivity aToYard = new TimeDrivenActivity(actCounter++, this, ACT_TO_YARD);
		aToYard.addWorkGroup(new DistanceTimeFunction(TIME_FROM_BERTH_TO_BLOCK), wgEmpty);
		final TimeDrivenActivity aPlace = new TimeDrivenActivity(actCounter++, this, ACT_PLACE);
		for (int i = 0; i < N_BLOCKS; i++) {
			final int blockId = i;
			aPlace.addWorkGroup(new SimulationTimeFunction(unit, PortSimulation.CONS_VAR, TIME_TO_PLACE[i]), wgPlace[i],
					new Condition() {
				@Override
				public boolean check(Element e) {
					return (((Container)e).getBlock() == blockId);
				}
			});
		}
		final TimeDrivenActivity aTruckReturn = new TimeDrivenActivity(actCounter++, this, ACT_TRUCK_RETURN);
		aTruckReturn.addWorkGroup(new DistanceTimeFunction(TIME_FROM_BERTH_TO_BLOCK), wgEmpty);

		// Defines the flow for the former activities
		final SingleFlow sfUnload = new SingleFlow(this, aUnload);
		final SingleFlow sfToYard = new SingleFlow(this, aToYard);
		final SingleFlow sfPlace = new SingleFlow(this, aPlace);		
		final SingleFlow sfTruckReturn = new SingleFlow(this, aTruckReturn);
		sfUnload.link(sfToYard);
		sfToYard.link(sfPlace);
		sfPlace.link(sfTruckReturn);
		
		// defines the main activity that drives the whole process, which involves seizing a truck
		final FlowDrivenActivity mainAct = new FlowDrivenActivity(actCounter++, this, ACT_SEA_TO_YARD);
		mainAct.addWorkGroup(sfUnload, sfTruckReturn, wgTruck);
		final SingleFlow sfMain = new SingleFlow(this, mainAct);
		
		// Generate orders for unloading containers
		final ArrivalPlanning planning = new ArrivalPlanning(0, "C:\\Users\\Iván Castilla\\git\\sighos\\PSIGHOS-Port\\src\\es\\ull\\iis\\simulation\\port\\testStowagePlan1.txt");
		for (int i = 0; i < N_BERTHS; i++) {
			final ContainerCreator cc = new ContainerCreator(this, planning, et, sfMain);
			new TimeDrivenGenerator(this, cc, planning);
		}
		
		addInfoReceivers();
	}

	private void addInfoReceivers() {
		addInfoReceiver(new StdInfoView(this));
	}
	
}
