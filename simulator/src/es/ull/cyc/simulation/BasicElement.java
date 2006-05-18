package es.ull.cyc.simulation;

import mjr.heap.Heapable;
import es.ull.cyc.sync.*;
import es.ull.cyc.util.*;

import java.util.*;

/**
 * Represents the simulation component that carries out events. 
 * MOD 14/01/05 Declarada abstract
 * MOD POOL 10/06/05: Esta clase ha sido modificada y ya no será un thread
 * @author Carlos Martin Galan
 */
public abstract class BasicElement extends SimulationObject {
    /** Current element's timestamp */
	protected double ts;
    /** List that contains all the events carried out by the element */
    protected ArrayList eventList;
    /** Access control */
    protected Semaphore sem;
    /** Flag that indicates if the simulation end has been reached */
    protected boolean endSimulationFlag = false;
    /** Flag that indicates if the element has finished its execution */
    protected boolean endFlag = false;
    /** Default logical process */
    protected LogicalProcess defLP = null;

    /**
     * Creates a basic element. 
     * @param id Element's identifier
     * @param simul Attached simulation object
     */
	public BasicElement(int id, Simulation simul) {
		this(id, simul, simul.getDefaultLogicalProcess());
	}

    /**
     * Creates a basic element. 
     * @param id Element's identifier
     * @param simul Attached simulation object
     * @param defLP Default Logical Process which this element is attached to.
     */
	public BasicElement(int id, Simulation simul, LogicalProcess defLP) {
		super(id, simul);
        eventList = new ArrayList();
		ts = defLP.getTs();
        sem = new Semaphore(1);
        this.defLP = defLP;
	}
    
    /**
     * Starts the element execution.
     */    
    public void start() {
    	print(Output.DEBUGMSG, "Starts Execution");
        simul.incElements();
        addEvent(new StartEvent());
    }
    
    /**
     * Contains the declaration of the first event/s that the element executes.
     */
    protected abstract void startEvents();
    
    /**
     * Adds a new event to the element's event list
     * @param e New event.
     */    
    protected void addEvent(Event e) {
        eventList.add(e);
        if (e.getTs() == e.getLp().getTs())
            e.getLp().addExecution(e);
        else if (e.getTs() > e.getLp().getTs())
            e.getLp().addWait(e);
        else
        	print(Output.ERRORMSG, "Causal restriction broken\t" + e.getLp().getTs(),
        			"Causal restriction broken\t" + e.getLp().getTs() + "\t" + e);
    }
    
    protected synchronized void notifyEnd() {
    	if (!endFlag) {
    		endFlag = true;
        	print(Output.DEBUGMSG, "Finished in notifyEnd()");
            addEvent(new FinalizeEvent());
        }
    }
    
    /**
     * Informs the element an "end of simulation" has happened. The element carries 
     * out an "end" event.
     */    
    protected synchronized void notifyEndSimulation() {
        if (!endSimulationFlag) {
            endSimulationFlag = true;
            notifyEnd();
        }
    }
    
    /**
     * Finishes the element execution.
     */    
    protected void end() {
    	print(Output.DEBUGMSG, "Ends execution");
        simul.decElements();
    }

    /**
     * Saves the state of the element when the end of the simulation is reached
     * and the element hasn't finished its execution.
     */
    protected abstract void saveState();
    
    /**
     * Returns the element's current timestamp.
     * @return Value of property ts.
     */
    public double getTs() {
        return ts;
    }
    
    /**
     * Establish a new element's timestamp. The new timestamp must be greater or 
     * equal than the previous one.
     * @param ts New value of property ts.
     */
    public void setTs(double ts) {
        if (ts >= this.ts)
            this.ts = ts;
        else
        	print(Output.WARNINGMSG, "Trying to go back in time\t" + ts);
    }

    public es.ull.cyc.simulation.LogicalProcess getDefLP() {
        return defLP;
    }
    
    /**
     * Sends a "wait" signal to the semaphore.
     */    
    protected void waitSemaphore() {
        sem.waitSemaphore();
    }
    
    /**
     * Sends a "continue" signal to the semaphore.
     */    
    protected void signalSemaphore() {
        sem.signalSemaphore();
    }
    
	public String getObjectTypeIdentifier() {
		return "BE";
	}
	
    /**
     * Element's basic event. Performs a task and then it's removed from the
     * execution queue.
     * @author Iván Castilla Rodríguez
     */
    public abstract class Event extends es.ull.cyc.sync.Event implements Heapable {
        /** Timestamp when this event will be executed */
        protected double ts;        
        /** Logical process */
        protected LogicalProcess lp = null;

        /**
         * Creates a new basic event.
         * @param ts Timestamp when the event will be executed.
         * @param lp Logical process where the event will be executed.
         */
        public Event(double ts, LogicalProcess lp) {
            this.ts = ts;
            this.lp = lp;
        }
        /**
         * Performs a task and then it removes this event from the execution 
         * queue. It also updates the element's timestamp.
         */        
        protected void run() {
            // MOD 9/01/06 Para actualizar el tiempo del elemento
            BasicElement.this.setTs(ts);
            super.run();
            if (!lp.removeExecutionSync(this))
            	print(Output.ERRORMSG, "Can not be removed from execution queue",
            			"Can not be removed from execution queue\t" + this.toString());
        }
        
        /**
         * The action/task that this event carries out.
         */
        public abstract void event();
        
        /**
         * String representation of the event
         * @return A character string "E[#]".
         */
        public String toString() {
            return "Ev(" + getClass().getName() + ")" + BasicElement.this.toString();
        }
        
        /**
         * Returns the element which this event is attached to.
         * @return The attached basic element.
         */
        public BasicElement getElement() {
            return BasicElement.this;
        }
        
        /**
         * Getter for property ts.
         * @return Value of property ts.
         */
        public double getTs() {
            return ts;
        }
        
        /**
         * Getter for property lp.
         * @return Value of property lp.
         */
        public es.ull.cyc.simulation.LogicalProcess getLp() {
            return lp;
        }
        
        /**
         * Checks if this event is greater than other.
         * @param other Compared event.
         * @return True if the current timestamp is greater to the timestamp of
         * the event passed by parameters. False in other case.
         */    
        public boolean greaterThan(Object other) {
            return (ts > ((Event)other).getTs());
        }

        /**
         * Checks if this event is less than other.
         * @param other Compared event.
         * @return True if the current timestamp is less to the timestamp of
         * the event passed by parameters. False in other case.
         */    
        public boolean lessThan(Object other) {
            return (ts < ((Event)other).getTs());
        }

        /**
         * Checks if two events are equals.
         * @param other Compared event.
         * @return True if the current timestamp is equal to the timestamp of
         * the event passed by parameters. False in other case.
         */    
        public boolean equalTo(Object other) {
            return(ts == ((Event)other).getTs());
        }
        
    }

    /**
     * Finaliza la ejecución del elemento, decrementando el contador de elementos 
     * del lp.
     * @author Iván Castilla Rodríguez
     */
    public class FinalizeEvent extends Event {
        
        public FinalizeEvent() {
            super(defLP.getTs(), defLP);
        }
        
        /**
         * Termina la ejecución del elemento.
         */        
        public void event() {
            if (endSimulationFlag)
                saveState();
            end();
        }
    }
    
    public class StartEvent extends Event {
        public StartEvent() {
            super(defLP.getTs(), defLP);
        }

        public void event() {
            startEvents();
        }
    }
    
}
