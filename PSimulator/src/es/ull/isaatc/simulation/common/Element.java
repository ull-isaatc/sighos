/**
 * 
 */
package es.ull.isaatc.simulation.common;

import es.ull.isaatc.simulation.common.flow.InitializerFlow;

/**
 * @author Iván Castilla Rodríguez
 *
 */
public interface Element extends VariableStoreSimulationObject {
	ElementType getType();
	InitializerFlow getFlow();
}
