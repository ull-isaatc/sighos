/*
 * WorkGroup.java
 *
 * Created on 17 de noviembre de 2005, 10:27
 */

package es.ull.cyc.simulation;

import java.util.ArrayList;

import es.ull.cyc.random.RandomNumber;
import es.ull.cyc.util.*;

/**
 * Conjunto de recursos requeridos para realizar una actividad en un tiempo
 * determinado. Un equipo de trabajo se compone de una tabla de clases de recursos
 * y una función que determina cuánto tarda ese conjunto de clases de recursos en
 * realizar la actividad. También puede llevar asociado un coste.
 *
 * @author Iván Castilla Rodríguez
 */
public class WorkGroup extends SimulationObject implements Prioritizable {
    /** Equipo de trabajo que realiza esta opción de la actividad */
    protected ArrayList<ResourceTypeTableEntry> resourceTypeTable;
    /** La actividad a la que está asociado */
    protected Activity act;
    /** Duración de la opción de la actividad */
    protected RandomNumber duration;
    /** Priority of the workGroup */
    protected int priority = 0;
    
    /**
     * Creates a new instance of WorkGroup
     * @param id Identifier of this workgroup.
     * @param act Actividad a la que se asocia este equipo de trabajo.
     * @param tcr Tabla de clases de recursos que componen el equipo de trabajo.
     * @param duracion Tiempo que tarda el equipo de trabajo en realizar la actividad.
     */    
    protected WorkGroup(int id, Activity act, RandomNumber duration, int priority) {
        super(id, act.getSimul());
        this.act = act;
        this.resourceTypeTable = new ArrayList<ResourceTypeTableEntry>();
        this.duration = duration;
        this.priority = priority;
    }

    /**
     * Devuelve la actividad a la que está asociado este equipo de trabajo.
     * @return Actividad a la que se asocia este equipo de trabajo.
     */    
    protected Activity getActivity() {
        return act;
    }
    /**
     * Devuelve la tabla de clases de recursos que compone este equipo de trabajo.
     * @return La tabla de clases de recursos de este equipo de trabajo.
     */    
    public ArrayList<ResourceTypeTableEntry> getResourceTypeTable() {
        return resourceTypeTable;
    }

    /**
     * Returns the duration of the activity when this workgroup is used. 
     * The value returned by the random number function could be negative. 
     * In this case, it returns 0.0.
     * @return The activity duration.
     */
    public double getDuration() {
        return duration.samplePositiveDouble();
    }
    
    /**
     * Devuelve la distribución de probabilidad que caracteriza la duración de la
     * actividad
     * @return Distribución de probabilidad de la actividad
     */
    public RandomNumber getDistribution() {
        return duration;
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
     * Comprueba si la solución es alcanzable a partir de una solución parcial.
     * De esta manera puede cortarse el árbol de búsqueda para la solución.
     * @param pos Posición de la que se parte.
     * @param nec Recursos necesarios.
     * @param e Elemento con el que se busca.
     * @return Verdadero (true) si hay solución. Falso (false) en otro caso.
     */
    protected boolean hasSolution(int []pos, int []nec, Element e) {
        for (int i = pos[0]; i < resourceTypeTable.size(); i++) {
            ResourceTypeTableEntry actual = resourceTypeTable.get(i);
            int j = pos[1];
            Resource res;
            int disp = 0;            
            while (((res = actual.getResource(j)) != null) && (disp < nec[i])) {
                // FIXME Debería bastar con preguntar por el RT
                if ((res.getCurrentElement() == null) && (res.getCurrentResourceType() == null))
                    disp++;
                j++;
            }
            if (disp < nec[i])
                return false;
        }
        return true;
    }
    
    /**
     * Devuelve la posición de la siguiente solución válida. La función sobreentiende
     * que el valor de la posición inicial pasada por parámetro es un valor válido.
     * @param pos Posición inicial.
     * @param nec Recursos necesarios.
     * @param e Elemento con el que se busca.
     * @return Posición de la siguiente solución válida.
     */
    private int []searchNext(int[] pos, int []nec, Element e) {
        int []aux = new int[2];
        aux[0] = pos[0];
        aux[1] = pos[1];
        // Busco la primera entrada que requiera recursos
        while (nec[aux[0]] == 0) {
            aux[0]++;
            // El valor del segundo índice ya no es válido
            aux[1] = -1;
            // No hacen falta más recursos ==> SOLUCION
            if (aux[0] == resourceTypeTable.size()) {
                return aux;
            }
        }
        // Cojo la entrada correspondiente al primer índice
        ResourceType rt = resourceTypeTable.get(aux[0]).getResourceType();
        // Busco el SIGUIENTE recurso disponible a partir del índice
        aux[1] = rt.getNextAvailableResource(aux[1] + 1, e);

        // No encontré ningún recurso disponible en esta clase de recurso
        if (aux[1] == -1)
            return null;
        return aux;
    }

    /**
     * Marca un elemento como perteneciente a la solución
     * @param pos Posición del elemento
     */
    private void mark(int []pos) {
        Resource res = resourceTypeTable.get(pos[0]).getResource(pos[1]);
        res.setCurrentResourceType(resourceTypeTable.get(pos[0]).getResourceType());
    }
    
    /**
     * Quita la marca de pertenencia a la solución de un elemento
     * @param pos Posición del elemento
     */
    private void unmark(int []pos) {
        Resource res = resourceTypeTable.get(pos[0]).getResource(pos[1]);
        res.setCurrentResourceType(null);
    }

    /**
     * Makes a depth first search looking for a solution.
     * @param pos Position to look for a solution [ResourceType, Resource] 
     * @param ned Resources needed
     * @param e Element looking for the solution
     * @return True if a valid solution exists. False in other case.
     */
    private boolean findSolution(int []pos, int []ned, Element e) {
        pos = searchNext(pos, ned, e);
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
        if (hasSolution(pos, ned, e))
        // ... the search continues
            if (findSolution(pos, ned, e))
                return true;
        // There's no solution with this resource. Try without it
        unmark(pos);
        ned[pos[0]]++;
        // ... and the search continues
        return findSolution(pos, ned, e);        
    }
    
    /**
     * Distribute the resources when there is a conflict inside the activity.
     *   
     * Los recursos de la lista de recursos con múltiples roles de cada clase de 
     * recurso estarán marcados.
     * @param e Element trying to carry out the activity with this workgroup 
     * @return True if a valid solution exists. False in other case.
     */
    protected boolean distributeResources(Element e) {
        int ned[] = new int[resourceTypeTable.size()];
        int []pos = {0, -1}; // "Start" position
        
        for (int i = 0; i < resourceTypeTable.size(); i++)
            ned[i] = resourceTypeTable.get(i).getNeeded();
        // B&B algorithm for finding a solution
        if (findSolution(pos, ned, e))
            return true;
        // If there is no solution, the "books" of this element are removed
        for (int i = 0; i < resourceTypeTable.size(); i++)
            resourceTypeTable.get(i).getResourceType().resetAvailable(e);
        return false;
    }
    
    /**
     * Checks if there are enough resources to carry out an activity by using this workgroup.   
     * The "potential" available resources are booked by the element requesting the activity. 
     * If there are less available resources than needed resources for any resource type, the 
     * activity can not be carried out, and all the "books" are removed.
     * Possible conflicts between resources inside the activity are solved by invoking a
     * branch-and-bound resource distribution algorithm. 
     * @param e Element trying to carry out the activity with this workgroup 
     * @return True if there are more "potential" available resources than needed resources for
     * thiw workgroup. False in other case.
     */
    protected boolean isFeasible(Element e) {
    	boolean conflict = false;

    	e.resetConflictZone();
        for (int i = 0; i < resourceTypeTable.size(); i++) {
            ResourceTypeTableEntry rttEntry = resourceTypeTable.get(i);       	
        	ResourceType rt = rttEntry.getResourceType();
        	int []avail = rt.getAvailable(e);
        	// If there are less "potential" available resources than needed
            if (avail[0] + avail[1] < rttEntry.getNeeded()) {
            	// The element frees the previously booked resources 
                // FIXME Esto es un poco ineficiente
                rt.resetAvailable(e);
                i--;
                for (; i >= 0; i--)
                    resourceTypeTable.get(i).getResourceType().resetAvailable(e);
                e.removeFromConflictZone();
                return false;            	
            }
            // If the available resources WITH conflicts are needed
            else if (avail[0] < rttEntry.getNeeded())
                conflict = true;
        }
        // When this point is reached, that means that the activity is POTENTIALLY feasible
        e.waitConflictSemaphore();
        // Now, this element has exclusive access to its resources. It's time to "recheck"
        // if the activity is feasible        
        if (conflict) { // The resource distribution algorithm is invoked
        	print(Output.MessageType.DEBUG, "Overlapped resources", "Overlapped resources with " + e);
            if (!distributeResources(e)) {
                e.removeFromConflictZone();
            	e.signalConflictSemaphore();
            	return false;
            }
        }
        else if (e.getConflictZone().size() > 1) {
        	print(Output.MessageType.DEBUG, "Possible conflict", "Possible conflict. Recheck is needed " + e);
            int ned[] = new int[resourceTypeTable.size()];
            int []pos = {0, 0}; // "Start" position
            for (int i = 0; i < resourceTypeTable.size(); i++)
                ned[i] = resourceTypeTable.get(i).getNeeded();
        	if (!hasSolution(pos, ned, e)) {
                e.removeFromConflictZone();
            	e.signalConflictSemaphore();
            	// The element frees the previously booked resources 
            	for (ResourceTypeTableEntry rttEntry : resourceTypeTable)
            		rttEntry.getResourceType().resetAvailable(e);
        		return false;
        	}
        }
        return true;
    }

    /**
     * Método que quita las unidades necesarias para una actividad a las
     * unidades disponibles en las Clases de Recursos.
     * @param e Elemento que está cogiendo los recursos
     */
    protected void catchResources(Element e) {
       for (ResourceTypeTableEntry rtte : resourceTypeTable) {
           rtte.getResourceType().decAvailable(rtte.getNeeded(), e);
       }
       // When this point is reached, that means that the resources have been completely taken
       e.signalConflictSemaphore();
    }
    
    /**
     * Releases the resources caught by an element.
     * @param e The element returning the resources.
     * @return A list of activity managers affected by the released resources
     */
    protected ArrayList<ActivityManager> releaseResources(Element e) {
        ArrayList<ActivityManager> amList = new ArrayList<ActivityManager>();
        ArrayList <Resource>resourceList = e.getCaughtResources();
        for (Resource res : resourceList) {
        	print(Output.MessageType.DEBUG, "Returned " + res);
        	// The resource is freed
        	if (res.releaseResource()) {
        		// The activity managers involved are included in the list
        		ArrayList<ActivityManager> auxList = res.getCurrentManagers();
        		for (int j = 0; j < auxList.size(); j++)
        			if (!amList.contains(auxList.get(j)))
        				amList.add(auxList.get(j));
        	}
        }
        return amList;
    }

    public String toString() {
    	return new String("(" + act + ")" + super.toString());
    }

	public String getObjectTypeIdentifier() {
		return "WGR";
	}

	public double getTs() {
		return act.getTs();
	}

	// FIXME
	public String getDescription() {
       StringBuffer str = new StringBuffer(this.toString() + "\tResource Table:\n"); 
       for (int i = 0; i < resourceTypeTable.size(); i++) {
           ResourceTypeTableEntry actual = resourceTypeTable.get(i);
           str.append(" | "+ actual.getResourceType().getDescription()+" | "+actual.getNeeded()+"\n");
        }
       return str.toString();
	}

}
