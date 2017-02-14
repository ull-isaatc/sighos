/**
 * 
 */
package es.ull.iis.simulation.model;

import es.ull.iis.simulation.model.flow.InitializerFlow;

/**
 * @author Iván Castilla
 *
 */
public interface Element extends EventSource {
	/**
	 * Returns the corresponding type of the element.
	 * @return the corresponding type of the element
	 */
	ElementType getType();
	/**
	 * Returns the associated {@link es.ull.iis.simulation.core.flow.Flow Flow}.
	 * @return the associated {@link es.ull.iis.simulation.core.flow.Flow Flow}
	 */
	InitializerFlow getFlow();

}
