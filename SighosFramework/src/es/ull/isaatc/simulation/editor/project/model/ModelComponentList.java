package es.ull.isaatc.simulation.editor.project.model;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

/**
 * An id ordered list of ModelComponent objects.
 * @author Roberto Muñoz
 */
public class ModelComponentList {
	/** By-id ordered list */
	ArrayList<ModelComponent> list;

	/** Creates a new instance of ModelComponentList */
	public ModelComponentList() {
		list = new ArrayList<ModelComponent>();
	}

	/**
	 * Add a new object to the list ordered by its id.
	 * @param newObj New object to be added
	 * @return True is the insertion was succesful. False if there already was  
	 * an object with the same id in the list.
	 */
	public boolean add(ModelComponent newObj) {
		int index = Collections.binarySearch(list, newObj);
		if (index < 0) {
			index = -index - 1;
			list.add(index, newObj);
			return true;
		}
		return false;
	}

	/**
	 * Remove an object from the list
	 * @param index objects index in the list
	 */
	public void remove(int index) {
		list.remove(index);
	}

	/**
	 * @param index object position in the list
	 * @return object at index position. null if index is invalids
	 */
	public ModelComponent get(int index) {
		if ((index >= list.size()) || (index < 0))
			return null;
		return list.get(index);
	}

	/**
	 * Searches for the object
	 * @param obj Searched object.
	 * @return the index of the object in the list 
	 */
	public int indexOf(ModelComponent obj) {
		return list.indexOf(obj);
	}

	/**
	 * Searches for the object with the expressed id
	 * @param id
	 * @return the object if is it found. null in other case
	 */
	public ModelComponent search(int key) {
		int index = Collections.binarySearch(list, new Integer(key));
		if (index < 0)
			return null;
		return list.get(index);
	}

	/**
	 * Searches for the objects with the expressed description
	 * @param description
	 * @return the objects found. null in other case
	 */
	public List<ModelComponent> search(String description) {
		List<ModelComponent> result = new ArrayList<ModelComponent>();
		if (description == null)
			return result;
		if (description.length() == 0)
			return result;
		
		for (int i = 0;  i < size(); i++) {
			if (list.get(i).description.equals(description))
				result.add(list.get(i));
		}
		return result;
	}
	
	/**
	 * Returns the size of the list.
	 * @return Size of the list.
	 */
	public int size() {
		return list.size();
	}
	
	public List getXML() {
		List xmlList = new ArrayList();
		Iterator<ModelComponent> listIt = list.iterator();
		while (listIt.hasNext())
			xmlList.add(listIt.next().getXML());
		return xmlList;
	}
}
