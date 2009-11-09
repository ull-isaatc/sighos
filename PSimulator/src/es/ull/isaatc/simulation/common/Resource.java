/**
 * 
 */
package es.ull.isaatc.simulation.common;

import java.util.ArrayList;
import java.util.Collection;


/**
 * @author Iván Castilla Rodríguez
 *
 */
public interface Resource extends VariableStoreSimulationObject, Describable {
	Collection<TimeTableEntry> getTimeTableEntries();
    void addTimeTableEntry(ModelCycle cycle, Time dur, ResourceType role);
    void addTimeTableEntry(ModelCycle cycle, Time dur, ArrayList<ResourceType> roleList);
    void addTimeTableEntry(ModelCycle cycle, double dur, ResourceType role);
    void addTimeTableEntry(ModelCycle cycle, double dur, ArrayList<ResourceType> roleList);
	
}
