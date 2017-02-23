/*
 * WorkGroup.java
 *
 * Created on 17 de noviembre de 2005, 10:27
 */

package es.ull.iis.simulation.parallel;

/**
 * A set of pairs &lt{@link ResourceType}, {@link Integer}&gt which defines how many resources 
 * from each type are required to do something (typically an {@link Activity}).
 * @author Iván Castilla Rodríguez
 */
public class WorkGroup implements es.ull.iis.simulation.core.WorkGroup {
    /** Set of resource types required */
	protected final ResourceType[] resourceTypes;
	/** Set of amounts of resources required */
	protected final int[] needed;
    
    /**
     * Creates a new WG which doesn't require resources. 
     */    
    public WorkGroup() {
        this(new ResourceType[0], new int[0]);
    }

    /**
     * Creates a new WG which requires <code>needed</code> resources of type
     * <code>rt</code>.
     * @param rt Resource Type
     * @param needed Resources needed
     */    
    public WorkGroup(ResourceType rt, int needed) {
        this(new ResourceType[] {rt}, new int[] {needed});
    }

    /**
     * Creates a new WG which requires <code>needs[i]</code> resources of type
     * <code>rts[i]</code>.
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
	public es.ull.iis.simulation.model.ResourceTypeEngine[] getResourceTypes() {
		return resourceTypes;
	}
}
