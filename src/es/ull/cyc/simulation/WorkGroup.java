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
 * y una funci�n que determina cu�nto tarda ese conjunto de clases de recursos en
 * realizar la actividad. Tambi�n puede llevar asociado un coste.
 *
 * @author Iv�n Castilla Rodr�guez
 */
public class WorkGroup extends SimulationObject implements Prioritizable {
    /** Equipo de trabajo que realiza esta opci�n de la actividad */
    protected ArrayList resourceTypeTable;
    /** La actividad a la que est� asociado */
    protected Activity act;
    /** Duraci�n de la opci�n de la actividad */
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
     * Devuelve la actividad a la que est� asociado este equipo de trabajo.
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
     * Devuelve la distribuci�n de probabilidad que caracteriza la duraci�n de la
     * actividad
     * @return Distribuci�n de probabilidad de la actividad
     */
    public RandomNumber getDistribution() {
        return duration;
    }
    
    /**
     * Devuelve el cost de la opci�n
     * @return cost de la opci�n
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
     * Comprueba si la soluci�n es alcanzable a partir de una soluci�n parcial.
     * De esta manera puede cortarse el �rbol de b�squeda para la soluci�n.
     * @param pos Posici�n de la que se parte.
     * @param nec Recursos necesarios.
     * @param e Elemento con el que se busca.
     * @return Verdadero (true) si hay soluci�n. Falso (false) en otro caso.
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
     * Devuelve la posici�n de la siguiente soluci�n v�lida. La funci�n sobreentiende
     * que el valor de la posici�n inicial pasada por par�metro es un valor v�lido.
     * @param pos Posici�n inicial.
     * @param nec Recursos necesarios.
     * @param e Elemento con el que se busca.
     * @return Posici�n de la siguiente soluci�n v�lida.
     */
    private int []searchNext(int[] pos, int []nec, BasicElement e) {
        int []aux = new int[2];
        aux[0] = pos[0];
        aux[1] = pos[1];
        // Busco la primera entrada que requiera recursos
        while (nec[aux[0]] == 0) {
            aux[0]++;
            // El valor del segundo �ndice ya no es v�lido
            aux[1] = -1;
            // No hacen falta m�s recursos ==> SOLUCION
            if (aux[0] == resourceTypeTable.size()) {
                return aux;
            }
        }
        // Cojo la entrada correspondiente al primer �ndice
        ResourceTypeTableEntry actual = (ResourceTypeTableEntry)resourceTypeTable.get(aux[0]);
        // Busco el SIGUIENTE recurso disponible a partir del �ndice
        aux[1] = actual.getRType().getBookedResource(aux[1] + 1, e);

        // No encontr� ning�n recurso disponible en esta clase de recurso
        if (aux[1] == -1)
            return null;
        return aux;
    }

    /**
     * Marca un elemento como perteneciente a la soluci�n
     * @param pos Posici�n del elemento
     */
    private void mark(int []pos) {
        ResourceTypeTableEntry actual = (ResourceTypeTableEntry)resourceTypeTable.get(pos[0]);
        MultipleRole erm = actual.getRType().getAvailableResource(pos[1]);
        erm.setBookedResourceType(actual.getRType());
    }
    
    /**
     * Quita la marca de pertenencia a la soluci�n de un elemento
     * @param pos Posici�n del elemento
     */
    private void unmark(int []pos) {
        ResourceTypeTableEntry actual = (ResourceTypeTableEntry)resourceTypeTable.get(pos[0]);
        MultipleRole erm = actual.getRType().getAvailableResource(pos[1]);
        erm.setBookedResourceType(null);
    }

    /**
     * Realiza una b�squeda en profundidad de una posible soluci�n
     * @param pos Posici�n de partida en la soluci�n
     * @param nec Array de recursos necesarios para la soluci�n
     * @param e Elemento que necesita la soluci�n
     * @return Verdadero (true) si encontr� una soluci�n v�lida; falso (false)
     * en otro caso.
     */
    private boolean findSolution(int []pos, int []nec, BasicElement e) {
        pos = searchNext(pos, nec, e);
        // No se encontr� soluci�n por este camino
        if (pos == null)
            return false;
        // No hac�an falta m�s elementos => SOLUCION ENCONTRADA
        if (pos[0] == resourceTypeTable.size())
            return true;
        // Marco el recurso como miembro de la soluci�n ...
        mark(pos);
        nec[pos[0]]--;
        // Mejora para cortar
        if (hasSolution(pos, nec, e))
        // ... y contin�o buscando
            if (findSolution(pos, nec, e))
                return true;
        // Marcando el recurso no encontr� soluci�n, as� que pruebo sin marcarlo
        unmark(pos);
        nec[pos[0]]++;
        // Busco una nueva soluci�n a partir de la actual
        return findSolution(pos, nec, e);        
    }
    
    /**
     * Funci�n que reparte los recursos cuando est�n solapados. Todos los 
     * recursos de la lista de recursos con m�ltiples roles de cada clase de 
     * recurso estar�n marcados.
     * @param e Elemento que est� intentando solicitar la actividad
     * @return Verdadero (true) si encontr� una soluci�n v�lida; falso (false)
     * en otro caso.
     */
    protected boolean distributeResources(BasicElement e) {
        int nec[] = new int[resourceTypeTable.size()];
        int []pos = {0, -1}; // Posici�n de partida
        MultipleRole erm;
        
        // Simplificaci�n de la b�squeda
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
        // Se busca la soluci�n mediante "fuerza bruta"
        if (findSolution(pos, nec, e)) {
            for (int i = 0; i < resourceTypeTable.size(); i++) {
                ResourceTypeTableEntry actual = (ResourceTypeTableEntry)resourceTypeTable.get(i);
                for (int j = 0; (erm = actual.getRType().getAvailableResource(j)) != null; j++)
                    if ((erm.getBookedElement() == e) && (erm.getBookedResourceType() == null)) // Este elemento no nos interesa
                        erm.releaseBooking();
            }
            return true;
        }
        // Si no encontr� soluci�n hay que volver a dejar los recursos como estaban
        for (int i = 0; i < resourceTypeTable.size(); i++) {
            ResourceTypeTableEntry actual = (ResourceTypeTableEntry)resourceTypeTable.get(i);
            actual.getRType().resetAvailable(e);
        }
        return false;
    }
    
    /**
     * MOD 28/10/04 
     * M�todo para comprobar si para toda la tabla el n�mero de elementos disponibles es mayor o igual
     * que el de necesarios. Si es as� devuelve cierto, y si no devuelve falso
     * La funci�n marca todos aquellos recursos con roles solapados en horario
     * necesarios para realizar la actividad. Son estos recursos marcados y no 
     * otros los que se cogen al realizar la actividad.
     * En caso de no ser realizable la actividad se desmarca cualquier recurso
     * reservado durante este proceso.
     * @param e Elemento que est� intentando solicitar la actividad
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
        if (solapado) {// Llamar a la funci�n de distribuci�n de recursos
        	print(Output.DEBUGMSG, "Overlapped resources", "Overlapped resources with " + e);
            return distributeResources(e);
        }
        return(true); // devuelve cierto si llego al final y hay al menos tantas disponibles como necesarias

    }

    /**
     * M�todo que quita las unidades necesarias para una actividad a las
     * unidades disponibles en las Clases de Recursos.
     * @param e Elemento que est� cogiendo los recursos
     */
    protected void catchResources(BasicElement e) {
       for (int i = 0; i < resourceTypeTable.size(); i++) {
           ResourceTypeTableEntry actual = (ResourceTypeTableEntry)resourceTypeTable.get(i);
           actual.getRType().decAvailable(actual.getNeeded(), e);
       }        
    }
    
    /**
     * M�todo que devuelve las unidades necesarias para una actividad a las
     * unidades disponibles en las Clases de Recursos.
     * @param e Elemento que est� devolviendo los recursos
     * @return Una lista con todos los gestores de actividades afectados por la
     * devoluci�n de alguno de los recursos (recursos con horarios solapados en
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
