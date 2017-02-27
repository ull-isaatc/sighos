/**
 * 
 */
package es.ull.iis.simulation.core;

import es.ull.iis.simulation.core.flow.ActivityFlow;
import es.ull.iis.simulation.model.engine.ResourceTypeEngine;

/**
 * A set of pairs &lt{@link ResourceTypeEngine}, {@link Integer}&gt which defines how many resources 
 * from each type are required to do something (typically an {@link ActivityFlow}).
 * @author Iván Castilla Rodríguez
 */
public interface WorkGroup {
	/**
	 * Returns the {@link ResourceTypeEngine}s of this {@link WorkGroup}.
	 * @return the {@link ResourceTypeEngine}s of this {@link WorkGroup}
	 */
	ResourceTypeEngine[] getResourceTypes();
	/**
	 * Returns the required amount of resources. 
	 * @return the required amount of resources
	 */
	int[] getNeeded();
}
