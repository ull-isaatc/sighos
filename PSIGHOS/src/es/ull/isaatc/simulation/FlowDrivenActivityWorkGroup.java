/**
 * 
 */
package es.ull.isaatc.simulation;

import es.ull.isaatc.simulation.flow.FinalizerFlow;
import es.ull.isaatc.simulation.flow.InitializerFlow;

/**
 * @author Iv�n Castilla Rodr�guez
 *
 */
public interface FlowDrivenActivityWorkGroup {

	InitializerFlow getInitialFlow();
	FinalizerFlow getFinalFlow();
}
