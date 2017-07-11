/**
 * 
 */
package es.ull.iis.simulation.port.sea2yard;

import java.util.ArrayList;
import java.util.Arrays;

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
 * A port model where quay cranes cannot operate when there is another crane inside the safety distance
 * @author Iv�n Castilla
 *
 */
public class PortModel2 extends Simulation {
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
	public PortModel2(StowagePlan plan, int id, String description, TimeUnit unit, long startTs, long endTs, int nTrucks) {
		super(id, description, unit, startTs, endTs);
		this.nTrucks = nTrucks;

		this.plan = plan;
		final Vessel ship = plan.getVessel();
		final int nBays = ship.getNBays();
		final int nContainers = plan.getNContainers();
		final int nCranes = plan.getNCranes();
		final int safetyDistance = plan.getSafetyDistance();
		
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
		for (int containerId = 0; containerId < nContainers; containerId++) {
			rtContainers[containerId] = new ResourceType(this, CONTAINER + containerId);
			final int containerBay = ship.getContainerBay(containerId);
			final ResourceType rtUnload[] = new ResourceType[2 + Math.min(containerBay, safetyDistance) + Math.min(nBays - containerBay - 1, safetyDistance)];
			rtUnload[0] = rtTrucks;
			rtUnload[1] = rtContainers[containerId];
			int rtIndex = 2;
			for (int bayId = Math.max(containerBay - safetyDistance, 0); bayId < containerBay; bayId++) {
				rtUnload[rtIndex++] = rtPositions[bayId]; 
			}
			for (int bayId = containerBay + 1; bayId < Math.min(nBays, containerBay + safetyDistance + 1); bayId++) {
				rtUnload[rtIndex++] = rtPositions[bayId]; 
			}
			final int[] needed = new int[rtUnload.length];
			Arrays.fill(needed, 1);
			wgContainers[containerId] = new WorkGroup(this, rtUnload, needed);
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

	private InitializerFlow createFlowFromPlan(StowagePlan plan, Vessel ship, int craneId) {
		int craneBay = plan.getInitialPosition(craneId);
		// First place the crane in the initial position, taking into account the safety distance
		final RequestResourcesFlow firstFlow =  new RequestResourcesFlow(this, ACT_PLACE + craneBay, craneBay+1);
		firstFlow.addWorkGroup(0, wgPositions[craneBay]);
		Flow flow = firstFlow;
		
		// Analyze the plan and move if needed
		final ArrayList<Integer> cranePlan = plan.getSchedule(craneId);
		for (int i = 0; i < cranePlan.size(); i++) {
			final int containerId = cranePlan.get(i);
			final int containerBay = ship.getContainerBay(containerId);
			// When moving to the right, release the (craneBay - safetyDistance)th container and acquire the (craneBay + safetyDistance + 1)th container  
			while (craneBay < containerBay) {
				final ReleaseResourcesFlow relBay = getLeaveBayFlow(craneBay);
				flow.link(getGetToBayFlow(craneBay + 1)).link(relBay);
				flow = relBay;
				craneBay++;
			}
			// When moving to the left, release the (craneBay + safetyDistance)th container and acquire the (craneBay - safetyDistance - 1)th container  
			while (craneBay > containerBay) {
				final ReleaseResourcesFlow relBay = getLeaveBayFlow(craneBay);
				flow.link(getGetToBayFlow(craneBay - 1)).link(relBay);
				flow = relBay;
				craneBay--;								
			}
			flow.link(actUnloads[containerId]);
			flow = actUnloads[containerId];
		}
		// Return to initial position (to right)
//		while (craneBay < plan.getInitialPosition(craneId)) {
//			final ReleaseResourcesFlow relBay = getLeaveBayFlow(craneBay);
//			flow.link(getGetToBayFlow(craneBay + 1)).link(relBay);
//			flow = relBay;
//			craneBay++;
//		}
		// Return to initial position (to left)
//		while (craneBay > plan.getInitialPosition(craneId)) {
//			final ReleaseResourcesFlow relBay = getLeaveBayFlow(craneBay);
//			flow.link(getGetToBayFlow(craneBay - 1)).link(relBay);
//			flow = relBay;
//			craneBay--;								
//		}
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
