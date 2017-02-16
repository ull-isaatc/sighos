/**
 * 
 */
package es.ull.iis.simulation.sequential;

import java.util.TreeMap;

import es.ull.iis.simulation.model.flow.ReleaseResourcesFlow;
import es.ull.iis.simulation.model.flow.ResourceHandlerFlow;

/**
 * @author Iván Castilla
 *
 */
public class ReleaseResources extends VariableStoreSimulationObject implements ResourceHandler {
    /** Resources cancellation table */
    protected final TreeMap<ResourceType, Long> cancellationList;

    private final ReleaseResourcesFlow modelRel;
    
	/**
	 * @param simul
	 * @param description
	 */
	public ReleaseResources(Simulation simul, ReleaseResourcesFlow modelRel) {
		super(simul.getNextActivityId(), simul);
		
		this.modelRel = modelRel;
		cancellationList = new TreeMap<ResourceType, Long>();
	}
	
	@Override
	public ResourceHandlerFlow getModelResHandler() {
		return modelRel;
	}
	
	public String getDescription() {
		return modelRel.getDescription();
	}

	@Override
	public String getObjectTypeIdentifier() {
		return modelRel.getObjectTypeIdentifier();
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
	
}
