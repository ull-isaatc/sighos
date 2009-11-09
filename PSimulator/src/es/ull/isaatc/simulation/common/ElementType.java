/**
 * 
 */
package es.ull.isaatc.simulation.common;

import es.ull.isaatc.util.Prioritizable;

/**
 * @author Iván Castilla Rodríguez
 *
 */
public interface ElementType extends VariableStoreSimulationObject, Describable, Prioritizable {
	void addElementVar(String name, Object value);
}
