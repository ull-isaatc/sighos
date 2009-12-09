package es.ull.isaatc.simulation;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.PriorityQueue;
import java.util.TreeSet;
import java.util.concurrent.PriorityBlockingQueue;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.ReentrantLock;

import es.ull.isaatc.simulation.BasicElement.DiscreteEvent;
import es.ull.isaatc.simulation.info.TimeChangeInfo;
import es.ull.isaatc.util.SingleThreadPool;
import es.ull.isaatc.util.ThreadPool;

/** 
 * A logical process (LP) is a subregion of the simulation. It includes a set of resource types,
 * activities, resources, and elements. The LP handles a set of events which interact with any
 * of the components associated to this LP. An LP is subdivided into activity managers.   
 * @author Carlos Martín Galán
 */
public class CustomLogicalProcess extends LogicalProcess {
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
	protected final WaitingQueue waitQueue;
    private int nThreads;

	/**
     * Creates a logical processwith initial timestamp <code>startT</code> and
     * finishing timestamp <code>endT</code>. 
     * @param simul Simulation which this LP is attached to.
     * @param startT Initial timestamp.
     * @param endT Finishing timestamp.
     */
	public CustomLogicalProcess(Simulation simul, long startT, long endT) {
        this(simul, startT, endT, 1);
	}

	/**
     * Creates a logical processwith initial timestamp <code>startT</code> and
     * finishing timestamp <code>endT</code>. 
     * @param simul Simulation which this LP is attached to.
     * @param startT Initial timestamp.
     * @param endT Finishing timestamp.
     */
	public CustomLogicalProcess(Simulation simul, long startT, long endT, int nThreads) {
		super(simul, startT, endT);
		this.nThreads = nThreads;
		tp = new SingleThreadPool<BasicElement.DiscreteEvent>();
        waitQueue = new DoubleBufferPriorityWaitingQueue();
        lpLock = new ReentrantLock();
        execEmpty = lpLock.newCondition();
	}
	
    /**
     * Sends an event to the execution queue by looking for a thread to execute it. An event is 
     * added to the execution queue when the LP has reached the event timestamp. 
     * @param e Event to be executed
     */
	public void addExecution(BasicElement.DiscreteEvent e) {
		lpLock.lock();
		try {
			executingEvents++;
			tp.execute(e);
		}
		finally {
			lpLock.unlock();
		}
	}

    /**
     * Removes an event from the execution queue, but performing a previous synchronization.
     * The synchronization consists on waiting for the LP to lock, or for the simulation end.
     * @param e Event to be removed
     */
    protected void removeExecution(BasicElement.DiscreteEvent e) {
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
				while (executingEvents != 0)
					execEmpty.await();
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			finally {
				lpLock.unlock();
			}
			// Now the simulation clock can advance
            waitQueue.execWaitingElements(this);
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

	/**
	 * Does a debug print of this LP. Prints the current local time, the contents of
	 * the waiting and execution queues, and the contents of the activity managers. 
	 */
	protected void printState() {
		if (isDebugEnabled()) {
			StringBuffer strLong = new StringBuffer("------    LP STATE    ------");
			strLong.append("LVT: " + lvt + "\r\n");
	        strLong.append(waitQueue.size() + " waiting elements: ");
	        while (!waitQueue.isEmpty())
	            strLong.append(waitQueue.poll() + " ");
	        strLong.append("\r\n" + executingEvents + " executing elements:");
	        strLong.append("\r\n");
	        strLong.append("\r\n------ LP STATE FINISHED ------\r\n");
			debug(strLong.toString());
		}
	}
	
	class DoubleBufferTreeSetWaitingQueue implements WaitingQueue {
		TreeSet<BasicElement.DiscreteEvent> internalQueue;
		ArrayList<ArrayDeque<BasicElement.DiscreteEvent>> auxInternalQueue;
		
		
		public DoubleBufferTreeSetWaitingQueue() {
			internalQueue = new TreeSet<BasicElement.DiscreteEvent>();
			auxInternalQueue = new ArrayList<ArrayDeque<BasicElement.DiscreteEvent>>(nThreads);
	        for (int i = 0; i < nThreads; i++)
	        	auxInternalQueue.add(new ArrayDeque<BasicElement.DiscreteEvent>());
		}

		@Override
		public boolean isEmpty() {
			return internalQueue.isEmpty();
		}

		@Override
		public DiscreteEvent peek() {
			return internalQueue.first();
		}

		@Override
		public DiscreteEvent poll() {
			return internalQueue.pollFirst();
		}

		@Override
		public void add(DiscreteEvent e) {
			auxInternalQueue.get(Integer.parseInt(Thread.currentThread().getName())).push(e);;
		}

		@Override
		public int size() {
			return internalQueue.size();
		}

		@Override
		public void execWaitingElements(LogicalProcess lp) {
			// Moves from one buffer to the other
            for (ArrayDeque<BasicElement.DiscreteEvent> ad : auxInternalQueue)
            	while (!ad.isEmpty())
            		internalQueue.add(ad.poll());
	        // Extracts the first event
	        if (! internalQueue.isEmpty()) {
	            BasicElement.DiscreteEvent e = internalQueue.first();
	            // Advances the simulation clock
	            simul.beforeClockTick();
	            lvt = e.getTs();
	            simul.getInfoHandler().notifyInfo(new TimeChangeInfo(simul, lvt));
	            simul.afterClockTick();
	            debug("SIMULATION TIME ADVANCING " + lvt);
	            // Events with timestamp greater or equal to the maximum simulation time aren't
	            // executed
	            if (lvt < maxgvt) {
	            	e = internalQueue.pollFirst();
	                addExecution(e);
	                // Extracts all the events with the same timestamp
	                boolean flag = false;
	                do {
	                    if (! internalQueue.isEmpty()) {
	                        if (internalQueue.first().getTs() == lvt) {
		                        e = internalQueue.pollFirst();
	                            addExecution(e);
	                            flag = true;
	                        }
	                        else {  
	                            flag = false;
	                        }
	                    }
	                    else {  // The waiting queue is empty
	                        flag = false;
	                    }
	                } while ( flag );
	            }
	        }        
			
		}
	}

	class DoubleBufferPriorityWaitingQueue implements WaitingQueue {
		PriorityQueue<BasicElement.DiscreteEvent> internalQueue;
		ArrayList<ArrayDeque<BasicElement.DiscreteEvent>> auxInternalQueue;
		
		
		public DoubleBufferPriorityWaitingQueue() {
			internalQueue = new PriorityQueue<BasicElement.DiscreteEvent>();
			auxInternalQueue = new ArrayList<ArrayDeque<BasicElement.DiscreteEvent>>(nThreads);
	        for (int i = 0; i < nThreads; i++)
	        	auxInternalQueue.add(new ArrayDeque<BasicElement.DiscreteEvent>());
		}

		@Override
		public boolean isEmpty() {
			return internalQueue.isEmpty();
		}

		@Override
		public DiscreteEvent peek() {
			return internalQueue.peek();
		}

		@Override
		public DiscreteEvent poll() {
			return internalQueue.poll();
		}

		@Override
		public void add(DiscreteEvent e) {
			auxInternalQueue.get(Integer.parseInt(Thread.currentThread().getName())).push(e);;
		}

		@Override
		public int size() {
			return internalQueue.size();
		}

		@Override
		public void execWaitingElements(LogicalProcess lp) {
			// Moves from one buffer to the other
            for (ArrayDeque<BasicElement.DiscreteEvent> ad : auxInternalQueue)
            	while (!ad.isEmpty())
            		internalQueue.add(ad.poll());
	        // Extracts the first event
	        if (! internalQueue.isEmpty()) {
	            BasicElement.DiscreteEvent e = internalQueue.peek();
	            // Advances the simulation clock
	            simul.beforeClockTick();
	            lvt = e.getTs();
	            simul.getInfoHandler().notifyInfo(new TimeChangeInfo(simul, lvt));
	            simul.afterClockTick();
	            debug("SIMULATION TIME ADVANCING " + lvt);
	            // Events with timestamp greater or equal to the maximum simulation time aren't
	            // executed
	            if (lvt < maxgvt) {
	            	e = internalQueue.poll();
	                addExecution(e);
	                // Extracts all the events with the same timestamp
	                boolean flag = false;
	                do {
	                    if (! internalQueue.isEmpty()) {
	                        if (internalQueue.peek().getTs() == lvt) {
		                        e = internalQueue.poll();
	                            addExecution(e);
	                            flag = true;
	                        }
	                        else {  
	                            flag = false;
	                        }
	                    }
	                    else {  // The waiting queue is empty
	                        flag = false;
	                    }
	                } while ( flag );
	            }
	        }        
			
		}
	}

	class PriorityBlockingWaitingQueue implements WaitingQueue {
		PriorityBlockingQueue<BasicElement.DiscreteEvent> internalQueue;
		
		public PriorityBlockingWaitingQueue() {
			internalQueue = new PriorityBlockingQueue<BasicElement.DiscreteEvent>();
		}

		@Override
		public boolean isEmpty() {
			return internalQueue.isEmpty();
		}

		@Override
		public DiscreteEvent peek() {
			return internalQueue.peek();
		}

		@Override
		public DiscreteEvent poll() {
			return internalQueue.poll();
		}

		@Override
		public void add(DiscreteEvent e) {
			internalQueue.add(e);
		}

		@Override
		public int size() {
			return internalQueue.size();
		}

		@Override
		public void execWaitingElements(LogicalProcess lp) {
	        // Extracts the first event
	        if (! internalQueue.isEmpty()) {
	            BasicElement.DiscreteEvent e = internalQueue.peek();
	            // Advances the simulation clock
	            simul.beforeClockTick();
	            lvt = e.getTs();
	            simul.getInfoHandler().notifyInfo(new TimeChangeInfo(simul, lvt));
	            simul.afterClockTick();
	            debug("SIMULATION TIME ADVANCING " + lvt);
	            // Events with timestamp greater or equal to the maximum simulation time aren't
	            // executed
	            if (lvt < maxgvt) {
	            	e = internalQueue.poll();
	                addExecution(e);
	                // Extracts all the events with the same timestamp
	                boolean flag = false;
	                do {
	                    if (! internalQueue.isEmpty()) {
	                        if (internalQueue.peek().getTs() == lvt) {
		                        e = internalQueue.poll();
	                            addExecution(e);
	                            flag = true;
	                        }
	                        else {  
	                            flag = false;
	                        }
	                    }
	                    else {  // The waiting queue is empty
	                        flag = false;
	                    }
	                } while ( flag );
	            }
	        }        
			
		}
			
//		@Override
//		public void execWaitingElements(LogicalProcess lp) {
//	    	long t1 = System.currentTimeMillis();
//	        // Extracts the first event
//	        if (! internalQueue.isEmpty()) {
//	            BasicElement.DiscreteEvent e = internalQueue.poll();
//	            // Advances the simulation clock
//	            simul.beforeClockTick();
//	            lvt = e.getTs();
//	            simul.getInfoHandler().notifyInfo(new TimeChangeInfo(simul, lvt));
//	            simul.afterClockTick();
//	            debug("SIMULATION TIME ADVANCING " + lvt);
//	            // Events with timestamp greater or equal to the maximum simulation time aren't
//	            // executed
//	            if (lvt >= maxgvt)
//	            	internalQueue.add(e);
//	            else {
//	                addExecution(e);
//	                // Extracts all the events with the same timestamp
//	                boolean flag = false;
//	                do {
//	                    if (! internalQueue.isEmpty()) {
//	                        e = internalQueue.poll();
//	                        if (e.getTs() == lvt) {
//	                            addExecution(e);
//	                            flag = true;
//	                        }
//	                        else {  
//	                            flag = false;
//	                            internalQueue.add(e);
//	                        }
//	                    }
//	                    else {  // The waiting queue is empty
//	                        flag = false;
//	                    }
//	                } while ( flag );
//	            }
//	        }        
//	        simul.lpTime.addAndGet(System.currentTimeMillis() - t1);
//			
//		}
	}
	
}