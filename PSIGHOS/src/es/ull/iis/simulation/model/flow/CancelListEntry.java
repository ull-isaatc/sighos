package es.ull.iis.simulation.model.flow;

import es.ull.iis.simulation.model.ResourceType;

/** 
 * Elements of the cancellation list.
 * @author ycallero
 *
 */
public class CancelListEntry {		
	private final ResourceType rt;
	private final long dur;
	
	public CancelListEntry(ResourceType rt, long dur) {
		this.rt = rt;
		this.dur = dur;
	}

	/**
	 * @return the dur
	 */
	public long getDuration() {
		return dur;
	}

	/**
	 * @return the rt
	 */
	public ResourceType getResourceType() {
		return rt;
	}
}