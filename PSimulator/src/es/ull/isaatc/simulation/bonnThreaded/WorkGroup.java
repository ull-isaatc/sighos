/*
 * WorkGroup.java
 *
 * Created on 17 de noviembre de 2005, 10:27
 */

package es.ull.isaatc.simulation.bonnThreaded;

import es.ull.isaatc.simulation.bonnThreaded.ResourceType;

/**
 * A basic implementation of {@link es.ull.isaatc.simulation.common.WorkGroup WorkGroup}
 * which contains a set of {resource type, #needed resources} pairs.
 * @author Iván Castilla Rodríguez
 */
public class WorkGroup implements es.ull.isaatc.simulation.common.WorkGroup {
    /** Collection of {@link ResourceType}s required by this workgroup */
	protected final ResourceType[] resourceTypes;
	/** Corresponding amount of {@link Resource}s required from each {@link ResourceType} */ 
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
     * {resource type, #needed resources}.<p>
     * The length of both arrays must be equal or an {@link IllegalArgumentException} will
     * be thrown.
     * @param rts Collection of {@link ResourceType}s required by this workgroup
     * @param needs Corresponding amount of {@link Resource}s required from each {@link ResourceType}
     */    
    public WorkGroup(ResourceType[] rts, int []needs) {
    	if (rts.length != needs.length)
    		throw new IllegalArgumentException("rts and needs dimensions must be equal." +
    				" rts has " + rts.length + " items and needs has " + needs.length);
    	this.resourceTypes = rts;
    	this.needed = needs;
    }

	/**
     * Returns how many different {@link ResourceType}s require this workgroup.
     * @return How many different {@link ResourceType}s require this workgroup
     */
    public int size() {
        return resourceTypes.length;
    }
    
    /**
     * Returns the ind-th {@link ResourceType}.
     * @param ind Index of the {@link ResourceType}
     * @return The ind-th {@link ResourceType} 
     */
    public ResourceType getResourceType(int ind) {
        return resourceTypes[ind];
    }

    /**
     * Returns the needed amount of resources for the ind-th {@link ResourceType}.
     * @param ind Index of the entry
     * @return The needed amount of resources from the ind-th {@link ResourceType}
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
