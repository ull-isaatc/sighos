package es.ull.isaatc.simulation;

import java.util.concurrent.PriorityBlockingQueue;

import es.ull.isaatc.simulation.info.TimeChangeInfo;
import es.ull.isaatc.util.StandardThreadPool;
import es.ull.isaatc.util.ThreadPool;

/** 
 * A logical process (LP) is a subregion of the simulation. It includes a set of resource types,
 * activities, resources, and elements. The LP handles a set of events which interact with any
 * of the components associated to this LP. An LP is subdivided into activity managers.   
 * @author Carlos Martín Galán
 */
public class BarrierLogicalProcess extends LogicalProcess {
    /** Thread pool to execute events */
    protected final ThreadPool<BasicElement.DiscreteEvent> tp;
	/** A timestamp-ordered list of events whose timestamp is in the future. */
	protected final PriorityBlockingQueue<BasicElement.DiscreteEvent> waitQueue;
    private DynamicBarrier barrier;

	/**
     * Creates a logical process with initial timestamp 0.0
     * @param simul Simulation which this LP is attached to.
     * @param endT Finishing timestamp.
     */
	public BarrierLogicalProcess(Simulation simul, long endT) {
        this(simul, 0, endT, 1);
	}

	/**
     * Creates a logical processwith initial timestamp <code>startT</code> and
     * finishing timestamp <code>endT</code>. 
     * @param simul Simulation which this LP is attached to.
     * @param startT Initial timestamp.
     * @param endT Finishing timestamp.
     */
	public BarrierLogicalProcess(Simulation simul, long startT, long endT) {
        this(simul, startT, endT, 1);
	}

	/**
     * Creates a logical process with initial timestamp 0.0
     * @param simul Simulation which this LP is attached to.
     * @param endT Finishing timestamp.
     */
	public BarrierLogicalProcess(Simulation simul, long endT, int nThreads) {
        this(simul, 0, endT, nThreads);
	}

	/**
     * Creates a logical processwith initial timestamp <code>startT</code> and
     * finishing timestamp <code>endT</code>. 
     * @param simul Simulation which this LP is attached to.
     * @param startT Initial timestamp.
     * @param endT Finishing timestamp.
     */
	public BarrierLogicalProcess(Simulation simul, long startT, long endT, int nThreads) {
		super(simul, startT, endT);
//		if (nThreads == 1)
//			tp = new SingleThreadPool<BasicElement.DiscreteEvent>();
//		else
			tp = new StandardThreadPool<BasicElement.DiscreteEvent>(nThreads);
        waitQueue = new PriorityBlockingQueue<BasicElement.DiscreteEvent>();
        barrier = new DynamicBarrier();
	}
	
    /**
     * Sends an event to the execution queue by looking for a thread to execute it. An event is 
     * added to the execution queue when the LP has reached the event timestamp. 
     * @param e Event to be executed
     */
	public void addExecution(BasicElement.DiscreteEvent e) {
		barrier.add(e);
	}

    /**
     * Removes an event from the execution queue, but performing a previous synchronization.
     * The synchronization consists on waiting for the LP to lock, or for the simulation end.
     * @param e Event to be removed
     */
    protected void removeExecution(BasicElement.DiscreteEvent e) {
    	barrier.notifyBarrier();
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

    class DynamicBarrier {
    	private int nelem = 0;
    	public synchronized void add(BasicElement.DiscreteEvent e) {
    		nelem++;
			tp.execute(e);
    	}
    	public synchronized void waitBarrier() {
    		try {
    			while (nelem > 0)
    				wait();
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
    	}
    	public synchronized void notifyBarrier() {
    		if (--nelem == 0)
    			notify();
    	}
    }
    
    /**
     * Controls the LP execution. First, a <code>SafeLPElement</code> is added. The execution loop 
     * consists on waiting for the elements which are in execution, then the simulation clock is 
     * advanced and a new set of events is executed. 
     */
	public void run() {
        new SafeLPElement().start(this, maxgvt);
		while (!isSimulationEnd()) {
			// Every time the loop is entered we must wait for all the events from the 
			// previous iteration to be finished (the execution queue must be empty)
			barrier.waitBarrier();
			// Now the simulation clock can advance
            execWaitingElements();
		}
		// We must wait for all the event to be finished
		barrier.waitBarrier();
		// Frees the execution queue
    	tp.shutdown();
    	debug("SIMULATION TIME FINISHES\r\nSimulation time = " +
            	lvt + "\r\nPreviewed simulation time = " + maxgvt);
    	printState();
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
//	        strLong.append("\r\n" + executingEvents + " executing elements:");
	        strLong.append("\r\n");
	        strLong.append("\r\n------ LP STATE FINISHED ------\r\n");
			debug(strLong.toString());
		}
	}
	
}