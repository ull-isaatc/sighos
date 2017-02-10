/**
 * 
 */
package es.ull.iis.simulation.core.flow;

import es.ull.iis.simulation.core.WorkThread;

/**
 * A {@link Flow} with a unique successor.
 * @author Iv�n Castilla Rodr�guez
 */
public interface SingleSuccessorFlow<WT extends WorkThread<?>> extends Flow<WT> {
	/**
	 * Returns this {@link Flow}'s unique successor. 
	 * @return This {@link Flow}'s unique successor
	 */
	public Flow<WT> getSuccessor();
	
}
