/**
 * 
 */
package es.ull.iis.simulation.model;

import java.util.ArrayList;
import java.util.Collection;

import es.ull.iis.simulation.info.ResourceInfo;
import es.ull.iis.simulation.model.engine.ResourceEngine;
import es.ull.iis.simulation.model.engine.SimulationEngine;
import es.ull.iis.util.DiscreteCycleIterator;

/**
 * A simulation resource whose availability is controlled by means of timetable entries.
 * A timetable entry us a trio &lt{@link ResourceType}, {@link SimulationCycle}, long&gt which defines a 
 * resource type, an availability cycle, and the duration of each availability period. Timetable entries 
 * can be overlapped in time, thus allowing the resource for being potentially available for
 * different resource types simultaneously.
 * A resource finishes its execution when it has no longer valid timetable entries.
 * @author Iván Castilla Rodríguez
 *
 */
public class Resource extends VariableStoreModelObject implements Describable, EventSource {
    /** A brief description of the resource */
    protected final String description;
	/** Timetable which defines the availability estructure of the resource. Define RollOn and RollOff events. */
    protected final ArrayList<TimeTableEntry> timeTable = new ArrayList<TimeTableEntry>();
    /** Availability time table. Define CancelPeriodOn and CancelPeriodOff events */
    protected final ArrayList<TimeTableEntry> cancelPeriodTable = new ArrayList<TimeTableEntry>();
    /** If true, indicates that this resource is being used after its availability time has expired */
    private boolean timeOut = false;
    /** The resource type which this resource is being booked for */
    protected ResourceType currentResourceType = null;
    /** The engine in charge of executing specific actions */
    private ResourceEngine engine;

	/**
	 * 
	 */
	public Resource(Simulation model, String description) {
		super(model, model.getResourceList().size(), "RES");
		this.description = description;
		model.add(this);
	}

	@Override
	protected void assignSimulation(SimulationEngine simul) {
		engine = simul.getResourceEngineInstance(this);
	}      
	
	@Override
	public String getDescription() {
		return description;
	}

	/**
	 * Returns a collection with the timetable defined for this resource.
	 * @return a collection with the timetable defined for this resource
	 */
	public Collection<TimeTableEntry> getTimeTableEntries() {
		return timeTable;
	}
	
	/**
	 * Returns a collection with the cancellation timetable defined for this resource.
	 * @return a collection with the cancellation timetable defined for this resource
	 */
	public Collection<TimeTableEntry> getCancellationPeriodEntries() {
		return cancelPeriodTable;
	}
	
	/**
	 * Adds a timetable entry for the whole duration of the simulation
	 * @param role The type of this resource during every activation 
	 */
	public void addTimeTableEntry(ResourceType role) {
		timeTable.add(new TimeTableEntry(role));
    }
    
	/**
	 * Adds a timetable entry for the whole duration of the simulation with overlapped resource types
	 * @param roleList The types of this resource during every activation 
	 */
	public void addTimeTableEntry(ArrayList<ResourceType> roleList) {
    	for (int i = 0; i < roleList.size(); i++)
            addTimeTableEntry(roleList.get(i));
    }
	
    /**
	 * Adds a timetable entry
	 * @param cycle ParallelSimulationEngine cycle to define activation time
	 * @param dur How long the resource is active
	 * @param role The type of this resource during every activation 
	 */
	public void addTimeTableEntry(SimulationCycle cycle, TimeStamp dur, ResourceType role) {
        timeTable.add(new TimeTableEntry(cycle, dur, role));
    }
    
	/**
	 * Adds a timetable entry with overlapped resource types
	 * @param cycle ParallelSimulationEngine cycle to define activation time
	 * @param dur How long the resource is active
	 * @param roleList The types of this resource during every activation 
	 */
	public void addTimeTableEntry(SimulationCycle cycle, TimeStamp dur, ArrayList<ResourceType> roleList) {
    	for (int i = 0; i < roleList.size(); i++)
            addTimeTableEntry(cycle, dur, roleList.get(i));
    }  
    
	/**
	 * Adds a timetable entry
	 * @param cycle ParallelSimulationEngine cycle to define activation time
	 * @param dur How long the resource is active using the default model time unit
	 * @param role The type of this resource during every activation 
	 */
	public void addTimeTableEntry(SimulationCycle cycle, long dur, ResourceType role) {
    	addTimeTableEntry(cycle, new TimeStamp(simul.getTimeUnit(), dur), role);
    }  
    
	/**
	 * Adds a timetable entry with overlapped resource types
	 * @param cycle ParallelSimulationEngine cycle to define activation time
	 * @param dur How long the resource is active using the default model time unit
	 * @param roleList The types of this resource during every activation 
	 */
	public void addTimeTableEntry(SimulationCycle cycle, long dur, ArrayList<ResourceType> roleList) {
    	addTimeTableEntry(cycle, new TimeStamp(simul.getTimeUnit(), dur), roleList);
    }  
    
    /**
     * Adds a new entry with a single role.
     * @param cycle Cycle that characterizes this entry
     * @param dur The long this resource plays this role every cycle
     * @param role Role that the resource plays during this cycle
     */
	public void addCancelTableEntry(SimulationCycle cycle, TimeStamp dur, ResourceType role) {
        cancelPeriodTable.add(new TimeTableEntry(cycle, dur, role));
    }  

    /**
     * Adds a new entry with a several roles.
     * @param cycle Cycle that characterizes this entry
     * @param dur The long this resource plays this role every cycle
     * @param roleList Roles that the resource play during this cycle
     */
	public void addCancelTableEntry(SimulationCycle cycle, TimeStamp dur, ArrayList<ResourceType> roleList) {
    	for (int i = 0; i < roleList.size(); i++)
            addCancelTableEntry(cycle, dur, roleList.get(i));
    }  
    
    /**
     * Adds a new entry with a single role.
     * @param cycle Cycle that characterizes this entry
     * @param dur The long this resource plays this role every cycle expressed in the 
     * default model time unit
     * @param role Role that the resource plays during this cycle
     */
	public void addCancelTableEntry(SimulationCycle cycle, long dur, ResourceType role) {
    	addCancelTableEntry(cycle, new TimeStamp(simul.getTimeUnit(), dur), role);
    }  

    /**
     * Adds a new entry with a several roles.
     * @param cycle Cycle that characterizes this entry
     * @param dur The long this resource plays this role every cycle cycle expressed in the 
     * default model time unit
     * @param roleList Roles that the resource play during this cycle
     */
	public void addCancelTableEntry(SimulationCycle cycle, long dur, ArrayList<ResourceType> roleList) {
    	addCancelTableEntry(cycle, new TimeStamp(simul.getTimeUnit(), dur), roleList);
    }

	@Override
	public DiscreteEvent onCreate(long ts) {
		return new CreateResourceEvent(ts);
	}

	@Override
	public DiscreteEvent onDestroy(long ts) {
		return new DiscreteEvent.DefaultFinalizeEvent(this, ts);
	}
    
    /**
     * Informs the element that it must finish its execution. Thus, a FinalizeEvent is
     * created.
     */
    public void notifyEnd() {
        engine.notifyEnd();
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
     * @param rt Value of property currentResourceType.
     */
    public void setCurrentResourceType(ResourceType rt) {
    	currentResourceType = rt;
    }
    
    public FlowExecutor getCurrentFlowExecutor() {
    	return engine.getCurrentFlowExecutor();
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
	 * Checks if a resource is available for a specific Resource Type. The resource type is used to prevent 
	 * using a resource when it's becoming unavailable right at this timestamp. 
	 * @return True if the resource is available.
	 */
	public boolean isAvailable(ResourceType rt) {
		return engine.isAvailable(rt);
	}

	/**
	 * Sets the available flag of a resource.
	 * @param available The availability state of the resource.
	 */
	public void setNotCanceled(boolean available) {
		engine.setNotCanceled(available);
	}
	
	public boolean add2Solution(ResourceType rt, FlowExecutor fe) {
		return engine.add2Solution(rt, fe);
	}

	public void removeFromSolution(FlowExecutor fe) {
		engine.removeFromSolution(fe);
	}

	/**
	 * Marks this resource as taken by an element. Sets the current work item, and the
	 * current resource type; and adds this resource to the item's caught resources list.
	 * A "taken" element continues being booked. The book is released when the resource itself is
	 * released. 
	 * @param wt The work thread in charge of executing the current flow
	 * @return The availability timestamp of this resource for this resource type 
	 */
	public long catchResource(FlowExecutor wt) {
		return engine.catchResource(wt);
	}
	
    /**
     * Releases this resource. If the resource has already expired its availability time, 
     * the timeOut flag is set off. Sets the current work item and the current resource type 
     * to <code>null</code>. The book of the resource is released too.
     * @return True if the resource could be correctly released. False if the availability
     * time of the resource had already expired.
     */
    public boolean releaseResource() {
    	return engine.releaseResource();
    }
    
    public ArrayList<ActivityManager> getCurrentManagers() {
    	return engine.getCurrentManagers();
    }
    
    /**
     * Generates an event which finalizes a period of unavailability.
     * @param ts Actual simulation time.
     * @param duration Duration of the unavailability period.
     */
    public void generateCancelPeriodOffEvent(long ts, long duration) {
    	CancelPeriodOffEvent aEvent = new CancelPeriodOffEvent(ts + duration, null, 0);
        simul.addEvent(aEvent);
    }
    
    /**
     * The event in charge of initializing the resource
     * @author Iván Castilla Rodríguez
     *
     */
    public class CreateResourceEvent extends DiscreteEvent {

    	public CreateResourceEvent(long ts) {
    		super(ts);
		}
    	
		@Override
		public void event() {
			simul.notifyInfo(new ResourceInfo(simul, Resource.this, null, ResourceInfo.Type.START, getTs()));
			for (int i = 0 ; i < timeTable.size(); i++) {
				TimeTableEntry tte = timeTable.get(i);
				if (tte.isPermanent()) {
		            final RoleOnEvent rEvent = new RoleOnEvent(getTs(), tte.getRole(), null, simul.getEndTs());
		            simul.addEvent(rEvent);
		            engine.incValidTimeTableEntries();
				}
				else {
			        DiscreteCycleIterator iter = tte.getCycle().getCycle().iterator(getTs(), simul.getEndTs());
			        final long nextTs = iter.next();
			        if (nextTs != -1) {
			            RoleOnEvent rEvent = new RoleOnEvent(nextTs, tte.getRole(), iter, simul.simulationTime2Long(tte.getDuration()));
			            simul.addEvent(rEvent);
			            engine.incValidTimeTableEntries();
			        }
				}
			}
			for (int i = 0 ; i < cancelPeriodTable.size(); i++) {
				final TimeTableEntry tte = cancelPeriodTable.get(i);
		        final DiscreteCycleIterator iter = tte.getCycle().getCycle().iterator(getTs(), simul.getEndTs());
		        long nextTs = iter.next();
		        if (nextTs != -1) {
		            final CancelPeriodOnEvent aEvent = new CancelPeriodOnEvent(nextTs, iter, simul.simulationTime2Long(tte.getDuration()));
		            simul.addEvent(aEvent);
		            engine.incValidTimeTableEntries();
		        }
			}
			if (engine.getValidTimeTableEntries() == 0)// at least one tte should be valid
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
        		simul.notifyInfo(new ResourceInfo(simul, Resource.this, role, ResourceInfo.Type.ROLON, ts));
        		debug("Resource available\t" + role);
        		role.incAvailable(Resource.this);
        		engine.addRole(role, ts + duration);
        		role.afterRoleOn();
        		RoleOffEvent rEvent = new RoleOffEvent(ts + duration, role, iter, duration);
        		simul.addEvent(rEvent);
        	} else {
        		RoleOnEvent rEvent = new RoleOnEvent(ts + waitTime, role, iter, duration);
        		simul.addEvent(rEvent);
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
        		simul.notifyInfo(new ResourceInfo(simul, Resource.this, role, ResourceInfo.Type.ROLOFF, ts));
        		role.decAvailable(Resource.this);
        		engine.removeRole(role);
        		debug("Resource unavailable\t" + role);
        		final long nextTs = (iter == null) ? -1 : iter.next();
        		if (nextTs != -1) {
        			RoleOnEvent rEvent = new RoleOnEvent(nextTs, role, iter, duration);
        			simul.addEvent(rEvent);            	
        		}
        		else if (engine.decValidTimeTableEntries() == 0) {
        			role.afterRoleOff();
        			notifyEnd();
        		}
        	} else {
        		RoleOffEvent rEvent = new RoleOffEvent(ts + waitTime, role, iter, duration);
        		simul.addEvent(rEvent);
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
			simul.notifyInfo(new ResourceInfo(simul, Resource.this, getCurrentResourceType(), ResourceInfo.Type.CANCELON, ts));
			engine.setNotCanceled(false);
			CancelPeriodOffEvent aEvent = new CancelPeriodOffEvent(ts + duration, iter, duration);
			simul.addEvent(aEvent);
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
			simul.notifyInfo(new ResourceInfo(simul, Resource.this, getCurrentResourceType(), ResourceInfo.Type.CANCELOFF, ts));
			engine.setNotCanceled(true);
			engine.notifyCurrentManagers();
			long nextTs = -1;
			if (iter != null)
				nextTs = iter.next();
			if (nextTs != -1) {
				CancelPeriodOnEvent aEvent = new CancelPeriodOnEvent(nextTs, iter, duration);
				simul.addEvent(aEvent);            	
			}
		}
	}

}
