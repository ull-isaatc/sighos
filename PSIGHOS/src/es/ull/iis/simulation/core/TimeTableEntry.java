/**
 * 
 */
package es.ull.iis.simulation.core;

import es.ull.iis.simulation.model.SimulationCycle;
import es.ull.iis.simulation.model.TimeStamp;
import es.ull.iis.simulation.model.engine.ResourceTypeEngine;

/**
 * Represents the role that a resource plays at a specific time cycle.
 * @author Iván Castilla Rodríguez
 */
public class TimeTableEntry {
	/** Cycle that characterizes this entry */
	private final SimulationCycle cycle;
    /** The long this resource plays this role every cycle */
	private final TimeStamp duration;
    /** Role that the resource plays during this cycle */
	private final ResourceTypeEngine role;
    
    /** Creates a new instance of TimeTableEntry
     * @param cycle Cycle that characterizes this entry
     * @param dur How long this resource plays this role every cycle
     * @param role Role that the resource plays during this cycle
     */
	public TimeTableEntry(SimulationCycle cycle, TimeStamp dur, ResourceTypeEngine role) {
		this.cycle = cycle;
		this.duration = dur;
		this.role = role;
	}
    
    /** Creates a new instance of TimeTableEntry that represents a permanent resource
     * @param role Role that the resource plays during this cycle
     */
	public TimeTableEntry(ResourceTypeEngine role) {
		this.cycle = null;
		this.duration = null;
		this.role = role;
	}
    
	public boolean isPermanent() {
		return (cycle == null);
	}
	
    /**
     * Returns how long this resource plays this role every cycle.
     * @return how long this resource plays this role every cycle
     */
    public TimeStamp getDuration() {
        return duration;
    }

    /**
     * Returns the cycle that characterizes this entry.
	 * @return the cycle that characterizes this entry
	 */
	public SimulationCycle getCycle() {
		return cycle;
	}

	/**
     * Returns the role that the resource plays during this cycle.
     * @return the role that the resource plays during this cycle
     */
    public ResourceTypeEngine getRole() {
        return role;
    }
    
    @Override
    public String toString() {
        StringBuffer str = new StringBuffer();
        str.append(" | " + role.getDescription() + " | " + duration
            + " | " + cycle + "\r\n");
        return str.toString();
    }
    

}
