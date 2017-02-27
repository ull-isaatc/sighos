/**
 * 
 */
package es.ull.iis.simulation.core.flow;

import es.ull.iis.simulation.core.WorkThread;
import es.ull.iis.simulation.model.ActivityWorkGroupEngine;

/**
 * @author Iván Castilla
 *
 */
public interface RequestResourcesFlow extends InitializerFlow, SingleSuccessorFlow, ResourcesFlow {
	/**
	 * Allows a user for adding a customized code when a {@link es.ull.iis.simulation.core.WorkThread} from an {@link es.ull.iis.simulation.core.Element}
	 * is enqueued, waiting for available {@link es.ull.iis.simulation.model.ResourceEngine}. 
	 * @param FlowExecutor {@link es.ull.iis.simulation.core.WorkThread} requesting resources
	 */
	public void inqueue(WorkThread wt);
	
    /**
     * Searches and returns the WG with the specified identifier.
     * @param wgId The identifier of the searched WG 
     * @return A WG defined in this activity with the specified identifier
     */
	public ActivityWorkGroupEngine getWorkGroup(int wgId);
	
	/**
	 * Returns the amount of WGs associated to this activity
	 * @return the amount of WGs associated to this activity
	 */
	public int getWorkGroupSize();
	
	/**
	 * Notifies this basic step that an element that was unavailable has become available.
	 * @param wThread The work thread requesting this basic step
	 */
	public void availableElement(WorkThread wThread);
	
	/**
	 * Notifies this basic step that one or more resources have become available.
	 * @param wThread The work thread requesting this basic step
	 * @return -1 if it can be carried out; 0 if the element is not valid to carry out the activity; 
	 * a positive value accounting for the elements waiting for this basic step if, even with this resource,
	 * it cannot be carried out. 
	 */
	public int availableResource(WorkThread wThread);
}
