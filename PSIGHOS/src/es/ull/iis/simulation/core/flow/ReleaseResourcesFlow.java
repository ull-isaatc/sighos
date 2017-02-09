/**
 * 
 */
package es.ull.iis.simulation.core.flow;

import es.ull.iis.simulation.core.ResourceType;

/**
 * @author Iván Castilla
 *
 */
public interface ReleaseResourcesFlow<RT extends ResourceType> extends FinalizerFlow, SingleSuccessorFlow {
	
	/**
	 * Adds a new ResouceType to the cancellation list.
	 * @param rt Resource type
	 * @param duration Duration of the cancellation.
	 */
	public void addResourceCancellation(RT rt, long duration);

	/**
	 * @return the cancellationList
	 */
	public long getResourceCancellation(RT rt);
	
	/** 
	 * Elements of the cancellation list.
	 * @author ycallero
	 *
	 */
	public class CancelListEntry<RT extends ResourceType> {		
		public RT rt;
		public long dur;
		
		public CancelListEntry(RT rt, long dur) {
			this.rt = rt;
			this.dur = dur;
		}
	}
	

}
