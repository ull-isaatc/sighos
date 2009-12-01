package es.ull.isaatc.simulation.optThreaded;

import java.util.concurrent.PriorityBlockingQueue;
import java.util.concurrent.atomic.AtomicInteger;

import es.ull.isaatc.simulation.common.info.TimeChangeInfo;
//import es.ull.isaatc.util.ActiveThreadPool;
import es.ull.isaatc.util.SingleThreadPool;
import es.ull.isaatc.util.StandardThreadPool;
import es.ull.isaatc.util.ThreadPool;

/** 
 * A logical process (LP) is a subregion of the simulation. It includes a set of resource types,
 * activities, resources, and elements. The LP handles a set of events which interact with any
 * of the components associated to this LP. An LP is subdivided into activity managers.   
 * @author Carlos Mart�n Gal�n
 */
public class LogicalProcess extends TimeStampedSimulationObject implements Runnable {
	/** LP's counter. */
	private static int nextId = 0;
    /** Local virtual time. Represents the current simulation time for this LP. */
	protected long lvt;
    /** The maximum timestamp for this logical process. When this timestamp is reached, this LP 
     * finishes its execution. */
    protected long maxgvt; 
    /** Thread pool to execute events */
    protected final ThreadPool<BasicElement.DiscreteEvent> tp;
    /** A counter to know how many events are in execution */
    protected AtomicInteger executingEvents = new AtomicInteger(0);
	/** A timestamp-ordered list of events whose timestamp is in the future. */
	protected final PriorityBlockingQueue<BasicElement.DiscreteEvent> waitQueue;
    /** Thread where the logical process function is implemented */
    private Thread lpThread = null;
	/** List of activity managers that partition the simulation. */
	protected final ActivityManager[] activityManagerList;

	/**
     * Creates a logical processwith initial timestamp <code>startT</code> and
     * finishing timestamp <code>endT</code>. 
     * @param simul Simulation which this LP is attached to.
     * @param startT Initial timestamp.
     * @param endT Finishing timestamp.
     */
	public LogicalProcess(Simulation simul, long startT, long endT, ActivityManager[] activityManagerList) {
        this(simul, startT, endT, 1, activityManagerList);
	}

	/**
     * Creates a logical process with initial timestamp <code>startT</code> and
     * finishing timestamp <code>endT</code>. 
     * @param simul Simulation which this LP is attached to.
     * @param startT Initial timestamp.
     * @param endT Finishing timestamp.
     */
	public LogicalProcess(Simulation simul, long startT, long endT, int nThreads, ActivityManager[] activityManagerList) {
		super(nextId++, simul);
		if (nThreads == 1)
			tp = new SingleThreadPool<BasicElement.DiscreteEvent>();
		else
			tp = new StandardThreadPool<BasicElement.DiscreteEvent>(nThreads);
//		tp = new ActiveThreadPool2<BasicElement.DiscreteEvent>(nThreads);
        waitQueue = new PriorityBlockingQueue<BasicElement.DiscreteEvent>();
        maxgvt = endT;
        lvt = startT;
        this.activityManagerList = activityManagerList;
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
	public void addExecution(BasicElement.DiscreteEvent e) {
		executingEvents.incrementAndGet();
		tp.execute(e);
	}

    /**
     * Removes an event from the execution queue, but performing a previous synchronization.
     * The synchronization consists on waiting for the LP to lock, or for the simulation end.
     * @param e Event to be removed
     */
    protected void removeExecution(BasicElement.DiscreteEvent e) {
		// The LP is informed of the finalization of an event. There's no need of 
		// checking if the exec. queue is empty because the main loop is already doing that.
		executingEvents.decrementAndGet();
    }
    
    /**
     * Sends an event to the waiting queue. An event is added to the waiting queue if 
     * its timestamp is greater than the LP timestamp.
     * @param e Event to be added
     */
	public void addWait(BasicElement.DiscreteEvent e) {
		waitQueue.add(e);
	}

    /**
     * Removes an event from the waiting queue. An event is removed from the waiting 
     * queue when the LP reaches the timestamp of that event.
     * @return The first event of the waiting queue.
     */
    protected BasicElement.DiscreteEvent removeWait() {
        return waitQueue.poll();
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
            simul.beforeClockTick();
            lvt = e.getTs();
            simul.getInfoHandler().notifyInfo(new TimeChangeInfo(simul, this.getTs()));
            simul.afterClockTick();
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
	 * @author Iv�n Castilla Rodr�guez
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
			while (executingEvents.get() > 0);
			// Now the simulation clock can advance
			for (ActivityManager am : activityManagerList)
				am.executeWork();
            // Each execution is divided into two steps
			while (executingEvents.get() > 0);
			// Now the AM events are executed
            execWaitingElements();
		}
		// We must wait for all the event to be finished
		while (executingEvents.get() > 0);
		// Now the simulation clock can advance
		for (ActivityManager am : activityManagerList)
			am.executeWork();
        // Each execution is divided into two steps
		while (executingEvents.get() > 0);
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
	        strLong.append("\r\n" + executingEvents + " executing elements:");
	        strLong.append("\r\n");
	        strLong.append("\r\n------ LP STATE FINISHED ------\r\n");
			debug(strLong.toString());
		}
	}
	
}