/**
 * 
 */
package es.ull.isaatc.simulation;

import java.util.PriorityQueue;

import es.ull.isaatc.simulation.BasicElement.DiscreteEvent;
import es.ull.isaatc.simulation.info.TimeChangeInfo;

/**
 * @author Iván Castilla Rodríguez
 *
 */
public class SequentialLogicalProcess extends LogicalProcess {
	/** A timestamp-ordered list of events whose timestamp is in the future. */
	protected final PriorityQueue<BasicElement.DiscreteEvent> waitQueue;

	/**
	 * @param simul
	 * @param startT
	 * @param endT
	 */
	public SequentialLogicalProcess(Simulation simul, long startT, long endT) {
		super(simul, startT, endT);
        waitQueue = new PriorityQueue<BasicElement.DiscreteEvent>();
	}

	/* (non-Javadoc)
	 * @see es.ull.isaatc.simulation.LogicalProcess#addExecution(es.ull.isaatc.simulation.BasicElement.DiscreteEvent)
	 */
	@Override
	public void addExecution(DiscreteEvent e) {
		e.run();
	}

	/* (non-Javadoc)
	 * @see es.ull.isaatc.simulation.LogicalProcess#addWait(es.ull.isaatc.simulation.BasicElement.DiscreteEvent)
	 */
	@Override
	public void addWait(DiscreteEvent e) {
		waitQueue.add(e);
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
	        strLong.append("\r\n");
	        strLong.append("\r\n------ LP STATE FINISHED ------\r\n");
			debug(strLong.toString());
		}
	}

	/* (non-Javadoc)
	 * @see es.ull.isaatc.simulation.LogicalProcess#removeExecution(es.ull.isaatc.simulation.BasicElement.DiscreteEvent)
	 */
	@Override
	protected void removeExecution(DiscreteEvent e) {
		// TODO Auto-generated method stub

	}

	/* (non-Javadoc)
	 * @see es.ull.isaatc.simulation.LogicalProcess#removeWait()
	 */
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

    /* (non-Javadoc)
	 * @see es.ull.isaatc.simulation.LogicalProcess#run()
	 */
	@Override
	public void run() {
        new SafeLPElement().start(this, maxgvt);
		while (!isSimulationEnd())
            execWaitingElements();
		// Frees the execution queue
    	debug("SIMULATION TIME FINISHES\r\nSimulation time = " +
            	lvt + "\r\nPreviewed simulation time = " + maxgvt);
    	printState();
	}

}
