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
import es.ull.iis.simulation.model.flow.TaskFlow;
import simkit.random.RandomNumber;
import simkit.random.RandomNumberFactory;

/**
 * A port model where quay cranes cannot operate when there is another crane OPERATING inside the safety distance
 * @author Iv�n Castilla
 * 
 * TODO
 *   Distancia de seguridad
 *   Si se mueven de izq a der., si la primera tarea de la gr�a 2 (m�s a la derecha) comienza m�s a la izq. que donde est� la gr�a 1, 
 *   desplazar gr�a 1 hacia la izq. respetando distancia de seguridad (mirar tareas ficiticias christopher)
 *   Crear tarea fictica para gr�a 1 de tiempo 0 y que dependa de un recurso que s�lo se activa cuando la gr�a 2
 *   ha terminado las tareas conflictivas (Christopher intentar� d�rmelo). 
 *
 *  It is possible for quay cranes to start a task that conflict with another crane. To avoid this situation, each pair of quay cranes share a "security token". 
 *  If schedule is left to right, the rightmost crane acquires this token at start. If there is no initial conflict, the crane releases the token immediately. 
 *  Otherwise, it keeps the token until the conflicting task finishes. The leftmost crane moves to avoid the conflict (according to a predefined fictitious task) and
 *  then tries to acquire the token. Increasing higher priority is assigned to each crane, so to ensure that, even when the leftmost crane do not have to move, 
 *  the rightmost crane acquires the token first. 
 *
 *	Al terminar su �ltima tarea, hacer que la gr�a se desplace lo m�s que pueda en la direcci�n en que estaba trabajando (no contabilizar estos
 *tiempos en el valor objetivo).
 *
 */
public class PortModel extends Simulation {
	public enum SafetyType {
		FULL,
		OPERATION_ONLY
	}
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
	protected static final String POSITION = "Position";
	protected static final String SECURITY_TOKEN = "Security token";
	private static final RandomNumber rng = RandomNumberFactory.getInstance();
	private final double pError;
	private final long currentSeed;
	private final StowagePlan plan;
	private final int []nVehicles;
	private final ResourceType rtGenericVehicle;
	private final ResourceType[] rtSpecificVehicle;
	private final ResourceType[] rtOpPositions;
	private final WorkGroup[] wgPositions;
	private final WorkGroup[] wgOpPositionsSides;
	private final ResourceType[] rtContainers;
	private final WorkGroup[] wgContainers;
	private final WorkGroup[] wgSpecificContainers;
	private final WorkGroup[] wgSecurityToken;
	private final Flow[][] cranePlan;
	private final TaskFlow[] actUnloads;
	private final SafetyType safetyType;

	/**
	 * Creates a port simulation with only generic delivery vehicles
	 * @param plan The stowage plan that defines the tasks to perform
	 * @param id The simulation identifier
	 * @param nGenericVehicles Number of delivery vehicles to be used
	 * @param pError Percentage error for each time parameter of the simulation. If 0.0, deterministic simulation 
	 */
	public PortModel(SafetyType safety, StowagePlan plan, int id, int nGenericVehicles, double pError) {
		this(safety, plan, id, new int[] {nGenericVehicles}, pError, rng.getSeed());
	}
	
	/**
	 * Creates a port simulation with only generic delivery vehicles, initialized with the specified random seed
	 * @param plan The stowage plan that defines the tasks to perform
	 * @param id The simulation identifier
	 * @param nGenericVehicles Number of delivery vehicles to be used
	 * @param pError Percentage error for each time parameter of the simulation. If 0.0, deterministic simulation
	 * @param seed Random seed used to ensure that the random parameters of the simulation replicate those from a former simulation 
	 */
	public PortModel(SafetyType safety, StowagePlan plan, int id, int nGenericVehicles, double pError, long seed) {
		this(safety, plan, id, new int[] {nGenericVehicles}, pError, seed);
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
	public PortModel(SafetyType safety, StowagePlan plan, int id, int[] nVehicles, double pError) {
		this(safety, plan, id, nVehicles, pError, rng.getSeed());
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
	public PortModel(SafetyType safety, StowagePlan plan, int id, int[] nVehicles, double pError, long seed) {
		super(id, DESCRIPTION + " " + id, PORT_TIME_UNIT, START_TS, END_TS);
		this.safetyType = safety;
		currentSeed = seed;
		rng.setSeed(seed);
		this.nVehicles = nVehicles;
		this.pError = pError;
		this.plan = plan;
		final Vessel vessel = plan.getVessel();
		final int nBays = vessel.getNBays();
		final int nContainers = plan.getNTasks();
		final int nCranes = plan.getNCranes();
		
		wgPositions = new WorkGroup[nBays];
		rtContainers = new ResourceType[nContainers];
		switch(safety) {
		case OPERATION_ONLY:
			rtOpPositions = new ResourceType[nBays];
			wgOpPositionsSides = new WorkGroup[nBays];
			actUnloads = new SafeOperationUnloadActivity[nContainers];
			wgSecurityToken = null;
			break;
		case FULL:
		default:
			rtOpPositions = null;
			wgOpPositionsSides = null;
			actUnloads = new FullSafeUnloadActivity[nContainers];
			wgSecurityToken = new WorkGroup[nCranes];
			break;
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
		
		createResources(vessel, nBays, nCranes, nContainers);
		
		// Set the containers which are available from the beginning and creates the activities
		for (int bayId = 0; bayId < nBays; bayId++) {
			final ArrayList<Integer> bay = vessel.getBay(bayId);
			if (!bay.isEmpty()) {
				// Creates the container on top of the bay
				int containerId1 = vessel.peek(bayId);
				final Resource resContainer = new Resource(this, CONTAINER + containerId1);
				resContainer.addTimeTableEntry(rtContainers[containerId1]);
				switch(safety) {
				case OPERATION_ONLY:
					// Creates the activities for the top and intermediate containers. These activities create new containers.
					for (int i = bay.size() - 1; i > 0; i--) {
						containerId1 = bay.get(i);
						actUnloads[containerId1] = new SafeOperationUnloadActivity(this, containerId1, bay.get(i-1));
					}
					// Creates the activity corresponding to the bottom container
					containerId1 = bay.get(0);
					actUnloads[containerId1] = new SafeOperationUnloadActivity(this, containerId1);
					break;
				case FULL:
				default:
					// Creates the activities for the top and intermediate containers. These activities create new containers.
					for (int i = bay.size() - 1; i > 0; i--) {
						containerId1 = bay.get(i);
						actUnloads[containerId1] = new FullSafeUnloadActivity(this, containerId1, bay.get(i-1));
					}
					// Creates the activity corresponding to the bottom container
					containerId1 = bay.get(0);
					actUnloads[containerId1] = new FullSafeUnloadActivity(this, containerId1);
					break;
				}
			}
		}
		
		// Creates the main element type representing quay cranes
		final ElementType[] ets = new ElementType[nCranes]; 
		cranePlan = createFlowFromPlan(plan, vessel);
		for (int craneId = 0; craneId < nCranes; craneId++) {
			ets[craneId] = new ElementType(this, QUAY_CRANE + craneId);
			new QuayCraneGenerator(this, ets[craneId], (InitializerFlow)cranePlan[craneId][0], plan.getInitialPosition(craneId));
		}
	}
	
	private void createResources(Vessel vessel, int nBays, int nCranes, int nContainers) {
		final int safetyDistance = plan.getSafetyDistance();
		// Creates the "positions" of the cranes in front of the bays and the activities to move among bays 
		final ResourceType[] rtPositions = new ResourceType[nBays];
		for (int bayId = 0; bayId < nBays; bayId++) {
			rtPositions[bayId] = new ResourceType(this, POSITION + bayId);
			rtPositions[bayId].addGenericResources(1);
			wgPositions[bayId] = new WorkGroup(this, rtPositions[bayId], 1);
		}
		
		switch(safetyType) {
		case OPERATION_ONLY:
			for (int bayId = 0; bayId < nBays; bayId++) {
				rtOpPositions[bayId] = new ResourceType(this, OPERATE + bayId);
				rtOpPositions[bayId].addGenericResources(1);
			}
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
			break;
		case FULL:
		default:
			for (int craneId = 0; craneId < nCranes; craneId++) {
				final ResourceType rtSecurityToken = new ResourceType(this, SECURITY_TOKEN + craneId);
				rtSecurityToken.addGenericResources(1);
				wgSecurityToken[craneId] = new WorkGroup(this, rtSecurityToken, 1);
			}
		}

		for (int containerId = 0; containerId < nContainers; containerId++) {
			rtContainers[containerId] = new ResourceType(this, CONTAINER + containerId);
		}
		if (hasGenericVehicles()) {
			rtGenericVehicle.addGenericResources(getNGenericVehicles());
			switch(safetyType) {
			case OPERATION_ONLY:
				for (int containerId = 0; containerId < nContainers; containerId++) {
					wgContainers[containerId] = createUnloadWorkGroup(vessel.getContainerBay(containerId), safetyDistance, nBays, rtContainers[containerId], rtGenericVehicle);
				}
				break;
			case FULL:
			default:
				for (int containerId = 0; containerId < nContainers; containerId++) {
					wgContainers[containerId] = new WorkGroup(this, new ResourceType[] {rtGenericVehicle, rtContainers[containerId]}, new int[] {1, 1});
				}
				break;
			}
		}
		if (hasSpecificVehicles()) {
			for (int craneId = 0; craneId < nCranes; craneId++) {
				rtSpecificVehicle[craneId] = new ResourceType(this, DELIVERY_VEHICLE + craneId);
				rtSpecificVehicle[craneId].addGenericResources(getNSpecificVehiclesXCrane(craneId));
				switch(safetyType) {
				case OPERATION_ONLY:
					for (int containerId : plan.getSchedule(craneId)) {
						wgSpecificContainers[containerId] = createUnloadWorkGroup(vessel.getContainerBay(containerId), safetyDistance, nBays, rtContainers[containerId], rtSpecificVehicle[craneId]);
					}
					break;
				case FULL:
				default:
					for (int containerId = 0; containerId < nContainers; containerId++) {
						wgSpecificContainers[containerId] = new WorkGroup(this, new ResourceType[] {rtSpecificVehicle[craneId], rtContainers[containerId]}, new int[] {1, 1});
					}
					break;
				}
			}
		}
	}
	
	private WorkGroup createUnloadWorkGroup(int containerBay, int safetyDistance, int nBays, ResourceType rtContainer, ResourceType rtVehicle) {
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
	 * @return the wgOpPositionsSides
	 */
	public WorkGroup getWgOpPositionsSides(int bayId) {
		if (SafetyType.OPERATION_ONLY == safetyType)
			return wgOpPositionsSides[bayId];
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
	 * @param leftToRight
	 * @param craneId
	 * @param craneBay
	 * @param offset The safety distance if "full safety" applies; 0 if "operation only" safety distance applies. 
	 * @param nBays
	 * @return
	 */
	private Flow[] initCrane(boolean leftToRight, int craneId, int craneBay, int offset, int nBays, int nCranes, boolean needToken) {
		final Flow[] initEndFlow = new Flow[2];
		if (leftToRight) {
			// Acquires the leftmost position 
			final RequestResourcesFlow reqFlow = new RequestResourcesFlow(this, ACT_PLACE + Math.max(craneBay - offset, 0), Math.max(craneBay - offset, 0)+1);
			reqFlow.addWorkGroup(wgPositions[Math.max(craneBay - offset, 0)]);
			initEndFlow[1] = reqFlow;
			if (needToken) {
				// Starts by acquiring the security token
				initEndFlow[0] = new RequestResourcesFlow(this, "Req " + SECURITY_TOKEN, nBays + craneId, nCranes - craneId);
				((RequestResourcesFlow)initEndFlow[0]).addWorkGroup(wgSecurityToken[craneId]);
				initEndFlow[0].link(reqFlow);
			}
			else {
				initEndFlow[0] = reqFlow;
			}

			// Then acquires the rest of positions within the safety distance (none if "operation only" safety distance applies)
			for (int i = Math.max(craneBay - offset, 0) + 1; i < Math.min(craneBay + offset + 1, nBays); i++) {
				final RequestResourcesFlow nextFlow =  new RequestResourcesFlow(this, ACT_PLACE + i, i+1);
				nextFlow.addWorkGroup(wgPositions[i]);
				initEndFlow[1].link(nextFlow);
				initEndFlow[1] = nextFlow;
			}
		}
		else {
			// Acquires the rightmost position 
			final RequestResourcesFlow reqFlow = new RequestResourcesFlow(this, ACT_PLACE + Math.min(craneBay + offset, nBays - 1), Math.min(craneBay + offset, nBays - 1)-1);
			reqFlow.addWorkGroup(wgPositions[Math.min(craneBay + offset, nBays - 1)]);
			initEndFlow[1] = reqFlow;
			if (needToken) {
				// Starts by acquiring the security token
				initEndFlow[0] = new RequestResourcesFlow(this, "Req " + SECURITY_TOKEN, nBays + craneId, craneId);
				((RequestResourcesFlow)initEndFlow[0]).addWorkGroup(wgSecurityToken[craneId]);
				initEndFlow[0].link(reqFlow);
			}
			else {
				initEndFlow[0] = reqFlow;
			}
			
			// Then acquires the rest of positions within the safety distance (none if "operation only" safety distance applies)
			for (int i = Math.min(craneBay + offset, nBays - 1) - 1; i >= Math.max(craneBay - offset, 0); i--) {
				final RequestResourcesFlow nextFlow =  new RequestResourcesFlow(this, ACT_PLACE + i, i-1);
				nextFlow.addWorkGroup(wgPositions[i]);
				initEndFlow[1].link(nextFlow);
				initEndFlow[1] = nextFlow;
			}			
		}
		return initEndFlow;		
	}
	
	/**
	 * 
	 * @param plan
	 * @param vessel
	 * @param offset
	 * @return
	 */
	private Flow[][] moveCranesToInitPosition(StowagePlan plan, Vessel vessel, int offset) {
		final boolean leftToRight = plan.isLeftToRight();
		final int nCranes = plan.getNCranes();
		final int nBays = vessel.getNBays();
		// Saves first and last flow for each crane
		final Flow[][] flows = new Flow[nCranes][2];
		for (int craneId = 0; craneId < nCranes; craneId++) {
			// First place the crane in the initial position
			flows[craneId] = initCrane(leftToRight, craneId, plan.getInitialPosition(craneId), offset, nBays, nCranes, plan.createsConflictAtStart(craneId));
		}
		// Check conflicts only if full safety applies
		if (SafetyType.FULL == safetyType) {
			if (leftToRight) {
				for (int craneId = 0; craneId < nCranes - 1; craneId++) {
					// Checks the dependencies with the next crane
					final int[] dep = plan.getDependenciesAtStart(craneId);
					if (dep[1] != -1) {
						// Creates the route to the fictitious task
						flows[craneId][1] = createMoveFlow(flows[craneId][1], plan.getInitialPosition(craneId), dep[0], offset);
						// Once in the fictitious task, try to acquire the corresponding "security token", applying different priority depending on the direction of the schedule
						final RequestResourcesFlow reqFlow = new RequestResourcesFlow(this, "Req " + SECURITY_TOKEN + craneId, nBays + craneId, nCranes - craneId);
						reqFlow.addWorkGroup(wgSecurityToken[craneId]);
						flows[craneId][1].link(reqFlow);
						flows[craneId][1] = reqFlow;
					}
				}
			}
			else {
				for (int craneId = 1; craneId < nCranes; craneId++) {
					// Checks the dependencies with the next crane
					final int[] dep = plan.getDependenciesAtStart(craneId);
					if (dep[1] != -1) {
						// Creates the route to the fictitious task
						flows[craneId][1] = createMoveFlow(flows[craneId][1], plan.getInitialPosition(craneId), dep[0], offset);
						// Once in the fictitious task, try to acquire the corresponding "security token", applying different priority depending on the direction of the schedule
						final RequestResourcesFlow reqFlow = new RequestResourcesFlow(this, "Req " + SECURITY_TOKEN + craneId, nBays + craneId, craneId);
						reqFlow.addWorkGroup(wgSecurityToken[craneId]);
						flows[craneId][1].link(reqFlow);
						flows[craneId][1] = reqFlow;
					}
				}
			}
		}
		return flows;		
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
	 * Creates the flows required to represent a crane moving from one bay to other.
	 * @param previousFlow A non-null flow representing the last step of the crane
	 * @param originBay The current position of the crane
	 * @param destinationBay The destination of the crane
	 * @param offset A modifier to take into account safety distance
	 * @return The last flow created
	 */
	private Flow createMoveFlow(Flow previousFlow, int originBay, int destinationBay, int offset) {
		Flow flow = previousFlow;
		// When moving to the right, release the (craneBay - safetyDistance)th container and acquire the (craneBay + safetyDistance + 1)th container  
		while (originBay < destinationBay) {
			final ReleaseResourcesFlow relBay = getLeaveBayFlow(originBay - offset);
			flow.link(getGetToBayFlow(originBay + offset + 1)).link(relBay);
			flow = relBay;
			originBay++;
		}
		// When moving to the left, release the (craneBay + safetyDistance)th container and acquire the (craneBay - safetyDistance - 1)th container  
		while (originBay > destinationBay) {
			final ReleaseResourcesFlow relBay = getLeaveBayFlow(originBay + offset);
			flow.link(getGetToBayFlow(originBay - offset - 1)).link(relBay);
			flow = relBay;
			originBay--;								
		}
		return flow;
	}
	/**
	 * Creates the work flow associated to a specific stowage plan for a specific crane
	 * @param plan The stowage plan
	 * @param vessel The vessel which is being operated 
	 * @param craneId Crane identifier
	 * @return The first step of the work flow that the specified crane must follow to fulfill its stowage plan.
	 */
	private Flow[][] createFlowFromPlan(StowagePlan plan, Vessel vessel) {
		final boolean leftToRight = plan.isLeftToRight();
		final int nBays = vessel.getNBays();
		final int offset = (SafetyType.FULL == safetyType) ? plan.getSafetyDistance() : 0;
		final Flow[][] flows = moveCranesToInitPosition(plan, vessel, offset);
		final int nCranes = plan.getNCranes();
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
			int craneBay = plan.getInitialPosition(craneId);
			Flow flow = flows[craneId][1];
			
			// Analyze the plan and move if needed
			final ArrayList<Integer> cranePlan = plan.getSchedule(craneId);
			for (int i = 0; i < cranePlan.size(); i++) {
				final int containerId = cranePlan.get(i);
				final int destinationBay = vessel.getContainerBay(containerId);
				flow = createMoveFlow(flow, craneBay, destinationBay, offset);
				flow.link(actUnloads[containerId]);
				// Checks whether this was the task creating a conflict
				if (containerId == freeConflictOn) {
					final ReleaseResourcesFlow relFlow = new ReleaseResourcesFlow(this, "Rel" + SECURITY_TOKEN + craneId, nBays + craneId, wgSecurityToken[craneId]);
					actUnloads[containerId].link(relFlow);
					flow = relFlow;
				}
				else {
					flow = actUnloads[containerId];
				}
				craneBay = destinationBay;
			}
			// When finish, move to try not to disturb
			if (leftToRight) {
				createMoveFlow(flow, craneBay, nBays - 1 - (nCranes - craneId - 1) * (offset + 1), offset);
			}
			else {
				createMoveFlow(flow, craneBay, craneId * (offset + 1), offset);
			}
		}
		
		return flows;
	}
	
}
