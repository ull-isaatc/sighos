/**
 * 
 */
package es.ull.isaatc.simulation;

import es.ull.isaatc.simulation.flow.FinalizerFlow;
import es.ull.isaatc.simulation.flow.InitializerFlow;

/**
 * @author Iván Castilla Rodríguez
 *
 */
public interface FlowDrivenActivityWorkGroup {

	InitializerFlow getInitialFlow();
	FinalizerFlow getFinalFlow();
}
