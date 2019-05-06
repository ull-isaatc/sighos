/**
 * 
 */
package es.ull.iis.simulation.model;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Collection;

import es.ull.iis.simulation.info.EntityLocationInfo;
import es.ull.iis.simulation.info.ResourceInfo;
import es.ull.iis.simulation.model.engine.ResourceEngine;
import es.ull.iis.simulation.model.engine.SimulationEngine;
import es.ull.iis.simulation.model.location.Location;
import es.ull.iis.simulation.model.location.Movable;
import es.ull.iis.simulation.model.location.MoveResourcesFlow;
import es.ull.iis.simulation.model.location.Router;
import es.ull.iis.simulation.model.location.TransportFlow;
import es.ull.iis.util.cycle.DiscreteCycleIterator;

/**
 * A simulation resource whose availability is controlled by means of {@link TimeTableEntry timetable entries}.
 * Timetable entries can overlap in time, thus allowing the resource for being potentially available for
 * different resource types simultaneously.
 * A resource finishes its execution when it has no longer valid timetable entries.
 * @author Iván Castilla Rodríguez
 *
 */
public class Resource extends VariableStoreSimulationObject implements Describable, EventSource, Movable {
    /** A brief description of the resource */
    protected final String description;
	/** The current location of the resource*/
	private Location currentLocation;
	/** The initial location of the resource*/
	private Location initLocation;
	/** The size of the resource */
	private final int size;
	/** The current element instance that drives the movement of the resource */
	private ElementInstance movingInstance = null;	
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
     * Creates a resource with size 0
     * @param model The simulation model this resource belongs to 
     * @param description A brief description of the resource
     */
	public Resource(final Simulation model, final String description) {
		this(model, description, 0, null);
	}

    /**
     * Creates a resource
     * @param model The simulation model this resource belongs to 
     * @param description A brief description of the resource
     * @param size The size of the resource
     * @param initLocation The initial location of the resource
     */
	public Resource(final Simulation model, final String description, final int size, final Location initLocation) {
		super(model, model.getResourceList().size(), "RES");
		this.description = description;
		this.size = size;
		this.initLocation = initLocation;
		model.add(this);
	}

	@Override
	protected void assignSimulation(SimulationEngine simul) {
		engine = simul.getResourceEngineInstance(this);
	}      
	
	/**
	 * Returns the associated {@link ResourceEngine}
	 * @return the associated {@link ResourceEngine}
	 */
	public ResourceEngine getEngine() {
		return engine;
	}

	@Override
	public String getDescription() {
		return description;
	}

	@Override
	public int getCapacity() {
		return size;
	}

	@Override
	public Location getLocation() {
		return currentLocation;
	}

	@Override
	public void setLocation(final Location location) {
		if (currentLocation == null) {
			simul.notifyInfo(new EntityLocationInfo(simul, this, location, EntityLocationInfo.Type.START, getTs()));
			currentLocation = location;
		}
		else {
			simul.notifyInfo(new EntityLocationInfo(simul, this, currentLocation, EntityLocationInfo.Type.LEAVE, getTs()));
			currentLocation = location;
			simul.notifyInfo(new EntityLocationInfo(simul, this, currentLocation, EntityLocationInfo.Type.ARRIVE, getTs()));
		}
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
	
	@Override
	public DiscreteEvent onCreate(final long ts) {
		if (initLocation != null) {
			if (initLocation.fitsIn(this)) {
				initLocation.enter(this);
			}
			else {
				error("Unable to initialize resource. Not enough space in location " + initLocation + " (available: " + initLocation.getAvailableCapacity() + " - required: " + size + ")");				
				return onDestroy(ts);
			}
		}
		return new CreateResourceEvent(ts);
	}

	@Override
	public DiscreteEvent onDestroy(final long ts) {
		return new DiscreteEvent.DefaultFinalizeEvent(this, ts);
	}
    
    /**
     * Informs the resource that it must finish its execution. 
     */
    public void notifyEnd() {
        engine.notifyEnd();
    }
    
    /**
     * Returns the {@link ResourceType resource type} currently assigned to this resource, in case it is in use. Returns null otherwise.
     * @return Value of property currentResourceType.
     */
    public ResourceType getCurrentResourceType() {
        return currentResourceType;
    }

    /**
     * Assigns a {@link ResourceType resource type} for this resource, whenever it is going to be used by an {@link Element}.
     * @param rt Value of property currentResourceType.
     */
    public void setCurrentResourceType(final ResourceType rt) {
    	currentResourceType = rt;
    }
    
    /**
     * Returns true if the resource is currently seized by an {@link Element element}; false otherwise 
     * @return true if the resource is currently seized by an {@link Element element}; false otherwise
     */
    public boolean isSeized() {
    	return (engine.getCurrentElement() != null);
    }
    
    /**
     * Returns <code>true</code> if this resource is being used in spite of having finished its availability.
     * @return <code>True</code> if this resource is being used in spite of having finished its availability;
     * <code>false</code> otherwise.
     */
    public boolean isTimeOut() {
        return timeOut;
    }
    
    /**
     * Sets the state of this resource as being used in spite of having finished its availability. 
     * @param timeOut <code>True</code> if this resource is being used beyond its availability; 
     * <code>false</code> otherwise.
     */
    public void setTimeOut(final boolean timeOut) {
        this.timeOut = timeOut;
    }
    
	/**
	 * Checks if a resource is available for a specific {@link ResourceType resource type}. The resource type is used to prevent 
	 * using a resource when it's becoming unavailable right at this timestamp. 
	 * @param rt Resource type
	 * @return True if the resource is available.
	 */
	public boolean isAvailable(final ResourceType rt) {
		return engine.isAvailable(rt);
	}

	/**
	 * Sets the available flag of a resource.
	 * @param available The availability state of the resource.
	 */
	protected void setNotCanceled(final boolean available) {
		engine.setNotCanceled(available);
	}
	
	/**
	 * Adds a resource to the set of resources requested by an {@link Element}. 
	 * @param solution Tentative solution with booked resources
	 * @param rt Resource type
	 * @param ei Element instance corresponding to an Element
	 * @return <code>true</code> if the resource can be used within the proposed solution; <code>false</code> otherwise.
	 */
	public boolean add2Solution(final ArrayDeque<Resource> solution, final ResourceType rt, final ElementInstance ei) {
		return engine.add2Solution(solution, rt, ei);
	}

	/**
	 * Removes a resource from the set of resourcs requested by an {@link Element}
	 * @param solution Tentative solution with booked resources
	 * @param ei Element instance corresponding to an Element
	 */
	public void removeFromSolution(final ArrayDeque<Resource> solution, final ElementInstance ei) {
		engine.removeFromSolution(solution, ei);
	}

	/**
	 * Marks this resource as taken by an {@link Element}
	 * @param ei The element instance in charge of executing the current flow
	 * @return The availability timestamp of this resource for this resource type 
	 */
	protected long catchResource(final ElementInstance ei) {
		return engine.catchResource(ei);
	}
	
    /**
     * Releases this resource
	 * @param ei The element instance in charge of executing the current flow
     * @return True if the resource could be correctly released. False if the availability time of the resource had already expired.
     */
    protected boolean releaseResource(final ElementInstance ei) {
    	return engine.releaseResource(ei);
    }
    
	/**
	 * Builds a list of activity managers referenced by the roles of the resource. 
	 * @return Returns the list of activity managers referenced by the roles of the resource.
	 */
    public ArrayList<ActivityManager> getCurrentManagers() {
    	return engine.getCurrentManagers();
    }
    
    /**
     * Generates an event which finalizes a period of unavailability.
     * @param ts Current simulation time.
     * @param duration Duration of the unavailability period.
     */
    public void generateCancelPeriodOffEvent(final long ts, final long duration) {
    	final CancelPeriodOffEvent aEvent = new CancelPeriodOffEvent(ts + duration, null, 0);
        simul.addEvent(aEvent);
    }
    
    /**
     * Creates a move event to move to destination using the specified router.
     * @param ei Element instance that initiates the move
     * @param destination Destination location
     * @param router Instance that returns the path for the element
     */
    public void startMove(final ElementInstance ei) {
    	final MoveResourcesFlow flow = (MoveResourcesFlow)ei.getCurrentFlow();
    	final Location destination = flow.getDestination();
    	final Router router = flow.getRouter();
		debug("Start route\t" + this + "\t" + destination);
    	movingInstance = ei;
    	// No need to move
    	if (currentLocation.equals(destination)) {
    		endMove(flow, true);
    	}
    	else {
			final Location nextLoc = router.getNextLocationTo(this, destination);
			if (Router.isUnreachableLocation(nextLoc)) {
	    		endMove(flow, false);
			}
			else {
		    	simul.addEvent(new MoveEvent(getTs() + currentLocation.getDelayAtExit(this), nextLoc, destination, router));
			}
    	}
    }
    
    /**
     * Creates a transport event to move to destination using the specified router.
     * @param ei Element instance that initiates the move
     * @param destination Destination location
     * @param router Instance that returns the path for the element
     */
    public void startTransport(final ElementInstance ei) {
    	final TransportFlow flow = (TransportFlow)ei.getCurrentFlow();
    	final Location destination = flow.getDestination();
    	final Router router = flow.getRouter();
		debug("Start transport\t" + this + "\t" + destination);
    	movingInstance = ei;
    	// No need to move
    	if (currentLocation.equals(destination)) {
    		endTransport(flow, true);
    	}
    	else {
			final Location nextLoc = router.getNextLocationTo(this, destination);
			if (Router.isUnreachableLocation(nextLoc)) {
	    		endTransport(flow, false);
			}
			else {
		    	simul.addEvent(new TransportEvent(getTs() + currentLocation.getDelayAtExit(this), nextLoc, destination, router));
			}
    	}
    }
    
    /**
     * Notifies the flow that the move has finished
     * @param ei Flow driving the movement
     * @param success True if the resource arrived at destination; false if the destination was unreachable
     */
    private void endMove(final MoveResourcesFlow flow, final boolean success) {
		flow.notifyArrival(movingInstance, success);
    	movingInstance = null;
    	if (success)
			debug("Finishes route\t" + this + "\t" + flow.getDestination());
    	else
			error("Destination unreachable. Current: " + currentLocation + "; destination: " + flow.getDestination());
    }

    /**
     * Notifies the flow that the transport has finished
     * @param ei Flow driving the movement
     * @param success True if the resource arrived at destination; false if the destination was unreachable
     */
    private void endTransport(final TransportFlow flow, final boolean success) {
    	if (success) {
    		flow.finish(movingInstance);
    		movingInstance = null;
    		debug("Finishes transport\t" + this + "\t" + flow.getDestination());
    	}
    	else {
			movingInstance.cancel(flow);
			flow.next(movingInstance);
	    	movingInstance = null;
    		error("Destination unreachable. Current: " + currentLocation + "; destination: " + flow.getDestination());
    	}
    }

	@Override
	public void notifyLocationAvailable(final Location location) {
		location.enter(this);

		if (movingInstance.getCurrentFlow() instanceof MoveResourcesFlow) {
	    	final MoveResourcesFlow flow = (MoveResourcesFlow)movingInstance.getCurrentFlow();
	    	final Location destination = flow.getDestination();
	    	final Router router = flow.getRouter();
			
			if (currentLocation.equals(destination)) {
				endMove(flow, true);
			}
			else {
				final Location nextLoc = router.getNextLocationTo(this, destination);
				if (Router.isUnreachableLocation(nextLoc)) {
					endMove(flow, false);
				}
				else {
			    	simul.addEvent(new MoveEvent(getTs() + currentLocation.getDelayAtExit(this), nextLoc, destination, router));						
				}
			}			
		}
		else if (movingInstance.getCurrentFlow() instanceof TransportFlow) {
	    	final TransportFlow flow = (TransportFlow)movingInstance.getCurrentFlow();
			// Move the element without checking anything else
			movingInstance.getElement().setLocation(location);
	    	final Location destination = flow.getDestination();
	    	final Router router = flow.getRouter();
			
			if (currentLocation.equals(destination)) {
				endTransport(flow, true);
			}
			else {
				final Location nextLoc = router.getNextLocationTo(this, destination);
				if (Router.isUnreachableLocation(nextLoc)) {
					endTransport(flow, false);
				}
				else {
			    	simul.addEvent(new TransportEvent(getTs() + currentLocation.getDelayAtExit(this), nextLoc, destination, router));						
				}
			}			
		}
	}
	
	/**
	 * Returns a builder class for adding time table or cancellation entries
	 * @param roleList The types of this resource during every activation /to be cancelled
	 * @return a builder class for adding time table or cancellation entries
	 */
	public TimeTableOrCancelEntriesAdder newTimeTableOrCancelEntriesAdder(final ArrayList<ResourceType> roleList) {
		return new TimeTableOrCancelEntriesAdder(roleList);
	}
	
	/**
	 * Returns a builder class for adding time table or cancellation entries
	 * @param role The type of this resource during every activation/to be cancelled 
	 * @return a builder class for adding time table or cancellation entries
	 */
	public TimeTableOrCancelEntriesAdder newTimeTableOrCancelEntriesAdder(final ResourceType role) {
		return new TimeTableOrCancelEntriesAdder(role);
	}
	
	/**
	 * A builder class to build time table or cancellation entries. With one builder you can create several entries with the same cycle and duration for
	 * one or more resource types. If you don't use the {@link #withDuration(SimulationCycle, TimeStamp)} method, the entries are assumed to last for
	 * the whole duration of the simulation. You can also use the same builder for time table or cancellation entries, by invoking, respectively, 
	 * {@link #addTimeTableEntry()} or {@link #addCancelEntry()}
	 * @author Iván Castilla
	 *
	 */
	public final class TimeTableOrCancelEntriesAdder {
		private final ArrayList<ResourceType> roleList = new ArrayList<ResourceType>();
		private SimulationCycle cycle = null;
		private TimeStamp dur = null;
		
		/**
		 * Creates an entry adder with a single role
		 * @param role The type of this resource during every activation/to be cancelled
		 */
		public TimeTableOrCancelEntriesAdder(final ResourceType role) {
			roleList.add(role);
		}

		/**
		 * Creates an entry adder with multiple concurrent roles
		 * @param roleList The types of this resource during every activation /to be cancelled
		 */
		public TimeTableOrCancelEntriesAdder(final ArrayList<ResourceType> roleList) {
			this.roleList.addAll(roleList);
		}
		
		/**
		 * Adds a duration and activation/cancellation cycle
		 * @param cycle Simulation cycle to define activation/deactivation time
		 * @param dur How long the resource is active/will remain inactive
		 */
		public TimeTableOrCancelEntriesAdder withDuration(final SimulationCycle cycle, final TimeStamp dur) {
			this.cycle = cycle;
			this.dur = dur;
			return this;
		}
		
		/**
		 * Adds a duration and activation/cancellation cycle
		 * @param cycle Simulation cycle to define activation/deactivation time
		 * @param dur How long the resource is active/will remain inactive
		 */
		public TimeTableOrCancelEntriesAdder withDuration(final SimulationCycle cycle, final long dur) {
			return this.withDuration(cycle, new TimeStamp(simul.getTimeUnit(), dur));
		}
		
		/**
		 * Creates the time table entry/ies with the specified characteristics
		 */
		public void addTimeTableEntry() {
			if (cycle == null) {
		    	for (final ResourceType role : roleList)
		    		timeTable.add(new TimeTableEntry(role));
			}
			else {
		    	for (final ResourceType role : roleList)
		    		timeTable.add(new TimeTableEntry(cycle, dur, role));
			}
		}
		
		/**
		 * Creates the cancellation entry/ies with the specified characteristics
		 */
		public void addCancelEntry() {
			if (cycle == null) {
		    	for (final ResourceType role : roleList)
		    		cancelPeriodTable.add(new TimeTableEntry(role));
			}
			else {
		    	for (final ResourceType role : roleList)
		    		cancelPeriodTable.add(new TimeTableEntry(cycle, dur, role));
			}
		}
	}
	    
    /**
     * The event in charge of initializing the resource
     * @author Iván Castilla Rodríguez
     *
     */
    protected class CreateResourceEvent extends DiscreteEvent {

    	public CreateResourceEvent(long ts) {
    		super(ts);
		}
    	
		@Override
		public void event() {
			simul.notifyInfo(new ResourceInfo(simul, Resource.this, null, ResourceInfo.Type.START, getTs()));
			for (int i = 0 ; i < timeTable.size(); i++) {
				TimeTableEntry tte = timeTable.get(i);
				if (tte.isPermanent()) {
		            final RoleOnEvent rEvent = new RoleOnEvent(getTs(), tte.getRole(), null, Long.MAX_VALUE - getTs());
		            simul.addEvent(rEvent);
		            engine.incValidTimeTableEntries();
				}
				else {
					// FIXME: Check whether it works when using a condition to end simulation: should I use simul.getEndTs() instead of Long.MAX_VALUE?
			        DiscreteCycleIterator iter = tte.getCycle().getCycle().iterator(getTs(), Long.MAX_VALUE);
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
				// FIXME: Check whether it works when using a condition to end simulation: should I use simul.getEndTs() instead of Long.MAX_VALUE?
		        final DiscreteCycleIterator iter = tte.getCycle().getCycle().iterator(getTs(), Long.MAX_VALUE);
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
    protected class RoleOnEvent extends DiscreteEvent {
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
        public RoleOnEvent(final long ts, final ResourceType role, final DiscreteCycleIterator iter, final long duration) {
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
		 * Returns the resource type
		 * @return Returns the role.
		 */
		public ResourceType getRole() {
			return role;
		}
    }
    
    /**
     * Makes unavailable a resource with a specific role. 
     */
    protected class RoleOffEvent extends DiscreteEvent {
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
        public RoleOffEvent(final long ts, final ResourceType role, final DiscreteCycleIterator iter, final long duration) {
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
		 * Returns the resource type
		 * @return Returns the role.
		 */
		public ResourceType getRole() {
			return role;
		}        
    }
    
	/**
	 * Event which starts a cancellation period for this resource
	 * @author ycallero
	 *
	 */
	protected class CancelPeriodOnEvent extends DiscreteEvent {
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
		public CancelPeriodOnEvent(final long ts, final DiscreteCycleIterator iter, final long duration) {
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
	 * Event which ends a cancellation period for this resource
	 * @author ycallero
	 *
	 */
	protected class CancelPeriodOffEvent extends DiscreteEvent {
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
		public CancelPeriodOffEvent(final long ts, final DiscreteCycleIterator iter, final long duration) {
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

	/**
	 * Event to move the resource to a different location
	 * @author Iván Castilla Rodríguez
	 *
	 */
	protected class MoveEvent extends DiscreteEvent {
		/** Final destination of the move */
		final private Location destination;
		/** Next location in the way to the final destination */
		final private Location nextLocation;
		/** The instance that computes the path to the final destination */
		final private Router router;
		
		/**
		 * Creates a move event that starts a move from the resource's current location
		 * @param ts Current timestamp
		 * @param destination Destination location
		 * @param router Instance that returns the path for the resource
		 */
		public MoveEvent(final long ts, final Location destination, final Router router) {
			this(ts, currentLocation, destination, router);
		}

		/**
		 * Creates a move event that makes an intermediate step in the way to destination
		 * @param ts Timestamp when the resource will arrive at the intermediate location
		 * @param nextLocation Intermediate location 
		 * @param destination Final destination
		 * @param router Instance that returns the path for the resource
		 */
		public MoveEvent(final long ts, final Location nextLocation, final Location destination, final Router router) {
			super(ts);
			this.destination = destination;
			this.nextLocation = nextLocation;
			this.router = router;
		}

		@Override
		public void event() {
			if (nextLocation.fitsIn(Resource.this)) {
				final MoveResourcesFlow flow = ((MoveResourcesFlow)movingInstance.getCurrentFlow());
				nextLocation.enter(Resource.this);
				if (nextLocation.equals(destination)) {
					endMove(flow, true);
				}
				else {
					final Location nextLoc = router.getNextLocationTo(Resource.this, destination);
					if (Router.isUnreachableLocation(nextLoc)) {
						endMove(flow, false);
					}
					else {
						final MoveEvent mEvent = new MoveEvent(getTs() + currentLocation.getDelayAtExit(Resource.this), nextLoc, destination, router);
				    	simul.addEvent(mEvent);						
					}
				}
			}
			else {
				nextLocation.waitFor(Resource.this);
				simul.notifyInfo(new EntityLocationInfo(simul, Resource.this, nextLocation, EntityLocationInfo.Type.WAIT_FOR, getTs()));
			}
		}
	}

	/**
	 * Event to move the resource to a different location
	 * @author Iván Castilla Rodríguez
	 *
	 */
	protected class TransportEvent extends DiscreteEvent {
		/** Final destination of the move */
		final private Location destination;
		/** Next location in the way to the final destination */
		final private Location nextLocation;
		/** The instance that computes the path to the final destination */
		final private Router router;
		
		/**
		 * Creates a transport event that starts a move from the resource's current location
		 * @param ts Current timestamp
		 * @param destination Destination location
		 * @param router Instance that returns the path for the resource
		 */
		public TransportEvent(final long ts, final Location destination, final Router router) {
			this(ts, currentLocation, destination, router);
		}

		/**
		 * Creates a transport event that makes an intermediate step in the way to destination
		 * @param ts Timestamp when the resource will arrive at the intermediate location
		 * @param nextLocation Intermediate location 
		 * @param destination Final destination
		 * @param router Instance that returns the path for the resource
		 */
		public TransportEvent(final long ts, final Location nextLocation, final Location destination, final Router router) {
			super(ts);
			this.destination = destination;
			this.nextLocation = nextLocation;
			this.router = router;
		}

		@Override
		public void event() {
			if (nextLocation.fitsIn(Resource.this)) {
				final TransportFlow flow = ((TransportFlow)movingInstance.getCurrentFlow());
				nextLocation.enter(Resource.this);
				// Move the element without checking anything else
				movingInstance.getElement().setLocation(nextLocation);
				if (nextLocation.equals(destination)) {
					endTransport(flow, true);
				}
				else {
					final Location nextLoc = router.getNextLocationTo(Resource.this, destination);
					if (Router.isUnreachableLocation(nextLoc)) {
						endTransport(flow, false);
					}
					else {
						final TransportEvent mEvent = new TransportEvent(getTs() + currentLocation.getDelayAtExit(Resource.this), nextLoc, destination, router);
				    	simul.addEvent(mEvent);						
					}
				}
			}
			else {
				nextLocation.waitFor(Resource.this);
				simul.notifyInfo(new EntityLocationInfo(simul, Resource.this, nextLocation, EntityLocationInfo.Type.WAIT_FOR, getTs()));
			}
		}
	}
}
