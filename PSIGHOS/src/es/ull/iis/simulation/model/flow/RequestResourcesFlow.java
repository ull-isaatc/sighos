/**
 * 
 */
package es.ull.iis.simulation.model.flow;

import java.util.Iterator;

import es.ull.iis.function.TimeFunction;
import es.ull.iis.function.TimeFunctionFactory;
import es.ull.iis.simulation.condition.Condition;
import es.ull.iis.simulation.condition.TrueCondition;
import es.ull.iis.simulation.info.ElementActionInfo;
import es.ull.iis.simulation.model.ActivityManager;
import es.ull.iis.simulation.model.ActivityWorkGroup;
import es.ull.iis.simulation.model.FlowExecutor;
import es.ull.iis.simulation.model.Model;
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
	public RequestResourcesFlow(Model model, String description, int resourcesId) {
		this(model, description, resourcesId, 0, false);
	}

	/**
	 * @param simul
	 * @param description
	 */
	public RequestResourcesFlow(Model model, String description, int resourcesId, int priority) {
		this(model, description, resourcesId, priority, false);
	}

	/**
	 * @param simul
	 * @param description
	 * @param priority
	 */
	public RequestResourcesFlow(Model model, String description, int resourcesId, int priority, boolean exclusive) {
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
	 * @return the exclusive
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
	 * Allows a user for adding a customized code when a {@link es.ull.iis.simulation.model.FlowExecutor} from an {@link es.ull.iis.simulation.model.Element}
	 * is enqueued, waiting for available {@link es.ull.iis.simulation.model.Resource}. 
	 * @param wt {@link es.ull.iis.simulation.model.FlowExecutor} requesting resources
	 */
	public void inqueue(FlowExecutor fe) {}
	
	
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
        workGroupTable.add(new ActivityWorkGroup(model, this, wgId, priority, wg, cond, duration));
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
	 * Allows a user for adding a customized code when the {@link FlowExecutor} actually starts the
	 * execution of the {@link ActivityFlow}.
	 * @param fe {@link FlowExecutor} requesting this {@link ActivityFlow}
	 */
	public void afterAcquire(FlowExecutor fe) {
	}

	@Override
	public void afterFinalize(FlowExecutor fe) {
	}

	/**
     * Checks if this basic step can be performed with any of its workgroups. Firstly 
     * checks if the basic step is not potentially feasible, then goes through the 
     * workgroups looking for an appropriate one. If the basic step cannot be performed with 
     * any of the workgroups it's marked as not potentially feasible. 
     * @param fe Work thread wanting to perform the basic step 
     * @return <code>True</code> if this activity can be carried out with any one of its 
     * WGs. <code>False</code> in other case.
     */
	public boolean isFeasible(FlowExecutor fe) {
    	if (!stillFeasible)
    		return false;
        Iterator<ActivityWorkGroup> iter = workGroupTable.randomIterator();
        while (iter.hasNext()) {
        	ActivityWorkGroup wg = iter.next();
            if (engine.checkWorkGroup(wg, fe)) {
                fe.setExecutionWG(wg);
        		fe.getElement().debug("Can carry out \t" + this + "\t" + wg);
                return true;
            }            
        }
        // No valid WG was found
        stillFeasible = false;
        return false;
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
    
    public void queueRemove(FlowExecutor fe) {
    	engine.queueRemove(fe);
    }
    
    /**
     * Catch the resources needed for each resource type to carry out an activity.
     * @return -1 if the resources cannot be acquired; 0 if no delay is required;
     * a delay duration otherwise.
     */
	public long acquireResources(FlowExecutor fe) {
		model.notifyInfo(new ElementActionInfo(model, fe, fe.getElement(), this, fe.getExecutionWG(), ElementActionInfo.Type.REQ, model.getSimulationEngine().getTs()));
		if (fe.getElement().isDebugEnabled())
			fe.getElement().debug("Requests\t" + this + "\t" + getDescription());
		if (!isExclusive() || (fe.getElement().getCurrent() == null)) {
			// There are enough resources to perform the activity
			if (isFeasible(fe)) {
				if (isExclusive()) 
					fe.getElement().setCurrent(fe);
		    	return fe.catchResources();
			}
		}
		engine.queueAdd(fe); // The element is introduced in the queue
		return -1L;
	}

	/*
	 * (non-Javadoc)
	 * @see es.ull.iis.simulation.Flow#request(es.ull.iis.simulation.FlowExecutor)
	 */
	public void request(final FlowExecutor wThread) {
		if (!wThread.wasVisited(this)) {
			if (wThread.isExecutable()) {
				if (beforeRequest(wThread)) {
					final long delay = acquireResources(wThread);
					// No delay
					if (delay == 0)
						next(wThread);
					else if (delay > 0)
						wThread.startDelay(delay);
				}
				else {
					wThread.cancel(this);
					next(wThread);
				}
			}
			else {
				wThread.updatePath(this);
				next(wThread);
			}
		} else
			wThread.notifyEnd();
	}

	/*
	 * (non-Javadoc)
	 * @see es.ull.iis.simulation.TaskFlow#finish(es.ull.iis.simulation.FlowExecutor)
	 */
	public void finish(final FlowExecutor wThread) {
		wThread.endDelay(this);
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
