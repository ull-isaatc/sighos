/**
 * 
 */
package es.ull.iis.simulation.model.flow;

import java.util.ArrayDeque;
import java.util.Iterator;

import es.ull.iis.function.TimeFunction;
import es.ull.iis.function.TimeFunctionFactory;
import es.ull.iis.simulation.condition.Condition;
import es.ull.iis.simulation.condition.TrueCondition;
import es.ull.iis.simulation.info.ElementActionInfo;
import es.ull.iis.simulation.model.ActivityManager;
import es.ull.iis.simulation.model.Describable;
import es.ull.iis.simulation.model.Element;
import es.ull.iis.simulation.model.ElementInstance;
import es.ull.iis.simulation.model.Identifiable;
import es.ull.iis.simulation.model.Resource;
import es.ull.iis.simulation.model.Simulation;
import es.ull.iis.simulation.model.WorkGroup;
import es.ull.iis.simulation.model.engine.RequestResourcesEngine;
import es.ull.iis.simulation.model.engine.SimulationEngine;
import es.ull.iis.util.Prioritizable;
import es.ull.iis.util.PrioritizedTable;

/**
 * A flow to request a set of resources, defined as {@link WorkGroup workgroups}. If all the resources from a workgroup are available, the element seizes 
 * them until a {@link ReleaseResourcesFlow} is used. The flow can use an identifier, so all the resources grouped under such identifier can be
 * released together later on. By default, all resources are grouped with a 0 identifier.<p> 
 * 
 * After seizing the resources, the element can suffer a delay.<p>
 * 
 * Each request flow is associated to an {@link ActivityManager}, which handles the way the resources are accessed.<p>
 * The flow is potentially feasible if there is no proof that none of the workgroups are available. The flow is feasible if it's potentially feasible 
 * and there is at least one workgroup with enough available resources.<p>
 * An element requesting a request flow which is not feasible is added to a queue until new resources are available.
 * @author Iván Castilla
 *
 */
public class RequestResourcesFlow extends SingleSuccessorFlow implements TaskFlow, ResourceHandlerFlow, Prioritizable {
    /** Priority. The lowest the value, the highest the priority */
    private final int priority;
    /** A brief description of the activity */
    private final String description;
    /** Work Groups available to perform this basic step */
    private final PrioritizedTable<ActivityWorkGroup> workGroupTable;
    /** A unique identifier that serves to tell a ReleaseResourcesFlow which resources to release */
	private final int resourcesId;
    /** Activity manager this request flow belongs to. */
    protected ActivityManager manager;
    /** Indicates that the basic step is potentially feasible. */
    protected boolean stillFeasible = true;
    /** Indicates whether the flow is the first step of an exclusive activity */
    private boolean inExclusiveActivity = false; 
    /** An engine to perform the simulation tasks associated to this flow */
    private RequestResourcesEngine engine;

	/**
	 * Creates a flow to seize a group of resources with the highest priority, and default identifier 
	 * @param model The simulation model this flow belongs to
	 * @param description A brief description of the flow
	 */
	public RequestResourcesFlow(final Simulation model, final String description) {
		this(model, description, 0, 0);
	}

	/**
	 * Creates a flow to seize a group of resources, with the specified priority, default identifier 
	 * @param model The simulation model this flow belongs to
	 * @param description A brief description of the flow
	 * @param priority Priority. The lowest the value, the highest the priority
	 */
	public RequestResourcesFlow(final Simulation model, final String description, final int priority) {
		this(model, description, 0, priority);
	}

	/**
	 * Creates a flow to seize a group of resources non-exclusively, with the specified priority and identifier 
	 * @param model The simulation model this flow belongs to
	 * @param description A brief description of the flow
	 * @param resourcesId Identifier of the group of resources
	 * @param priority Priority. The lowest the value, the highest the priority
	 */
	public RequestResourcesFlow(final Simulation model, final String description, final int resourcesId, final int priority) {
		super(model);
        this.description = description;
        this.priority = priority;
		this.resourcesId = resourcesId;
        workGroupTable = new PrioritizedTable<ActivityWorkGroup>();
	}

	@Override
	public void setParent(final StructuredFlow parent) {
		super.setParent(parent);
		if (parent instanceof ActivityFlow)
			inExclusiveActivity = ((ActivityFlow)parent).isExclusive();
	}
	
	/**
	 * Returns true if the flow is descendant of an exclusive activity; false otherwise 
	 * @return True if the flow is descendant of an exclusive activity; false otherwise
	 */
	public boolean isInExclusiveActivity() {
		return inExclusiveActivity;
	}

	@Override
	public String getDescription() {
		return description;
	}

	@Override
    public int getPriority() {
        return priority;
    }
	
    /**
     * Returns the activity manager this flow belongs to.
     * @return Value of property manager.
     */
    public ActivityManager getManager() {
        return manager;
    }
    
    /**
     * Sets the activity manager this flow belongs to. It also
     * adds this flow to the manager.
     * @param manager New value of manager.
     */
    public void setManager(final ActivityManager manager) {
        this.manager = manager;
        manager.add(this);
    }

	/**
	 * Returns an identifier for the group of resources seized within this flow 
	 * @return an identifier for the group of resources seized within this flow
	 */
	public int getResourcesId() {
		return resourcesId;
	}
	
    /**
     * Searches and returns a workgroup with the specified id.
     * @param wgId The id of the workgroup searched
     * @return A workgroup contained in this flow with the specified id
     */
    public ActivityWorkGroup getWorkGroup(final int wgId) {
        Iterator<ActivityWorkGroup> iter = workGroupTable.iterator();
        while (iter.hasNext()) {
        	ActivityWorkGroup opc = iter.next();
        	if (opc.getIdentifier() == wgId)
        		return opc;        	
        }
        return null;
    }
	
	/**
	 * Returns the amount of WGs associated to this flow
	 * @return the amount of WGs associated to this flow
	 */
	public int getWorkGroupSize() {
		return workGroupTable.size();
	}

    /**
     * Returns an iterator over the WGs of this activity.
     * @return An iterator over the WGs that can perform this activity.
     */
    public Iterator<ActivityWorkGroup> iterator() {
    	return workGroupTable.iterator();
    }

	@Override
	public void addPredecessor(final Flow newFlow) {}
    
	/**
	 * Returns true if this delay flow is being used as part of an interruptible activity
	 * @return True if this delay flow is being used as part of an interruptible activity
	 */
	public boolean partOfInterruptible() {
		if (parent != null)
			if (parent instanceof ActivityFlow)
				return ((ActivityFlow)parent).isInterruptible();
		return false;
	}
	
	@Override
	public String getObjectTypeIdentifier() {
		return "ACQ";
	}
	
	// User methods
	/**
	 * Allows a user for adding a customized code when the {@link ElementInstance} actually starts the
	 * execution of the {@link RequestResourcesFlow}.
	 * @param ei {@link ElementInstance} requesting this {@link RequestResourcesFlow}
	 */
	public void afterAcquire(final ElementInstance ei) {}

	/**
	 * Allows a user for adding a customized code when a {@link es.ull.iis.simulation.model.ElementInstance} from an {@link es.ull.iis.simulation.model.Element}
	 * is enqueued, waiting for available {@link es.ull.iis.simulation.model.Resource}. 
	 * @param ei {@link es.ull.iis.simulation.model.ElementInstance} requesting resources
	 */
	public void inqueue(final ElementInstance ei) {}
	
	@Override
	public void afterFinalize(final ElementInstance ei) {}

	// End of user methods

	/**
     * Checks if this basic step can be performed with any of its workgroups. Firstly 
     * checks if the basic step is not potentially feasible, then goes through the 
     * workgroups looking for an appropriate one. If the basic step cannot be performed with 
     * any of the workgroups it's marked as not potentially feasible. 
     * @param ei Element instance wanting to perform the basic step 
     * @return A set of resources that makes a valid solution for this request flow; null otherwise. 
     */
	public ArrayDeque<Resource> isFeasible(final ElementInstance ei) {
    	if (!stillFeasible)
    		return null;
        Iterator<ActivityWorkGroup> iter = workGroupTable.randomIterator();
        while (iter.hasNext()) {
        	ActivityWorkGroup wg = iter.next();
        	final ArrayDeque<Resource> solution = new ArrayDeque<Resource>(); 
            if (engine.checkWorkGroup(solution, wg, ei)) {
                ei.setExecutionWG(wg);
        		ei.getElement().debug("Can carry out \t" + this + "\t" + wg);
                return solution;
            }            
        }
        // No valid WG was found
        stillFeasible = false;
        return null;
	}

    /**
     * Sets this activity as potentially feasible.
     */
    public void resetFeasible() {
    	stillFeasible = true;
    }
    
    /**
     * Returns how many elements are waiting to seize resources with this flow
     * @return how many elements are waiting to seize resources with this flow
     */
    public int getQueueSize() {
    	return engine.getQueueSize();
    }
    
    /**
     * Removes an element instance from the queue
     * @param ei Element instance
     */
    public void queueRemove(final ElementInstance ei) {
    	engine.queueRemove(ei);
    }

	/*
	 * (non-Javadoc)
	 * @see es.ull.iis.simulation.Flow#request(es.ull.iis.simulation.FlowExecutor)
	 */
	public void request(final ElementInstance ei) {
		if (!ei.wasVisited(this)) {
			if (ei.isExecutable()) {
				if (beforeRequest(ei)) {
					simul.notifyInfo(new ElementActionInfo(simul, ei, ei.getElement(), this, ei.getExecutionWG(), null, ElementActionInfo.Type.REQ, simul.getTs()));
					if (ei.getElement().isDebugEnabled())
						ei.getElement().debug("Requests\t" + this + "\t" + getDescription());
					engine.queueAdd(ei); // The element is introduced in the queue
					manager.notifyAvailableElement(ei);
				}
				else {
					ei.cancel(this);
					next(ei);
				}
			}
			else {
				ei.updatePath(this);
				next(ei);
			}
		} else
			ei.notifyEnd();
	}

	/*
	 * (non-Javadoc)
	 * @see es.ull.iis.simulation.TaskFlow#finish(es.ull.iis.simulation.FlowExecutor)
	 */
	public void finish(final ElementInstance wThread) {
		wThread.endDelay(this);
		next(wThread);
	}
	
	@Override
	public void assignSimulation(final SimulationEngine simul) {
		engine = simul.getRequestResourcesEngineInstance(this);
	}

	/**
	 * Creates a builder object for adding workgroups to this flow. 
	 * @param wg The set of pairs <ResurceType, amount> which will be seized
	 * @return The builder object for adding workgroups to this flow
	 */
	public WorkGroupAdder newWorkGroupAdder(final WorkGroup wg) {
		return new WorkGroupAdder(wg);
	}
	
	/**
	 * A builder for adding workgroups. By default, workgroups have the highest priority, unconditionally available and have not delay.
	 * The priority, condition and delay can be modified by using the "with..." methods.
	 * @author Iván Castilla
	 *
	 */
	public final class WorkGroupAdder {
		/** The set of pairs <ResurceType, amount> which will be seized */
		final private WorkGroup wg;
		/** Priority of the workgroup */
		private int priority = 0;
		/** Availability condition */
		private Condition<ElementInstance> cond = null;
		/** Delay applied after seizing the resources */
		private TimeFunction delay = null;
		
		private WorkGroupAdder(final WorkGroup wg) {
			this.wg = wg;
		}
		
		public WorkGroupAdder withPriority(final int priority) {
			this.priority = priority;
			return this;
		}
		
		public WorkGroupAdder withCondition(final Condition<ElementInstance> cond) {
			this.cond = cond;
			return this;
		}

		public WorkGroupAdder withDelay(final TimeFunction delay) {
			this.delay = delay;
			return this;
		}

		public WorkGroupAdder withDelay(final long delay) {
			this.delay = TimeFunctionFactory.getInstance("ConstantVariate", delay);
			return this;
		}
		
	    /**
	     * Creates a new workgroup for this flow. 
	     * @return The new workgroup's identifier.
	     */
		public int add() {
			if (cond == null)
				cond = new TrueCondition<ElementInstance>();
			if (delay == null)
				delay = TimeFunctionFactory.getInstance("ConstantVariate", 0L);			
	    	final int wgId = workGroupTable.size();
	        workGroupTable.add(new ActivityWorkGroup(simul, wgId, priority, wg, cond, delay));
	        return wgId;
		}
	}

	/**
	 * A set of resources needed for carrying out an activity. A workgroup (WG) consists on a 
	 * set of (resource type, #needed resources) pairs, a condition which determines if the 
	 * workgroup can be used or not, and the priority of the workgroup inside the basicStep.
	 * @author Iván Castilla Rodríguez
	 */
	public class ActivityWorkGroup extends WorkGroup implements Prioritizable, Identifiable, Describable {
		/** Priority of the workgroup */
	    final private int priority;
	    /** Availability condition */
	    final private Condition<ElementInstance> cond;
	    /** A function to characterize the duration of the delay */
	    final private TimeFunction duration;
	    /** Precomputed string which identifies this WG */
	    final private String idString; 
		
	    /**
	     * Creates a new instance of WorkGroup which contains the same resource types
	     * than an already existing one.
	     * @param id Identifier of this workgroup.
	     * @param priority Priority of the workgroup.
	     * @param wg The original workgroup
	     * @param cond  Availability condition
	     */    
	    public ActivityWorkGroup(final Simulation model, final int id, final int priority, final WorkGroup wg, final Condition<ElementInstance> cond, final TimeFunction duration) {
	    	super(model, wg.getResourceTypes(), wg.getNeeded());
	        this.priority = priority;
	        this.cond = cond;
	        this.duration = duration;
	        this.idString = new String("(" + RequestResourcesFlow.this + ")" + wg.getDescription());
	    }
	    
	    /**
	     * Getter for property priority.
	     * @return Value of property priority.
	     */
	    public int getPriority() {
	        return priority;
	    }

	    /**
	     * Returns a function to characterize the duration of the delay
		 * @return A function to characterize the duration of the delay
		 */
		public TimeFunction getDuration() {
			return duration;
		}

	    /**
	     * Returns the duration of the activity where this workgroup is used. 
	     * The value returned by the random number function could be negative. 
	     * In this case, it returns 0.
	     * @param elem The element performing the activity
	     * @return The activity duration.
	     */
	    public long getDurationSample(final Element elem) {
	    	return Math.round(duration.getValue(elem));
	    }
	    
	    @Override
	    public String toString() {
	    	return idString;
	    }

	    /**
	     * Returns a condition to set the availability of the workgroup
	     * @return a condition to set the availability of the workgroup
	     */
		public Condition<ElementInstance> getCondition() {
			return cond;
		}
	}
}
