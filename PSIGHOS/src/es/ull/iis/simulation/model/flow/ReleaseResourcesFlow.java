/**
 * 
 */
package es.ull.iis.simulation.model.flow;

import java.util.ArrayDeque;
import java.util.TreeMap;

import es.ull.iis.simulation.condition.Condition;
import es.ull.iis.simulation.info.ElementActionInfo;
import es.ull.iis.simulation.model.ElementInstance;
import es.ull.iis.simulation.model.Resource;
import es.ull.iis.simulation.model.Simulation;
import es.ull.iis.simulation.model.WorkGroup;
import es.ull.iis.simulation.model.ResourceType;

/**
 * @author Iván Castilla
 *
 */
public class ReleaseResourcesFlow extends SingleSuccessorFlow implements ResourceHandlerFlow, FinalizerFlow {
    /** A brief description of the activity */
    protected final String description;
    /** A workgroup of resources to release */
    protected final WorkGroup wg;
    /** A unique identifier that sets which resources to release */
	protected final int resourcesId;
    /** Resources cancellation table */
    protected final TreeMap<ResourceType, Long> cancellationList;
    /** Conditions associated to resource cancellations */
    protected final TreeMap<ResourceType, Condition> cancellationConditionList;
	
	/**
	 * @param simul
	 * @param description
	 */
	public ReleaseResourcesFlow(Simulation model, String description) {
		this(model, description, 0, null);
	}
	
	/**
	 * @param simul
	 * @param description
	 */
	public ReleaseResourcesFlow(Simulation model, String description, WorkGroup wg) {
		this(model, description, 0, wg);
	}
	
	/**
	 * @param simul
	 * @param description
	 */
	public ReleaseResourcesFlow(Simulation model, String description, int resourcesId) {
		this(model, description, resourcesId, null);
	}
	
	/**
	 * @param simul
	 * @param description
	 */
	public ReleaseResourcesFlow(Simulation model, String description, int resourcesId, WorkGroup wg) {
		super(model);
        this.description = description;
		this.resourcesId = resourcesId;
		cancellationList = new TreeMap<ResourceType, Long>();
		cancellationConditionList = new TreeMap<ResourceType, Condition>();
		this.wg = wg;
	}
	
	/**
	 * @return the resourcesId
	 */
	public int getResourcesId() {
		return resourcesId;
	}

	@Override
	public String getDescription() {
		return description;
	}
    
	/**
	 * @return the wg
	 */
	public WorkGroup getWorkGroup() {
		return wg;
	}

	/**
	 * Adds a new ResouceType to the cancellation list.
	 * @param rt Resource type
	 * @param duration Duration of the cancellation.
	 */
	public void addResourceCancellation(ResourceType rt, long duration) {
		cancellationList.put(rt, duration);		
	}

	/**
	 * Adds a new ResouceType to the cancellation list.
	 * @param rt Resource type
	 * @param duration Duration of the cancellation.
	 * @param cond Condition that must be fulfilled to apply the cancellation 
	 */
	public void addResourceCancellation(ResourceType rt, long duration, Condition cond) {
		cancellationList.put(rt, duration);	
		cancellationConditionList.put(rt, cond);
	}

	/**
	 * Returns the duration of the cancellation of a resource with the specified
	 * resource type.
	 * @param rt Resource Type
	 * @return The duration of the cancellation
	 */
	public long getResourceCancellation(ResourceType rt, ElementInstance ei) {
		final Long duration = cancellationList.get(rt);
		if (duration != null) {
			final Condition cond = cancellationConditionList.get(rt);
			if (cond == null) {
				return duration;
			}
			else if (cond.check(ei)) {
				return duration;
			}
		}
		return 0;
	}
	
	@Override
	public String getObjectTypeIdentifier() {
		return "REL";
	}
	
	@Override
	public void addPredecessor(Flow newFlow) {}

	@Override
	public void afterFinalize(ElementInstance fe) {}

    /**
     * Releases the resources caught by this item to perform the activity.
     */
    public void releaseResources(ElementInstance ei) {
    	final ArrayDeque<Resource> resources = ei.releaseCaughtResources(wg);
		simul.notifyInfo(new ElementActionInfo(simul, ei, ei.getElement(), this, ei.getExecutionWG(), resources, ElementActionInfo.Type.REL, simul.getTs()));
		if (ei.getElement().isDebugEnabled())
			ei.getElement().debug("Finishes\t" + this + "\t" + getDescription());
		afterFinalize(ei);
    }

	/*
	 * (non-Javadoc)
	 * @see es.ull.iis.simulation.Flow#request(es.ull.iis.simulation.FlowExecutor)
	 */
	public void request(final ElementInstance wThread) {
		if (!wThread.wasVisited(this)) {
			if (wThread.isExecutable()) {
				if (beforeRequest(wThread)) {
					releaseResources(wThread);
					next(wThread);
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

}
