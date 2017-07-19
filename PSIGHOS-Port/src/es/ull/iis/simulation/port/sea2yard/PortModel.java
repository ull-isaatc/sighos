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
import es.ull.iis.simulation.model.flow.DelayFlow;
import es.ull.iis.simulation.model.flow.Flow;
import es.ull.iis.simulation.model.flow.InitializerFlow;
import es.ull.iis.simulation.model.flow.ReleaseResourcesFlow;
import es.ull.iis.simulation.model.flow.RequestResourcesFlow;
import es.ull.iis.simulation.model.flow.TaskFlow;
import simkit.random.RandomNumber;
import simkit.random.RandomNumberFactory;

/**
 * A port model for sea to yard operations. The sea-side is divided into bays. There are N quay cranes (qc), each one starting at a specific bay.
 * QCs can move from one bay to another, by always keeping certain safety distance among them to avoid interferences. 
 * QCs has an ordered list of tasks associated (the "schedule"). Each task represent either a container load or unload (aka. transshipment operation).
 * Each task requires a delivery vehicle to be performed. Every time a vehicle is used, it has to go  
 * 
 *  When the first task of a crane is It is possible for quay cranes to start a task that creates a conflict with another crane, because the task is . To avoid this situation, each pair of quay cranes share a "security token". 
 *  schedule is left to right, the rightmost crane acquires this token at start. If there is no initial conflict, the crane releases the token immediately. 
 *  Otherwise, it keeps the token until the conflicting task finishes. The leftmost crane moves to avoid the conflict (according to a predefined fictitious task) and
 *  then tries to acquire the token. Increasing higher priority is assigned to each crane, so to ensure that, even when the leftmost crane do not have to move, 
 *  the rightmost crane acquires the token first. 
 *
 * When a QC finishes its last task, it moves "out" of the bays to avoid conflicting with the rest of cranes. 
 * @author Iván Castilla
 */
public class PortModel extends Simulation {
	protected final static TimeUnit PORT_TIME_UNIT = TimeUnit.SECOND;
	private final static String DESCRIPTION = "Port Simulation";
	protected static final long T_OPERATION = 1L * 60;
	protected static final long T_TRANSPORT = 3 * T_OPERATION;
	protected static final long T_MOVE = T_OPERATION;
	private static final long START_TS = 0;
	private static final long END_TS = 10000 * 60 * 60;
	protected static final String OPERATE = "Operate";
	protected static final String QUAY_CRANE = "Quay Crane";
	protected static final String DELIVERY_VEHICLE = "Vehicle";
	protected static final String CONTAINER = "Container";
	protected static final String ACT_UNLOAD = "Unload";
	protected static final String ACT_PLACE = "Place at";
	protected static final String ACT_GET_TO_BAY = "Get to bay";
	protected static final String ACT_LEAVE_BAY = "Leave bay";
	protected static final String ACT_MOVING = "Moving";
	protected static final String END = "End";
	protected static final String POSITION = "Position";
	protected static final String SECURITY_TOKEN = "Security token";
	protected static final String FIRST_HALF = "(First half)";
	protected static final String SECOND_HALF = "(Second half)";
	private static final RandomNumber rng = RandomNumberFactory.getInstance();
	private final double pError;
	private final long currentSeed;
	private final StowagePlan plan;
	private final int []nVehicles;
	private final ResourceType rtGenericVehicle;
	private final ResourceType[] rtSpecificVehicle;
	private final WorkGroup[][] wgPositions;
	private final WorkGroup[] wgInitPositions;
	private final ResourceType[] rtContainers;
	private final WorkGroup[] wgContainers;
	private final WorkGroup[] wgSpecificContainers;
	private final WorkGroup[] wgSecurityToken;
	private final Flow[][] cranePlan;
	private final TaskFlow[] actUnloads;
	private final int extraBays;

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
	 * Creates a port simulation
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
		final int nContainers = plan.getNTasks();
		final int nCranes = plan.getNCranes();
		extraBays = (1 + plan.getSafetyDistance());
		
		wgPositions = new WorkGroup[nBays + extraBays * 2][2];
		wgInitPositions = new WorkGroup[nBays];
		rtContainers = new ResourceType[nContainers];
		actUnloads = new FullSafeUnloadActivity[nContainers];
		wgSecurityToken = new WorkGroup[nCranes];
		if (hasGenericVehicles()) {
			rtGenericVehicle = new ResourceType(this, DELIVERY_VEHICLE);
			wgContainers = new WorkGroup[nContainers];
		}
		else {
			rtGenericVehicle = null;
			wgContainers = null;
		}
		if (hasSpecificVehicles()) {
			rtSpecificVehicle = new ResourceType[nCranes];
			wgSpecificContainers = new WorkGroup[nContainers];
		}
		else {
			rtSpecificVehicle = null;
			wgSpecificContainers = null;
		}		
		
		createResources(nBays, nCranes, nContainers);
		
		// Set the containers which are available from the beginning and creates the activities
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
					actUnloads[containerId1] = new FullSafeUnloadActivity(this, containerId1, bay.get(i-1));
				}
				// Creates the activity corresponding to the bottom container
				containerId1 = bay.get(0);
				actUnloads[containerId1] = new FullSafeUnloadActivity(this, containerId1);
			}
		}
		
		// Creates the main element type representing quay cranes
		final ElementType[] ets = new ElementType[nCranes]; 
		cranePlan = createFlowFromPlan();
		for (int craneId = 0; craneId < nCranes; craneId++) {
			ets[craneId] = new ElementType(this, QUAY_CRANE + craneId);
			new QuayCraneGenerator(this, ets[craneId], (InitializerFlow)cranePlan[craneId][0], plan.getInitialPosition(craneId));
		}
	}
	
	/**
	 * 
	 * @param vessel
	 * @param nBays
	 * @param nCranes
	 * @param nContainers
	 */
	private void createResources(int nBays, int nCranes, int nContainers) {
		final int safetyDistance = plan.getSafetyDistance();
		
		// Creates the "positions" of the cranes in front of the bays and the activities to move among bays 
		final ResourceType[] rtPositions = new ResourceType[nBays + extraBays * 2];
		
		for (int craneId = 0; craneId < nCranes; craneId++) {
			final ResourceType rtSecurityToken = new ResourceType(this, SECURITY_TOKEN + craneId);
			rtSecurityToken.addGenericResources(1);
			wgSecurityToken[craneId] = new WorkGroup(this, rtSecurityToken, 1);
		}
		for (int bayId = 0; bayId < nBays; bayId++) {
			rtPositions[bayId + extraBays] = new ResourceType(this, POSITION + bayId);
			rtPositions[bayId + extraBays].addGenericResources(2);
		}
		for (int bayId = 0; bayId < extraBays; bayId++) {
			rtPositions[bayId] = new ResourceType(this, POSITION + (-bayId -1));
			rtPositions[bayId].addGenericResources(2);
			rtPositions[nBays + extraBays + bayId] = new ResourceType(this, POSITION + (nBays + bayId));
			rtPositions[nBays + extraBays + bayId].addGenericResources(2);
		}
		// Creates  the workgroups to take the initial positions of the cranes
		for (int bayId = 0; bayId < nBays; bayId++) {
			final ResourceType[] rtInitPlace = new ResourceType[1 + (bayId < safetyDistance ? bayId : safetyDistance) + (bayId + safetyDistance >= nBays ? nBays - bayId - 1 : safetyDistance)];
			int i = 0;
			for (int bayLeft = Math.max(0, bayId - safetyDistance); bayLeft < bayId; bayLeft++)
				rtInitPlace[i++] = rtPositions[bayLeft + extraBays];
			for (int bayRight = bayId + 1; bayRight <= Math.min(nBays - 1, bayId + safetyDistance); bayRight++)
				rtInitPlace[i++] = rtPositions[bayRight + extraBays];
			rtInitPlace[rtInitPlace.length - 1] = rtPositions[bayId + extraBays];
			final int[] needed = new int[rtInitPlace.length];
			Arrays.fill(needed, 1);
			needed[needed.length - 1] = 2;
			wgInitPositions[bayId] = new WorkGroup(this, rtInitPlace, needed);
		}
		// Creates the workgroups to deal with movements
		for (int bayId = 0; bayId < nBays + 2 * extraBays; bayId++) {
			if (bayId < safetyDistance) {
				wgPositions[bayId][0] = new WorkGroup(this, rtPositions[bayId], 1);
			}
			else {
				wgPositions[bayId][0] = new WorkGroup(this, new ResourceType[] {rtPositions[bayId], rtPositions[bayId - safetyDistance]}, new int[] {1, 1});
			}
			if (bayId + safetyDistance >= (nBays + extraBays * 2)) {
				wgPositions[bayId][1] = new WorkGroup(this, rtPositions[bayId], 1);				
			}
			else {
				wgPositions[bayId][1] = new WorkGroup(this, new ResourceType[] {rtPositions[bayId], rtPositions[bayId + safetyDistance]}, new int[] {1, 1});
			}
		}

		for (int containerId = 0; containerId < nContainers; containerId++) {
			rtContainers[containerId] = new ResourceType(this, CONTAINER + containerId);
		}
		if (hasGenericVehicles()) {
			rtGenericVehicle.addGenericResources(getNGenericVehicles());
			for (int containerId = 0; containerId < nContainers; containerId++) {
				wgContainers[containerId] = new WorkGroup(this, new ResourceType[] {rtGenericVehicle, rtContainers[containerId]}, new int[] {1, 1});
			}
		}
		if (hasSpecificVehicles()) {
			for (int craneId = 0; craneId < nCranes; craneId++) {
				rtSpecificVehicle[craneId] = new ResourceType(this, DELIVERY_VEHICLE + craneId);
				rtSpecificVehicle[craneId].addGenericResources(getNSpecificVehiclesXCrane(craneId));
				for (int containerId = 0; containerId < nContainers; containerId++) {
					wgSpecificContainers[containerId] = new WorkGroup(this, new ResourceType[] {rtSpecificVehicle[craneId], rtContainers[containerId]}, new int[] {1, 1});
				}
			}
		}
	}
	
	/**
	 * Returns the stowage plan being modelled
	 * @return the stowage plan being modelled
	 */
	public StowagePlan getPlan() {
		return plan;
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

	/**
	 * 
	 * @param craneId
	 * @param craneBay
	 * @param nCranes
	 * @param needToken
	 * @return
	 */
	private Flow[] initCrane(int craneId, int craneBay, int nCranes, boolean needToken) {
		final Flow[] initEndFlow = new Flow[2];
		// Acquires the "actual" position of the crane, first the left, then the right
		RequestResourcesFlow reqFlow = new RequestResourcesFlow(this, ACT_PLACE + craneBay);
		reqFlow.addWorkGroup(wgInitPositions[craneBay]);
		initEndFlow[1] = reqFlow;
		
		if (needToken) {
			// Starts by acquiring the security token
			reqFlow = new RequestResourcesFlow(this, "Req " + SECURITY_TOKEN, nCranes - craneId, false);
			reqFlow.addWorkGroup(wgSecurityToken[craneId]);
			reqFlow.link(initEndFlow[1]);
		}
		initEndFlow[0] = reqFlow;
		return initEndFlow;		
	}

	/**
	 * 
	 * @param leftToRight
	 * @param nCranes
	 * @return
	 */
	private Flow[][] moveCranesToInitPosition(boolean leftToRight, int nCranes) {
		// Saves first and last flow for each crane
		final Flow[][] flows = new Flow[nCranes][2];
		for (int craneId = 0; craneId < nCranes; craneId++) {
			// First place the crane in the initial position
			flows[craneId] = initCrane(craneId, plan.getInitialPosition(craneId), nCranes, plan.createsConflictAtStart(craneId));
		}
		// Check conflicts 
		if (leftToRight) {
			for (int craneId = 0; craneId < nCranes - 1; craneId++) {
				// Checks the dependencies with the next crane
				final int[] dep = plan.getDependenciesAtStart(craneId);
				if (dep[1] != -1) {
					// Creates the route to the fictitious task
					flows[craneId][1] = moveCrane(flows[craneId][1], plan.getInitialPosition(craneId), dep[0]);
					// Once in the fictitious task, try to acquire the corresponding "security token", applying different priority depending on the direction of the schedule
					final RequestResourcesFlow reqFlow = new RequestResourcesFlow(this, "Req " + SECURITY_TOKEN + (craneId + 1), nCranes - craneId, false);
					reqFlow.addWorkGroup(wgSecurityToken[craneId + 1]);
					flows[craneId][1] = flows[craneId][1].link(reqFlow);
				}
			}
		}
		else {
			for (int craneId = 1; craneId < nCranes; craneId++) {
				// Checks the dependencies with the next crane
				final int[] dep = plan.getDependenciesAtStart(craneId);
				if (dep[1] != -1) {
					// Creates the route to the fictitious task
					flows[craneId][1] = moveCrane(flows[craneId][1], plan.getInitialPosition(craneId), dep[0]);
					// Once in the fictitious task, try to acquire the corresponding "security token", applying different priority depending on the direction of the schedule
					final RequestResourcesFlow reqFlow = new RequestResourcesFlow(this, "Req " + SECURITY_TOKEN + (craneId - 1), craneId, false);
					reqFlow.addWorkGroup(wgSecurityToken[craneId - 1]);
					flows[craneId][1] = flows[craneId][1].link(reqFlow);
				}
			}
		}
		return flows;		
	}
	
	/**
	 * Creates the flows required to represent a crane moving from one bay to other.
	 * @param previousFlow A non-null flow representing the last step of the crane
	 * @param originBay The current position of the crane
	 * @param destinationBay The destination of the crane
	 * @return The last flow created
	 */
	private Flow moveCrane(Flow previousFlow, int originBay, int destinationBay) {
		Flow flow = previousFlow;
		int currentBay = originBay;
		// When moving to the right, release the (craneBay - safetyDistance)th container and acquire the (craneBay + safetyDistance + 1)th container  
		while (currentBay < destinationBay) {
			final ReleaseResourcesFlow relBay = new ReleaseResourcesFlow(this, ACT_LEAVE_BAY + currentBay, wgPositions[currentBay + extraBays][0]);
			final RequestResourcesFlow reqBay =  new RequestResourcesFlow(this, ACT_GET_TO_BAY + (currentBay + 1));
			reqBay.addWorkGroup(0, wgPositions[currentBay + 1 + extraBays][1]);	
			final DelayFlow delayBay = new DelayFlow(this, ACT_MOVING, getTimeWithError(T_MOVE));
			flow = flow.link(reqBay).link(relBay).link(delayBay);
			currentBay++;
		}
		// When moving to the left, release the (craneBay + safetyDistance)th container and acquire the (craneBay - safetyDistance - 1)th container  
		while (currentBay > destinationBay) {
			final ReleaseResourcesFlow relBay = new ReleaseResourcesFlow(this, ACT_LEAVE_BAY + currentBay, wgPositions[currentBay + extraBays][1]);
			final RequestResourcesFlow reqBay =  new RequestResourcesFlow(this, ACT_GET_TO_BAY + (currentBay - 1));
			reqBay.addWorkGroup(0, wgPositions[currentBay - 1 + extraBays][0]);	
			final DelayFlow delayBay = new DelayFlow(this, ACT_MOVING, getTimeWithError(T_MOVE));
			flow = flow.link(reqBay).link(relBay).link(delayBay);
			currentBay--;
		}
		return flow;
	}
	/**
	 * Creates the work flow associated to a specific stowage plan for a specific crane
	 * @param plan The stowage plan
	 * @param vessel The vessel which is being operated 
	 * @param craneId Crane identifier
	 * @return The first and last steps of the work flow that the specified crane must follow to fulfill its stowage plan.
	 */
	private Flow[][] createFlowFromPlan() {
		final boolean leftToRight = plan.isLeftToRight();
		final int nBays = plan.getVessel().getNBays();
		final int safetyDistance = plan.getSafetyDistance();
		final int nCranes = plan.getNCranes();
		final Flow[][] flows = moveCranesToInitPosition(leftToRight, nCranes);
		// Now reproduces the schedule
		for (int craneId = 0; craneId < nCranes; craneId++) {
			// ... but first takes care of possible conflicts
			int freeConflictOn = -1;
			if (leftToRight && craneId > 0) {
				freeConflictOn = plan.getDependenciesAtStart(craneId - 1)[1];
			}
			else if (!leftToRight && craneId < nCranes - 1) {
				freeConflictOn = plan.getDependenciesAtStart(craneId + 1)[1];
			}
			int craneBay = (plan.getDependenciesAtStart(craneId)[1] == -1) ? plan.getInitialPosition(craneId) : plan.getDependenciesAtStart(craneId)[0];
			Flow flow = flows[craneId][1];
			
			// Analyze the plan and move if needed
			final ArrayList<Integer> cranePlan = plan.getSchedule(craneId);
			for (int i = 0; i < cranePlan.size(); i++) {
				final int containerId = cranePlan.get(i);
				final int destinationBay = plan.getVessel().getContainerBay(containerId);
				flow = moveCrane(flow, craneBay, destinationBay);
				flow.link(actUnloads[containerId]);
				// Checks whether this was the task creating a conflict
				if (containerId == freeConflictOn) {
					final ReleaseResourcesFlow relFlow = new ReleaseResourcesFlow(this, "Rel" + SECURITY_TOKEN + craneId, wgSecurityToken[craneId]);
					actUnloads[containerId].link(relFlow);
					flow = relFlow;
				}
				else {
					flow = actUnloads[containerId];
				}
				craneBay = destinationBay;
			}
			// When finish, move to try not to disturb and releases all resources
			if (leftToRight) {
				flow = moveCrane(flow, craneBay, nBays - 1 - (nCranes - craneId - 1) * (safetyDistance + 1));
			}
			else {
				flow = moveCrane(flow, craneBay, craneId * (safetyDistance + 1));
			}
			flows[craneId][1] = flow.link(new ReleaseResourcesFlow(this, END));
		}
		
		return flows;
	}
	
}
