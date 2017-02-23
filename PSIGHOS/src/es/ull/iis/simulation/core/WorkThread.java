/**
 * 
 */
package es.ull.iis.simulation.core;

import es.ull.iis.simulation.core.flow.Flow;
import es.ull.iis.simulation.core.flow.InitializerFlow;
import es.ull.iis.simulation.model.ActivityWorkGroupEngine;
import es.ull.iis.simulation.model.Identifiable;
import es.ull.iis.util.Prioritizable;

/**
 * A work thread is an instance of an {@link Element} performing a {@link SingleFlow}.
 * @author Iván Castilla Rodríguez
 *
 */
public interface WorkThread extends Comparable<WorkThread>, Prioritizable, Identifiable {

	/**
	 * Sets the flow currently executed by this workthread 
	 * @param f The flow to be performed
	 */
    void setCurrentFlow(Flow f);

    /**
     * Returns the flow being performed.
	 * @return The flow being performed.
	 */
	Flow getCurrentFlow();

	/**
     * Returns the element performing this single flow.
     * @return The element performing this single flow
     */
    Element getElement();
    
	/**
	 * Returns the order this item occupies among the rest of work items.
	 * @return The order of arrival of this work item to request the activity
	 */
	int getArrivalOrder();

	/**
	 * Returns the timestamp when this work item arrived to request the current single flow.
	 * @return The timestamp when this work item arrived to request the current single flow
	 */
	long getArrivalTs();

	/**
	 * Returns the time required to finish the current single flow (only for interruptible activities) 
	 * @return The time required to finish the current single flow 
	 */
	long getTimeLeft();

    /**
     * Returns the workgroup which is used to perform this flow, or <tt>null</tt>  
     * if the flow has not been carried out.
	 * @return the workgroup which is used to perform this flow, or <tt>null</tt>  
     * if the flow has not been carried out.
	 */
	ActivityWorkGroupEngine getExecutionWG();

	/**
	 * Returns true if the specified flow was already visited from this thread.
	 * @param flow Flow to be checked.
	 * @return True if the specified flow was already visited from this thread; false otherwise.
	 */
	boolean wasVisited (Flow flow);

	/**
	 * Returns <code>true</code> if this thread is valid.
	 * @return <code>True</code> if this thread is valid; false otherwise.
	 */
	boolean isExecutable();

	/**
	 * @param executable the executable to set
	 */
	void setExecutable(boolean executable, Flow startPoint);
	
    /**
     * Notifies the parent this thread has finished.
     */
    void notifyEnd();
	
	/**
	 * Returns a new instance of a work thread created to carry out the inner subflow of a structured flow. 
	 * The current thread is the parent of the newly created child thread. has the same state than the c
	 * @return A new instance of a work thread created to carry out the inner subflow of a structured flow
	 */
	WorkThread getInstanceDescendantWorkThread(InitializerFlow newFlow);
	
	/**
	 * Adds a new visited flow to the list.
	 * @param flow New visited flow
	 */
	void updatePath(Flow flow);
	
	/**
	 * Returns the last flow visited by this workthread
	 * @return the last flow visited by this workthread
	 */
	Flow getLastFlow();

	/**
	 * Sets the last flow visited by this thread.
	 * @param lastFlow The lastFlow visited by this thread
	 */
	void setLastFlow(Flow lastFlow);	
	
}
