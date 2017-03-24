/**
 * 
 */
package es.ull.iis.simulation.model.flow;

import java.util.TreeMap;

import es.ull.iis.simulation.info.ElementActionInfo;
import es.ull.iis.simulation.model.ElementInstance;
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
	 * Returns the duration of the cancellation of a resource with the specified
	 * resource type.
	 * @param rt Resource Type
	 * @return The duration of the cancellation
	 */
	public long getResourceCancellation(ResourceType rt) {
		Long duration = cancellationList.get(rt); 
		if (duration == null)
			return 0;
		return duration;
	}
	
	/**
	 * @return the cancellationList
	 */
	public TreeMap<ResourceType, Long> getCancellationList() {
		return cancellationList;
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
     * @return A list of activity managers affected by the released resources
     */
    public boolean releaseResources(ElementInstance fe) {
        if (!fe.releaseCaughtResources(wg))
        	return false;
		simul.notifyInfo(new ElementActionInfo(simul, fe, fe.getElement(), this, fe.getExecutionWG(), ElementActionInfo.Type.REL, simul.getSimulationEngine().getTs()));
		if (fe.getElement().isDebugEnabled())
			fe.getElement().debug("Finishes\t" + this + "\t" + getDescription());
		afterFinalize(fe);
		return true;

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
