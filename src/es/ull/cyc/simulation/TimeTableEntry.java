/*
 * TimeTableEntry.java
 *
 * Created on 16 de noviembre de 2005, 12:26
 */

package es.ull.cyc.simulation;

import java.util.ArrayList;

import es.ull.cyc.util.CycleIterator;
import es.ull.cyc.util.Cycle;

/**
 * Represents the role that a resource plays at a specific time cycle. It starts 
 * and finishes the availability of a resource.
 * @author Iván Castilla Rodríguez
 */
class TimeTableEntry {
	/** Cycle that characterizes this entry */
	protected Cycle cycle;
    /** The long this resource plays this role every cycle */
	protected double duration;
    /** Roles that the resource plays during this cycle */
    protected ArrayList roleList;
    
    /** Creates a new instance of TimeTableEntry
    * @param cycle 
    * @param dur The long this resource plays this role every cycle
    * @param r Roles that the resource plays during this cycle
    */
	public TimeTableEntry(Cycle cycle, double dur, ArrayList r) {
		this.cycle = cycle;
		duration = dur;
		roleList = r;
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
     * Getter for property roleList.
     * @return Value of property roleList.
     */
    public java.util.ArrayList getRoleList() {
        return roleList;
    }
    
    
    /**
     * Representación en String de una entrada horaria 
     * @return Un string con la representación de la entrada horaria
     */
    public String toString() {
        StringBuffer str = new StringBuffer();
        for (int i = 0; i < roleList.size(); i++) {
            ResourceType rt = (ResourceType) roleList.get(i);
            str.append(" | " + cycle.getStartTs() + " | " + cycle.getPeriod() + " | " + duration
                + " | " + rt.getDescription() + " | " + cycle.getIterations() + "\r\n");
        }        
        return str.toString();
    }
    
}
