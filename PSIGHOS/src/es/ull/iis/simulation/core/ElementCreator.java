/**
 * 
 */
package es.ull.iis.simulation.core;

import es.ull.iis.function.TimeFunction;
import es.ull.iis.simulation.core.flow.InitializerFlow;

/**
 * @author Iván Castilla Rodríguez
 *
 */
public interface ElementCreator {
	
	TimeFunction getNElem();

	void add(ElementType et, InitializerFlow flow, double prop);
	
	/* User methods */
	public int beforeCreateElements(int n);
	public void afterCreateElements();

}
