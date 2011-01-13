/**
 * 
 */
package es.ull.isaatc.simulation.core;

import es.ull.isaatc.simulation.core.flow.FinalizerFlow;
import es.ull.isaatc.simulation.core.flow.InitializerFlow;

/**
 * @author Iv�n Castilla Rodr�guez
 *
 */
public interface FlowDrivenActivityWorkGroup {

	InitializerFlow getInitialFlow();
	FinalizerFlow getFinalFlow();
}
