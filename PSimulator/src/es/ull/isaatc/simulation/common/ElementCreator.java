/**
 * 
 */
package es.ull.isaatc.simulation.common;

import es.ull.isaatc.function.TimeFunction;

/**
 * @author Iván Castilla Rodríguez
 *
 */
public interface ElementCreator {
	TimeFunction getNElem();

	/* User methods */
	public int beforeCreateElements(int n);
	public void afterCreateElements();

}
