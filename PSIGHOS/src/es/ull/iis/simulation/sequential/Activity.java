package es.ull.iis.simulation.sequential;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.EnumSet;
import java.util.Iterator;
import java.util.Set;

import es.ull.iis.function.TimeFunction;
import es.ull.iis.function.TimeFunctionFactory;
import es.ull.iis.simulation.condition.Condition;
import es.ull.iis.simulation.condition.TrueCondition;
import es.ull.iis.simulation.info.ElementActionInfo;
import es.ull.iis.simulation.sequential.flow.BasicFlow;
import es.ull.iis.simulation.sequential.flow.InitializerFlow;
import es.ull.iis.util.PrioritizedTable;
import es.ull.iis.util.RandomPermutation;

/**
 * A task which could be carried out by an element. An activity is characterized by its priority
 * and a set of workgropus. Each workgroup represents a combination of resource types required 
 * for carrying out the activity.<p>
 * Each activity belongs to an Activity Manager, which handles the way the activity is accessed.<p>
 * An activity is potentially feasible if there is no proof that there are not enough resources
 * to perform it. An activity is feasible if it's potentially feasible and there is at least one
 * workgroup with enough available resources to perform the activity.<p>
 * An activity can be requested by a valid element, that is, check if the activity is feasible. 
 * If the activity is not feasible, the element is added to a queue until new resources are 
 * available. If the activity is feasible, the element "carries out" the activity, that is, 
 * catches the resources needed to perform the activity. Whenever it is determined that the 
 * activity has finished, the element releases the resources previously caught.<p>
 * An activity can also define cancellation periods for each one of the resource types it uses. 
 * If an element takes a resource belonging to one of the cancellation periods of the activity, this
 * resource can't be used during a period of time after the activity finishes.
 * FIXME: Complete and rewrite (original description for TimeDrivenActivities)
 *  A task which could be carried out by an element in a specified time. This kind of activities
 * can be characterized by a priority value, presentiality, interruptibility, and a set of 
 * workgropus. Each workgroup represents a combination of resource types required for carrying out 
 * the activity, and the duration of the activity when performed with this workgroup.<p>
 * By default, time-driven activities are presential, that is, an element carrying out this 
 * activity can't perform simultaneously any other presential activity; and ininterruptible, i.e., 
 * once started, the activity keeps its resources until it's finished, even if the resources become 
 * unavailable while the activity is being performed. This two characteristics are customizable by 
 * means of the <code>Modifier</code> enum type. An activity can be <code>NONPRESENTIAL</code>, when 
 * the element can perform other activities while it's performing this one; and <code>INTERRUPTIBLE</code>, 
 * when the activity can be interrupted, and later continued, if the resources become unavailable 
 * while the activity is being performed.

 * @author Carlos Martín Galán
 */
public class Activity extends TimeStampedSimulationObject implements es.ull.iis.simulation.core.Activity {
	/** 
	 * An artificially created final node. This flow informs the flow-driven
	 * work groups that they have being finalized.
	 */
	private BasicFlow virtualFinalFlow = new BasicFlow(simul) {
		public void addPredecessor(es.ull.iis.simulation.core.flow.Flow newFlow) {}

		public void request(WorkThread wThread) {
			wThread.notifyEnd();
		}

		public void link(es.ull.iis.simulation.core.flow.Flow successor) {}

		public void setRecursiveStructureLink(es.ull.iis.simulation.core.flow.StructuredFlow parent, Set<es.ull.iis.simulation.core.flow.Flow> visited) {}
		
	};
	/** The set of modifiers of this activity. */
    protected final EnumSet<Modifier> modifiers;
    /** Priority. The lowest the value, the highest the priority */
    protected int priority = 0;
    /** A brief description of the activity */
    protected final String description;
    /** Total of work items waiting for carrying out this activity */
    protected int queueSize = 0;
    /** Activity manager this activity belongs to */
    protected ActivityManager manager = null;
    /** Work Groups available to perform this activity */
    protected final PrioritizedTable<ActivityWorkGroup> workGroupTable;
    /** Indicates that the activity is potentially feasible. */
    protected boolean stillFeasible = true;
    /** Resources cancellation table */
    protected final ArrayList<CancelListEntry> cancellationList;
    /** Last activity start */
    protected long lastStartTs = 0;
    /** Last activity finish */
    protected long lastFinishTs = 0;

	/**
     * Creates a new activity with 0 priority.
     * @param simul Simulation which this activity is attached to.
     * @param description A short text describing this Activity.
     */
    public Activity(Simulation simul, String description) {
        this(simul, description, 0, EnumSet.noneOf(Modifier.class));
    }

    /**
     * Creates a new activity.
     * @param simul Simulation which this activity is attached to.
     * @param description A short text describing this Activity.
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
        this.description = description;
        this.priority = priority;
        workGroupTable = new PrioritizedTable<ActivityWorkGroup>();
        simul.add(this);
		cancellationList = new ArrayList<CancelListEntry>();
        this.modifiers = modifiers;
    }

    /*
     * (non-Javadoc)
     * @see es.ull.iis.simulation.Describable#getDescription()
     */
	public String getDescription() {
		return description;
	}

	/**
     * Returns the activity's priority.
     * @return Value of the activity's priority.
     */
    public int getPriority() {
        return priority;
    }
    
    /**
     * Returns the activity manager this activity belongs to.
     * @return The activity manager this activity belongs to.
     */
    public ActivityManager getManager() {
        return manager;
    }

    /**
     * Sets the activity manager this activity type belongs to. It also
     * adds this activity to the manager.
     * @param manager The activity manager.
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
	
	/**
     * Creates a new workgroup for this activity using the specified wg.
     * @param priority Priority of the workgroup
     * @param wg The set of pairs <ResurceType, amount> which will perform the activity
     * @return The new workgroup's identifier.
     */
    public int addWorkGroup(int priority, WorkGroup wg) {
    	int wgId = workGroupTable.size();
        workGroupTable.add(new ActivityWorkGroup(this, wgId, priority, wg));
        return wgId;
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
    	int wgId = workGroupTable.size();
        workGroupTable.add(new ActivityWorkGroup(this, wgId, priority, wg, cond));
        return wgId;
    }
    
    /**
     * Creates a new workgroup for this activity with the highest level of priority using 
     * the specified wg.
     * @param wg The set of pairs <ResurceType, amount> which will perform the activity
     * @return The new workgroup's identifier.
     */
    public int addWorkGroup(WorkGroup wg) {    	
        return addWorkGroup(0, wg);
    }
    
    /**
     * Creates a new workgroup for this activity with the highest level of priority using 
     * the specified wg. This workgroup is only available if cond is true.
     * @param wg The set of pairs <ResurceType, amount> which will perform the activity
     * @param cond Availability condition
     * @return The new workgroup's identifier.
     */
    public int addWorkGroup(WorkGroup wg, Condition cond) {    	
        return addWorkGroup(0, wg, cond);
    }

	@Override
    public TimeDrivenActivityWorkGroup addWorkGroup(TimeFunction duration, int priority, es.ull.iis.simulation.core.WorkGroup wg, Condition cond) {
		TimeDrivenActivityWorkGroup aWg = new TimeDrivenActivityWorkGroup(this, workGroupTable.size(), duration, priority, (WorkGroup)wg, cond); 
        workGroupTable.add(aWg);
        return aWg;
    }
    
	@Override
    public TimeDrivenActivityWorkGroup addWorkGroup(TimeFunction duration, int priority, es.ull.iis.simulation.core.WorkGroup wg) {
		return addWorkGroup(duration, priority, (WorkGroup)wg, new TrueCondition());
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
    public FlowDrivenActivityWorkGroup addWorkGroup(es.ull.iis.simulation.core.flow.InitializerFlow initialFlow, 
    		es.ull.iis.simulation.core.flow.FinalizerFlow finalFlow, es.ull.iis.simulation.core.WorkGroup wg) {    	
        return addWorkGroup(initialFlow, finalFlow, 0, wg);
    }
    
	@Override
    public FlowDrivenActivityWorkGroup addWorkGroup(es.ull.iis.simulation.core.flow.InitializerFlow initialFlow, 
    		es.ull.iis.simulation.core.flow.FinalizerFlow finalFlow, es.ull.iis.simulation.core.WorkGroup wg, Condition cond) {    	
        return addWorkGroup(initialFlow, finalFlow, 0, wg, cond);
    }
    
    /**
     * Returns an iterator over the workgroups of this activity.
     * @return An iterator over the workgroups that can perform this activity.
     */
    public Iterator<ActivityWorkGroup> iterator() {
    	return workGroupTable.iterator();
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
     * Checks if this activity can be performed with any of its workgroups. Firstly 
     * checks if the activity is not potentially feasible, then goes through the 
     * workgroups looking for an appropriate one. If the activity can't be performed with 
     * any of the workgroups it's marked as not potentially feasible. 
     * @param wi Work Item wanting to perform the activity 
     * @return The set of resources which compound the solution. Null if there are not enough
     * resources to carry out the activity by using this workgroup.
     */
    protected ArrayDeque<Resource> isFeasible(WorkItem wi) {
    	if (!stillFeasible)
    		return null;
        Iterator<ActivityWorkGroup> iter = workGroupTable.randomIterator();
        while (iter.hasNext()) {
        	ActivityWorkGroup wg = iter.next();
        	ArrayDeque<Resource> solution = wg.isFeasible(wi); 
            if (solution != null) {
                wi.setExecutionWG(wg);
        		debug("Can be carried out by\t" + wi.getElement().getIdentifier() + "\t" + wi.getExecutionWG());
                if (!isNonPresential())
                	wi.getElement().setCurrent(wi);
                return solution;
            }            
        }
        stillFeasible = false;
        return null;
    }

    /**
     * Sets the activity as potentially feasible.
     */
    protected void resetFeasible() {
    	stillFeasible = true;
    }
    
    /**
     * Add a work item to the element queue.
     * @param wi Work Item added
     */
    protected void queueAdd(WorkItem wi) {
        manager.queueAdd(wi);
    	queueSize++;
		wi.getElement().incInQueue(wi);
		wi.getFlow().inqueue(wi.getElement());
    }
    
    /**
     * Remove a specific work item from the element queue.
     * @param wi Work Item that must be removed from the element queue.
     */
    protected void queueRemove(WorkItem wi) {
    	manager.queueRemove(wi);
    	queueSize--;
		wi.getElement().decInQueue(wi);
    }

    /**
     * Returns the size of this activity's queue 
     * @return the size of this activity's queue
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
	 * Adds a new ResouceType to the cancellation list.
	 * @param rt Resource type
	 * @param duration Duration of the cancellation.
	 */
	public void addResourceCancelation(ResourceType rt, long duration) {
		CancelListEntry entry = new CancelListEntry(rt, duration);
		cancellationList.add(entry);
	}
	
	/** 
	 * Elements of the cancellation list.
	 * @author ycallero
	 *
	 */
	public class CancelListEntry {		
		public ResourceType rt;
		public long dur;
		
		CancelListEntry(ResourceType rt, long dur) {
			this.rt = rt;
			this.dur = dur;
		}
	}
	
	/**
	 * Checks if the element is valid to perform this activity.
	 * An element is valid to perform an activity is it's not currently carrying
	 * out another activity or this activity is non presential.
	 * @param wItem Work item requesting this activity
	 * @return True if the element is valid, false in other case.
	 */
	public boolean validElement(WorkItem wItem) {
		return (wItem.getElement().getCurrent() == null || isNonPresential());
	}

	
	/**
	 * Requests this activity. Checks if this activity is feasible by the
	 * specified work item. If the activity is feasible, <code>carryOut</code>
	 * is called; in other case, the work item is added to this activity's queue.
	 * @param wItem Work Item requesting this activity.
	 */
	public void request(WorkItem wItem) {
		final Element elem = wItem.getElement();
		simul.getInfoHandler().notifyInfo(new ElementActionInfo(simul, wItem, elem, ElementActionInfo.Type.REQACT, elem.getTs()));
		if (elem.isDebugEnabled())
			elem.debug("Requests\t" + this + "\t" + description);
		// If the element is not performing a presential activity yet or the
		// activity to be requested is non presential
		if (validElement(wItem)) {
			// There are enough resources to perform the activity
			final ArrayDeque<Resource> solution = isFeasible(wItem); 
			if (solution != null) {
				carryOut(wItem, solution);
			}
			else {
				queueAdd(wItem); // The element is introduced in the queue
			}
		} else {
			queueAdd(wItem); // The element is introduced in the queue
		}
	
	}

	/**
	 * Catches the resources required to carry out this activity. In case it used a time-driven workgroup,
	 * updates the element's timestamp, catch the corresponding resources and produces a 
	 * <code>FinishFlowEvent</code>. In case it used a flow-driven workgroup, catches the resources required 
	 * and launches the initial flow.
	 * @param wItem Work item requesting this activity
	 */
	public void carryOut(WorkItem wItem, ArrayDeque<Resource> solution) {
		final Element elem = wItem.getElement();
		wItem.getFlow().afterStart(elem);
		long auxTs = wItem.catchResources(solution);
		// Before this line, the code is common for time- and flow- driven WGs
		
		if (wItem.getExecutionWG() instanceof FlowDrivenActivityWorkGroup) {
			simul.getInfoHandler().notifyInfo(new ElementActionInfo(simul, wItem, elem, ElementActionInfo.Type.STAACT, elem.getTs()));
			elem.debug("Starts\t" + this + "\t" + description);
			InitializerFlow initialFlow = ((FlowDrivenActivityWorkGroup)wItem.getExecutionWG()).getInitialFlow();
			wItem.getWorkThread().getElement().addRequestEvent(initialFlow, wItem.getWorkThread().getInstanceDescendantWorkThread(initialFlow));
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
			long finishTs = elem.getTs() + wItem.getTimeLeft();
			// The required time for finishing the activity is reduced (useful only for interruptible activities)
			if (isInterruptible() && (finishTs - auxTs > 0.0))
				wItem.setTimeLeft(finishTs - auxTs);				
			else {
				auxTs = finishTs;
				wItem.setTimeLeft(0);
			}
			elem.addEvent(elem.new FinishFlowEvent(auxTs, wItem.getFlow(), wItem.getWorkThread()));
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

		final ArrayList<ActivityManager> amList = wItem.releaseCaughtResources();

		if (!isNonPresential())
			elem.setCurrent(null);

		final int[] order = RandomPermutation.nextPermutation(amList.size());
		for (int ind : order) {
			ActivityManager am = amList.get(ind);
			// FIXME: Esto debería ser un evento por cada AM
			am.availableResource();
		}

		// FIXME: Esto sustituye a lo anterior para que sea determinista
//		for (ActivityManager am : amList)
//			am.availableResource();

		if (wItem.getExecutionWG() instanceof FlowDrivenActivityWorkGroup) {
			simul.getInfoHandler().notifyInfo(new ElementActionInfo(simul, wItem, elem, ElementActionInfo.Type.ENDACT, elem.getTs()));
			if (elem.isDebugEnabled())
				elem.debug("Finishes\t" + this + "\t" + description);
	        return true;
		}
		else if (wItem.getExecutionWG() instanceof TimeDrivenActivityWorkGroup) {
			// FIXME: CUIDADO CON ESTO!!! Nunca debería ser menor
			if (wItem.getTimeLeft() <= 0.0) {
				simul.getInfoHandler().notifyInfo(new ElementActionInfo(this.simul, wItem, elem, ElementActionInfo.Type.ENDACT, elem.getTs()));
				if (elem.isDebugEnabled())
					elem.debug("Finishes\t" + this + "\t" + description);
				// Checks if there are pending activities that haven't noticed the
				// element availability
				if (!isNonPresential())
					elem.addAvailableElementEvents();
				return true;
			}
			// Added the condition(Lancaster compatibility), even when it should be unnecessary.
			else {
				simul.getInfoHandler().notifyInfo(new ElementActionInfo(this.simul, wItem, elem, ElementActionInfo.Type.INTACT, elem.getTs()));
				if (elem.isDebugEnabled())
					elem.debug("Finishes part of \t" + this + "\t" + description + "\t" + wItem.getTimeLeft());				
				// The element is introduced in the queue
				queueAdd(wItem); 
			}
		}
		return false;		
	}
	
	@Override
	public int getWorkGroupSize() {
		return workGroupTable.size();
	}
	
	public long getLastStartTs() {
		return lastStartTs;
	}


	public long getLastFinishTs() {
		return lastFinishTs;
	}

	public void setLastFinishTs(long lastFinishTs) {
		this.lastFinishTs = lastFinishTs;
	}

	protected BasicFlow getVirtualFinalFlow() {
		return virtualFinalFlow;
	}
	
}
