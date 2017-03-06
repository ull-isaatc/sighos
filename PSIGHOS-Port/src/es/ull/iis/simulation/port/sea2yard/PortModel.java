/**
 * 
 */
package es.ull.iis.simulation.port.sea2yard;

import java.util.ArrayList;

import es.ull.iis.simulation.model.ElementType;
import es.ull.iis.simulation.model.FlowExecutor;
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
	private static final int N_TRUCKS = 1;
	private static final int[] N_CONTAINERS = new int[] {10, 16};
	private static final int N_CRANES = 2;
	protected static final String QUAY_CRANE = "Quay Crane";
	private static final String TRUCK = "Truck";
	protected static final String CONTAINER = "Container";
	private static final String ACT_UNLOAD = "Unload";
	private static final String ACT_GET_TO_BAY = "Get to bay";
	private static final String ACT_LEAVE_BAY = "Leave bay";
	private static final String POSITION = "Position";
	private final ResourceType[] rtContainers;
	private final ActivityFlow[] actUnloads;
	private final RequestResourcesFlow[] reqBays;
	private final ReleaseResourcesFlow[] relBays;
	private static final long T_UNLOAD = 3L;
	private static final long T_TRANSPORT = 10L;
	private static final long T_MOVE = 1L;
	private final Ship ship;
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
		
		ship = (config == 0) ? fillTestShip1() : fillTestShip2();
		final int nBays = ship.getNBays();
		
		// Creates the "positions" of the cranes in front of the bays and the activities to move among bays 
		final ResourceType[] rtPositions = new ResourceType[nBays];
		final WorkGroup[] wgPositions = new WorkGroup[nBays];
		reqBays = new RequestResourcesFlow[nBays];
		relBays = new ReleaseResourcesFlow[nBays];
		for (int i = 0; i < nBays; i++) {
			rtPositions[i] = new ResourceType(this, POSITION + i);
			rtPositions[i].addGenericResources(1);
			wgPositions[i] = new WorkGroup(this, rtPositions[i], 1);
			reqBays[i] = new RequestResourcesFlow(this, ACT_GET_TO_BAY + i, i+1);
			reqBays[i].addWorkGroup(0, wgPositions[i], T_MOVE);
			relBays[i] = new ReleaseResourcesFlow(this, ACT_LEAVE_BAY + i, i+1);
		}
		
		// Creates the rest of resources
		final ResourceType rtTrucks = new ResourceType(this, TRUCK);
		rtTrucks.addGenericResources(N_TRUCKS);
		rtContainers = new ResourceType[N_CONTAINERS[config]];
		final Resource[] resContainers = new Resource[N_CONTAINERS[config]];
		final WorkGroup[] wgContainers = new WorkGroup[N_CONTAINERS[config]];
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
					actUnloads[containerId1] = new ActivityFlow(this, ACT_UNLOAD + containerId1) {
						@Override
						public void afterFinalize(FlowExecutor fe) {
							resContainers[containerId2] = new Resource(model, PortModel.CONTAINER + containerId2);
							resContainers[containerId2].addTimeTableEntry(rtContainers[containerId2]);
							model.getSimulationEngine().addEvent(resContainers[containerId2].onCreate(model.getSimulationEngine().getTs()));
						}
					};
					actUnloads[containerId1].addWorkGroup(0, wgContainers[containerId1], T_UNLOAD);
					actUnloads[containerId1].addResourceCancellation(rtTrucks, T_TRANSPORT);
				}
				// Creates the activity corresponding to the bottom container
				containerId1 = bay.get(0);
				actUnloads[containerId1] = new ActivityFlow(this, ACT_UNLOAD + containerId1);
				actUnloads[containerId1].addWorkGroup(0, wgContainers[containerId1], T_UNLOAD);
				actUnloads[containerId1].addResourceCancellation(rtTrucks, T_TRANSPORT);
			}
		}
		
		// Sets the tasks that the cranes have to perform
		plan = (config == 0) ? fillTestPlan1() : fillTestPlan2();
//		for (int craneId = 0; craneId < N_CRANES; craneId++) {
//			final ArrayList<Integer> cranePlan = plan.get(craneId);
//			ActivityFlow lastAct = actUnloads[cranePlan.get(0)];
//			for (int i = 1; i < cranePlan.size(); i++) {
//				lastAct.link(actUnloads[cranePlan.get(i)]);
//				lastAct = actUnloads[cranePlan.get(i)];
//			}
//		}

		// Creates the main element type representing quay cranes
		final ElementType[] ets = new ElementType[N_CRANES]; 
		for (int craneId = 0; craneId < N_CRANES; craneId++) {
			ets[craneId] = new ElementType(this, QUAY_CRANE + craneId);
			new QuayCraneGenerator(this, ets[craneId], createFlowFromPlan(plan, ship, craneId), plan.getInitialPosition(craneId));
		}
	}
	
	private InitializerFlow createFlowFromPlan(StowagePlan plan, Ship ship, int craneId) {
		int craneBay = plan.getInitialPosition(craneId);
		// First place the crane in the initial position
		final InitializerFlow firstFlow = reqBays[craneBay];
		Flow flow = firstFlow;
		// Analyze the plan and move if needed
		final ArrayList<Integer> cranePlan = plan.get(craneId);
		for (int i = 0; i < cranePlan.size(); i++) {
			final int containerId = cranePlan.get(i);
			final int containerBay = ship.getContainerBay(containerId);
			while (craneBay < containerBay) {
				flow.link(reqBays[craneBay + 1]).link(relBays[craneBay]);
				flow = relBays[craneBay];
				craneBay++;
			}
			while (craneBay > containerBay) {
				flow.link(reqBays[craneBay - 1]).link(relBays[craneBay]);
				flow = relBays[craneBay];
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
	 * @return The ship with the specified load
	 */
	Ship fillTestShip1() {
		final Ship ship = new Ship(10);
		ship.push(1, 2);
		ship.push(1, 1);
		ship.push(1, 0);
		ship.push(3, 3);
		ship.push(4, 5);
		ship.push(4, 4);
		ship.push(6, 6);
		ship.push(7, 7);
		ship.push(8, 9);
		ship.push(8, 8);
		return ship;
	}
	
	Ship fillTestShip2() {
		final Ship ship = new Ship(16);
		ship.push(1, 0);
		ship.push(2, 1);
		ship.push(4, 3);
		ship.push(4, 2);
		ship.push(9, 5);
		ship.push(9, 4);
		ship.push(10, 7);
		ship.push(10, 6);
		ship.push(11, 9);
		ship.push(11, 8);
		ship.push(14, 11);
		ship.push(14, 10);
		ship.push(15, 14);
		ship.push(15, 13);
		ship.push(15, 12);
		return ship;
	}
	
	/**
	 * Creates a stowage plan for two cranes:
	 * - Crane 0 unloads 0, 1, 2, 5, 7
	 * - Crane 1 unloads 3, 4, 6, 8, 9
	 * @return A stowage plane for two cranes
	 */
	StowagePlan fillTestPlan1() {
		final StowagePlan plan = new StowagePlan(2);
		plan.addAll(0, new int[]{0, 1, 2, 5, 7});
		plan.addAll(1, new int[]{3, 4, 6, 8, 9});
		plan.setInitialPosition(0, 2);
		plan.setInitialPosition(1, 6);
		return plan;
	}

	StowagePlan fillTestPlan2() {
		final StowagePlan plan = new StowagePlan(2);
		plan.addAll(0, new int[]{0, 1, 2, 3, 7, 8, 9});
		plan.addAll(1, new int[]{4, 5, 6, 10, 11, 12, 13, 14});
		return plan;
	}

	/**
	 * @return the ship
	 */
	public Ship getShip() {
		return ship;
	}

	/**
	 * @return the plan
	 */
	public StowagePlan getPlan() {
		return plan;
	}
}
