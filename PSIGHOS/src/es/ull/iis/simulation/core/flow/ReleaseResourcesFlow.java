/**
 * 
 */
package es.ull.iis.simulation.core.flow;

import es.ull.iis.simulation.core.ResourceType;

/**
 * @author Iv�n Castilla
 *
 */
public interface ReleaseResourcesFlow extends FinalizerFlow, SingleSuccessorFlow, ResourcesFlow {
	
	/**
	 * Adds a new ResouceType to the cancellation list.
	 * @param rt Resource type
	 * @param duration Duration of the cancellation.
	 */
	public void addResourceCancellation(ResourceType rt, long duration);

	/**
	 * @return the cancellationList
	 */
	public long getResourceCancellation(ResourceType rt);
	
	/** 
	 * Elements of the cancellation list.
	 * @author ycallero
	 *
	 */
	public class CancelListEntry {		
		public ResourceType rt;
		public long dur;
		
		public CancelListEntry(ResourceType rt, long dur) {
			this.rt = rt;
			this.dur = dur;
		}
	}
	

}
