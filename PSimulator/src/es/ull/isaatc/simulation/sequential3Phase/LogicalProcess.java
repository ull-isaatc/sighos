package es.ull.isaatc.simulation.sequential3Phase;

import java.util.PriorityQueue;

/** 
 * A logical process (LP) is a subregion of the simulation. It includes a set of resource types,
 * activities, resources, and elements. The LP handles a set of events which interact with any
 * of the components associated to this LP. An LP is subdivided into activity managers.   
 * @author Carlos Martín Galán
 */
public abstract class LogicalProcess extends TimeStampedSimulationObject implements Runnable {
	/** LP's counter. */
	private static int nextId = 0;
    /** Local virtual time. Represents the current simulation time for this LP. */
	protected long lvt;
    /** The maximum timestamp for this logical process. When this timestamp is reached, this LP 
     * finishes its execution. */
    protected long maxgvt; 
	/** A timestamp-ordered list of events whose timestamp is in the future. */
	protected final PriorityQueue<BasicElement.DiscreteEvent> waitQueue;
    /** Thread where the logical process function is implemented */
    private Thread lpThread = null;

	/**
     * Creates a logical process with initial timestamp 0.0
     * @param simul Simulation which this LP is attached to.
     * @param endT Finishing timestamp.
     */
	public LogicalProcess(Simulation simul, long endT) {
        this(simul, 0, endT);
	}

	/**
     * Creates a logical processwith initial timestamp <code>startT</code> and
     * finishing timestamp <code>endT</code>. 
     * @param simul Simulation which this LP is attached to.
     * @param startT Initial timestamp.
     * @param endT Finishing timestamp.
     */
	public LogicalProcess(Simulation simul, long startT, long endT) {
		super(nextId++, simul);
        waitQueue = new PriorityQueue<BasicElement.DiscreteEvent>();
        maxgvt = endT;
        lvt = startT;
	}
    
    /**
     * Indicates if the simulation end has been reached.
     * @return True if the maximum simulation time has been reached. False in other case.
     */
    public boolean isSimulationEnd() {
        return(lvt >= maxgvt);
    }

    @Override
	public long getTs() {
		return lvt;
	}

    /**
     * Sends an event to the waiting queue. An event is added to the waiting queue if 
     * its timestamp is greater than the LP timestamp.
     * @param e Event to be added
     */
	public abstract void addWait(BasicElement.DiscreteEvent e);
	
	public abstract void addExecution(BasicElement.DiscreteEvent e);
	
    /**
     * Removes an event from the waiting queue. An event is removed from the waiting 
     * queue when the LP reaches the timestamp of that event.
     * @return The first event of the waiting queue.
     */
    protected abstract BasicElement.DiscreteEvent removeWait();
    
    /**
     * Executes a simulation clock cycle. Extracts all the events from the waiting queue with 
     * timestamp equal to the LP timestamp. 
     */
    protected abstract void execWaitingElements();
    
	/**
	 * A basic element which facilitates the end-of-simulation control. It simply
	 * has an event at <code>maxgvt</code>, so there's always at least one event in 
	 * the LP. 
	 * @author Iván Castilla Rodríguez
	 */
    class SafeLPElement extends BasicElement {

		public SafeLPElement() {
			super(0, LogicalProcess.this.simul);
		}

		@Override
		protected void init() {
			addEvent(new DummyEvent(maxgvt));
		}
		
		@Override
		protected void end() {
		}

    	class DummyEvent extends BasicElement.DiscreteEvent {
    		DummyEvent(long ts) {
    			super(ts, LogicalProcess.this);
    		}
			@Override
			public void event() {				
			}    		
    	}
    }
    
    /**
     * Controls the LP execution. First, a <code>SafeLPElement</code> is added. The execution loop 
     * consists on waiting for the elements which are in execution, then the simulation clock is 
     * advanced and a new set of events is executed. 
     */
	public void run() {
        new SafeLPElement().start(this);
		while (!isSimulationEnd())
            execWaitingElements();
		// Frees the execution queue
    	debug("SIMULATION TIME FINISHES\r\nSimulation time = " +
            	lvt + "\r\nPreviewed simulation time = " + maxgvt);
    	printState();
    	// Notifies the simulation about the end of this logical process
        simul.notifyEnd();
	}

	/**
	 * Starts the thread that handles the LP execution.
	 */
	public void start() {
		if (lpThread == null) {
	        lpThread = new Thread(this, "LP " + id);
	        // Esto mejora el tiempo de este hilo, pero no el resultado final
//	        lpThread.setPriority(Thread.MAX_PRIORITY);
	        lpThread.start();
		}
	}

	@Override
	public String getObjectTypeIdentifier() {
		return "LP";
	}

	/**
	 * Does a debug print of this LP. Prints the current local time, the contents of
	 * the waiting and execution queues, and the contents of the activity managers. 
	 */
	protected void printState() {
		if (isDebugEnabled()) {
			StringBuffer strLong = new StringBuffer("------    LP STATE    ------");
			strLong.append("LVT: " + lvt + "\r\n");
	        strLong.append(waitQueue.size() + " waiting elements: ");
	        for (BasicElement.DiscreteEvent e : waitQueue)
	            strLong.append(e + " ");
	        strLong.append("\r\n");
	        strLong.append("\r\n------ LP STATE FINISHED ------\r\n");
			debug(strLong.toString());
		}
	}
	
}