package es.ull.iis.simulation.sequential;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.TreeMap;

import es.ull.iis.simulation.info.ResourceUsageInfo;
import es.ull.iis.simulation.model.ActivityManager;
import es.ull.iis.simulation.model.Element;
import es.ull.iis.simulation.model.ElementInstance;
import es.ull.iis.simulation.model.Resource;
import es.ull.iis.simulation.model.ResourceType;
import es.ull.iis.simulation.model.engine.EngineObject;
import es.ull.iis.simulation.model.flow.ResourceHandlerFlow;

/**
 * A resource is an element that becomes available at a specific simulation time and 
 * becomes unavailable at other simulation time. The availability of a resource is controlled
 * by means of timetable entries, which define a resource type and an availability cycle.
 * A resource finishes its execution when it has no longer valid timetable entries.
 * @author Carlos Martín Galán
 */
public class ResourceEngine extends EngineObject implements es.ull.iis.simulation.model.engine.ResourceEngine {
    /** If true, indicates that this resource is being used after its availability time has expired */
    private boolean timeOut = false;
    /** List of currently active roles and the timestamp which marks the end of their availibity time. */
    protected final TreeMap<ResourceType, Long> currentRoles;
    /** A counter of the valid timetable entries which this resource is following. */
    private int validTTEs = 0;
    /** Element which currently holds this resource */
    protected Element currentElem = null;
    /** Availability flag */
    protected boolean notCanceled;
    /** The associated {@link Resource} */
    private final Resource modelRes;

    /**
     * Creates a new instance of Resource.
     * @param id This resource's identifier.
     * @param simul ParallelSimulationEngine this resource is attached to.
     * @param description A short text describing this resource.
     */
	public ResourceEngine(SequentialSimulationEngine simul, es.ull.iis.simulation.model.Resource modelRes) {
		super(modelRes.getIdentifier(), simul, "RES");
        currentRoles = new TreeMap<ResourceType, Long>();
        notCanceled = true;
        this.modelRes = modelRes;
	}

    /**
     * Returns the associated {@link Resource}
	 * @return the associated {@link Resource}
	 */
	public Resource getModelRes() {
		return modelRes;
	}

	/**
	 * Adds a new resource type to the list of current roles. If the list already contains an
	 * entry for the resource type, the greater timestamp is added.
	 * @param role New resource type added
	 * @param ts Timestamp when the availability of this resource finishes for this resource type. 
	 */
	@Override
	public void addRole(ResourceType role, long ts) {
		Long avEnd = currentRoles.get(role);
		if ((avEnd == null) || (ts > avEnd))
			currentRoles.put(role, ts);
		// The activity manager is informed of new available resources
		role.notifyResource(); 
	}

	/**
	 * Removes a resource type from the list of current roles. If the role doesn't exist
	 * the removal is silently skipped (that's because a resource can have several timetable 
	 * entries for the same role, but the <code>currentRoles</code> list only contains 
	 * one entry per role). However, checks if it's time for removing the role before doing it.
	 * @param role Resource type removed
	 */
	@Override
	public void removeRole(ResourceType role) {
		Long avEnd = currentRoles.get(role);
		if (avEnd != null)
			if (avEnd <= simul.getTs())
				currentRoles.remove(role);
	}

	/**
	 * Builds a list of activity managers referenced by the roles of the resource. 
	 * @return Returns the currentManagers.
	 */
	@Override
	public ArrayList<ActivityManager> getCurrentManagers() {
		ArrayList <ActivityManager> currentManagers = new ArrayList<ActivityManager>();
		for (ResourceType role : currentRoles.keySet())
			if (!currentManagers.contains(role.getManager()))
				currentManagers.add(role.getManager());
		return currentManagers;
	}

	/**
	 * Marks this resource as taken by an element. Sets the current work item, and the
	 * current resource type; and adds this resource to the item's caught resources list.
	 * A "taken" element continues being booked. The book is released when the resource itself is
	 * released. 
	 * @param ei The element instance in charge of executing the current flow
	 * @return The availability timestamp of this resource for this resource type 
	 */
	public long catchResource(ElementInstance ei) {
		simul.getSimulation().notifyInfo(new ResourceUsageInfo(simul.getSimulation(), modelRes, modelRes.getCurrentResourceType(), ei, ei.getElement(), (ResourceHandlerFlow) ei.getCurrentFlow(), ResourceUsageInfo.Type.CAUGHT, simul.getTs()));
		currentElem = ei.getElement();
		return currentRoles.get(modelRes.getCurrentResourceType());
	}
	
    /**
     * Releases this resource. If the resource has already expired its availability time, 
     * the timeOut flag is set off. Sets the current work item and the current resource type 
     * to <code>null</code>. The book of the resource is released too.
     * @return True if the resource could be correctly released. False if the availability
     * time of the resource had already expired.
     */
    public boolean releaseResource(ElementInstance ei) {
    	simul.getSimulation().notifyInfo(new ResourceUsageInfo(simul.getSimulation(), modelRes, modelRes.getCurrentResourceType(), ei, currentElem, (ResourceHandlerFlow) ei.getCurrentFlow(), ResourceUsageInfo.Type.RELEASED, simul.getTs()));
        currentElem = null;
        modelRes.setCurrentResourceType(null);        
        if (timeOut) {
        	timeOut = false;
        	return false;
        }
        return true;
    }
    
 	@Override
	public Element getCurrentElement() {
		return currentElem;
	}
 	
	@Override
	public void notifyCurrentManagers() {
		for (ActivityManager am : getCurrentManagers()) {
			// The activity manger is informed of new available resources
			am.notifyResource(); 
		}
	}

    /**
     * Returns the availability of this resource for the specified resource type.
     * @param rt Resource type
     * @return the availability of this resource for the specified resource type; 
     * <code>null</code> if the resource is not available for this resource type.
     */
    protected Long getAvailability(ResourceType rt) {
    	return currentRoles.get(rt); 
    }

	public TreeMap<ResourceType, Long> getCurrentRoles() {
		return currentRoles;
	}

	@Override
	public int incValidTimeTableEntries() {
		return ++validTTEs;
	}

	@Override
	public int decValidTimeTableEntries() {
		return --validTTEs;
	}

	@Override
	public int getValidTimeTableEntries() {
		return validTTEs;
	}

	/**
	 * Checks if a resource is available for a specific Resource Type. The resource type is used to prevent 
	 * using a resource when it's becoming unavailable right at this timestamp. 
	 * @return True if the resource is available.
	 */
	@Override
	public boolean isAvailable(ResourceType rt) {
		return ((currentElem == null) && (notCanceled) && (getAvailability(rt) > simul.getTs()));
	}
	
	/**
	 * Sets the available flag of a resource.
	 * @param available The availability state of the resource.
	 */
	@Override
	public void setNotCanceled(boolean available) {
		notCanceled = available;
	}

	@Override
	public boolean add2Solution(ArrayDeque<Resource> solution, ResourceType rt, ElementInstance ei) {
        // Checks if the resource is busy (taken by other element or conflict in the same activity)
		// TODO: Check if "isAvailable" is required in this condition
        if (isAvailable(rt) && (modelRes.getCurrentResourceType() == null)) {
	        // This resource belongs to the solution...
        	modelRes.setCurrentResourceType(rt);
        	solution.push(modelRes);
        	return true;
        }
		return false;
	}

	@Override
	public void removeFromSolution(ArrayDeque<Resource> solution, ElementInstance ei) {
		modelRes.setCurrentResourceType(null);
		solution.remove(modelRes);
	}
	
	@Override
    public void notifyEnd() {
        simul.addEvent(modelRes.onDestroy(simul.getTs()));
    }
    

}
