package es.ull.iis.simulation.sequential;

import java.util.ArrayList;
import java.util.TreeMap;

import es.ull.iis.simulation.info.ResourceInfo;
import es.ull.iis.simulation.info.ResourceUsageInfo;
import es.ull.iis.simulation.model.DiscreteEvent;
import es.ull.iis.simulation.model.flow.ResourcesFlow;
import es.ull.iis.util.DiscreteCycleIterator;

/**
 * A resource is an element that becomes available at a specific simulation time and 
 * becomes unavailable at other simulation time. The availability of a resource is controlled
 * by means of timetable entries, which define a resource type and an availability cycle.
 * A resource finishes its execution when it has no longer valid timetable entries.
 * @author Carlos Martín Galán
 */
public class Resource extends BasicElement {
	/** Timetable which defines the availability estructure of the resource. Define RollOn and RollOff events. */
    protected final ArrayList<TimeTableEntry> timeTable = new ArrayList<TimeTableEntry>();
    /** Availability time table. Define CancelPeriodOn and CancelPeriodOff events */
    protected final ArrayList<TimeTableEntry> cancelPeriodTable = new ArrayList<TimeTableEntry>();
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
    protected final es.ull.iis.simulation.model.Resource modelRes;

    /**
     * Creates a new instance of Resource.
     * @param id This resource's identifier.
     * @param simul Simulation this resource is attached to.
     * @param description A short text describing this resource.
     */
	public Resource(Simulation simul, es.ull.iis.simulation.model.Resource modelRes) {
		super(simul.getNextResourceId(), simul);
        currentRoles = new TreeMap<ResourceType, Long>();
        notCanceled = true;
        this.modelRes = modelRes;
        simul.add(this);
        for (es.ull.iis.simulation.model.TimeTableEntry tte : modelRes.getTimeTableEntries()) {
        	timeTable.add(new TimeTableEntry(tte.getCycle(), tte.getDuration(), simul.getResourceType(tte.getRole())));
        }
        for (es.ull.iis.simulation.model.TimeTableEntry tte : modelRes.getCancellationPeriodEntries()) {
        	timeTable.add(new TimeTableEntry(tte.getCycle(), tte.getDuration(), simul.getResourceType(tte.getRole())));
        }
	}

    /**
	 * @return the modelRes
	 */
	public es.ull.iis.simulation.model.Resource getModelRes() {
		return modelRes;
	}

	@Override
	public String getObjectTypeIdentifier() {
		return modelRes.getObjectTypeIdentifier();
	}

    /*
     * (non-Javadoc)
     * @see es.ull.iis.simulation.Describable#getDescription()
     */
	public String getDescription() {
		return modelRes.getDescription();
	}

	/**
	 * Adds a new resource type to the list of current roles. If the list already contains an
	 * entry for the resource type, the greater timestamp is added.
	 * @param role New resource type added
	 * @param ts Timestamp when the availability of this resource finishes for this resource type. 
	 */
	protected void addRole(ResourceType role, long ts) {
		Long avEnd = currentRoles.get(role);
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
		Long avEnd = currentRoles.get(role);
		if (avEnd != null)
			if (avEnd <= getTs())
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
		simul.getInfoHandler().notifyInfo(new ResourceUsageInfo(simul, modelRes, currentResourceType.getModelRT(), wt, (ResourcesFlow) wt.getCurrentFlow(), ResourceUsageInfo.Type.CAUGHT, getTs()));
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
		simul.getInfoHandler().notifyInfo(new ResourceUsageInfo(simul, modelRes, currentResourceType.getModelRT(), currentWT, (ResourcesFlow) currentWT.getCurrentFlow(), ResourceUsageInfo.Type.RELEASED, getTs()));
        currentWT = null;
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
    public void generateCancelPeriodOffEvent(long ts, long duration) {
    	CancelPeriodOffEvent aEvent = new CancelPeriodOffEvent(ts + duration, null, 0);
        addEvent(aEvent);
    }
    
    /**
     * Returns the work item of the element which currently owns this resource.
     * @return The current work item.
     */
    protected WorkThread getCurrentWI() {
        return currentWT;
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

	@Override
	public DiscreteEvent onCreate(long ts) {
		return new CreateResourceEvent(ts);
	}

	@Override
	public DiscreteEvent onDestroy() {
		return new BasicElement.DefaultFinalizeEvent();
	}

	public TreeMap<ResourceType, Long> getCurrentRoles() {
		return currentRoles;
	}
    
    public class CreateResourceEvent extends DiscreteEvent {

    	public CreateResourceEvent(long ts) {
    		super(ts);
		}
    	
		@Override
		public void event() {
			simul.getInfoHandler().notifyInfo(new ResourceInfo(simul, modelRes, getCurrentResourceType().getModelRT(), ResourceInfo.Type.START, getTs()));
			for (int i = 0 ; i < timeTable.size(); i++) {
				TimeTableEntry tte = timeTable.get(i);
				if (tte.isPermanent()) {
		            final RoleOnEvent rEvent = new RoleOnEvent(getTs(), (ResourceType) tte.getRole(), null, simul.getInternalEndTs());
		            addEvent(rEvent);
		            validTTEs++;
				}
				else {
			        DiscreteCycleIterator iter = tte.getCycle().getCycle().iterator(getTs(), simul.getInternalEndTs());
			        long nextTs = iter.next();
			        if (nextTs != -1) {
			            RoleOnEvent rEvent = new RoleOnEvent(nextTs, (ResourceType) tte.getRole(), iter, simul.simulationTime2Long(tte.getDuration()));
			            addEvent(rEvent);
			            validTTEs++;
			        }
				}
			}
			for (int i = 0 ; i < cancelPeriodTable.size(); i++) {
				TimeTableEntry tte = cancelPeriodTable.get(i);
		        DiscreteCycleIterator iter = tte.getCycle().getCycle().iterator(getTs(), simul.getInternalEndTs());
		        long nextTs = iter.next();
		        if (nextTs != -1) {
		            CancelPeriodOnEvent aEvent = new CancelPeriodOnEvent(nextTs, iter, simul.simulationTime2Long(tte.getDuration()));
		            addEvent(aEvent);
		            validTTEs++;
		        }
			}
			if (validTTEs == 0)// at least one tte should be valid
				notifyEnd();
		}
    	
    }
    /**
     * Makes available a resource with a specific role. 
     */
    public class RoleOnEvent extends DiscreteEvent {
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
        		simul.getInfoHandler().notifyInfo(new ResourceInfo(simul, modelRes, role.getModelRT(), ResourceInfo.Type.ROLON, ts));
        		debug("Resource available\t" + role);
        		role.incAvailable(Resource.this);
        		addRole(role, ts + duration);
        		// The activity manager is informed of new available resources
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
    public class RoleOffEvent extends DiscreteEvent {
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
        		simul.getInfoHandler().notifyInfo(new ResourceInfo(simul, modelRes, role.getModelRT(), ResourceInfo.Type.ROLOFF, ts));
        		role.decAvailable(Resource.this);
        		removeRole(role);
        		debug("Resource unavailable\t" + role);
        		final long nextTs = (iter == null) ? -1 : iter.next();
        		if (nextTs != -1) {
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
	public class CancelPeriodOnEvent extends DiscreteEvent {
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
			simul.getInfoHandler().notifyInfo(new ResourceInfo(simul, modelRes, currentResourceType.getModelRT(), ResourceInfo.Type.CANCELON, ts));
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
	public class CancelPeriodOffEvent extends DiscreteEvent {
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
			simul.getInfoHandler().notifyInfo(new ResourceInfo(simul, modelRes, currentResourceType.getModelRT(), ResourceInfo.Type.CANCELOFF, ts));
			setNotCanceled(true);
			for (ActivityManager am : getCurrentManagers()) {
				// The activity manger is informed of new available resources
				am.availableResource(); 
			}
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
	 * Checks if a resource is available for a specific Resource Type. The resource type is used to prevent 
	 * using a resource when it's becoming unavailable right at this timestamp. 
	 * @return True if the resource is available.
	 */
	public boolean isAvailable(ResourceType rt) {
		return ((getCurrentWI() == null) && (notCanceled) && (getAvailability(rt) > getTs()));
	}
	
	/**
	 * Sets the available flag of a resource.
	 * @param available The availability state of the resource.
	 */
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
