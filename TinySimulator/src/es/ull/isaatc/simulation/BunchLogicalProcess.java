/**
 * 
 */
package es.ull.isaatc.simulation;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.List;
import java.util.TreeMap;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;

import es.ull.isaatc.simulation.info.TimeChangeInfo;

/**
 * @author Iván Castilla Rodríguez
 *
 */
// FIXME: Se bloquea o da un error de NoSuchElement esporádicamente
public class BunchLogicalProcess extends LogicalProcess {
    /** A counter to know how many events are in execution */
    protected AtomicInteger executingEvents = new AtomicInteger(0);
	/** A timestamp-ordered list of events whose timestamp is in the future. */
	protected final TreeMap<Long, ArrayList<BasicElement.DiscreteEvent>> waitQueue;
    private EventExecutor [] executor;

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
        waitQueue = new TreeMap<Long, ArrayList<BasicElement.DiscreteEvent>>();
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
		ArrayList<BasicElement.DiscreteEvent> list = waitQueue.get(e.getTs());
		if (list == null) {
			list = new ArrayList<BasicElement.DiscreteEvent>();
			list.add(e);
			waitQueue.put(e.getTs(), list);
		}
		else
			list.add(e);
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
	        for (ArrayList<BasicElement.DiscreteEvent> ad : waitQueue.values())
	        	for (BasicElement.DiscreteEvent e : ad)
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

	@Override
    public void addEvent(BasicElement.DiscreteEvent e) {
    	long evTs = e.getTs();
        if (evTs == lvt) {
            addExecution(e);
    		((EventExecutor)Thread.currentThread()).addEvent(e);
        }
        else if (evTs > lvt)
    		((EventExecutor)Thread.currentThread()).addWaitingEvent(e);
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
    			addWait(e);
    		}    		
    	}
        // Advances the simulation clock
        simul.beforeClockTick();
        lvt = waitQueue.firstKey();
        simul.getInfoHandler().notifyInfo(new TimeChangeInfo(simul, lvt));
        simul.afterClockTick();
        debug("SIMULATION TIME ADVANCING " + lvt);
        // Events with timestamp greater or equal to the maximum simulation time aren't
        // executed
        if (lvt < maxgvt) {
        	ArrayList<BasicElement.DiscreteEvent> list = waitQueue.pollFirstEntry().getValue();
    		int share = list.size();
    		executingEvents.addAndGet(share);
    		share = share / executor.length;
    		int iter = 0;
    		for (; iter < executor.length - 1; iter++)
    			executor[iter].addEvents(list.subList(share * iter, share * (iter + 1)));
    		executor[iter].addEvents(list.subList(share * iter, list.size()));
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
			addWait(gen.getStartEvent(this, simul.getInternalStartTs()));
        addWait(new SafeLPElement().getStartEvent(this, maxgvt));
        
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
		public void addEvents(List<BasicElement.DiscreteEvent> eventList) {
			extraEvents.addAll(eventList);
			flag.set(true);
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
