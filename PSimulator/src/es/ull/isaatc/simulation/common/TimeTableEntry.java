/**
 * 
 */
package es.ull.isaatc.simulation.common;


/**
 * Represents the role that a resource plays at a specific time cycle.
 * @author Iván Castilla Rodríguez
 */
public class TimeTableEntry {
	/** Cycle that characterizes this entry */
	private final ModelCycle cycle;
    /** The long this resource plays this role every cycle */
	private final Time duration;
    /** Role that the resource plays during this cycle */
	private final ResourceType role;
    
    /** Creates a new instance of TimeTableEntry
     * @param cycle 
     * @param dur The long this resource plays this role every cycle
     * @param role Role that the resource plays during this cycle
     */
	public TimeTableEntry(ModelCycle cycle, Time dur, ResourceType role) {
		this.cycle = cycle;
		this.duration = dur;
		this.role = role;
	}
    
    /**
     * Getter for property duration.
     * @return Value of property duration.
     */
    public Time getDuration() {
        return duration;
    }

    /**
	 * @return the cycle
	 */
	public ModelCycle getCycle() {
		return cycle;
	}

	/**
     * Getter for property role.
     * @return Value of property role.
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
