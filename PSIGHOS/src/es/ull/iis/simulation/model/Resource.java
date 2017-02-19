/**
 * 
 */
package es.ull.iis.simulation.model;

import java.util.ArrayList;
import java.util.Collection;

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
public class Resource extends ModelObject implements Describable {
    /** A brief description of the resource */
    protected final String description;
	/** Timetable which defines the availability estructure of the resource. Define RollOn and RollOff events. */
    protected final ArrayList<TimeTableEntry> timeTable = new ArrayList<TimeTableEntry>();
    /** Availability time table. Define CancelPeriodOn and CancelPeriodOff events */
    protected final ArrayList<TimeTableEntry> cancelPeriodTable = new ArrayList<TimeTableEntry>();

	/**
	 * 
	 */
	public Resource(Model model, String description) {
		super(model, model.getResourceList().size(), "RES");
		this.description = description;
		model.add(this);
	}

	@Override
	public String getDescription() {
		return description;
	}

	/**
	 * Returns a collection with the timetable defined for this resource.
	 * @return a collection with the timetable defined for this resource
	 */
	public Collection<TimeTableEntry> getTimeTableEntries() {
		return timeTable;
	}
	
	/**
	 * Returns a collection with the cancellation timetable defined for this resource.
	 * @return a collection with the cancellation timetable defined for this resource
	 */
	public Collection<TimeTableEntry> getCancellationPeriodEntries() {
		return cancelPeriodTable;
	}
	
	/**
	 * Adds a timetable entry for the whole duration of the simulation
	 * @param role The type of this resource during every activation 
	 */
	public void addTimeTableEntry(ResourceType role) {
		timeTable.add(new TimeTableEntry(role));
    }
    
	/**
	 * Adds a timetable entry for the whole duration of the simulation with overlapped resource types
	 * @param roleList The types of this resource during every activation 
	 */
	public void addTimeTableEntry(ArrayList<ResourceType> roleList) {
    	for (int i = 0; i < roleList.size(); i++)
            addTimeTableEntry(roleList.get(i));
    }
	
    /**
	 * Adds a timetable entry
	 * @param cycle Simulation cycle to define activation time
	 * @param dur How long the resource is active
	 * @param role The type of this resource during every activation 
	 */
	public void addTimeTableEntry(ModelCycle cycle, TimeStamp dur, ResourceType role) {
        timeTable.add(new TimeTableEntry(cycle, dur, role));
    }
    
	/**
	 * Adds a timetable entry with overlapped resource types
	 * @param cycle Simulation cycle to define activation time
	 * @param dur How long the resource is active
	 * @param roleList The types of this resource during every activation 
	 */
	public void addTimeTableEntry(ModelCycle cycle, TimeStamp dur, ArrayList<ResourceType> roleList) {
    	for (int i = 0; i < roleList.size(); i++)
            addTimeTableEntry(cycle, dur, roleList.get(i));
    }  
    
	/**
	 * Adds a timetable entry
	 * @param cycle Simulation cycle to define activation time
	 * @param dur How long the resource is active using the default model time unit
	 * @param role The type of this resource during every activation 
	 */
	public void addTimeTableEntry(ModelCycle cycle, long dur, ResourceType role) {
    	addTimeTableEntry(cycle, new TimeStamp(model.getUnit(), dur), role);
    }  
    
	/**
	 * Adds a timetable entry with overlapped resource types
	 * @param cycle Simulation cycle to define activation time
	 * @param dur How long the resource is active using the default model time unit
	 * @param roleList The types of this resource during every activation 
	 */
	public void addTimeTableEntry(ModelCycle cycle, long dur, ArrayList<ResourceType> roleList) {
    	addTimeTableEntry(cycle, new TimeStamp(model.getUnit(), dur), roleList);
    }  
    
    /**
     * Adds a new entry with a single role.
     * @param cycle Cycle that characterizes this entry
     * @param dur The long this resource plays this role every cycle
     * @param role Role that the resource plays during this cycle
     */
	public void addCancelTableEntry(ModelCycle cycle, TimeStamp dur, ResourceType role) {
        cancelPeriodTable.add(new TimeTableEntry(cycle, dur, role));
    }  

    /**
     * Adds a new entry with a several roles.
     * @param cycle Cycle that characterizes this entry
     * @param dur The long this resource plays this role every cycle
     * @param roleList Roles that the resource play during this cycle
     */
	public void addCancelTableEntry(ModelCycle cycle, TimeStamp dur, ArrayList<ResourceType> roleList) {
    	for (int i = 0; i < roleList.size(); i++)
            addCancelTableEntry(cycle, dur, roleList.get(i));
    }  
    
    /**
     * Adds a new entry with a single role.
     * @param cycle Cycle that characterizes this entry
     * @param dur The long this resource plays this role every cycle expressed in the 
     * default model time unit
     * @param role Role that the resource plays during this cycle
     */
	public void addCancelTableEntry(ModelCycle cycle, long dur, ResourceType role) {
    	addCancelTableEntry(cycle, new TimeStamp(model.getUnit(), dur), role);
    }  

    /**
     * Adds a new entry with a several roles.
     * @param cycle Cycle that characterizes this entry
     * @param dur The long this resource plays this role every cycle cycle expressed in the 
     * default model time unit
     * @param roleList Roles that the resource play during this cycle
     */
	public void addCancelTableEntry(ModelCycle cycle, long dur, ArrayList<ResourceType> roleList) {
    	addCancelTableEntry(cycle, new TimeStamp(model.getUnit(), dur), roleList);
    }      
}
