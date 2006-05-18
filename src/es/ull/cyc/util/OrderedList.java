/**
 * 
 */
package es.ull.cyc.util;

import java.util.ArrayList;
import java.util.Collections;

/**
 * An ordered object list. The order is based on the object's key.  
 * @author Iván Castilla Rodríguez
 */
public class OrderedList {
    /** A list that contains the elements ordered by their key. */    
    ArrayList list = null;
    
    /** Creates a new instance of IdObjectList */
    public OrderedList() {
        list = new ArrayList();
    }
    
    /**
     * Add a new object to the list ordered by its identifier.
     * @param newObj New object to be added
     * @return True is the insertion was succesful. False if there is  
     * an object with the same identifier in the list.
     */
    public boolean add(Orderable newObj) {
    	int index = Collections.binarySearch(list, newObj.getKey());
    	if (index >= 0)
    		return false;
    	list.add(-index - 1, newObj);
        return true;
    }
    
    /**
     * Searches an object in the list with the specified key.
     * @param id The key that identifies the object
     * @return The object with the specified key. null if the object is not
     * in the list.
     */
    public Orderable get(Comparable key) {
    	int index = Collections.binarySearch(list, key);
    	if (index < 0)
    		return null;
    	return (Orderable)list.get(index);
    }
    
    /**
     * Searches an object in the list at the specified position.
     * @param index Position of the object.
     * @return The object at the specified position. null if the index is not
     * valid.
     */    
    public Orderable get(int index) {
        if ((index >= list.size()) || (index < 0))
            return null;
        return (Orderable) list.get(index);
    }
    
    /**
     * Searches for the first ocurrence of the given argument.
     * @param obj Searched object.
     * @return The index of the first argument ocurrence in this list. -1 if the 
     * object is not found.
     */
    public int indexOf(Orderable obj) {
        return list.indexOf(obj);
    }
    
    /**
     * Returns the size of the list.
     * @return Size of the list.
     */
    public int size() {
        return list.size();
    }
}
