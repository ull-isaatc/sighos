/**
 * 
 */
package es.ull.isaatc.simulation.common;

import es.ull.isaatc.simulation.common.flow.InitializerFlow;

/**
 * @author Iv�n Castilla Rodr�guez
 *
 */
public interface Element extends VariableStoreModelObject {
	ElementType getType();
	InitializerFlow getFlow();
}
