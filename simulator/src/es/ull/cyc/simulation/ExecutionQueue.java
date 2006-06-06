/*
 * ControladorEjecucion.java
 *
 * Created on 13 de junio de 2005, 19:39
 */

package es.ull.cyc.simulation;

import java.util.Vector;
import es.ull.cyc.sync.*;
import es.ull.cyc.util.*;

/**
 * Controla la cola de ejecución del proceso lógico. Todos los elementos que 
 * tienen que realizar alguna acción se van a añadiendo a una cola de ejecución. 
 * Desde esta cola se les busca un thread libre en el pool donde poder ejecutar 
 * esta acción.
 * @author Iván Castilla Rodríguez
 */
public class ExecutionQueue {
    /** Proceso lógico al que está asociado este controlador */
    protected LogicalProcess lp;
    /** Pool de elementos en ejecución */
    protected ThreadPool tp;
	/** Cola de elementos de sim. que en este momento estan en ejecucion */
	protected Vector<BasicElement.Event> executionQueue;
    
    /** 
     * Crea un nuevo ControladorElementos.
     * @param pl Proceso lógico al que está asociado.
     */
    public ExecutionQueue(LogicalProcess pl) {
        this.lp = pl;
        tp = new ThreadPool(3, 3);
        executionQueue = new Vector<BasicElement.Event>();
    }
    
    /**
     * Quita un elemento de la cola.
     * @param e Elemento a quitar
     * @return Verdadero (true) si estaba y pudo quitarlo de la cola
     */
	protected synchronized boolean removeEvent(BasicElement.Event e) {
		if (executionQueue.remove(e)) { // pudo quitarse
			// si era el último tiene que notificarlo, se comprueba que ademas no hay operaciones pendientes
			if (executionQueue.isEmpty() && !lp.isSimulationEnd()) {
				lp.unlock();
			}
            // Si era el último elemento del sistema
			// MOD 7/3/06 Añadida la 1ª condición para evitar que más de un evento
			// del mismo elemento dispare esta condición.
            if (executionQueue.isEmpty()) {
            	if(lp.getSimul().getElements() == 0) {
	            	lp.print(Output.MessageType.DEBUG, "Execution queue freed",
	            			"TP. MAX:" + tp.getMaxThreads() + "\tINI:" + tp.getInitThreads() 
	            			+ "\tCREATED:" + tp.getNThreads());
	            	tp.finish();
	                lp.getSimul().notifyEnd();
            	}
            }
			return(true);
		}
		return(false);
	}

    /**
     * Inserta un elemento en la cola.
     * @param e Elemento a insertar
     * @return Verdadero (true) si pudo insertarlo
     */
	protected boolean addEvent(BasicElement.Event e) {
		tp.getThread(e);
        return executionQueue.add(e);
	}

    /**
     * Permite saber si un elemento está o no en la cola de ejecución
     * @param e Elemento por el que se pregunta
     * @return Verdadero si el elemento pertenece ya a la cola; falso e.o.c.
     */
    protected boolean inQueue(BasicElement.Event e) {
        return executionQueue.contains(e);
    }
    
    /**
     * Devuelve el número de elementos contenidos en la cola de ejecución
     * @return Tamaño de la cola de ejecución
     */
    protected int size() {
        return executionQueue.size();
    }
    
    /**
     * Devuelve el elemento que ocupa la posición ind en la cola de ejecución.
     * @param ind Indice del elemento que se busca
     * @return El elemento indicado mediante el índice ind
     */
    protected synchronized BasicElement.Event getEvent(int ind) {
        return executionQueue.get(ind);
    }
}
