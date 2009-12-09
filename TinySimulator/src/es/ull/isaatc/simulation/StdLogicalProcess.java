/**
 * 
 */
package es.ull.isaatc.simulation;

import java.util.concurrent.PriorityBlockingQueue;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.ReentrantLock;

import es.ull.isaatc.simulation.BasicElement.DiscreteEvent;
import es.ull.isaatc.simulation.info.TimeChangeInfo;
import es.ull.isaatc.util.SingleThreadPool;
import es.ull.isaatc.util.StandardThreadPool;
import es.ull.isaatc.util.ThreadPool;

/**
 * @author Iván Castilla Rodríguez
 *
 */
public class StdLogicalProcess extends LogicalProcess {
    /** A lock for the execution queue. */
	protected final ReentrantLock lpLock;
	/** A condition related to <code>lpLock</code>. This condition is used to control
	 * the advance of simulation time */
	protected final Condition execEmpty;
    /** Thread pool to execute events */
    protected final ThreadPool<BasicElement.DiscreteEvent> tp;
    /** A counter to know how many events are in execution */
    protected int executingEvents = 0;
	/** A timestamp-ordered list of events whose timestamp is in the future. */
	protected final PriorityBlockingQueue<BasicElement.DiscreteEvent> waitQueue;

	/**
	 * @param simul
	 * @param startT
	 * @param endT
	 */
	public StdLogicalProcess(Simulation simul, long startT, long endT) {
        this(simul, startT, endT, 1);
	}

	/**
	 * @param simul
	 * @param startT
	 * @param endT
	 * @param threads
	 */
	public StdLogicalProcess(Simulation simul, long startT, long endT,
			int nThreads) {
		super(simul, startT, endT);
        lpLock = new ReentrantLock();
        execEmpty = lpLock.newCondition();
		if (nThreads == 1)
			tp = new SingleThreadPool<BasicElement.DiscreteEvent>();
		else
			tp = new StandardThreadPool<BasicElement.DiscreteEvent>(nThreads);
        waitQueue = new PriorityBlockingQueue<BasicElement.DiscreteEvent>();
	}

	@Override
	public void addExecution(DiscreteEvent e) {
		lpLock.lock();
		try {
			executingEvents++;
			tp.execute(e);
		}
		finally {
			lpLock.unlock();
		}
		
	}

	@Override
	protected void removeExecution(DiscreteEvent e) {
        lpLock.lock();
        try {
			// The LP is informed of the finalization of an event. There's no need of 
			// checking if the exec. queue is empty because the main loop is already doing that.
			if (--executingEvents == 0)// FIXME: Check if the execution queue is empty is an improvement?
				execEmpty.signal();
        }
		finally {
			lpLock.unlock();
		}
	}

	@Override
	public void addWait(DiscreteEvent e) {
		waitQueue.add(e);
	}

	@Override
	protected DiscreteEvent removeWait() {
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

	@Override
	public void run() {
        new SafeLPElement().start(this, maxgvt);
		while (!isSimulationEnd()) {
			// Every time the loop is entered we must wait for all the events from the 
			// previous iteration to be finished (the execution queue must be empty)
			lpLock.lock();
			try {
				// The LP waits for all the events to be removed from the execution queue 
				while (executingEvents != 0)
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
			while (executingEvents != 0)
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
		
	}

	@Override
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
