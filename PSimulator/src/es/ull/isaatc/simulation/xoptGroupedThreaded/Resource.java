package es.ull.isaatc.simulation.xoptGroupedThreaded;

import java.util.ArrayList;
import java.util.Collection;
import java.util.TreeMap;
import java.util.concurrent.atomic.AtomicInteger;

import es.ull.isaatc.simulation.common.SimulationCycle;
import es.ull.isaatc.simulation.common.TimeStamp;
import es.ull.isaatc.simulation.common.TimeTableEntry;
import es.ull.isaatc.simulation.common.info.ResourceInfo;
import es.ull.isaatc.simulation.common.info.ResourceUsageInfo;
import es.ull.isaatc.util.DiscreteCycleIterator;

/**
 * A resource is an element that becomes available at a specific simulation time and 
 * becomes unavailable at other simulation time. The availability of a resource is controlled
 * by means of timetable entries, which define a resource type and an availability cycle.
 * A resource finishes its execution when it has no longer valid timetable entries.
 * TODO Comment
 * @author Carlos Martn Galn
 */
public class Resource extends BasicElement implements es.ull.isaatc.simulation.common.Resource {
	/** Timetable which defines the availability estructure of the resource. Define RollOn and RollOff events. */
    protected final ArrayList<TimeTableEntry> timeTable = new ArrayList<TimeTableEntry>();
    /** A brief description of the resource */
    protected final String description;
    /** If true, indicates that this resource is being used after its availability time has expired */
    private boolean timeOut = false;
    /** List of currently active roles and the timestamp which marks the end of their availability time. */
    protected final TreeMap<ResourceType, Long> currentRoles = new TreeMap<ResourceType, Long>();
    /** A counter of the valid timetable entries which this resource is following. */
    private final AtomicInteger validTTEs = new AtomicInteger();
    /** The resource type which this resource is being booked for */
    protected ResourceType currentResourceType = null;
    /** Work item which currently holds this resource */
    protected WorkItem currentWI = null;
    /** List of elements trying to book this resource */
    protected final TreeMap<WorkItem, ResourceType> bookList = new TreeMap<WorkItem, ResourceType>();
    /** Availability flag */
    protected volatile boolean notCanceled = true;
    protected TreeMap<ActivityManager, Integer> currentAMs = new TreeMap<ActivityManager, Integer>();

    /**
     * Creates a new instance of Resource.
     * @param id This resource's identifier.
     * @param simul Simulation this resource is attached to.
     * @param description A short text describing this resource.
     */
	public Resource(int id, Simulation simul, String description) {
		super(id, simul);
		this.description = description;
        simul.add(this);
	}

	/*
	 * (non-Javadoc)
	 * @see es.ull.isaatc.simulation.BasicElement#init()
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
		if (validTTEs.get() == 0)// at least one tte should be valid
			notifyEnd();
	}

	/*
	 * (non-Javadoc)
	 * @see es.ull.isaatc.simulation.BasicElement#end()
	 */
	@Override
	protected void end() {		
	}
	
    /**
     * Adds a new entry with a single role.
     * @param cycle Cycle that characterizes this entry
     * @param dur The long this resource plays this role every cycle
     * @param role Role that the resource plays during this cycle
     */
    public void addTimeTableEntry(SimulationCycle cycle, TimeStamp dur, es.ull.isaatc.simulation.common.ResourceType role) {
        timeTable.add(new TimeTableEntry(cycle, dur, role));
    }  

    /**
     * Adds a new entry with a several roles.
     * @param cycle Cycle that characterizes this entry
     * @param dur The long this resource plays this role every cycle
     * @param roleList Roles that the resource play during this cycle
     */
    public void addTimeTableEntry(SimulationCycle cycle, TimeStamp dur, ArrayList<es.ull.isaatc.simulation.common.ResourceType> roleList) {
    	for (int i = 0; i < roleList.size(); i++)
            addTimeTableEntry(cycle, dur, roleList.get(i));
    }  
    
    /**
     * Adds a new entry with a single role.
     * @param cycle Cycle that characterizes this entry
     * @param dur The long this resource plays this role every cycle expressed in the 
     * simulation time unit
     * @param role Role that the resource plays during this cycle
     */
    public void addTimeTableEntry(SimulationCycle cycle, long dur, es.ull.isaatc.simulation.common.ResourceType role) {
    	addTimeTableEntry(cycle, new TimeStamp(simul.getTimeUnit(), dur), role);
    }  

    /**
     * Adds a new entry with a several roles.
     * @param cycle Cycle that characterizes this entry
     * @param dur The long this resource plays this role every cycle expressed in the 
     * simulation time unit
     * @param roleList Roles that the resource play during this cycle
     */
    public void addTimeTableEntry(SimulationCycle cycle, long dur, ArrayList<es.ull.isaatc.simulation.common.ResourceType> roleList) {
    	addTimeTableEntry(cycle, new TimeStamp(simul.getTimeUnit(), dur), roleList);
    }  
    
    @Override
	public String getObjectTypeIdentifier() {
		return "RES";
	}

    /*
     * (non-Javadoc)
     * @see es.ull.isaatc.simulation.Describable#getDescription()
     */
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
		Long avEnd = currentRoles.get(role);
		if ((avEnd == null) || (ts > avEnd))
			currentRoles.put(role, ts);
		// Updates AM list
		ActivityManager am = role.getManager();
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
		Long avEnd = currentRoles.get(role);
		if (avEnd != null)
			if (avEnd <= ts)
				currentRoles.remove(role);
		// Updates AM list
		ActivityManager am = role.getManager();
		Integer counter = currentAMs.get(am);
		if (counter > 1)
			currentAMs.put(am, counter - 1);
		else
			currentAMs.remove(am);
		signalSemaphore();
	}

	/**
	 * Builds a list of activity managers referenced by the roles of the resource. 
	 * @return Returns the currentManagers.
	 */
	public void notifyCurrentManagers() {
		waitSemaphore();
		for (ActivityManager am : currentAMs.keySet())
			am.notifyResource();
		signalSemaphore();
	}

	protected boolean inSeveralManagers() {
		return (currentAMs.size() > 1);
	}
	
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
	
	protected void removeFromSolution(WorkItem wi) {
    	if (inSeveralManagers()) {
    		waitSemaphore();
	        wi.popResource(true);
	        ResourceType rt = bookList.get(wi);
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
	
	protected boolean checkSolution(WorkItem wi) {
		if (inSeveralManagers()) {
			waitSemaphore();
			if (currentWI == null) {
		        ResourceType rt = bookList.get(wi);
		        currentResourceType = rt;
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
	 * An element books this resource. The element is simply included in the book list
	 * of this resource.
	 * @param wi The work item booking this resource
	 */
	protected void addBook(WorkItem wi, ResourceType rt) {
		// First I complete the conflicts list
		if (bookList.size() > 0)
			wi.mergeConflictList(bookList.firstKey());
		bookList.put(wi, rt);
		debug("booked\t" + wi.getElement());
	}
	
	/**
	 * An element releases the book over this resource. This, the element is removed from the 
	 * book list of this resource.
	 * @param wi The work item releasing the book over this resource.
	 */
	protected void removeBook(WorkItem wi) {
		bookList.remove(wi); 
		debug("unbooked\t" + wi.getElement());
	}

	/**
	 * Checks if the resource is currently booked by the specified single flow
	 * @param sf Single flow which can have booked the resource
	 * @return True if this resource is currently booked by the specified single flow 
	 */
	protected boolean isBooked(WorkItem wi) {
		return bookList.containsKey(wi);
	}

	/**
	 * Marks this resource as taken by an element. Sets the current work item, and the
	 * current resource type; and adds this resource to the item's caught resources list.
	 * A "taken" element continues being booked. The book is released when the resource itself is
	 * released. 
	 * @param wi The work item which an element is executing
	 * @param rt The role this resource has been taken for.
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
     * the timeOut flag is set off. Sets the current work item and the current resource type 
     * to <code>null</code>. The book of the resource is released too.
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
     * @param ts Actual simulation time.
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
     * Getter for property currentResourceType.
     * @return Value of property currentResourceType.
     */
    public ResourceType getCurrentResourceType() {
        return currentResourceType;
    }
    
    /**
     * Setter for property currentResourceType.
     * @param rt New value of property currentResourceType.
     */
    protected void setCurrentResourceType(ResourceType rt) {
        this.currentResourceType = rt;
    }
    
    /**
     * Getter for property timeOut.
     * @return Value of property timeOut.
     */
    protected boolean isTimeOut() {
        return timeOut;
    }
    
    /**
     * Setter for property timeOut.
     * @param timeOut New value of property timeOut.
     */
    protected void setTimeOut(boolean timeOut) {
        this.timeOut = timeOut;
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
    
    /**
     * Makes available a resource with a specific role. 
     */
    public class RoleOnEvent extends BasicElement.DiscreteEvent {
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
        	long waitTime = role.beforeRoleOn();
        	if (waitTime == 0.0) {
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


		/**
		 * @return Returns the role.
		 */
		public ResourceType getRole() {
			return role;
		}
    }
    
    /**
     * Makes unavailable a resource with a specific role. 
     */
    public class RoleOffEvent extends BasicElement.DiscreteEvent {
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
        	long waitTime = role.beforeRoleOff();
        	if (waitTime == 0.0) {
        		simul.getInfoHandler().notifyInfo(new ResourceInfo(Resource.this.simul, Resource.this, role, ResourceInfo.Type.ROLOFF, ts));
        		role.decAvailable(Resource.this);
        		removeRole(role);
        		debug("Resource unavailable\t" + role);
        		long nextTs = iter.next();
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

		/**
		 * @return Returns the role.
		 */
		public ResourceType getRole() {
			return role;
		}        
    }

	/**
	 * Event which opens a cancellation period for this resource
	 * @author ycallero
	 *
	 */
	public class CancelPeriodOnEvent extends BasicElement.DiscreteEvent {
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
			// FIXME: Habr�a que controlar el acceso concurrente a esta variable. Puede dar problemas.
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
	public class CancelPeriodOffEvent extends BasicElement.DiscreteEvent {
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
			// FIXME: Habr�a que controlar el acceso concurrente a esta variable. Puede dar problemas.
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
	public void setNotCanceled(boolean available) {
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