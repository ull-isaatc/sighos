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
public interface FlowDrivenActivityWorkGroup {

	InitializerFlow getInitialFlow();
	FinalizerFlow getFinalFlow();
}
