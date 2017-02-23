/**
 * 
 */
package es.ull.iis.simulation.core.flow;

import es.ull.iis.simulation.model.ResourceTypeEngine;

/**
 * @author Iván Castilla
 *
 */
public interface ReleaseResourcesFlow extends FinalizerFlow, SingleSuccessorFlow, ResourcesFlow {
	
	/**
	 * Adds a new ResouceType to the cancellation list.
	 * @param rt Resource type
	 * @param duration Duration of the cancellation.
	 */
	public void addResourceCancellation(ResourceTypeEngine rt, long duration);

	/**
	 * @return the cancellationList
	 */
	public long getResourceCancellation(ResourceTypeEngine rt);
	
	/** 
	 * Elements of the cancellation list.
	 * @author ycallero
	 *
	 */
	public class CancelListEntry {		
		public ResourceTypeEngine rt;
		public long dur;
		
		public CancelListEntry(ResourceTypeEngine rt, long dur) {
			this.rt = rt;
			this.dur = dur;
		}
	}
	

}
