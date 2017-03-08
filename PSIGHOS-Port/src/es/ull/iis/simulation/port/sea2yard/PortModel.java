/**
 * 
 */
package es.ull.iis.simulation.port.sea2yard;

import java.util.ArrayList;

import es.ull.iis.simulation.model.ElementType;
import es.ull.iis.simulation.model.Resource;
import es.ull.iis.simulation.model.ResourceType;
import es.ull.iis.simulation.model.Simulation;
import es.ull.iis.simulation.model.TimeUnit;
import es.ull.iis.simulation.model.WorkGroup;
import es.ull.iis.simulation.model.flow.ActivityFlow;
import es.ull.iis.simulation.model.flow.Flow;
import es.ull.iis.simulation.model.flow.InitializerFlow;
import es.ull.iis.simulation.model.flow.ReleaseResourcesFlow;
import es.ull.iis.simulation.model.flow.RequestResourcesFlow;

/**
 * @author Iván Castilla
 *
 */
public class PortModel extends Simulation {
	private static final int N_TRUCKS = 4;
	private static final int[] N_CONTAINERS = new int[] {10, 16};
	private static final int N_CRANES = 2;
	protected static final String QUAY_CRANE = "Quay Crane";
	private static final String TRUCK = "Truck";
	protected static final String CONTAINER = "Container";
	protected static final String ACT_UNLOAD = "Unload";
	protected static final String ACT_GET_TO_BAY = "Get to bay";
	protected static final String ACT_LEAVE_BAY = "Leave bay";
	private static final String POSITION = "Position";
	private final ResourceType[] rtContainers;
	private final ResourceType rtTrucks;
	private final ActivityFlow[] actUnloads;
	private final WorkGroup[] wgPositions;
	private final WorkGroup[] wgContainers;
	protected static final long T_TRANSPORT = 10L;
	private static final long T_MOVE = 1L;
	private final StowagePlan plan;
	
	/**
	 * @param id
	 * @param description
	 * @param unit
	 * @param startTs
	 * @param endTs
	 */
	public PortModel(int config, int id, String description, TimeUnit unit, long startTs, long endTs) {
		super(id, description, unit, startTs, endTs);
		
		// Sets the tasks that the cranes have to perform
		plan = (config == 0) ? fillTestPlan1() : fillTestPlan2();
		final Ship ship = plan.getShip();
		final int nBays = ship.getNBays();
		
		// Creates the "positions" of the cranes in front of the bays and the activities to move among bays 
		final ResourceType[] rtPositions = new ResourceType[nBays];
		wgPositions = new WorkGroup[nBays];
		for (int i = 0; i < nBays; i++) {
			rtPositions[i] = new ResourceType(this, POSITION + i);
			rtPositions[i].addGenericResources(1);
			wgPositions[i] = new WorkGroup(this, rtPositions[i], 1);
		}
		
		// Creates the rest of resources
		rtTrucks = new ResourceType(this, TRUCK);
		rtTrucks.addGenericResources(N_TRUCKS);
		rtContainers = new ResourceType[N_CONTAINERS[config]];
		final Resource[] resContainers = new Resource[N_CONTAINERS[config]];
		wgContainers = new WorkGroup[N_CONTAINERS[config]];
		for (int i = 0; i < N_CONTAINERS[config]; i++) {
			rtContainers[i] = new ResourceType(this, CONTAINER + i);
			wgContainers[i] = new WorkGroup(this, new ResourceType[] {rtTrucks, rtContainers[i]}, new int[] {1, 1});
		}
		
		// Set the containers which are available from the beginning and creates the activities
		actUnloads = new ActivityFlow[N_CONTAINERS[config]];
		for (int bayId = 0; bayId < nBays; bayId++) {
			final ArrayList<Integer> bay = ship.getBay(bayId);
			if (!bay.isEmpty()) {
				// Creates the container on top of the bay
				int containerId1 = ship.peek(bayId);
				resContainers[containerId1] = new Resource(this, CONTAINER + containerId1);
				resContainers[containerId1].addTimeTableEntry(rtContainers[containerId1]);
				// Creates the activities for the top and intermediate containers. These activities create new containers.
				for (int i = bay.size() - 1; i > 0; i--) {
					containerId1 = bay.get(i);
					final int containerId2 = bay.get(i-1);
					actUnloads[containerId1] = new ActivityUnload(this, containerId1, ship.getContainerProcessingTime(containerId1), containerId2);
				}
				// Creates the activity corresponding to the bottom container
				containerId1 = bay.get(0);
				actUnloads[containerId1] = new ActivityUnload(this, containerId1, ship.getContainerProcessingTime(containerId1));
			}
		}
		
		// Creates the main element type representing quay cranes
		final ElementType[] ets = new ElementType[N_CRANES]; 
		for (int craneId = 0; craneId < N_CRANES; craneId++) {
			ets[craneId] = new ElementType(this, QUAY_CRANE + craneId);
			new QuayCraneGenerator(this, ets[craneId], createFlowFromPlan(plan, ship, craneId), plan.getInitialPosition(craneId));
		}
	}
	
	private RequestResourcesFlow getGetToBayFlow(int id) {
		final RequestResourcesFlow reqBay = new RequestResourcesFlow(this, ACT_GET_TO_BAY + id, id+1);
		reqBay.addWorkGroup(0, wgPositions[id], T_MOVE);		
		return reqBay;
	}
	
	private ReleaseResourcesFlow getLeaveBayFlow(int id) {
		final ReleaseResourcesFlow relBay = new ReleaseResourcesFlow(this, ACT_LEAVE_BAY + id, id+1);
		return relBay;
	}
	
	/**
	 * @return the rtContainers
	 */
	public ResourceType getContainerResourceType(int containerId) {
		return rtContainers[containerId];
	}

	public WorkGroup getContainerWorkGroup(int containerId) {
		return wgContainers[containerId];
	}

	/**
	 * @return the rtTrucks
	 */
	public ResourceType getTruckResourceType() {
		return rtTrucks;
	}

	private InitializerFlow createFlowFromPlan(StowagePlan plan, Ship ship, int craneId) {
		int craneBay = plan.getInitialPosition(craneId);
		// First place the crane in the initial position
		final RequestResourcesFlow firstFlow =  new RequestResourcesFlow(this, ACT_GET_TO_BAY + craneBay, craneBay+1);
		firstFlow.addWorkGroup(0, wgPositions[craneBay]);
		Flow flow = firstFlow;
		// Analyze the plan and move if needed
		final ArrayList<Integer> cranePlan = plan.get(craneId);
		for (int i = 0; i < cranePlan.size(); i++) {
			final int containerId = cranePlan.get(i);
			final int containerBay = ship.getContainerBay(containerId);
			while (craneBay < containerBay) {
				final ReleaseResourcesFlow relBay = getLeaveBayFlow(craneBay);
				flow.link(getGetToBayFlow(craneBay + 1)).link(relBay);
				flow = relBay;
				craneBay++;
			}
			while (craneBay > containerBay) {
				final ReleaseResourcesFlow relBay = getLeaveBayFlow(craneBay);
				flow.link(getGetToBayFlow(craneBay - 1)).link(relBay);
				flow = relBay;
				craneBay--;								
			}
			flow.link(actUnloads[containerId]);
			flow = actUnloads[containerId];
		}
		return firstFlow;
	}
	
	/**
	 * Creates a ship with 10 bays
	 * 		0
	 * 		1			4				8
	 * 		2		3	5		6	7	9
	 * ---------------------------------------
	 * 	0	1	2	3	4	5	6	7	8	9
	 * Creates a stowage plan for two cranes:
	 * - Crane 0 unloads 0, 1, 2, 5, 7
	 * - Crane 1 unloads 3, 4, 6, 8, 9
	 * @return A stowage plane for two cranes
	 */
	StowagePlan fillTestPlan1() {
		final Ship ship = new Ship(10);
		ship.push(2, 1, 6);
		ship.push(1, 1, 18);
		ship.push(0, 1, 14);
		ship.push(3, 3, 10);
		ship.push(5, 4, 17);
		ship.push(4, 4, 21);
		ship.push(6, 6, 10);
		ship.push(7, 7, 9);
		ship.push(9, 8, 19);
		ship.push(8, 8, 7);
		final StowagePlan plan = new StowagePlan(ship, 2);
		plan.addAll(0, new int[]{0, 1, 2, 5, 7});
		plan.addAll(1, new int[]{3, 4, 6, 8, 9});
		plan.setInitialPosition(0, 2);
		plan.setInitialPosition(1, 6);
		return plan;
	}

	StowagePlan fillTestPlan2() {
		final Ship ship = new Ship(16);
		ship.push(0, 1, 33);
		ship.push(1, 2, 2);
		ship.push(3, 4, 59);
		ship.push(2, 4, 44);
		ship.push(5, 9, 6);
		ship.push(4, 9, 60);
		ship.push(7, 10, 52);
		ship.push(6, 10, 60);
		ship.push(9, 11, 41);
		ship.push(8, 11, 56);
		ship.push(11, 14, 22);
		ship.push(10, 14, 38);
		ship.push(14, 15, 34);
		ship.push(13, 15, 16);
		ship.push(12, 15, 54);
		final StowagePlan plan = new StowagePlan(ship, 2);
		plan.addAll(0, new int[]{0, 1, 2, 3, 7, 8, 9});
		plan.addAll(1, new int[]{4, 5, 6, 10, 11, 12, 13, 14});
		plan.setInitialPosition(0, 1);
		plan.setInitialPosition(1, 8);
		return plan;
	}

	/**
	 * @return the plan
	 */
	public StowagePlan getPlan() {
		return plan;
	}
}
