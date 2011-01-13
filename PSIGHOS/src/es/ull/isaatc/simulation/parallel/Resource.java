package es.ull.isaatc.simulation.parallel;

import java.util.ArrayList;
import java.util.Collection;
import java.util.TreeMap;
import java.util.concurrent.atomic.AtomicInteger;

import es.ull.isaatc.simulation.core.SimulationCycle;
import es.ull.isaatc.simulation.core.TimeStamp;
import es.ull.isaatc.simulation.core.TimeTableEntry;
import es.ull.isaatc.simulation.info.ResourceInfo;
import es.ull.isaatc.simulation.info.ResourceUsageInfo;
import es.ull.isaatc.util.DiscreteCycleIterator;

/**
 * A resource is an element that becomes available at a specific simulation time and 
 * becomes unavailable at other simulation time. The availability of a resource is controlled
 * by means of timetable entries, which define a resource type and an availability cycle.
 * A resource finishes its execution when it has no longer valid timetable entries.
 * TODO Comment
 * @author Carlos Martín Galán
 */
public class Resource extends BasicElement implements es.ull.isaatc.simulation.core.Resource {
	/** Timetable which defines the availability structure of the resource */
    protected final ArrayList<TimeTableEntry> timeTable = new ArrayList<TimeTableEntry>();
    /** Availability time table. Define CancelPeriodOn and CancelPeriodOff events */
    protected final ArrayList<TimeTableEntry> cancelPeriodTable = new ArrayList<TimeTableEntry>();;
    /** A brief description of the resource */
    protected final String description;
    /** If true, indicates that this resource is being used after its availability time has expired */
    private boolean timeOut = false;
    /** List of currently active roles and the timestamp which marks the end of their availability time */
    protected final TreeMap<ResourceType, Long> currentRoles = new TreeMap<ResourceType, Long>();
    /** A counter of the valid timetable entries which this resource is following */
    private final AtomicInteger validTTEs = new AtomicInteger();
    /** The resource type which this resource is being booked for */
    protected ResourceType currentResourceType = null;
    /** Work item which currently holds this resource */
    protected WorkItem currentWI = null;
    /** List of elements trying to book this resource */
    protected final TreeMap<WorkItem, ResourceType> bookList = new TreeMap<WorkItem, ResourceType>();
    /** Availability flag */
    protected volatile boolean notCanceled = true;
    /** List of current activity managers which contain a resource type using this resource */
    protected final TreeMap<ActivityManager, Integer> currentAMs = new TreeMap<ActivityManager, Integer>();

    /**
     * Creates a new Resource.
     * @param id This resource's identifier.
     * @param simul Simulation this resource is attached to.
     * @param description A short text describing this resource.
     */
	public Resource(int id, Simulation simul, String description) {
		super(id, simul);
		this.description = description;
        simul.add(this);
	}

	/**
	 * Launches the events corresponding to the timetable entries. If no valid entry is found, this resource 
	 * finishes its execution. 
	 */
	@Override
    protected void init() {
		simul.getInfoHandler().notifyInfo(new ResourceInfo(this.simul, this, this.getCurrentResourceType(), ResourceInfo.Type.START, ts));
		for (int i = 0 ; i < timeTable.size(); i++) {
			TimeTableEntry tte = timeTable.get(i);
	        DiscreteCycleIterator iter = tte.getCycle().getCycle().iterator(getTs(), simul.getInternalEndTs());
	        long nextTs = iter.next();
	        if (nextTs != -1) {
	            RoleOnEvent rEvent = new RoleOnEvent(nextTs, (ResourceType) tte.getRole(), iter, simul.simulationTime2Long(tte.getDuration()));
	            addEvent(rEvent);
	            validTTEs.incrementAndGet();
	        }
		}
		for (int i = 0 ; i < cancelPeriodTable.size(); i++) {
			TimeTableEntry tte = cancelPeriodTable.get(i);
	        DiscreteCycleIterator iter = tte.getCycle().getCycle().iterator(getTs(), simul.getInternalEndTs());
	        long nextTs = iter.next();
	        if (nextTs != -1) {
	            CancelPeriodOnEvent aEvent = new CancelPeriodOnEvent(nextTs, iter, simul.simulationTime2Long(tte.getDuration()));
	            addEvent(aEvent);
	            validTTEs.incrementAndGet();
	        }
		}
		if (validTTEs.get() == 0)// at least one tte should be valid
			notifyEnd();
	}

	@Override
	protected void end() {		
	}

	@Override
    public void addTimeTableEntry(SimulationCycle cycle, TimeStamp dur, es.ull.isaatc.simulation.core.ResourceType role) {
        timeTable.add(new TimeTableEntry(cycle, dur, role));
    }  

	@Override
    public void addTimeTableEntry(SimulationCycle cycle, TimeStamp dur, ArrayList<es.ull.isaatc.simulation.core.ResourceType> roleList) {
    	for (int i = 0; i < roleList.size(); i++)
            addTimeTableEntry(cycle, dur, roleList.get(i));
    }  
    
	@Override
    public void addTimeTableEntry(SimulationCycle cycle, long dur, es.ull.isaatc.simulation.core.ResourceType role) {
    	addTimeTableEntry(cycle, new TimeStamp(simul.getTimeUnit(), dur), role);
    }  

	@Override
    public void addTimeTableEntry(SimulationCycle cycle, long dur, ArrayList<es.ull.isaatc.simulation.core.ResourceType> roleList) {
    	addTimeTableEntry(cycle, new TimeStamp(simul.getTimeUnit(), dur), roleList);
    }  
    
	@Override
    public void addCancelTableEntry(SimulationCycle cycle, TimeStamp dur, es.ull.isaatc.simulation.core.ResourceType role) {
        cancelPeriodTable.add(new TimeTableEntry(cycle, dur, role));
    }  

	@Override
    public void addCancelTableEntry(SimulationCycle cycle, TimeStamp dur, ArrayList<es.ull.isaatc.simulation.core.ResourceType> roleList) {
    	for (int i = 0; i < roleList.size(); i++)
            addCancelTableEntry(cycle, dur, roleList.get(i));
    }  
    
	@Override
    public void addCancelTableEntry(SimulationCycle cycle, long dur, es.ull.isaatc.simulation.core.ResourceType role) {
    	addCancelTableEntry(cycle, new TimeStamp(simul.getTimeUnit(), dur), role);
    }  

	@Override
    public void addCancelTableEntry(SimulationCycle cycle, long dur, ArrayList<es.ull.isaatc.simulation.core.ResourceType> roleList) {
    	addCancelTableEntry(cycle, new TimeStamp(simul.getTimeUnit(), dur), roleList);
    }
    
    @Override
	public String getObjectTypeIdentifier() {
		return "RES";
	}

	@Override
	public String getDescription() {
		return description;
	}

	/**
	 * Adds a new resource type to the list of current roles. If the list already contains an
	 * entry for the resource type, the greater timestamp is added.
	 * @param role New resource type added
	 * @param ts Timestamp when the availability of this resource finishes for this resource type. 
	 */
	protected void addRole(ResourceType role, long ts) {
		waitSemaphore();
		final Long avEnd = currentRoles.get(role);
		if ((avEnd == null) || (ts > avEnd))
			currentRoles.put(role, ts);
		// Updates AM list
		final ActivityManager am = role.getManager();
		Integer counter = currentAMs.get(am);
		if (counter == null)
			counter = 1;
		else
			counter++;
		currentAMs.put(am, counter);
		signalSemaphore();
	}

	/**
	 * Removes a resource type from the list of current roles. If the role doesn't exist
	 * the removal is silently skipped (that's because a resource can have several timetable 
	 * entries for the same role, but the <code>currentRoles</code> list only contains 
	 * one entry per role). However, checks if it's time for removing the role before doing it.
	 * @param role Resource type removed
	 */
	protected void removeRole(ResourceType role) {
		waitSemaphore();
		final Long avEnd = currentRoles.get(role);
		if (avEnd != null)
			if (avEnd <= ts)
				currentRoles.remove(role);
		// Updates AM list
		final ActivityManager am = role.getManager();
		final Integer counter = currentAMs.get(am);
		if (counter > 1)
			currentAMs.put(am, counter - 1);
		else
			currentAMs.remove(am);
		signalSemaphore();
	}

	/**
	 * Notifies all the activity managers using this resource that it has become available. 
	 */
	protected void notifyCurrentManagers() {
		waitSemaphore();
		for (ActivityManager am : currentAMs.keySet())
			am.notifyResource();
		signalSemaphore();
	}

	/**
	 * Returns <code>true</code> if the resource is being used from multiple activity managers.
	 * @return <code>True</code> if the resource is being used from multiple activity managers; 
	 * <code>false</code> otherwise.
	 */
	protected boolean inSeveralManagers() {
		return (currentAMs.size() > 1);
	}
	
	/**
	 * Tentatively adds this resource to a solution built to carry out an activity. First checks if this 
	 * resource is not being using yet in another solution.
	 * @param rt Resource type to be assigned in this solution 
	 * @param wi Work item trying to catch this resource
	 * @return <code>True</code> if this resource can be used in the solution; <code>false</code> otherwise.
	 */
	protected boolean add2Solution(ResourceType rt, WorkItem wi) {
		if (notCanceled) {
	    	if (inSeveralManagers()) {
	            // Checks if the resource is busy (taken by other element or conflict in the same activity)
	    		waitSemaphore();
	    		// First checks if this resource was previously booked by this element 
	        	if ((currentWI == null) && !isBooked(wi)) {
	            	addBook(wi, rt);
	    	        wi.pushResource(this, true);
		        	// No other element has tried to book this resource
		        	if (currentResourceType == null)
		    	        currentResourceType = rt;
	        		signalSemaphore();
	        		return true;
	        	}
	    		signalSemaphore();
	    	}
	    	else {
	    		// Simply checks if the resource is available and has not been used in another RT of the same activity yet.
	            if (currentResourceType == null) {
	    	        // This resource belongs to the solution...
	    	        currentResourceType = rt;
	    	        wi.pushResource(this, false);    	        
	            	return true;
	            }
	    	}
		}
		return false;
	}
	
	/**
	 * Removes this resource from a solution it was tentatively added to. 
	 * @param wi Work item which won't use this resource to carry out an activity.
	 */
	protected void removeFromSolution(WorkItem wi) {
    	if (inSeveralManagers()) {
    		waitSemaphore();
	        wi.popResource(true);
	        final ResourceType rt = bookList.get(wi);
	        removeBook(wi);
    		if (currentResourceType == rt) {
    			if (bookList.isEmpty())
        			currentResourceType = null;
    			else
    				currentResourceType = bookList.firstEntry().getValue();
    		}
    		signalSemaphore();    		
    	}
    	else {
	        currentResourceType = null;
	        wi.popResource(false);    		
    	}
	}
	
	/**
	 * Checks if this resource, which was tentatively added to a solution, is still valid, i.e. has 
	 * not been used to carry out another activity.
	 * @param wi Work item trying to catch this resource
	 * @return <code>True</code> if this resource is still valid for a solution; <code>false</code> otherwise.
	 */
	protected boolean checkSolution(WorkItem wi) {
		if (inSeveralManagers()) {
			waitSemaphore();
			if (currentWI == null) {
		        currentResourceType = bookList.get(wi);
			}
			else {
				signalSemaphore();
				return false;
			}
			signalSemaphore();
		}
		return true;
	}
	
	/**
	 * Makes a reservation on this resource. This step is required when a resource is being used from several activity
	 * managers.
	 * @param wi The work item booking this resource
	 * @param rt The resource type to be assigned to this resource.
	 */
	protected void addBook(WorkItem wi, ResourceType rt) {
		// First I complete the conflicts list
		if (bookList.size() > 0)
			wi.mergeConflictList(bookList.firstKey());
		bookList.put(wi, rt);
		debug("booked\t" + wi.getElement());
	}
	
	/**
	 * Releases a reservation previously made on this resource. This step is required when a resource is being used 
	 * from several activity managers.
	 * @param wi The work item releasing the book over this resource.
	 */
	protected void removeBook(WorkItem wi) {
		bookList.remove(wi); 
		debug("unbooked\t" + wi.getElement());
	}

	/**
	 * Checks if this resource is currently booked by the specified work item.
	 * @param wi Work item which may have booked this resource
	 * @return <code>True</code> if this resource is currently booked by the specified work item 
	 */
	protected boolean isBooked(WorkItem wi) {
		return bookList.containsKey(wi);
	}

	/**
	 * Definitely takes this resource by setting its current work item. If the resource was booked, the 
	 * reservation is released. 
	 * @param wi The work item which an element is executing
	 * @return The availability timestamp of this resource for this resource type 
	 */
	protected long catchResource(WorkItem wi) {
		setTs(wi.getElement().getTs());
		simul.getInfoHandler().notifyInfo(new ResourceUsageInfo(this.simul, this, currentResourceType, wi, ResourceUsageInfo.Type.CAUGHT, getTs()));
		if (inSeveralManagers()) {
			waitSemaphore();
			removeBook(wi);
			currentWI = wi;
			signalSemaphore();
		}
		else
			currentWI = wi;
		return currentRoles.get(currentResourceType);
	}
	
    /**
     * Releases this resource. If the resource has already expired its availability time, 
     * the <code>timeOut</code> flag is set off. Sets the current work item and the current resource type 
     * to <code>null</code>. 
     * @return True if the resource could be correctly released. False if the availability
     * time of the resource had already expired.
     */
    protected boolean releaseResource() {
		waitSemaphore();
		setTs(simul.getTs());
		simul.getInfoHandler().notifyInfo(new ResourceUsageInfo(this.simul, this, this.getCurrentResourceType(), currentWI, ResourceUsageInfo.Type.RELEASED, getTs()));
        currentWI = null;
        currentResourceType = null;        
        if (timeOut) {
        	timeOut = false;
    		signalSemaphore();
        	return false;
        }
		signalSemaphore();
        return true;
    }
    
    /**
     * Generates an event which finalizes a period of unavailability.
     * @param ts Current simulation time.
     * @param duration Duration of the unavailability period.
     */
    public void generateCancelPeriodOffEvent(long ts, long duration) {
    	CancelPeriodOffEvent aEvent = new CancelPeriodOffEvent(ts + duration, null, 0);
        addEvent(aEvent);
    }
    
    /**
     * Returns the work item of the element which currently owns this resource.
     * @return The current work item.
     */
    protected WorkItem getCurrentWI() {
        return currentWI;
    }
    
    /**
     * Returns the current resource type this resource is being used for.
     * @return Current resource type of this resource
     */
    public ResourceType getCurrentResourceType() {
        return currentResourceType;
    }
    
    /**
     * Returns <code>true</code> if this resource is being used in spite of having finished its availability.
     * @return <code>True</code> if this resource is being used in spite of having finished its availability;
     * <code>false</code> otherwise.
     */
    protected boolean isTimeOut() {
        return timeOut;
    }
    
    /**
     * Sets the state of this resource as being used in spite of having finished its availability. 
     * @param timeOut <code>True</code> if this resource is being used beyond its availability; 
     * <code>false</code> otherwise.
     */
    protected void setTimeOut(boolean timeOut) {
        this.timeOut = timeOut;
    }
    
    /**
     * Returns the availability of this resource for the specified resource type.
     * @param rt Resource type
     * @return The availability of this resource for the specified resource type; 
     * <code>null</code> if the resource is not available for this resource type.
     */
    protected Long getAvailability(ResourceType rt) {
    	return currentRoles.get(rt); 
    }
    
    /**
     * An event to make available this resource with a specific role. 
     */
    public final class RoleOnEvent extends BasicElement.DiscreteEvent {
        /** Available role */
        private final ResourceType role;
        /** Cycle iterator */
        private final DiscreteCycleIterator iter;
        /** Availability duration */
        private final long duration;
        
        /**
         * Creates a new event
         * @param ts Timestamp when the resource will  be available.
         * @param role Role played by the resource.
         * @param iter The cycle iterator that handles the availability of this resource
         * @param duration The duration of the availability.
         */        
        public RoleOnEvent(long ts, ResourceType role, DiscreteCycleIterator iter, long duration) {
            super(ts);
            this.iter = iter;
            this.role = role;
            this.duration = duration;
        }
        
        @Override
        public void event() {
        	final long waitTime = role.beforeRoleOn();
        	if (waitTime == 0) {
        		simul.getInfoHandler().notifyInfo(new ResourceInfo(Resource.this.simul, Resource.this, role, ResourceInfo.Type.ROLON, ts));
        		debug("Resource available\t" + role);
        		role.incAvailable(Resource.this);
        		addRole(role, ts + duration);
        		// The activity manger is informed of new available resources
        		
        		role.getManager().notifyResource();
        		role.afterRoleOn();
        		RoleOffEvent rEvent = new RoleOffEvent(ts + duration, role, iter, duration);
        		addEvent(rEvent);
        	} else {
        		RoleOnEvent rEvent = new RoleOnEvent(ts + waitTime, role, iter, duration);
        		addEvent(rEvent);
        	}
        }
    }
    
    /**
     * An event to make unavailable this resource with a specific role. 
     */
    public final class RoleOffEvent extends BasicElement.DiscreteEvent {
        /** Unavailable role */
        private final ResourceType role;
        /** Cycle iterator */
        private final DiscreteCycleIterator iter;
        /** Availability duration */
        private final long duration;
        
        /**
         * Creates a new event
         * @param ts Timestamp when the resource will be unavailable.
         * @param role Role played by the resource.
         * @param iter The cycle iterator that handles the availability of this resource
         * @param duration The duration of the availability.
         */        
        public RoleOffEvent(long ts, ResourceType role, DiscreteCycleIterator iter, long duration) {
            super(ts);
            this.role = role;
            this.iter = iter;
            this.duration = duration;
        }
        
        @Override
        public void event() {
        	final long waitTime = role.beforeRoleOff();
        	if (waitTime == 0) {
        		simul.getInfoHandler().notifyInfo(new ResourceInfo(Resource.this.simul, Resource.this, role, ResourceInfo.Type.ROLOFF, ts));
        		role.decAvailable(Resource.this);
        		removeRole(role);
        		debug("Resource unavailable\t" + role);
        		final long nextTs = iter.next();
    	        if (nextTs != -1) {
        			RoleOnEvent rEvent = new RoleOnEvent(nextTs, role, iter, duration);
        			addEvent(rEvent);            	
        		}
        		else if (validTTEs.decrementAndGet() == 0) {
        			role.afterRoleOff();
        			notifyEnd();
        		}
        	} else {
        		RoleOffEvent rEvent = new RoleOffEvent(ts + waitTime, role, iter, duration);
        		addEvent(rEvent);
        	}
        }
    }

	/**
	 * Event which opens a cancellation period for this resource
	 * @author ycallero
	 *
	 */
	public final class CancelPeriodOnEvent extends BasicElement.DiscreteEvent {
		/** Cycle iterator */
		private final DiscreteCycleIterator iter;
		/** Duration of the availability */
		private final long duration;

		/**
		 * Creates a new CancelPeriodOnEvent.
		 * @param ts Actual simulation time.
		 * @param iter Cycle iterator.
		 * @param duration Event duration.
		 */
		public CancelPeriodOnEvent(long ts, DiscreteCycleIterator iter, long duration) {
			super(ts);
			this.iter = iter;
			this.duration = duration;
		}

		@Override
		public void event() {
			simul.getInfoHandler().notifyInfo(new ResourceInfo(Resource.this.simul, Resource.this, currentResourceType, ResourceInfo.Type.CANCELON, ts));
			// FIXME: Habría que controlar el acceso concurrente a esta variable. Puede dar problemas.
			setNotCanceled(false);
			CancelPeriodOffEvent aEvent = new CancelPeriodOffEvent(ts + duration, iter, duration);
			addEvent(aEvent);
		}

	}

	/**
	 * Event which closes a cancellation period for this resource
	 * @author ycallero
	 *
	 */
	public final class CancelPeriodOffEvent extends BasicElement.DiscreteEvent {
		/** Cycle iterator */
		private final DiscreteCycleIterator iter;
		/** Duration of the availability */
		private final long duration;

		/**
		 * Creates a new CancelPeriodOffEvent.
		 * @param ts Actual simulation time.
		 * @param iter Cycle iterator.
		 * @param duration The event duration.
		 */   
		public CancelPeriodOffEvent(long ts, DiscreteCycleIterator iter, long duration) {
			super(ts);
			this.iter = iter;
			this.duration = duration;
		}

		@Override
		public void event() {
			simul.getInfoHandler().notifyInfo(new ResourceInfo(Resource.this.simul, Resource.this, currentResourceType,ResourceInfo.Type.CANCELOFF, ts));
			// FIXME: Habría que controlar el acceso concurrente a esta variable. Puede dar problemas.
			setNotCanceled(true);
			notifyCurrentManagers();
			long nextTs = -1;
			if (iter != null)
				nextTs = iter.next();
	        if (nextTs != -1) {
				CancelPeriodOnEvent aEvent = new CancelPeriodOnEvent(nextTs, iter, duration);
				addEvent(aEvent);            	
			}
		}
	}
	
	/**
	 * Sets the available flag of a resource.
	 * @param available The availability state of the resource.
	 * @param ts
	 */
	protected void setNotCanceled(boolean available) {
		notCanceled = available;
	}
	
	public TreeMap<ResourceType, Long> getCurrentRoles() {
		return currentRoles;
	}

	@Override
	public Collection<TimeTableEntry> getTimeTableEntries() {
		return timeTable;
	}
	
}
