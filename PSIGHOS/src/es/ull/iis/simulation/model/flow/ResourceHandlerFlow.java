/**
 * 
 */
package es.ull.iis.simulation.model.flow;

import es.ull.iis.simulation.model.Resource;

/**
 * A flow step that can interact with {@link Resource resources} 
 * @author Iván Castilla
 *
 */
public interface ResourceHandlerFlow extends ActionFlow {
	/**
	 * Returns a unique identifier for the set of resources that handles
	 * @return a unique identifier for the set of resources that handles
	 */
	int getResourcesId();
}
