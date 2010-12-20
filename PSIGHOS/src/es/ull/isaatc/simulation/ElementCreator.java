/**
 * 
 */
package es.ull.isaatc.simulation;

import es.ull.isaatc.function.TimeFunction;
import es.ull.isaatc.simulation.flow.InitializerFlow;

/**
 * @author Iv�n Castilla Rodr�guez
 *
 */
public interface ElementCreator {
	
	TimeFunction getNElem();

	void add(ElementType et, InitializerFlow flow, double prop);
	
	/* User methods */
	public int beforeCreateElements(int n);
	public void afterCreateElements();

}
