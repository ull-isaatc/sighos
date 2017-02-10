package es.ull.iis.simulation.parallel;

import java.util.EnumSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

import es.ull.iis.function.TimeFunction;
import es.ull.iis.function.TimeFunctionFactory;
import es.ull.iis.simulation.condition.Condition;
import es.ull.iis.simulation.condition.TrueCondition;
import es.ull.iis.simulation.core.flow.Flow;
import es.ull.iis.simulation.core.flow.StructuredFlow;
import es.ull.iis.simulation.info.ElementActionInfo;
import es.ull.iis.simulation.parallel.flow.BasicFlow;
import es.ull.iis.simulation.parallel.flow.InitializerFlow;
import es.ull.iis.util.PrioritizedTable;

/**
 * A task which could be carried out by a {@link WorkItem} and requires certain amount and 
 * type of {@link Resource resources} to be performed.  An activity is characterized by its 
 * priority and a set of {@link WorkGroup Workgroups} (WGs). Each WG represents a combination 
 * of {@link ResourceType resource types} required to carry out the activity.<p>
 * Each activity is attached to an {@link ActivityManager}, which manages the access to the activity.<p>
 * An activity is potentially feasible if there is no proof that there are not enough resources
 * to perform it. An activity is feasible if it's potentially feasible and there is at least one
 * WG with enough available resources to perform the activity. The WGs are checked in 
 * order according to some priorities, and can also have an associated condition which must be 
 * accomplished to be selected.<p>
 * An activity can be requested (that is, check if the activity is feasible) by a valid 
 * {@link WorkItem}. 
 * If the activity is not feasible, the work item is added to a queue until new resources are 
 * available. If the activity is feasible, the work item "carries out" the activity, that is, 
 * catches the resources needed to perform the activity. Whenever it is determined that the 
 * activity has finished, the work item releases the resources previously caught.<p>
 * An activity can also define cancellation periods for each one of the resource types it uses. 
 * If a work item takes a resource belonging to one of the cancellation periods of the activity, this
 * resource can't be used during a period of time after the activity finishes.
 * @author Carlos Martín Galán
 */
public class Activity extends TimeStampedSimulationObject implements es.ull.iis.simulation.core.flow.ActivityFlow {
	/** 
	 * An artificially created final node. This flow informs the flow-driven
	 * activity that it has being finalized.
	 */
	private final BasicFlow virtualFinalFlow = new BasicFlow(simul) {

		public void request(WorkThread wThread) {
			wThread.notifyEnd();
		}

		@Override
		public void addPredecessor(Flow newFlow) {}

		@Override
		public Flow link(Flow successor) {}

		@Override
		public void setRecursiveStructureLink(StructuredFlow parent, Set<es.ull.iis.simulation.core.flow.Flow> visited) {}		
	};
	/** The set of modifiers of this activity. */
    protected final EnumSet<Modifier> modifiers;
    /** Priority. 0 for the higher priority, higher values for lower priorities */
    protected int priority = 0;
    /** A brief description of this activity */
    protected final String description;
    /** Total amount of {@link WorkItem WorkItems} waiting for carrying out this activity */
    protected int queueSize = 0;
    /** The activity manager this activity is attached to */
    protected ActivityManager manager = null;
    /** WGs available to perform this activity */
    protected final PrioritizedTable<ActivityWorkGroup> workGroupTable = new PrioritizedTable<ActivityWorkGroup>();
    /** Indicates that the activity is potentially feasible. */
    protected boolean stillFeasible = true;
    /** Resource cancellation table */
    protected final Map<ResourceType, Long> cancellationList = new TreeMap<ResourceType, Long>();

	/**
     * Creates a new activity with the highest priority.
     * @param simul The {@link Simulation} where this activity is used
     * @param description A short text describing this activity
     */
    public Activity(Simulation simul, String description) {
        this(simul, description, 0, EnumSet.noneOf(Modifier.class));
    }

    /**
     * Creates a new activity.
     * @param simul The {@link Simulation} where this activity is used
     * @param description A short text describing this activity
     * @param priority Activity's priority.
     */
    public Activity(Simulation simul, String description, int priority) {
        this(simul, description, priority, EnumSet.noneOf(Modifier.class));
    }

    /**
     * Creates a new activity with the highest priority and customized behavior.
     * @param simul Simulation which this activity is attached to.
     * @param description A short text describing this Activity.
     * @param modifiers Indicates if the activity has special characteristics. 
     */
    public Activity(Simulation simul, String description, EnumSet<Modifier> modifiers) {
        this(simul, description, 0, modifiers);
    }

    /**
     * Creates a new activity with the specified priority and customized behavior.
     * @param simul Simulation which this activity is attached to.
     * @param description A short text describing this Activity.
     * @param priority Activity's priority.
     * @param modifiers Indicates if the activity has special characteristics. 
     */
    public Activity(Simulation simul, String description, int priority, EnumSet<Modifier> modifiers) {
        super(simul.getNextActivityId(), simul);
        this.modifiers = modifiers;
        this.description = description;
        this.priority = priority;
        simul.add(this);
    }

    @Override
	public String getDescription() {
		return description;
	}

	/**
     * Returns the priority of this activity.
     * @return Priority of this activity
     */
    @Override
    public int getPriority() {
        return priority;
    }
    
    /**
     * Returns the {@link ActivityManager} where this activity is located.
     * @return The {@link ActivityManager} where this activity is located
     */
    public ActivityManager getManager() {
        return manager;
    }

    /**
     * Sets the {@link ActivityManager} where this activity is located. Also
     * adds this activity to the manager.
     * @param manager {@link ActivityManager} where this activity is located.
     */
    public void setManager(ActivityManager manager) {
        this.manager = manager;
        manager.add(this);
    }
    
	@Override
	public EnumSet<Modifier> getModifiers() {
		return modifiers;
	}
	
	/** 
	 * Returns <tt>true</tt> if the activity is non presential, i.e., an element can perform other 
	 * activities at the same time. 
	 * @return <tt>True</tt> if the activity is non presential, <tt>false</tt> in other case.
	 */
    public boolean isNonPresential() {
        return modifiers.contains(Modifier.NONPRESENTIAL);
    }
    
    /**
     * Returns <tt>true</tt> if this activity is interruptible, i.e., the activity is
     * suspended when any of the the resources taken to perform the activity finalize 
     * their availability. The activity can be resumed when there are available resources 
     * again (<b>but not necessarily the same resources</b>). 
     * <p>By default, an activity is not interruptible.  
     * @return Always <tt>false</tt>. Subclasses overriding this method must change the 
     * default behavior. 
     */
	public boolean isInterruptible() {
		return modifiers.contains(Modifier.INTERRUPTIBLE);
	}

	@Override
    public TimeDrivenActivityWorkGroup addWorkGroup(TimeFunction duration, int priority, es.ull.iis.simulation.core.WorkGroup wg) {
    	final TimeDrivenActivityWorkGroup aWg = new TimeDrivenActivityWorkGroup(this, workGroupTable.size(), duration, priority, (WorkGroup)wg);
        workGroupTable.add(aWg);
        return aWg;
    }
    
	@Override
    public TimeDrivenActivityWorkGroup addWorkGroup(TimeFunction duration, int priority, es.ull.iis.simulation.core.WorkGroup wg, Condition cond) {
    	final TimeDrivenActivityWorkGroup aWg = new TimeDrivenActivityWorkGroup(this, workGroupTable.size(), duration, priority, (WorkGroup)wg, cond); 
        workGroupTable.add(aWg);
        return aWg;
    }
    
	@Override
    public TimeDrivenActivityWorkGroup addWorkGroup(long duration, int priority, es.ull.iis.simulation.core.WorkGroup wg) {    	
        return addWorkGroup(TimeFunctionFactory.getInstance("ConstantVariate", duration), priority, wg);
    }
    
	@Override
    public TimeDrivenActivityWorkGroup addWorkGroup(long duration, int priority, es.ull.iis.simulation.core.WorkGroup wg, Condition cond) {    	
        return addWorkGroup(TimeFunctionFactory.getInstance("ConstantVariate", duration), priority, wg, cond);
    }

	@Override
	public FlowDrivenActivityWorkGroup addWorkGroup(es.ull.iis.simulation.core.flow.InitializerFlow initFlow,
			es.ull.iis.simulation.core.flow.FinalizerFlow finalFlow, int priority, es.ull.iis.simulation.core.WorkGroup wg, Condition cond) {
		FlowDrivenActivityWorkGroup aWg = new FlowDrivenActivityWorkGroup(this, workGroupTable.size(), initFlow, finalFlow, priority, (WorkGroup)wg, cond);
		workGroupTable.add(aWg);
		// Activities with Flow-driven workgroups cannot be presential nor interruptible
		modifiers.add(Modifier.NONPRESENTIAL);
		if (modifiers.contains(Modifier.INTERRUPTIBLE)) {
			error("Trying to add a flow-driven workgroup to an interruptible activity. This attribute will be overriden to ensure proper functioning");
			modifiers.remove(Modifier.INTERRUPTIBLE);
		}
		return aWg;
	}

	@Override
	public FlowDrivenActivityWorkGroup addWorkGroup(es.ull.iis.simulation.core.flow.InitializerFlow initFlow,
			es.ull.iis.simulation.core.flow.FinalizerFlow finalFlow, int priority, es.ull.iis.simulation.core.WorkGroup wg) {
		return addWorkGroup(initFlow, finalFlow, priority, wg, new TrueCondition());
	}

	@Override
	public FlowDrivenActivityWorkGroup addWorkGroup(es.ull.iis.simulation.core.flow.InitializerFlow initFlow, 
			es.ull.iis.simulation.core.flow.FinalizerFlow finalFlow, es.ull.iis.simulation.core.WorkGroup wg) {
		return addWorkGroup(initFlow, finalFlow, 0, wg, new TrueCondition());
	}

	@Override
	public FlowDrivenActivityWorkGroup addWorkGroup(es.ull.iis.simulation.core.flow.InitializerFlow initFlow, 
			es.ull.iis.simulation.core.flow.FinalizerFlow finalFlow, es.ull.iis.simulation.core.WorkGroup wg, Condition cond) {
		return addWorkGroup(initFlow, finalFlow, 0, wg, cond);
	}
	
    /**
     * Creates a new WG for this activity, with the specified priority and using the resource
     * types indicated by <code>wg</code>.
     * @param priority Priority of the WG
     * @param wg The set of pairs <ResurceType, amount> which can be used to carry out this activity
     * @return The identifier of the new WG.
     */
    public int addWorkGroup(int priority, es.ull.iis.simulation.parallel.WorkGroup wg) {
    	final int wgId = workGroupTable.size();
        workGroupTable.add(new ActivityWorkGroup(this, wgId, priority, wg));
        return wgId;
    }
    
    /**
     * Creates a new WG for this activity, with the specified priority and using the resource
     * types indicated by <code>wg</code>. This WG is only available if <code>cond</code> is 
     * <code>true</code>.
     * @param priority Priority of the WG
     * @param wg The set of pairs <ResurceType, amount> which can be used to carry out this activity
     * @param cond Availability condition
     * @return The identifier of the new WG.
     */
    public int addWorkGroup(int priority, es.ull.iis.simulation.parallel.WorkGroup wg, Condition cond) {
    	final int wgId = workGroupTable.size();
        workGroupTable.add(new ActivityWorkGroup(this, wgId, priority, wg, cond));
        return wgId;
    }
    
    /**
     * Creates a new WG for this activity with the highest level of priority and using the 
     * resource types indicated by <code>wg</code>.
     * @param wg The set of pairs <ResurceType, amount> which can be used to carry out this activity
     * @return The identifier of the new WG.
     */
    public int addWorkGroup(es.ull.iis.simulation.parallel.WorkGroup wg) {    	
        return addWorkGroup(0, wg);
    }
    
    /**
     * Creates a new WG for this activity with the highest level of priority and using the 
     * resource types indicated by <code>wg</code>. This WG is only available if 
     * <code>cond</code> is <code>true</code>.
     * @param wg The set of pairs <ResurceType, amount> which can be used to carry out this activity
     * @param cond Availability condition
     * @return The identifier of the new WG.
     */
    public int addWorkGroup(es.ull.iis.simulation.parallel.WorkGroup wg, Condition cond) {    	
        return addWorkGroup(0, wg, cond);
    }

    /**
     * Returns an iterator over the WGs of this activity.
     * @return An iterator over the WGs that can perform this activity.
     */
    public Iterator<ActivityWorkGroup> iterator() {
    	return workGroupTable.iterator();
    }

    @Override
    public ActivityWorkGroup getWorkGroup(int wgId) {
        final Iterator<ActivityWorkGroup> iter = workGroupTable.iterator();
        while (iter.hasNext()) {
        	final ActivityWorkGroup opc = iter.next();
        	if (opc.getIdentifier() == wgId)
        		return opc;        	
        }
        return null;
    }
    
	/**
     * Checks if this activity can be carried out with any of its WGs. Firstly checks if 
     * the activity is not potentially feasible, then goes through the WGs looking for an 
     * appropriate one. If this activity can't be performed with any of the WGs it's marked 
     * as not potentially feasible. 
     * @param wi Work Item wanting to carry out this activity 
     * @return <code>True</code> if this activity can be carried out with any one of its 
     * WGs. <code>False</code> in other case.
     */
    protected boolean isFeasible(WorkItem wi) {
    	if (!stillFeasible)
    		return false;
    	// WGs with the same priority are traversed in random order
        final Iterator<ActivityWorkGroup> iter = workGroupTable.randomIterator();
        while (iter.hasNext()) {
        	final ActivityWorkGroup wg = iter.next();
            if (wg.isFeasible(wi)) {
                wi.setExecutionWG(wg);
        		debug("Can be carried out by\t" + wi.getElement().getIdentifier() + "\t" + wi.getExecutionWG());
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
    protected void resetFeasible() {
    	stillFeasible = true;
    }
    
    /**
     * Adds a work item to the queue.
     * @param wi Work Item added
     */
    protected synchronized void queueAdd(WorkItem wi) {
        manager.queueAdd(wi);
    	queueSize++;
		wi.getElement().incInQueue(wi);
		wi.getCurrentFlow().inqueue(wi.getElement());
    }
    
    /**
     * Removes a specific work item from the queue.
     * @param wi Work Item that must be removed from the queue.
     */
    protected void queueRemove(WorkItem wi) {
    	manager.queueRemove(wi);
    	queueSize--;
		wi.getElement().decInQueue(wi);
    }

    /**
     * Returns how many work items are waiting to carry out this activity. 
     * @return The size of this activity's queue
     */
    public int getQueueSize() {
    	return queueSize;    	
    }
    
	@Override
	public String getObjectTypeIdentifier() {
		return "ACT";
	}

	@Override
	public long getTs() {
		return manager.getTs();
	}
	
	/**
	 * Adds a new {@link ResourceType} to the cancellation list.
	 * @param rt Resource type
	 * @param duration Duration of the cancellation.
	 */
	public void addResourceCancelation(ResourceType rt, long duration) {
		cancellationList.put(rt, duration);
	}
	
	/**
	 * Returns the duration of the cancellation of a resource with the specified
	 * resource type.
	 * @param rt Resource Type
	 * @return The duration of the cancellation
	 */
	public long getResourceCancelation(ResourceType rt) {
		final Long dur = cancellationList.get(rt);
		if (dur == null)
			return 0;
		return dur;
	}
	
	/**
	 * Returns true if this activity is the main activity that an element can do.
	 * @return True if the activity requires an element MUTEX.
	 */
	public boolean mainElementActivity() {
		return !isNonPresential();		
	}
	
	/**
	 * Requests this activity. Checks if this activity is feasible by the
	 * specified work item. If the activity is feasible, {@link #carryOut(WorkItem)}
	 * is invoked; in other case, the work item is added to this activity's queue.
	 * @param wItem Work Item requesting this activity.
	 */
	public void request(WorkItem wItem) {
		final Element elem = wItem.getElement();
		simul.getInfoHandler().notifyInfo(new ElementActionInfo(this.simul, wItem, elem, ElementActionInfo.Type.REQACT, elem.getTs()));
		if (elem.isDebugEnabled())
			elem.debug("Requests\t" + this + "\t" + description);

		queueAdd(wItem); // The element is introduced in the queue
		manager.notifyElement(wItem);
	}

	/**
	 * Catches the resources required to carry out this activity.
	 * @param wItem Work item requesting this activity
	 */
	public void carryOut(WorkItem wItem) {
		final Element elem = wItem.getElement();
		wItem.getCurrentFlow().afterStart(elem);
		long auxTs = wItem.catchResources();

		if (wItem.getExecutionWG() instanceof FlowDrivenActivityWorkGroup) {
			simul.getInfoHandler().notifyInfo(new ElementActionInfo(simul, wItem, elem, ElementActionInfo.Type.STAACT, elem.getTs()));
			elem.debug("Starts\t" + this + "\t" + description);
			InitializerFlow initialFlow = ((FlowDrivenActivityWorkGroup)wItem.getExecutionWG()).getInitialFlow();
			// The inner request is scheduled a bit later
			elem.addDelayedRequestEvent(initialFlow, wItem.getWorkThread().getInstanceDescendantWorkThread());
		}
		else if (wItem.getExecutionWG() instanceof TimeDrivenActivityWorkGroup) {
			// The first time the activity is carried out (useful only for interruptible activities)
			if (wItem.getTimeLeft() == -1) {
				wItem.setTimeLeft(((TimeDrivenActivityWorkGroup)wItem.getExecutionWG()).getDurationSample(elem));
				simul.getInfoHandler().notifyInfo(new ElementActionInfo(this.simul, wItem, elem, ElementActionInfo.Type.STAACT, elem.getTs()));
				elem.debug("Starts\t" + this + "\t" + description);			
			}
			else {
				simul.getInfoHandler().notifyInfo(new ElementActionInfo(this.simul, wItem, elem, ElementActionInfo.Type.RESACT, elem.getTs()));
				elem.debug("Continues\t" + this + "\t" + description);						
			}
			final long finishTs = elem.getTs() + wItem.getTimeLeft();
			// The required time for finishing the activity is reduced (useful only for interruptible activities)
			if (isInterruptible() && (finishTs - auxTs > 0))
				wItem.setTimeLeft(finishTs - auxTs);				
			else {
				auxTs = finishTs;
				wItem.setTimeLeft(0);
			}
			elem.addEvent(elem.new FinishFlowEvent(auxTs, wItem.getCurrentFlow(), wItem.getWorkThread()));
		} 
		else {
			elem.error("Trying to carry out unexpected type of workgroup");
		}

	}

	/**
	 * Releases the resources required to carry out this activity.
	 * @param wItem Work item which requested this activity
	 * @return True if this activity was actually finished; false in other case
	 */
	public boolean finish(WorkItem wItem) {
		final Element elem = wItem.getElement();

		wItem.releaseCaughtResources();
		
		if (wItem.getExecutionWG() instanceof FlowDrivenActivityWorkGroup) {
			simul.getInfoHandler().notifyInfo(new ElementActionInfo(simul, wItem, elem, ElementActionInfo.Type.ENDACT, elem.getTs()));
			if (elem.isDebugEnabled())
				elem.debug("Finishes\t" + this + "\t" + description);
	        return true;
		}
		else if (wItem.getExecutionWG() instanceof TimeDrivenActivityWorkGroup) {
			if (!isNonPresential())
				elem.setCurrent(null);

			assert wItem.getTimeLeft() >= 0 : "Time left < 0: " + wItem.getTimeLeft();
			if (wItem.getTimeLeft() == 0) {
				simul.getInfoHandler().notifyInfo(new ElementActionInfo(this.simul, wItem, elem, ElementActionInfo.Type.ENDACT, elem.getTs()));
				if (elem.isDebugEnabled())
					elem.debug("Finishes\t" + this + "\t" + description);
				// Checks if there are pending activities that haven't noticed the
				// element availability
				if (!isNonPresential())
					elem.addAvailableElementEvents();
				return true;
			}
			else {
				simul.getInfoHandler().notifyInfo(new ElementActionInfo(this.simul, wItem, elem, ElementActionInfo.Type.INTACT, elem.getTs()));
				if (elem.isDebugEnabled())
					elem.debug("Finishes part of \t" + this + "\t" + description + "\t" + wItem.getTimeLeft());				
				// The element is introduced in the queue
				queueAdd(wItem); 
				// FIXME: ¿No debería hacer un availableElements también?
			}
		}
		return false;		
	}
	
	@Override
	public int getWorkGroupSize() {
		return workGroupTable.size();
	}
	
	/**
	 * @return the virtualFinalFlow
	 */
	protected BasicFlow getVirtualFinalFlow() {
		return virtualFinalFlow;
	}

}
