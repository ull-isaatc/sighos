/**
 * 
 */
package es.ull.iis.simulation.core.flow;

import java.util.Set;

import es.ull.iis.simulation.core.Element;
import es.ull.iis.simulation.core.WorkThread;
import es.ull.iis.simulation.model.EngineObject;

/**
 * The process an element has to carry out.<p>
 * A flow is structured as a graph, so it can have successors and predecessor. Successors can be
 * added by using {@link #link(Flow)}. This implies invoking the {@link #addPredecessor(Flow)} 
 * method for the new successor. By creating this basic structure, the Sequence workflow pattern
 * (WFP1) is implemented.<p>
 * Flows can have not only successors and predecessors, but can be enclosed by an structured 
 * flow, which is considered its parent.<p>
 * This flow can be requested to be carried out by an element. To do this, a set of user-defined 
 * conditions are first checked by invoking <code>beforeRequest</code>. If this method is true, 
 * the Element can definitely request this flow.  
 * @author Iv�n Castilla Rodr�guez
 *
 */
public interface Flow extends EngineObject {
	/**
	 * Adds a flow's successor. This method must invoke <code>successor.addPredecessor</code>
	 * to build the graph properly. 
	 * @param successor This flow's successor.
	 * @return The successor (useful for chained links)
	 */
	Flow link(Flow successor);	
	
	/**
	 * Notifies this flow that it has been linked (i.e. added as a successor) to
	 * another flow.
	 * @param predecessor This flow's predecessor.
	 */
	void addPredecessor(Flow predecessor);
	
	/**
	 * Returns the structured flow which contains this flow.
	 * @return the structured flow which contains this flow.
	 */
	StructuredFlow getParent();
	
	/**
	 * Sets the structured flow which contains this flow. 
	 * @param parent the structured flow which contains this flow.
	 */
	void setParent(StructuredFlow parent);
	
	/**
	 * Sets the structured flow which contains this flow and does the same for the
	 * successors of this flow.
	 * @param parent the structured flow which contains this flow and its successors.
	 * @param visited list of already visited flows (to prevent infinite recursion when 
	 * arbitrary loops are present)
	 */
	void setRecursiveStructureLink(StructuredFlow parent, Set<Flow> visited);
	
	/**
	 * Allows a user to add conditions which the element requesting this flow must meet
	 * before request this flow.
	 * @param e The element trying to request this flow.
	 * @return True if this flow can be requested; false in other case.
	 */
	boolean beforeRequest(Element e);
	
	
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
