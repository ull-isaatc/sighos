/*
 * ResourceTypeTableEntry.java
 *
 * Created on 14 de noviembre de 2005, 12:24
 */

package es.ull.cyc.simulation;

/**
 * This class represents the t-uplas of a table of resource types.
 * @author Carlos Martín Galán
 */
class ResourceTypeTableEntry {
	/** Needed units */
	protected int needed;
	/** Resource type */
	protected ResourceType rType;

    /**
     * Creates a new entry in a resource type table
     * @param rt Resource type
     * @param uN Needed units
     */
	ResourceTypeTableEntry(es.ull.cyc.simulation.ResourceType rt, int uN) {
		rType = rt;
		needed = uN;
	}
    
    /**
     * Getter for property needed.
     * @return Value of property needed.
     */
    public int getNeeded() {
        return needed;
    }
    
    /**
     * Getter for property rType.
     * @return Value of property rType.
     */
    public es.ull.cyc.simulation.ResourceType getResourceType() {
        return rType;
    }   
}
