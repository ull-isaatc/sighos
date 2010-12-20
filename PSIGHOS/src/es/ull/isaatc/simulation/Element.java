/**
 * 
 */
package es.ull.isaatc.simulation;

import es.ull.isaatc.simulation.flow.InitializerFlow;

/**
 * An entity capable of creating simulation events. Elements have a type and
 * an associated {@link es.ull.isaatc.simulation.flow.Flow Flow}.
 * @author Iván Castilla Rodríguez
 *
 */
public interface Element extends VariableStoreSimulationObject {
	/**
	 * Returns the corresponding type of the element.
	 * @return the corresponding type of the element
	 */
	ElementType getType();
	/**
	 * Returns the associated {@link es.ull.isaatc.simulation.flow.Flow Flow}.
	 * @return the associated {@link es.ull.isaatc.simulation.flow.Flow Flow}
	 */
	InitializerFlow getFlow();
}
