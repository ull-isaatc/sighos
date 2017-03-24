/**
 * 
 */
package es.ull.iis.simulation.port;

import es.ull.iis.simulation.condition.Condition;
import es.ull.iis.simulation.model.ElementType;
import es.ull.iis.simulation.model.Simulation;
import es.ull.iis.simulation.model.Resource;
import es.ull.iis.simulation.model.ResourceType;
import es.ull.iis.simulation.model.TimeUnit;
import es.ull.iis.simulation.model.WorkGroup;
import es.ull.iis.simulation.model.flow.ActivityFlow;
import es.ull.iis.simulation.model.flow.DelayFlow;
import es.ull.iis.simulation.model.ElementInstance;
import es.ull.iis.simulation.model.flow.ReleaseResourcesFlow;
import es.ull.iis.simulation.model.flow.RequestResourcesFlow;

/**
 * @author Iván Castilla
 *
 */
public class PortModel extends Simulation {
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
	public static final String ACT_REQ_TRUCK = "Pick up truck";
	public static final String ACT_REL_TRUCK = "Free truck";
	private static final long[] TIME_TO_UNLOAD = {15};
	private static final long[][] TIME_FROM_BERTH_TO_BLOCK = {{20, 30, 40}};
	private static final long[] TIME_TO_PLACE = {10, 10, 10};
	private int qCraneIdCounter = 0; 
	private int yCraneIdCounter = 0; 
	
	/**
	 * @param id
	 * @param description
	 * @param unit
	 * @param startTs
	 * @param endTs
	 */
	public PortModel(int id, String description, TimeUnit unit, long startTs, long endTs) {
		super(id, description, unit, startTs, endTs);
		// Creates the main element type representing containers
		final ElementType et = new ElementType(this, CONTAINER);
		et.addElementVar("Berth", 0);
		et.addElementVar("Block", 0);
		// Creates the resource types and specific resources
		final ResourceType[] rtQuayCranes = new ResourceType[N_BERTHS];
		for (int i = 0; i < N_BERTHS; i++) {
			rtQuayCranes[i] = new QuayCraneResourceType(qCraneIdCounter++, this, i);
			for (int j = 0; j < N_QUAYS_PER_BERTH[i]; j++) {
				final Resource res = new Resource(this, QUAY_CRANE + " " + i + "." + j);
				res.addTimeTableEntry(rtQuayCranes[i]);
			}
		}
		final ResourceType rtTrucks = new ResourceType(this, TRUCK);
		for (int i = 0; i < N_TRUCKS; i++) {
			final Resource res = new Resource(this, TRUCK + " " + i);
			res.addTimeTableEntry(rtTrucks);
		}
		
		final ResourceType[] rtYardCranes = new ResourceType[N_BLOCKS];
		for (int i = 0; i < N_BLOCKS; i++) {
			rtYardCranes[i] = new YardCraneResourceType(yCraneIdCounter++, this, i);
			for (int j = 0; j < N_QUAYS_PER_BLOCK[i]; j++) {
				final Resource res = new Resource(this, YARD_CRANE + " " + i + "." + j);
				res.addTimeTableEntry(rtYardCranes[i]);
			}
		}
		// Defines the needs of the activities in terms of resources
		final WorkGroup wgEmpty = new WorkGroup(this);
		final WorkGroup wgTruck = new WorkGroup(this, rtTrucks, 1);
		final WorkGroup []wgUnload = new WorkGroup[N_BERTHS];
		for (int i = 0; i < N_BERTHS; i++) {
			wgUnload[i] = new WorkGroup(this, rtQuayCranes[i], 1);
		}
		final WorkGroup []wgPlace = new WorkGroup[N_BLOCKS];
		for (int i = 0; i < N_BLOCKS; i++) {
			wgPlace[i] = new WorkGroup(this, rtYardCranes[i], 1);
		}
		
		// Activities
		final ActivityFlow aUnload = new ActivityFlow(this, ACT_UNLOAD); 
		for (int i = 0; i < N_BERTHS; i++) {
			final int berthId = i;
			aUnload.addWorkGroup(0, wgUnload[i], new Condition() {
				@Override
				public boolean check(ElementInstance fe) {
					return (((Container)fe.getElement()).getBerth() == berthId);
				}
			}, TIME_TO_UNLOAD[i]);
		}
		final DelayFlow aToYard = new DelayFlow(this, ACT_TO_YARD, new DistanceTimeFunction(TIME_FROM_BERTH_TO_BLOCK));
		final ActivityFlow aPlace = new ActivityFlow(this, ACT_PLACE);
		for (int i = 0; i < N_BLOCKS; i++) {
			final int blockId = i;
			aPlace.addWorkGroup(0, wgPlace[i],
					new Condition() {
				@Override
				public boolean check(ElementInstance fe) {
					return (((Container)fe.getElement()).getBlock() == blockId);
				}
			}, TIME_TO_PLACE[i]);
		}
		final ActivityFlow aTruckReturn = new ActivityFlow(this, ACT_TRUCK_RETURN);
		aTruckReturn.addWorkGroup(0, wgEmpty, new DistanceTimeFunction(TIME_FROM_BERTH_TO_BLOCK));

		final RequestResourcesFlow reqTruck = new RequestResourcesFlow(this, ACT_REQ_TRUCK, 1);
		reqTruck.addWorkGroup(wgTruck);
		final ReleaseResourcesFlow relTruck = new ReleaseResourcesFlow(this, ACT_REL_TRUCK, 1);
		
		// Defines the flow for the former activities
		reqTruck.link(aUnload).link(aToYard).link(aPlace).link(aTruckReturn).link(relTruck);
		
		// Generate orders for unloading containers
		final ArrivalPlanning planning = new ArrivalPlanning(0, "C:\\Users\\Iván Castilla\\git\\sighos\\PSIGHOS-Port\\src\\es\\ull\\iis\\simulation\\port\\testStowagePlan1.txt");
		for (int i = 0; i < N_BERTHS; i++) {
			new ContainerCreator(this, planning, et, reqTruck);
		}
	}
	
}
