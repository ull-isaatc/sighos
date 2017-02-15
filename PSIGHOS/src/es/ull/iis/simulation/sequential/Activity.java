/**
 * 
 */
package es.ull.iis.simulation.sequential;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Collection;
import java.util.EnumSet;
import java.util.Map.Entry;
import java.util.Set;
import java.util.TreeMap;

import es.ull.iis.function.TimeFunction;
import es.ull.iis.function.TimeFunctionFactory;
import es.ull.iis.simulation.condition.Condition;
import es.ull.iis.simulation.condition.TrueCondition;
import es.ull.iis.simulation.model.flow.FinalizerFlow;
import es.ull.iis.simulation.model.flow.Flow;
import es.ull.iis.simulation.model.flow.InitializerFlow;
import es.ull.iis.simulation.model.flow.StructuredFlow;
import es.ull.iis.simulation.model.flow.TaskFlow;
import es.ull.iis.simulation.info.ElementActionInfo;
import es.ull.iis.simulation.info.ResourceInfo;
import es.ull.iis.simulation.model.ActivityWorkGroup.DrivenBy;
import es.ull.iis.simulation.model.flow.ActivityFlow;
import es.ull.iis.simulation.model.flow.BasicFlow;
import es.ull.iis.simulation.sequential.ActivityManager;
import es.ull.iis.simulation.sequential.ActivityWorkGroup;
import es.ull.iis.simulation.sequential.Element;
import es.ull.iis.simulation.sequential.Resource;
import es.ull.iis.simulation.sequential.ResourceType;
import es.ull.iis.simulation.sequential.Simulation;
import es.ull.iis.simulation.sequential.WorkThread;

/**
 * A flow which executes a single activity.
 * 
 *  TODO: Fix documentation... From here on belongs to the old "activity" class
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
 * @author Iván Castilla Rodríguez
 */
public class Activity extends RequestResources {
	/** 
	 * An artificially created final node. This flow informs the flow-driven
	 * work groups that they have being finalized.
	 */
	private BasicFlow virtualFinalFlow = new BasicFlow() {
		public void addPredecessor(Flow newFlow) {}

		public void request(WorkThread wThread) {
			wThread.notifyEnd();
		}

		public Flow link(Flow successor) {
			return successor;
		}

		public void setRecursiveStructureLink(StructuredFlow parent, Set<Flow> visited) {}
		
	};
    /** Resources cancellation table */
    protected final TreeMap<ResourceType, Long> cancellationList = new TreeMap<ResourceType, Long>();

	/**
     * Creates a new activity with 0 priority.
     * @param simul Simulation which this activity is attached to.
     * @param description A short text describing this Activity.
     */
    public Activity(Simulation simul, ActivityFlow modelAct) {
        super(simul, modelAct);
        final TreeMap<es.ull.iis.simulation.model.ResourceType, Long> originalList = modelAct.getCancellationList();
        for (Entry<es.ull.iis.simulation.model.ResourceType, Long> entry : originalList.entrySet()) {
        	cancellationList.put(simul.getResourceType(entry.getKey()), entry.getValue());
        }
    }

	/** 
	 * Returns <tt>true</tt> if the activity is non presential, i.e., an element can perform other 
	 * activities at the same time. 
	 * @return <tt>True</tt> if the activity is non presential, <tt>false</tt> in other case.
	 */
    public boolean isNonPresential() {
        return ((ActivityFlow)modelReq).isNonPresential();
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
		return ((ActivityFlow)modelReq).isInterruptible();
	}
	
	/**
	 * @return the cancellationList
	 */
	public long getResourceCancellation(ResourceType rt) {
		Long entry = cancellationList.get(rt); 
		if (entry == null)
			return 0;
		return entry;
	}
	
	/**
	 * Checks if the element is valid to perform this activity.
	 * An element is valid to perform an activity is it's not currently carrying
	 * out another activity or this activity is non presential.
	 * @param wThread Work thread requesting this activity
	 * @return True if the element is valid, false in other case.
	 */
	// TODO: Change by a Condition to make it more generic or implement a "hold"-kind flow
	@Override
	protected boolean validElement(WorkThread wThread) {
		return (wThread.getElement().getCurrent() == null || isNonPresential());
	}

    @Override
    protected ArrayDeque<Resource> isFeasible(WorkThread wt) {
    	final ArrayDeque<Resource> solution = super.isFeasible(wt);
    	if (solution != null) {
	        if (!isNonPresential())
	        	wt.getElement().setCurrent(wt);
    	}
        return solution;
    }

	/**
	 * Catches the resources required to carry out this activity. In case it used a time-driven workgroup,
	 * updates the element's timestamp, catch the corresponding resources and produces a 
	 * <code>FinishFlowEvent</code>. In case it used a flow-driven workgroup, catches the resources required 
	 * and launches the initial flow.
	 * @param wThread Work thread requesting this activity
	 */
	@Override
	protected void carryOut(WorkThread wThread, ArrayDeque<Resource> solution) {
		((ActivityFlow)modelReq).afterStart(wThread);
		long auxTs = wThread.acquireResources(solution, modelReq.getResourcesId());
		// Before this line, the code is common for time- and flow- driven WGs
		
		final Element elem = wThread.getElement();
		if (wThread.getExecutionWG().getDrivenBy() == DrivenBy.FLOW) {
			simul.getInfoHandler().notifyInfo(new ElementActionInfo(simul, wThread, wThread.getElement(), modelReq, wThread.getExecutionWG().getModelAWG(), ElementActionInfo.Type.STAACT, elem.getTs()));
			elem.debug("Starts\t" + this + "\t" + modelReq.getDescription());
			final InitializerFlow initialFlow = wThread.getExecutionWG().getInitialFlow();
			elem.addRequestEvent(initialFlow, wThread.getInstanceDescendantWorkThread(initialFlow));
		}
		else if (wThread.getExecutionWG().getDrivenBy() == DrivenBy.TIME) {
			// The first time the activity is carried out (useful only for interruptible activities)
			if (wThread.getTimeLeft() == -1) {
				wThread.setTimeLeft(wThread.getExecutionWG().getDurationSample(elem));
				simul.getInfoHandler().notifyInfo(new ElementActionInfo(this.simul, wThread, wThread.getElement(), modelReq, wThread.getExecutionWG().getModelAWG(), ElementActionInfo.Type.STAACT, elem.getTs()));
				elem.debug("Starts\t" + this + "\t" + modelReq.getDescription());			
			}
			else {
				simul.getInfoHandler().notifyInfo(new ElementActionInfo(this.simul, wThread, wThread.getElement(), modelReq, wThread.getExecutionWG().getModelAWG(), ElementActionInfo.Type.RESACT, elem.getTs()));
				elem.debug("Continues\t" + this + "\t" + modelReq.getDescription());						
			}
			long finishTs = elem.getTs() + wThread.getTimeLeft();
			// The required time for finishing the activity is reduced (useful only for interruptible activities)
			if (isInterruptible() && (finishTs - auxTs > 0.0))
				wThread.setTimeLeft(finishTs - auxTs);				
			else {
				auxTs = finishTs;
				wThread.setTimeLeft(0);
			}
			elem.addFinishEvent(auxTs, modelReq, wThread);
		}
		else {
			elem.error("Trying to carry out unexpected type of workgroup");
		}
	}

	/**
	 * Releases the resources required to carry out this activity.
	 * @param wThread Work thread which requested this activity
	 */
	public void finish(WorkThread wThread) {
		final Element elem = wThread.getElement();

		final Collection<Resource> caughtResources = wThread.releaseResources(modelReq.getResourcesId());
		if (caughtResources == null) {
			elem.error("Trying to release group of resources not already created. ID:" + id);
		}
        ArrayList<ActivityManager> amList = new ArrayList<ActivityManager>();
        // Generate unavailability periods.
        for (Resource res : caughtResources) {
        	final long cancellationDuration = getResourceCancellation(res.getCurrentResourceType());
        	if (cancellationDuration > 0) {
				long actualTs = elem.getTs();
				res.setNotCanceled(false);
				simul.getInfoHandler().notifyInfo(new ResourceInfo(simul, res.getModelRes(), res.getCurrentResourceType().getModelRT(), ResourceInfo.Type.CANCELON, actualTs));
				res.generateCancelPeriodOffEvent(actualTs, cancellationDuration);
			}
			elem.debug("Returned " + res);
        	// The resource is freed
        	if (res.releaseResource()) {
        		// The activity managers involved are included in the list
        		for (ActivityManager am : res.getCurrentManagers())
        			if (!amList.contains(am))
        				amList.add(am);
        	}
        }

		if (!isNonPresential())
			elem.setCurrent(null);

        // FIXME: Preparado para hacerlo aleatorio
//		final int[] order = RandomPermutation.nextPermutation(amList.size());
//		for (int ind : order) {
//			ActivityManager am = amList.get(ind);
//			am.availableResource();
//		}

		for (ActivityManager am : amList) {
			am.availableResource();
		}		

		if (wThread.getExecutionWG().getDrivenBy() == DrivenBy.FLOW) {
			simul.getInfoHandler().notifyInfo(new ElementActionInfo(simul, wThread, wThread.getElement(), modelReq, wThread.getExecutionWG().getModelAWG(), ElementActionInfo.Type.ENDACT, elem.getTs()));
			if (elem.isDebugEnabled())
				elem.debug("Finishes\t" + this + "\t" + modelReq.getDescription());
			modelReq.afterFinalize(wThread);
			next(wThread);
		}
		else if (wThread.getExecutionWG().getDrivenBy() == DrivenBy.TIME) {
			// FIXME: CUIDADO CON ESTO!!! Nunca debería ser menor
			if (wThread.getTimeLeft() <= 0.0) {
				simul.getInfoHandler().notifyInfo(new ElementActionInfo(this.simul, wThread, wThread.getElement(), modelReq, wThread.getExecutionWG().getModelAWG(), ElementActionInfo.Type.ENDACT, elem.getTs()));
				if (elem.isDebugEnabled())
					elem.debug("Finishes\t" + this + "\t" + modelReq.getDescription());
				// Checks if there are pending activities that haven't noticed the
				// element availability
				if (!isNonPresential())
					elem.addAvailableElementEvents();
				modelReq.afterFinalize(wThread);
				next(wThread);
			}
			// Added the condition(Lancaster compatibility), even when it should be unnecessary.
			else {
				simul.getInfoHandler().notifyInfo(new ElementActionInfo(this.simul, wThread, wThread.getElement(), modelReq, wThread.getExecutionWG().getModelAWG(), ElementActionInfo.Type.INTACT, elem.getTs()));
				if (elem.isDebugEnabled())
					elem.debug("Finishes part of \t" + this + "\t" + modelReq.getDescription() + "\t" + wThread.getTimeLeft());				
				// The element is introduced in the queue
				queueAdd(wThread); 
			}
		}
	}
}
