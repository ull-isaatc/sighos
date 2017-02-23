/**
 * 
 */
package es.ull.iis.simulation.sequential;

import java.util.Map.Entry;
import java.util.TreeMap;

import es.ull.iis.simulation.model.flow.ReleaseResourcesFlow;

/**
 * @author Iván Castilla
 *
 */
public class ReleaseResources extends VariableStoreSimulationObject implements ResourceHandler {
    /** Resources cancellation table */
    protected final TreeMap<ResourceTypeEngine, Long> cancellationList = new TreeMap<ResourceTypeEngine, Long>();

    private final ReleaseResourcesFlow modelRel;
    
	/**
	 * @param simul
	 * @param description
	 */
	public ReleaseResources(SequentialSimulationEngine simul, ReleaseResourcesFlow modelRel) {
		super(simul.getNextActivityId(), simul, "REL");		
		this.modelRel = modelRel;
        final TreeMap<es.ull.iis.simulation.model.ResourceType, Long> originalList = modelRel.getCancellationList();
        for (Entry<es.ull.iis.simulation.model.ResourceType, Long> entry : originalList.entrySet()) {
        	cancellationList.put(simul.getResourceType(entry.getKey()), entry.getValue());
        }
        simul.add(this);
	}
	
	public ReleaseResourcesFlow getModelRelFlow() {
		return modelRel;
	}
	
	public String getDescription() {
		return modelRel.getDescription();
	}

	/**
	 * @return the cancellationList
	 */
	public long getResourceCancellation(ResourceTypeEngine rt) {
		Long duration = cancellationList.get(rt); 
		if (duration == null)
			return 0;
		return duration;
	}
	
}
