/**
 * 
 */
package es.ull.iis.simulation.model.flow;

import java.util.TreeMap;

import es.ull.iis.simulation.model.ResourceType;

/**
 * @author Iván Castilla
 *
 */
public class ReleaseResourcesFlow extends SingleSuccessorFlow implements ResourcesFlow, FinalizerFlow {
    /** A brief description of the activity */
    protected final String description;
    /** A unique identifier that sets which resources to release */
	protected final int resourcesId;
    /** Resources cancellation table */
    protected final TreeMap<ResourceType, Long> cancellationList;
	
	/**
	 * @param simul
	 * @param description
	 */
	public ReleaseResourcesFlow(String description, int resourcesId) {
		super();
        this.description = description;
		this.resourcesId = resourcesId;
		cancellationList = new TreeMap<ResourceType, Long>();
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
	 * Adds a new ResouceType to the cancellation list.
	 * @param rt Resource type
	 * @param duration Duration of the cancellation.
	 */
	public void addResourceCancellation(ResourceType rt, long duration) {
		cancellationList.put(rt, duration);		
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
	public void afterFinalize(FlowExecutor fe) {}

}
