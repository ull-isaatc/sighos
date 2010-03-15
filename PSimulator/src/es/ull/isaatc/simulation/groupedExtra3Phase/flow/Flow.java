/**
 * 
 */
package es.ull.isaatc.simulation.groupedExtra3Phase.flow;

import es.ull.isaatc.simulation.groupedExtra3Phase.WorkThread;

/**
 * The process an element has to carry out.<p>
 * A flow is structured in a graph, so it can have successors and predecessor. One or several 
 * successors to this flow can be added by using <code>link</code>. If this flow is added as 
 * a successor for another flow, <code>addPredecessor</code> is invoked too. Having one or 
 * several successors implies implementing Sequence (WFP1)<p>
 * Flows can have not only successors and predecessors, but can be enclosed by an structured 
 * flow, which is considered its parent.<p>
 * This flow can be requested to be carried out by an element. To do this, a set of user-defined 
 * conditions are first checked by invoking <code>beforeRequest</code>. If this method is true, 
 * the Element can definitely request this flow.  
 * @author Iván Castilla Rodríguez
 */
public interface Flow extends es.ull.isaatc.simulation.common.flow.Flow {

	/**
	 * Requests this flow. An element, by means of a work thread, requests this flow to
	 * carry it out.
	 * @param wThread The work thread requesting this flow.
	 */
	void request(WorkThread wThread);
	
	/**
	 * Requests this flow successor(s) to continue the execution. This method is invoked 
	 * after all the tasks associated to this flow has been successfully carried out.
	 * @param wThread The work thread which requested this flow.
	 */
	void next(WorkThread wThread);
}
