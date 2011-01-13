/**
 * 
 */
package es.ull.isaatc.simulation.core.flow;

import es.ull.isaatc.simulation.core.Activity;
import es.ull.isaatc.simulation.core.Element;

/**
 * A {@link Flow} which executes an {@link Activity}. 
 * @author Iv�n Castilla Rodr�guez
 */
public interface SingleFlow extends SingleSuccessorFlow, TaskFlow {
	/**
	 * Obtains the {@link Activity} associated to this {@link SingleFlow}.
	 * @return The associated {@link Activity} 
	 */
	public Activity getActivity();

	/**
	 * Allows a user for adding a customized code when an {@link Element}
	 * is enqueued in an {@link Activity}, waiting for available {@link Resources}. 
	 * @param e {@link Element} requesting this {@link SingleFlow}
	 */
	public void inqueue(Element e);
	
	/**
	 * Allows a user for adding a customized code when the {@link Element} actually starts the
	 * execution of the {@link Activity}.
	 * @param e {@link Element} requesting this {@link SingleFlow}
	 */
	public void afterStart(Element e);
	
}

