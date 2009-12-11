/**
 * 
 */
package es.ull.isaatc.simulation;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.PriorityQueue;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;

import es.ull.isaatc.simulation.info.TimeChangeInfo;

/**
 * @author Iván Castilla Rodríguez
 *
 */
public class BunchLogicalProcess extends LogicalProcess {
    /** A counter to know how many events are in execution */
    protected AtomicInteger executingEvents = new AtomicInteger(0);
	/** A timestamp-ordered list of events whose timestamp is in the future. */
	protected final PriorityQueue<BasicElement.DiscreteEvent> waitQueue;
    private EventExecutor [] executor;
    private int nextExecutor = 0; 

	/**
	 * @param simul
	 * @param startT
	 * @param endT
	 */
	public BunchLogicalProcess(Simulation simul, long startT, long endT) {
		this(simul, startT, endT, 1);
	}

	/**
	 * @param simul
	 * @param startT
	 * @param endT
	 */
	public BunchLogicalProcess(Simulation simul, long startT, long endT, int nThreads) {
		super(simul, startT - 1, endT);
        waitQueue = new PriorityQueue<BasicElement.DiscreteEvent>();
        executor = new EventExecutor[nThreads];
        for (int i = 0; i < nThreads; i++) {
			executor[i] = new EventExecutor(i);
			executor[i].start();
		}
	}

	/* (non-Javadoc)
	 * @see es.ull.isaatc.simulation.LogicalProcess#addExecution(es.ull.isaatc.simulation.BasicElement.DiscreteEvent)
	 */
	@Override
	public void addExecution(BasicElement.DiscreteEvent e) {
		executingEvents.incrementAndGet();
	}

	/* (non-Javadoc)
	 * @see es.ull.isaatc.simulation.LogicalProcess#addWait(es.ull.isaatc.simulation.BasicElement.DiscreteEvent)
	 */
	@Override
	public void addWait(BasicElement.DiscreteEvent e) {
		((EventExecutor)Thread.currentThread()).addWaitingEvent(e);
	}

	/* (non-Javadoc)
	 * @see es.ull.isaatc.simulation.LogicalProcess#printState()
	 */
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

	/* (non-Javadoc)
	 * @see es.ull.isaatc.simulation.LogicalProcess#removeExecution(es.ull.isaatc.simulation.BasicElement.DiscreteEvent)
	 */
	@Override
	protected void removeExecution(BasicElement.DiscreteEvent e) {
    	executingEvents.decrementAndGet();
	}

	/* (non-Javadoc)
	 * @see es.ull.isaatc.simulation.LogicalProcess#removeWait()
	 */
	@Override
	protected BasicElement.DiscreteEvent removeWait() {
        return waitQueue.poll();
	}

	@Override
    public void addEvent(BasicElement.DiscreteEvent e) {
    	long evTs = e.getTs();
        if (evTs == lvt) {
            addExecution(e);
    		((EventExecutor)Thread.currentThread()).addEvent(e);
        }
        else if (evTs > lvt)
            addWait(e);
        else
        	error("Causal restriction broken\t" + lvt + "\t" + e);
    }

	
    /**
     * Executes a simulation clock cycle. Extracts all the events from the waiting queue with 
     * timestamp equal to the LP timestamp. 
     */
    private void execWaitingElements() {
    	// Updates the future event list with the events produced by the executor threads
    	for (EventExecutor ee : executor) {
    		ArrayDeque<BasicElement.DiscreteEvent> list = ee.getWaitingEvents();
    		while (!list.isEmpty()) {
    			BasicElement.DiscreteEvent e = list.pop();
    			waitQueue.add(e);
    		}    		
    	}
        // Advances the simulation clock
        simul.beforeClockTick();
        lvt = waitQueue.peek().getTs();
        simul.getInfoHandler().notifyInfo(new TimeChangeInfo(simul, lvt));
        simul.afterClockTick();
        debug("SIMULATION TIME ADVANCING " + lvt);
        // Events with timestamp greater or equal to the maximum simulation time aren't
        // executed
        if (lvt < maxgvt) {
        	do {
                BasicElement.DiscreteEvent e = removeWait();
                addExecution(e);
                executor[nextExecutor].addEvent(e);
                nextExecutor = (nextExecutor + 1) % executor.length;
        	} while (waitQueue.peek().getTs() == lvt);
        	for (EventExecutor ee: executor)
        		ee.setReady();
        }
    }

	/* (non-Javadoc)
	 * @see es.ull.isaatc.simulation.LogicalProcess#run()
	 */
	@Override
	public void run() {
		// Starts all the generators
		ArrayList<Generator> genList = simul.getGeneratorList();
		for (Generator gen : genList)
			waitQueue.add(gen.getStartEvent(this, simul.getInternalStartTs()));
        waitQueue.add(new SafeLPElement().getStartEvent(this, maxgvt));
        
        // Simulation main loop
		while (!isSimulationEnd()) {
			// Every time the loop is entered we must wait for all the events from the 
			// previous iteration to be finished (the execution queue must be empty)
			while (executingEvents.get() > 0);
			// Now the simulation clock can advance
            execWaitingElements();
		}
		// The simulation waits for all the events to be removed from the execution queue 
		while (executingEvents.get() > 0);
    	debug("SIMULATION TIME FINISHES\r\nSimulation time = " +
            	lvt + "\r\nPreviewed simulation time = " + maxgvt);
    	printState();
	}

	final class EventExecutor extends Thread {
		private ArrayDeque<BasicElement.DiscreteEvent> extraEvents = new ArrayDeque<BasicElement.DiscreteEvent>();
		private ArrayDeque<BasicElement.DiscreteEvent> extraWaitingEvents = new ArrayDeque<BasicElement.DiscreteEvent>();
		private AtomicBoolean flag = new AtomicBoolean(false);
		
		public EventExecutor(int i) {
			super("LPExec-" + i);
		}
		
		/**
		 * @param event the event to set
		 */
		public void addEvent(BasicElement.DiscreteEvent event) {
			extraEvents.push(event);
		}

		/**
		 * @param event the event to set
		 */
		public void addWaitingEvent(BasicElement.DiscreteEvent event) {
			extraWaitingEvents.push(event);
		}

		public ArrayDeque<BasicElement.DiscreteEvent> getWaitingEvents() {
			return extraWaitingEvents;
		}
		
		public void setReady() {
			flag.set(true);
		}

		@Override
		public void run() {
			while (lvt < maxgvt) {
				if (flag.compareAndSet(true, false)) {
					while (!extraEvents.isEmpty()) {
						extraEvents.pop().run();
					}
				}
			}
		}
	}
	
}
