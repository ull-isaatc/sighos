package es.ull.isaatc.simulation;

import es.ull.isaatc.simulation.BasicElement.DiscreteEvent;



/** 
 * A logical process (LP) is a subregion of the simulation. It includes a set of resource types,
 * activities, resources, and elements. The LP handles a set of events which interact with any
 * of the components associated to this LP. An LP is subdivided into activity managers.   
 * @author Carlos Martín Galán
 */
public abstract class LogicalProcess extends TimeStampedSimulationObject {
	/** LP's counter. */
	private static int nextId = 0;
    /** Local virtual time. Represents the current simulation time for this LP. */
	protected long lvt;
    /** The maximum timestamp for this logical process. When this timestamp is reached, this LP 
     * finishes its execution. */
    protected long maxgvt; 

	/**
     * Creates a logical processwith initial timestamp <code>startT</code> and
     * finishing timestamp <code>endT</code>. 
     * @param simul Simulation which this LP is attached to.
     * @param startT Initial timestamp.
     * @param endT Finishing timestamp.
     */
	public LogicalProcess(Simulation simul, long startT, long endT) {
		super(nextId++, simul);
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
     * Sends an event to the execution queue by looking for a thread to execute it. An event is 
     * added to the execution queue when the LP has reached the event timestamp. 
     * @param e Event to be executed
     */
	public abstract void addExecution(BasicElement.DiscreteEvent e);

    /**
     * Removes an event from the execution queue, but performing a previous synchronization.
     * The synchronization consists on waiting for the LP to lock, or for the simulation end.
     * @param e Event to be removed
     */
    protected abstract void removeExecution(BasicElement.DiscreteEvent e);
    
    /**
     * Sends an event to the waiting queue. An event is added to the waiting queue if 
     * its timestamp is greater than the LP timestamp.
     * @param e Event to be added
     */
	public abstract void addWait(BasicElement.DiscreteEvent e);

    /**
     * Communicates a new event to the logical process. 
     * @param e New event.
     */    
    public void addEvent(DiscreteEvent e) {
    	long evTs = e.getTs();
        if (evTs == lvt)
            addExecution(e);
        else if (evTs > lvt)
            addWait(e);
        else
        	error("Causal restriction broken\t" + lvt + "\t" + e);
    }

    
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
		}
		
		@Override
		protected void end() {
		}
    }
    
    /**
     * Controls the LP execution. First, a <code>SafeLPElement</code> is added. The execution loop 
     * consists on waiting for the elements which are in execution, then the simulation clock is 
     * advanced and a new set of events is executed. 
     */
	public abstract void run();

	@Override
	public String getObjectTypeIdentifier() {
		return "LP";
	}

	/**
	 * Does a debug print of this LP. Prints the current local time, the contents of
	 * the waiting and execution queues, and the contents of the activity managers. 
	 */
	protected abstract void printState();
	
}