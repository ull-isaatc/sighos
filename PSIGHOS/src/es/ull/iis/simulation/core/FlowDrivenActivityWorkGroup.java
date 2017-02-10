/**
 * 
 */
package es.ull.iis.simulation.core;

import es.ull.iis.simulation.core.flow.FinalizerFlow;
import es.ull.iis.simulation.core.flow.InitializerFlow;

/**
 * @author Iv�n Castilla Rodr�guez
 *
 */
public interface FlowDrivenActivityWorkGroup<WT extends WorkThread<?>> {

	InitializerFlow<WT> getInitialFlow();
	FinalizerFlow<WT> getFinalFlow();
}
