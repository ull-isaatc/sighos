/**
 * 
 */
package es.ull.isaatc.simulation.common;

import java.util.Collection;

import es.ull.isaatc.simulation.Describable;

/**
 * @author Iván Castilla Rodríguez
 *
 */
public interface Resource extends SimulationObject, Describable {
	Collection<TimeTableEntry> getTimeTableEntries();
}
