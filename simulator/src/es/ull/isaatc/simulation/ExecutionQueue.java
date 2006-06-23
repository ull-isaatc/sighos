/*
 * ControladorEjecucion.java
 *
 * Created on 13 de junio de 2005, 19:39
 */

package es.ull.isaatc.simulation;

import java.util.Vector;

import es.ull.isaatc.sync.*;
import es.ull.isaatc.util.*;

/**
 * Manages the execution queue of a logical process. This class uses a thread pool. 
 * Thus, each event is added to an execution queue and a thread is requested from the
 * pool to execute the event. 
 * @author Iván Castilla Rodríguez
 */
public class ExecutionQueue {
    /** Logical process whose execution queue is managed */
    protected LogicalProcess lp;
    /** Thread pool to execute events */
    protected ThreadPool tp;
	/** A queue containing the events currently executing */
	protected Vector<BasicElement.Event> executionQueue;
    
    /** 
     * Creates a new execution queue.
     * @param lp Logical process.
     */
    public ExecutionQueue(LogicalProcess lp) {
        this.lp = lp;
        tp = new ThreadPool(3, 3);
        executionQueue = new Vector<BasicElement.Event>();
    }
    
    /**
     * Removes an event from the queue. When the last event is removed, the logical
     * process is informed.
     * @param e Event to be removed
     * @return True if the event was removed correctly. Fasle in other case.
     */
	protected synchronized boolean removeEvent(BasicElement.Event e) {
		if (executionQueue.remove(e)) { // pudo quitarse
            // Si era el último elemento del sistema
			// MOD 7/3/06 Añadida la 1ª condición para evitar que más de un evento
			// del mismo elemento dispare esta condición.
            if (executionQueue.isEmpty()) {
    			// si era el último tiene que notificarlo
    			if (!lp.isSimulationEnd()) 
    				lp.unlock();
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
     * Inserts a new event in the queue and looks for a thread to execute it.
     * @param e Event to be added
     * @return True if the event was added correctly. False in other case. 
     */
	protected boolean addEvent(BasicElement.Event e) {
		tp.getThread(e);
        return executionQueue.add(e);
	}

    /**
     * Returns the total amount of events in the queue.
     * @return Total events in the queue.
     */
    protected int size() {
        return executionQueue.size();
    }
    
    /**
     * Returns the event at position ind.
     * @param ind Position of the event.
     * @return A specific event.
     */
    protected synchronized BasicElement.Event getEvent(int ind) {
        return executionQueue.get(ind);
    }
}
