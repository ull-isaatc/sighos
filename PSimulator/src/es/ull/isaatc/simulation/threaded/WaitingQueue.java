/**
 * 
 */
package es.ull.isaatc.simulation.threaded;

/**
 * @author Iván Castilla Rodríguez
 *
 */
public interface WaitingQueue {
	void add(BasicElement.DiscreteEvent e);
	BasicElement.DiscreteEvent poll();
	BasicElement.DiscreteEvent peek();
	boolean isEmpty();
	int size();
    /**
     * Executes a simulation clock cycle. Extracts all the events from the waiting queue with 
     * timestamp equal to the LP timestamp. 
     */
	void execWaitingElements(LogicalProcess lp);
	
}
