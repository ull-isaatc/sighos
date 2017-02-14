/**
 * 
 */
package es.ull.iis.simulation.core;

import es.ull.iis.simulation.core.flow.InitializerFlow;
import es.ull.iis.function.TimeFunction;

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
