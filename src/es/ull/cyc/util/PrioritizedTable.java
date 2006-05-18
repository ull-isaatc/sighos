
package es.ull.cyc.util;

import java.util.ArrayList;

/**
 * Estructura para almacenar todos los objetos de la misma prioridad.
 * Funciona como un "pool", es decir, el objeto que se escoge en primer lugar
 * var�a cada vez mediante la rotaci�n de un �ndice.
 * @author Iv�n Castilla Rodr�guez
 */
class PrioritizedLevel extends ArrayList {
    /** Indice del siguiente objeto que puede elegirse */
    protected int chosen;
    /** Prioridad de este nivel */
    protected int priority;
    
    /**
     * Constructor que indica la prioridad de las actividades
     * @param pri Prioridad de este nivel.
     */
    PrioritizedLevel(int pri) {
        super();
        chosen = 0;
        priority = pri;
    }

    /**
     * Permite obtener el siguiente objeto del pool. Cada vez que se llama a 
     * esta funci�n el pool devuelve un objeto distinto.
     * @return El siguiente objeto del pool
     */
    public Prioritizable get() {
        Prioritizable obj = (Prioritizable) get(chosen);
        chosen = (chosen + 1) % size();
        return obj;
    }    
}

/**
 * Estructura que contiene objetos organizados por niveles de prioridad. Los 
 * niveles del pool est�n ordenados seg�n su prioridad, y dentro de cada nivel 
 * todos los objetos tienen la misma probabilidad de ser escogidos.
 * @author Iv�n Castilla Rodr�guez
 */
public class PrioritizedTable {
    /** Lista de todos los niveles del pool. */    
    protected ArrayList levels;
    
    /** Creates a new instance of PoolObjetos */
    public PrioritizedTable() {
        levels = new ArrayList();
    }
    
	/**
     * Devuelve el �ndice del primer nivel de la cola con prioridad pri. 
     * Si no hay ning�n nivel con esa prioridad, devuelve el �ndice del primer  
     * nivel con un valor de prioridad mayor (o el tama�o de este pool de
     * objetos si no hay ning�n nivel con prioridad mayor).
     * @param pri Prioridad 
     * @return El �ndice del primer nivel con esa prioridad o el tama�o del pool 
     * si la prioridad es mayor que cualquier nivel
     */
    public int searchLevel(int pri) {
        PrioritizedLevel aux;
        int ind, i = 0, j = levels.size();

        while (i != j) {
            ind = (i + j) / 2;
            aux = (PrioritizedLevel) levels.get(ind);
            if (aux.priority < pri)
                i = ind + 1;
            else if (aux.priority > pri)
                j = ind;
            else {
                i = ind;
                j = ind;
            }
        }
        return i;
    } // Fin de BuscaNivel

	/**
     * A�ade un objeto al pool, insert�ndola en orden seg�n su prioridad.
     * @param obj Objeto a a�adir
     * @param prioridad Prioridad con la que se a�ade el objeto.
     */
    public void add(Prioritizable obj) {
        int ind = searchLevel(obj.getPriority());
        PrioritizedLevel pLevel = null;
        if (ind == levels.size()) {
            pLevel = new PrioritizedLevel(obj.getPriority());
            levels.add(pLevel);
        }
        else {
            pLevel = (PrioritizedLevel) levels.get(ind);
            if (pLevel.priority != obj.getPriority()) {
                pLevel = new PrioritizedLevel(obj.getPriority());
                levels.add(ind, pLevel);
            }
        }
        pLevel.add(obj);
	}

	/**
     * Devuelve el n�mero total de elementos que contiene el pool
     * @return El tama�o total del pool
     */
	public int size() {
        int suma = 0;
        for (int i = 0; i < levels.size(); i++) {
            PrioritizedLevel pLevel = (PrioritizedLevel) levels.get(i);
            suma += pLevel.size();
        }
		return suma;
	}
    
    /**
     * Construye un array conteniendo todos los objetos del pool ordenados por
     * prioridad.
     * @return Un array que contiene todos los objetos del pool.
     */
    public Prioritizable []toArray() {
        int cont = 0;
        Prioritizable []array = new Prioritizable[size()];
        for (int i = 0; i < levels.size(); i++) {
            PrioritizedLevel level = (PrioritizedLevel) levels.get(i);
            for (int j = 0; j < level.size(); j++)
                array[cont++] = (Prioritizable)level.get(j);            
        }
        return array;
    }
}
