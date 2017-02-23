package es.ull.iis.simulation.sequential;

import java.util.ArrayList;
import java.util.TreeMap;

import es.ull.iis.simulation.info.ResourceInfo;
import es.ull.iis.simulation.info.ResourceUsageInfo;
import es.ull.iis.simulation.model.DiscreteEvent;
import es.ull.iis.simulation.model.Resource;
import es.ull.iis.simulation.model.Resource.CancelPeriodOffEvent;
import es.ull.iis.simulation.model.ResourceType;
import es.ull.iis.simulation.model.flow.FlowExecutor;
import es.ull.iis.simulation.model.flow.ResourceHandlerFlow;
import es.ull.iis.util.DiscreteCycleIterator;

/**
 * A resource is an element that becomes available at a specific simulation time and 
 * becomes unavailable at other simulation time. The availability of a resource is controlled
 * by means of timetable entries, which define a resource type and an availability cycle.
 * A resource finishes its execution when it has no longer valid timetable entries.
 * @author Carlos Martín Galán
 */
public class ResourceEngine extends EventSourceEngine implements es.ull.iis.simulation.model.ResourceEngine {
    /** If true, indicates that this resource is being used after its availability time has expired */
    private boolean timeOut = false;
    /** List of currently active roles and the timestamp which marks the end of their availibity time. */
    protected final TreeMap<ResourceType, Long> currentRoles;
    /** A counter of the valid timetable entries which this resource is following. */
    private int validTTEs = 0;
    /** The resource type which this resource is being booked for */
    protected ResourceType currentResourceType = null;
    /** Work thread which currently holds this resource */
    protected WorkThread currentWT = null;
    /** Availability flag */
    protected boolean notCanceled;

    /**
     * Creates a new instance of Resource.
     * @param id This resource's identifier.
     * @param simul Simulation this resource is attached to.
     * @param description A short text describing this resource.
     */
	public ResourceEngine(SequentialSimulationEngine simul, es.ull.iis.simulation.model.Resource modelRes) {
		super(simul, modelRes, "RES");
        currentRoles = new TreeMap<ResourceType, Long>();
        notCanceled = true;
        simul.add(this);
	}

    /**
	 * @return the modelRes
	 */
	public Resource getModelRes() {
		return (Resource)modelEv;
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
		role.getManager().availableResource(); 
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
	 * @param wt The work thread in charge of executing the current flow
	 * @return The availability timestamp of this resource for this resource type 
	 */
	protected long catchResource(WorkThread wt) {
		simul.getModel().getInfoHandler().notifyInfo(new ResourceUsageInfo(simul, (Resource) modelEv, currentResourceType, wt, wt.getModelElement(), (ResourceHandlerFlow) wt.getCurrentFlow(), ResourceUsageInfo.Type.CAUGHT, simul.getTs()));
		currentWT = wt;
		return currentRoles.get(currentResourceType);
	}
	
    /**
     * Releases this resource. If the resource has already expired its availability time, 
     * the timeOut flag is set off. Sets the current work item and the current resource type 
     * to <code>null</code>. The book of the resource is released too.
     * @return True if the resource could be correctly released. False if the availability
     * time of the resource had already expired.
     */
    public boolean releaseResource() {
    	simul.getModel().getInfoHandler().notifyInfo(new ResourceUsageInfo(simul, (Resource) modelEv, currentResourceType, currentWT, currentWT.getModelElement(), (ResourceHandlerFlow) currentWT.getCurrentFlow(), ResourceUsageInfo.Type.RELEASED, simul.getTs()));
        currentWT = null;
        currentResourceType = null;        
        if (timeOut) {
        	timeOut = false;
        	return false;
        }
        return true;
    }
    
    /**
     * Returns the work item of the element which currently owns this resource.
     * @return The current work item.
     */
 	@Override
	public FlowExecutor getCurrentFlowExecutor() {
		return currentWT;
	}
    /**
     * Getter for property currentResourceType.
     * @return Value of property currentResourceType.
     */
    @Override
    public ResourceType getCurrentResourceType() {
        return currentResourceType;
    }
    
	@Override
	public void notifyCurrentManagers() {
		for (ActivityManager am : getCurrentManagers()) {
			// The activity manger is informed of new available resources
			am.availableResource(); 
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
		return ((currentWT == null) && (notCanceled) && (getAvailability(rt) > simul.getTs()));
	}
	
	/**
	 * Sets the available flag of a resource.
	 * @param available The availability state of the resource.
	 */
	@Override
	public void setNotCanceled(boolean available) {
		notCanceled = available;
	}

	class ClockOnEntry {
		private long init = 0;
		private long finish = 0;
		private long avCounter = 0;
		
		
		public ClockOnEntry(long init) {
			this.init = init;
			finish = 0;
			avCounter = 0;
		}

		public long getFinish() {
			return finish;
		}

		public void setFinish(long finish) {
			this.finish = finish;
		}

		public long getInit() {
			return init;
		}

		public void setInit(long init) {
			this.init = init;
		}

		public long getAvCounter() {
			return avCounter;
		}

		public void setAvCounter(long avCounter) {
			this.avCounter = avCounter;
		}

	}

}
