/**
 * 
 */
package es.ull.isaatc.simulation.common.flow;

import es.ull.isaatc.simulation.common.Element;
import es.ull.isaatc.simulation.common.SimulationObject;

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
 *
 */
public interface Flow extends SimulationObject {
	/**
	 * Adds a flow's successor. This method must invoke <code>successor.addPredecessor</code>
	 * to build the graph properly. 
	 * @param successor This flow's successor.
	 */
	void link(Flow successor);	
	
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
	 */
	void setRecursiveStructureLink(StructuredFlow parent);
	
	/**
	 * Allows a user to add conditions which the element requesting this flow must meet
	 * before request this flow.
	 * @param e The element trying to request this flow.
	 * @return True if this flow can be requested; false in other case.
	 */
	boolean beforeRequest(Element e);
}
