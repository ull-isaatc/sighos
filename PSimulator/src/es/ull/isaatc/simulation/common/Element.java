/**
 * 
 */
package es.ull.isaatc.simulation.common;

import es.ull.isaatc.simulation.common.flow.InitializerFlow;

/**
 * @author Iv�n Castilla Rodr�guez
 *
 */
public interface Element extends SimulationObject {
	ElementType getType();
	InitializerFlow getFlow();
}
