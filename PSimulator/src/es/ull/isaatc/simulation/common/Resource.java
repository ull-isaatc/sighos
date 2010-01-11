/**
 * 
 */
package es.ull.isaatc.simulation.common;

import java.util.ArrayList;
import java.util.Collection;


/**
 * A simulation resource whose availability is controlled by means of timetable entries.
 * A timetable entry us a trio &lt{@link ResourceType}, {@link SimulationCycle}, long&gt which defines a 
 * resource type, an availability cycle, and the duration of each availability period. Timetable entries 
 * can be overlapped in time, thus allowing the resource for being potentially available for
 * different resource types simultaneously.
 * A resource finishes its execution when it has no longer valid timetable entries.
 * @author Iv�n Castilla Rodr�guez
 *
 */
public interface Resource extends VariableStoreSimulationObject, Describable {
	/**
	 * Returns a collection with the timetable defined for this resource.
	 * @return a collection with the timetable defined for this resource
	 */
	Collection<TimeTableEntry> getTimeTableEntries();
	
	/**
	 * Adds a timetable entry
	 * @param cycle Simulation cycle to define activation time
	 * @param dur How long the resource is active
	 * @param role The type of this resource during every activation 
	 */
    void addTimeTableEntry(SimulationCycle cycle, TimeStamp dur, ResourceType role);
    
	/**
	 * Adds a timetable entry with overlapped resource types
	 * @param cycle Simulation cycle to define activation time
	 * @param dur How long the resource is active
	 * @param roleList The types of this resource during every activation 
	 */
    void addTimeTableEntry(SimulationCycle cycle, TimeStamp dur, ArrayList<ResourceType> roleList);
    
	/**
	 * Adds a timetable entry
	 * @param cycle Simulation cycle to define activation time
	 * @param dur How long the resource is active using the simulation time unit
	 * @param role The type of this resource during every activation 
	 */
    void addTimeTableEntry(SimulationCycle cycle, long dur, ResourceType role);
    
	/**
	 * Adds a timetable entry with overlapped resource types
	 * @param cycle Simulation cycle to define activation time
	 * @param dur How long the resource is active using the simulation time unit
	 * @param roleList The types of this resource during every activation 
	 */
    void addTimeTableEntry(SimulationCycle cycle, long dur, ArrayList<ResourceType> roleList);
	
}
