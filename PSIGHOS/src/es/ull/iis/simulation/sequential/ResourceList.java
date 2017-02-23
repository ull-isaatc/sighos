package es.ull.iis.simulation.sequential;

import java.util.ArrayList;
import java.util.Collection;

import es.ull.iis.simulation.model.Resource;

/**
 * Handles the overlap of timetable entries for the same resource, i.e., a resource that has 
 * several timetable entries at the same time interval with the same resource type. This list 
 * counts how many times it occurs to avoid incorrect behaviors of the amount of available
 * resources.
 * @author Iván Castilla Rodríguez
 */
public class ResourceList extends es.ull.iis.simulation.model.ResourceList {
	/** List of resources */
	private final ArrayList<Resource> resources;
	/** A count of how many times each resource has been put as available */
	private final ArrayList<Integer> counter;
    
    /**
     * Creates a new resource list.
     */
    public ResourceList() {
    	resources = new ArrayList<Resource>();
    	counter = new ArrayList<Integer>();
    }

    /**
     * Adds a resource. If the resource isn't present in the list, it's included with a "1" count.
     * If the resource exists already, the count is increased.
     * @param res The resource added
     */
    public void add(Resource res) {
    	int pos = resources.indexOf(res);
    	if (pos == -1) {
    		resources.add(res);
    		counter.add(1);
    	}
    	else
    		counter.set(pos, counter.get(pos).intValue() + 1); 
    }
    
    /**
     * Removes a resource. The resource can have more than one appearance in the list. In 
     * this case, it's no t really removed.
     * @param res The resource removed.
     * @return True if the resource is completely removed from the list. False in other case.
     */
    public boolean remove(Resource res) {
    	int pos = resources.indexOf(res);
    	// FIXME Debería crearme un tipo personalizado de excepción
    	if (pos == -1)
    		throw new RuntimeException("Unexpected error: Resource not found in resource type");
    	if (counter.get(pos).intValue() > 1) {
    		counter.set(pos, new Integer(counter.get(pos).intValue() - 1));
    		return false;
    	}
		resources.remove(pos);
		counter.remove(pos);
    	return true;
    }
    
    /**
     * Returns the resource at the specified position 
     * @param index The position of the resource
     * @return The resource at the specified position.
     */
    public Resource get(int index) {
    	return resources.get(index);
    }
    
    /**
     * Returns the count at the specified position
     * @param index The position of the count.
     * @return the count at the specified position
     */
    public int getCounter(int index) {
    	return counter.get(index);
    }
    
    /**
     * Returns the number of resources in this list. 
     * @return The number of resources in this list.
     */
    public int size() {
    	return resources.size();
    }

    /**
     * Returns the list of available resources
     * @return the list of available resources
     */
    @Override
	public Collection<Resource> getResources() {
		return resources;
	}
}