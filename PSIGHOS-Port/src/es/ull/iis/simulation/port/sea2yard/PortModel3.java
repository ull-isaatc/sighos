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
 * A port model where quay cranes cannot move/operate if there is another crane inside the safety distance
 * @author Iván Castilla
 *
 */
public class PortModel3 extends Simulation {
	protected static final String QUAY_CRANE = "Quay Crane";
	private static final String TRUCK = "Truck";
	protected static final String CONTAINER = "Container";
	protected static final String ACT_UNLOAD = "Unload";
	protected static final String ACT_PLACE = "Place at";
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
	private final int nTrucks;
	
	/**
	 * @param id
	 * @param description
	 * @param unit
	 * @param startTs
	 * @param endTs
	 */
	public PortModel3(StowagePlan plan, int id, String description, TimeUnit unit, long startTs, long endTs, int nTrucks) {
		super(id, description, unit, startTs, endTs);
		this.nTrucks = nTrucks;

		this.plan = plan;
		final Ship ship = plan.getShip();
		final int nBays = ship.getNBays();
		final int nContainers = plan.getNContainers();
		final int nCranes = plan.getNCranes();
		
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
		rtTrucks.addGenericResources(nTrucks);
		rtContainers = new ResourceType[nContainers];
		final Resource[] resContainers = new Resource[nContainers];
		wgContainers = new WorkGroup[nContainers];
		for (int i = 0; i < nContainers; i++) {
			rtContainers[i] = new ResourceType(this, CONTAINER + i);
			wgContainers[i] = new WorkGroup(this, new ResourceType[] {rtTrucks, rtContainers[i]}, new int[] {1, 1});
		}
		
		// Set the containers which are available from the beginning and creates the activities
		actUnloads = new ActivityFlow[nContainers];
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
		final ElementType[] ets = new ElementType[nCranes]; 
		for (int craneId = 0; craneId < nCranes; craneId++) {
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
		final int safetyDistance = plan.getSafetyDistance();
		// First place the crane in the initial position, taking into account the safety distance
		final RequestResourcesFlow firstFlow =  new RequestResourcesFlow(this, ACT_PLACE + Math.max(craneBay - safetyDistance, 0), Math.max(craneBay - safetyDistance, 0)+1);
		firstFlow.addWorkGroup(0, wgPositions[Math.max(craneBay - safetyDistance, 0)]);
		Flow flow = firstFlow;
		
		for (int i = Math.max(craneBay - safetyDistance, 0) + 1; i < Math.min(craneBay + safetyDistance + 1, ship.getNBays()); i++) {
			final RequestResourcesFlow nextFlow =  new RequestResourcesFlow(this, ACT_PLACE + i, i+1);
			nextFlow.addWorkGroup(0, wgPositions[i]);
			flow.link(nextFlow);
			flow = nextFlow;
		}
		
		// Analyze the plan and move if needed
		final ArrayList<Integer> cranePlan = plan.get(craneId);
		for (int i = 0; i < cranePlan.size(); i++) {
			final int containerId = cranePlan.get(i);
			final int containerBay = ship.getContainerBay(containerId);
			// When moving to the right, release the (craneBay - safetyDistance)th container and acquire the (craneBay + safetyDistance + 1)th container  
			while (craneBay < containerBay) {
				final ReleaseResourcesFlow relBay = getLeaveBayFlow(craneBay - safetyDistance);
				flow.link(getGetToBayFlow(craneBay + safetyDistance + 1)).link(relBay);
				flow = relBay;
				craneBay++;
			}
			// When moving to the left, release the (craneBay + safetyDistance)th container and acquire the (craneBay - safetyDistance - 1)th container  
			while (craneBay > containerBay) {
				final ReleaseResourcesFlow relBay = getLeaveBayFlow(craneBay + safetyDistance);
				flow.link(getGetToBayFlow(craneBay - safetyDistance - 1)).link(relBay);
				flow = relBay;
				craneBay--;								
			}
			flow.link(actUnloads[containerId]);
			flow = actUnloads[containerId];
		}
		return firstFlow;
	}
	
	/**
	 * @return the plan
	 */
	public StowagePlan getPlan() {
		return plan;
	}

	/**
	 * @return the nTrucks
	 */
	public int getNTrucks() {
		return nTrucks;
	}
}
