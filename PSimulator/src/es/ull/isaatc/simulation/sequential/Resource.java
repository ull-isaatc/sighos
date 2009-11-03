package es.ull.isaatc.simulation.sequential;

import java.util.ArrayList;
import java.util.Collection;
import java.util.TreeMap;

import es.ull.isaatc.simulation.common.ModelCycle;
import es.ull.isaatc.simulation.common.Time;
import es.ull.isaatc.simulation.common.TimeTableEntry;
import es.ull.isaatc.simulation.sequential.info.ResourceInfo;
import es.ull.isaatc.simulation.sequential.info.ResourceUsageInfo;
import es.ull.isaatc.util.CycleIterator;

/**
 * A resource is an element that becomes available at a specific simulation time and 
 * becomes unavailable at other simulation time. The availability of a resource is controlled
 * by means of timetable entries, which define a resource type and an availability cycle.
 * A resource finishes its execution when it has no longer valid timetable entries.
 * @author Carlos Mart�n Gal�n
 */
public class Resource extends BasicElement implements es.ull.isaatc.simulation.common.Resource {
	/** Timetable which defines the availability estructure of the resource. Define RollOn and RollOff events. */
    protected final ArrayList<TimeTableEntry> timeTable;
    /** A brief description of the resource */
    protected final String description;
    /** If true, indicates that this resource is being used after its availability time has expired */
    private boolean timeOut = false;
    /** List of currently active roles and the timestamp which marks the end of their availibity time. */
    protected final TreeMap<ResourceType, Double> currentRoles;
    /** A counter of the valid timetable entries which this resource is following. */
    private int validTTEs = 0;
    /** The resource type which this resource is being booked for */
    protected ResourceType currentResourceType = null;
    /** Work item which currently holds this resource */
    protected WorkItem currentWI = null;
    /** Availability flag */
    protected boolean notCanceled;

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
        currentRoles = new TreeMap<ResourceType, Double>();
        notCanceled = true;
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
	        CycleIterator iter = tte.getCycle().getCycle().iterator(getTs(), simul.getInternalEndTs());
	        double nextTs = iter.next();
	        if (!Double.isNaN(nextTs)) {
	            RoleOnEvent rEvent = new RoleOnEvent(nextTs, (ResourceType) tte.getRole(), iter, simul.simulationTime2Double(tte.getDuration()));
	            addEvent(rEvent);
	            validTTEs++;
	        }
		}
		if (validTTEs == 0)// at least one tte should be valid
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
    public void addTimeTableEntry(ModelCycle cycle, Time dur, ResourceType role) {
        timeTable.add(new TimeTableEntry(cycle, dur, role));
    }  

    /**
     * Adds a new entry with a several roles.
     * @param cycle Cycle that characterizes this entry
     * @param dur The long this resource plays this role every cycle
     * @param roleList Roles that the resource play during this cycle
     */
    public void addTimeTableEntry(ModelCycle cycle, Time dur, ArrayList<ResourceType> roleList) {
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
    public void addTimeTableEntry(ModelCycle cycle, double dur, ResourceType role) {
    	addTimeTableEntry(cycle, new Time(simul.getTimeUnit(), dur), role);
    }  

    /**
     * Adds a new entry with a several roles.
     * @param cycle Cycle that characterizes this entry
     * @param dur The long this resource plays this role every cycle expressed in the 
     * simulation time unit
     * @param roleList Roles that the resource play during this cycle
     */
    public void addTimeTableEntry(ModelCycle cycle, double dur, ArrayList<ResourceType> roleList) {
    	addTimeTableEntry(cycle, new Time(simul.getTimeUnit(), dur), roleList);
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
	protected void addRole(ResourceType role, double ts) {
		Double avEnd = currentRoles.get(role);
		if ((avEnd == null) || (ts > avEnd))
			currentRoles.put(role, ts);
	}

	/**
	 * Removes a resource type from the list of current roles. If the role doesn't exist
	 * the removal is silently skipped (that's because a resource can have several timetable 
	 * entries for the same role, but the <code>currentRoles</code> list only contains 
	 * one entry per role). However, checks if it's time for removing the role before doing it.
	 * @param role Resource type removed
	 */
	protected void removeRole(ResourceType role) {
		Double avEnd = currentRoles.get(role);
		if (avEnd != null)
			if (avEnd <= ts)
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
	 * @param wi The work item which an element is executing
	 * @return The availability timestamp of this resource for this resource type 
	 */
	protected double catchResource(WorkItem wi) {
		setTs(wi.getElement().getTs());
		simul.getInfoHandler().notifyInfo(new ResourceUsageInfo(this.simul, this, currentResourceType, wi, ResourceUsageInfo.Type.CAUGHT, getTs()));
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
		setTs(defLP.getTs());
		simul.getInfoHandler().notifyInfo(new ResourceUsageInfo(this.simul, this, this.getCurrentResourceType(), currentWI, ResourceUsageInfo.Type.RELEASED, getTs()));
        currentWI = null;
        currentResourceType = null;        
        if (timeOut) {
        	timeOut = false;
        	return false;
        }
        return true;
    }
    
    /**
     * Generates an event which finalizes a period of unavailability.
     * @param ts Actual simulation time.
     * @param duration Duration of the unavailability period.
     */
    public void generateCancelPeriodOffEvent(double ts, double duration) {
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
    protected Double getAvailability(ResourceType rt) {
    	return currentRoles.get(rt); 
    }
    
    /**
     * Makes available a resource with a specific role. 
     */
    public class RoleOnEvent extends BasicElement.DiscreteEvent {
        /** Available role */
        private final ResourceType role;
        /** Cycle iterator */
        private final CycleIterator iter;
        /** Availability duration */
        private final double duration;
        
        /**
         * Creates a new event
         * @param ts Timestamp when the resource will  be available.
         * @param role Role played by the resource.
         * @param iter The cycle iterator that handles the availability of this resource
         * @param duration The duration of the availability.
         */        
        public RoleOnEvent(double ts, ResourceType role, CycleIterator iter, double duration) {
            super(ts, role.getManager().getLp());
            this.iter = iter;
            this.role = role;
            this.duration = duration;
        }
        
        @Override
        public void event() {
        	double waitTime = role.beforeRoleOn();
        	if (waitTime == 0.0) {
        		simul.getInfoHandler().notifyInfo(new ResourceInfo(Resource.this.simul, Resource.this, role, ResourceInfo.Type.ROLON, ts));
        		debug("Resource available\t" + role);
        		role.incAvailable(Resource.this);
        		addRole(role, ts + duration);
        		// The activity manger is informed of new available resources
        		role.getManager().availableResource(); 
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
        private final CycleIterator iter;
        /** Availability duration */
        private final double duration;
        
        /**
         * Creates a new event
         * @param ts Timestamp when the resource will be unavailable.
         * @param role Role played by the resource.
         * @param iter The cycle iterator that handles the availability of this resource
         * @param duration The duration of the availability.
         */        
        public RoleOffEvent(double ts, ResourceType role, CycleIterator iter, double duration) {
            super(ts, role.getManager().getLp());
            this.role = role;
            this.iter = iter;
            this.duration = duration;
        }
        
        @Override
        public void event() {
        	double waitTime = role.beforeRoleOff();
        	if (waitTime == 0.0) {
        		simul.getInfoHandler().notifyInfo(new ResourceInfo(Resource.this.simul, Resource.this, role, ResourceInfo.Type.ROLOFF, ts));
        		role.decAvailable(Resource.this);
        		removeRole(role);
        		debug("Resource unavailable\t" + role);
        		double nextTs = iter.next();
        		if (!Double.isNaN(nextTs)) {
        			RoleOnEvent rEvent = new RoleOnEvent(nextTs, role, iter, duration);
        			addEvent(rEvent);            	
        		}
        		else if (--validTTEs == 0) {
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
		private final CycleIterator iter;
		/** Duration of the availability */
		private final double duration;

		/**
		 * Creates a new CancelPeriodOnEvent.
		 * @param ts Actual simulation time.
		 * @param iter Cycle iterator.
		 * @param duration Event duration.
		 */
		public CancelPeriodOnEvent(double ts, CycleIterator iter, double duration) {
			super(ts, simul.getDefaultLogicalProcess());
			this.iter = iter;
			this.duration = duration;
		}

		@Override
		public void event() {
			simul.getInfoHandler().notifyInfo(new ResourceInfo(Resource.this.simul, Resource.this, currentResourceType, ResourceInfo.Type.CANCELON, ts));
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
		private final CycleIterator iter;
		/** Duration of the availability */
		private final double duration;

		/**
		 * Creates a new CancelPeriodOffEvent.
		 * @param ts Actual simulation time.
		 * @param iter Cycle iterator.
		 * @param duration The event duration.
		 */   
		public CancelPeriodOffEvent(double ts, CycleIterator iter, double duration) {
			super(ts, simul.getDefaultLogicalProcess());
			this.iter = iter;
			this.duration = duration;
		}

		@Override
		public void event() {
			simul.getInfoHandler().notifyInfo(new ResourceInfo(Resource.this.simul, Resource.this, currentResourceType,ResourceInfo.Type.CANCELOFF, ts));
			setNotCanceled(true);
			for (ActivityManager am : getCurrentManagers()) {
				// The activity manger is informed of new available resources
				am.availableResource(); 
			}
			double nextTs = Double.NaN;
			if (iter != null)
				nextTs = iter.next();
			if (!Double.isNaN(nextTs)) {
				CancelPeriodOnEvent aEvent = new CancelPeriodOnEvent(nextTs, iter, duration);
				addEvent(aEvent);            	
			}
		}
	}

	/**
	 * Checks if a resource is available.
	 * @return True if the resource is available.
	 */
	public boolean isAvailable() {
		return ((getCurrentWI() == null) && (notCanceled));
	}
	
	/**
	 * Sets the available flag of a resource.
	 * @param available The availability state of the resource.
	 * @param ts
	 */
	public void setNotCanceled(boolean available) {
		notCanceled = available;
	}

	class ClockOnEntry {
		private double init = 0;
		private double finish = 0;
		private double avCounter = 0;
		
		
		public ClockOnEntry(double init) {
			this.init = init;
			finish = 0;
			avCounter = 0;
		}

		public double getFinish() {
			return finish;
		}

		public void setFinish(double finish) {
			this.finish = finish;
		}

		public double getInit() {
			return init;
		}

		public void setInit(double init) {
			this.init = init;
		}

		public double getAvCounter() {
			return avCounter;
		}

		public void setAvCounter(double avCounter) {
			this.avCounter = avCounter;
		}

	}

	public TreeMap<ResourceType, Double> getCurrentRoles() {
		return currentRoles;
	}

	@Override
	public Collection<es.ull.isaatc.simulation.common.TimeTableEntry> getTimeTableEntries() {
		return timeTable;
	}
	
}