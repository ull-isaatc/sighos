/**
 * 
 */
package es.ull.isaatc.simulation.common;

import es.ull.isaatc.function.TimeFunction;

/**
 * @author Iv�n Castilla Rodr�guez
 *
 */
public interface ElementCreator {
	TimeFunction getNElem();

	/* User methods */
	public int beforeCreateElements(int n);
	public void afterCreateElements();

}
