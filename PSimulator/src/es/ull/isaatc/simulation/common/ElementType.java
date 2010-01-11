/**
 * 
 */
package es.ull.isaatc.simulation.common;

import es.ull.isaatc.util.Prioritizable;

/**
 * The type of an element. It serves for grouping and identification purposes. Elements
 * belonging to the same type have the same priority {@see Prioritizable}. Since individual 
 * elements are not visible when defining the model, element variables are also defined from the 
 * {@link ElementType}.
 * @author Iván Castilla Rodríguez
 *
 */
public interface ElementType extends VariableStoreSimulationObject, Describable, Prioritizable {
	/**
	 * Adds a variable which will be associated to every element of this type. Once instantiated,
	 * each element has its own variable.
	 * @param name Variable name
	 * @param value Initial value of the variable.
	 */
	void addElementVar(String name, Object value);
}
