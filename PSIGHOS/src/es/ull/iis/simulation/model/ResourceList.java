/**
 * 
 */
package es.ull.iis.simulation.model;

import java.util.Collection;

/**
 * @author Iván Castilla
 *
 */
public abstract class ResourceList {

	/**
	 * 
	 */
	public ResourceList() {
	}
	
    /**
     * Adds a resource. If the resource isn't present in the list, it's included with a "1" count.
     * If the resource exists already, the count is increased.
     * @param res The resource added
     */
	protected abstract void add(Resource res);
    /**
     * Removes a resource. The resource can have more than one appearance in the list. In 
     * this case, it's no t really removed.
     * @param res The resource removed.
     * @return True if the resource is completely removed from the list. False in other case.
     */
	protected abstract boolean remove(Resource res);
    /**
     * Returns the resource at the specified position 
     * @param index The position of the resource
     * @return The resource at the specified position.
     */
    protected abstract Resource get(int index);
    
    /**
     * Returns the count at the specified position
     * @param index The position of the count.
     * @return the count at the specified position
     */
    protected abstract int getCounter(int index);
    
    /**
     * Returns the number of resources in this list. 
     * @return The number of resources in this list.
     */
    protected abstract int size();
    /**
     * Returns the list of available resources
     * @return the list of available resources
     */
	protected abstract Collection<Resource> getResources();
	
}
