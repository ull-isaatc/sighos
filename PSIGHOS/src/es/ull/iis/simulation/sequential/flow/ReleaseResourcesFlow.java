/**
 * 
 */
package es.ull.iis.simulation.sequential.flow;

import java.util.Collection;
import java.util.TreeMap;
import java.util.TreeSet;

import es.ull.iis.simulation.core.flow.FinalizerFlow;
import es.ull.iis.simulation.core.flow.Flow;
import es.ull.iis.simulation.info.ElementActionInfo;
import es.ull.iis.simulation.info.ResourceInfo;
import es.ull.iis.simulation.sequential.ActivityManager;
import es.ull.iis.simulation.sequential.Element;
import es.ull.iis.simulation.sequential.Resource;
import es.ull.iis.simulation.sequential.ResourceType;
import es.ull.iis.simulation.sequential.Simulation;
import es.ull.iis.simulation.sequential.WorkThread;

/**
 * @author Iván Castilla
 *
 */
public class ReleaseResourcesFlow extends SingleSuccessorFlow implements es.ull.iis.simulation.core.flow.ReleaseResourcesFlow<WorkThread, ResourceType>, FinalizerFlow<WorkThread> {
    /** A brief description of the activity */
    protected final String description;
    /** A unique identifier that sets which resources to release */
	protected final int resourcesId;
    /** Resources cancellation table */
    protected final TreeMap<ResourceType, CancelListEntry<ResourceType>> cancellationList;

	/**
	 * @param simul
	 * @param description
	 */
	public ReleaseResourcesFlow(Simulation simul, String description, int resourcesId) {
		super(simul);
        this.description = description;
		this.resourcesId = resourcesId;
		cancellationList = new TreeMap<ResourceType, CancelListEntry<ResourceType>>();
	}
	
	@Override
	public String getDescription() {
		return description;
	}

	@Override
	public void request(WorkThread wThread) {
		if (!wThread.wasVisited(this)) {
			if (wThread.isExecutable()) {
				final Element elem = wThread.getElement();
				if (beforeRequest(elem)) {
					final Collection<Resource> caughtResources = wThread.releaseResources(resourcesId);
					if (caughtResources == null) {
						elem.error("Trying to release group of resources not already created. ID:" + id);
					}
			        TreeSet<ActivityManager> amList = new TreeSet<ActivityManager>();
			        // Generate unavailability periods.
			        for (Resource res : caughtResources) {
			        	final long cancellationDuration = getResourceCancellation(res.getCurrentResourceType());
			        	if (cancellationDuration > 0) {
							long actualTs = elem.getTs();
							res.setNotCanceled(false);
							simul.getInfoHandler().notifyInfo(new ResourceInfo(simul, res, res.getCurrentResourceType(), ResourceInfo.Type.CANCELON, actualTs));
							res.generateCancelPeriodOffEvent(actualTs, cancellationDuration);
						}
						elem.debug("Returned " + res);
			        	// The resource is freed
			        	if (res.releaseResource()) {
			        		// The activity managers involved are included in the list
			        		for (ActivityManager am : res.getCurrentManagers()) {
			        			amList.add(am);
			        		}
			        	}
			        }

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
					simul.getInfoHandler().notifyInfo(new ElementActionInfo(simul, wThread, ElementActionInfo.Type.ENDACT, elem.getTs()));
					if (elem.isDebugEnabled())
						elem.debug("Finishes\t" + this + "\t" + description);
					afterFinalize(wThread);
					next(wThread);
				}
				else {
					wThread.setExecutable(false, this);
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

	/**
	 * Adds a new ResouceType to the cancellation list.
	 * @param rt Resource type
	 * @param duration Duration of the cancellation.
	 */
	@Override
	public void addResourceCancellation(ResourceType rt, long duration) {
		CancelListEntry<ResourceType> entry = new CancelListEntry<ResourceType>(rt, duration);
		cancellationList.put(rt, entry);
	}
	
	/**
	 * @return the cancellationList
	 */
	@Override
	public long getResourceCancellation(ResourceType rt) {
		CancelListEntry<ResourceType> entry = cancellationList.get(rt); 
		if (entry == null)
			return 0;
		return entry.dur;
	}
	
	@Override
	public String getObjectTypeIdentifier() {
		return "REL";
	}
	
	@Override
	public void addPredecessor(Flow<WorkThread> newFlow) {}

	@Override
	public void afterFinalize(WorkThread wt) {}
}
