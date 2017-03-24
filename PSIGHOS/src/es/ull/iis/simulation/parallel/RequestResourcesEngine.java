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
import es.ull.iis.simulation.model.flow.Flow;
import es.ull.iis.simulation.model.flow.StructuredFlow;
import es.ull.iis.simulation.info.ElementActionInfo;
import es.ull.iis.simulation.model.ElementInstance;
import es.ull.iis.simulation.model.engine.EngineObject;
import es.ull.iis.simulation.model.flow.BasicFlow;
import es.ull.iis.simulation.model.flow.InitializerFlow;
import es.ull.iis.simulation.model.flow.RequestResourcesFlow;
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
public class RequestResourcesEngine extends EngineObject implements es.ull.iis.simulation.model.engine.RequestResourcesEngine {
    /** Total amount of {@link ElementInstance} waiting for carrying out this activity */
    protected int queueSize = 0;
    /** The associated {@link RequestResourcesFlow} */
    final protected RequestResourcesFlow modelReq;

	/**
     * Creates a new activity with the highest priority.
     * @param simul The {@link ParallelSimulationEngine} where this activity is used
     * @param description A short text describing this activity
     */
    public RequestResourcesEngine(ParallelSimulationEngine simul, RequestResourcesFlow modelReq) {
    	super(modelReq.getIdentifier(), simul,"REQ");
        this.modelReq = modelReq;
    }

	public RequestResourcesFlow getModelReqFlow() {
		return modelReq;
	}

	@Override
	public synchronized void queueAdd(ElementInstance fe) {
        modelReq.getManager().queueAdd(fe);
    	queueSize++;
		fe.getElement().incInQueue(fe);
		modelReq.inqueue(fe);
    }
    
	@Override
	// TODO: Check why it's not synchronized
    public void queueRemove(ElementInstance fe) {
		modelReq.getManager().queueRemove(fe);
    	queueSize--;
		fe.getElement().decInQueue(fe);
    }

    /**
     * Returns how many element instances are waiting to carry out this activity. 
     * @return The size of this activity's queue
     */
    public int getQueueSize() {
    	return queueSize;    	
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
		final ElementEngine elem = wItem.getElement();
		simul.notifyInfo(null).notifyInfo(new ElementActionInfo(this.simul, wItem, elem, ElementActionInfo.Type.REQ, elem.getTs()));
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
		final ElementEngine elem = wItem.getElement();
		wItem.getCurrentFlow().afterStart(elem);
		long auxTs = wItem.catchResources();

		if (wItem.getExecutionWG() instanceof FlowDrivenActivityWorkGroup) {
			simul.notifyInfo(null).notifyInfo(new ElementActionInfo(simul, wItem, elem, ElementActionInfo.Type.START, elem.getTs()));
			elem.debug("Starts\t" + this + "\t" + description);
			InitializerFlow initialFlow = ((FlowDrivenActivityWorkGroup)wItem.getExecutionWG()).getInitialFlow();
			// The inner request is scheduled a bit later
			elem.addDelayedRequestEvent(initialFlow, wItem.getWorkThread().getInstanceDescendantWorkThread());
		}
		else if (wItem.getExecutionWG() instanceof TimeDrivenActivityWorkGroup) {
			// The first time the activity is carried out (useful only for interruptible activities)
			if (wItem.getTimeLeft() == -1) {
				wItem.setTimeLeft(((TimeDrivenActivityWorkGroup)wItem.getExecutionWG()).getDurationSample(elem));
				simul.notifyInfo(null).notifyInfo(new ElementActionInfo(this.simul, wItem, elem, ElementActionInfo.Type.START, elem.getTs()));
				elem.debug("Starts\t" + this + "\t" + description);			
			}
			else {
				simul.notifyInfo(null).notifyInfo(new ElementActionInfo(this.simul, wItem, elem, ElementActionInfo.Type.RESACT, elem.getTs()));
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
		final ElementEngine elem = wItem.getElement();

		wItem.releaseCaughtResources();
		
		if (wItem.getExecutionWG() instanceof FlowDrivenActivityWorkGroup) {
			simul.notifyInfo(null).notifyInfo(new ElementActionInfo(simul, wItem, elem, ElementActionInfo.Type.END, elem.getTs()));
			if (elem.isDebugEnabled())
				elem.debug("Finishes\t" + this + "\t" + description);
	        return true;
		}
		else if (wItem.getExecutionWG() instanceof TimeDrivenActivityWorkGroup) {
			if (!isNonPresential())
				elem.setCurrent(null);

			assert wItem.getTimeLeft() >= 0 : "Time left < 0: " + wItem.getTimeLeft();
			if (wItem.getTimeLeft() == 0) {
				simul.notifyInfo(null).notifyInfo(new ElementActionInfo(this.simul, wItem, elem, ElementActionInfo.Type.END, elem.getTs()));
				if (elem.isDebugEnabled())
					elem.debug("Finishes\t" + this + "\t" + description);
				// Checks if there are pending activities that haven't noticed the
				// element availability
				if (!isNonPresential())
					elem.addAvailableElementEvents();
				return true;
			}
			else {
				simul.notifyInfo(null).notifyInfo(new ElementActionInfo(this.simul, wItem, elem, ElementActionInfo.Type.INTACT, elem.getTs()));
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
