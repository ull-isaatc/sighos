/**
 * 
 */
package es.ull.isaatc.simulation.common.flow;

import es.ull.isaatc.simulation.common.Element;
import es.ull.isaatc.simulation.common.Activity;

/**
 * A flow which executes a single activity. 
 * @author Iván Castilla Rodríguez
 */
public interface SingleFlow extends SingleSuccessorFlow, TaskFlow {
	/**
	 * Obtain the Activity associated to the SingleFlow.
	 * @return The associated Activity.
	 */
	public Activity getActivity();

	public void inqueue(Element e);
	public void afterStart(Element e);
	
}

