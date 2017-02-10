/**
 * 
 */
package es.ull.iis.simulation.core;

import es.ull.iis.simulation.core.flow.Flow;
import es.ull.iis.util.Prioritizable;

/**
 * A work thread is an instance of an {@link Element} performing a {@link SingleFlow}.
 * @author Iván Castilla Rodríguez
 *
 */
public interface WorkThread<F extends Flow<?>> extends Comparable<WorkThread<F>>, Prioritizable, Identifiable {

	/**
	 * Sets the flow currently executed by this workthread 
	 * @param f The flow to be performed
	 */
    public void setCurrentFlow(F f);

    /**
     * Returns the flow being performed.
	 * @return The flow being performed.
	 */
	public F getCurrentFlow();

	/**
     * Returns the element performing this single flow.
     * @return The element performing this single flow
     */
    public Element<?> getElement();
    
	/**
	 * Returns the order this item occupies among the rest of work items.
	 * @return The order of arrival of this work item to request the activity
	 */
	public int getArrivalOrder();

	/**
	 * Returns the timestamp when this work item arrived to request the current single flow.
	 * @return The timestamp when this work item arrived to request the current single flow
	 */
	public long getArrivalTs();

	/**
	 * Returns the time required to finish the current single flow (only for interruptible activities) 
	 * @return The time required to finish the current single flow 
	 */
	public long getTimeLeft();

    /**
     * Returns the workgroup which is used to perform this flow, or <tt>null</tt>  
     * if the flow has not been carried out.
	 * @return the workgroup which is used to perform this flow, or <tt>null</tt>  
     * if the flow has not been carried out.
	 */
	public ActivityWorkGroup getExecutionWG();
	
}
