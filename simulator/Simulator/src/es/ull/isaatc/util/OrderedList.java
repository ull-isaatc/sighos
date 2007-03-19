/**
 * 
 */
package es.ull.isaatc.util;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;

/**
 * An ordered object list. The order is based on the object's key.  
 * @author Iván Castilla Rodríguez
 */
public class OrderedList<T extends Orderable> extends ArrayList<T> {
	private static final long serialVersionUID = 8437034514869779814L;

	/** Creates a new instance of IdObjectList */
    public OrderedList() {
    	super();
    }
    
    /**
     * Add a new object to the list ordered by its identifier.
     * @param newObj New object to be added
     * @return True is the insertion was succesful. False if there is  
     * an object with the same identifier in the list.
     */
    public boolean add(T newObj) {
    	int index = Collections.binarySearch(this, newObj.getKey());
    	if (index >= 0)
    		return false;
    	super.add(-index - 1, newObj);
        return true;
    }
    
    /**
     * Inserts orderly all the components of the specified collection.
     * @param other Other collection of objects
     * @return True if anyone of the components is at the current list yet.
     * False in other case. 
     */
    public boolean addAll(Collection<? extends T> other) {
    	boolean result = true;
    	for (T obj : other) {
    		if (!add(obj))
    			result = false;
    	}
    	return result;
    }
    
    /**
     * Searches an object in the list with the specified key.
     * @param id The key that identifies the object
     * @return The object with the specified key. null if the object is not
     * in the list.
     */
    public T get(Comparable key) {
    	int index = Collections.binarySearch(this, key);
    	if (index < 0)
    		return null;
    	return super.get(index);
    }    
}
