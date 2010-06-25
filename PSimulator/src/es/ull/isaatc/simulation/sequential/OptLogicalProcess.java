/**
 * 
 */
package es.ull.isaatc.simulation.sequential;

import java.util.ArrayDeque;

import es.ull.isaatc.simulation.common.info.TimeChangeInfo;

/**
 * @author Iv�n
 *
 */
public class OptLogicalProcess extends LogicalProcess {
	protected final ArrayDeque<BasicElement.DiscreteEvent> execQueue = new ArrayDeque<BasicElement.DiscreteEvent>();;
	
	/**
     * Creates a logical process with initial timestamp 0.0
     * @param simul Simulation which this LP is attached to.
     * @param endT Finishing timestamp.
     */
	public OptLogicalProcess(Simulation simul, long endT) {
        super(simul, 0, endT);
	}

	/**
     * Creates a logical processwith initial timestamp <code>startT</code> and
     * finishing timestamp <code>endT</code>. 
     * @param simul Simulation which this LP is attached to.
     * @param startT Initial timestamp.
     * @param endT Finishing timestamp.
     */
	public OptLogicalProcess(Simulation simul, long startT, long endT) {
		super(simul, startT, endT);
	}

	@Override
	public void addWait(BasicElement.DiscreteEvent e) {
		waitQueue.add(e);
	}
	
	@Override
	public void addExecution(BasicElement.DiscreteEvent e) {
		execQueue.push(e);
	}
	
	@Override
    protected BasicElement.DiscreteEvent removeWait() {
        return waitQueue.poll();
    }
    
    /**
     * Executes a simulation clock cycle. Extracts all the events from the waiting queue with 
     * timestamp equal to the LP timestamp. 
     */
    protected void execWaitingElements() {
        while (!execQueue.isEmpty())
    		execQueue.pop().run();
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

}