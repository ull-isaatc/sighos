package es.ull.isaatc.simulation;

import java.util.ArrayList;
import java.util.TreeSet;

import es.ull.isaatc.simulation.info.ResourceInfo;
import es.ull.isaatc.simulation.info.ResourceUsageInfo;
import es.ull.isaatc.simulation.state.RecoverableState;
import es.ull.isaatc.simulation.state.ResourceState;
import es.ull.isaatc.util.Cycle;
import es.ull.isaatc.util.CycleIterator;

/**
 * A resource is an element that becomes available at a specific simulation time and 
 * becomes unavailable at other simulation time. The availability of a resource is controlled
 * by means of timetable entries, which define a resource type and an availability cycle.
 * A resource finishes its execution when it has no longer valid timetable entries.
 * @author Carlos Martín Galán
 */
public class Resource extends BasicElement implements RecoverableState<ResourceState> {
	/** Timetable which defines the availability estructure of the resource */
    protected ArrayList<TimeTableEntry> timeTable;
    /** A brief description of the resource */
    protected String description;
    /** If true, indicates that this resource is being used after its availability time has expired */
    private boolean timeOut = false;
    /** List of currently active roles */
    protected ArrayList<ResourceType> currentRoles;
    /** A counter of the valid timetable entries which this resource is following. */
    private int validTTEs = 0;
    /** The resource type which this resource is being booked for */
    protected ResourceType currentResourceType = null;
    /** Single Flow which currently has got this resource */
    protected SingleFlow currentSF = null;
    /** List of elements trying to book this resource */
    protected TreeSet<SingleFlow> bookList;

    /**
     * Creates a new instance of Resource.
     * @param id This resource's identifier.
     * @param simul Simulation this resource is attached to.
     * @param description A short text describing this resource.
     */
	public Resource(int id, Simulation simul, String description) {
		super(id, simul);
		this.description = description;
        timeTable = new ArrayList<TimeTableEntry>();
        currentRoles = new ArrayList<ResourceType>();
        bookList = new TreeSet<SingleFlow>();
        simul.add(this);
	}

	@Override
    protected void init() {
    	simul.notifyListeners(new ResourceInfo(this, ResourceInfo.Type.START, ts, timeTable.size()));
		for (int i = 0 ; i < timeTable.size(); i++) {
			TimeTableEntry tte = timeTable.get(i);
	        CycleIterator iter = tte.iterator(tte.getRole().getTs(), simul.getEndTs());
	        double nextTs = iter.next();
	        if (!Double.isNaN(nextTs)) {
	            RoleOnEvent rEvent = new RoleOnEvent(nextTs, tte.getRole(), iter, tte.getDuration());
	            addEvent(rEvent);
	            validTTEs++;
	        }
		}
		if (validTTEs == 0)// at least one tte should be valid
			notifyEnd();
	}

	@Override
	protected void end() {		
	}
	
    /**
     * Adds a new entry with a single role.
     * @param cycle Cycle that characterizes this entry
     * @param dur The long this resource plays this role every cycle
     * @param role Role that the resource plays during this cycle
     */
    public void addTimeTableEntry(Cycle cycle, double dur, ResourceType role) {
        timeTable.add(new TimeTableEntry(cycle, dur, role));
    }  

    /**
     * Adds a new entry with a several roles.
     * @param cycle Cycle that characterizes this entry
     * @param dur The long this resource plays this role every cycle
     * @param roleList Roles that the resource play during this cycle
     */
    public void addTimeTableEntry(Cycle cycle, double dur, ArrayList<ResourceType> roleList) {
    	for (int i = 0; i < roleList.size(); i++)
            addTimeTableEntry(cycle, dur, roleList.get(i));
    }  
    
    @Override
	public String getObjectTypeIdentifier() {
		return "RES";
	}

	/**
	 * Adds a new resource type to the list of current roles.
	 * @param role New resource type added
	 */
	protected void addRole(ResourceType role) {
		debug("MUTEX\trequesting\t(add role)");    	
		waitSemaphore();
		debug("MUTEX\tadquired\t(add role)");    	
		currentRoles.add(role);
		debug("MUTEX\treleasing\t(add role)");    	
		signalSemaphore();
		debug("MUTEX\tfreed\t(add role)");    	
	}

	/**
	 * Removes a resource type from the list of current roles.
	 * @param role Resource type removed
	 */
	protected void removeRole(ResourceType role) {
		debug("MUTEX\trequesting\t(remove role)");    	
		waitSemaphore();
		debug("MUTEX\tadquired\t(remove role)");    	
		currentRoles.remove(role);
		debug("MUTEX\treleasing\t(remove role)");    	
		signalSemaphore();
		debug("MUTEX\tfreed\t(remove role)");    	
	}

	/**
	 * Builds a list of activity managers referenced by the roles of the resource. 
	 * @return Returns the currentManagers.
	 */
	public ArrayList<ActivityManager> getCurrentManagers() {
		debug("MUTEX\trequesting\t(get man)");    	
		waitSemaphore();
		debug("MUTEX\tadquired\t(get man)");    	
		ArrayList <ActivityManager> currentManagers = new ArrayList<ActivityManager>();
		for (ResourceType role : currentRoles)
			if (!currentManagers.contains(role.getManager()))
				currentManagers.add(role.getManager());
		debug("MUTEX\treleasing\t(get man)");    	
		signalSemaphore();
		debug("MUTEX\tfreed\t(get man)");    	
		return currentManagers;
	}

	/**
	 * An element books this resource. The element is simply included in the book list
	 * of this resource.
	 * @param sf The single flow booking this resource
	 * @return False if the element has already booked this resource (in the same activity).
	 * True in other case. 
	 */
	protected boolean addBook(SingleFlow sf) {
		debug("MUTEX\trequesting\t" + sf.getElement() + "(add book)");    	
		waitSemaphore();
		debug("MUTEX\tadquired\t" + sf.getElement() + "(add book)");    	
		// First I complete the conflicts list
		if (bookList.size() > 0)
			sf.mergeConflictList(bookList.first());
		boolean result = bookList.add(sf);
		debug("booked\t" + sf.getElement());
		debug("MUTEX\treleasing\t" + sf.getElement() + " (add book)");    	
		signalSemaphore();
		debug("MUTEX\tfreed\t" + sf.getElement() + " (add book)");    	
		return result;
	}
	
	/**
	 * An element releases the book over this resource. This, the element is removed from the 
	 * book list of this resource.
	 * @param sf The single flow releasing the book over this resource.
	 */
	protected void removeBook(SingleFlow sf) {
		debug("MUTEX\trequesting\t" + sf.getElement() + "(remove book)");    	
		waitSemaphore();
		debug("MUTEX\tadquired\t" + sf.getElement() + "(remove book)");    	
		bookList.remove(sf); 
		debug("unbooked\t" + sf.getElement());
		debug("MUTEX\treleasing\t" + sf.getElement() + " (remove book)");    	
		signalSemaphore();
		debug("MUTEX\tfreed\t" + sf.getElement() + " (remove book)");    	
	}

	/**
	 * Marks this resource as taken by an element. Sets the current Single flow, and the
	 * current resource type; and adds this resource to the caugt-resources list of the single flow.
	 * A "taken" element continues being booked. The book is released when the resource itself is
	 * released. 
	 * @param sf The single flow which an element is executing
	 * @param rt The role this resource has been taken for.
	 */
	protected void catchResource(SingleFlow sf, ResourceType rt) {
		debug("MUTEX\trequesting\t" + sf.getElement() + "(catch res.)");    	
		waitSemaphore();
		debug("MUTEX\tadquired\t" + sf.getElement() + "(catch res.)");    	
		// FIXME: Es esto o debería cogerlo del LP?
		setTs(sf.getElement().getTs());
        simul.notifyListeners(new ResourceUsageInfo(Resource.this, ResourceUsageInfo.Type.CAUGHT, ts, sf.getElement().getIdentifier(), rt.getIdentifier()));
		currentSF = sf;
		sf.addCaughtResource(this);
		currentResourceType = rt;
//		bookList.clear();
		debug("MUTEX\treleasing\t" + sf.getElement() + " (catch res.)");    	
		signalSemaphore();
		debug("MUTEX\tfreed\t" + sf.getElement() + " (catch res.)");    	
	}
	
    /**
     * Releases this resource. If the resource has already expired its availability time, 
     * the timeOut flag is set off. Sets the current single flow and the current resource type 
     * to <code>null</code>. The book of the resource is released too.
     * @return True if the resource could be correctly released. False if the availability
     * time of the resource had already expired.
     */
    protected boolean releaseResource() {
		debug("MUTEX\trequesting\t" + currentSF.getElement() + "(rel. res.)");    	
		waitSemaphore();
		debug("MUTEX\tadquired\t" + currentSF.getElement() + "(rel. res.)");    	
		// FIXME: Es esto o debería cogerlo del LP?
		setTs(currentSF.getElement().getTs());
        simul.notifyListeners(new ResourceUsageInfo(Resource.this, ResourceUsageInfo.Type.RELEASED, ts, currentSF.getElement().getIdentifier(), currentResourceType.getIdentifier()));
        // The book is removed
		bookList.remove(currentSF); 
		debug("unbooked\t" + currentSF.getElement());
        currentSF = null;
        currentResourceType = null;        
        if (timeOut) {
        	timeOut = false;
    		debug("MUTEX\treleasing\t" + "(rel. res.)");    	
    		signalSemaphore();
    		debug("MUTEX\tfreed\t" + "(rel. res.)");    	
        	return false;
        }
		debug("MUTEX\treleasing\t" + "(rel. res.)");    	
		signalSemaphore();
		debug("MUTEX\tfreed\t" + "(rel. res.)");    	
        return true;
    }
    
    /**
     * Returns the single flow of the element which currently owns this resource.
     * @return The current single flow.
     */
    protected SingleFlow getCurrentSF() {
        return currentSF;
    }
    
    /**
     * Getter for property currentResourceType.
     * @return Value of property currentResourceType.
     */
    protected ResourceType getCurrentResourceType() {
        return currentResourceType;
    }
    
    /**
     * Setter for property currentResourceType.
     * @param cr New value of property currentResourceType.
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
     * @param fueraTiempo New value of property timeOut.
     */
    protected void setTimeOut(boolean timeOut) {
        this.timeOut = timeOut;
    }
    
    /**
     * Makes available a resource with a specific role. 
     */
    public class RoleOnEvent extends BasicElement.DiscreteEvent {
        /** Available role */
        ResourceType role;
        /** Cycle iterator */
        CycleIterator iter;
        /** Availability duration */
        double duration;
        
        /**
         * Creates a new event
         * @param ts Timestamp when the resource will  be available.
         * @param role Role played by the resource.
         * @param iter The cycle iterator that handles the availability of this resource
         * @param duration The duration of the availability.
         */        
        RoleOnEvent(double ts, ResourceType role, CycleIterator iter, double duration) {
            super(ts, role.getManager().getLp());
            this.iter = iter;
            this.role = role;
            this.duration = duration;
        }
        
        @Override
        public void event() {
            simul.notifyListeners(new ResourceInfo(Resource.this, ResourceInfo.Type.ROLON, ts, role.getIdentifier()));
            debug("Resource available\t" + role);
            // Beginning MUTEX access to activity manager
            role.getManager().waitSemaphore();
            role.incAvailable(Resource.this);
            // The activity manger is informed of new available resources
            role.getManager().availableResource(); 
            // Ending MUTEX access to activity manager
            role.getManager().signalSemaphore();
            addRole(role);
            RoleOffEvent rEvent = new RoleOffEvent(ts + duration, role, iter, duration);
            addEvent(rEvent);
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
        ResourceType role;
        /** Cycle iterator */
        CycleIterator iter;
        /** Availability duration */
        double duration;
        
        /**
         * Creates a new event
         * @param ts Timestamp when the resource will be unavailable.
         * @param role Role played by the resource.
         * @param iter The cycle iterator that handles the availability of this resource
         * @param duration The duration of the availability.
         */        
        RoleOffEvent(double ts, ResourceType role, CycleIterator iter, double duration) {
            super(ts, role.getManager().getLp());
            this.role = role;
            this.iter = iter;
            this.duration = duration;
        }
        
        @Override
        public void event() {
            simul.notifyListeners(new ResourceInfo(Resource.this, ResourceInfo.Type.ROLOFF, ts, role.getIdentifier()));
            // Beginning MUTEX access to activity manager
            role.getManager().waitSemaphore();
            role.decAvailable(Resource.this);
            // Ending MUTEX access to activity manager
            role.getManager().signalSemaphore();        
            // MOD 22/05/06
            removeRole(role);
            debug("Resource unavailable\t" + role);
            double nextTs = iter.next();
            if (!Double.isNaN(nextTs)) {
                RoleOnEvent rEvent = new RoleOnEvent(nextTs, role, iter, duration);
                addEvent(rEvent);            	
            }
            else if (--validTTEs == 0)
        		notifyEnd();
        }

		/**
		 * @return Returns the role.
		 */
		public ResourceType getRole() {
			return role;
		}        
    }

    /**
     * Represents the role that a resource plays at a specific time cycle.
     * @author Iván Castilla Rodríguez
     */
    class TimeTableEntry {
    	/** Cycle that characterizes this entry */
    	protected Cycle cycle;
        /** The long this resource plays this role every cycle */
    	protected double duration;
        /** Role that the resource plays during this cycle */
        protected ResourceType role;
        
        /** Creates a new instance of TimeTableEntry
         * @param cycle 
         * @param dur The long this resource plays this role every cycle
         * @param role Role that the resource plays during this cycle
         */
    	public TimeTableEntry(Cycle cycle, double dur, ResourceType role) {
    		this.cycle = cycle;
    		this.duration = dur;
    		this.role = role;
    	}
        
        /**
         * Getter for property duration.
         * @return Value of property duration.
         */
        public double getDuration() {
            return duration;
        }

        /**
         * Returns an iterator over the cycle defined for this timetable entry. 
         * @param startTs Absolute start timestamp
         * @param endTs Absolute end timestamp
         * @return An iterator over the cycle defined for this timetable entry.
         */
        public CycleIterator iterator(double startTs, double endTs) {
        	return cycle.iterator(startTs, endTs);
        }
        
        /**
         * Getter for property role.
         * @return Value of property role.
         */
        public ResourceType getRole() {
            return role;
        }
        
        @Override
        public String toString() {
            StringBuffer str = new StringBuffer();
            str.append(" | " + role.getDescription() + " | " + duration
                + " | " + cycle + "\r\n");
            return str.toString();
        }
        
    }

    /**
     * Returns the state of this resource. The state of a resource consists on the amount of
     * valid timetable entries, the current single flow (if it exists), and the current roles.
     * @return The state of this resource.
     */
	public ResourceState getState() {
		ResourceState state = null;
		if (currentSF == null)
			state = new ResourceState(id, validTTEs);
		else
			state = new ResourceState(id, validTTEs, currentSF.getIdentifier(), currentSF.getElement().getIdentifier(), currentResourceType.getIdentifier(), timeOut);
		for (ResourceType rt : currentRoles)
			state.add(rt.getIdentifier());
		return state;
	}

    /**
     * Sets the state of this resource. The state of a resource consists on the amount of
     * valid timetable entries, the current single flow (if it exists), and the current roles.
     * @param state The state of this resource.
     */
	public void setState(ResourceState state) {
		validTTEs = state.getValidTTEs();
		if (state.getCurrentElemId() != -1) {
			Element elem = simul.getActiveElement(state.getCurrentElemId());
			currentSF = elem.searchSingleFlow(state.getCurrentSFId());
			currentResourceType = simul.getResourceType(state.getCurrentRTId());
			for (Integer rtId : state.getCurrentRoles())
				currentRoles.add(simul.getResourceType(rtId));
		}
	}
}
