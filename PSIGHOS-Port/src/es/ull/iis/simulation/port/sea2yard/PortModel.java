/**
 * 
 */
package es.ull.iis.simulation.port.sea2yard;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.TreeMap;
import java.util.TreeSet;

import java.util.Map.Entry;

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

/**
 * A port model for sea to yard operations. The sea-side is divided into bays. There are N quay cranes (qc), each one starting at a specific bay.
 * QCs can move from one bay to another, by always keeping certain safety distance among them to avoid interferences. 
 * QCs has an ordered list of tasks associated (the "schedule"). Each task represent either a container load or unload (aka. transshipment operation).
 * Each task requires a delivery vehicle to be performed. Every time a vehicle is used, it has to go  
 * 
 *  It is possible for quay cranes to start a task that creates a conflict with another crane. To avoid this situation, each pair of quay cranes share a "security token". 
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
	protected static final String END_WORK = "End work";
	protected static final String POSITION = "Position";
	protected static final String SECURITY_TOKEN = "Security token";
	protected static final String FIRST_HALF = "(First half)";
	protected static final String SECOND_HALF = "(Second half)";
	private final StowagePlan plan;
	private final int []nVehicles;
	private final ResourceType rtGenericVehicle;
	private final ResourceType[] rtSpecificVehicle;
	private final WorkGroup[][] wgPositions;
	private final WorkGroup[] wgInitPositions;
	private final ResourceType[] rtContainers;
	private final WorkGroup[] wgContainers;
	private final WorkGroup[] wgSpecificContainers;
	/**
	 * Security tokens are used to control active waiting of cranes, when a crane has to wait in a bay until another has finished a task.
	 *  A crane acquires its security token when it starts. 
	 */
	private final ArrayList<TreeMap<Integer, WorkGroup>> wgSecurityToken;
	private final Flow[][] cranePlan;
	private final TaskFlow[] actUnloads;
	private final int extraBays;
	private final TimeRepository times;
	private final int[] waitIndex;
	
	/**
	 * Creates a port simulation with only generic delivery vehicles, initialized with the specified random seed
	 * @param plan The stowage plan that defines the tasks to perform
	 * @param id The simulation identifier
	 * @param nGenericVehicles Number of delivery vehicles to be used
	 */
	public PortModel(StowagePlan plan, int id, int nGenericVehicles, TimeRepository times) {
		this(plan, id, new int[] {nGenericVehicles}, times);
	}
	
	/**
	 * Creates a port simulation
	 * @param plan The stowage plan that defines the tasks to perform
	 * @param id The simulation identifier
	 * @param nVehicles Number of delivery vehicles to be used, distributed per crane. The last position of the array contains the
	 * number of generic delivery vehicles
	 */
	public PortModel(StowagePlan plan, int id, int[] nVehicles, TimeRepository times) {
		super(id, DESCRIPTION + " " + id, PORT_TIME_UNIT, START_TS, END_TS);
		this.times = times;
		this.nVehicles = nVehicles;
		this.plan = plan;
		final Vessel vessel = plan.getVessel();
		final int nBays = vessel.getNBays();
		final int nContainers = plan.getNTasks();
		final int nCranes = plan.getNCranes();
		this.waitIndex = new int[nCranes];
		this.extraBays = (1 + plan.getSafetyDistance()) * nCranes;
		
		wgPositions = new WorkGroup[nBays + extraBays * 2][2];
		wgInitPositions = new WorkGroup[nBays];
		rtContainers = new ResourceType[nContainers];
		actUnloads = new FullSafeUnloadActivity[nContainers];
		wgSecurityToken = new ArrayList<TreeMap<Integer, WorkGroup>>(nCranes);
		for (int craneId = 0; craneId < nCranes; craneId++) {
			wgSecurityToken.add(new TreeMap<>());
		}
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
			new QuayCraneGenerator(this, ets[craneId], (InitializerFlow)cranePlan[craneId][0], plan.getStartingPosition(craneId), plan.getSchedule(craneId).get(plan.getSchedule(craneId).size()-1));
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
		
		final ArrayList<TreeSet<Integer>> dependanceTasks = plan.getDependanceTasks();
		for (int craneId = 0; craneId < nCranes; craneId++) {
			for (int task : dependanceTasks.get(craneId)) {
				final ResourceType rtSecurityToken = new ResourceType(this, SECURITY_TOKEN + task);
				rtSecurityToken.addGenericResources(1);
				wgSecurityToken.get(craneId).put(task, new WorkGroup(this, rtSecurityToken, 1));				
			}
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
	 * @return the times
	 */
	public TimeRepository getTimes() {
		return times;
	}

	private Flow checkDependencies(int craneId, int containerId, int priority, Flow currentFlow, int[] currentBays) {
		// Checks the dependencies with the next cranes
		int[] dep = plan.getNextWaitIfNeeded(craneId, waitIndex[craneId], containerId);
		while (dep != null) {
			// Creates the route to the fictitious task
			currentFlow = moveCrane(craneId, currentFlow, currentBays[craneId], dep[1]);
			// Once in the fictitious task, acquires and releases the corresponding "security token", applying different priority depending on the direction of the schedule
			final int craneDoingTask = plan.getCraneDoTask(dep[2]);
			final WorkGroup wgSec = wgSecurityToken.get(craneDoingTask).get(dep[2]);
			final RequestResourcesFlow reqFlow = new RequestResourcesFlow(this, "Req " + SECURITY_TOKEN + dep[2], priority, false);
			reqFlow.addWorkGroup(wgSec);
			final ReleaseResourcesFlow relFlow = new ReleaseResourcesFlow(this, "Rel " + SECURITY_TOKEN + dep[2], wgSec);
			currentFlow = currentFlow.link(reqFlow).link(relFlow);					
			// Updates position
			currentBays[craneId] = dep[1];
			// Checks whether there are more dependencies
			waitIndex[craneId]++;
			dep = plan.getNextWaitIfNeeded(craneId, waitIndex[craneId], containerId);
		}		
		return currentFlow;
	}
	
	/**
	 * 
	 * @param leftToRight
	 * @param nCranes
	 * @return
	 */
	private Flow[][] moveCranesToInitPosition(boolean leftToRight, int nCranes, int[] currentBays) {
		// Saves first and last flow for each crane
		final Flow[][] flows = new Flow[nCranes][2];
		// Requests the security token of this crane and places it in its initial bay.
		for (int craneId = 0; craneId < nCranes; craneId++) {
			// First place the crane in the initial position
			currentBays[craneId] = plan.getStartingPosition(craneId);
			Flow lastFlow = null;
			// Starts by acquiring all the security token
			for (Entry<Integer,WorkGroup> entry : wgSecurityToken.get(craneId).entrySet()) {
				RequestResourcesFlow reqFlow = new RequestResourcesFlow(this, "Req " + SECURITY_TOKEN + entry.getKey(), nCranes - craneId, false);
				reqFlow.addWorkGroup(entry.getValue());
				if (lastFlow == null) {
					flows[craneId][0] = reqFlow;
					lastFlow = reqFlow;
				}
				else {
					lastFlow = lastFlow.link(reqFlow);
				}
			}

			// Acquires the initial position of the crane
			RequestResourcesFlow reqFlow = new RequestResourcesFlow(this, ACT_PLACE + currentBays[craneId]);
			reqFlow.addWorkGroup(wgInitPositions[currentBays[craneId]]);
			// No need to use security tokens
			if (lastFlow == null) {
				flows[craneId][0] = reqFlow;
				flows[craneId][1] = reqFlow;
			}
			else {
				flows[craneId][1] = lastFlow.link(reqFlow);				
			}
		}
		// Check conflicts 
		if (leftToRight) {
			for (int craneId = 0; craneId < nCranes - 1; craneId++) {
				flows[craneId][1] = checkDependencies(craneId, -1, nCranes - craneId, flows[craneId][1], currentBays);
			}
		}
		else {
			for (int craneId = 1; craneId < nCranes; craneId++) {
				flows[craneId][1] = checkDependencies(craneId, -1, craneId, flows[craneId][1], currentBays);
			}
		}
		return flows;		
	}
	
	/**
	 * Creates the flows required to represent a crane moving from one bay to other.
	 * @param craneId Crane identifier
	 * @param previousFlow A non-null flow representing the last step of the crane
	 * @param originBay The current position of the crane
	 * @param destinationBay The destination of the crane
	 * @return The last flow created
	 */
	private Flow moveCrane(int craneId, Flow previousFlow, int originBay, int destinationBay) {
		Flow flow = previousFlow;
		int currentBay = originBay;
		// When moving to the right, release the (craneBay - safetyDistance)th container and acquire the (craneBay + safetyDistance + 1)th container  
		while (currentBay < destinationBay) {
			final ReleaseResourcesFlow relBay = new ReleaseResourcesFlow(this, ACT_LEAVE_BAY + currentBay, wgPositions[currentBay + extraBays][0]);
			final RequestResourcesFlow reqBay =  new RequestResourcesFlow(this, ACT_GET_TO_BAY + (currentBay + 1));
			reqBay.addWorkGroup(0, wgPositions[currentBay + 1 + extraBays][1]);	
			final DelayFlow delayBay = new DelayFlow(this, ACT_MOVING, times.getMoveTime(craneId, currentBay));
			flow = flow.link(reqBay).link(relBay).link(delayBay);
			currentBay++;
		}
		// When moving to the left, release the (craneBay + safetyDistance)th container and acquire the (craneBay - safetyDistance - 1)th container  
		while (currentBay > destinationBay) {
			final ReleaseResourcesFlow relBay = new ReleaseResourcesFlow(this, ACT_LEAVE_BAY + currentBay, wgPositions[currentBay + extraBays][1]);
			final RequestResourcesFlow reqBay =  new RequestResourcesFlow(this, ACT_GET_TO_BAY + (currentBay - 1));
			reqBay.addWorkGroup(0, wgPositions[currentBay - 1 + extraBays][0]);	
			final DelayFlow delayBay = new DelayFlow(this, ACT_MOVING, times.getMoveTime(craneId, currentBay));
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
		final int []currentBays = new int[nCranes];
		
		final Flow[][] flows = moveCranesToInitPosition(leftToRight, nCranes, currentBays);
		// Now reproduces the schedule
		for (int craneId = 0; craneId < nCranes; craneId++) {
			Flow flow = flows[craneId][1];
			
			// Analyze the plan and move if needed
			final ArrayList<Integer> cranePlan = plan.getSchedule(craneId);
			for (int i = 0; i < cranePlan.size(); i++) {
				final int containerId = cranePlan.get(i);
				final int destinationBay = plan.getVessel().getContainerBay(containerId);
				flow = moveCrane(craneId, flow, currentBays[craneId], destinationBay);
				flow.link(actUnloads[containerId]);
				// Checks whether this was the task creating a conflict
				if (wgSecurityToken.get(craneId).containsKey(containerId)) {
					final ReleaseResourcesFlow relFlow = new ReleaseResourcesFlow(this, "Rel " + SECURITY_TOKEN + containerId, wgSecurityToken.get(craneId).get(containerId));
					actUnloads[containerId].link(relFlow);
					flow = relFlow;
				}
				else {
					flow = actUnloads[containerId];
				}
				currentBays[craneId] = destinationBay;
				// Checks if there is a dependency before moving any more
				flow = checkDependencies(craneId, containerId, (leftToRight) ? nCranes - craneId : craneId, flow, currentBays);				
			}
			// When finish, move to try not to disturb and releases all resources
			if (leftToRight) {
				//flow = moveCrane(craneId, flow, currentBays[craneId], nBays + extraBays * 2 - 1 - (nCranes - craneId - 1) * (safetyDistance + 1));
				flow = moveCrane(craneId, flow, currentBays[craneId], nBays + craneId * (safetyDistance + 1));
			}
			else {
				flow = moveCrane(craneId, flow, currentBays[craneId], craneId * (safetyDistance + 1));
			}
			flows[craneId][1] = flow.link(new ReleaseResourcesFlow(this, END_WORK));
		}
		
		return flows;
	}
	
}
