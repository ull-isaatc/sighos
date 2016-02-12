/**
 * 
 */
package es.ull.iis.simulation.core;


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
	private final ResourceType role;
    
    /** Creates a new instance of TimeTableEntry
     * @param cycle Cycle that characterizes this entry
     * @param dur How long this resource plays this role every cycle
     * @param role Role that the resource plays during this cycle
     */
	public TimeTableEntry(SimulationCycle cycle, TimeStamp dur, ResourceType role) {
		this.cycle = cycle;
		this.duration = dur;
		this.role = role;
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
    public ResourceType getRole() {
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
