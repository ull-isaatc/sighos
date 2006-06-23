/*
 * FlujoGrupo.java
 *
 * Created on 17 de junio de 2005, 12:48
 */

package es.ull.isaatc.simulation;

import java.util.ArrayList;

/**
 * Representa un nodo de un flujo que contiene m�s de un flujo.
 * @author Iv�n Castilla Rodr�guez
 */
public abstract class GroupFlow extends Flow {
    /**
     * Lista de flujos que contiene este flujo.
     */    
    protected ArrayList<Flow> list;
    /**
     * N�mero de flujos de este flujo cuya ejecuci�n ha finalizado.
     */    
    protected int finishedFlows;
    
    /**
     * Crea un nuevo grupo de flujos.
     * @param elem Elemento al que est� asociado este flujo.
     */
    public GroupFlow(Element elem) {
        super(elem);
        list = new ArrayList<Flow>();
    }
    
    /**
     * Crea un nuevo grupo de flujos.
     * @param parent Flujo padre del actual.
     * @param elem Elemento al que est� asociado este flujo.
     */
    public GroupFlow(GroupFlow parent, Element elem) {
        super(parent, elem);
        if (parent != null)
        	parent.add(this);
        list = new ArrayList<Flow>();
    }
    
    /**
     * A�ade un nuevo flujo al final de la lista de flujos.
     * @param newFlow Nuevo flujo a a�adir.
     */    
    public void add(Flow newFlow) {
        list.add(newFlow);
    }
    
    /**
     * Inserta un nuevo flujo en la posici�n indicada de la lista de flujos.
     * @param pos Posici�n de la lista de flujos donde se inserta el nuevo flujo.
     * @param newFlow Nuevo flujo a a�adir.
     */    
    public void add(int pos, Flow newFlow) {
        list.add(pos, newFlow);
    }
    
    /**
     * Devuelve el n�mero de actividades que contiene la lista de flujos.
     * @return El n�mero de actividades del flujo actual.
     */    
    protected int[] countActivities() {
        int []cont = new int[2];
        cont[0] = cont[1] = 0;
        for (int i = 0; i < list.size(); i++) {
            int []contAux = list.get(i).countActivities();
            cont[0] += contAux[0];
            cont[1] += contAux[1];
        }
        return cont;
    }
       
}
