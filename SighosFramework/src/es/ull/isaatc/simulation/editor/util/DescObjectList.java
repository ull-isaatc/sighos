/*
 * IdObjectList.java
 *
 * Created on 9 de noviembre de 2005, 13:29
 */

package es.ull.isaatc.simulation.editor.util;

import java.util.ArrayList;


/**
 * A by-description-ordered list of ModelComponent objects.
 * @author Iván Castilla Rodríguez
 */
public class DescObjectList {
    /** By-description ordered list */    
    ArrayList<ModelComponent> list = null;
    
    /** Creates a new instance of IdObjectList */
    public DescObjectList() {
        list = new ArrayList<ModelComponent>();
    }
    
    /**
     * Add a new object to the list ordered by its description.
     * @param newObj New object to be added
     * @return True is the insertion was succesful. False if there already was  
     * an object with the same description in the list.
     */
    public boolean add(ModelComponent newObj) {
        int middle = 0;
        int first = 0;
        int last = list.size() - 1;
        while (first <= last) {
            middle = (first + last) / 2;
            ModelComponent obj = (ModelComponent) list.get(middle);
            int resul = obj.getId() - newObj.getId();
            if (resul < 0)
                first = middle + 1;
            else if (resul > 0)
                last = middle - 1;
            else
                return false;
        }
        list.add(first, newObj);
        return true;
    }
    
    /**
     * Remove an object from the list
     * @param index objects index in the list
     */
    public void remove(int index) {
    	list.remove(index);
    }
    
    /**
     * Devuelve el objeto de la list que corresponde a la descripción indicada.
     * @param descripcion Descripción del objeto que se busca en la list
     * @return El objeto que corresponde a la descripción indicada. null si no
     * se encuentra.
     */
    public ModelComponent getByDesc(String description) {
        if (description == null)
            return null;
        int middle = 0;
        int first = 0;
        int last = list.size() - 1;
        while (first <= last) {
            middle = (first + last) / 2;
            ModelComponent obj = (ModelComponent) list.get(middle);
            int resul = obj.getDescription().compareTo(description);
            if (resul < 0)
                first = middle + 1;
            else if (resul > 0)
                last = middle - 1;
            else
                return obj;
        }
        return null;
    }
    
    /**
     * Devuelve el objeto de la list que se encuentra en la posición indicada.
     * @param indice Posición del objeto en la list.
     * @return El objeto que corresponde a la posición indicada. null si el 
     * índice no es válido.
     */    
    public ModelComponent get(int index) {
        if ((index >= list.size()) || (index < 0))
            return null;
        return (ModelComponent) list.get(index);
    }
    
    /**
     * Searches for the first ocurrence of the given argument.
     * @param obj Searched object.
     * @return The index of the first argument ocurrence in this list. -1 if the 
     * object is not found.
     */
    public int indexOf(ModelComponent obj) {
        return list.indexOf(obj);
    }
    
    /**
     * Returns the size of the list.
     * @return Size of the list.
     */
    public int size() {
        return list.size();
    }
    
    public Object[] toArray() {
    	Object[] array = new Object[list.size()];
    	for (int i = 0; i < list.size(); i++)
    		array[i] = list.get(i);
    	return array;
    }
}
