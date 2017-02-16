/**
 * 
 */
package es.ull.iis.simulation.sequential;

import java.util.Collection;
import java.util.TreeMap;

import es.ull.iis.simulation.info.ElementActionInfo;
import es.ull.iis.simulation.model.flow.ReleaseResourcesFlow;
import es.ull.iis.simulation.model.flow.ResourceHandlerFlow;

/**
 * @author Iván Castilla
 *
 */
public class ReleaseResources extends VariableStoreSimulationObject implements ReleaseResourceHandler {
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
	
	@Override
	public boolean releaseResources(WorkThread wThread) {
		final ReleaseResourcesFlow f =(ReleaseResourcesFlow) wThread.getCurrentFlow();
		final Element elem = wThread.getElement();
        Collection<ActivityManager> amList = wThread.releaseResources(f.getResourcesId(), cancellationList);

        // FIXME: Preparado para hacerlo aleatorio
//					final int[] order = RandomPermutation.nextPermutation(amList.size());
//					for (int ind : order) {
//						ActivityManager am = amList.get(ind);
//						// FIXME: Esto debería ser un evento por cada AM
//						am.availableResource();
//					}

		for (ActivityManager am : amList) {
			am.availableResource();
		}
		
		// TODO Change by more appropriate messages
		simul.getInfoHandler().notifyInfo(new ElementActionInfo(simul, wThread, wThread.getElement(), modelRel, wThread.getExecutionWG().getModelAWG(), ElementActionInfo.Type.ENDACT, elem.getTs()));
		if (elem.isDebugEnabled())
			elem.debug("Finishes\t" + this + "\t" + getDescription());
		f.afterFinalize(wThread);
		return true;
	}

}
