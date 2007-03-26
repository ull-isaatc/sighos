package es.ull.isaatc.simulation;

import java.util.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.ReentrantLock;

import es.ull.isaatc.simulation.info.TimeChangeInfo;

/** 
 * A logical process (LP) is a subregion of the simulation. It includes a set of resource types,
 * activities, resources, and elements. The LP handles a set of events which interact with any
 * of the components associated to this LP. An LP is subdivided into activity managers.   
 * @author Carlos Martín Galán
 */
public class LogicalProcess extends TimeStampedSimulationObject implements Runnable {
	/** LP's counter. */
	private static int nextId = 0;
    /** A lock for the execution queue. */
	protected ReentrantLock lpLock;
	/** A condition related to <code>lpLock</code>. This condition is used to control
	 * the advance of simulation time */
	protected Condition execEmpty;
    /** Local virtual time. Represents the current simulation time for this LP. */
	protected double lvt;
    /** The maximum timestamp for this logical process. When this timestamp is reached, this LP 
     * finishes its execution. */
    protected double maxgvt; 
    /** Thread pool to execute events */
    protected ExecutorService tp;
	/** A queue containing the events currently executing */
	protected Vector<BasicElement.DiscreteEvent> execQueue;
	/** A timestamp-ordered list of events whose timestamp is in the future. */
	protected PriorityQueue<BasicElement.DiscreteEvent> waitQueue;
    /** The set of activity managers contained by this LP. */
    protected ArrayList<ActivityManager> managerList;
    /** Thread where the logical process function is implemented */
    private Thread lpThread = null;

	/**
     * Creates a logical process with initial timestamp 0.0
     * @param simul Simulation which this LP is attached to.
     * @param endT Finishing timestamp.
     */
	public LogicalProcess(Simulation simul, double endT) {
        this(simul, 0.0, endT);
	}

	/**
     * Creates a logical processwith initial timestamp <code>startT</code> and
     * finishing timestamp <code>endT</code>. 
     * @param simul Simulation which this LP is attached to.
     * @param startT Initial timestamp.
     * @param endT Finishing timestamp.
     */
	public LogicalProcess(Simulation simul, double startT, double endT) {
		super(nextId++, simul);
        tp = Executors.newFixedThreadPool(3);
        execQueue = new Vector<BasicElement.DiscreteEvent>();
        waitQueue = new PriorityQueue<BasicElement.DiscreteEvent>();
        lpLock = new ReentrantLock();
        execEmpty = lpLock.newCondition();
        maxgvt = endT;
        lvt = startT;
        managerList = new ArrayList<ActivityManager>();
	}
    
    /**
     * Indicates if the simulation end has been reached.
     * @return True if the maximum simulation time has been reached. False in other case.
     */
    public boolean isSimulationEnd() {
        return(lvt >= maxgvt);
    }

    @Override
	public double getTs() {
		return lvt;
	}

    /**
     * Sends an event to the execution queue by looking for a thread to execute it. An event is 
     * added to the execution queue when the LP has reached the event timestamp. 
     * @param e Event to be executed
     * @return True if the event was succesfully added. False in other case.
     */
	public boolean addExecution(BasicElement.DiscreteEvent e) {
		lpLock.lock();
		boolean res;
		try {
			res = execQueue.add(e);
			tp.execute(e);
		}
		finally {
			lpLock.unlock();
		}
        return res;
	}

    /**
     * Removes an event from the execution queue, but performing a previous synchronization.
     * The synchronization consists on waiting for the LP to lock, or for the simulation end.
     * @param e Event to be removed
     * @return True if the event was succesfully removed. False in other case.
     */
    protected boolean removeExecution(BasicElement.DiscreteEvent e) {
        boolean res = false;
        lpLock.lock();
        try {
			if (execQueue.remove(e)) { // pudo quitarse
				// The LP is informed of the finalization of an event. There's no need of 
				// checking if the exec. queue is empty because the main loop is already doing that.
				// FIXME: Check if the execution queue is empty is an improvement?
				execEmpty.signal();
				res = true;
			}
        }
		finally {
			lpLock.unlock();
		}
		return res;
    }
    
    /**
     * Sends an event to the waiting queue. An event is added to the waiting queue if 
     * its timestamp is greater than the LP timestamp.
     * @param e Event to be added
     */
	public synchronized void addWait(BasicElement.DiscreteEvent e) {
		waitQueue.add(e);
	}

    /**
     * Removes an event from the waiting queue. An event is removed from the waiting 
     * queue when the LP reaches the timestamp of that event.
     * @return The first event of the waiting queue.
     */
    protected synchronized BasicElement.DiscreteEvent removeWait() {
        return waitQueue.poll();
    }

    /**
     * Adds an activity manager to the manager list
     * @param am New Activity manager.
     */
    protected void add(ActivityManager am) {
        managerList.add(am);
    }
    
    /**
     * Executes a simulation clock cycle. Extracts all the events from the waiting queue with 
     * timestamp equal to the LP timestamp. 
     */
    private void execWaitingElements() {
        // Extracts the first event
        if (! waitQueue.isEmpty()) {
            BasicElement.DiscreteEvent e = removeWait();
            // Advances the simulation clock
            lvt = e.getTs();
            simul.notifyListeners(new TimeChangeInfo(this));
            debug("SIMULATION TIME ADVANCING " + lvt);
            // Events with timestamp greater or equal to the maximum simulation time aren't
            // executed
            if (lvt >= maxgvt)
                addWait(e);
            else {
                addExecution(e);
                // Extracts all the events with the same timestamp
                boolean flag = false;
                do {
                    if (! waitQueue.isEmpty()) {
                        e = removeWait();
                        if (e.getTs() == lvt) {
                            addExecution(e);
                            flag = true;
                        }
                        else {  
                            flag = false;
                            addWait(e);
                        }
                    }
                    else {  // The waiting queue is empty
                        flag = false;
                    }
                } while ( flag );
            }
        }        
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
			addEvent(new DummyEvent(maxgvt));
		}
		
		@Override
		protected void end() {
		}

    	class DummyEvent extends BasicElement.DiscreteEvent {
    		DummyEvent(double ts) {
    			super(ts, LogicalProcess.this);
    		}
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
		while (!isSimulationEnd()) {
			// Every time the loop is entered we must wait for all the events from the 
			// previous iteration to be finished (the execution queue must be empty)
			lpLock.lock();
			try {
				// The LP waits for all the events to be removed from the execution queue 
				while (!execQueue.isEmpty())
					execEmpty.await();
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			finally {
				lpLock.unlock();
			}
			// Now the simulation clock can advance
            execWaitingElements();
		}
		// We must wait for all the event to be finished
		lpLock.lock();
		try {
			// The LP waits for all the events to be removed from the execution queue 
			while (!execQueue.isEmpty())
				execEmpty.await();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		finally {
			lpLock.unlock();
		}
		// Frees the execution queue
    	tp.shutdown();    	
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
	        strLong.append("\r\n" + execQueue.size() + " executing elements:");
	        for (BasicElement.DiscreteEvent e : execQueue)
	            strLong.append(e + " ");
	        strLong.append("\r\n");
	        for (ActivityManager am : managerList)
	        	strLong.append("Activity Manager " + am.getIdentifier() + "\r\n");
	        strLong.append("\r\n------ LP STATE FINISHED ------\r\n");
			debug(strLong.toString());
		}
	}
}