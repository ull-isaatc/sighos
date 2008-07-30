/*
 * WorkGroup.java
 *
 * Created on 17 de noviembre de 2005, 10:27
 */

package es.ull.isaatc.simulation;

import java.util.ArrayList;

/**
 * A set of {resource type, #needed resources} pairs.
 * @author Iván Castilla Rodríguez
 */
public class WorkGroup extends SimulationObject implements Describable {
    /** Set of (resource type, #needed) pairs. */
    protected ArrayList<ResourceTypeTableEntry> resourceTypeTable;
    /** A brief description of the workgroup */
    protected String description;
    
    /**
     * Creates a new instance of WorkGroup with an empty list of pairs
     * {resource type, #needed resources}. 
     * @param id Identifier of this workgroup.
     * @param simul Simulation which this workgroup is attached to.
     * @param description A brief description of the workgroup
     */    
    public WorkGroup(int id, Simulation simul, String description) {
        this(id, simul, description, new ResourceType[0], new int[0]);
    }

    /**
     * Creates a new instance of WorkGroup, initializing the list of pairs
     * {resource type, #needed resources}.
     * @param id Identifier of this workgroup.
     * @param simul Simulation which this workgroup is attached to.
     * @param description A brief description of the workgroup
     * @param rts The resource types which compounds this WG.
     * @param needs The amounts of resource types required by this WG.
     */    
    public WorkGroup(int id, Simulation simul, String description, ResourceType[] rts, int []needs) {
        super(id, simul);
        this.description = description;
        this.resourceTypeTable = new ArrayList<ResourceTypeTableEntry>();
        for (int i = 0; i < (rts.length < needs.length ? rts.length : needs.length); i++)
        	add(rts[i], needs[i]);
    }

    /**
	 * @return the description
	 */
	public String getDescription() {
		return description;
	}

	/**
     * Returns the amount of entries of the resource type table.
     * @return Amount of entries.
     */
    public int size() {
        return resourceTypeTable.size();
    }
    
    /**
     * Returns the resource type from the position ind of the table.
     * @param ind Index of the entry
     * @return The resource type from the position ind. null if it's a not valid 
     * index.
     */
    public ResourceType getResourceType(int ind) {
        if (ind < 0 || ind >= resourceTypeTable.size())
            return null;
        return resourceTypeTable.get(ind).getResourceType();
    }

    /**
     * Returns the needed amount of resources from the position ind of the table.
     * @param ind Index of the entry
     * @return The needed amount of resources from the position ind. -1 if it's 
     * a not valid index.
     */
    public int getNeeded(int ind) {
        if (ind < 0 || ind >= resourceTypeTable.size())
            return -1;
        return resourceTypeTable.get(ind).getNeeded();
    }

	/**
     * Looks for an entry of the table that corresponds with a resource type.
     * @param rt Resource Type searched
     * @return The index of the entry. -1 if it's not found.
     */
	public int find(ResourceType rt) {
       int size = resourceTypeTable.size();
       int index = 0;
       boolean found = false;

       while ( (index < size) && ! found ) {
           if ( resourceTypeTable.get(index).getResourceType() == rt )
                return(index);
           else
                index++;
        }
        return(-1); 
	} 

	/**
     * Adds a new entry.
     * If there is already an entry for this resource type, it's overwritten.
     * @param rt Resource Type
     * @param needed Needed units
     */
    public void add(ResourceType rt, int needed) {
       ResourceTypeTableEntry newEntry = new ResourceTypeTableEntry(rt, needed);
       int index = find(rt);

       if (index == -1 ) // is a new entry
            resourceTypeTable.add(newEntry);
       else
            resourceTypeTable.set(index,newEntry); // overwrite the old entry
    }  

	@Override
	public String getObjectTypeIdentifier() {
		return "WGR";
	}

	/**
	 * This class represents the t-uplas (resource type, #needed) of a resource type table.
	 * @author Carlos Martín Galán
	 */
	class ResourceTypeTableEntry {
		/** Resource type */
		protected ResourceType rType;
		/** Needed units */
		protected int needed;

	    /**
	     * Creates a new entry in a resource type table
	     * @param rt Resource type
	     * @param uN Needed units
	     */
		ResourceTypeTableEntry(es.ull.isaatc.simulation.ResourceType rt, int uN) {
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
	    public es.ull.isaatc.simulation.ResourceType getResourceType() {
	        return rType;
	    }
	    
	    /**
	     * Returns the resource in the specified position of the available resource list of
	     * the resource type. 
	     * @param index Position of the resource.
	     * @return The resource in the specified position.
	     */
	    public Resource getResource(int index) {
	    	return rType.getResource(index);
	    }
	}
}
