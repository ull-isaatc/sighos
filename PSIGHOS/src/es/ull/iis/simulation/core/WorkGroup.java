/**
 * 
 */
package es.ull.iis.simulation.core;

import es.ull.iis.simulation.core.flow.ActivityFlow;

/**
 * A set of pairs &lt{@link ResourceType}, {@link Integer}&gt which defines how many resources 
 * from each type are required to do something (typically an {@link ActivityFlow}).
 * @author Iván Castilla Rodríguez
 */
public interface WorkGroup {
	/**
	 * Returns the {@link ResourceType}s of this {@link WorkGroup}.
	 * @return the {@link ResourceType}s of this {@link WorkGroup}
	 */
	ResourceType[] getResourceTypes();
	/**
	 * Returns the required amount of resources. 
	 * @return the required amount of resources
	 */
	int[] getNeeded();
}
