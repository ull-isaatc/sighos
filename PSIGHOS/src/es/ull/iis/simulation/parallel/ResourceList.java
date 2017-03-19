package es.ull.iis.simulation.parallel;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Map;
import java.util.TreeMap;
import es.ull.iis.simulation.model.Resource;

/**
 * Handles the overlap of timetable entries for the same resource, i.e., a resource that has 
 * several timetable entries at the same time interval with the same resource type. This resources 
 * counts how many times it occurs to avoid incorrect behaviors of the amount of available
 * resources.
 * @author Iván Castilla Rodríguez
 */
public class ResourceList extends es.ull.iis.simulation.model.ResourceList {
	/** A set of <resource - count> pairs, where the count value indicates how many times a 
	 * resource has been added to this resource type. */
	final private Map<Resource, Integer> counter;
	/** The resources of available resources */
	final private ArrayList<Resource> resources;

    /**
     * Creates a new resource resources.
     */
    public ResourceList() {
    	counter = new TreeMap<Resource, Integer>();
    	resources = new ArrayList<Resource>();
    }
    
    /**
     * Adds a resource. If the resource isn't present in the resources, it's included with a "1" count.
     * If the resource exists already, the count is increased.
     * @param res The resource added
     */
    public synchronized void add(Resource res) {
    	Integer val = counter.get(res);
    	if (val == null) {
    		val = 1;
    		resources.add(res);
    	}
    	else
    		val++;
    	counter.put(res, val);
    }
    
    /**
     * Removes a resource. The resource can have more than one appearance in the resources. In 
     * this case, it's no t really removed.
     * @param res The resource removed.
     * @return True if the resource is completely removed from the resources. False in other case.
     */
    public synchronized boolean remove(Resource res) {
    	Integer val = counter.get(res);
    	assert val != null : "Unexpected error: Resource not found in resource type";
    	if (val > 1) {
    		counter.put(res, val - 1);
    		return false;
    	}
    	counter.remove(res);
    	resources.remove(res);
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
    	return counter.get(get(index));
    }
    
    /**
     * Returns the number of resources in this resources. 
     * @return The number of resources in this resources.
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