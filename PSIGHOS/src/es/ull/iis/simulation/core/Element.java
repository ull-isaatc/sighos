/**
 * 
 */
package es.ull.iis.simulation.core;

import es.ull.iis.simulation.core.flow.Flow;
import es.ull.iis.simulation.core.flow.InitializerFlow;

/**
 * An entity capable of creating simulation events. Elements have a type and
 * an associated {@link es.ull.iis.simulation.core.flow.Flow Flow}.
 * @author Iv�n Castilla Rodr�guez
 *
 */
public interface Element extends BasicElement, VariableStoreSimulationObject {
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
	
	/**
	 * Adds a new request event.
	 * @param f The flow to be requested
	 * @param wThread The work thread used to request the flow
	 */
	void addRequestEvent(Flow f, WorkThread wThread);
	
}
