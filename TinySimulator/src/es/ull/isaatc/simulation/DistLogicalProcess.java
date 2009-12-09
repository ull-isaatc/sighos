/**
 * 
 */
package es.ull.isaatc.simulation;

import java.util.PriorityQueue;
import java.util.concurrent.Semaphore;

import es.ull.isaatc.simulation.BasicElement.DiscreteEvent;
import es.ull.isaatc.simulation.info.TimeChangeInfo;

/**
 * IMPORTANTE: Para usarlo hay que cambiar el método BasicElement.start(), de forma que el evento se añada 
 * directamente a la cola de espera.
 * @author Iván Castilla Rodríguez
 *
 */
public class DistLogicalProcess extends LogicalProcess {
    /** Thread pool to execute events */
    protected final EventExecutor[] tp;
	protected final Semaphore sem;
    /** A counter to know how many events are in execution */
    protected int executingEvents = 0;
	/** A timestamp-ordered list of events whose timestamp is in the future. */
	protected final PriorityQueue<BasicElement.DiscreteEvent> waitQueue;

	/**
	 * @param simul
	 * @param startT
	 * @param endT
	 */
	public DistLogicalProcess(Simulation simul, long startT, long endT) {
        this(simul, startT, endT, 1);
	}

	/**
	 * @param simul
	 * @param startT
	 * @param endT
	 * @param threads
	 */
	public DistLogicalProcess(Simulation simul, long startT, long endT,
			int nThreads) {
		super(simul, startT, endT);
		sem = new Semaphore(1);
        tp = new EventExecutor[nThreads];
        for (int i = 0; i < nThreads; i++)
        	tp[i] = new EventExecutor(i);
        waitQueue = new PriorityQueue<BasicElement.DiscreteEvent>();
	}

	@Override
	public void addExecution(DiscreteEvent e) {
		try {
			sem.acquire();
			executingEvents++;
		} catch (InterruptedException e1) {
			e1.printStackTrace();
		}
		finally {
			sem.release();
		}
		e.run();
		
	}

	@Override
	protected void removeExecution(DiscreteEvent e) {
        try {
			sem.acquire();
			// The LP is informed of the finalization of an event. There's no need of 
			// checking if the exec. queue is empty because the main loop is already doing that.
			--executingEvents;
		} catch (InterruptedException e1) {
			e1.printStackTrace();
		}
		finally {
			sem.release();
		}
	}

	@Override
	public void addWait(DiscreteEvent e) {
        try {
			sem.acquire();
			waitQueue.add(e);
			debug("New event waiting... " + e);
		} catch (InterruptedException e1) {
			e1.printStackTrace();
		}
		finally {
			sem.release();
		}
	}

	@Override
	protected DiscreteEvent removeWait() {
        return waitQueue.poll();
	}

	@Override
	public void run() {
        new SafeLPElement().getStartEvent(this, maxgvt);
        for (EventExecutor ee : tp)
        	ee.start();
		try {
			for (EventExecutor ee : tp)
				ee.join();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
        
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

	class EventExecutor extends Thread {
		boolean finished = false;
		
		public EventExecutor(int id) {
			super ("EE" + id);
		}
		
		@Override
		public void run() {
			while (!finished) {
				BasicElement.DiscreteEvent e = null;
				try {
					sem.acquire();
			        if (executingEvents == 0 && waitQueue.isEmpty())
			        	finished = true;
			        else if (!waitQueue.isEmpty()) {
			        	if (executingEvents == 0) {
			                // Advances the simulation clock
			                simul.beforeClockTick();
			        		lvt = waitQueue.peek().getTs();
			                simul.getInfoHandler().notifyInfo(new TimeChangeInfo(simul, lvt));
			                simul.afterClockTick();
			                debug("SIMULATION TIME ADVANCING " + lvt);
			        	}
			        	if (isSimulationEnd())
			        		finished = true;
			        	else if (waitQueue.peek().ts == lvt) {
			        		e = removeWait();
			        		executingEvents++;
			        	}
			        }
				} catch (InterruptedException e1) {
					e1.printStackTrace();
				}
				finally {
					sem.release();					
				}
				if (e != null)
					e.run();
			}
		}
	}
}
