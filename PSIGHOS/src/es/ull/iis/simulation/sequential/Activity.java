/**
 * 
 */
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
import es.ull.iis.util.RandomPermutation;

/**
 * @author Iván Castilla
 *
 */
public class Activity extends BasicStep implements es.ull.iis.simulation.core.Activity<ActivityWorkGroup, WorkThread, Resource> {
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
        super(simul, description, priority);
        this.modifiers = modifiers;
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
	
	protected BasicFlow getVirtualFinalFlow() {
		return virtualFinalFlow;
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
    
	@Override
	public String getObjectTypeIdentifier() {
		return "ACT";
	}

	/**
	 * Checks if the element is valid to perform this activity.
	 * An element is valid to perform an activity is it's not currently carrying
	 * out another activity or this activity is non presential.
	 * @param wThread Work thread requesting this activity
	 * @return True if the element is valid, false in other case.
	 */
	@Override
	public boolean validElement(WorkThread wThread) {
		return (wThread.getElement().getCurrent() == null || isNonPresential());
	}

	/**
	 * Requests this activity. Checks if this activity is feasible by the
	 * specified work item. If the activity is feasible, <code>carryOut</code>
	 * is called; in other case, the work item is added to this activity's queue.
	 * @param wThread Work thread requesting this activity.
	 */
	@Override
	public void request(WorkThread wThread) {
		final Element elem = wThread.getElement();
		simul.getInfoHandler().notifyInfo(new ElementActionInfo(simul, wThread, elem, ElementActionInfo.Type.REQACT, elem.getTs()));
		if (elem.isDebugEnabled())
			elem.debug("Requests\t" + this + "\t" + description);
		// If the element is not performing a presential activity yet or the
		// activity to be requested is non presential
		if (validElement(wThread)) {
			// There are enough resources to perform the activity
			final ArrayDeque<Resource> solution = isFeasible(wThread); 
			if (solution != null) {
				carryOut(wThread, solution);
			}
			else {
				queueAdd(wThread); // The element is introduced in the queue
			}
		} else {
			queueAdd(wThread); // The element is introduced in the queue
		}
	
	}

    @Override
    public ArrayDeque<Resource> isFeasible(WorkThread wt) {
    	if (!stillFeasible)
    		return null;
        Iterator<ActivityWorkGroup> iter = workGroupTable.randomIterator();
        while (iter.hasNext()) {
        	ActivityWorkGroup wg = iter.next();
        	ArrayDeque<Resource> solution = wg.isFeasible(wt); 
            if (solution != null) {
                wt.setExecutionWG(wg);
        		debug("Can be carried out by\t" + wt.getElement().getIdentifier() + "\t" + wt.getExecutionWG());
                if (!isNonPresential())
                	wt.getElement().setCurrent(wt);
                return solution;
            }            
        }
        stillFeasible = false;
        return null;
    }

	/**
	 * Catches the resources required to carry out this activity. In case it used a time-driven workgroup,
	 * updates the element's timestamp, catch the corresponding resources and produces a 
	 * <code>FinishFlowEvent</code>. In case it used a flow-driven workgroup, catches the resources required 
	 * and launches the initial flow.
	 * @param wThread Work thread requesting this activity
	 */
	@Override
	public void carryOut(WorkThread wThread, ArrayDeque<Resource> solution) {
		final Element elem = wThread.getElement();
		wThread.getSingleFlow().afterStart(elem);
		long auxTs = wThread.acquireResources(solution, -id);
		// Before this line, the code is common for time- and flow- driven WGs
		
		if (wThread.getExecutionWG() instanceof FlowDrivenActivityWorkGroup) {
			simul.getInfoHandler().notifyInfo(new ElementActionInfo(simul, wThread, elem, ElementActionInfo.Type.STAACT, elem.getTs()));
			elem.debug("Starts\t" + this + "\t" + description);
			InitializerFlow initialFlow = ((FlowDrivenActivityWorkGroup)wThread.getExecutionWG()).getInitialFlow();
			elem.addRequestEvent(initialFlow, wThread.getInstanceDescendantWorkThread(initialFlow));
		}
		else if (wThread.getExecutionWG() instanceof TimeDrivenActivityWorkGroup) {
			// The first time the activity is carried out (useful only for interruptible activities)
			if (wThread.getTimeLeft() == -1) {
				wThread.setTimeLeft(((TimeDrivenActivityWorkGroup)wThread.getExecutionWG()).getDurationSample(elem));
				simul.getInfoHandler().notifyInfo(new ElementActionInfo(this.simul, wThread, elem, ElementActionInfo.Type.STAACT, elem.getTs()));
				elem.debug("Starts\t" + this + "\t" + description);			
			}
			else {
				simul.getInfoHandler().notifyInfo(new ElementActionInfo(this.simul, wThread, elem, ElementActionInfo.Type.RESACT, elem.getTs()));
				elem.debug("Continues\t" + this + "\t" + description);						
			}
			long finishTs = elem.getTs() + wThread.getTimeLeft();
			// The required time for finishing the activity is reduced (useful only for interruptible activities)
			if (isInterruptible() && (finishTs - auxTs > 0.0))
				wThread.setTimeLeft(finishTs - auxTs);				
			else {
				auxTs = finishTs;
				wThread.setTimeLeft(0);
			}
			elem.addEvent(elem.new FinishFlowEvent(auxTs, wThread.getSingleFlow(), wThread));
		}
		else {
			elem.error("Trying to carry out unexpected type of workgroup");
		}
	}

	/**
	 * Releases the resources required to carry out this activity.
	 * @param wThread Work thread which requested this activity
	 * @return True if this activity was actually finished; false in other case
	 */
	@Override
	public boolean finish(WorkThread wThread) {
		final Element elem = wThread.getElement();

		final ArrayList<ActivityManager> amList = wThread.releaseResources(-id);

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

		if (wThread.getExecutionWG() instanceof FlowDrivenActivityWorkGroup) {
			simul.getInfoHandler().notifyInfo(new ElementActionInfo(simul, wThread, elem, ElementActionInfo.Type.ENDACT, elem.getTs()));
			if (elem.isDebugEnabled())
				elem.debug("Finishes\t" + this + "\t" + description);
	        return true;
		}
		else if (wThread.getExecutionWG() instanceof TimeDrivenActivityWorkGroup) {
			// FIXME: CUIDADO CON ESTO!!! Nunca debería ser menor
			if (wThread.getTimeLeft() <= 0.0) {
				simul.getInfoHandler().notifyInfo(new ElementActionInfo(this.simul, wThread, elem, ElementActionInfo.Type.ENDACT, elem.getTs()));
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
				simul.getInfoHandler().notifyInfo(new ElementActionInfo(this.simul, wThread, elem, ElementActionInfo.Type.INTACT, elem.getTs()));
				if (elem.isDebugEnabled())
					elem.debug("Finishes part of \t" + this + "\t" + description + "\t" + wThread.getTimeLeft());				
				// The element is introduced in the queue
				queueAdd(wThread); 
			}
		}
		return false;		
	}
	
}
