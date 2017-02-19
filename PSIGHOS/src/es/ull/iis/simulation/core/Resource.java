/**
 * 
 */
package es.ull.iis.simulation.core;

import java.util.ArrayList;
import java.util.Collection;

import es.ull.iis.simulation.model.Describable;
import es.ull.iis.simulation.model.ModelCycle;
import es.ull.iis.simulation.model.TimeStamp;


/**
 * A simulation resource whose availability is controlled by means of timetable entries.
 * A timetable entry us a trio &lt{@link ResourceType}, {@link ModelCycle}, long&gt which defines a 
 * resource type, an availability cycle, and the duration of each availability period. Timetable entries 
 * can be overlapped in time, thus allowing the resource for being potentially available for
 * different resource types simultaneously.
 * A resource finishes its execution when it has no longer valid timetable entries.
 * @author Iván Castilla Rodríguez
 *
 */
public interface Resource extends BasicElement, VariableStoreSimulationObject, Describable {
	/**
	 * Returns a collection with the timetable defined for this resource.
	 * @return a collection with the timetable defined for this resource
	 */
	Collection<TimeTableEntry> getTimeTableEntries();
	
	/**
	 * Adds a timetable entry for the whole duration of the simulation
	 * @param role The type of this resource during every activation 
	 */
    void addTimeTableEntry(ResourceType role);
    
	/**
	 * Adds a timetable entry for the whole duration of the simulation with overlapped resource types
	 * @param roleList The types of this resource during every activation 
	 */
    void addTimeTableEntry(ArrayList<ResourceType> roleList);
	
    /**
	 * Adds a timetable entry
	 * @param cycle Simulation cycle to define activation time
	 * @param dur How long the resource is active
	 * @param role The type of this resource during every activation 
	 */
    void addTimeTableEntry(ModelCycle cycle, TimeStamp dur, ResourceType role);
    
	/**
	 * Adds a timetable entry with overlapped resource types
	 * @param cycle Simulation cycle to define activation time
	 * @param dur How long the resource is active
	 * @param roleList The types of this resource during every activation 
	 */
    void addTimeTableEntry(ModelCycle cycle, TimeStamp dur, ArrayList<ResourceType> roleList);
    
	/**
	 * Adds a timetable entry
	 * @param cycle Simulation cycle to define activation time
	 * @param dur How long the resource is active using the simulation time unit
	 * @param role The type of this resource during every activation 
	 */
    void addTimeTableEntry(ModelCycle cycle, long dur, ResourceType role);
    
	/**
	 * Adds a timetable entry with overlapped resource types
	 * @param cycle Simulation cycle to define activation time
	 * @param dur How long the resource is active using the simulation time unit
	 * @param roleList The types of this resource during every activation 
	 */
    void addTimeTableEntry(ModelCycle cycle, long dur, ArrayList<ResourceType> roleList);

    
    /**
     * Adds a new entry with a single role.
     * @param cycle Cycle that characterizes this entry
     * @param dur The long this resource plays this role every cycle
     * @param role Role that the resource plays during this cycle
     */
    void addCancelTableEntry(ModelCycle cycle, TimeStamp dur, ResourceType role);

    /**
     * Adds a new entry with a several roles.
     * @param cycle Cycle that characterizes this entry
     * @param dur The long this resource plays this role every cycle
     * @param roleList Roles that the resource play during this cycle
     */
    void addCancelTableEntry(ModelCycle cycle, TimeStamp dur, ArrayList<ResourceType> roleList);
    
    /**
     * Adds a new entry with a single role.
     * @param cycle Cycle that characterizes this entry
     * @param dur The long this resource plays this role every cycle expressed in the 
     * simulation time unit
     * @param role Role that the resource plays during this cycle
     */
    void addCancelTableEntry(ModelCycle cycle, long dur, ResourceType role);

    /**
     * Adds a new entry with a several roles.
     * @param cycle Cycle that characterizes this entry
     * @param dur The long this resource plays this role every cycle cycle expressed in the 
     * simulation time unit
     * @param roleList Roles that the resource play during this cycle
     */
    void addCancelTableEntry(ModelCycle cycle, long dur, ArrayList<ResourceType> roleList);    
}
