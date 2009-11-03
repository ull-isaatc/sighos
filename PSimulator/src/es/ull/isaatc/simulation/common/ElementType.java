/**
 * 
 */
package es.ull.isaatc.simulation.common;

import es.ull.isaatc.simulation.Describable;
import es.ull.isaatc.util.Prioritizable;

/**
 * @author Iv�n Castilla Rodr�guez
 *
 */
public interface ElementType extends VariableStoreModelObject, Describable, Prioritizable {
	void addElementVar(String name, Object value);
}
