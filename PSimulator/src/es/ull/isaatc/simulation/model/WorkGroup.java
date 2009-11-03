/*
 * WorkGroup.java
 *
 * Created on 17 de noviembre de 2005, 10:27
 */

package es.ull.isaatc.simulation.model;

import java.util.Iterator;
import java.util.TreeMap;

/**
 * A set of {resource type, #needed resources} pairs.
 * @author Iván Castilla Rodríguez
 */
public class WorkGroup implements es.ull.isaatc.simulation.common.WorkGroup {
    /** Set of (resource type, #needed) pairs. */
    protected final TreeMap<ResourceType, Integer> resourceTypeTable;
    
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
        this.resourceTypeTable = new TreeMap<ResourceType, Integer>();
        for (int i = 0; i < (rts.length < needs.length ? rts.length : needs.length); i++)
        	add(rts[i], needs[i]);
    }

	/**
     * Returns the amount of entries of the resource type table.
     * @return Amount of entries.
     */
    public int size() {
        return resourceTypeTable.size();
    }
    
    /**
	 * @return the resourceTypeTable
	 */
	public TreeMap<ResourceType, Integer> getResourceTypeTable() {
		return resourceTypeTable;
	}

	public Iterator<ResourceType> getResourceTypeIterator() {
    	return resourceTypeTable.keySet().iterator();
    }
    
    /**
     * Returns the resource type from the position ind of the table.
     * @param ind Index of the entry
     * @return The resource type from the position ind. null if it's a not valid 
     * index.
     */
    public ResourceType getFirstResourceType() {
        return resourceTypeTable.firstKey();
    }

    /**
     * Returns the needed amount of resources from the position ind of the table.
     * @param ind Index of the entry
     * @return The needed amount of resources from the position ind. -1 if it's 
     * a not valid index.
     */
    public int getNeeded(ResourceType rt) {
        return resourceTypeTable.get(rt);
    }

	/**
     * Adds a new entry.
     * If there is already an entry for this resource type, it's overwritten.
     * @param rt Resource Type
     * @param needed Needed units
     */
    public void add(ResourceType rt, int needed) {
    	resourceTypeTable.put(rt, needed);
    }

	@Override
	public int[] getNeeded() {
		int [] ned = new int[resourceTypeTable.size()];
		int i = 0;
		for (Integer n : resourceTypeTable.values())
			ned[i++] = n;
		return ned;
	}

	@Override
	public es.ull.isaatc.simulation.common.ResourceType[] getResourceTypes() {		
		return resourceTypeTable.keySet().toArray(new ResourceType[0]);
	}
}
