/*
 * TimeTableEntry.java
 *
 * Created on 16 de noviembre de 2005, 12:26
 */

package es.ull.cyc.simulation;

import es.ull.cyc.util.CycleIterator;
import es.ull.cyc.util.Cycle;

/**
 * Represents the role that a resource plays at a specific time cycle. It starts 
 * and finishes the availability of a resource.
 * HISTORY:
 *  22/05/06 Changed from using a list of roles to a single role 
 * @author Iván Castilla Rodríguez
 */
class TimeTableEntry {
	/** Cycle that characterizes this entry */
	protected Cycle cycle;
    /** The long this resource plays this role every cycle */
	protected double duration;
    /** Role that the resource plays during this cycle */
    protected ResourceType role;
    
    /** Creates a new instance of TimeTableEntry
    * @param cycle 
    * @param dur The long this resource plays this role every cycle
    * @param role Role that the resource plays during this cycle
    */
	public TimeTableEntry(Cycle cycle, double dur, ResourceType role) {
		this.cycle = cycle;
		this.duration = dur;
		this.role = role;
	}
    
    /**
     * Getter for property duration.
     * @return Value of property duration.
     */
    public double getDuration() {
        return duration;
    }
    
    public CycleIterator getIterator(double startTs, double endTs) {
    	return new CycleIterator(cycle, startTs, endTs);
    }
    
    /**
     * Getter for property role.
     * @return Value of property role.
     */
    public ResourceType getRole() {
        return role;
    }
    
    
    /**
     * Representación en String de una entrada horaria 
     * @return Un string con la representación de la entrada horaria
     */
    public String toString() {
        StringBuffer str = new StringBuffer();
        str.append(" | " + cycle.getStartTs() + " | " + cycle.getPeriod() + " | " + duration
            + " | " + role.getDescription() + " | " + cycle.getIterations() + "\r\n");
        return str.toString();
    }
    
}
