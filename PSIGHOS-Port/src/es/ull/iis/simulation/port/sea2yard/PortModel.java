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
import es.ull.iis.simulation.model.flow.Flow;
import es.ull.iis.simulation.model.flow.InitializerFlow;
import es.ull.iis.simulation.model.flow.ReleaseResourcesFlow;
import es.ull.iis.simulation.model.flow.RequestResourcesFlow;
import simkit.random.RandomNumber;
import simkit.random.RandomNumberFactory;

/**
 * A port model where quay cranes cannot operate when there is another crane OPERATING inside the safety distance
 * @author Iván Castilla
 *
 */
public class PortModel extends Simulation {
	protected final static TimeUnit PORT_TIME_UNIT = TimeUnit.SECOND;
	private final static String DESCRIPTION = "Port Simulation";
	protected static final long T_OPERATION = 1L * 60;
	protected static final long T_TRANSPORT = 3 * T_OPERATION;
	protected static final long T_MOVE = T_OPERATION;
	private static final long START_TS = 0;
	private static final long END_TS = 10000 * 60 * 60;
	protected static final String QUAY_CRANE = "Quay Crane";
	private static final String DELIVERY_VEHICLE = "Vehicle";
	protected static final String CONTAINER = "Container";
	protected static final String ACT_UNLOAD = "Unload";
	protected static final String ACT_PLACE = "Place at";
	protected static final String ACT_GET_TO_BAY = "Get to bay";
	protected static final String ACT_LEAVE_BAY = "Leave bay";
	private static final String POSITION = "Position";
	private static final String OPERATE = "Operate";
	private final ResourceType[] rtContainers;
	private final ResourceType rtGenericVehicle;
	private final ResourceType[] rtSpecificVehicle;
	private final UnloadActivity[] actUnloads;
	private final InitializerFlow[] cranePlan;
	private final WorkGroup[] wgPositions;
	private final WorkGroup[] wgOpPositionsSides;
	private final WorkGroup[] wgContainers;
	private final WorkGroup[] wgSpecificContainers;
	private final StowagePlan plan;
	private final int []nVehicles;
	private final double pError;
	private static final RandomNumber rng = RandomNumberFactory.getInstance();
	private final long currentSeed;

	/**
	 * Creates a port simulation with only generic delivery vehicles
	 * @param plan The stowage plan that defines the tasks to perform
	 * @param id The simulation identifier
	 * @param nGenericVehicles Number of delivery vehicles to be used
	 * @param pError Percentage error for each time parameter of the simulation. If 0.0, deterministic simulation 
	 */
	public PortModel(StowagePlan plan, int id, int nGenericVehicles, double pError) {
		this(plan, id, new int[] {nGenericVehicles}, pError, rng.getSeed());
	}
	
	/**
	 * Creates a port simulation with only generic delivery vehicles, initialized with the specified random seed
	 * @param plan The stowage plan that defines the tasks to perform
	 * @param id The simulation identifier
	 * @param nGenericVehicles Number of delivery vehicles to be used
	 * @param pError Percentage error for each time parameter of the simulation. If 0.0, deterministic simulation
	 * @param seed Random seed used to ensure that the random parameters of the simulation replicate those from a former simulation 
	 */
	public PortModel(StowagePlan plan, int id, int nGenericVehicles, double pError, long seed) {
		this(plan, id, new int[] {nGenericVehicles}, pError, seed);
	}
	
	/**
	 * Creates a probabilistic port simulation
	 * @param plan The stowage plan that defines the tasks to perform
	 * @param id The simulation identifier
	 * @param nVehicles Number of delivery vehicles to be used, distributed per crane. The last position of the array contains the
	 * number of generic delivery vehicles
	 * @param pError Percentage error for each time parameter of the simulation. If T is the constant duration of any activity 
	 * within the simulation, the effective time will be uniformly distributed in the interval (T-T*pError, T+T*pError)
	 */
	public PortModel(StowagePlan plan, int id, int[] nVehicles, double pError) {
		this(plan, id, nVehicles, pError, rng.getSeed());
	}
	
	/**
	 * Creates a port simulation
	 * @param plan The stowage plan that defines the tasks to perform
	 * @param id The simulation identifier
	 * @param nVehicles Number of delivery vehicles to be used, distributed per crane. The last position of the array contains the
	 * number of generic delivery vehicles
	 * @param pError Percentage error for each time parameter of the simulation. If T is the constant duration of any activity 
	 * within the simulation, the effective time will be uniformly distributed in the interval (T-T*pError, T+T*pError). 
	 * If pError == 0.0, creates a deterministic simulation
	 * @param seed Random seed used to ensure that the random parameters of the simulation replicate those from a former simulation 
	 */
	public PortModel(StowagePlan plan, int id, int[] nVehicles, double pError, long seed) {
		super(id, DESCRIPTION + " " + id, PORT_TIME_UNIT, START_TS, END_TS);
		currentSeed = seed;
		rng.setSeed(seed);
		this.nVehicles = nVehicles;
		this.pError = pError;
		this.plan = plan;
		final Vessel vessel = plan.getVessel();
		final int nBays = vessel.getNBays();
		final int nContainers = plan.getNContainers();
		final int nCranes = plan.getNCranes();
		final int safetyDistance = plan.getSafetyDistance();
		
		// Creates the "positions" of the cranes in front of the bays and the activities to move among bays 
		final ResourceType[] rtPositions = new ResourceType[nBays];
		final ResourceType[] rtOpPositions = new ResourceType[nBays];
		wgPositions = new WorkGroup[nBays];
		for (int bayId = 0; bayId < nBays; bayId++) {
			rtPositions[bayId] = new ResourceType(this, POSITION + bayId);
			rtPositions[bayId].addGenericResources(1);
			wgPositions[bayId] = new WorkGroup(this, rtPositions[bayId], 1);
			rtOpPositions[bayId] = new ResourceType(this, OPERATE + bayId);
			rtOpPositions[bayId].addGenericResources(1);
		}
		wgOpPositionsSides = new WorkGroup[nBays];
		for (int bayId = 0; bayId < nBays; bayId++) {
			final ResourceType rtSidePositions[] = new ResourceType[Math.min(bayId, safetyDistance) + Math.min(nBays - bayId - 1, safetyDistance)];
			int rtIndex = 0;
			for (int bayId2 = Math.max(bayId - safetyDistance, 0); bayId2 < bayId; bayId2++) {
				rtSidePositions[rtIndex++] = rtOpPositions[bayId2]; 
			}
			for (int bayId2 = bayId + 1; bayId2 < Math.min(nBays, bayId + safetyDistance + 1); bayId2++) {
				rtSidePositions[rtIndex++] = rtOpPositions[bayId2]; 
			}
			final int[] needed = new int[rtSidePositions.length];
			Arrays.fill(needed, 1);
			wgOpPositionsSides[bayId] = new WorkGroup(this, rtSidePositions, needed);
		}
		
		// Creates the rest of resources
		rtContainers = new ResourceType[nContainers];
		for (int containerId = 0; containerId < nContainers; containerId++) {
			rtContainers[containerId] = new ResourceType(this, CONTAINER + containerId);
		}
		if (hasGenericVehicles()) {
			rtGenericVehicle = new ResourceType(this, DELIVERY_VEHICLE);
			rtGenericVehicle.addGenericResources(getNGenericVehicles());
			wgContainers = new WorkGroup[nContainers];
			for (int containerId = 0; containerId < nContainers; containerId++) {
				wgContainers[containerId] = createUnloadWorkGroup(vessel.getContainerBay(containerId), safetyDistance, nBays, rtContainers[containerId], rtGenericVehicle, rtOpPositions);
			}
		}
		else {
			rtGenericVehicle = null;
			wgContainers = null;
		}
		if (hasSpecificVehicles()) {
			rtSpecificVehicle = new ResourceType[nCranes];
			wgSpecificContainers = new WorkGroup[nContainers];
			for (int craneId = 0; craneId < nCranes; craneId++) {
				rtSpecificVehicle[craneId] = new ResourceType(this, DELIVERY_VEHICLE + craneId);
				rtSpecificVehicle[craneId].addGenericResources(getNSpecificVehiclesXCrane(craneId));
				for (int containerId : plan.get(craneId)) {
					wgSpecificContainers[containerId] = createUnloadWorkGroup(vessel.getContainerBay(containerId), safetyDistance, nBays, rtContainers[containerId], rtSpecificVehicle[craneId], rtOpPositions);
				}
			}
		}
		else {
			rtSpecificVehicle = null;
			wgSpecificContainers = null;
		}
		
		// Set the containers which are available from the beginning and creates the activities
		actUnloads = new UnloadActivity[nContainers];
		for (int bayId = 0; bayId < nBays; bayId++) {
			final ArrayList<Integer> bay = vessel.getBay(bayId);
			if (!bay.isEmpty()) {
				// Creates the container on top of the bay
				int containerId1 = vessel.peek(bayId);
				final Resource resContainer = new Resource(this, CONTAINER + containerId1);
				resContainer.addTimeTableEntry(rtContainers[containerId1]);
				// Creates the activities for the top and intermediate containers. These activities create new containers.
				for (int i = bay.size() - 1; i > 0; i--) {
					containerId1 = bay.get(i);
					final int containerId2 = bay.get(i-1);
					// FIXME: Add crane identifier
					actUnloads[containerId1] = new UnloadActivity(this, containerId1, containerId2);
				}
				// Creates the activity corresponding to the bottom container
				containerId1 = bay.get(0);
				actUnloads[containerId1] = new UnloadActivity(this, containerId1);
			}
		}
		
		// Creates the main element type representing quay cranes
		final ElementType[] ets = new ElementType[nCranes]; 
		cranePlan = new InitializerFlow[nCranes];
		for (int craneId = 0; craneId < nCranes; craneId++) {
			ets[craneId] = new ElementType(this, QUAY_CRANE + craneId);
			cranePlan[craneId] = createFlowFromPlan(plan, vessel, craneId);
			new QuayCraneGenerator(this, ets[craneId], cranePlan[craneId], plan.getInitialPosition(craneId));
		}
	}
	
	private WorkGroup createUnloadWorkGroup(int containerBay, int safetyDistance, int nBays, ResourceType rtContainer, ResourceType rtVehicle, ResourceType[] rtOpPositions) {
		final ResourceType rtUnload[] = new ResourceType[3 + Math.min(containerBay, safetyDistance) + Math.min(nBays - containerBay - 1, safetyDistance)];
		rtUnload[0] = rtVehicle;
		rtUnload[1] = rtContainer;
		int rtIndex = 2;
		for (int bayId = Math.max(containerBay - safetyDistance, 0); bayId < Math.min(nBays, containerBay + safetyDistance + 1); bayId++) {
			rtUnload[rtIndex++] = rtOpPositions[bayId]; 
		}
		final int[] needed = new int[rtUnload.length];
		Arrays.fill(needed, 1);
		return new WorkGroup(this, rtUnload, needed);
	}
	/**
	 * Returns the flow of a crane that must arrive at a specific bay
	 * @param id Bay identifier
	 * @return A {@link RequestResourcesFlow} representing the flow of a crane that must arrive at a specific bay 
	 */
	private RequestResourcesFlow getGetToBayFlow(int id) {
		final RequestResourcesFlow reqBay = new RequestResourcesFlow(this, ACT_GET_TO_BAY + id, id+1);
		reqBay.addWorkGroup(0, wgPositions[id], getTimeWithError(T_MOVE));		
		return reqBay;
	}
	
	/**
	 * Returns the flow of a crane that must leave a specific bay
	 * @param id Bay identifier
	 * @return A {@link ReleaseResourcesFlow} representing the flow of a crane that must leave a specific bay
	 */
	private ReleaseResourcesFlow getLeaveBayFlow(int id) {
		final ReleaseResourcesFlow relBay = new ReleaseResourcesFlow(this, ACT_LEAVE_BAY + id, id+1);
		return relBay;
	}
	
	/**
	 * Returns the {@link ResourceType Resource Type} representing a specific container
	 * @param containerId Container identifier
	 * @return the {@link ResourceType Resource Type} representing a specific container
	 */
	public ResourceType getContainerResourceType(int containerId) {
		return rtContainers[containerId];
	}

	/**
	 * Returns the {@link WorkGroup} representing a specific container
	 * @param containerId Container identifier
	 * @return the {@link WorkGroup} representing a specific container
	 */
	public WorkGroup getContainerWorkGroup(int containerId) {
		return wgContainers[containerId];
	}

	/**
	 * Returns the {@link WorkGroup} representing a specific container
	 * @param containerId Container identifier
	 * @return the {@link WorkGroup} representing a specific container
	 */
	public WorkGroup getSpecificContainerWorkGroup(int containerId) {
		if (wgSpecificContainers != null)
			return wgSpecificContainers[containerId];
		return null;
	}

	/**
	 * @return the rtTrucks
	 */
	public ResourceType getGenericVehicleResourceType() {
		return rtGenericVehicle;
	}

	/**
	 * @return the rtTrucks
	 */
	public ResourceType getSpecificVehicleResourceType(int id) {
		return rtSpecificVehicle[id];
	}

	/**
	 * @return the wgOpPositionsSides
	 */
	public WorkGroup getWgOpPositionsSides(int bayId) {
		return wgOpPositionsSides[bayId];
	}

	/**
	 * Creates the work flow associated to a specific stowage plan for a specific crane
	 * @param plan The stowage plan
	 * @param vessel The vessel which is being operated 
	 * @param craneId Crane identifier
	 * @return The first step of the work flow that the specified crane must follow to fulfill its stowage plan.
	 */
	private InitializerFlow createFlowFromPlan(StowagePlan plan, Vessel vessel, int craneId) {
		int craneBay = plan.getInitialPosition(craneId);
		// First place the crane in the initial position, taking into account the safety distance
		final RequestResourcesFlow firstFlow =  new RequestResourcesFlow(this, ACT_PLACE + craneBay, craneBay+1);
		firstFlow.addWorkGroup(0, wgPositions[craneBay]);
		Flow flow = firstFlow;
		
		// Analyze the plan and move if needed
		final ArrayList<Integer> cranePlan = plan.get(craneId);
		for (int i = 0; i < cranePlan.size(); i++) {
			final int containerId = cranePlan.get(i);
			final int containerBay = vessel.getContainerBay(containerId);
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
	 * Returns the stowage plan being modelled
	 * @return the stowage plan being modelled
	 */
	public StowagePlan getPlan() {
		return plan;
	}

	/**
	 * @return the nTrucks
	 */
	public int getNGenericVehicles() {
		return nVehicles[nVehicles.length - 1];
	}

	public boolean hasSpecificVehicles() {
		return nVehicles.length > 1;
	}
	
	public boolean hasGenericVehicles() {
		return nVehicles[nVehicles.length - 1] > 0;
	}
	
	/**
	 * @return the nSpecificVehiclesXCrane
	 */
	public int getNSpecificVehiclesXCrane(int craneId) {
		return nVehicles[craneId];
	}

	/**
	 * Computes a time parameter by adding a uniform error. The error is specified as {@link #pError}
	 * @param constantTime
	 * @return
	 */
	public long getTimeWithError(long constantTime) {
		if (pError == 0) {
			return constantTime;
		}
		else {
			double rnd = (rng.draw() - 0.5) * 2.0;
			return (long)(constantTime * (1 + rnd * pError));
		}
	}
	
	public long getCurrentSeed() {
		return currentSeed;
	}
	
}
