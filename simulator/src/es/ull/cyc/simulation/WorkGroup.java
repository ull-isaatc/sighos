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
    protected ArrayList resourceTypeTable;
    /** La actividad a la que está asociado */
    protected Activity act;
    /** Duración de la opción de la actividad */
    protected RandomNumber duration;
    /** Un posible cost */
    protected double cost;
    /** Priority of the workGroup */
    protected int priority = 0;
    
    /**
     * Creates a new instance of WorkGroup
     * @param id Identifier of this workgroup.
     * @param act Actividad a la que se asocia este equipo de trabajo.
     * @param tcr Tabla de clases de recursos que componen el equipo de trabajo.
     * @param duracion Tiempo que tarda el equipo de trabajo en realizar la actividad.
     * @param coste Coste asociado a este equipo de trabajo.
     */    
    protected WorkGroup(int id, Activity act, RandomNumber duration, int priority, double cost) {
        super(id, act.getSimul());
        this.act = act;
        this.resourceTypeTable = new ArrayList();
        this.duration = duration;
        this.priority = priority;
        this.cost = cost;
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
    public ArrayList getResourceTypeTable() {
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
     * Devuelve el cost de la opción
     * @return cost de la opción
     */
    public double getCost() {
        return cost;
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
        ResourceTypeTableEntry rtte = (ResourceTypeTableEntry) resourceTypeTable.get(ind);
        return rtte.getRType();
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
        ResourceTypeTableEntry rtte = (ResourceTypeTableEntry) resourceTypeTable.get(ind);
        return rtte.getNeeded();
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
       ResourceTypeTableEntry current;

       while ( (index < size) && ! found ) {
           current = (ResourceTypeTableEntry) resourceTypeTable.get(index);
           if ( current.getRType() == rt )
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
    protected boolean hasSolution(int []pos, int []nec, BasicElement e) {
        for (int i = pos[0]; i < resourceTypeTable.size(); i++) {
            ResourceTypeTableEntry actual = (ResourceTypeTableEntry) resourceTypeTable.get(i);
            int j = pos[1];
            MultipleRole erm;
            int disp = 0;
            while (((erm = actual.getRType().getAvailableResource(j)) != null) && (disp < nec[i])) {
                if ((erm.getBookedElement() == e) && (erm.getBookedResourceType() == null))
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
    private int []searchNext(int[] pos, int []nec, BasicElement e) {
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
        ResourceTypeTableEntry actual = (ResourceTypeTableEntry)resourceTypeTable.get(aux[0]);
        // Busco el SIGUIENTE recurso disponible a partir del índice
        aux[1] = actual.getRType().getBookedResource(aux[1] + 1, e);

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
        ResourceTypeTableEntry actual = (ResourceTypeTableEntry)resourceTypeTable.get(pos[0]);
        MultipleRole erm = actual.getRType().getAvailableResource(pos[1]);
        erm.setBookedResourceType(actual.getRType());
    }
    
    /**
     * Quita la marca de pertenencia a la solución de un elemento
     * @param pos Posición del elemento
     */
    private void unmark(int []pos) {
        ResourceTypeTableEntry actual = (ResourceTypeTableEntry)resourceTypeTable.get(pos[0]);
        MultipleRole erm = actual.getRType().getAvailableResource(pos[1]);
        erm.setBookedResourceType(null);
    }

    /**
     * Realiza una búsqueda en profundidad de una posible solución
     * @param pos Posición de partida en la solución
     * @param nec Array de recursos necesarios para la solución
     * @param e Elemento que necesita la solución
     * @return Verdadero (true) si encontró una solución válida; falso (false)
     * en otro caso.
     */
    private boolean findSolution(int []pos, int []nec, BasicElement e) {
        pos = searchNext(pos, nec, e);
        // No se encontró solución por este camino
        if (pos == null)
            return false;
        // No hacían falta más elementos => SOLUCION ENCONTRADA
        if (pos[0] == resourceTypeTable.size())
            return true;
        // Marco el recurso como miembro de la solución ...
        mark(pos);
        nec[pos[0]]--;
        // Mejora para cortar
        if (hasSolution(pos, nec, e))
        // ... y continúo buscando
            if (findSolution(pos, nec, e))
                return true;
        // Marcando el recurso no encontré solución, así que pruebo sin marcarlo
        unmark(pos);
        nec[pos[0]]++;
        // Busco una nueva solución a partir de la actual
        return findSolution(pos, nec, e);        
    }
    
    /**
     * Función que reparte los recursos cuando están solapados. Todos los 
     * recursos de la lista de recursos con múltiples roles de cada clase de 
     * recurso estarán marcados.
     * @param e Elemento que está intentando solicitar la actividad
     * @return Verdadero (true) si encontró una solución válida; falso (false)
     * en otro caso.
     */
    protected boolean distributeResources(BasicElement e) {
        int nec[] = new int[resourceTypeTable.size()];
        int []pos = {0, -1}; // Posición de partida
        MultipleRole erm;
        
        // Simplificación de la búsqueda
        for (int i = 0; i < resourceTypeTable.size(); i++) {
            ResourceTypeTableEntry actual = (ResourceTypeTableEntry)resourceTypeTable.get(i);
            nec[i] = actual.getNeeded();
            int disp = actual.getRType().getAvailable();
            if (disp >= nec[i])
                nec[i] = 0;
            else // No puedo solucionarlo con los recursos "simples"
                nec[i] -= disp;
            // Sustituyo a "e" por "e1" en todos los recursos reservados
        }
        // Se busca la solución mediante "fuerza bruta"
        if (findSolution(pos, nec, e)) {
            for (int i = 0; i < resourceTypeTable.size(); i++) {
                ResourceTypeTableEntry actual = (ResourceTypeTableEntry)resourceTypeTable.get(i);
                for (int j = 0; (erm = actual.getRType().getAvailableResource(j)) != null; j++)
                    if ((erm.getBookedElement() == e) && (erm.getBookedResourceType() == null)) // Este elemento no nos interesa
                        erm.releaseBooking();
            }
            return true;
        }
        // Si no encontró solución hay que volver a dejar los recursos como estaban
        for (int i = 0; i < resourceTypeTable.size(); i++) {
            ResourceTypeTableEntry actual = (ResourceTypeTableEntry)resourceTypeTable.get(i);
            actual.getRType().resetAvailable(e);
        }
        return false;
    }
    
    /**
     * MOD 28/10/04 
     * Método para comprobar si para toda la tabla el número de elementos disponibles es mayor o igual
     * que el de necesarios. Si es así devuelve cierto, y si no devuelve falso
     * La función marca todos aquellos recursos con roles solapados en horario
     * necesarios para realizar la actividad. Son estos recursos marcados y no 
     * otros los que se cogen al realizar la actividad.
     * En caso de no ser realizable la actividad se desmarca cualquier recurso
     * reservado durante este proceso.
     * @param e Elemento que está intentando solicitar la actividad
     * @return Verdadero si hay tantas unidades disponibles de cada Clase de Recurso como son necesarias
     */
    protected boolean isFeasible(BasicElement e) {
        boolean solapado = false;
        ResourceTypeTableEntry actual;
        
        for (int i = 0; i < resourceTypeTable.size(); i++) {
            actual = (ResourceTypeTableEntry)resourceTypeTable.get(i);
            int []disp = actual.getRType().getAvailable(e);
            if (disp[0] + disp[1] < actual.getNeeded()) {
                // Deshago las posibles reservas realizadas 
                // FIXME Esto es un poco ineficiente
                actual.getRType().resetAvailable(e);
                i--;
                for (; i >= 0; i--) {
                    actual = (ResourceTypeTableEntry)resourceTypeTable.get(i);
                    actual.getRType().resetAvailable(e);
                }
                return false;
            }
            else if (disp[0] < actual.getNeeded())
                solapado = true;
        }
        if (solapado) {// Llamar a la función de distribución de recursos
        	print(Output.DEBUGMSG, "Overlapped resources", "Overlapped resources with " + e);
            return distributeResources(e);
        }
        return(true); // devuelve cierto si llego al final y hay al menos tantas disponibles como necesarias

    }

    /**
     * Método que quita las unidades necesarias para una actividad a las
     * unidades disponibles en las Clases de Recursos.
     * @param e Elemento que está cogiendo los recursos
     */
    protected void catchResources(BasicElement e) {
       for (int i = 0; i < resourceTypeTable.size(); i++) {
           ResourceTypeTableEntry actual = (ResourceTypeTableEntry)resourceTypeTable.get(i);
           actual.getRType().decAvailable(actual.getNeeded(), e);
       }        
    }
    
    /**
     * Método que devuelve las unidades necesarias para una actividad a las
     * unidades disponibles en las Clases de Recursos.
     * @param e Elemento que está devolviendo los recursos
     * @return Una lista con todos los gestores de actividades afectados por la
     * devolución de alguno de los recursos (recursos con horarios solapados en
     * distintos GAs)
     */
    protected ArrayList releaseResources(BasicElement e) {
        ArrayList listaGA = new ArrayList();
        for (int i = 0; i < resourceTypeTable.size(); i++) {
            ResourceTypeTableEntry actual = (ResourceTypeTableEntry)resourceTypeTable.get(i);
            ArrayList listaAux = actual.getRType().incAvailable(actual.getNeeded(), e);
            // MOD 2/11/04 
            for (int j = 0; j < listaAux.size(); j++) {
                ActivityManager ga = (ActivityManager) listaAux.get(j);
                if (!listaGA.contains(ga))
                    listaGA.add(ga);
            }
        }
        return listaGA;
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
           ResourceTypeTableEntry actual = (ResourceTypeTableEntry) resourceTypeTable.get(i);
           str.append(" | "+ actual.getRType().getDescription()+" | "+actual.getNeeded()+"\n");
        }
       return str.toString();
	}

}
