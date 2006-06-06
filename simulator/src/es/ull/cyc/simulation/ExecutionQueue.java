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
 * Controla la cola de ejecuci�n del proceso l�gico. Todos los elementos que 
 * tienen que realizar alguna acci�n se van a a�adiendo a una cola de ejecuci�n. 
 * Desde esta cola se les busca un thread libre en el pool donde poder ejecutar 
 * esta acci�n.
 * @author Iv�n Castilla Rodr�guez
 */
public class ExecutionQueue {
    /** Proceso l�gico al que est� asociado este controlador */
    protected LogicalProcess lp;
    /** Pool de elementos en ejecuci�n */
    protected ThreadPool tp;
	/** Cola de elementos de sim. que en este momento estan en ejecucion */
	protected Vector<BasicElement.Event> executionQueue;
    
    /** 
     * Crea un nuevo ControladorElementos.
     * @param pl Proceso l�gico al que est� asociado.
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
			// si era el �ltimo tiene que notificarlo, se comprueba que ademas no hay operaciones pendientes
			if (executionQueue.isEmpty() && !lp.isSimulationEnd()) {
				lp.unlock();
			}
            // Si era el �ltimo elemento del sistema
			// MOD 7/3/06 A�adida la 1� condici�n para evitar que m�s de un evento
			// del mismo elemento dispare esta condici�n.
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
     * Permite saber si un elemento est� o no en la cola de ejecuci�n
     * @param e Elemento por el que se pregunta
     * @return Verdadero si el elemento pertenece ya a la cola; falso e.o.c.
     */
    protected boolean inQueue(BasicElement.Event e) {
        return executionQueue.contains(e);
    }
    
    /**
     * Devuelve el n�mero de elementos contenidos en la cola de ejecuci�n
     * @return Tama�o de la cola de ejecuci�n
     */
    protected int size() {
        return executionQueue.size();
    }
    
    /**
     * Devuelve el elemento que ocupa la posici�n ind en la cola de ejecuci�n.
     * @param ind Indice del elemento que se busca
     * @return El elemento indicado mediante el �ndice ind
     */
    protected synchronized BasicElement.Event getEvent(int ind) {
        return executionQueue.get(ind);
    }
}
