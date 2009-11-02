/**
 * 
 */
package es.ull.isaatc.simulation.common;

import es.ull.isaatc.simulation.common.flow.SingleFlow;

/**
 * @author Iván Castilla Rodríguez
 *
 */
public interface WorkItem {
	/**
     * Returns the activity wrapped with the current single flow.
     * @return Activity wrapped with the current single flow.
     */
    public Activity getActivity();
	
    /**
     * Gets the current single flow
	 * @return the current single flow
	 */
	public SingleFlow getFlow();

	/**
     * Returns the element which carries out this flow.
     * @return The associated element.
     */
    public Element getElement();
    
	/**
	 * Returns the order this item occupies among the rest of work items.
	 * @return the order of arrival of this work item to request the activity
	 */
	public int getArrivalOrder();

	/**
	 * Returns the timestamp when this work item arrives to request the current single flow.
	 * @return the timestamp when this work item arrives to request the current single flow
	 */
	public double getArrivalTs();

	/**
	 * Returns the time required to finish the current single flow (only for interruptible activities) 
	 * @return the time required to finish the current single flow 
	 */
	public double getTimeLeft();

    /**
     * Returns the workgroup which is used to perform this flow, or <code>null</code>  
     * if the flow has not been carried out.
	 * @return the workgroup which is used to perform this flow, or <code>null</code>  
     * if the flow has not been carried out.
	 */
	public ActivityWorkGroup getExecutionWG();
	
}
