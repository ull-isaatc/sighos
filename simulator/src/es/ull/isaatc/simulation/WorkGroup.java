/*
 * WorkGroup.java
 *
 * Created on 17 de noviembre de 2005, 10:27
 */

package es.ull.isaatc.simulation;

import java.util.ArrayList;

import es.ull.isaatc.function.TimeFunction;
import es.ull.isaatc.util.*;

/**
 * A set of resources needed for carrying out an activity. A workgroup (WG) consists on a 
 * set of (resource type, #needed resources) pairs, the duration of the activity when using 
 * this workgroup, and the priority of the workgroup inside the activity.
 * @author Iván Castilla Rodríguez
 */
public class WorkGroup extends SimulationObject implements Prioritizable {
    /** Set of (resource type, #needed) pairs. */
    protected ArrayList<ResourceTypeTableEntry> resourceTypeTable;
    /** Activity this WG belongs to. */
    protected Activity act;
    /** Duration of the activity when using this WG */
    protected TimeFunction duration;
    /** Priority of the workgroup */
    protected int priority = 0;
    
    /**
     * Creates a new instance of WorkGroup
     * @param id Identifier of this workgroup.
     * @param act Activity this WG belongs to.
     * @param duracion Duration of the activity when using this WG.
     * @param priority Priority of the workgroup.
     */    
    protected WorkGroup(int id, Activity act, TimeFunction duration, int priority) {
        super(id, act.getSimul());
        this.act = act;
        this.resourceTypeTable = new ArrayList<ResourceTypeTableEntry>();
        this.duration = duration;
        this.priority = priority;
    }

    /**
     * Returns the activity this WG belongs to.
     * @return Activity this WG belongs to.
     */    
    protected Activity getActivity() {
        return act;
    }

    /**
     * Returns the duration of the activity when this workgroup is used. 
     * The value returned by the random number function could be negative. 
     * In this case, it returns 0.0.
     * @return The activity duration.
     */
    public double getDuration() {
        return duration.getPositiveValue(getTs());
    }
    
    /**
     * Getter for property priority.
     * @return Value of property priority.
     */
    public int getPriority() {
        return priority;
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

    /**
     * Checks if a valid solution can be reached from the current situation. This method 
     * is used to bound the search tree.
     * @param pos Initial position.
     * @param nec Resources needed.
     * @return True if there is a reachable solution. False in other case.
     */
    protected boolean hasSolution(int []pos, int []nec) {
        for (int i = pos[0]; i < resourceTypeTable.size(); i++) {
            ResourceTypeTableEntry actual = resourceTypeTable.get(i);
            int j = pos[1];
            Resource res;
            int disp = 0;            
            while (((res = actual.getResource(j)) != null) && (disp < nec[i])) {
                // FIXME Debería bastar con preguntar por el RT
                if ((res.getCurrentSF() == null) && (res.getCurrentResourceType() == null))
                    disp++;
                j++;
            }
            if (disp < nec[i])
                return false;
        }
        return true;
    }
    
    /**
     * Returns the position [ResourceType, Resource] of the next valid solution. The initial position
     * <code>pos</code> is supposed as correct.
     * @param pos Initial position [ResourceType, Resource].
     * @param nec Resource needed.
     * @return [ResourceType, Resource] where the next valid solution can be found.
     */
    private int []searchNext(int[] pos, int []nec) {
        int []aux = new int[2];
        aux[0] = pos[0];
        aux[1] = pos[1];
        // Searches a resource type that requires resources
        while (nec[aux[0]] == 0) {
            aux[0]++;
            // The second index is reset
            aux[1] = -1;
            // No more resources needed ==> SOLUTION
            if (aux[0] == resourceTypeTable.size()) {
                return aux;
            }
        }
        // Takes the first resource type
        ResourceType rt = resourceTypeTable.get(aux[0]).getResourceType();
        // Searches the NEXT available resource
        aux[1] = rt.getNextAvailableResource(aux[1] + 1);

        // This resource type don't have enough available resources
        if (aux[1] == -1)
            return null;
        return aux;
    }

    /**
     * Marks a resource as belonging to the solution
     * @param pos Position [ResourceType, Resource] of the resource
     */
    private void mark(int []pos) {
        Resource res = resourceTypeTable.get(pos[0]).getResource(pos[1]);
        res.setCurrentResourceType(resourceTypeTable.get(pos[0]).getResourceType());
    }
    
    /**
     * Removes the mark of a resource as belonging to the solution
     * @param pos Position [ResourceType, Resource] of the resource
     */
    private void unmark(int []pos) {
        Resource res = resourceTypeTable.get(pos[0]).getResource(pos[1]);
        res.setCurrentResourceType(null);
    }

    /**
     * Makes a depth first search looking for a solution.
     * @param pos Position to look for a solution [ResourceType, Resource] 
     * @param ned Resources needed
     * @return True if a valid solution exists. False in other case.
     */
    private boolean findSolution(int []pos, int []ned) {
        pos = searchNext(pos, ned);
        // No solution
        if (pos == null)
            return false;
        // No more elements needed => SOLUTION
        if (pos[0] == resourceTypeTable.size())
            return true;
        // This resource belongs to the solution...
        mark(pos);
        ned[pos[0]]--;
        // Bound
        if (hasSolution(pos, ned))
        // ... the search continues
            if (findSolution(pos, ned))
                return true;
        // There's no solution with this resource. Try without it
        unmark(pos);
        ned[pos[0]]++;
        // ... and the search continues
        return findSolution(pos, ned);        
    }
    
    /**
     * Distribute the resources when there is a conflict inside the activity.
     * @param sf Single flow trying to carry out the activity with this workgroup 
     * @return True if a valid solution exists. False in other case.
     */
    protected boolean distributeResources(SingleFlow sf) {
        int ned[] = new int[resourceTypeTable.size()];
        int []pos = {0, -1}; // "Start" position
        
        for (int i = 0; i < resourceTypeTable.size(); i++)
            ned[i] = resourceTypeTable.get(i).getNeeded();
        // B&B algorithm for finding a solution
        if (findSolution(pos, ned))
            return true;
        // If there is no solution, the "books" of this element are removed
        for (int i = 0; i < resourceTypeTable.size(); i++)
            resourceTypeTable.get(i).getResourceType().resetAvailable(sf);
        return false;
    }
    
    /**
     * Checks if there are enough resources to carry out an activity by using this workgroup.   
     * The "potential" available resources are booked by the element requesting the activity. 
     * If there are less available resources than needed resources for any resource type, the 
     * activity can not be carried out, and all the "books" are removed.
     * Possible conflicts between resources inside the activity are solved by invoking a
     * branch-and-bound resource distribution algorithm. 
     * @param sf Single flow trying to carry out the activity with this workgroup 
     * @return True if there are more "potential" available resources than needed resources for
     * thiw workgroup. False in other case.
     */
    protected boolean isFeasible(SingleFlow sf) {
    	boolean conflict = false;

    	sf.resetConflictZone();
        for (int i = 0; i < resourceTypeTable.size(); i++) {
            ResourceTypeTableEntry rttEntry = resourceTypeTable.get(i);       	
        	ResourceType rt = rttEntry.getResourceType();
        	int []avail = rt.getAvailable(sf);
        	// If there are less "potential" available resources than needed
            if (avail[0] + avail[1] < rttEntry.getNeeded()) {
            	// The element frees the previously booked resources 
                rt.resetAvailable(sf);
                i--;
                for (; i >= 0; i--)
                    resourceTypeTable.get(i).getResourceType().resetAvailable(sf);
                sf.removeFromConflictZone();
                return false;            	
            }
            // If the available resources WITH conflicts are needed
            else if (avail[0] < rttEntry.getNeeded())
                conflict = true;
        }
        // When this point is reached, that means that the activity is POTENTIALLY feasible
        sf.waitConflictSemaphore();
        // Now, this element has exclusive access to its resources. It's time to "recheck"
        // if the activity is feasible        
        if (conflict) { // The resource distribution algorithm is invoked
        	print(Output.MessageType.DEBUG, "Overlapped resources", "Overlapped resources with " + sf.getElement());
            if (!distributeResources(sf)) {
                sf.removeFromConflictZone();
            	sf.signalConflictSemaphore();
            	return false;
            }
        }
        else if (sf.getConflictZone().size() > 1) {
        	print(Output.MessageType.DEBUG, "Possible conflict", "Possible conflict. Recheck is needed " + sf.getElement());
            int ned[] = new int[resourceTypeTable.size()];
            int []pos = {0, 0}; // "Start" position
            for (int i = 0; i < resourceTypeTable.size(); i++)
                ned[i] = resourceTypeTable.get(i).getNeeded();
        	if (!hasSolution(pos, ned)) {
                sf.removeFromConflictZone();
            	sf.signalConflictSemaphore();
            	// The element frees the previously booked resources 
            	for (ResourceTypeTableEntry rttEntry : resourceTypeTable)
            		rttEntry.getResourceType().resetAvailable(sf);
        		return false;
        	}
        }
        return true;
    }

    /**
     * Catch the resources needed for each resource type to carry out an activity.
     * @param sf Single flow which requires the resources
     */
    protected void catchResources(SingleFlow sf) {
       for (ResourceTypeTableEntry rtte : resourceTypeTable)
           rtte.getResourceType().catchResources(rtte.getNeeded(), sf);
       // When this point is reached, that means that the resources have been completely taken
       sf.signalConflictSemaphore();
    }

    @Override
    public String toString() {
    	return new String("(" + act + ")" + super.toString());
    }

    @Override
	public String getObjectTypeIdentifier() {
		return "WGR";
	}

    @Override
	public double getTs() {
		return act.getTs();
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
