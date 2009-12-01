package es.ull.isaatc.simulation.optThreaded;

import java.util.concurrent.Semaphore;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * Represents the simulation component that carries out events. 
 * @author Carlos Martin Galan
 */
public abstract class BasicElement extends TimeStampedSimulationObject {
    /** Current element's timestamp */
	protected long ts;
    /** Access control */
    final protected Semaphore sem;
    /** Flag that indicates if the element has finished its execution */
    final protected AtomicBoolean endFlag = new AtomicBoolean(false);
    /** Default logical process */
    protected LogicalProcess defLP = null;

    /**
     * Creates a basic element. 
     * @param id Element's identifier
     * @param simul Attached simulation object
     */
	public BasicElement(int id, Simulation simul) {
		super(id, simul);
        sem = new Semaphore(1);
	}

    /**
     * Starts the element execution.
     * @param defLP Logical process this element is attached to
     */    
    public void start(LogicalProcess defLP) {
    	this.defLP = defLP;
		ts = defLP.getTs();
        addEvent(new StartEvent());
    }
    
    /**
     * Contains the declaration of the first event/s that the element executes.
     */
    protected abstract void init();
    
    /**
     * Contains the actions the element carried out when it finishes its execution. 
     */
    protected abstract void end();
    
    /**
     * Communicates a new event to the logical process. 
     * @param e New event.
     */    
    protected void addEvent(DiscreteEvent e) {
    	long evTs = e.getTs();
    	long lpTs = e.getLp().getTs();
        if (evTs == lpTs)
            e.getLp().addExecution(e);
        else if (evTs > lpTs)
            e.getLp().addWait(e);
        else
        	error("Causal restriction broken\t" + lpTs + "\t" + e);
    }

    /**
     * Informs the element that it must finish its execution. Thus, a FinalizeEvent is
     * created.
     */
    protected void notifyEnd() {
    	if (!endFlag.getAndSet(true)) {
        	debug("Ends execution");
        	end();
        }
    }
    
    /**
     * Returns the element's current timestamp.
     * @return Value of property ts.
     */
    public long getTs() {
        return ts;
    }
    
    /**
     * Establishes a new element's timestamp. The new timestamp must be greater or 
     * equal than the previous one.
     * @param ts New value of property ts.
     */
    public void setTs(long ts) {
        if (ts >= this.ts)
            this.ts = ts;
        else
        	error("Trying to go back in time\t" + ts);
    }

    /**
     * Returns the default logical process.
     * @return The logical process associated to this element.
     */
    public LogicalProcess getDefLP() {
        return defLP;
    }
    
    /**
     * Associates this element to a default logical process.
     * @param lp Logical process
     */
    public void setDefLP(LogicalProcess lp) {
    	this.defLP = lp;
    }
    
    /**
     * Sends a "wait" signal to the semaphore.
     */    
    protected void waitSemaphore() {
        try {
			sem.acquire();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
    }
    
    /**
     * Sends a "continue" signal to the semaphore.
     */    
    protected void signalSemaphore() {
        sem.release();
    }
    
	public String getObjectTypeIdentifier() {
		return "BE";
	}
	
    /**
     * Element's basic event. Performs a task and then it's removed from the
     * execution queue.
     * @author Iván Castilla Rodríguez
     */
    public abstract class DiscreteEvent implements Runnable, Comparable<DiscreteEvent> {
        /** Timestamp when this event will be executed */
        final protected long ts;        
        /** Logical process */
        final protected LogicalProcess lp;

        /**
         * Creates a new basic event.
         * @param ts Timestamp when the event will be executed.
         * @param lp Logical process where the event will be executed.
         */
        public DiscreteEvent(long ts, LogicalProcess lp) {
            this.ts = ts;
            this.lp = lp;
        }
        
        /**
         * Performs a task and then it removes this event from the execution 
         * queue. It also updates the element's timestamp.
         */        
        public void run() {
            BasicElement.this.setTs(ts);
            event();
            lp.removeExecution(this);
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
            return "Ev(" + getClass().getName() + ")[" + ts + "]" + BasicElement.this.toString();
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
        public long getTs() {
            return ts;
        }
        
        /**
         * Getter for property lp.
         * @return Value of property lp.
         */
        public es.ull.isaatc.simulation.optThreaded.LogicalProcess getLp() {
            return lp;
        }
        
        /**
         * Checks if this event is greater than, less than, or equal to other.
         * @param e Compared event.
         * @return 1, -1 or 0 if the current timestamp is greater than, less than, or equal to
         * the timestamp of the event passed by parameters.
         */    
    	public int compareTo(DiscreteEvent e) {
    		long evTs = e.getTs();
    		if (ts > evTs)
    			return 1;
    		else if (ts < evTs)
    			return -1;
    		return 0;
    	}
    }

    /**
     * The first event this element executes.
     * @author Iván Castilla Rodríguez
     *
     */
    public class StartEvent extends DiscreteEvent {
        public StartEvent() {
            super(BasicElement.this.defLP.getTs(), BasicElement.this.defLP);
        }

        public void event() {
        	debug("Starts Execution");
            BasicElement.this.init();
        }
    }
    
}
