/**
 * 
 */
package es.ull.iis.simulation.condition;

import es.ull.iis.simulation.model.ElementInstance;
import es.ull.iis.simulation.model.ResourceType;

/**
 * @author Iván Castilla
 *
 */
public class ResourceTypeAcquiredCondition extends Condition {
	final private ResourceType rt;
	
	/**
	 * 
	 */
	public ResourceTypeAcquiredCondition(ResourceType rt) {
		this.rt = rt;
	}

	@Override
	public boolean check(ElementInstance fe) {
		return (fe.getElement().isAcquiredResourceType(rt));
	}
	
	public ResourceType getResourceType() {
		return rt;
	}
}
