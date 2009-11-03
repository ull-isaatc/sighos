/**
 * 
 */
package es.ull.isaatc.simulation.common;

import java.util.Collection;

import es.ull.isaatc.simulation.Describable;

/**
 * @author Iv�n Castilla Rodr�guez
 *
 */
public interface Resource extends VariableStoreModelObject, Describable {
	Collection<TimeTableEntry> getTimeTableEntries();
}
