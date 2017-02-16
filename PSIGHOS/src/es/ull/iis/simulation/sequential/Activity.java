/**
 * 
 */
package es.ull.iis.simulation.sequential;

import java.util.ArrayDeque;
import java.util.Collection;
import java.util.Map.Entry;
import java.util.Set;
import java.util.TreeMap;

import es.ull.iis.simulation.info.ElementActionInfo;
import es.ull.iis.simulation.model.ActivityWorkGroup.DrivenBy;
import es.ull.iis.simulation.model.flow.ActivityFlow;
import es.ull.iis.simulation.model.flow.BasicFlow;
import es.ull.iis.simulation.model.flow.Flow;
import es.ull.iis.simulation.model.flow.InitializerFlow;
import es.ull.iis.simulation.model.flow.StructuredFlow;

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
public class Activity extends RequestResources implements ReleaseResourceHandler {
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
        return !((ActivityFlow)modelReq).isExclusive();
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
		Long duration = cancellationList.get(rt); 
		if (duration == null)
			return 0;
		return duration;
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
}
