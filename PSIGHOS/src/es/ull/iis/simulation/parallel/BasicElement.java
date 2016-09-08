package es.ull.iis.simulation.parallel;

import java.util.concurrent.atomic.AtomicBoolean;

/**
 * Represents the simulation component that carries out events. 
 * TODO Comment
 * @author Carlos Martin Galan
 */
public abstract class BasicElement extends TimeStampedSimulationObject {
    /** Current element's timestamp */
	protected long ts;
    /** Access control */
    final private AtomicBoolean sem = new AtomicBoolean(false);
    /** Flag that indicates if the element has finished its execution */
    final private AtomicBoolean endFlag = new AtomicBoolean(false);

    /**
     * Creates a basic element. 
     * @param id Element's identifier
     * @param simul Attached simulation object
     */
	public BasicElement(int id, Simulation simul) {
		super(id, simul);
	}

    /**
     * Starts the element execution.
     * @param ts The time stamp when the starting event of this element is scheduled
     */    
    public StartEvent getStartEvent(long ts) {
		this.ts = simul.getTs();
        return new StartEvent(ts);
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
   		((EventExecutor)Thread.currentThread()).addEvent(e);
    }

    /**
     * Informs the element that it must finish its execution. Thus, a FinalizeEvent is
     * created.
     */
    protected void notifyEnd() {
    	if (!endFlag.getAndSet(true)) {
        	debug("Finished");
            addEvent(new FinalizeEvent());
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
     * Sends a "wait" signal to the semaphore.
     */    
    protected void waitSemaphore() {
    	while (!sem.compareAndSet(false, true));
    }
    
    /**
     * Sends a "continue" signal to the semaphore.
     */    
    protected void signalSemaphore() {
        sem.set(false);
    }
    
	public String getObjectTypeIdentifier() {
		return "BE";
	}
	
    /**
     * Element's basic event. Performs a task and then it's removed from the
     * execution queue.
     * @author Iv�n Castilla Rodr�guez
     */
    public abstract class DiscreteEvent implements Runnable, Comparable<DiscreteEvent> {
        /** Timestamp when this event will be executed */
        final protected long ts;

        /**
         * Creates a new basic event.
         * @param ts Timestamp when the event will be executed.
         */
        public DiscreteEvent(long ts) {
            this.ts = ts;
        }
        
        /**
         * Performs a task and then it removes this event from the execution 
         * queue. It also updates the element's timestamp.
         */        
        public void run() {
            BasicElement.this.setTs(ts);
            event();
        }
        
        /**
         * The action/task that this event carries out.
         */
        public abstract void event();
        
        /**
         * Cancels this event. This function only works when the event is currently scheduled in the 
         * simulation.
         * @return True if the event can be cancelled; false otherwise. 
         */
        public boolean cancel() {
        	return simul.removeWait(this);
        }
        
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
     * The last event this element executes. It decrements the total amount of elements of the
     * simulation.
     * @author Iv�n Castilla Rodr�guez
     */
    public final class FinalizeEvent extends DiscreteEvent {
        
        public FinalizeEvent() {
            super(simul.getTs());
        }
        
        public void event() {
        	debug("Ends execution");
        	BasicElement.this.end();
        }
    }

    /**
     * The first event this element executes.
     * @author Iv�n Castilla Rodr�guez
     *
     */
    public final class StartEvent extends DiscreteEvent {
        public StartEvent(long ts) {
            super(ts);
        }

        public void event() {
        	debug("Starts Execution");
            BasicElement.this.init();
        }
    }
    
}