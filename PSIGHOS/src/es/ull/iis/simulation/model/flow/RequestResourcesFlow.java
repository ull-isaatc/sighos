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
import es.ull.iis.simulation.model.ActivityWorkGroup;
import es.ull.iis.simulation.model.ElementInstance;
import es.ull.iis.simulation.model.Resource;
import es.ull.iis.simulation.model.Simulation;
import es.ull.iis.simulation.model.WorkGroup;
import es.ull.iis.simulation.model.engine.RequestResourcesEngine;
import es.ull.iis.simulation.model.engine.SimulationEngine;
import es.ull.iis.util.Prioritizable;
import es.ull.iis.util.PrioritizedTable;

/**
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
	/** Only one exclusive set of resources can be acquired by an element at the same time */
	private final boolean exclusive;
    /** Activity manager this request flow belongs to. */
    protected ActivityManager manager;
    /** Indicates that the basic step is potentially feasible. */
    protected boolean stillFeasible = true;
    private RequestResourcesEngine engine;

	/**
	 * @param simul
	 * @param description
	 */
	public RequestResourcesFlow(Simulation model, String description) {
		this(model, description, 0, 0, false);
	}

	/**
	 * @param simul
	 * @param description
	 * @param priority
	 */
	public RequestResourcesFlow(Simulation model, String description, int priority, boolean exclusive) {
		this(model, description, 0, priority, exclusive);
	}

	/**
	 * @param simul
	 * @param description
	 */
	public RequestResourcesFlow(Simulation model, String description, int resourcesId) {
		this(model, description, resourcesId, 0, false);
	}

	/**
	 * @param simul
	 * @param description
	 */
	public RequestResourcesFlow(Simulation model, String description, int resourcesId, int priority) {
		this(model, description, resourcesId, priority, false);
	}

	/**
	 * @param simul
	 * @param description
	 * @param priority
	 */
	public RequestResourcesFlow(Simulation model, String description, int resourcesId, int priority, boolean exclusive) {
		super(model);
        this.description = description;
        this.priority = priority;
		this.resourcesId = resourcesId;
		this.exclusive = exclusive;
        workGroupTable = new PrioritizedTable<ActivityWorkGroup>();
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
	 * Returns <tt>true</tt> if the activity is exclusive, i.e., an element cannot perform other 
	 * exclusive activities at the same time. 
	 * @return <tt>True</tt> if the activity is exclusive, <tt>false</tt> in other case.
	 */
	public boolean isExclusive() {
		return exclusive;
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
     * adds this resource type to the manager.
     * @param manager New value of property manager.
     */
    public void setManager(ActivityManager manager) {
        this.manager = manager;
        manager.add(this);
    }

	/**
	 * @return the resourcesId
	 */
	public int getResourcesId() {
		return resourcesId;
	}

	/**
     * Creates a new workgroup for this activity using the specified wg.
     * @param priority Priority of the workgroup
     * @param wg The set of pairs <ResurceType, amount> which will perform the activity
     * @return The new workgroup's identifier.
     */
    public int addWorkGroup(int priority, WorkGroup wg) {
    	return addWorkGroup(priority, wg, new TrueCondition(), 0L);
    }
    
    /**
     * Creates a new workgroup for this activity using the specified wg. This workgroup
     * is only available if cond is true.
     * @param priority Priority of the workgroup
     * @param wg The set of pairs <ResurceType, amount> which will perform the activity
     * @param cond Availability condition
     * @return The new workgroup's identifier.
     */
    public int addWorkGroup(int priority, WorkGroup wg, Condition cond) {
    	return addWorkGroup(priority, wg, cond, 0L);
    }
    
    /**
     * Creates a new workgroup for this activity with the highest level of priority using 
     * the specified wg.
     * @param wg The set of pairs <ResurceType, amount> which will perform the activity
     * @return The new workgroup's identifier.
     */
    public int addWorkGroup(WorkGroup wg) {    	
    	return addWorkGroup(0, wg, new TrueCondition(), 0L);
    }
    
    /**
     * Creates a new workgroup for this activity with the highest level of priority using 
     * the specified wg. This workgroup is only available if cond is true.
     * @param wg The set of pairs <ResurceType, amount> which will perform the activity
     * @param cond Availability condition
     * @return The new workgroup's identifier.
     */
    public int addWorkGroup(WorkGroup wg, Condition cond) {    	
    	return addWorkGroup(0, wg, cond, 0L);
    }
	
    /**
     * Creates a new workgroup for this activity. 
     * @param duration Duration of the activity when performed with the new workgroup
     * @param priority Priority of the workgroup
     * @param wg The set of pairs <ResurceType, amount> which will perform the activity
     * @return The new workgroup's identifier.
     */
    public int addWorkGroup(int priority, WorkGroup wg, Condition cond, TimeFunction duration) {
    	final int wgId = workGroupTable.size();
        workGroupTable.add(new ActivityWorkGroup(simul, this, wgId, priority, wg, cond, duration));
        return wgId;
    }
    
    /**
     * Creates a new workgroup for this activity. 
     * @param duration Duration of the activity when performed with the new workgroup
     * @param priority Priority of the workgroup
     * @param wg The set of pairs <ResurceType, amount> which will perform the activity
     * @param cond Availability condition
     * @return The new workgroup's identifier.
     */    
    public int addWorkGroup(int priority, WorkGroup wg, TimeFunction duration) {
		return addWorkGroup(priority, (WorkGroup)wg, new TrueCondition(), duration);
    }
    
    /**
     * Creates a new workgroup for this activity. 
     * @param duration Duration of the activity when performed with the new workgroup
     * @param priority Priority of the workgroup
     * @param wg The set of pairs <ResurceType, amount> which will perform the activity
     * @return The new workgroup's identifier.
     */
    public int addWorkGroup(int priority, WorkGroup wg, long duration) {
        return addWorkGroup(priority, wg, TimeFunctionFactory.getInstance("ConstantVariate", duration));
    }
    
    /**
     * Creates a new workgroup for this activity. 
     * @param duration Duration of the activity when performed with the new workgroup
     * @param priority Priority of the workgroup
     * @param wg The set of pairs <ResurceType, amount> which will perform the activity
     * @param cond Availability condition
     * @return The new workgroup's identifier.
     */    
    public int addWorkGroup(int priority, WorkGroup wg, Condition cond, long duration) {    	
        return addWorkGroup(priority, wg, cond, TimeFunctionFactory.getInstance("ConstantVariate", duration));
    }

    /**
     * Searches and returns a workgroup with the specified id.
     * @param wgId The id of the workgroup searched
     * @return A workgroup contained in this activity with the specified id
     */
    public ActivityWorkGroup getWorkGroup(int wgId) {
        Iterator<ActivityWorkGroup> iter = workGroupTable.iterator();
        while (iter.hasNext()) {
        	ActivityWorkGroup opc = iter.next();
        	if (opc.getIdentifier() == wgId)
        		return opc;        	
        }
        return null;
    }
	
	/**
	 * Returns the amount of WGs associated to this activity
	 * @return the amount of WGs associated to this activity
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
	public void addPredecessor(Flow newFlow) {}
    
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
	
	/**
	 * Allows a user for adding a customized code when the {@link ElementInstance} actually starts the
	 * execution of the {@link RequestResourcesFlow}.
	 * @param ei {@link ElementInstance} requesting this {@link RequestResourcesFlow}
	 */
	public void afterAcquire(ElementInstance ei) {}

	/**
	 * Allows a user for adding a customized code when a {@link es.ull.iis.simulation.model.ElementInstance} from an {@link es.ull.iis.simulation.model.Element}
	 * is enqueued, waiting for available {@link es.ull.iis.simulation.model.Resource}. 
	 * @param ei {@link es.ull.iis.simulation.model.ElementInstance} requesting resources
	 */
	public void inqueue(ElementInstance ei) {}
	
	@Override
	public void afterFinalize(ElementInstance ei) {}

	/**
     * Checks if this basic step can be performed with any of its workgroups. Firstly 
     * checks if the basic step is not potentially feasible, then goes through the 
     * workgroups looking for an appropriate one. If the basic step cannot be performed with 
     * any of the workgroups it's marked as not potentially feasible. 
     * @param fe Work thread wanting to perform the basic step 
     * @return A set of resources that makes a valid solution for this request flow; null otherwise. 
     */
	public ArrayDeque<Resource> isFeasible(ElementInstance fe) {
    	if (!stillFeasible)
    		return null;
        Iterator<ActivityWorkGroup> iter = workGroupTable.randomIterator();
        while (iter.hasNext()) {
        	ActivityWorkGroup wg = iter.next();
        	final ArrayDeque<Resource> solution = new ArrayDeque<Resource>(); 
            if (engine.checkWorkGroup(solution, wg, fe)) {
                fe.setExecutionWG(wg);
        		fe.getElement().debug("Can carry out \t" + this + "\t" + wg);
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
    
    public int getQueueSize() {
    	return engine.getQueueSize();
    }
    
    public void queueRemove(ElementInstance fe) {
    	engine.queueRemove(fe);
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
		if (isExclusive()) {
			wThread.getElement().setCurrent(null);
		}
		next(wThread);
	}
	
	@Override
	public void assignSimulation(SimulationEngine simul) {
		engine = simul.getRequestResourcesEngineInstance(this);
	}
	
	static class Trio {
	    /** Availability condition */
	    final protected Condition cond;
	    final protected TimeFunction duration;
	    final protected WorkGroup wg;
	    
		/**
		 * @param cond
		 * @param duration
		 * @param wg
		 */
		public Trio(Condition cond, TimeFunction duration, WorkGroup wg) {
			this.cond = cond;
			this.duration = duration;
			this.wg = wg;
		}
	}
}
