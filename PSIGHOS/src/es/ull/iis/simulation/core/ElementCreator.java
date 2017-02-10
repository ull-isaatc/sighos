/**
 * 
 */
package es.ull.iis.simulation.core;

import es.ull.iis.simulation.core.flow.InitializerFlow;
import es.ull.iis.function.TimeFunction;

/**
 * @author Iván Castilla Rodríguez
 *
 */
public interface ElementCreator<WT extends WorkThread<?>> {
	
	TimeFunction getNElem();

	void add(ElementType et, InitializerFlow<WT> flow, double prop);
	
	/* User methods */
	public int beforeCreateElements(int n);
	public void afterCreateElements();

}
