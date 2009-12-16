/*
 * WorkGroup.java
 *
 * Created on 17 de noviembre de 2005, 10:27
 */

package es.ull.isaatc.simulation.groupedThreaded;

import es.ull.isaatc.simulation.groupedThreaded.ResourceType;

/**
 * A set of {resource type, #needed resources} pairs.
 * @author Iván Castilla Rodríguez
 */
public class WorkGroup implements es.ull.isaatc.simulation.common.WorkGroup {
    /** Set of (resource type, #needed) pairs. */
	protected final ResourceType[] resourceTypes;
	protected final int[] needed;
    
    /**
     * Creates a new instance of WorkGroup with an empty list of pairs
     * {resource type, #needed resources}. 
     */    
    public WorkGroup() {
        this(new ResourceType[0], new int[0]);
    }

    /**
     * Creates a new instance of WorkGroup initializing the list of pairs
     * {resource type, #needed resources} with one pair. 
     * @param rt Resource Type
     * @param needed Resources needed
     */    
    public WorkGroup(ResourceType rt, int needed) {
        this(new ResourceType[] {rt}, new int[] {needed});
    }

    /**
     * Creates a new instance of WorkGroup, initializing the list of pairs
     * {resource type, #needed resources}.
     * @param rts The resource types which compounds this WG.
     * @param needs The amounts of resource types required by this WG.
     */    
    public WorkGroup(ResourceType[] rts, int []needs) {
    	this.resourceTypes = rts;
    	this.needed = needs;
    }

	/**
     * Returns the amount of entries of the resource type table.
     * @return Amount of entries.
     */
    public int size() {
        return resourceTypes.length;
    }
    
    /**
     * Returns the resource type from the position ind of the table.
     * @param ind Index of the entry
     * @return The resource type from the position ind. 
     */
    public ResourceType getResourceType(int ind) {
        return resourceTypes[ind];
    }

    /**
     * Returns the needed amount of resources from the position ind of the table.
     * @param ind Index of the entry
     * @return The needed amount of resources from the position ind. 
     */
    public int getNeeded(int ind) {
        return needed[ind];
    }

	@Override
	public int[] getNeeded() {
		return needed;
	}

	@Override
	public es.ull.isaatc.simulation.common.ResourceType[] getResourceTypes() {
		return resourceTypes;
	}
}
